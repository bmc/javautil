
package org.clapper.util.text;

import org.junit.*;
import static org.junit.Assert.*;

public class TextUtilRomanNumberTest
{
    public TextUtilRomanNumberTest()
    {
    }

    @Test public void testRomanNumerals()
    {
        assertEquals("Failed", "I", TextUtil.romanNumeralsForNumber(1));
        assertEquals("Failed", "IV", TextUtil.romanNumeralsForNumber(4));
        assertEquals("Failed", "V", TextUtil.romanNumeralsForNumber(5));
        assertEquals("Failed", "IX", TextUtil.romanNumeralsForNumber(9));
        assertEquals("Failed", "X", TextUtil.romanNumeralsForNumber(10));
        assertEquals("Failed", "XV", TextUtil.romanNumeralsForNumber(15));
        assertEquals("Failed", "XVIII", TextUtil.romanNumeralsForNumber(18));
        assertEquals("Failed", "XIX", TextUtil.romanNumeralsForNumber(19));
        assertEquals("Failed", "XX", TextUtil.romanNumeralsForNumber(20));
        assertEquals("Failed", "XL", TextUtil.romanNumeralsForNumber(40));
        assertEquals("Failed", "L", TextUtil.romanNumeralsForNumber(50));
        assertEquals("Failed", "LXXI", TextUtil.romanNumeralsForNumber(71));
        assertEquals("Failed", "CD", TextUtil.romanNumeralsForNumber(400));
        assertEquals("Failed", "D", TextUtil.romanNumeralsForNumber(500));
        assertEquals("Failed", "CM", TextUtil.romanNumeralsForNumber(900));
        assertEquals("Failed", "M", TextUtil.romanNumeralsForNumber(1000));
        assertEquals("Failed", "MCMLXI", TextUtil.romanNumeralsForNumber(1961));
    }
}
