/*
 * Copyright  2003-2006 The Apache Software Foundation.
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
package org.apache.myfaces.trinidad.bean.util;

import java.util.Map;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.bean.PropertyMap;
import org.apache.myfaces.trinidad.util.ArrayMap;

import javax.faces.context.FacesContext;

public class PropertyArrayMap extends ArrayMap<PropertyKey,Object>
                             implements PropertyMap
{
  public PropertyArrayMap(
    int initialCapacity)
  {
    super(initialCapacity);
  }

  public PropertyArrayMap()
  {
    super();
  }

  public Object get(
    PropertyKey pKey)
  {
    if (pKey.getIndex() < 0)
      return get(pKey);
    return getByIdentity(pKey);
  }

  @Override
  public Object put(
    PropertyKey key,
    Object      value)
  {
    Object retValue = super.put(key, value);
    if (_createDeltas())
    {
      if (!_equals(value, retValue))
        _deltas.put(key, value);
    }

    return retValue;
  }

  @Override
  public Object remove(
    Object key)
  {
    if (_createDeltas())
    {
      if (!super.containsKey(key))
        return null;
      
      // If this key is contained, it certainly must be a PropertyKey!
      assert(key instanceof PropertyKey);
      _deltas.put((PropertyKey) key, null);
    }

    return super.remove(key);
  }

  @Override
  public void putAll(Map<? extends PropertyKey, ? extends Object> t)
  {
    if (_createDeltas())
      _deltas.putAll(t);

    super.putAll(t);
  }

  public Object saveState(FacesContext context)
  {
    if (_initialStateMarked)
    {
      if (_deltas == null)
        return null;

      return StateUtils.saveState(_deltas, context, getUseStateHolder());
    }
    else
    {
      return StateUtils.saveState(this, context, getUseStateHolder());
    }
  }

  public void restoreState(
    FacesContext context,
    FacesBean.Type type,
    Object state)
  {
    StateUtils.restoreState(this, context, type, state, getUseStateHolder());
  }

  protected PropertyMap createDeltaPropertyMap()
  {
    PropertyArrayMap map = new PropertyArrayMap(2);
    map.setUseStateHolder(getUseStateHolder());
    return map;
  }


  public boolean getUseStateHolder()
  {
    return _useStateHolder;
  }

  public void setUseStateHolder(boolean useStateHolder)
  {
    _useStateHolder = useStateHolder;
  }



  // =-=AEW CLEAR?

  public void markInitialState()
  {
    _initialStateMarked = true;
  }


  private boolean _createDeltas()
  {
    if (_initialStateMarked)
    {
      if (_deltas == null)
      {
        _deltas = createDeltaPropertyMap();
      }

      return true;
    }
    
    return false;
  }

  static private boolean _equals(Object a, Object b)
  {
    if (a == b)
      return true;

    if (a == null)
      return false;

    return a.equals(b);
  }

  private transient boolean _initialStateMarked;
  private transient PropertyMap _deltas;
  private boolean      _useStateHolder;
}
