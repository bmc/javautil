package org.clapper.util.html;

import org.junit.*;
import static org.junit.Assert.*;

import org.clapper.util.text.XStringBuilder;

public class HTMLEntitiesTest
{
    public HTMLEntitiesTest()
    {
    }

    @Test public void convertHTMLEntities()
    {
        class TestData
        {
            String before;
            String after;

            TestData(String before, String after)
            {
                this.before = before;
                this.after = after;
            }
        }

        TestData[] testData = new TestData[]
        {
            new TestData("&#8482;", "\u2122"),
            new TestData("&#8482", "&#8482"),
            new TestData("&#x2122;", "\u2122"),
            new TestData("&#x2122", "&#x2122"),
            new TestData("&#x7F;", "\u007f")
        };

        XStringBuilder bufAfter = new XStringBuilder();
        XStringBuilder bufExpected = new XStringBuilder();
        for (int i = 0; i < testData.length; i++)
        {
            String after = HTMLUtil.convertCharacterEntities(testData[i].before);
            bufAfter.reset(after);
            bufAfter.encodeMetacharacters();
            bufExpected.reset(testData[i].after);
            bufExpected.encodeMetacharacters();
            assertEquals(testData[i].before + " converts to \"" +
                         bufAfter.toString() +
                         "\", instead of the expected value of \"" +
                         bufExpected.toString() + "\"",
                         testData[i].after, after);
        }
    }

    @Test public void makeCharacterEntities()
    {
        class TestData
        {
            String before;
            String after;

            TestData(String before, String after)
            {
                this.before = before;
                this.after = after;
            }
        }

        TestData[] testData = new TestData[]
        {
            new TestData("\u00a0", "&nbsp;"),
            new TestData("\u00b9", "&sup1;"),
            new TestData("\u00cb", "&Euml;"),
            new TestData("\u2288", "&#8840;"),
            new TestData("\u00c8", "&Egrave;"),
            new TestData("\u2264", "&le;")
        };

        XStringBuilder bufBefore = new XStringBuilder();
        XStringBuilder bufExpected = new XStringBuilder();
        XStringBuilder bufActual = new XStringBuilder();
        for (int i = 0; i < testData.length; i++)
        {
            String after = HTMLUtil.makeCharacterEntities(testData[i].before);
            bufActual.reset(after);
            bufActual.encodeMetacharacters();
            bufExpected.reset(testData[i].after);
            bufExpected.encodeMetacharacters();
            bufBefore.reset(testData[i].before);
            bufBefore.encodeMetacharacters();
            assertEquals(bufBefore.toString() + " converts to \"" +
                         bufActual.toString() +
                         "\", instead of the expected value of \"" +
                         bufExpected.toString() + "\"",
                         testData[i].after, after);
        }
    }
}
