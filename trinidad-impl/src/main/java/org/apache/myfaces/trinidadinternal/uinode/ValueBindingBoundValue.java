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
package org.apache.myfaces.trinidadinternal.uinode;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.data.BoundValue;

class ValueBindingBoundValue implements BoundValue
{
  public ValueBindingBoundValue(ValueBinding binding)
  {
    if (binding == null)
      throw new NullPointerException();

    _binding = binding;
  }

  /**
   * @todo Better way to retrieve FacesContext
   */
  public Object getValue(UIXRenderingContext rContext)
  {
    FacesContext fContext = FacesContext.getCurrentInstance();
    return _binding.getValue(fContext);
  }

  private final ValueBinding _binding;
}
