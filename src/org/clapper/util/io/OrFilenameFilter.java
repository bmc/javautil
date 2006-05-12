/*---------------------------------------------------------------------------*\
  $Id: OrFilenameFilter.java 5812 2006-05-12 00:38:16Z bmc $
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import java.io.FilenameFilter;
import java.io.File;

/**
 * <p>A <tt>OrFilenameFilter</tt> contains one or more <tt>java.io.FilenameFilter</tt>
 * objects. When its {@link #accept accept()} method is called, the
 * <tt>OrFilenameFilter</tt> object passes the file through the contained
 * filters. The file is accepted if it is accepted by any of the contained
 * filters. This class conceptually provides a logical "OR" operator for
 * file filters.</p>
 *
 * <p>The contained filters are applied in the order they were added to the
 * <tt>OrFilenameFilter</tt> object. This class's {@link #accept accept()}
 * method stops looping over the contained filters as soon as it encounters
 * one whose <tt>accept()</tt> method returns <tt>true</tt> (implementing
 * a "short-circuited OR" operation.) </p>
 *
 * @see FilenameFilter
 * @see AndFilenameFilter
 * @see NotFilenameFilter
 * @see OrFilenameFilter
 * @see RegexFilenameFilter
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class OrFilenameFilter implements FilenameFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private List<FilenameFilter> filters = new LinkedList<FilenameFilter>();

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>OrFilenameFilter</tt> with no contained filters.
     */
    public OrFilenameFilter()
    {
    }

    /**
     * Construct a new <tt>OrFilenameFilter</tt> with two contained filters.
     * Additional filters may be added later, via calls to the
     * {@link #addFilter addFilter()} method.
     *
     * @param filter1  first filter to add
     * @param filter2  second filter to add
     */
    public OrFilenameFilter (FilenameFilter filter1, FilenameFilter filter2)
    {
        addFilter (filter1);
        addFilter (filter2);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add a filter to the set of contained filters.
     *
     * @param filter the <tt>FilenameFilter</tt> to add.
     *
     * @return this object, to permit chained calls.
     *
     * @see #removeFilter
     */
    public OrFilenameFilter addFilter (FilenameFilter filter)
    {
        filters.add (filter);
        return this;
    }

    /**
     * Remove a filter from the set of contained filters.
     *
     * @param filter the <tt>FilenameFilter</tt> to remove.
     *
     * @see #addFilter
     */
    public void removeFilter (FilenameFilter filter)
    {
        filters.remove (filter);
    }

    /**
     * Determine whether a file is to be accepted or not, based on the
     * contained filters. The file is accepted if any one of the contained
     * filters accepts it. This method stops looping over the contained
     * filters as soon as it encounters one whose <tt>accept()</tt> method
     * returns <tt>true</tt> (implementing a "short-circuited OR"
     * operation.)</p>
     *
     * <p>If the set of contained filters is empty, then this method
     * returns <tt>true</tt>.</p>
     *
     * @param dir   The directory containing the file.
     * @param name  the file name
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File dir, String name)
    {
        boolean accepted = false;

        if (filters.size() == 0)
            accepted = true;

        else
        {
            for (FilenameFilter filter : filters)
            {
                accepted = filter.accept (dir, name);
                if (accepted)
                    break;
            }
        }

        return accepted;
    }
}
