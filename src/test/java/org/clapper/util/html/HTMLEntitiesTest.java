package org.clapper.util.html;

import org.junit.*;
import static org.junit.Assert.*;

import org.clapper.util.text.XStringBuilder;

public class HTMLEntitiesTest
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

    public HTMLEntitiesTest()
    {
    }

    @Test public void convertHTMLEntities()
    {
        TestData[] testData = new TestData[]
        {
            new TestData("&#8482;", "\u2122"),
            new TestData("&#8482", "&#8482"),
            new TestData("&#x2122;", "\u2122"),
            new TestData("&#x2122", "&#x2122"),
            new TestData("&#x7F;", "\u007f"),
            new TestData("&foobar&nbsp;baz", "&foobar\u00a0baz"),
            new TestData("&foobar;&nbsp;baz", "&foobar;\u00a0baz"),
            new TestData("&foobar;&nbsp baz", "&foobar;&nbsp baz")
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

    @Test public void textFromHTML()
    {
        TestData[] testData = new TestData[]
        {
            new TestData("&foobar&nbsp;baz", "&foobar baz"),
            new TestData("&foobar;&nbsp;baz", "&foobar; baz"),
            new TestData("&foobar;&nbsp baz", "&foobar;&nbsp baz")
        };

        XStringBuilder bufBefore = new XStringBuilder();
        XStringBuilder bufExpected = new XStringBuilder();
        XStringBuilder bufActual = new XStringBuilder();
        for (TestData data : testData)
        {
            String after = HTMLUtil.textFromHTML(data.before);
            bufActual.reset(after);
            bufActual.encodeMetacharacters();
            bufExpected.reset(data.after);
            bufExpected.encodeMetacharacters();
            bufBefore.reset(data.before);
            bufBefore.encodeMetacharacters();
            assertEquals(bufBefore.toString() + " converts to \"" +
                         bufActual.toString() +
                         "\", instead of the expected value of \"" +
                         bufExpected.toString() + "\"",
                         data.after, after);
        }
    }

    @Test public void makeCharacterEntities()
    {
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
