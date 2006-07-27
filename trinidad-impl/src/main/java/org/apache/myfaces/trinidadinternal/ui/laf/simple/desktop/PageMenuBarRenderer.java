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

package org.apache.myfaces.trinidadinternal.ui.laf.simple.desktop;

import org.apache.myfaces.trinidad.component.UIXHierarchy;
import org.apache.myfaces.trinidad.component.UIXPage;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.UINode;
import org.apache.myfaces.trinidadinternal.ui.laf.base.xhtml.PageRendererUtils;

/**
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/simple/desktop/PageMenuBarRenderer.java#0 $) $Date: 10-nov-2005.18:51:23 $
 * @author The Oracle ADF Faces Team
 */
public class PageMenuBarRenderer extends GlobalHeaderRenderer
{


  protected UIXHierarchy getHierarchyBase(
    UIXRenderingContext context,
    UINode           node
  )
  {
    UINode pageNode = context.getParentContext().getAncestorNode(0);
    return (UIXPage) pageNode.getUIComponent();
  }


  protected UINode getStamp(
    UIXRenderingContext context,
    UINode           node
    )
  {
    UINode pageNode = context.getParentContext().getAncestorNode(0);
    return getNamedChild(context, pageNode, NODE_STAMP_CHILD);
  }


  protected boolean setNewPath(
    UIXRenderingContext context,
    UINode           node,
    UIXHierarchy    component
  )
  {
    int startDepth = getIntAttributeValue(context, node, LEVEL_ATTR, 0);
    return PageRendererUtils.setNewPath(context, ((UIXPage)component), startDepth);
  }


  /**
   * Checks to see whether the globalHeader is empty (contains no
   * indexed children).
   */
  protected boolean isEmpty(
    UIXRenderingContext context,
    UINode           node
    )
  {
    UINode pageNode = context.getParentContext().getAncestorNode(0);
    UIXPage component = (UIXPage) pageNode.getUIComponent();
    int startDepth = getIntAttributeValue(context, node, LEVEL_ATTR, 0);

    return PageRendererUtils.isEmpty(context, component, startDepth);
  }
}
