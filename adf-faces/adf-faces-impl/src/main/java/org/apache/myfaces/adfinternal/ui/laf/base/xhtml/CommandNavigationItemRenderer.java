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
package org.apache.myfaces.adfinternal.ui.laf.base.xhtml;

import org.apache.myfaces.adfinternal.ui.RenderingContext;
import org.apache.myfaces.adfinternal.ui.UIConstants;
import org.apache.myfaces.adfinternal.ui.UINode;

public class CommandNavigationItemRenderer extends AliasRenderer
{
  protected String getLocalName(
    RenderingContext context,
    UINode           node)
  {

    Object rendererType = getNavigationItemRendererType(context);

    if ( OPTION_TYPE.equals(rendererType))
    {
      return UIConstants.COMMAND_ITEM_NAME;
    }
    if ( GLOBAL_BUTTON_TYPE.equals(rendererType))
      return UIConstants.GLOBAL_BUTTON_NAME;


    return UIConstants.LINK_NAME;
  }

  public static final void setNavigationItemRendererType(
    RenderingContext context,
    Object type
    )
  {
    context.setProperty(UIConstants.MARLIN_NAMESPACE,
                        _NAVIGATION_ITEM_RENDERER_KEY,
                        type);
  }

  public static final Object getNavigationItemRendererType(
    RenderingContext context
    )
  {
    return context.getProperty(UIConstants.MARLIN_NAMESPACE,
                               _NAVIGATION_ITEM_RENDERER_KEY);
  }


  private static final Object _NAVIGATION_ITEM_RENDERER_KEY = new Object();
  public static final Object GLOBAL_BUTTON_TYPE = new Object();
  public static final Object OPTION_TYPE = new Object();

}
