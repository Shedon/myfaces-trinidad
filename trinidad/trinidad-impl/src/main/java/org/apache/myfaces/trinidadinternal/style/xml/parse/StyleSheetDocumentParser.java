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

package org.apache.myfaces.trinidadinternal.style.xml.parse;

import java.io.File;
import java.io.IOException;

import java.util.Map;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



import org.apache.myfaces.trinidad.logging.TrinidadLogger;

import org.apache.myfaces.trinidadinternal.share.io.InputStreamProvider;
import org.apache.myfaces.trinidadinternal.share.xml.BaseNodeParser;
import org.apache.myfaces.trinidadinternal.share.xml.NodeParser;
import org.apache.myfaces.trinidadinternal.share.xml.ParseContext;
import org.apache.myfaces.trinidadinternal.share.xml.XMLUtils;

import org.apache.myfaces.trinidadinternal.style.StyleConstants;
import org.apache.myfaces.trinidadinternal.style.xml.XMLConstants;

/**
 * NodeParser for style sheet document nodes
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/style/xml/parse/StyleSheetDocumentParser.java#0 $) $Date: 10-nov-2005.18:58:46 $
 * @author The Oracle ADF Faces Team
 */
public class StyleSheetDocumentParser extends BaseNodeParser
  implements XMLConstants, StyleConstants
{
  /**
   * Implementation of NodeParser.endElement()
   */
  @Override
  public void startElement(
    ParseContext context,
    String       namespaceURI,
    String       localName,
    Attributes   attrs) throws SAXParseException
  {
    _documentVersion = attrs.getValue(DOCUMENT_VERSION_ATTR);
  }

  /**
   * Implementation of NodeParser.endElement()
   */
  @Override
  public Object endElement(
    ParseContext context,
    String       namespaceURI,
    String       localName
    )
  {
    StyleSheetNode[] styleSheets = _getStyleSheets();
    ColorSchemeNode[] colorSchemes = _getColorSchemes();
    String documentVersion = _getDocumentVersion();
    long documentTimestamp = _getDocumentTimestamp(context);

    styleSheets = _resolveColorProperties(styleSheets, colorSchemes);

    return new StyleSheetDocument(styleSheets,
                                  documentVersion,
                                  documentTimestamp);
  }

  /**
   * Implementation of NodeParser.startChildElement()
   */
  @Override
  public NodeParser startChildElement(
    ParseContext context,
    String       namespaceURI,
    String       localName,
    Attributes   attrs
    ) throws SAXParseException
  {
    if (localName.equals(STYLE_SHEET_NAME))
    {
      return context.getParser(
        StyleSheetNode.class,
        namespaceURI,
        localName
        );
    }
    else if (localName.equals(COLOR_SCHEME_NAME))
    {
      return context.getParser(
        ColorSchemeNode.class,
        namespaceURI,
        localName
        );
    }
    else if (localName.equals(IMPORT_NAME))
    {
      String href = attrs.getValue(HREF_ATTR);
      try
      {
        _handleImport(context, href);
      }
      catch (IOException e)
      {
        if (_LOG.isWarning())
          _LOG.warning("Could not parse import: " + href, e);
      }

      return this;
    }

    return null;
  }

  /**
   * Implementation of NodeParser.addCompletedChild().
   */
  @Override
  public void addCompletedChild(
    ParseContext context,
    String       namespaceURI,
    String       localName,
    Object       child
    )
  {
    if (child != null)
    {
      if (localName.equals(STYLE_SHEET_NAME))
      {
        if (_styleSheets == null)
          _styleSheets = new Vector<StyleSheetNode>();

        _styleSheets.addElement((StyleSheetNode)child);
      }
      else if (localName.equals(COLOR_SCHEME_NAME))
      {
        if (_colorSchemes == null)
          _colorSchemes = new Vector<ColorSchemeNode>();

        _colorSchemes.addElement((ColorSchemeNode)child);
      }
    }
  }

  // Return all style sheet nodes - this includes imported style sheets
  // as well as style sheets from this document.  Imported style
  // sheets have lower precedence - they come first.
  private StyleSheetNode[] _getStyleSheets()
  {
    StyleSheetNode[] styleSheets = null;

    if (_imports == null)
    {
      if (_styleSheets != null)
      {
        styleSheets = new StyleSheetNode[_styleSheets.size()];
        _styleSheets.copyInto(styleSheets);
      }
    }
    else
    {
      // If we've got imported documents, copy all style sheets
      // into a Vector first.
      // -= Simon Lessard =-
      // TODO: Check if synchronization is truly required.
      Vector<StyleSheetNode> v = new Vector<StyleSheetNode>();
      for(StyleSheetDocument doc : _imports)
      {
        Iterator<StyleSheetNode> e = doc.getStyleSheets();
        while (e.hasNext())
          v.addElement(e.next());
      }

      // Might as well add the rest of the style sheets in now too
      if (_styleSheets != null)
      {
        for (int i = 0; i < _styleSheets.size(); i++)
          v.addElement(_styleSheets.elementAt(i));
      }

      // Now, copy everything into a single array
      styleSheets = new StyleSheetNode[v.size()];
      v.copyInto(styleSheets);
    }

    return styleSheets;
  }

  // Return a list of color schemes defined in this document and
  // imported documents.  We merge color scheme nodes with the same
  // namespace/name now, because we can.
  private ColorSchemeNode[] _getColorSchemes()
  {
    // We collect ColorSchemeNodes in a dictionary (hashed by namespaceURI +
    // name) to simplify merging
    // -= Simon Lessard =-
    // TODO: Check if synchronization is really needed.
    Hashtable<String, ColorSchemeNode> colorSchemesTable = 
      new Hashtable<String, ColorSchemeNode>(19);

    // Merge in imported color schemes first
    if (_imports != null)
    {
      for(StyleSheetDocument doc : _imports)
      {
        Iterator<ColorSchemeNode> e = doc.getColorSchemes();
        while (e.hasNext())
        {
          _mergeColorScheme(colorSchemesTable, e.next());
        }
      }
    }

    // Merge in color schemes defined by this document
    if (_colorSchemes != null)
    {
      for (int i = 0; i < _colorSchemes.size(); i++)
      {
        _mergeColorScheme(colorSchemesTable, _colorSchemes.elementAt(i));
      }
    }

    // Now, copy everything into a single array
    int count = colorSchemesTable.size();
    ColorSchemeNode[] colorSchemes = new ColorSchemeNode[count];
    int i = 0;
    for(ColorSchemeNode node : colorSchemesTable.values())
    {
      colorSchemes[i++] = node;
    }

    return colorSchemes;
  }

  // Merges the ColorSchemeNode into a Map of ColorSchemeNodes, hashed
  // by namespaceURI + name.
  private void _mergeColorScheme(
    Map<String, ColorSchemeNode> colorSchemes,
    ColorSchemeNode colorScheme
    )
  {
    String key = colorScheme.getNamespaceURI() + colorScheme.getName();
    ColorSchemeNode oldColorScheme = colorSchemes.get(key);

    if (oldColorScheme == null)
    {
      colorSchemes.put(key, colorScheme);
      return;
    }

    // If we've already got a color scheme, we need to merge the colors
    // from the new and old color schemes.  Brute force method here...
    // -= Simon Lessard =-
    // TODO: Check if synchronization is truly required.
    Hashtable<String, ColorNode> colorsTable = new Hashtable<String, ColorNode>();

    // First, merge in the old colors
    Iterator<ColorNode> e = oldColorScheme.getColors();
    while (e.hasNext())
    {
      ColorNode color = e.next();
      colorsTable.put(color.getName(), color);
    }

    // Now, overwrite with new colors
    e = colorScheme.getColors();
    while (e.hasNext())
    {
      ColorNode color = e.next();
      colorsTable.put(color.getName(), color);
    }

    // Now, create the merged ColorSchemeNode
    ColorNode[] colors = new ColorNode[colorsTable.size()];
    int i = 0;
    for(ColorNode color : colorsTable.values())
    {
      colors[i++] = color;
    }

    ColorSchemeNode newColorScheme = new ColorSchemeNode(
                                           colorScheme.getNamespaceURI(),
                                           colorScheme.getName(),
                                           colors);

    colorSchemes.put(key, newColorScheme);
  }

  // Handle an import - parse the imported XSS document
  private void _handleImport(
    ParseContext context,
    String href
    ) throws SAXParseException, IOException
  {
    if (href == null)
    {
      _LOG.warning("Import missing required href attribute");
      return;
    }

    // Now, parse the imported document
    StyleSheetDocument doc = null;

    try
    {
      doc = (StyleSheetDocument)XMLUtils.parseInclude(context,
                                                    href,
                                                    StyleSheetDocument.class);
    }
    catch (SAXException e)
    {
      _LOG.severe(e);
    }

    if (doc != null)
    {
      if (_imports == null)
        _imports = new ArrayList<StyleSheetDocument>();

      _imports.add(doc);
    }
  }

  /**
   * Converts any color property nodes into normal property nodes
   */
  private StyleSheetNode[] _resolveColorProperties(
    StyleSheetNode[]  styleSheets,
    ColorSchemeNode[] colorSchemes
    )
  {
    if (styleSheets == null)
      return null;

    // If we are the top level document, resolve all <colorProperty>
    // elements now.
    Map<String, String> defaultColors = _getDefaultColors(colorSchemes);
    for (int i = 0; i < styleSheets.length; i++)
    {
      StyleSheetNode styleSheet = styleSheets[i];
      // -= Simon Lessard =- 
      // TODO: Check if synchronization is really needed.
      Vector<StyleNode> stylesVector = new Vector<StyleNode>();
      Iterator<StyleNode> stylesIterator = styleSheet.getStyles();
      while (stylesIterator.hasNext())
      {
        boolean resolvedColorProperty = false;
        StyleNode style = stylesIterator.next();
        // -= Simon Lessard =- 
        // TODO: Check if synchronization is really needed.
        Vector<PropertyNode> propertiesVector = new Vector<PropertyNode>();
        Iterator<PropertyNode> propertiesIterator = style.getProperties();
        while (propertiesIterator.hasNext())
        {
          PropertyNode property = propertiesIterator.next();

          if (property.__isColorProperty())
          {
            String name = property.getName();
            String value = defaultColors.get(property.getValue());

            assert (value != null);

            property = new PropertyNode(name, value);

            resolvedColorProperty = true;
          }

          propertiesVector.addElement(property);
        }

        if (resolvedColorProperty)
        {
          int count = propertiesVector.size();
          PropertyNode[] properties = new PropertyNode[count];
          propertiesVector.copyInto(properties);

          style = new StyleNode(style, properties);
        }

        stylesVector.addElement(style);
      }

      // Create a new StyleSheetNode for the resolved styles
      StyleNode[] styles = new StyleNode[stylesVector.size()];
      stylesVector.copyInto(styles);
      styleSheet = new StyleSheetNode(styleSheet, styles);

      styleSheets[i] = styleSheet;
    }

    return styleSheets;
  }

  // Returns a Map of color names to (Strings) color values
  private Map<String, String> _getDefaultColors(ColorSchemeNode[] colorSchemes)
  {
    // -= Simon Lessard =-
    // TODO: Check if synchronization is really needed.
    Hashtable<String, String> colors = new Hashtable<String, String>();

    // Initialize the table with default values
    for (int i = 0; i < _DEFAULT_COLORS.length; i += 2)
      colors.put(_DEFAULT_COLORS[i], _DEFAULT_COLORS[i + 1]);

    if (colorSchemes != null)
    {
      // Now, add any overridden values defined by the "default" color scheme
      for (int i = 0; i < colorSchemes.length; i++)
      {
        ColorSchemeNode colorScheme = colorSchemes[i];
        if ("default".equals(colorScheme.getName()))
        {
          Iterator<ColorNode> e = colorScheme.getColors();
          while (e.hasNext())
          {
            ColorNode color = e.next();
            colors.put(color.getName(), color.getValue());
          }

          break;
        }
      }
    }

    return colors;
  }

  // Returns the document version for this style sheet, which
  // includes versions specified by any imported style sheets.
  private String _getDocumentVersion()
  {
    StringBuffer buffer = new StringBuffer();

    // Start with the version from this document
    if (_documentVersion != null)
      buffer.append(_documentVersion);

    // Tack on versions from imported style sheets
    if (_imports != null)
    {
      for(StyleSheetDocument document : _imports)
      {
        String documentVersion = document.getDocumentVersion();
        if (documentVersion != null)
          buffer.append(documentVersion);
      }
    }

    if (buffer.length() > 0)
      return buffer.toString();

    return null;
  }

  // Returns the document timestamp for the style sheet that
  // is currently being parsed, taking into account timestamps
  // of any imported style sheets.
  private long _getDocumentTimestamp(ParseContext context)
  {
    long timestamp = StyleSheetDocument.UNKNOWN_TIMESTAMP;

    // The only way to get the timestamp is through the
    // InputStreamProvider.
    InputStreamProvider provider = XMLUtils.getInputStreamProvider(context);

    if (provider != null)
    {
      // And this only works if we are using a File-based InputStream
      Object identifier = provider.getIdentifier();
      if (identifier instanceof File)
        timestamp = ((File)identifier).lastModified();
    }

    // Merge in timestamps of imported style sheets
    if (_imports != null)
    {
      for(StyleSheetDocument document : _imports)
      {
        long importTimestamp = document.getDocumentTimestamp();
        if (importTimestamp > timestamp)
          timestamp = importTimestamp;
      }
    }

    return timestamp;
  }

  // -= Simon Lessard =-
  // TODO: Check if synchronization is truly required
  private Vector<StyleSheetNode> _styleSheets;   // Vector of StyleSheetNode
  private Vector<ColorSchemeNode> _colorSchemes;  // Vector of ColorSchemeNodes
  private ArrayList<StyleSheetDocument> _imports;       // Vector of imported StyleSheetDocument
  private String _documentVersion; // Version identifier for the document

  // Default values for BLAF color scheme
  private static final String[] _DEFAULT_COLORS =
  {
    "VeryDark",
    "#003366",
    "Dark",
    "#336699",
    "Medium",
    "#6699cc",
    "Light",
    "#99ccff",
    "VeryDarkShadowAccent",
    "#333300",
    "DarkShadowAccent",
    "#666633",
    "VeryDarkAccent",
    "#999966",
    "DarkAccent",
    "#d2d8b0",
    "MediumAccent",
    "#ffffcc",
    "LightAccent",
    "#f7f7e7",
    "VeryDarkExtraAccent",
    "#333333",
    "DarkExtraAccent",
    "#666666",
    "MediumExtraAccent",
    "#999999",
    "LightExtraAccent",
    "#cccccc",
    "AFTextForeground",
    "#000000",
    "AFTextBackground",
    "#ffffff",
  };
  private static final TrinidadLogger _LOG = TrinidadLogger.createTrinidadLogger(StyleSheetDocumentParser.class);
}
