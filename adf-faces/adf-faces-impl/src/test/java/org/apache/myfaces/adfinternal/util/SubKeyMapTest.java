/*
 * Copyright 2000, 2005-2006 The Apache Software Foundation.
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
package org.apache.myfaces.adfinternal.util;

import java.util.Map;
import java.util.HashMap;

/**
 * Test of SubKeyMap.
 */
public class SubKeyMapTest extends org.apache.myfaces.adfbuild.test.MapTestCase
{
  public SubKeyMapTest(String testName)
  {
    super(testName);
  }

  public void testSubKey()
  {
    HashMap base = new HashMap();
    SubKeyMap one = new SubKeyMap(base, "one");
    SubKeyMap two = new SubKeyMap(base, "two");

    base.put("foo", "bar");
    assertTrue(one.isEmpty());
    assertTrue(two.isEmpty());

    one.put("foo", "baz");
    assertEquals(one.size(), 1);
    assertTrue(two.isEmpty());
    
    two.put("foo", "bap");
    assertEquals(one.size(), 1);
    assertEquals(base.get("foo"), "bar");
    assertEquals(one.get("foo"), "baz");
    assertEquals(two.get("foo"), "bap");
    assertEquals(two.size(), 1);

    one.clear();
    two.clear();
    assertEquals(base.size(), 1);
    assertTrue(one.isEmpty());
    assertTrue(two.isEmpty());
  }


  protected boolean supportsNullKeys()
  {
    return false;
  }


  protected Map createMap()
  {
    HashMap base = new HashMap();

    // Make sure we're not fooled by pre-existing contents
    base.put("something", "here");
    base.put(null, "ya know");
    base.put("elsewhere", null);
            
    return new SubKeyMap(base, "prefix1");
  }
}
