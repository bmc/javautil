/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.io;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;

import java.io.FilenameFilter;
import java.io.File;

/**
 * <p>Used solely to define type-safe mode values for
 * {@link CombinationFilenameFilter} and {@link CombinationFileFilter}.
 *
 * @see CombinationFileFilter
 * @see CombinationFilenameFilter
 *
 * @version <tt>$Revision$</tt>
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
}
