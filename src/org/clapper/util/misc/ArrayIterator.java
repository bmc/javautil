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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The <tt>ArrayIterator</tt> class provides a bridge between an array of
 * objects and an <tt>Iterator</tt>. It's useful in cases where you have an
 * array, but you need an <tt>Iterator</tt>; using an instance of
 * <tt>ArrayIterator</tt> saves copying the array's contents into a
 * <tt>Collection</tt>, just to get an <tt>Iterator</tt>.
 *
 * @see java.util.Iterator
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class ArrayIterator<T> implements Iterator<T>
{
    /*----------------------------------------------------------------------*\
                           Private Data Elements
    \*----------------------------------------------------------------------*/

    /**
     * The underlying Array.
     */
    private T array[] = null;

    /**
     * The next array index.
     */
    private int nextIndex = 0;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>ArrayIterator</tt> object that will
     * iterate over the specified array of objects.
     *
     * @param array  The array over which to iterate
     */
    public ArrayIterator (T array[])
    {
        this.array = array;
    }

    /**
     * Allocate a new <tt>ArrayIterator</tt> object that will iterate
     * over the specified array of objects, starting at a particular index.
     * The index isn't checked for validity until <tt>next()</tt> is called.
     *
     * @param array  The array over which to iterate
     * @param index  The index at which to start
     */
    public ArrayIterator (T array[], int index)
    {
        this.array = array;
        this.nextIndex  = index;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the index of the next element to be retrieved. This index value
     * might be past the end of the array.
     *
     * @return the index
     */
    public int getNextIndex()
    {
        return nextIndex;
    }

    /**
     * Determine whether the underlying <tt>Iterator</tt> has more
     * elements.
     *
     * @return <tt>true</tt> if and only if a call to
     *         <tt>next()</tt> will return an element,
     *         <tt>false</tt> otherwise.
     *
     * @see #next
     */
    public boolean hasNext()
    {
        return (array != null) && (nextIndex < array.length);
    }

    /**
     * Get the next element from the underlying array.
     *
     * @return the next element from the underlying array
     *
     * @exception java.util.NoSuchElementException
     *            No more elements exist
     *
     * @see #previous()
     * @see java.util.Iterator#next
     */
    public T next() throws NoSuchElementException
    {
        T result = null;

        try
        {
            result = array[nextIndex++];
        }

        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new NoSuchElementException(); // NOPMD
        }

        return result;
    }

    /**
     * Get the previous element from the underlying array. This method
     * decrements the iterator's internal index by one, and returns the
     * corresponding element.
     *
     * @return the previous element from the underlying array
     *
     * @exception java.util.NoSuchElementException
     *            Attempt to move internal index before the first array element
     *
     * @see #next()
     */
    public T previous() throws NoSuchElementException
    {
        T result = null;

        try
        {
            result = array[--nextIndex];
        }

        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new NoSuchElementException();  // NOPMD
        }

        return result;
    }

    /**
     * Required by the <tt>Iterator</tt> interface, but not supported by
     * this class.
     *
     * @throws UnsupportedOperationException  unconditionally
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
