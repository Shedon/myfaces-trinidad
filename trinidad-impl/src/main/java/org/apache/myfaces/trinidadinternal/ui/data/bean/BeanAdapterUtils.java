/*
 * Copyright  2001-2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.myfaces.trinidadinternal.ui.data.bean;

import java.util.Collection;
import java.util.Map;

import org.apache.myfaces.trinidadinternal.util.OptimisticHashMap;

import org.apache.myfaces.trinidad.logging.ADFLogger;

import org.apache.myfaces.trinidadinternal.ui.RenderingContext;
import org.apache.myfaces.trinidadinternal.ui.data.DataObject;
import org.apache.myfaces.trinidadinternal.ui.data.DataObjectList;
import org.apache.myfaces.trinidadinternal.ui.data.MutableDataObject;

/**
 * Utilities for working with bean DataObject adapters.  Provides
 * a location to register adapter classes, which allow for a
 * higher-performance implementation than ordinary introspection.
 * However, developers should generally delay creating and registering
 * an adapter class until performance metrics justify the addition.
 * <p>
 * By default, the Bean adapter classes will use the actual class
 * of an object instance - that is, object.getClass() - to find
 * an adapter, or use introspection to create an adapter.  However,
 * this is not always desirable for two reasons:
 * <ol>
 * <li>For efficiency reasons, it's faster to reuse the same
 *     adapter class for all implementations of a single interface.
 *
 * <li>Introspection cannot succeed on package-private classes.  However,
 *     it works fine if that package-private class has a public superclass
 *     or implements a public interface - and that public class or interface
 *     supports those properties.
 * </ol>
 * <p>
 * Adapters can be built using the BuildBeanDOAdapter tool,
 * and all of this rigamarole can be entirely bypassed by
 * handing to Cabo instances of these adapter classes instead
 * of the bean classes themselves.
 * <p>
 * @see org.apache.myfaces.trinidadinternal.ui.tools.BuildBeanDOAdapter
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/data/bean/BeanAdapterUtils.java#0 $) $Date: 10-nov-2005.18:56:48 $
 * @author The Oracle ADF Faces Team
 */
public class BeanAdapterUtils
{
  /**
   * Creates a DataObject adapter class around an object instance.
   * Instead of using the actual class of the instance, use a superclass.
   * See above for reasons why this may be useful.
   */
  static public DataObject getAdapter(Object instance, Class objClass)
    throws IllegalAccessException, InstantiationException
  {
    if (instance == null)
      return null;

    if (instance instanceof DataObject)
      return (DataObject) instance;

    if (instance instanceof Map)
      return new MapDataObject((Map) instance);

    if (objClass == null)
      objClass = instance.getClass();

    Class adapterClass = (Class) _sAdapters.get(objClass);
    if (adapterClass != null)
    {
      BeanDOAdapter adapter = (BeanDOAdapter) adapterClass.newInstance();
      adapter.setInstance(instance);
      return adapter;
    }
    else
    {
      return IntrospectionAdapter.getAdapter(instance);
    }
  }


  /**
   * Creates a DataObject adapter class around an object instance.
   */
  static public DataObject getAdapter(Object instance)
    throws IllegalAccessException, InstantiationException
  {
    return getAdapter(instance, null);
  }

  /**
   * Creates a DataObject adapter class around an object instance.
   * Instead of throwing exceptions, log exceptions with the
   * RenderingContext's error log.
   */
  static public DataObject getAdapter(
    RenderingContext context,
    Object           instance)
  {
    try
    {
      return getAdapter(instance);
    }
    catch (InstantiationException ie)
    {
      _LOG.severe(ie);
    }
    catch (IllegalAccessException iae)
    {
      _LOG.severe(iae);
    }

    return null;
  }



  /**
   * Creates a DataObjectList adapter class around an object.
   */
  static public DataObjectList getAdapterList(
    RenderingContext context, Object listInstance)
  {
    if (listInstance == null)
      return null;

    if (listInstance instanceof DataObjectList)
      return (DataObjectList) listInstance;

    if (listInstance instanceof Object[])
      return new BeanArrayDataObjectList((Object[]) listInstance);

    if (listInstance instanceof Collection)
      return new BeanArrayDataObjectList(((Collection) listInstance).toArray());

    if (_LOG.isInfo())
    {
      _LOG.info("Cannot convert " + listInstance
                + " of class:"+ listInstance.getClass()
                + " into DataObjectList");
    }

    return null;
  }



  /**
   * Registers an adapter class to be used in place of introspection.
   */
  static public void registerAdapterClass(
    Class beanClass,
    Class adapterClass)
  {
    // =-=AEW This forces classes to be loaded.  Should we allow
    // registration by string name?
    if (!BeanDOAdapter.class.isAssignableFrom(adapterClass))
      throw new IllegalArgumentException(
         "Adapter class doesn't implement BeanDOAdapter");

    _sAdapters.put(beanClass, adapterClass);
  }

  static private final class MapDataObject implements MutableDataObject
  {
    public MapDataObject(Map map)
    {
      _map = map;
    }

    public Object selectValue(RenderingContext context, Object select)
    {
      return _map.get(select);
    }

    public void updateValue(
      RenderingContext context,
      Object select,
      Object value)
    {
      _map.put(select, value);
    }

    private final Map _map;
  }

  static private final OptimisticHashMap _sAdapters =
    new OptimisticHashMap(101);
  private static final ADFLogger _LOG = ADFLogger.createADFLogger(BeanAdapterUtils.class);
}
