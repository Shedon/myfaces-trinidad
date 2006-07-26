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

package org.apache.myfaces.trinidadinternal.image.xml.parse;

import org.apache.myfaces.trinidadinternal.share.xml.ParserFactory;
import org.apache.myfaces.trinidadinternal.share.xml.ParserManager;
import org.apache.myfaces.trinidadinternal.share.xml.NodeParser;
import org.apache.myfaces.trinidadinternal.share.xml.ParseContext;

import org.apache.myfaces.trinidadinternal.image.util.MapArea;


/**
 * Factory for creating ImageMapParsers
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/image/xml/parse/ImageMapParserFactory.java#0 $) $Date: 10-nov-2005.19:04:07 $
 * @author The Oracle ADF Faces Team
 */
public class ImageMapParserFactory implements ParserFactory
{
  /**
   * Gets the shared ImageMapParserFactory instance.
   */
  public static ImageMapParserFactory sharedInstance()
  {
    return _sInstance;
  }

  /**
   * Registers this factory on a ParserManager.
   */
  public void registerSelf(ParserManager manager, String namespace)
  {
    // We support parsing of both a single ImageProviderRequest as 
    // well as a set of ImageProviderRequests.
    manager.registerFactory(MapArea[].class, namespace, this);
  }

  /**
   * Returns the parser used for creating ImageProviderRequest
   * instances with the given namespace and local name.
   */
  public NodeParser getParser(
    ParseContext context,                                
    String       namespaceURI,
    String       localName)
  {
    return new ImageMapParser();
  }

  private ImageMapParserFactory() {}

  private static final ImageMapParserFactory _sInstance = 
    new ImageMapParserFactory();
}
