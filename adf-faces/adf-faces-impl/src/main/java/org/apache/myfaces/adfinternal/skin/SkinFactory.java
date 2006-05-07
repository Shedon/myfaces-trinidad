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
package org.apache.myfaces.adfinternal.skin;

import java.util.HashMap;
import java.util.Iterator;
import javax.faces.context.FacesContext;


/**
 * Factory for creating Skin objects.
 * 
 * @author The Oracle ADF Faces Team
 */
abstract public class SkinFactory
{
  /**
   * Retrieve the current SkinFactory.
   */
  static public SkinFactory getFactory()
  {
    synchronized (_FACTORIES)
    {
      return (SkinFactory) _FACTORIES.get(_getClassLoader());
    }
  }

  /**
   * Store the current SkinFactory.
   */
  static public void setFactory(SkinFactory factory)
  {
    synchronized (_FACTORIES)
    {
      ClassLoader cl = _getClassLoader();
      if (_FACTORIES.get(cl) != null)
      {
        throw new IllegalStateException(
          "Factory already available for this class loader.");
      }

      _FACTORIES.put(cl, factory);
    }
  }

  /**
   * <p>Register the specified {@link Skin} instance, associated with
   * the specified <code>skinId</code>, to be supported by this
   * {@link SkinFactory}, replacing any previously registered
   * {@link Skin} for this identifier.</p>
   *
   * @param skinId Identifier of the {@link Skin} to register
   * @param skin {@link Skin} instance that we are registering
   */
  public abstract void addSkin(String skinId, Skin skin);


  /**
   * <p>Return a {@link Skin} instance for the specified skinId. 
   * If there is no registered {@link
   * Skin} for the specified identifier, return
   * <code>null</code>.  The set of available skin identifiers
   * is available via the <code>getSkinIds()</code> method.</p>
   *
   * @param context FacesContext for the request currently being
   * processed, or <code>null</code> if none is available.
   * @param skinId Skin identifier of the requested
   *  {@link Skin} instance
   */
  public abstract Skin getSkin(FacesContext context, String skinId);

  /**
   * <p>Return a {@link Skin} instance for the specified skinFamily and
   * renderKitId. 
   * If there is no registered {@link
   * Skin} for the specified identifier, return
   * <code>null</code>.  The set of available skin identifiers
   * is available via the <code>getSkinIds()</code> method.</p>
   *
   * @param context FacesContext for the request currently being
   * processed, or <code>null</code> if none is available.
   * @param family family of the requested {@link Skin} instance
   * @param renderKitId RenderKit identifier of the requested
   *  {@link Skin} instance
   */
  public abstract Skin getSkin(
    FacesContext context, 
    String family, 
    String renderKitId);
  
  /**
   * <p>Return an <code>Iterator</code> over the set of skin
   * identifiers registered with this factory.
   * </p>
   */
  public abstract Iterator getSkinIds();


  static private ClassLoader _getClassLoader()
  {
    return Thread.currentThread().getContextClassLoader();
  }

  static private final HashMap _FACTORIES = new HashMap();
}
