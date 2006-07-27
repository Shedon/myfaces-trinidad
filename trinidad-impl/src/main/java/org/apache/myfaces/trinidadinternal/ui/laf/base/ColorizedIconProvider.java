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
package org.apache.myfaces.trinidadinternal.ui.laf.base;


import org.apache.myfaces.trinidadinternal.image.ImageProviderResponse;
import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;

/**
 * Abstracts out the retrieval of colorized icons given icon keys
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/ColorizedIconProvider.java#0 $) $Date: 10-nov-2005.18:52:59 $
 * @author The Oracle ADF Faces Team
 */
public interface ColorizedIconProvider 
{
  
  /**
   * Retrieves the ImageProviderReponse for the image indentified
   * by the iconKey
   */
  public ImageProviderResponse getColorizedIcon(
    UIXRenderingContext context,
    IconKey          iconKey);
  



  /**
   * Returns the URI to the icon indentified by the icon key
   */
  public String getColorizedIconURI(
    UIXRenderingContext context,
    IconKey          iconKey
    );

}
