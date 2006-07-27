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
package org.apache.myfaces.trinidadinternal.ui.laf.base.desktop;

import java.io.IOException;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.UINode;


/**
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/desktop/CellFormatRenderer.java#0 $) $Date: 10-nov-2005.18:55:11 $
 * @author The Oracle ADF Faces Team
 */
public class CellFormatRenderer 
                     extends org.apache.myfaces.trinidadinternal.ui.laf.base.xhtml.CellFormatRenderer
{  
  /**
   * Renders attributes of the current node.
   */
  protected void renderAttributes(
    UIXRenderingContext context,
    UINode           node
    ) throws IOException
  {
    super.renderAttributes(context, node);

    //
    // render attributes not present in xhtml
    //
    renderAttribute(context, node, "nowrap",  WRAPPING_DISABLED_ATTR);
    renderAttribute(context, node, "width",  WIDTH_ATTR);
    renderAttribute(context, node, "height", HEIGHT_ATTR);
  }
}
