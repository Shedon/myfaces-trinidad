/*
* Copyright 2006 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.myfaces.adf.bean;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class TestValueBinding extends ValueBinding implements StateHolder
{
  public TestValueBinding()
  {
  }

  public Object getValue(FacesContext context)
  {
    return _value;
  }

  
  public void setValue(FacesContext context, Object value)
  {
    _value = value;
  }

  public boolean isReadOnly(FacesContext context)
  {
    return false;
  }

  public Class getType(FacesContext context)
  {
    return null;
  }

  public Object saveState(FacesContext context)
  {
    return _value;
  }

  public void restoreState(FacesContext context, Object state)
  {
    _value = state;
  }

  public boolean isTransient()
  {
    return _transient;
  }

  public void setTransient(boolean newTransientValue)
  {
    _transient = newTransientValue;
  }

  private boolean _transient;
  private Object _value;
}
