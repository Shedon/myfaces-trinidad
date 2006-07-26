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

import javax.faces.context.ResponseWriter;

import org.apache.myfaces.trinidadinternal.ui.RenderingContext;
import org.apache.myfaces.trinidadinternal.ui.UINode;
import org.apache.myfaces.trinidadinternal.agent.AdfFacesAgent;


/**
 * Renderer for sideBar
 * 
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/desktop/SideBarRenderer.java#0 $) $Date: 10-nov-2005.18:56:15 $
 * @author The Oracle ADF Faces Team
 */
public class SideBarRenderer extends HtmlLafRenderer
{
 // issues 

  protected String getElementName(
    RenderingContext context,
    UINode           node
    )
  {
    return DIV_ELEMENT;
  }                



  protected void renderAttributes(
    RenderingContext context,
    UINode           node
    )throws IOException
  {
    super.renderAttributes(context, node);
    ResponseWriter writer = context.getResponseWriter();
    renderStyleClassAttribute(context, AF_PANEL_SIDE_BAR_STYLE_CLASS);
    Object width = node.getAttributeValue(context, WIDTH_ATTR);
    
    boolean isMac = context.getAgent().getAgentOS() == AdfFacesAgent.OS_MACOS;
    if (width != null && !isMac)
    {
      writer.writeAttribute(STYLE_ATTRIBUTE,  
                            _WIDTH_CONSTANT + width.toString(),
							null);
    }
  }

  /**
   *
   */
  protected void prerender(
    RenderingContext context,
    UINode           node
    )
    throws IOException
  {   
    //--pu-- Note: Take care to render start of related link block wrapper as the  
    //first activity in the prerender of SideBarRenderer and its derivative 
    //SideNavRenderer (in case this method is overridden there)
    renderRelatedLinksBlockStart(context, "af_panelSideBar.BLOCK_TITLE");    
    super.prerender(context, node);

    ResponseWriter writer = context.getResponseWriter();

    UINode filter = node.getNamedChild(context, FILTER_CHILD);
    
    if ( filter != null)
    {
      renderNamedChild(context, node, filter, FILTER_CHILD);
      writer.startElement(HORIZONTAL_RULE_ELEMENT, null);
      writer.endElement(HORIZONTAL_RULE_ELEMENT);
    }
    
  }

  
  protected final void postrender(
    RenderingContext context,
    UINode           node
    )
    throws IOException
  { 

    ResponseWriter writer = context.getResponseWriter();

    Object width = node.getAttributeValue(context, WIDTH_ATTR);
    // For width tried using min-width in css, but not working 
    // on ie6 or mac, getting overlapping on mozilla 1.3 and netscape 7, 
    // so rendering a div at bottom      
    if ( width == null )
    {         
      writer.startElement(DIV_ELEMENT, null);
      renderStyleClassAttribute(context, 
                                SIDE_BAR_MIN_WIDTH_STYLE_CLASS);
      writer.endElement(DIV_ELEMENT);
    }  
    else
    {
      writer.startElement(DIV_ELEMENT, null);
      writer.writeAttribute(STYLE_ATTRIBUTE,  
                            _WIDTH_CONSTANT + width.toString(),
							null);    
      writer.endElement(DIV_ELEMENT);
    }
    
    
    super.postrender(context, node);
    //--pu-- Note: Take care to render end of related links block wrapper as the 
    //last activity in the prerender of SideBarRenderer and its derivative 
    //SideNavRenderer (in case this method is overridden there)
    renderRelatedLinksBlockEnd(context);    
  }


  static private String _WIDTH_CONSTANT = "width:"; 
}
