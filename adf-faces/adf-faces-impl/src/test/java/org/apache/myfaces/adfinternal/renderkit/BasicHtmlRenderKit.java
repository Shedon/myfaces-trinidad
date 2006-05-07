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
package org.apache.myfaces.adfinternal.renderkit;

import java.io.OutputStream;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.component.UICommand;
import javax.faces.component.UIData;
import javax.faces.component.UIForm;
import javax.faces.component.UIGraphic;
import javax.faces.component.UIInput;
import javax.faces.component.UIMessage;
import javax.faces.component.UIMessages;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;

import javax.faces.context.FacesContext;

import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;

public class BasicHtmlRenderKit extends RenderKitBase
{
  public BasicHtmlRenderKit()
  {
    addRenderer("javax.faces.Output",
                "javax.faces.Text",
                "com.sun.faces.renderkit.html_basic.TextRenderer");
    addRenderer("javax.faces.Input",
                "javax.faces.Text",
                "com.sun.faces.renderkit.html_basic.TextRenderer");
    addRenderer("javax.faces.Data",
                "javax.faces.Table",
                "com.sun.faces.renderkit.html_basic.TableRenderer");
    addRenderer("javax.faces.SelectOne",
                "javax.faces.Menu",
                "com.sun.faces.renderkit.html_basic.MenuRenderer");
    addRenderer("javax.faces.SelectOne",
                "javax.faces.Radio",
                "com.sun.faces.renderkit.html_basic.RadioRenderer");
    Renderer n = new Renderer()
    {
      public void decode(FacesContext context, UIComponent component)
      {
      }

      public void encodeBegin(FacesContext context, UIComponent component)
      {
      }
      public void encodeChildren(FacesContext context, UIComponent component)
      {
      }
      public void encodeEnd(FacesContext context, UIComponent component)
      {
      }
    };

    addRenderer(UICommand.COMPONENT_FAMILY,       "javax.faces.Button", n);
    addRenderer(UIPanel.COMPONENT_FAMILY,         "javax.faces.Grid", n);
    addRenderer(UIPanel.COMPONENT_FAMILY,         "javax.faces.Group", n);
    addRenderer(UIInput.COMPONENT_FAMILY,         "javax.faces.Hidden", n);
    addRenderer(UIInput.COMPONENT_FAMILY,         "javax.faces.Secret", n);
    addRenderer(UIInput.COMPONENT_FAMILY,         "javax.faces.Textarea", n);
    addRenderer(UIGraphic.COMPONENT_FAMILY,       "javax.faces.Image", n);
    addRenderer(UIMessage.COMPONENT_FAMILY,       "javax.faces.Message", n);
    addRenderer(UIMessages.COMPONENT_FAMILY,      "javax.faces.Messages", n);
    addRenderer(UIOutput.COMPONENT_FAMILY ,       "javax.faces.Format", n);
    addRenderer(UIOutput.COMPONENT_FAMILY ,       "javax.faces.Label", n);
    addRenderer(UIOutput.COMPONENT_FAMILY ,       "javax.faces.Link", n);
    addRenderer(UISelectBoolean.COMPONENT_FAMILY, "javax.faces.Checkbox", n);
    addRenderer(UISelectOne.COMPONENT_FAMILY,     "javax.faces.Listbox", n);
    addRenderer(UISelectMany.COMPONENT_FAMILY,    "javax.faces.Listbox", n);
    addRenderer(UISelectMany.COMPONENT_FAMILY,    "javax.faces.Menu", n);
    addRenderer(UISelectMany.COMPONENT_FAMILY,    "javax.faces.Checkbox", n);

  }

  public ResponseStateManager getResponseStateManager()
  {
    return null;
  }

  public ResponseStream createResponseStream(OutputStream out)
  {
    return null;
  }

  public ResponseWriter createResponseWriter(Writer out, String contentType, String encoding)
  {
    return null;
  }
}
