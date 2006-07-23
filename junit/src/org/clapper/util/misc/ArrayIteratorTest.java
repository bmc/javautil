/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2006 Brian M. Clapper."

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

package org.clapper.util.misc;

import junit.framework.*;
import java.util.NoSuchElementException;

/**
 *
 */
public class ArrayIteratorTest extends TestCase
{
    private String[] array = new String[]
    {
        "a", "b", "c"
    };

    public ArrayIteratorTest(String testName)
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
     * Test of hasNext method, of class org.clapper.util.misc.ArrayIterator.
     */
    public void testHasNext()
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

    /**
     * Test of next method, of class org.clapper.util.misc.ArrayIterator.
     */
    public void testNext()
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

        try
        {
            s = it.next();
            fail("Attempt to overflow iterator should throw exception");
        }

        catch (NoSuchElementException ex)
        {
            // Okay
        }
    }

    /**
     * Test of previous method, of class org.clapper.util.misc.ArrayIterator.
     */
    public void testPrevious()
    {
        ArrayIterator<String> it = new ArrayIterator<String>(array);

        it.next(); // "a"
        it.next(); // "b"
        it.next(); // "c"

        String s = it.previous();
        assertEquals("Didn't get expected result from previous()", s, "c");
    }
}
