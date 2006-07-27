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
package org.apache.myfaces.trinidadinternal.uinode.nav;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.component.UIXComponent;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.UIConstants;
import org.apache.myfaces.trinidadinternal.ui.collection.AttributeMap;
import org.apache.myfaces.trinidadinternal.ui.data.BoundValue;
import org.apache.myfaces.trinidadinternal.uinode.bind.ClientIdBoundValue;


public class CommandNavigationItemFacesBean extends CommandLinkFacesBean
{
 protected AttributeMap createAttributeMap(String componentFamily)
  {
    AttributeMap attrMap = super.createAttributeMap(componentFamily);
    attrMap.setAttribute(UIConstants.VALUE_ATTR,
                         new ClientIdBoundValue(getUIXComponent()));

    return attrMap;
  }
}
