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
package org.apache.myfaces.adfinternal.agent.parse;

import java.net.URL;

/**
 * Object that holds information about the include node in capabilities file
 */
class IncludeNode
{

  IncludeNode(String refid, URL srcUrl)
  {
    _refid = refid;
    _srcUrl = srcUrl;
  }

  String __getRefId()
  {
    return _refid;
  }

  URL __getSrcUrl()
  {
    return _srcUrl;
  }

  private String _refid;
  private URL _srcUrl;

}
