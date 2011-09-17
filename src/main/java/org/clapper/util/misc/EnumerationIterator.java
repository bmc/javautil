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
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * <p>The <tt>EnumerationIterator</tt> class is an adapter that makes a
 * <tt>java.util.Enumeration</tt> object look and behave like a
 * <tt>java.util.Iterator</tt> objects. The <tt>EnumerationIterator</tt>
 * class implements the <tt>Iterator</tt> interface and wraps an existing
 * <tt>Enumeration</tt> object. This class is the conceptual opposite of
 * the <tt>Collections.enumeration()</tt> method in the <tt>java.util</tt>
 * package.</p>
 *
 * <p>You can also use an instance of this class to wrap an
 * <tt>Enumeration</tt> for use in a JDK 1.5-style <i>for each</i> loop.
 * For instance:</p>
 *
 * <blockquote><pre>
 * Vector<String> v = ...
 * for (String s : new EnumerationIterator<String> (v.elements()))
 *     ...
 * </pre></blockquote>
 *
 * @see java.util.Iterator
 * @see java.util.Enumeration
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class EnumerationIterator<T> implements Iterator<T>, Iterable<T>
{
    /*----------------------------------------------------------------------*\
                           Private Data Elements
    \*----------------------------------------------------------------------*/

    /**
     * The underlying Enumeration.
     */
    private Enumeration<T> enumeration = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>EnumerationIterator</tt> object that will
     * forward its calls to the specified <tt>Enumeration</tt>.
     *
     * @param enumeration  The <tt>Enumeration</tt> to which to forward calls
     */
    public EnumerationIterator (Enumeration<T> enumeration)
    {
        this.enumeration = enumeration;
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
        return enumeration.hasMoreElements();
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
     * Get the next element from the underlying <tt>Enumeration</tt>.
     *
     * @return the next element from the underlying <tt>Enumeration</tt>
     *
     * @exception NoSuchElementException No more elements exist
     *
     * @see Iterator#next
     */
    public T next() throws NoSuchElementException
    {
        return enumeration.nextElement();
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
