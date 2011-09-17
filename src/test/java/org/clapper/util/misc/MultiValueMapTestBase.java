package org.clapper.util.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 */
public abstract class MultiValueMapTestBase extends MapTestBase
{
    /*----------------------------------------------------------------------*\
                                 Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    protected MultiValueMapTestBase()
    {
    }

    /*----------------------------------------------------------------------*\
                               Public Methods
    \*----------------------------------------------------------------------*/

    @Test public void multiPut() throws Exception
    {
        final int A_SIZE = 3;
        final int B_SIZE = 4;

        MultiValueMap<String,String> map = newMultiValueMap();
        putValuesForKey(map, "a", A_SIZE);
        putValuesForKey(map, "b", B_SIZE);
        assertEquals("size() failed", 7, map.size());
        Collection<String> values = map.getCollection("a");
        assertNotNull("No collection for key \"a\"", values);
        assertEquals("Wrong size for values collection for \"a\"",
                     A_SIZE, values.size());
        values = map.getCollection("b");
        assertNotNull("No collection for key \"b\"", values);
        assertEquals("Wrong size for values collection for \"b\"",
                     B_SIZE, values.size());
        assertNull("Found collection for nonexistent key",
                   map.getCollection("c"));
        map.remove("b");
        assertEquals("After removing \"b\" values, map is wrong size",
                     A_SIZE, map.size());
        values = map.getCollection("b");
        assertNull("After removing \"b\" values, getCollection() non-null",
                   values);
    }

    /*----------------------------------------------------------------------*\
                               Protected Methods
    \*----------------------------------------------------------------------*/

    protected abstract MultiValueMap<String,String> newMultiValueMap();

    protected final Map<String,String> newMap()
    {
        return newMultiValueMap();
    }

    /*----------------------------------------------------------------------*\
                               Private Methods
    \*----------------------------------------------------------------------*/

    private void putValuesForKey(MultiValueMap<String,String> map,
                                 String                       key,
                                 int                          n)
        throws Exception
    {
        int i;
        int origSize = map.size();

        for (i = 0; i < n; i++)
            map.put(key, String.valueOf(i));

        i = 0;
        for (Iterator<String> it = map.getValuesForKey(key).iterator();
             it.hasNext(); )
        {
            it.next();
            i++;
        }

        assertEquals("Wrong number of values found", n, i);
        assertEquals("Size increase wrong", origSize + n, map.size());
    }
}
