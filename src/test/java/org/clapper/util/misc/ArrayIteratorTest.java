package org.clapper.util.misc;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.NoSuchElementException;
import java.util.Collections;

public class ArrayIteratorTest
{
    private String[] array = new String[]
    {
        "a", "b", "c"
    };

    public ArrayIteratorTest()
    {
    }

    @Test public void hasNext()
    {
        ArrayIterator<String> it = new ArrayIterator<String>(array);
        assertTrue(it.hasNext());
        assertEquals(it.getNextIndex(), 0);

        it.next(); // "a"
        assertTrue(it.hasNext());
        assertEquals(it.getNextIndex(), 1);

        it.next(); // "b"
        assertTrue(it.hasNext());
        assertEquals(it.getNextIndex(), 2);

        it.next(); // "c"
        assertFalse(it.hasNext());
    }

    @Test public void next()
    {
        ArrayIterator<String> it = new ArrayIterator<String>(array);

        String s = it.next(); // "a"
        assertNotNull(s);
        assertEquals("Didn't get expected element from array", s, "a");

        s = it.next(); // "b"
        assertNotNull(s);
        assertEquals("Didn't get expected element from array", s, "b");

        s = it.next(); // "c"
        assertNotNull(s);
        assertEquals("Didn't get expected element from array", s, "c");
    }

    @Test(expected=NoSuchElementException.class)
    public void nextOverflow()
    {
        String[] emptyArray = new String[0];
        new ArrayIterator<String>(emptyArray).next();
    }

    /**
     * Test of previous method, of class org.clapper.util.misc.ArrayIterator.
     */
    @Test public void previous()
    {
        ArrayIterator<String> it = new ArrayIterator<String>(array);

        it.next(); // "a"
        it.next(); // "b"
        it.next(); // "c"

        String s = it.previous();
        assertEquals("Didn't get expected result from previous()", s, "c");
    }
}
