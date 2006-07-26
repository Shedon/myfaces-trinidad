/*
 * Copyright  2001-2006 The Apache Software Foundation.
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

import org.apache.myfaces.trinidadinternal.ui.RenderingContext;

/**
 * Renderer for processChoiceBar
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/desktop/ProcessChoiceBarRenderer.java#0 $) $Date: 10-nov-2005.18:55:35 $
 * @author The Oracle ADF Faces Team
 * @todo copied from NavigationBarRenderer. will need to refactor to share
 * code.
 */
public class ProcessChoiceBarRenderer extends
   org.apache.myfaces.trinidadinternal.ui.laf.base.xhtml.ProcessChoiceBarRenderer
{

  /**
   * Writes the separator between two elements
   */
  protected void renderItemSpacer(
    RenderingContext context
    ) throws IOException
  {
    renderSpacer(context, 5, 1);
  }
  
}
