/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The <tt>EnumerationIterator</tt> class is an adapter that makes a
 * <tt>java.util.Enumeration</tt> object look and behave like a
 * <tt>java.util.Iterator</tt> objects. The <tt>EnumerationIterator</tt>
 * class implements the <tt>Iterator</tt> interface and wraps an existing
 * <tt>Enumeration</tt> object.
 *
 * @see java.util.Iterator
 * @see java.util.Enumeration
 * @see IteratorEnumeration
 *
 * @version <tt>$Revision$</tt>
 */
public class EnumerationIterator implements Iterator
{
    /*----------------------------------------------------------------------*\
                           Private Data Elements
    \*----------------------------------------------------------------------*/

    /**
     * The underlying Enumeration.
     */
    private Enumeration enum = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>EnumerationIterator</tt> object that will
     * forward its calls to the specified <tt>Enumeration</tt>.
     *
     * @param enum  The <tt>Enumeration</tt> to which to forward calls
     */
    public EnumerationIterator (Enumeration enum)
    {
        this.enum = enum;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether the underlying <tt>Enumeration</tt> has more
     * elements.
     *
     * @return <tt>true</tt> if and only if a call to
     *         <tt>next()</tt> will return an element,
     *         <tt>false</tt> otherwise.
     *
     * @see #next()
     * @see Enumeration#hasMoreElements
     */
    public boolean hasNext()
    {
        return enum.hasMoreElements();
    }

    /**
     * Get the next element from the underlying <tt>Enumeration</tt>.
     *
     * @return the next element from the underlying <tt>Enumeration</tt>
     *
     * @exception NoSuchElementException No more elements exist
     *
     * @see Iterator#next
     */
    public Object next() throws NoSuchElementException
    {
        return enum.nextElement();
    }

    /**
     * Removes from the underlying collection the last element returned by
     * the iterator. Not supported by this class.
     *
     * @throws IllegalStateException         doesn't
     * @throws UnsupportedOperationException unconditionally
     */
    public void remove()
        throws IllegalStateException,
               UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
}
