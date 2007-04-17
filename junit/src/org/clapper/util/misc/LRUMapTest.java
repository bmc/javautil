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

import java.util.Map;
import org.clapper.util.logging.Logger;

/**
 *
 */
public class LRUMapTest extends MapTestBase
{
    /*----------------------------------------------------------------------*\
                            Private Instance Data
    \*----------------------------------------------------------------------*/

    private static final Logger log = new Logger(LRUMapTest.class);

    /*----------------------------------------------------------------------*\
                               Inner Classes
    \*----------------------------------------------------------------------*/

    class TestListener implements ObjectRemovalListener
    {
        private String lookForKey;
        private String lookForValue;
        private boolean called;

        TestListener(String lookForKey, String lookForValue)
        {
            this.lookForKey = lookForKey;
            this.lookForValue = lookForValue;
        }

        public void objectRemoved(ObjectRemovalEvent event)
        {
            Map.Entry<String,String> removed =
                (Map.Entry<String,String>) event.getSource();
            log.debug("ObjectRemovalListener called for " +
                      removed.getKey() + "=" + removed.getValue());
            assertEquals("Removed item has wrong key",
                         lookForKey, removed.getKey());
            assertEquals("Removed item has wrong value",
                         lookForValue, removed.getValue());
            called = true;
        }

        boolean wasCalled()
        {
            return called;
        }
    }

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    public LRUMapTest(String testName)
    {
        super(testName);
    }

    /*----------------------------------------------------------------------*\
                               Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Test of addRemovalListener method, of class org.clapper.util.misc.LRUMap.
     */
    public void testAddRemovalListener()
    {
        LRUMap<String,String> map = new LRUMap<String,String>(1);
        map.put("a", "a value");

        TestListener listener = new TestListener("a", "a value");
        map.addRemovalListener(listener, true);
        map.put("b", "b value");
        assertEquals("Map size should be 1", 1, map.size());
        assertTrue("Listener not invoked as expected", listener.wasCalled());
    }

    /**
     * Test of removeRemovalListener method, of class org.clapper.util.misc.LRUMap.
     */
    public void testRemoveRemovalListener()
    {
        LRUMap<String,String> map = new LRUMap<String,String>(1);
        map.put("a", "a value");

        TestListener listener = new TestListener("a", "a value");
        map.addRemovalListener(listener, false);
        map.removeRemovalListener(listener);
        map.remove("a");
        assertEquals("Map size should be 0", 0, map.size());
        assertFalse("Listener unexpectedly invoked", listener.wasCalled());
    }

    /**
     * Test of getInitialCapacity method, of class org.clapper.util.misc.LRUMap.
     */
    public void testGetInitialCapacity()
    {
        LRUMap<String,String> map = new LRUMap<String,String>(1, 10);
        assertEquals("Explicit initial capacity problem",
                     1,
                     map.getInitialCapacity());
        map = new LRUMap<String,String>(100);
        assertEquals("Default initial capacity problem",
                     LRUMap.DEFAULT_INITIAL_CAPACITY,
                     map.getInitialCapacity());
    }

    /**
     * Test of getLoadFactor method, of class org.clapper.util.misc.LRUMap.
     */
    public void testGetLoadFactor()
    {
        LRUMap<String,String> map = new LRUMap<String,String>(1, 10);
        assertEquals("Default load factor problem",
                     LRUMap.DEFAULT_LOAD_FACTOR,
                     map.getLoadFactor());
        map = new LRUMap<String,String>(1, 0.90f, 10);
        assertEquals("Explicit load factor problem",
                     0.90f,
                     map.getLoadFactor());
    }

    /**
     * Test of getMaximumCapacity method, of class org.clapper.util.misc.LRUMap.
     */
    public void testGetMaximumCapacity()
    {
        LRUMap<String,String> map = new LRUMap<String,String>(1, 10);
        assertEquals("Maximum capacity problem",
                     10,
                     map.getMaximumCapacity());
        map = new LRUMap<String,String>(10);
        assertEquals("Maximum capacity problem",
                     10,
                     map.getMaximumCapacity());
    }

    /**
     * Test of setMaximumCapacity method, of class org.clapper.util.misc.LRUMap.
     */
    public void testSetMaximumCapacity()
    {
        LRUMap<Integer,String> map = makeAndFillIntegerKeyedMap(10);
        // Add some more.
        for (int i = 0; i < 5; i++)
            map.put(i, String.valueOf(i));
        map.setMaximumCapacity(5);
        assertEquals("Explicitly lowered maximum capacity incorrect", 5,
                     map.getMaximumCapacity());
        assertEquals("Size should be same as max capacity, but isn't",
                     map.getMaximumCapacity(), map.size());
    }

    /**
     * Test of clone method, of class org.clapper.util.misc.LRUMap.
     */
    public void testClone()
        throws CloneNotSupportedException
    {
        LRUMap<Integer,String> map = makeAndFillIntegerKeyedMap(10);
        LRUMap<String,Integer> map2 = (LRUMap<String,Integer>) map.clone();
        assertEquals("Clone map not equal to original", map, map2);
    }

    /**
     * Test the LRU behavior.
     */
    public void testLRUBehavior()
    {
        LRUMap<Integer,String> map = makeAndFillIntegerKeyedMap(10);

        // First one thrown out should be 0, if we add a new one
        assertTrue("Map doesn't contain key 0", map.containsKey(0));
        map.put(100, "100");
        assertFalse("Map still contains key 0", map.containsKey(0));

        // Next one thrown out would be 1, except that we're going to
        // access it, which makes it "new" again, so 2 should be tossed out
        // instead.
        map.get(1);
        assertTrue("Map doesn't contain key 1", map.containsKey(1));
        map.put(101, "101");
        map.get(1);
        assertTrue("Map doesn't contain freshened key 1", map.containsKey(1));
        assertFalse("Map still contains key 2", map.containsKey(2));
        
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    protected Map<String,String> newMap()
    {
        return new LRUMap<String,String>(100);
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private LRUMap<Integer,String> makeAndFillIntegerKeyedMap(int capacity)
    {
        LRUMap<Integer,String> map = new LRUMap<Integer,String>(capacity);
        for (int i = 0; i < capacity; i++)
            map.put(i, String.valueOf(i));
        return map;
    }
}


