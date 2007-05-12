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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * <p>The <tt>MultiIterator</tt> class provides a way to iterate over
 * multiple <tt>Collection</tt>, <tt>Enumeration</tt> and <tt>Iterator</tt>
 * objects at once. You instantiate a <tt>MultiIterator</tt> object and add
 * one or more <tt>Collection</tt>, <tt>Enumeration</tt> or
 * <tt>Iterator</tt> objects to it; when you use the iterator, it iterates
 * over the contents of each contained composite object, one by one, in the
 * order they were added to the <tt>MultiIterator</tt>. When the iterator
 * reaches the end of one object's contents, it moves on to the next
 * object, until no more composite objects are left.</p>
 *
 * @see java.util.Iterator
 * @see java.util.Enumeration
 * @see java.util.Collection
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class MultiIterator<T> implements Iterator<T>, Iterable<T>
{
    /*----------------------------------------------------------------------*\
                           Private Data Elements
    \*----------------------------------------------------------------------*/

    /**
     * The underlying objects being iterated over, stored in a Collection
     */
    private Collection<Iterator<T>> aggregation = new ArrayList<Iterator<T>>();

    /**
     * The iterator for the list of aggregation
     */
    private Iterator<Iterator<T>> aggregationIterator = null;

    /**
     * The iterator for the current object
     */
    private Iterator<T> it = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>MultiIterator</tt> object.
     */
    public MultiIterator()
    {
        // Nothing to do
    }

    /**
     * Allocate a new <tt>MultiIterator</tt> object that will iterate, in
     * turn, over the contents of each <tt>Collection</tt> in the supplied
     * array.
     *
     * @param array  The <tt>Collection</tt>s over which to iterate
     *
     * @see #addCollection(Collection)
     */
    public MultiIterator (Collection<T> array[])
    {
        for (int i = 0; i < array.length; i++)
            aggregation.add (array[i].iterator());
    }

    /**
     * Allocate a new <tt>MultiIterator</tt> object that will iterate, in
     * turn, over the contents of each <tt>Collection</tt> in the supplied
     * <tt>Collection</tt>
     *
     * @param coll  A <tt>Collection</tt> of <tt>Collection</tt> objects
     *
     * @see #addCollection(Collection)
     */
    public MultiIterator (Collection<Collection<T>> coll)
    {
        for (Iterator<Collection<T>> iterator = coll.iterator();
             iterator.hasNext(); )
        {
            aggregation.add (iterator.next().iterator());
        }
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * <p>Add a <tt>Collection</tt> to the end of the list of composite
     * objects being iterated over. It's safe to call this method while
     * iterating, as long as you haven't reached the end of the last
     * composite object currently in the iterator.</p>
     *
     * <p><b>Note</b>: This method is simply shorthand for:</p>
     *
     * <blockquote>
     * <pre>addIterator (collection.iterator());</pre>
     * </blockquote>
     *
     * @param collection  The <tt>Collection</tt> to add.
     *
     * @see #addIterator
     * @see #addEnumeration
     */
    public void addCollection (Collection<T> collection)
    {
        aggregation.add (collection.iterator());
    }

    /**
     * Add an <tt>Iterator</tt> to the end of the list of composite objects
     * being iterated over. It's safe to call this method while iterating,
     * as long as you haven't reached the end of the last composite object
     * currently in the iterator.
     *
     * @param iterator  The <tt>Iterator</tt> to add.
     *
     * @see #addCollection
     * @see #addEnumeration
     */
    public void addIterator (Iterator<T> iterator)
    {
        aggregation.add (iterator);
    }

    /**
     * Add an <tt>Enumeration</tt> to the end of the list of composite
     * objects being iterated over. It's safe to call this method while
     * iterating, as long as you haven't reached the end of the last
     * composite object currently in the iterator.
     *
     * <p><b>Note</b>: This method is simply shorthand for:</p>
     *
     * <blockquote>
     * <pre>addIterator (new EnumerationIterator<T> (enumeration));</pre>
     * </blockquote>
     *
     * @param enumeration  The <tt>Enumeration</tt> to add.
     *
     * @see #addCollection
     * @see #addIterator
     * @see EnumerationIterator
     */
    public void addEnumeration (Enumeration<T> enumeration)
    {
        aggregation.add (new EnumerationIterator<T> (enumeration));
    }

    /**
     * Determine whether the underlying <tt>Iterator</tt> has more
     * elements.
     *
     * @return <tt>true</tt> if and only if a call to
     *         <tt>nextElement()</tt> will return an element,
     *         <tt>false</tt> otherwise.
     *
     * @see #next
     */
    public boolean hasNext()
    {
        boolean someLeft = false;

        checkIterator();
        if (it != null)
            someLeft = it.hasNext();

        return someLeft;
    }

    /**
     * Returns this iterator. Necessary for the <tt>Iterable</tt> interface.
     *
     * @return this object
     */
    public Iterator<T> iterator()
    {
        return this;
    }

    /**
     * Get the next element from the underlying array.
     *
     * @return the next element from the underlying array
     *
     * @throws NoSuchElementException No more elements exist
     *
     * @see java.util.Iterator#next
     */
    public T next() throws NoSuchElementException
    {
        T result = null;

        checkIterator();
        if (it != null)
            result = it.next();

        return result;
    }

    /**
     * Remove the object most recently extracted from the iterator.
     * The object is removed from whatever underlying <tt>Collection</tt>
     * is currently being traversed.
     */
    public void remove()
    {
        if (it != null)
            it.remove();
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private synchronized void checkIterator()
    {
        if (aggregationIterator == null) 
        {
            it = null;
            aggregationIterator = aggregation.iterator();
            if (aggregationIterator.hasNext())
                it = aggregationIterator.next();
        }

        while ( (it != null) && (! it.hasNext()) )
        {
            if (! aggregationIterator.hasNext())
                it = null;

            else
                it = aggregationIterator.next();
        }
    }
}
