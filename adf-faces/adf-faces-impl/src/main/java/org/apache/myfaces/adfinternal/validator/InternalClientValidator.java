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
package org.apache.myfaces.adfinternal.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.myfaces.adf.validator.ClientValidator;

/**
 * Interface that can return scriptlet keys to load our javascript libs
 * <p>
 * @see javax.faces.validator.Validator
 * @author The Oracle ADF Faces Team
 */
public interface InternalClientValidator extends ClientValidator
{

  public String getLibKey(
   FacesContext context,
   UIComponent component); 
  
  /**
   * Called to retrieve the format to display a validation error
   * This method should not return a value that is Javascript-escaped.
   */
  public String getClientValidationFormat(
   FacesContext context,
   UIComponent component);
  
}
