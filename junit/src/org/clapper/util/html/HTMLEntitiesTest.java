/*---------------------------------------------------------------------------*\
  $Id: TextUtilRomanNumberTest.java 6511 2006-10-12 00:43:47Z bmc $
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

package org.clapper.util.html;

import junit.framework.*;
import org.clapper.util.text.XStringBuilder;

/**
 *
 */
public class HTMLEntitiesTest extends TestCase
{
    public HTMLEntitiesTest(String testName)
    {
        super(testName);
    }

    public void testConvertHTMLEntities()
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

    public void testMakeCharacterEntities()
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
