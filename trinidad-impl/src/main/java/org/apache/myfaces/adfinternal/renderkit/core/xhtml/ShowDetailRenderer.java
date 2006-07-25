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
package org.apache.myfaces.adfinternal.renderkit.core.xhtml;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.adf.bean.FacesBean;
import org.apache.myfaces.adf.bean.PropertyKey;
import org.apache.myfaces.adf.component.core.layout.CoreShowDetail;

import org.apache.myfaces.adfinternal.renderkit.AdfRenderingContext;
import org.apache.myfaces.adfinternal.skin.icon.Icon;
import org.apache.myfaces.adf.logging.ADFLogger;

/**
 * This needs to be massively cleaned up...
 * @todo TEST NON-PPR!  I removed the non-PPR case from the
 *   JS script
 */
public class ShowDetailRenderer extends ShowDetailItemRenderer
{
  public ShowDetailRenderer()
  {
    this(CoreShowDetail.TYPE);
  }

  protected ShowDetailRenderer(FacesBean.Type type)
  {
    super(type);
  }

  protected void findTypeConstants(FacesBean.Type type)
  {
    super.findTypeConstants(type);
    _immediateKey = type.findKey("immediate");
    _disclosedTextKey = type.findKey("disclosedText");
    _undisclosedTextKey = type.findKey("undisclosedText");
  }

  /**
   */
  protected void encodeAll(
    FacesContext        context,
    AdfRenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    boolean disclosed = getDisclosed(bean);

    ResponseWriter rw = context.getResponseWriter();
    rw.startElement("span", component);
    if (!isTableAllDisclosure() && !isTableDetailDisclosure())
      renderId(context, component);

    renderPromptStart(context, arc, component, bean);
    _renderScripts(context, arc, component);
    String onClickString =
               _generateOnClickString(context, arc, component, bean, disclosed);
    _renderLinkStart(context, arc, onClickString);
    String sourceValue = getClientId(context, component);
    String linkId = getLinkId(sourceValue, disclosed);
    if (linkId != null)
      rw.writeAttribute("id", linkId, null);

    if (!isTableAllDisclosure())
    {
      renderDisclosureIcon(context, arc, disclosed);
      _renderLinkEnd(context, arc);
    }

    UIComponent prompt = getFacet(component,
                                  CoreShowDetail.PROMPT_FACET);
    if (prompt == null)
    {
      String text = getDisclosureText(arc, bean, disclosed);
      if (text != null)
      {
        if (!isTableAllDisclosure())
          _renderLinkStart(context, arc, onClickString);
        String styleClass = getLinkStyleClass();
        if (styleClass != null)
          renderStyleClass(context, arc, styleClass);
        rw.writeText(text,
                     disclosed ? "disclosedText" : "undisclosedText");

        _renderLinkEnd(context, arc);
      }
    }

    if (isTableAllDisclosure() && prompt != null)
      _renderLinkEnd(context, arc);

    if (prompt != null)
    {
      encodeChild(context, prompt);
    }

    renderPromptEnd(context);

    if (disclosed &&
        !isTableAllDisclosure() &&
        !isTableDetailDisclosure() &&
        !renderAsInline())
    {
      rw.startElement("div", null);
      encodeAllChildren(context, component);
      rw.endElement("div");
    }

    rw.endElement("span");

  }

  protected void renderPromptStart(
    FacesContext        context,
    AdfRenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();

    // Start a different span;  this is where all attributes
    // except the ID will go.
    if ( renderAsInline())
      writer.startElement("span", component);
    else
      writer.startElement("div", component);

    renderAllAttributes(context, arc, bean);
  }

  protected void renderPromptEnd(
    FacesContext        context) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();

    if ( renderAsInline())
      writer.endElement("span");
    else
      writer.endElement("div");
  }


  static public void renderDisclosureIcon(
    FacesContext        context,
    AdfRenderingContext arc,
    boolean             disclosed,
    String              disclosedAltTextKey,
    String              undisclosedAltTextKey) throws IOException
  {
    Icon icon = _getDisclosureIcon(arc, disclosed);

    if (icon != null)
    {
      // Get the alt text
      String key = disclosed ? disclosedAltTextKey : undisclosedAltTextKey;
      String altText = arc.getTranslatedString(key);
      // Get the align
      String align = OutputUtils.getMiddleIconAlignment(arc);

      // Render the icon with the specified attrs
      OutputUtils.renderIcon(context, arc, icon, altText, align);
    }
  }


  protected void renderDisclosureIcon(
    FacesContext        context,
    AdfRenderingContext arc,
    boolean             disclosed) throws IOException
  {
    renderDisclosureIcon(context, arc, disclosed,
                         _DISCLOSED_TIP_KEY, _UNDISCLOSED_TIP_KEY);
  }

  /**
   * Always render an ID, needed for proper PPR.
   */
  protected boolean shouldRenderId(
    FacesContext context,
    UIComponent  component)
  {
    return true;
  }


  // Returns the disclosure Icon
  private static Icon _getDisclosureIcon(
    AdfRenderingContext arc,
    boolean             disclosed
    )
  {
    String iconName = (disclosed
                       ? XhtmlConstants.AF_SHOW_DETAIL_DISCLOSED_ICON_NAME
                       : XhtmlConstants.AF_SHOW_DETAIL_UNDISCLOSED_ICON_NAME);

    return arc.getIcon(iconName);
  }

  protected String getDisclosureText(
    AdfRenderingContext arc,
    FacesBean           bean,
    boolean             disclosed)
  {
    String text;
    if (disclosed)
    {
      text = getDisclosedText(bean);
      if (text == null)
      {
        //smo: This functionality was added durring API Merge.  If getDisclosedText is not present
        //we'll render undislosedText text if it's present before rendering the defaults
        text = getUndisclosedText(bean);
        if(text == null)
        {
          text = arc.getTranslatedString(_DISCLOSED_KEY);
        }
      }
    }
    else
    {
      text = getUndisclosedText(bean);
      if (text == null)
      {
        text = getDisclosedText(bean);
        if(text == null)
        {
          text = arc.getTranslatedString(_UNDISCLOSED_KEY);
        }
      }
    }

    return text;
  }

  private void _renderScripts(
    FacesContext        context,
    AdfRenderingContext arc,
    UIComponent         component) throws IOException
  {
    if (!supportsNavigation(arc))
      return;

    FormData fData = arc.getFormData();
    if (fData == null)
    {
      _LOG.warning("showDetail is not in a form, and " +
                   "will not function correctly");
      return;
    }

    String valueValue = getValueParameter(component);


    ResponseWriter rw = context.getResponseWriter();
    boolean partial = PartialPageUtils.isPPRActive(context);

    // the first time, render the necessary Javascript
    if (arc.getProperties().get(_SHOW_DETAIL_SUBMIT_JS_RENDERED) == null)
    {
      arc.getProperties().put(_SHOW_DETAIL_SUBMIT_JS_RENDERED,
                              Boolean.TRUE);
      // write the submit function

      String js;
      // Javascript function, optimized to reduce size.  Parameters are:
      // a: form name
      // v: validate?
      // b: event name
      // c: source parameter (id)
      // l: ID of link (for focusing)
      // d: value parameter (for use in table)
      // =-=AEW Why bother including "document" and "window" in
      // _setRequestedFocusNode() call?  It could derive them itself.
      if (partial)
      {
        js =
          "function _submitHideShow(a,v,b,c,l,d) {" +
            "var o = {"+
                  XhtmlConstants.EVENT_PARAM + ":b," +
                  XhtmlConstants.SOURCE_PARAM + ":c};" +
            "if (d!=(void 0)) o." +
                  XhtmlConstants.VALUE_PARAM + "=d;" +
            "_setRequestedFocusNode(document,l,false,window);" +
            "_submitPartialChange(a,v,o);" +
            "return false;}";
      }
      else
      {
        js =
          "function _submitHideShow(a,v,b,c,l,d) {" +
            "var o={"+
                  XhtmlConstants.EVENT_PARAM + ":b," +
                  XhtmlConstants.SOURCE_PARAM + ":c};" +
            "if (d!=(void 0)) o." +
                  XhtmlConstants.VALUE_PARAM + "=d;" +
            "submitForm(a,v,o);" +
            "return false;}";
      }

      // write the submit function
      rw.startElement("script", null);
      renderScriptDeferAttribute(context, arc);
      renderScriptTypeAttribute(context, arc);

      rw.writeText(js, null);
      rw.endElement("script");

      // Add these needed values at most once per page
      fData.addNeededValue(XhtmlConstants.EVENT_PARAM);
      fData.addNeededValue(XhtmlConstants.SOURCE_PARAM);
      if (partial)
        fData.addNeededValue(XhtmlConstants.PARTIAL_PARAM);
    }

    // And add this needed value if it ever comes up
    if (valueValue != null)
      fData.addNeededValue(XhtmlConstants.VALUE_PARAM);

  }

  private void _renderLinkStart(
    FacesContext        context,
    AdfRenderingContext arc,
    String           onclickString ) throws IOException
  {
    if (!supportsNavigation(arc))
      return;

    ResponseWriter rw = context.getResponseWriter();
    rw.startElement("a", null);
    rw.writeAttribute("onclick", onclickString, null);
    rw.writeAttribute("href", "#", null);
  }

  private String _generateOnClickString(
    FacesContext        context,
    AdfRenderingContext arc,
    UIComponent         component,
    FacesBean           bean,
    boolean             disclosed)
  {
    FormData fData = arc.getFormData();
    if (fData == null)
       return null;

    String sourceValue = getClientId(context, component);
    String formName = fData.getName();
    String eventValue = (disclosed
                         ? XhtmlConstants.HIDE_EVENT
                         : XhtmlConstants.SHOW_EVENT);
    String valueValue = getValueParameter(component);
    String linkId = getLinkId(sourceValue, disclosed);

    int linkBufferLen = 41 +
                        formName.length() +
                        eventValue.length() +
                        sourceValue.length();

    if (valueValue != null)
      linkBufferLen += valueValue .length();
    else
      linkBufferLen += 4;


    StringBuffer linkBuffer = new StringBuffer(linkBufferLen);
    linkBuffer.append("return _submitHideShow('");
    linkBuffer.append(formName);
    linkBuffer.append("',");
    linkBuffer.append(getImmediate(bean) ? '0' : '1');
    linkBuffer.append(",'");
    linkBuffer.append(eventValue);
    linkBuffer.append("','");
    linkBuffer.append(sourceValue);
    linkBuffer.append("',");

    if (linkId != null)
      linkBuffer.append("'").append(linkId).append("'");
    else
      linkBuffer.append("null");

    if (valueValue != null)
      linkBuffer.append(",'").append(valueValue).append("'");

    linkBuffer.append(")");
    return linkBuffer.toString();
  }


  private void _renderLinkEnd(
    FacesContext        context,
    AdfRenderingContext arc) throws IOException
  {
    if (!supportsNavigation(arc))
      return;

    context.getResponseWriter().endElement("a");
  }

  /**
   * Hook for table;  it'd be cleaner to simply expose
   * more protected hooks in general, and eliminate this
   * Table-ShowDetail dependency.
   */
  protected boolean isTableDetailDisclosure()
  {
    return false;
  }

  /**
   * Hook for table;  it'd be cleaner to simply expose
   * more protected hooks in general, and eliminate this
   * Table-ShowDetail dependency.
   */
  protected boolean isTableAllDisclosure()
  {
    return false;
  }

  protected boolean renderAsInline()
  {
    return false;
  }

  protected String getValueParameter(UIComponent component)
  {
    return null;
  }

  /**
   * Returns the style class to use for links rendered by the
   * ShowDetailRenderer
   */
  protected String getLinkStyleClass()
  {
    return XhtmlConstants.LINK_STYLE_CLASS;
  }

  /**
   */
  protected String getLinkId(String rootId, boolean disclosed)
  {
    return XhtmlUtils.getCompositeId(rootId, null);
  }

  protected boolean getImmediate(FacesBean bean)
  {
    Object o = bean.getProperty(_immediateKey);
    if (o == null)
      o = _immediateKey.getDefault();

    return Boolean.TRUE.equals(o);
  }


  protected String getDisclosedText(FacesBean bean)
  {
    // It can be null in the table...
    if (_disclosedTextKey == null)
      return null;

    return toString(bean.getProperty(_disclosedTextKey));
  }

  protected String getUndisclosedText(FacesBean bean)
  {
    // It can be null in the table...
    if (_undisclosedTextKey == null)
      return null;

    return toString(bean.getProperty(_undisclosedTextKey));
  }



  private PropertyKey _immediateKey;
  private PropertyKey _disclosedTextKey;
  private PropertyKey _undisclosedTextKey;

  private static final Object _SHOW_DETAIL_SUBMIT_JS_RENDERED =
                             new Object();

  private static final String _DISCLOSED_KEY =
    "af_showDetail.DISCLOSED";
  private static final String _UNDISCLOSED_KEY =
    "af_showDetail.UNDISCLOSED";

  private static final String _DISCLOSED_TIP_KEY =
    "af_showDetail.DISCLOSED_TIP";
  private static final String _UNDISCLOSED_TIP_KEY =
    "af_showDetail.UNDISCLOSED_TIP";

  private static final ADFLogger _LOG = ADFLogger.createADFLogger(ShowDetailRenderer.class);
}
