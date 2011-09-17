package org.clapper.util.misc;

import java.util.Map;
import org.clapper.util.logging.Logger;

import org.junit.*;
import static org.junit.Assert.*;

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

    public LRUMapTest()
    {
    }

    /*----------------------------------------------------------------------*\
                               Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Test of addRemovalListener method, of class org.clapper.util.misc.LRUMap.
     */
    @Test public void addRemovalListener()
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
     * Test of removeRemovalListener method, of class
     * org.clapper.util.misc.LRUMap.
     */
    @Test public void removeRemovalListener()
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
    @Test public void getInitialCapacity()
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
    @Test public void getLoadFactor()
    {
        LRUMap<String,String> map = new LRUMap<String,String>(1, 10);
        assertEquals(LRUMap.DEFAULT_LOAD_FACTOR, map.getLoadFactor(), 0.0f);
        map = new LRUMap<String,String>(1, 0.90f, 10);
        assertEquals(0.90f, map.getLoadFactor(), 0.0f);
    }

    /**
     * Test of getMaximumCapacity method, of class org.clapper.util.misc.LRUMap.
     */
    @Test public void getMaximumCapacity()
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
    @Test public void setMaximumCapacity()
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
    @Test public void testClone()
        throws CloneNotSupportedException
    {
        LRUMap<Integer,String> map = makeAndFillIntegerKeyedMap(10);
        LRUMap<String,Integer> map2 = (LRUMap<String,Integer>) map.clone();
        assertEquals("Clone map not equal to original", map, map2);
    }

    /**
     * Test the LRU behavior.
     */
    @Test public void lruBehavior()
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


