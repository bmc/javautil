/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2006 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M.a Clapper.

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

import java.io.IOException;
import junit.framework.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class FileHashMapTest extends TestCase
{
    public FileHashMapTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    /**
     * Test of clear method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testClear()
        throws IOException
    {
        System.out.println("clear");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.put("a", "foo");
        assertEquals ("Hash table size mismatch", 1, map.size());

        map.clear();
        assertEquals ("Hash table size mismatch", 0, map.size());

        map.close();
   }

    /**
     * Test of close method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testClose()
        throws IOException
    {
        System.out.println("close");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.close();
        assertFalse (map.isValid());

        map.close();
   }

    /**
     * Test of containsKey method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testContainsKey()
        throws IOException
    {
        System.out.println("containsKey");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.put("a", "a value");
        map.put("b", "b value");

        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertFalse(map.containsKey("c"));

        map.close();
   }

    /**
     * Test of containsValue method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testContainsValue()
        throws IOException
    {
        System.out.println("containsValue");


        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.put("a", "a value");
        map.put("b", "b value");

        assertTrue(map.containsValue("a value"));
        assertTrue(map.containsValue("b value"));
        assertFalse(map.containsValue("c value"));

        map.close();
    }

    /**
     * Test of entrySet method, of class org.clapper.util.misc.FileHashMap.
      *
     * @throws IOException error initializing map
    */
    public void testEntrySet()
         throws IOException
   {
        System.out.println("entrySet");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
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

        map.close();
    }

    /**
     * Test of equals method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testEquals()
        throws IOException
    {
        System.out.println("equals");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        FileHashMap<String,String> map2 = new FileHashMap<String,String>();
        assertTrue ("Two empty maps not equal", map.equals (map2));

        map.put("a", "a value");
        map2.put("a", "a value");
        assertTrue ("Two maps with one identical value not equal",
                    map.equals(map2));

        map2.put("b", "b value");
        assertFalse("Maps with different values are equal", map.equals(map2));

        map.close();
    }

    /**
     * Test of get method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testGet()
        throws IOException
    {
        System.out.println("get");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.put("a", "a value");

        assertNotNull("Attempt to retrieve known value returns null",
                      map.get("a"));
        assertNull("Attempt to retrieve nonexistent value returns non-null",
                   map.get("foo"));

        map.close();
    }

    /**
     * Test of keySet method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testKeySet()
        throws IOException
    {
        System.out.println("keySet");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.put("a", "a value");
        map.put("b", "b value");

        Set<String> keySet = map.keySet();
        assertTrue(keySet.contains("a"));
        assertTrue(keySet.contains("b"));
        assertFalse(keySet.contains("c"));

        map.close();
    }

    /**
     * Test of put method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testPut()
        throws IOException
    {
        System.out.println("put");

        FileHashMap<String,String> map = new FileHashMap<String,String>();

        map.put("a", "a value");
        assertTrue("Map doesn't contain value just put there",
                   map.containsKey("a"));

        map.put("b", "b value");
        assertTrue("Map doesn't contain value just put there",
                   map.containsKey("b"));

        map.close();
    }

    /**
     * Test of remove method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testRemove()
        throws IOException
    {
        System.out.println("remove");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.put("a", "a value");
        map.put("b", "b value");

        String val = map.remove("a");
        assertNotNull("Couldn't remove existing object in map", val);
        assertEquals("Got back wrong object from remove", "a value", val);
        assertNull("Map still contains removed value", map.get("a"));
    }

  /**
     * Test of size method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testSize()
        throws IOException
    {
        System.out.println("size");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.put("a", "a value");
        assertEquals("Size not valid", 1, map.size());

        map.put("b", "b value");
        assertEquals("Size not valid", 2, map.size());

        map.remove("b");
        assertEquals("Size not valid", 1, map.size());

        map.close();
    }

    /**
     * Test of values method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testValues()
        throws IOException
    {
        System.out.println("values");

        FileHashMap<String,String> map = new FileHashMap<String,String>();
        map.put("a", "a value");
        map.put("b", "b value");

        Collection<String> values = map.values();
        assertTrue("Returned values set does not contain value",
                   values.contains("a value"));
        assertTrue("Returned values set does not contain value",
                   values.contains("b value"));

        map.close();
    }
}
