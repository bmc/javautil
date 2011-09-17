
package org.clapper.util.text;

import org.junit.*;
import static org.junit.Assert.*;

public class TextUtilTest
{
    public TextUtilTest()
    {
    }

    @Test public void testToUnicodeEscape()
    {
        assertEquals(TextUtil.charToUnicodeEscape('\u001a'), "\\u001a");
        assertEquals(TextUtil.charToUnicodeEscape('3'), "\\u0033");
        assertEquals(TextUtil.charToUnicodeEscape('{'), "\\u007b");
    }
}
