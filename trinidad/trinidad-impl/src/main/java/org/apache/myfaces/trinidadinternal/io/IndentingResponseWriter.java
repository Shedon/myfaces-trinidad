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
package org.apache.myfaces.trinidadinternal.io;

import java.io.IOException;
import java.io.Writer;

import java.util.HashSet;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

/**
 * Output method that decorates another to pretty-print HTML.
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/io/IndentingResponseWriter.java#0 $) $Date: 10-nov-2005.19:03:49 $
 * @author The Oracle ADF Faces Team
 */
public class IndentingResponseWriter extends ResponseWriterDecorator
{  
  public IndentingResponseWriter(ResponseWriter decorated)
  {
    this(decorated, _DEFAULT_SPACES_PER_LEVEL);
  }

  public IndentingResponseWriter(
    ResponseWriter decorated,
    int spacesPerLevel)
  {
    super(decorated);
    _spacesPer = spacesPerLevel;
    _isHtml = _HTML_TYPES.contains(decorated.getContentType());
  }

  /**
   * Creates a new instance of this DebugResponseWriter, using a different
   * Writer.
   */
  public ResponseWriter cloneWithWriter(Writer writer)
  {
    return new IndentingResponseWriter(
      getResponseWriter().cloneWithWriter(writer));
  }

  public void startElement(String name,
                           UIComponent component) throws IOException
  {
    int depth = _depth++;
    if (depth > 0)
    {
      // Imagemaps are _really_ tight about embedded whitespace.  Don't give
      // 'em an inch.
      if (!_isWhiteSpaceSensitive(name))
      {
        _writeIndent(depth);
      }
    }
    
    _justEndedElement = false;
    super.startElement(name, component);
  }

  public void endElement(String name) throws IOException
  {
    _depth--;
    _seeIfJustEndedElement();
    super.endElement(name);
    
    // don't indent image or anchor end tags because of a bug in the ie and
    // netscape browser table engines that causes the cell to think that it
    // also contains a newline character.  Ditto for "span", "area", "map"...
    if (!_isWhiteSpaceSensitive(name))

    {
      _justEndedElement = true;
    }
  }

  /**
   * Writes a comment.
   */
  public void writeComment(Object comment) throws IOException
  {
    // start a new line only if an element just ended and
    // it wasn't a special case element
    _seeIfJustEndedElement();

    super.writeComment(comment);
  }

  /**
   * Writes a String, escaped properly for this method.
   */
  public void writeText(Object text, String componentPropertyName) throws IOException
  {
    _seeIfJustEndedElement();
    super.writeText(text, componentPropertyName);
  }


  /**
   * Writes a character array, escaped properly for this method.
   */
  public void writeText(
    char[]      text, 
    int         start,
    int         length) throws IOException
  {
    _seeIfJustEndedElement();
    super.writeText(text, start, length);
  }

  /**
   * Writes a string, without performing any escaping.
   */
  public void write(String text) throws IOException
  {
    _seeIfJustEndedElement();
    super.write(text);
  }

  /**
   * Writes a character array, without performing any escaping.
   */
  public void write(
    char[]      text, 
    int         start,
    int         length) throws IOException
  {
    _seeIfJustEndedElement();
    super.write(text, start, length);
  }

  /**
   * Writes a character, without performing any escaping.
   */
  public void write(
    int c
    ) throws IOException
  {
    _seeIfJustEndedElement();
    super.write(c);
  }

  private void _seeIfJustEndedElement() throws IOException
  {
    if (_justEndedElement)
    {
      _justEndedElement = false;
      _writeIndent(_depth);
    }
  }

  private void _writeIndent(int depth) throws IOException
  {
    depth = depth * _spacesPer;
    if (depth > _MAX_INDENT)
      depth = _MAX_INDENT;

    writeText("\n", null);
    
    // If depth goes negative, this isn't just a no-op, it's
    // death.
    if (depth > 0)
      super.write(_sSpaces, 0, depth);

  }

  private boolean _isWhiteSpaceSensitive(String element)
  {
    // Assume that only HTML is a freak about whitespace.
    if (!_isHtml)
      return false;

    String name = element.toLowerCase();
    return ("img".equals(name)    ||
            "a".equals(name)      ||
            "br".equals(name)     ||
            "span".equals(name)   ||
            "div".equals(name)    ||
            "area".equals(name)   ||
            "u".equals(name)      ||
            "i".equals(name)      ||
            "b".equals(name)      ||
            "em".equals(name)     ||
            "strong".equals(name) ||
            "map".equals(name)    ||
            "label".equals(name)  ||
            "font".equals(name)   ||
            "table".equals(name)  ||
            "tbody".equals(name)  ||
            "tr".equals(name)     ||
            "nobr".equals(name)   ||
            "wbr".equals(name)   ||
            "script".equals(name));
  }

  private boolean _justEndedElement;
  private int     _depth;
  private int     _spacesPer;
  private boolean _isHtml;
 
  static private final int _MAX_INDENT = 50;
  static private final int _DEFAULT_SPACES_PER_LEVEL = 2;

  static private final char[] _sSpaces;

  static
  {
    _sSpaces = new char[_MAX_INDENT];
    
    for (int i = 0; i < _MAX_INDENT; i++)
    {
      _sSpaces[i] = ' ';
    }
  }

  static private final Set _HTML_TYPES = new HashSet();
  static
  {
    _HTML_TYPES.add(HtmlResponseWriter.HTML_CONTENT_TYPE);
    _HTML_TYPES.add(XhtmlResponseWriter.XHTML_CONTENT_TYPE);
  }
}
