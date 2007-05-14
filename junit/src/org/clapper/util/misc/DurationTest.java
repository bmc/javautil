/*---------------------------------------------------------------------------*\
  $Id: DurationTest.java 6735 2007-05-12 11:30:05Z bmc $
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
      without prior written permission of Brian M. Clapper.

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

package org.clapper.util.misc;

import junit.framework.*;

/**
 *
 */
public class DurationTest extends TestCase
{
    public DurationTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    /**
     * Test of parse() method
     */
    public void testParse() throws Exception // NOPMD
    {
        parseOne("5 weeks, 30 hours, 30100 milliseconds", 3132030100L);
        parseOne("1001 hours, 1100 ms", 3603601100L);
        parseOne("1 day", 86400000L);
        parseOne("1 Day", 86400000L);
    }

    /**
     * Test format() method
     */
    public void testFormat() throws Exception // NOPMD
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
