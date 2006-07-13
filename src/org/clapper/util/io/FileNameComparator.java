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

package org.clapper.util.io;

import java.io.File;
import java.util.Comparator;

/**
 * <p><tt>FileNameComparator</tt> implements a <tt>Comparator</tt> class
 * that compares file names (expressed either as <tt>String</tt> or
 * <tt>File</tt> objects) for sorting. The comparison can be case-sensitive
 * or case-insensitive, and can apply to the entire path (if available) or
 * just the file name part.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class FileNameComparator implements Comparator<Object>
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private boolean foldCase   = false;
    private boolean entirePath = false;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>FileNameComparator</tt> with the default settings.
     * The default settings are:
     *
     * <ul>
     *   <li> Compare only file names, not paths
     *   <li> Use case-sensitive comparison
     * </ul>
     *
     * Calling this constructor is equivalent to:
     *
     * <pre>Comparator cmp = new FileNameComparator (false, false);</pre>
     */
    public FileNameComparator()
    {
        this (false, false);
    }

    /**
     * Construct a new <tt>FileNameComparator</tt>.
     *
     * @param foldCase   <tt>true</tt> for case-insensitive comparison,
     *                   <tt>false</tt> for case-sensitive comparison
     * @param entirePath <tt>true</tt> to compare the entire path (where
     *                   available, <tt>false</tt> to use just the name.
     */
    public FileNameComparator (boolean foldCase,
                               boolean entirePath)
    {
        this.foldCase = foldCase;
        this.entirePath = entirePath;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Compares its two arguments for order. Returns a negative integer,
     * zero, or a positive integer as the first argument is less than,
     * equal to, or greater than the second.
     *
     * @param o1  the first object to be compared
     * @param o2  the second object to be compared
     *
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare (Object o1, Object o2)
    {
        String s1  = getFileName (o1);
        String s2  = getFileName (o2);
        int    cmp = 0;

        if (foldCase)
            cmp = s1.compareToIgnoreCase (s2);
        else
            cmp = s1.compareTo (s2);

        return cmp;
    }

    /**
     * <p>Indicates whether some other object is "equal to" this
     * <tt>Comparator</tt>.</p>
     *
     * @param o  the object to compare
     *
     * @return <tt>true</tt> only if the pecified object is also a comparator
     *         and it imposes the same ordering as this comparator.
     */
    public boolean equals (Object o)
    {
        boolean eq = false;

        if (o instanceof FileNameComparator)
        {
            FileNameComparator other = (FileNameComparator) o;

            eq = (other.foldCase == this.foldCase) &&
                  (other.entirePath == this.entirePath);
        }

        return eq;
    }

    /**
     * Get the hash code for this object.
     *
     * @return the hash code
     */
    public int hashCode()                                             // NOPMD
    {
        return super.hashCode();
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private String getFileName (Object o)
    {
        String name = null;

        if (o instanceof File)
            name = ((File) o).getPath();

        else
            name = (String) o;

        if (! entirePath)
            name = FileUtil.basename (name);

        return name;
    }
}
