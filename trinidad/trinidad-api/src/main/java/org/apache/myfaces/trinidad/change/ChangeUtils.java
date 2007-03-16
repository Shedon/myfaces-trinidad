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
package org.apache.myfaces.trinidad.change;

import java.util.List;

import javax.faces.component.UIComponent;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * Utility functions for use by Changes.
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-api/src/main/java/oracle/adf/view/faces/change/ChangeUtils.java#0 $) $Date: 10-nov-2005.19:09:58 $
 */
class ChangeUtils 
{
  private ChangeUtils()
  {
  }

  /**
   * Given a parent component and the identifier for the child, looks up among
   *  the children for a child with the specified identifier and returns.
   * Returns null if there were to be no such child
   */
  @SuppressWarnings("unchecked")
  public static UIComponent getChildForId(UIComponent parent, String childId)
  {
    if (parent == null)
      return null;

    int numChildren = parent.getChildCount();
    if (numChildren == 0)
      return null;

    List children = parent.getChildren();
    UIComponent child;
    for (int i=0; i<numChildren; i++)
    {
      child = (UIComponent) children.get(i);
      if ( childId.equals(child.getId()) )
        return child;
    }
    return null;
  }
  
  /**
   * Given a node representing a component, returns the named facet's Element.
   * @param componentNode The node to search for a facet contained in it.
   * @param facetName The name of the facet to search for.
   * @return
   */
  static Element __getFacetElement(
    Node componentNode,
    String facetName)
  {
    assert componentNode != null;
    assert (facetName != null) && (facetName.length() > 0);
    
    Node currChild = componentNode.getFirstChild();
    
    while (currChild != null)
    {
      // check for local name match
      if ("facet".equals(currChild.getLocalName()))
      {
        // check for namespace match
        if (__JSF_CORE_NAMESPACE.equals(currChild.getNamespaceURI()))
        {
          NamedNodeMap attributes = currChild.getAttributes();

          if (facetName.equals(attributes.getNamedItem("name").getNodeValue()))
          {
            return (Element)currChild;
          }
        }
      }

      currChild = currChild.getNextSibling();
    }
    
    return null;
  }

  /**
   * Removes all of the children from the parent Node.
   * @param parentNode 
   */
  static void __removeAllChildren(Node parentNode)
  {
    Node nukeChild = parentNode.getFirstChild();
    
    while (nukeChild != null)
    {
      parentNode.removeChild(nukeChild);
      nukeChild = parentNode.getFirstChild();
    }
  }

  static final String __JSF_CORE_NAMESPACE = "http://java.sun.com/jsf/core";
}
