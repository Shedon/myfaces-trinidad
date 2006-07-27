/*
 * Copyright  1999-2006 The Apache Software Foundation.
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
package org.apache.myfaces.trinidadinternal.ui.data;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;

import java.util.Iterator;
import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;

/**
 * The DictionaryData interface provides a simple,
 * hashtable-like datasource.  The "select" strings
 * are defined as keys to string values;  DataSet
 * is not supported.
 * 
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/data/DictionaryData.java#0 $) $Date: 10-nov-2005.18:56:32 $
 * @author The Oracle ADF Faces Team
 */
public class DictionaryData implements KeyedDataObject, MutableDataObject
{
  /**
   * Creates a DictionaryData.
   */
  public DictionaryData()
  {
    _table = new Hashtable(7);
  }

  /**
   * Creates a DictionaryData, using the provided Dictionary
   * for storage.
   */
  public DictionaryData(Dictionary table)
  {
    _table = table;
  }


  /**
   * Creates a DictionaryData initialized with a single
   * pair of data.
   */
  public DictionaryData(Object select, Object data)
  {
    this();
    put(select, data);
  }


  /**
   * Adds a key/value pair to the DictionaryData.
   */
  public void put(Object select, Object data)
  {
    _table.put(select, data);
  }


  /**
   * Returns the value registered with the select key.
   */
  public Object selectValue(UIXRenderingContext context, Object select)
  {
    if (select == null)
      return null;
    return  _table.get(select);
  }
  
  public void updateValue(
    UIXRenderingContext context, 
    Object select,
    Object value)
  {
    put(select, value);
  }
  
  public Iterator keys(
    UIXRenderingContext context
    )
  {
    ArrayList keyList = new ArrayList();
    Enumeration keyEnum = _table.keys();  
    while(keyEnum.hasMoreElements())
    {
      keyList.add(keyEnum.nextElement());
    }
    return keyList.iterator();
  }
  
  private Dictionary _table;
}
