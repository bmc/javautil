/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms are permitted provided
  that: (1) source distributions retain this entire copyright notice and
  comment; and (2) modifications made to the software are prominently
  mentioned, and a copy of the original software (or a pointer to its
  location) are included. The name of the author may not be used to endorse
  or promote products derived from this software without specific prior
  written permission.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
  WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.

  Effectively, this means you can do what you want with the software except
  remove this notice or take advantage of the author's name. If you modify
  the software and redistribute your modified version, you must indicate that
  your version is a modification of the original, and you must provide either
  a pointer to or a copy of the original.
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
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
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
        checkIterator();
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
