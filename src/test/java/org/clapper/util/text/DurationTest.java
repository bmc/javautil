package org.clapper.util.text;

import org.clapper.util.text.Duration;
import org.junit.*;
import static org.junit.Assert.*;

public class DurationTest
{
    public DurationTest()
    {
    }

    /**
     * Test of parse() method
     */
    @Test public void parse() throws Exception
    {
        parseOne("5 weeks, 30 hours, 30100 milliseconds", 3132030100L);
        parseOne("1001 hours, 1100 ms", 3603601100L);
        parseOne("1 day", 86400000L);
        parseOne("1 Day", 86400000L);
    }

    /**
     * Test format() method
     */
    @Test public void format() throws Exception
    {
        formatOne(1, "1 millisecond");
        formatOne(1000, "1 second");
        formatOne(1001, "1 second, 1 millisecond");
        formatOne(86401001, "1 day, 1 second, 1 millisecond");
        formatOne(864001001, "10 days, 1 second, 1 millisecond");
    }

    private void parseOne(String s, long expected) throws Exception
    {
        Duration d = new Duration(s);
        assertEquals("Parse of \"" + s + "\" did not produce correct result",
                     expected, d.getDuration());
    }

    private void formatOne(long ms, String expected) throws Exception
    {
        Duration d = new Duration(ms);
        assertEquals("Format of " + expected + " did not produce expected " +
                     "result.", expected, d.format());
    }
}
