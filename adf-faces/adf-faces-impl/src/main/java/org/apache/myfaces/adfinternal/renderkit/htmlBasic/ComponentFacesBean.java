/*
 * Copyright  2004-2006 The Apache Software Foundation.
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
package org.apache.myfaces.adfinternal.renderkit.htmlBasic;

import java.util.Iterator;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.adf.bean.FacesBean;
import org.apache.myfaces.adf.bean.PropertyKey;

/**
 * Implementation of FacesBean that purely passes through 
 * back to a UIComponent.  This exists so that we can
 * reuse existing rendering code to render on a non-FacesBean-based
 * component.  It's also completely immutable.
 * 
 * @author The Oracle ADF Faces Team
 */
public class ComponentFacesBean implements FacesBean
{
  public ComponentFacesBean(UIComponent component)
  {
    _component = component;
  }

  public Type getType()
  {
    throw new UnsupportedOperationException();
  }

  final public Object getProperty(PropertyKey key)
  {
    return _component.getAttributes().get(key.getName());
  }

  /**
   * @todo Need *good* way of hooking property-sets;  it's
   * currently not called from state restoring, so really, it shouldn't
   * be used as a hook, but EditableValueBase currently
   * requires hooking this method.
   */
  public void setProperty(PropertyKey key, Object value)
  {
    throw new UnsupportedOperationException();
  }

  final public Object getLocalProperty(PropertyKey key)
  {
    throw new UnsupportedOperationException();
  }

  final public ValueBinding getValueBinding(PropertyKey key)
  {
    return _component.getValueBinding(key.getName());
  }

  final public void setValueBinding(PropertyKey key, ValueBinding binding)
  {
    throw new UnsupportedOperationException();
  }


  final public void addEntry(PropertyKey listKey, Object value)
  {
    throw new UnsupportedOperationException();
  }

  final public void removeEntry(PropertyKey listKey, Object value)
  {
    throw new UnsupportedOperationException();
  }

  final public Object[] getEntries(PropertyKey listKey, Class clazz)
  {
    throw new UnsupportedOperationException();
  }

  final public boolean containsEntry(PropertyKey listKey, Class clazz)
  {
    throw new UnsupportedOperationException();
  }

  final public Iterator entries(PropertyKey listKey)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @todo provide more efficient implementation for copying
   * from other FacesBeanImpl instances
   */
  public void addAll(FacesBean from)
  {
    throw new UnsupportedOperationException();
  }
  

  final public Set keySet()
  {
    throw new UnsupportedOperationException();
  }

  final public Set bindingKeySet()
  {
    throw new UnsupportedOperationException();
  }

  public void markInitialState()
  {
    throw new UnsupportedOperationException();
  }

  public void restoreState(FacesContext context, Object state)
  {
    throw new UnsupportedOperationException();
  }

  public Object saveState(FacesContext context)
  {
    throw new UnsupportedOperationException();
  }

  private final UIComponent _component;
}
