/*
 * Copyright  2005,2006 The Apache Software Foundation.
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

package org.apache.myfaces.adf.change;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Change specialization for change in attributes.
 * While applying this Change, the specified attribute state is  restored.
 * @author The Oracle ADF Faces Team
 */
public class AttributeDocumentChange implements DocumentChange 
{
  /**
   * Constructs an AttributeChange with the given attributeName and 
   *  attributeValue.
   * @param attributeName The name of the attribute for which the value needs
   *         to be restored.
   * @param attributeValueString The value of the attribute that needs to be restored.
    * @throws IllegalArgumentException if specified attributeName were to be null.
   * =-= bts TODO Figure out what to do about non String values
   */
  public AttributeDocumentChange(
    String attributeName,
    String attributeValueString)
  {
    if ((attributeName == null) || (attributeName.length() == 0))
      throw new IllegalArgumentException(
        "Cannot construct an AttributeChange with null attribute name.");
        
    _attributeName = attributeName;
    _attributeValueString = attributeValueString;
  }
  
  /**
   * Returns the name of the attribute that represents this Change.
   */
  public String getAttributeName()
  {
    return _attributeName;
  }

  /**
   * Returns the value of the attribute corresponding to this AttributeChange.
   */
  public Object getAttributeStringValue()
  {
    return _attributeValueString;
  }
  
  /**
   * {@inheritDoc}
   */
  public void changeDocument(Node componentNode)
  {
    NamedNodeMap attributes = componentNode.getAttributes();
    
    if (attributes != null)
    {
      // remove the attribute
      if (_attributeValueString == null)
        attributes.removeNamedItem(_attributeName);
      else
      {
        ((Element)componentNode).setAttribute(_attributeName,
                                              _attributeValueString);
      }
    }
  }

  /** 
   * Returns true if adding the DocumentChange should force the JSP Document
   * to reload
   */
  public boolean getForcesDocumentReload()
  {
    return false;
  }

    
  private final String _attributeName;  
  private final  String _attributeValueString;
}
