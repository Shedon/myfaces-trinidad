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
package org.apache.myfaces.trinidadinternal.ui.data.bind;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.data.BoundValue;

/**
 * BoundValue implementation that returns the result
 * of calling RenderingContext.getProperty().
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/data/bind/ContextPropertyBoundValue.java#0 $) $Date: 10-nov-2005.18:56:37 $
 * @author The Oracle ADF Faces Team
 */
public class ContextPropertyBoundValue implements BoundValue
{
  /**
   * Create a BoundValue that returns the result of
   * calling RenderingContext.getProperty()
   * <p>
   * @see org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext#getProperty
   * @see org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext#setProperty
   */
  public ContextPropertyBoundValue(
    String namespace,
    Object key)
  {
    _namespace = namespace;
    _key = key;
  }
  
  
  /**
   * Called to retrieve a value based on the current rendering
   * context.  ContextPropertyBoundValues always return the value
   * passed into the constructor.
   * <p>
   * @param context the rendering context
   */
  public Object getValue(
    UIXRenderingContext context
    )
  {
    if (context == null)
      return null;

    return context.getProperty(_namespace, _key);
  }
  
  private String _namespace;
  private Object _key;
}
