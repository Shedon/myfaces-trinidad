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
package org.apache.myfaces.adfinternal.taglib.util;

import javax.faces.el.ValueBinding;

import org.apache.myfaces.adf.bean.PropertyKey;
import org.apache.myfaces.adf.bean.FacesBean;

import org.apache.myfaces.adfinternal.util.nls.StringUtils;

import org.apache.myfaces.adfinternal.binding.AccessKeyBinding;
import org.apache.myfaces.adfinternal.binding.StripAccessKeyBinding;

/**
 *
 * @author The Oracle ADF Faces Team
 */
public class VirtualAttributeUtils
{
  public static void setAccessKeyAttribute(
    FacesBean    bean,
    String       text,
    PropertyKey  textKey,
    PropertyKey  accessKeyKey)
  {
    // set the acess key, if any
    int accessKeyIndex = StringUtils.getMnemonicIndex(text);

    if (accessKeyIndex != StringUtils.MNEMONIC_INDEX_NONE)
    {
      bean.setProperty(accessKeyKey,
                       new Character(text.charAt(accessKeyIndex + 1)));
    }

    // set the stripped text on the node using the appropriate attribute name
    bean.setProperty(textKey,
                     StringUtils.stripMnemonic(text));
  }

  public static void setAccessKeyAttribute(
    FacesBean    bean,
    ValueBinding valueBinding,
    PropertyKey  textKey,
    PropertyKey  accessKeyKey)
  {
    bean.setValueBinding(accessKeyKey,
                         new AccessKeyBinding(valueBinding));
    bean.setValueBinding(textKey,
                         new StripAccessKeyBinding(valueBinding));
  }

  private VirtualAttributeUtils()
  {
  }
}
