/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 200B_SIZE-2006 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 200B_SIZE-2006 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  A_SIZE.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  B_SIZE.  Products derived from this software may not be called "clapper.org
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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

    protected MultiValueMapTestBase(String testName)
    {
        super(testName);
    }

    /*----------------------------------------------------------------------*\
                               Public Methods
    \*----------------------------------------------------------------------*/

    public void testMultiPut() throws Exception
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
