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

package org.clapper.util.io;

/**
 * <p>Used solely to define type-safe mode values for
 * {@link CombinationFilenameFilter} and {@link CombinationFileFilter}.
 *
 * @see CombinationFileFilter
 * @see CombinationFilenameFilter
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public final class CombinationFilterMode
{
    /*----------------------------------------------------------------------*\
                               Instance Data
    \*----------------------------------------------------------------------*/

    /**
     * The actual value, package visible.
     */
    final int value;

    /*----------------------------------------------------------------------*\
                                Constructor1
    \*----------------------------------------------------------------------*/

    /**
     * One constructor, again, package-visible.
     */
    CombinationFilterMode (int value)
    {
        this.value = value;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether this object equals another one.
     *
     * @param other the other object
     *
     * @return <tt>true</tt> if they're equal, <tt>false</tt> if not.
     */
    public boolean equals (Object other)
    {
        boolean eq = false;

        if (other instanceof CombinationFilterMode)
            eq = this.value == ((CombinationFilterMode) other).value;

        return eq;
    }

    /**
     * Get the hash code for this object.
     *
     * @return the hash code
     */
    public int hashCode()
    {
        return value;
    }
}
