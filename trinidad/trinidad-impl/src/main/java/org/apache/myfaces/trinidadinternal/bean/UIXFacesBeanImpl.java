/*
 * Copyright  2005,2006 The Apache Software Foundation.
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
package org.apache.myfaces.trinidadinternal.bean;

import org.apache.myfaces.trinidad.bean.FacesBeanImpl;
import org.apache.myfaces.trinidad.component.UIXComponent;
import org.apache.myfaces.trinidad.component.UIXFacesBean;

public class UIXFacesBeanImpl extends FacesBeanImpl implements UIXFacesBean
{
  public UIXFacesBeanImpl()
  {
  }

  @Override
  public Type getType()
  {
    return _type;
  }

  public void init(
    UIXComponent   component,
    Type type)
  {
    _type = type;
  }  

  private Type _type;
}
