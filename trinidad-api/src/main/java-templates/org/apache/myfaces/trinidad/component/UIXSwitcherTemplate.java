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
package org.apache.myfaces.trinidad.component;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 * Base class for the switcher componnet.
 * <p>
 * @version $Name:  $ ($Revision$) $Date$
 * @author The Oracle ADF Faces Team
 */
abstract public class UIXSwitcherTemplate extends UIXComponentBase
{
/**/ // Abstract methods implemented by code gen
/**/  abstract public String getFacetName();
/**/  abstract public String getDefaultFacet();

  /**
   * Only decode the currently active facet.
   */
  @Override
  public void processDecodes(FacesContext context)
  {
    UIComponent facet = _getFacet();
    if (facet != null)
      facet.processDecodes(context);
  }

  /**
   * Only process validations on the currently active facet.
   */
  @Override
  public void processValidators(FacesContext context)
  {
    UIComponent facet = _getFacet();
    if (facet != null)
      facet.processValidators(context);
  }


  /**
   * Only process updates on the currently active facet.
   */
  @Override
  public void processUpdates(FacesContext context)
  {
    UIComponent facet = _getFacet();
    if (facet != null)
      facet.processUpdates(context);
  }


  /**
   * Only render the currently active facet.
   */
  @Override
  public void encodeChildren(FacesContext context)
    throws IOException
  {
    UIComponent facet = _getFacet();
    if (facet != null)
      __encodeRecursive(context, facet);
  }


  /**
   * Override to return true.
   */
  @Override
  public boolean getRendersChildren()
  {
    return true;
  }

  private UIComponent _getFacet()
  {
    if (!isRendered())
      return null;

    String facetName = getFacetName();
    if (facetName != null)
    {
      UIComponent facet = getFacet(facetName);
      if (facet != null)
        return facet;
    }

    String defaultFacet = getDefaultFacet();
    if (defaultFacet != null)
      return getFacet(defaultFacet);

    return null;
  }

}

