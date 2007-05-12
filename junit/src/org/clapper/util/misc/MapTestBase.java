/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2007 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M. Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc;

import junit.framework.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base class for map testers.
 *
 */
public abstract class MapTestBase extends TestCase
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    protected MapTestBase(String testName)
    {
        super(testName);
    }

    /*----------------------------------------------------------------------*\
                               Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Test of clear method.
     */
    public void testClear()
    {
        Map<String,String> map = newMap();
        map.put("a", "foo");
        assertEquals ("Hash table size mismatch", 1, map.size());

        map.clear();
        assertEquals ("Hash table size mismatch", 0, map.size());
   }

    /**
     * Test of containsKey method.
     */
    public void testContainsKey()
    {
        Map<String,String> map = newMap();
        map.put("a", "a value");
        map.put("b", "b value");

        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertFalse(map.containsKey("c"));
   }

    /**
     * Test of containsValue method.
     */
    public void testContainsValue()
    {
        Map<String,String> map = newMap();
        map.put("a", "a value");
        map.put("b", "b value");

        assertTrue("Missing expected value", map.containsValue("a value"));
        assertTrue("Missing expected value", map.containsValue("b value"));
        assertFalse("Unexpected value", map.containsValue("c value"));
    }

    /**
     * Test of entrySet method.
     */
    public void testEntrySet()
   {
        Map<String,String> map = newMap();
        map.put("a", "a value");
        map.put("b", "b value");

        Set<Map.Entry<String,String>> entrySet = map.entrySet();
        Set<Map.Entry<String,String>> entrySet2 = map.entrySet();
        assertEquals(entrySet, entrySet2);

        for (Map.Entry<String,String> entry : entrySet)
        {
            assertTrue("Map.Entry contains key not in map",
                       map.containsKey(entry.getKey()));
            assertTrue("Map.Entry contains value not in map",
                       map.containsKey(entry.getKey()));
        }

    }

    /**
     * Test of equals method.
     */
    public void testEquals()
    {
        Map<String,String> map = newMap();
        Map<String,String> map2 = newMap();
        assertTrue ("Two empty maps not equal", map.equals (map2));

        map.put("a", "a value");
        map2.put("a", "a value");
        assertTrue ("Two maps with one identical value not equal",
                    map.equals(map2));

        map2.put("b", "b value");
        assertFalse("Maps with different values are equal", map.equals(map2));
    }

    /**
     * Test of get method.
     */
    public void testGet()
    {
        Map<String,String> map = newMap();
        map.put("a", "a value");

        assertNotNull("Attempt to retrieve known value returns null",
                      map.get("a"));
        assertNull("Attempt to retrieve nonexistent value returns non-null",
                   map.get("foo"));
    }

    /**
     * Test of keySet method.
     */
    public void testKeySet()
    {
        Map<String,String> map = newMap();
        map.put("a", "a value");
        map.put("b", "b value");

        Set<String> keySet = map.keySet();
        assertTrue(keySet.contains("a"));
        assertTrue(keySet.contains("b"));
        assertFalse(keySet.contains("c"));
    }

    /**
     * Test of put method.
     */
    public void testPut()
    {
        Map<String,String> map = newMap();

        map.put("a", "a value");
        assertTrue("Map doesn't contain value just put there",
                   map.containsKey("a"));

        map.put("b", "b value");
        assertTrue("Map doesn't contain value just put there",
                   map.containsKey("b"));
    }

    /**
     * Test of putAll() method
     */
    public void putAll()
    {
        Map<String,String> map = new HashMap<String,String>();
        map.put("a", "a value");
        map.put("b", "b value");
        map.put("c", "c value");

        Map<String,String> map2 = newMap();
        map2.putAll(map);

        assertEquals("After putAll(), maps aren't equal", map, map2);
    }

    /**
     * Test of remove method.
     */
    public void testRemove()
    {
        Map<String,String> map = newMap();
        map.put("a", "a value");
        map.put("b", "b value");

        String val = map.remove("a");
        assertNotNull("Couldn't remove existing object in map", val);
        assertEquals("Got back wrong object from remove", "a value", val);
        assertNull("Map still contains removed value", map.get("a"));
    }

    /**
     * Test of size method.
     */
    public void testSize()
    {
        Map<String,String> map = newMap();
        map.put("a", "a value");
        assertEquals("Size not valid", 1, map.size());

        map.put("b", "b value");
        assertEquals("Size not valid", 2, map.size());

        map.remove("b");
        assertEquals("Size not valid", 1, map.size());
    }

    /**
     * Test of values method.
     */
    public void testValues()
    {
        Map<String,String> map = newMap();
        map.put("a", "a value");
        map.put("b", "b value");

        Collection<String> values = map.values();
        assertTrue("Returned values set does not contain value",
                   values.contains("a value"));
        assertTrue("Returned values set does not contain value",
                   values.contains("b value"));
        for (String key : map.keySet())
        {
            String value = map.get(key);
            assertTrue("Value set is missing value \"" + value + "\" " +
                       "for key \"" + key + "\"",
                       values.contains(value));
        }
    }

    /**
     * Test of isEmpty method, of class org.clapper.util.misc.LRUMap.
     */
    public void testIsEmpty()
    {
        Map<String,String> map = newMap();
        assertTrue("isEmpty() returns false on empty map", map.isEmpty());
        map.put("a", "a value");
        assertFalse("isEmpty() returns true on non-empty map", map.isEmpty());
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    protected abstract Map<String,String> newMap();
}
