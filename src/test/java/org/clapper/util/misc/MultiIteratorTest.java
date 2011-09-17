/*---------------------------------------------------------------------------*\
  $Id$
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

import java.util.Collections;
import java.util.HashSet;
import junit.framework.*;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 */
public class MultiIteratorTest extends TestCase
{
    public MultiIteratorTest(String testName)
    {
        super(testName);
    }

    /**
     * Test of addCollection method, of class org.clapper.util.misc.MultiIterator.
     */
    public void testAddCollection()
    {
        Set<String> set = makeTestSet();
        MultiIterator<String> it = new MultiIterator<String>();
        it.addCollection(set);
        while (it.hasNext())
        {
            String item = it.next();
            assertTrue("Iterator returned value not in collection",
                       set.contains(item));
        }
    }

    /**
     * Test of addIterator method, of class org.clapper.util.misc.MultiIterator.
     */
    public void testAddIterator()
    {
        Set<String> set = makeTestSet();
        MultiIterator<String> it = new MultiIterator<String>();
        it.addIterator(set.iterator());
        while (it.hasNext())
        {
            String item = it.next();
            assertTrue("Iterator returned value not in collection",
                       set.contains(item));
        }
    }

    /**
     * Test of addEnumeration method, of class org.clapper.util.misc.MultiIterator.
     */
    public void testAddEnumeration()
    {
        Set<String> set = makeTestSet();
        MultiIterator<String> it = new MultiIterator<String>();
        it.addEnumeration(Collections.enumeration(set));
        while (it.hasNext())
        {
            String item = it.next();
            assertTrue("Iterator returned value not in collection",
                       set.contains(item));
        }
    }

    /**
     * Test of hasNext method, of class org.clapper.util.misc.MultiIterator.
     */
    public void testHasNext()
    {
        Set<String> set = makeTestSet();
        MultiIterator<String> it = new MultiIterator<String>();
        it.addCollection(set);
        assertTrue("hasNext() doesn't work", it.hasNext());
    }

    /**
     * Test of next method, of class org.clapper.util.misc.MultiIterator.
     */
    public void testNext()
    {
        Set<String> set = makeTestSet();
        MultiIterator<String> it = new MultiIterator<String>();
        it.addCollection(set);
        Set<String> copy = new HashSet<String>(set);
        while (it.hasNext())
        {
            String item = it.next();
            assertTrue("Iterator returned value not in collection",
                       set.contains(item));
            copy.remove(item);
        }

        assertTrue("Iterator didn't return all items", copy.isEmpty());
    }

    /**
     * Test iterating across multiples
     */
    public void testMulti()
    {
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        Set<String> combined = new HashSet<String>();
        MultiIterator<String> it = setUpTwo(set1, set2, combined);

        while (it.hasNext())
        {
            String item = it.next();
            assertTrue("Iterator returned value not in collection",
                       combined.contains(item));
            combined.remove(item);
        }

        assertTrue("Iterator didn't return all items", combined.isEmpty());
    }

    /**
     * Test of remove method, of class org.clapper.util.misc.MultiIterator.
     */
    public void testRemove()
    {
        Set<String> set1 = new TreeSet<String>();
        Set<String> set2 = new TreeSet<String>();
        MultiIterator<String> it = setUpTwo(set1, set2, null);

        while (it.hasNext())
        {
            System.out.println("Removing " + it.next());
            it.remove();
        }

        assertTrue("After removal, set1 not empty", set1.isEmpty());
        assertTrue("After removal, set2 not empty", set2.isEmpty());
    }

    private Set<String> makeTestSet()
    {
        Set<String> set = new HashSet<String>();
        set.add("a");
        set.add("b");
        set.add("c");
        set.add("d");
        set.add("e");
        set.add("f");

        return set;
    }

    private MultiIterator<String> setUpTwo(Set<String> set1,
                                           Set<String> set2,
                                           Set<String> combined)
    {
        set1.add("a");
        set1.add("b");
        set1.add("c");
        set1.add("d");

        set2.add("e");
        set2.add("f");
        set2.add("g");
        set2.add("h");

        if (combined != null)
        {
            combined.addAll(set1);
            combined.addAll(set2);
        }

        MultiIterator<String> it = new MultiIterator<String>();
        it.addCollection(set1);
        it.addCollection(set2);

        return it;
    }
}
