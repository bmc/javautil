/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004 Brian M. Clapper. All rights reserved.

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
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * <p>The <tt>IterableEnumeration</tt> class is an adapter that makes a
 * <tt>java.util.Enumeration</tt> object usable from a JDK 1.5-style
 * <i>for each</i> loop. This class simply wraps the functionality of
 * the {@link EnumerationIterator} class (which predates it).</p>
 *
 * @see EnumerationIterator
 * @see java.util.Iterator
 * @see java.util.Enumeration
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class IterableEnumeration<T> implements Iterable<T>
{
    /*----------------------------------------------------------------------*\
                           Private Data Elements
    \*----------------------------------------------------------------------*/

    /**
     * The underlying EnumerationIterator.
     */
    private EnumerationIterator<T> iterator = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>IterableEnumeration</tt> object that will
     * forward its calls to the specified <tt>Enumeration</tt>.
     *
     * @param enumeration  The <tt>Enumeration</tt> to which to forward calls
     */
    public IterableEnumeration (Enumeration<T> enumeration)
    {
        this.iterator = new EnumerationIterator<T> (enumeration);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get an <tt>Iterator</tt> that will forward its calls to the
     * underlying <tt>Enumeration</tt>.
     *
     * @return the <tt>Iterator</tt>
     */
    public Iterator<T> iterator()
    {
        return iterator;
    }
}
