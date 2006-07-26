/*
* Copyright 2006 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.myfaces.trinidadinternal.agent.parse;


import org.apache.myfaces.trinidadinternal.share.xml.BaseNodeParser;
import org.apache.myfaces.trinidadinternal.share.xml.NodeParser;
import org.apache.myfaces.trinidadinternal.share.xml.ParseContext;
import org.apache.myfaces.trinidad.logging.ADFLogger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;


/**
 * Node parser for capabilities node of capabilities file
 */
class CapabilitiesNodeParser extends BaseNodeParser implements XMLConstants
{

  public void startElement (ParseContext context,
                            String       namespaceURI,
                            String       localName,
                            Attributes   attrs )
          throws SAXParseException
  {
    if (!NS_URI.equals(namespaceURI))
    {
      throw new SAXParseException("Invalid Namespace: " +
                                  namespaceURI, context.getLocator());
    }

    _id = attrs.getValue(ATTRIBUTE_ID);
    String isDefault = attrs.getValue(ATTRIBUTE_DEFAULT);
    if ("true".equals(isDefault))
      _isDefault = true;

    String agent = attrs.getValue(ATTRIBUTE_AGENTS);
    if (agent != null)
    {
      try
      {
        _agent = new NameVersion(agent);
      }
      catch (ParseException pe)
      {
        _LOG.warning("Unable to parse agent string");
      }
    }

    String platform = attrs.getValue(ATTRIBUTE_PLATFORMS);
    if (platform != null)
    {
      try
      {
        _platform = new NameVersion(platform);
      }
      catch (ParseException pe)
      {
        _LOG.warning("Unable to parse agent string");
      }
    }

    if ((_id == null) && (_agent == null))
      _LOG.warning("Element " + ELEMENT_CAPABILITIES +
                   " has missing (or empty) attributes");

  }


  public NodeParser startChildElement(ParseContext context,
                                      String       namespaceURI,
                                      String       localName,
                                      Attributes   attrs)
          throws SAXParseException
  {
    if (ELEMENT_INCLUDE.equals(localName))
      return new IncludeNodeParser();

    //return null; if unknown element
    return null;
  }


  public void addCompletedChild (ParseContext context,
                                 String       namespaceURI,
                                 String       localName,
                                 Object       child)
          throws SAXParseException
  {
    if (child == null)
      return;

    if (ELEMENT_INCLUDE.equals(localName))
      _includeNodes.add(child);
  }

  public Object endElement (ParseContext context,
                            String       namespaceURI,
                            String       localName)
  {
    if ((_id == null) && (_agent == null))
      return null;

    ArrayList nodesWithRefList = new ArrayList(_includeNodes.size());
    ArrayList nodesWithSrcList = new ArrayList(_includeNodes.size());
    for (int i = 0; i < _includeNodes.size(); i++)
    {
      IncludeNode node = (IncludeNode) _includeNodes.get(i);
      if (node.__getRefId() != null)
        nodesWithRefList.add(node);
      else
        nodesWithSrcList.add(node);
    }
    IncludeNode[] nodesWithRef = (IncludeNode[])
            nodesWithRefList.toArray(new IncludeNode[nodesWithRefList.size()]);
    IncludeNode[] nodesWithSrc = (IncludeNode[])
            nodesWithSrcList.toArray(new IncludeNode[nodesWithSrcList.size()]);

    return new CapabilitiesNode(_id, _isDefault, _agent,
                                _platform, nodesWithRef, nodesWithSrc);
  }



  private String _id;
  private boolean _isDefault;
  private NameVersion _agent;
  private NameVersion _platform;
  private List _includeNodes = new ArrayList();

  static private final ADFLogger _LOG = ADFLogger.createADFLogger(CapabilitiesNodeParser.class);
}
