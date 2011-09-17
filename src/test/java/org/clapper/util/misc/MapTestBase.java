package org.clapper.util.misc;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base class for map testers.
 *
 */
public abstract class MapTestBase
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    protected MapTestBase()
    {
    }

    /*----------------------------------------------------------------------*\
                               Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Test of clear method.
     */
    @Test public void clear()
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
    @Test public void containsKey()
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
    @Test public void containsValue()
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
    @Test public void entrySet()
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
    @Test public void equals()
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
    @Test public void get()
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
    @Test public void keySet()
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
    @Test public void put()
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
    @Test public void putAll()
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
    @Test public void remove()
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
    @Test public void size()
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
    @Test public void values()
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
    @Test public void isEmpty()
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
