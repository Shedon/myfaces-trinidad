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
package org.apache.myfaces.trinidadinternal.renderkit.core.xhtml;

import java.beans.Beans;

import java.io.IOException;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.component.html.HtmlBody;
import org.apache.myfaces.trinidad.context.AdfFacesContext;


import org.apache.myfaces.trinidadinternal.renderkit.AdfRenderingContext;
import org.apache.myfaces.trinidadinternal.skin.Skin;


/**
 * Renderer for the panelPartialRoot.
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/renderkit/core/xhtml/BodyRenderer.java#1 $) $Date: 11-nov-2005.14:59:41 $
 * @author The Oracle ADF Faces Team
 */
public class BodyRenderer extends PanelPartialRootRenderer
{
  public BodyRenderer()
  {
    this(HtmlBody.TYPE);
  }

  protected BodyRenderer(FacesBean.Type type)
  {
    super(type);
  }
  
  protected void findTypeConstants(FacesBean.Type type)
  {
    super.findTypeConstants(type);
    _firstClickPassedKey = type.findKey("firstClickPassed");
    _initialFocusIdKey = type.findKey("initialFocusId");
    _onloadKey = type.findKey("onload");
    _onunloadKey = type.findKey("onunload");
  }


  protected void encodeAll(
    FacesContext        context,
    AdfRenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    writer.startElement("body", component);
    renderId(context, component);
    renderAllAttributes(context, arc, bean);
    super.encodeAll(context, arc, component, bean);

    // Output a version comment at the bottom of the body
    _writeVersionInformation(context, arc);
  }

  protected void renderAtEnd(
    FacesContext context,
    AdfRenderingContext arc) throws IOException
  {
    context.getResponseWriter().endElement("body");

    _renderInitialFocusScript(context, arc);
  }

  protected void renderPPRSupport(
    FacesContext        context,
    AdfRenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    super.renderPPRSupport(context, arc, component, bean);
    if (getFirstClickPassed(bean))
    {
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement("script", null);
      renderScriptDeferAttribute(context, arc);
      renderScriptTypeAttribute(context, arc);
      writer.writeText("var _pprFirstClickPass=true;", null);
      writer.endElement("script");
    }
  }

  protected void renderContent(
    FacesContext        context,
    AdfRenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    boolean isPartialPass = PartialPageUtils.isPartialRenderingPass(arc);

    _renderAnchorForTop(context);
    _renderNoScript(context, arc);

    if (supportsScripting(arc))
    {
      _storeInitialFocus(arc, bean);
    }

    if (!isPartialPass)
    {
      // start the span here, and end it in postrender
      _renderPartialBackSupportSpan(context, arc, true);
    }

    super.renderContent(context, arc, component, bean);

    if (!isPartialPass)
    {
      // end the span for PPR Back button support (and render the hidden fields)
      _renderPartialBackSupportSpan(context, arc, false);
    }
  }

  protected String getStyleClass(FacesBean bean)
  {
    String styleClass = super.getStyleClass(bean);
    return styleClass;
  }

  protected void renderEventHandlers(
    FacesContext context,
    FacesBean    bean) throws IOException
  {
    super.renderEventHandlers(context, bean);
    AdfRenderingContext arc = AdfRenderingContext.getCurrentInstance();
    ResponseWriter rw = context.getResponseWriter();

    if (PartialPageUtils.isPartialRenderingPass(arc))
    {
      rw.writeAttribute("onunload", _PARTIAL_ONUNLOAD_HANDLER, null);
    }
    else
    {
      rw.writeAttribute("onload", getOnload(arc, bean), "onload");
      rw.writeAttribute("onunload", getOnunload(arc, bean), "onunload");

      // If partial back is supported,
      // render an onbeforeunload event handler. This javascript function
      // will save the page's state when the page is unloaded. This way if the
      // user goes back to the page via the Back button, we'll be able to
      // restore the state: the html and the javascript.
      if (_isPartialBackSupported(arc))
      {
        rw.writeAttribute("onbeforeunload",
                          _PPR_BACK_UNLOAD_SCRIPT,
                          null);
      }
    }
  }

  protected boolean getFirstClickPassed(FacesBean bean)
  {
    // =-=AEW firstClickPassed is not currently supported on document
    if (_firstClickPassedKey == null)
      return false;

    Object o = bean.getProperty(_firstClickPassedKey);
    if (o == null)
      o = _firstClickPassedKey.getDefault();
    return Boolean.TRUE.equals(o);
  }

  protected String getInitialFocusId(FacesBean bean)
  {
    return toString(bean.getProperty(_initialFocusIdKey));
  }

  protected String getOnload(AdfRenderingContext arc, FacesBean bean)
  {
    String onload = toString(bean.getProperty(_onloadKey));
    if (PartialPageUtils.supportsPartialRendering(arc))
    {
      // Don't short circuit...
      onload = XhtmlUtils.getChainedJS("_checkLoad(event)", onload, false);
    }

    return onload;
  }

  protected String getOnunload(AdfRenderingContext arc, FacesBean bean)
  {
    String onunload = toString(bean.getProperty(_onunloadKey));
    if (PartialPageUtils.supportsPartialRendering(arc))
    {
      // Don't short circuit...
      onunload = XhtmlUtils.getChainedJS("_checkUnload(event)",
                                         onunload,
                                         false);
    }

    return onunload;
  }



  /**
   * Renders a top anchor at the top of the page
   * In quirks mode this is not required,but Mozilla will complain
   * in standards mode.
   * @param context
   * @throws IOException
   */
  private void _renderAnchorForTop(FacesContext context) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    writer.startElement("a",null);
    writer.writeAttribute("name","top",null);
    writer.endElement("a");
  }

  private void _renderNoScript(
    FacesContext        context,
    AdfRenderingContext arc) throws IOException
  {
    // Some accessibility standards rather oddly claim that NOSCRIPT
    // tags are essential for compliance.  So, render NOSCRIPT, at
    // least when we're not in "inacessible" mode.
    //
    // But don't bother in design time mode - this check is
    // largely there for JDev 10.1.3 preview, which was rendering
    // the contents of any NOSCRIPT tags in the VE, but it's
    // a check that does no harm.
    if (!isInaccessibleMode(arc) && !Beans.isDesignTime())
    {
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement("noscript",null);
      String message = arc.getTranslatedString("NO_SCRIPT_MESSAGE");
      writer.writeText(message, null);
      writer.endElement("noscript");
    }
  }

  private void _storeInitialFocus(
    AdfRenderingContext arc,
    FacesBean           bean)
  {

    // The initialFocus functionality is only supported in inaccessible mode,
    // and only platforms that support scripting.
    if (!isInaccessibleMode(arc) || !supportsScripting(arc))
    {
      return;
    }

    // initial focus is the id of the component to which you want the
    // focus to be when the page full-page loads. In a PPR, the focus is
    // not set, which is a good thing.
    String initialFocusID = getInitialFocusId(bean);
    if (initialFocusID != null)
    {
      // Put the initial focus id on the rendering context for use in
      // postrender and also so that it can be modified
      // by the component with this id if necessary. For example,
      // the NavigationBar needs its initial focus on the Next button, but
      // the component's id does not get rendered on the Next button. The
      // NavigationBar creates a special id for the Next button, and sticks
      // this id back on the AdfRenderingContext for the body to know about in
      // postrender.
      arc.getProperties().put(XhtmlConstants.INITIAL_FOCUS_CONTEXT_PROPERTY,
                              initialFocusID);
    }
  }

  //
  // Writes a small script that sets the _initialFocusID variable on the page.
  //
  private void _renderInitialFocusScript(
    FacesContext        context,
    AdfRenderingContext arc
    ) throws IOException
  {

    // The initialFocus functionality is not supported if not inaccessible mode
    // nor on Netscape nor on platforms that do not support scripting.
    if (!isInaccessibleMode(arc) || !supportsScripting(arc))
    {
      return;
    }

    // Render the initial focus id, if it exists on the rendering context.
    // The initial focus id was initially set in prerender, and may have
    // been overwritten by the component's renderer if need be.
    String initialFocusID = (String) arc.getProperties().get(
                                    XhtmlConstants.INITIAL_FOCUS_CONTEXT_PROPERTY);

    if (initialFocusID != null)
    {
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement("script", null);
      XhtmlRenderer.renderScriptTypeAttribute(context, arc);
      writer.writeText("_initialFocusID='", null);
      writer.writeText(initialFocusID, null);
      writer.writeText("';", null);
      writer.endElement("script");
    }
  }

  // If partial back is supported,
  // render a span with the _PPR_BACK_CONTENT_ID id.
  // This is used to be able to save the html content of the page
  // when the user leaves the page. This way we can restore the html if
  // the user used the Back button to go back to the page.
  private static void _renderPartialBackSupportSpan(
    FacesContext        context,
    AdfRenderingContext arc,
    boolean             isStart
    ) throws IOException
  {
    if (_isPartialBackSupported(arc))
    {
      ResponseWriter writer = context.getResponseWriter();

      if (isStart)
      {
        writer.startElement("span", null);
        writer.writeAttribute("id", _PPR_BACK_CONTENT_ID, null);
      }
      else
      {
        writer.endElement("span");
        // render these hidden, disabled fields to save the javascript
        // library names, the inline scripts, and the page's contents
        // these will be restored when we come back to the page after
        // leaving it.
        _renderPartialBackSupportHiddenFields(writer, _PPR_BACK_SAVE_LIBRARY_ID);
        _renderPartialBackSupportHiddenFields(writer, _PPR_BACK_SAVE_SCRIPT_ID);
        _renderPartialBackSupportHiddenFields(writer, _PPR_BACK_SAVE_CONTENT_ID);
      }
    }
  }

  // given the field name, render an input element, with the fieldName
  // as the id, and make it hidden and disabled, so that the values do
  // not go to the server when the form is submitted.
  private static void _renderPartialBackSupportHiddenFields(
    ResponseWriter writer,
    String       fieldName
    ) throws IOException
  {

    writer.startElement("input", null);

    writer.writeAttribute("id", fieldName, null);
    writer.writeAttribute("type", "hidden", null);
    writer.writeAttribute("disabled", Boolean.TRUE, null);

    writer.endElement("input");
  }


  private static boolean _isPartialBackSupported(
    AdfRenderingContext arc
    )
  {
    /*
      // Only supported on IE  - but comment this out while
      // it's off altogether
    if (!isIE(arc))
      return false;
    */

    // =-=AEW We need a mechanism to turn it on... or we need
    // to remove it altogether
    return false;
  }

  //
  // Writes version information about the page.
  //
  static private void _writeVersionInformation(
    FacesContext context,
    AdfRenderingContext arc)
    throws IOException
  {
    String comment = _VERSION_COMMENT;

    Class implClass = BodyRenderer.class;
    Package implPkg = implClass.getPackage();

    Class apiClass  =  FacesBean.class;
    Package apiPkg  =  apiClass.getPackage();

    String versionInfo = _getVersionInfo(apiPkg, implPkg);

    comment += versionInfo;

    String accessibilityMode = null;
    if (isInaccessibleMode(arc))
      accessibilityMode = "disabled";
    else if (isScreenReaderMode(arc))
      accessibilityMode = "enhanced";

    if (accessibilityMode != null)
    {
      comment += ", Accessibility:"+accessibilityMode;
    }

    // Tack on the Skin id
    Skin skin = arc.getSkin();
    String skinId = skin.getId();
    if (skinId != null)
    {
      comment += ", skin:" + skinId;

      // Also log preferred Skin if we have one
      AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
      String preferredSkin = adfFacesContext.getSkinFamily();
      if (preferredSkin != null)
        comment += (" (" + preferredSkin.toString() + ")");
    }

    context.getResponseWriter().writeComment(comment);
  }


  private static String _getVersionInfo(Package apiPackage, Package implPackage)
  {

    String versionInfo    = "";
    String apiSpecTitle   = "ADF Faces API";
    String implSpecTitle  = "ADF Faces Implementation";
    String apiVersion     = "??";
    String implVersion    = "??";

    String temp;
    // This normally happens in dev environment, when we have mapped to the
    // dependencies. If you need to see the version info, then map it to the
    // api snapshot and impl snapshot jar and remove the dependency on api and impl
    // in the project. If we don't want to impact golden files, we can simply
    // return a blank string.
    if (apiPackage == null && implPackage == null)
    {
      return "(Version unknown)";
    }
    else
    {
      if (apiPackage != null)
      {
        apiSpecTitle = (((temp = apiPackage.getSpecificationTitle()) != null)?
                        temp : apiSpecTitle);

        apiVersion   = (((temp = apiPackage.getImplementationVersion()) != null)?
                        temp : apiVersion);
      }

      if(implPackage != null)
      {
        implSpecTitle = (((temp = implPackage.getSpecificationTitle())!= null)?
                        temp : implSpecTitle);

        implVersion   = (((temp = implPackage.getImplementationVersion())!= null)?
                        temp : implVersion);
       }

      // if there is version mismatch let us mark and print a verbose
      // information
      if (apiVersion == "??" || implVersion == "??")
        versionInfo = "Version unknown: ";
      else if (apiVersion != "??" && implVersion != "??"
                && !apiVersion.equals(implVersion))
        versionInfo = "Version mismatch: ";
    }

    return "(" + versionInfo + apiSpecTitle  + " - " + apiVersion  + "/"
                             + implSpecTitle + " - " + implVersion + ")";
  }

  private PropertyKey _firstClickPassedKey;
  private PropertyKey _initialFocusIdKey;
  private PropertyKey _onloadKey;
  private PropertyKey _onunloadKey;

  // Onunload handler used for partial page rendering
  private static final String _PARTIAL_ONUNLOAD_HANDLER = "_partialUnload()";

  static private final String _VERSION_COMMENT = "Created by Oracle ADF Faces ";

  // Constants for PPR back
  static private final String _PPR_BACK_UNLOAD_SCRIPT = "_savePageStateIE()";
  static private final String _PPR_BACK_CONTENT_ID = "_pprPageContent";
  static private final String _PPR_BACK_SAVE_CONTENT_ID = "_pprSavePage";
  static private final String _PPR_BACK_SAVE_SCRIPT_ID = "_pprSaveScript";
  static private final String _PPR_BACK_SAVE_LIBRARY_ID = "_pprSaveLib";
}
