/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.myfaces.trinidadinternal.style.xml.parse;

/**
 * IncludePropertyNode is used to represent a single <includeProperty> element
 * in a parsed XML Style Sheet Language document.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/style/xml/parse/IncludePropertyNode.java#0 $) $Date: 10-nov-2005.18:58:07 $
 */
public class IncludePropertyNode
{
  /**
   * Creates an IncludePropertyNode.  In general, either the name or
   * selector of the included style is specified.
   */
  public IncludePropertyNode(
    String name,
    String selector,
    String propertyName,
    String localPropertyName)
  {

    assert ((name!=null) ||(selector!=null));

    if (propertyName == null)
    {
      throw new NullPointerException("Null propertyName");
    }

    _name = name;
    _selector = selector;
    _propertyName = propertyName;
    _localPropertyName = localPropertyName;
  }

  /**
   * Returns the name of the style to include.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Returns the selector of the style to include.
   */
  public String getSelector()
  {
    return _selector;
  }

  /**
   * Returns the name of the property to include
   */
  public String getPropertyName()
  {
    return _propertyName;
  }

  /**
   * Returns the name of the property as it should appear in the
   * including style.
   */
  public String getLocalPropertyName()
  {
    if (_localPropertyName == null)
      return _propertyName;

    return _localPropertyName;
  }

  private String _name;
  private String _selector;
  private String _propertyName;
  private String _localPropertyName;
}
