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

package org.apache.myfaces.adfinternal.ui.laf.base.desktop;
import org.apache.myfaces.adf.component.UIXHierarchy;
import org.apache.myfaces.adf.component.UIXPage;

import org.apache.myfaces.adf.model.RowKeySet;
import org.apache.myfaces.adfinternal.ui.RenderingContext;
import org.apache.myfaces.adfinternal.ui.UINode;
import org.apache.myfaces.adfinternal.ui.laf.base.xhtml.PageRendererUtils;


/**
 * Renderer for navigationTree in a page 
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/desktop/PageNavigationTreeRenderer.java#0 $) $Date: 10-nov-2005.18:55:32 $
 * @author The Oracle ADF Faces Team
 */
public class PageNavigationTreeRenderer extends NavigationTreeRenderer
{ 


  protected RowKeySet getExpandedRowKeys(UIXHierarchy tree)
  {
    return ((UIXPage)tree).getDisclosedRowKeys();    
  }

  protected UIXHierarchy getTree(
    RenderingContext context, 
    UINode           node)
  {
    UINode pageNode = context.getParentContext().getAncestorNode(0);
    UIXHierarchy component = (UIXHierarchy) pageNode.getUIComponent();     
    return component;    
  }
  
  protected UINode getStamp(
    RenderingContext context, 
    UINode           node)
  {
    UINode pageNode = context.getParentContext().getAncestorNode(0);
    return getNamedChild(context, pageNode, NODE_STAMP_CHILD);
  }  
  
  protected String getFormName(
    RenderingContext context, 
    UINode           node)
  {
    return getParentFormName(context.getParentContext());
  }
  
  protected boolean setInitialPath(
    RenderingContext context, 
    UINode           node,
    UIXHierarchy         tree)
  {
   
    int startLevel = getIntAttributeValue(context, node, START_LEVEL_ATTR, 0);
    return PageRendererUtils.setNewPath(context, tree, startLevel); 
  }
}
