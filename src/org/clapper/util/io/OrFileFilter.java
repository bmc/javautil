/*---------------------------------------------------------------------------*\
  $Id: OrFileFilter.java 5812 2006-05-12 00:38:16Z bmc $
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

import java.io.FileFilter;
import java.io.File;

/**
 * <p>An <tt>OrFileFilter</tt> logically ORs other <tt>java.io.FileFilter</tt>
 * objects. When its {@link #accept accept()} method is called, the
 * <tt>OrFileFilter</tt> object passes the file through the contained
 * filters. The file is accepted if it is accepted by any of the contained
 * filters. This class conceptually provides a logical "OR" operator for
 * file filters.</p>
 *
 * <p>The contained filters are applied in the order they were added to the
 * <tt>OrFileFilter</tt> object. This class's {@link #accept accept()}
 * method stops looping over the contained filters as soon as it encounters
 * one whose <tt>accept()</tt> method returns <tt>true</tt> (implementing
 * a "short-circuited OR" operation.) </p>
 *
 * @see FileFilter
 * @see AndFileFilter
 * @see NotFileFilter
 * @see OrFilenameFilter
 * @see RegexFileFilter
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class OrFileFilter implements FileFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private List<FileFilter> filters = new LinkedList<FileFilter>();

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>OrFileFilter</tt> with no contained filters.
     */
    public OrFileFilter()
    {
    }

    /**
     * Construct a new <tt>OrFileFilter</tt> with two contained filters.
     * Additional filters may be added later, via calls to the
     * {@link #addFilter addFilter()} method.
     *
     * @param filters  filters to add
     */
    public OrFileFilter (FileFilter... filters)
    {
        for (FileFilter filter : filters)
            addFilter (filter);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add a filter to the set of contained filters.
     *
     * @param filter the <tt>FileFilter</tt> to add.
     *
     * @return this object, to permit chained calls.
     *
     * @see #removeFilter
     */
    public OrFileFilter addFilter (FileFilter filter)
    {
        filters.add (filter);
        return this;
    }

    /**
     * Remove a filter from the set of contained filters.
     *
     * @param filter the <tt>FileFilter</tt> to remove.
     *
     * @see #addFilter
     */
    public void removeFilter (FileFilter filter)
    {
        filters.remove (filter);
    }

    /**
     * Determine whether a file is to be accepted or not, based on the
     * contained filters. The file is accepted if any one of the contained
     * filters accepts it. This method stops looping over the contained
     * filters as soon as it encounters one whose <tt>accept()</tt> method
     * returns <tt>false</tt> (implementing a "short-circuited AND"
     * operation.)</p>
     *
     * <p>If the set of contained filters is empty, then this method
     * returns <tt>true</tt>.</p>
     *
     * @param file  The file to check for acceptance
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File file)
    {
        boolean accepted = false;

        if (filters.size() == 0)
            accepted = true;

        else
        {
            for (FileFilter filter : filters)
            {
                accepted = filter.accept (file);
                if (accepted)
                    break;
            }
        }

        return accepted;
    }
}
