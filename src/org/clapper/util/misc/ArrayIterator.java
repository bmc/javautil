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
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
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
            if (array == null)
                throw new NoSuchElementException(); // NOPMD

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
