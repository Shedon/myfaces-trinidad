/*
 * Copyright  2004-2006 The Apache Software Foundation.
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
package org.apache.myfaces.trinidadinternal.renderkit.uix;


import java.io.IOException;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.trinidad.logging.ADFLogger;
import org.apache.myfaces.trinidad.component.UIXProgress;
import org.apache.myfaces.trinidad.model.BoundedRangeModel;

import org.apache.myfaces.trinidadinternal.ui.UIConstants;
import org.apache.myfaces.trinidadinternal.uinode.UINodeRendererBase;

/**
 * Renderer for progress components
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/renderkit/uix/ProgressRenderer.java#0 $) $Date: 10-nov-2005.19:00:33 $
 * @author The Oracle ADF Faces Team
 */
public class ProgressRenderer extends UINodeRendererBase
{
  public void decode(FacesContext context, UIComponent component)
  {
    UIXProgress progressComponent = (UIXProgress)component;
    Map attrs = component.getAttributes();

    Object modelValue= attrs.get(UIConstants.VALUE_PARAM);
    if (modelValue instanceof BoundedRangeModel)
    {
      BoundedRangeModel model = (BoundedRangeModel) modelValue;
      if (model != null)
      {
        long value = model.getValue();
        long maximum = model.getMaximum();
        if (maximum <= value)
        //pu: This means the background task is complete.
        {
          (new ActionEvent(progressComponent)).queue();
        }
      }
    }
  }

  public void encodeBegin(FacesContext context, UIComponent component)
    throws IOException
  {
    //pu: This seems to be the best place to validate the model for the value
    Map attrs = component.getAttributes();
    Object modelObject = attrs.get(UIConstants.VALUE_PARAM);
    if (modelObject == null || !(modelObject instanceof BoundedRangeModel))
    {
      _LOG.warning("Value for component with id '" +
        component.getId() +
        "' is not a valid BoundedRangeModel instance");
    }
    super.encodeBegin(context, component);
  }

  static private final ADFLogger _LOG = ADFLogger.createADFLogger(ProgressRenderer.class);
}
