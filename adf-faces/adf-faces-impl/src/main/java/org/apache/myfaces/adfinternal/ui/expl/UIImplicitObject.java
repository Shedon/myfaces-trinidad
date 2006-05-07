/*
 * Copyright  2000-2006 The Apache Software Foundation.
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
package org.apache.myfaces.adfinternal.ui.expl;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.AbstractMap;
import java.util.AbstractList;
import java.util.Collections;


import org.apache.myfaces.adf.logging.ADFLogger;

import org.apache.myfaces.adfinternal.ui.RenderingContext;
import org.apache.myfaces.adfinternal.ui.AttributeKey;
import org.apache.myfaces.adfinternal.ui.data.DataObject;
import org.apache.myfaces.adfinternal.ui.data.DataObjectList;
import org.apache.myfaces.adfinternal.ui.data.DataSet;
import org.apache.myfaces.adfinternal.ui.data.MutableDataObject;
import org.apache.myfaces.adfinternal.ui.data.bean.BeanDOAdapter;

/**
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/expl/UIImplicitObject.java#0 $) $Date: 10-nov-2005.18:56:28 $
 * @author The Oracle ADF Faces Team
 */
public class UIImplicitObject
{
  protected UIImplicitObject(UIVariableResolver varResolver)
  {
    _varResolver = varResolver;
  }

  /**
   * Gets the VariableResolver
   */
  public final UIVariableResolver getVariableResolver()
  {
    return _varResolver;
  }

  /**
   * Gets the current RenderingContext.
   */
  public final RenderingContext getRenderingContext()
  {
    return _varResolver.getRenderingContext();
  }

  /**
   * Gets the current data on the rendering context.
   */
  public final Object getCurrent()
  {
    RenderingContext context = getRenderingContext();
    DataObject currentData = context.getCurrentDataObject();
    return adapt(currentData);
  }

  /**
   * Gets the data provider map from the rendering context.
   */
  public final Object getData()
  {
    return _dataMap;
  }

  /**
   * Gets the root attribute map.
   */
  public final Object getRootAttr()
  {
    return _rootAttrsMap;
  }


  /**
   * Gets a map of the support color palettes.
   */
  public final Object getColorPalette()
  {
    return ColorPaletteUtils.getColorPaletteMap();
  }

  /**
   * Converts some instance into a {@link Map} or {@link List}.  The instances
   * recognized by this method are {@link DataSet}, {@link DataObject} and
   * {@link DataObjectList}
   */
  public Object adapt(Object value)
  {
    if ((value==null) || (value instanceof List) || (value instanceof Map))
    {
      // no conversion necessary
      return value;
    }
    else if (value instanceof BeanDOAdapter)
    {
      Object bean = ((BeanDOAdapter) value).selectValue(getRenderingContext(),
                                                        ".");
      if (bean != null)
        return bean;

      return new DataObjectMap((DataObject) value);
    }
    else if (value instanceof DataSet)
    {
      return new DataSetMap((DataSet) value);
    }
    else if (value instanceof DataObjectList)
    {
      // sometimes objects might implement both DataObject and DataObjectList
      // but not DataSet:
      if (value instanceof DataObject)
        return new DataSetMap((DataObject) value, (DataObjectList) value);
      else
        return new DOLList((DataObjectList) value);
    }
    else if (value instanceof DataObject)
    {
      return new DataObjectMap((DataObject) value);
    }

    return value;
  }

  private final Map _dataMap = new AbstractMap()
    {
      public Set entrySet()
      {
        return Collections.EMPTY_SET;
      }

      public Object get(Object key)
      {
        RenderingContext context = getRenderingContext();
        return adapt(context.getDataObject("" /*namespace*/,
                                           key.toString()));
      }
    };

  private final Map _rootAttrsMap = new AbstractMap()
    {
      public Set entrySet()
      {
        return Collections.EMPTY_SET; // for now
      }

      public Object get(Object key)
      {
        RenderingContext compositeContext = getRenderingContext();

        RenderingContext context = compositeContext.getParentContext();
        if (context == null)
        {
          _LOG.warning
            ("rootAttrs can only be used inside a template");
          return null;
        }

        final AttributeKey attrKey;

        if (key.getClass() == AttributeKey.class)
          attrKey = (AttributeKey) key;
        else
        {
          // this should not happen too often because of the optimization in
          // ELExpressionParser._processRootAttrs(..)
          attrKey = AttributeKey.getAttributeKey(key.toString());
        }

        return context.getAncestorNode(0).getAttributeValue(context, attrKey);
      }
    };

  private final class DOLList extends AbstractList implements DataObjectList
  {
    public DOLList(DataObjectList lst)
    {
      if (lst == null)
      {
        throw new NullPointerException("Null list argument");
      }
      _lst = lst;
    }

    public int size()
    {
      return _lst.getLength();
    }

    public int getLength()
    {
      return size();
    }

    public DataObject getItem(int i)
    {
      return _lst.getItem(i);
    }

    public Object get(int i)
    {
      return new DataObjectMap(getItem(i));
    }

    private final DataObjectList _lst;
  }

  private class DataObjectMap extends AbstractMap implements MutableDataObject
  {
    public DataObjectMap(DataObject dob)
    {
      if (dob == null)
      {
        throw new NullPointerException("Null data object argument");
      }

      _dob = dob;
    }

    public Object selectValue(RenderingContext rc, Object key)
    {
      return _dob.selectValue(rc, key);
    }

    public void updateValue(RenderingContext rc, Object key, Object value)
    {
      if (_dob instanceof MutableDataObject)
        ((MutableDataObject) _dob).updateValue(rc, key, value);
      else
        throw new UnsupportedOperationException();
    }

    public Object get(Object key)
    {
      RenderingContext context = getRenderingContext();
      Object value = selectValue(context, key);
      //ystem.out.println("key:"+key+" value:"+value);
      value = adapt(value);
      return value;
    }

    public Object put(Object key, Object value)
    {
      RenderingContext context = getRenderingContext();
      Object old = selectValue(context, key);
      updateValue(context, key, value);
      return old;
    }

    public Set entrySet()
    {
      return Collections.EMPTY_SET;
    }

    public String toString()
    {
      return _dob.toString();
    }

    private final DataObject _dob;
  }

  private final class DataSetMap extends DataObjectMap implements DataSet
  {
    public DataSetMap(DataSet ds)
    {
      super(ds);

      _ds = ds;
    }



    public DataSetMap(DataObject dob, DataObjectList dol)
    {
      this(new DataSetImpl(dob, dol));
    }

    public Object get(Object key)
    {
      if (key instanceof Number)
      {
        Object value = getItem(((Number) key).intValue());
        return adapt(value);
      }
      else
      {
        return super.get(key);
      }
    }

    public int getLength()
    {
      return _ds.getLength();
    }

    public DataObject getItem(int i)
    {
      return _ds.getItem(i);
    }

    private final DataSet _ds;
  }

  private static class DataSetImpl implements DataSet
  {
    public DataSetImpl(DataObject dob, DataObjectList dol)
    {
      _dob = dob;
      _dol = dol;
    }

    public Object selectValue(RenderingContext rc, Object key)
    {
      return _dob.selectValue(rc, key);
    }

    public DataObject getItem(int index)
    {
      return _dol.getItem(index);
    }

    public int getLength()
    {
      return _dol.getLength();
    }

    private final DataObject _dob;
    private final DataObjectList _dol;

  }

  private final UIVariableResolver _varResolver;
  private static final ADFLogger _LOG = ADFLogger.createADFLogger(UIImplicitObject.class);
}
