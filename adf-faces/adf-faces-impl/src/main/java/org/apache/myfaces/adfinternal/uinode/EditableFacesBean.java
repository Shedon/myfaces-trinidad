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
package org.apache.myfaces.adfinternal.uinode;

import org.apache.myfaces.adf.bean.PropertyKey;
import org.apache.myfaces.adf.component.UIXEditableValue;

import org.apache.myfaces.adfinternal.ui.UIConstants;
import org.apache.myfaces.adfinternal.ui.collection.AttributeMap;
import org.apache.myfaces.adfinternal.ui.data.BoundValue;
import org.apache.myfaces.adfinternal.uinode.bind.ConverterBoundValue;
import org.apache.myfaces.adfinternal.uinode.bind.EntriesBoundValue;
import org.apache.myfaces.adfinternal.uinode.bind.PropertyBoundValue;

public class EditableFacesBean extends UINodeFacesBean
{

 /**
  * @todo what about UIXEditableValue.VALIDATOR_KEY?
  */
 protected AttributeMap createAttributeMap(String componentFamily)
  {
    AttributeMap attrMap = super.createAttributeMap(componentFamily);
    attrMap.setAttribute(UIConstants.CONVERTER_ATTR,
                         getConverterBoundValue());
                                                      
                                                          
    attrMap.setAttribute(UIConstants.VALIDATORS_ATTR,
                  new EntriesBoundValue(this,  
                                        UIXEditableValue.VALIDATORS_KEY));
                          
    BoundValue unvalidatedBV = new PropertyBoundValue(this,
                                             UIXEditableValue.IMMEDIATE_KEY);
    attrMap.setAttribute(UIConstants.UNVALIDATED_ATTR, unvalidatedBV);                                 
    return  attrMap;
  }
  
  public void setProperty(PropertyKey key, Object value)
  {
    super.setProperty(key, value);
    if (key == UIXEditableValue.VALUE_KEY)
      setProperty(UIXEditableValue.LOCAL_VALUE_SET_KEY, Boolean.TRUE);
  }
  
  protected BoundValue getConverterBoundValue()
  {
    return new ConverterBoundValue(getUIXComponent());  
  }
}
