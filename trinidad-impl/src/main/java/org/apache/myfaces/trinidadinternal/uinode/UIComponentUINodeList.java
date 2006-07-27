/*
 * Copyright  2003-2006 The Apache Software Foundation.
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
package org.apache.myfaces.trinidadinternal.uinode;

import java.util.List;

import javax.faces.component.UIComponent;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.UINode;
import org.apache.myfaces.trinidadinternal.ui.collection.UINodeList;

/**
 * Use a UIComponent as a uinodeList to deliver the component's children as
 * UIComponentUINode's
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/uinode/UIComponentUINodeList.java#0 $) $Date: 10-nov-2005.18:49:17 $
 * @author The Oracle ADF Faces Team
 */
public class UIComponentUINodeList implements UINodeList
{
  public UIComponentUINodeList(
   UIComponent component
   )
  {
    if (component == null)
      throw new NullPointerException();

    _component = component;
  } 

  public int size(UIXRenderingContext context)
  {
    return _component.getChildCount();
  }

  public UINode getUINode(UIXRenderingContext context, int index)
  {      
    List children =  _component.getChildren();
    
    return UIComponentUINode.__getUINode((UIComponent)children.get(index));
  }

  public UINode setUINode(int index, UINode node)
  {
    throw new UnsupportedOperationException();
  }

  public void addUINode(int index, UINode node)
  {
    throw new UnsupportedOperationException();
  }

  public void addUINode(UINode node)
  {
    throw new UnsupportedOperationException();
  }

  public UINode removeUINode(int index)
  {
    throw new UnsupportedOperationException();
  }

  public void clearUINodes()
  {
    throw new UnsupportedOperationException();
  }

  public Object clone()
  {
    throw new UnsupportedOperationException();
  }

  private UIComponent _component;
}
