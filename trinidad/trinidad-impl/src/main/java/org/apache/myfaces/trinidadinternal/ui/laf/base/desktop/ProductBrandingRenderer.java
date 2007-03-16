/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.myfaces.trinidadinternal.ui.laf.base.desktop;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.UINode;


/**
 * Renderer for product branding
 * 
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/desktop/ProductBrandingRenderer.java#0 $) $Date: 10-nov-2005.18:56:12 $
 */
public class ProductBrandingRenderer extends HtmlLafRenderer
{
  @Override
  protected void renderContent(
    UIXRenderingContext context,
    UINode           node) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();

    writer.startElement("span", null);

    renderStyleClassAttribute(context, getTextStyle(context, node));

	// @todo =-=bwa. Need to change the "null" to writeText() to be
	// the PropertyKey corresponding to the "text" attribute on
	// the product branding component. However, the component does not
	// exist yet, since the component is still in experimental.
    writer.writeText(getText(context, node), TEXT_ATTR.getAttributeName());
    writer.endElement("span");

  }

  @Override
  protected String getElementName(
    UIXRenderingContext context,
    UINode           node
    )
  {
    return "span";
  }
  

  protected String getTextStyle(
    UIXRenderingContext context,
    UINode           node
    )
  {
    return "p_OraProductBrandingCompactText";
  }  

  
}

