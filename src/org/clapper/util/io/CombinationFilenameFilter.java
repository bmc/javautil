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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import java.io.FilenameFilter;
import java.io.File;

/**
 * <p>A <tt>CombinationFilenameFilter</tt> contains one or more
 * <tt>java.io.FilenameFilter</tt> objects. When its {@link #accept accept()} 
 * method is called, the <tt>CombinationFilenameFilter</tt> object passes
 * the file through the contained filters. If the
 * <tt>CombinationFilenameFilter</tt> object's mode is set to
 * {@link #AND_FILTERS}, then a file must be accepted by all contained
 * filters to be accepted. If the <tt>CombinationFilenameFilter</tt>
 * object's mode is set to {@link #OR_FILTERS}, then a file name is
 * accepted if any one of the contained filters accepts it. The default
 * mode is <tt>AND_FILTERS</tt>.</p>
 *
 * <p>The contained filters are applied in the order they were added to
 * the <tt>CombinationFilenameFilter</tt> object.</p>
 *
 * @see FilenameFilter
 * @see CombinationFileFilter
 * @see MultipleRegexFilenameFilter
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class CombinationFilenameFilter implements FilenameFilter
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * Mode setting that instructs the filter to <tt>AND</tt> all the
     * contained filters.
     */
    public static final CombinationFilterMode AND_FILTERS =
                                                new CombinationFilterMode (1);

    /**
     * Mode setting that instructs the filter to <tt>OR</tt> all the
     * contained filters.
     */
    public static final CombinationFilterMode OR_FILTERS  =
                                                new CombinationFilterMode (2);

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private List<FilenameFilter> filters = new LinkedList<FilenameFilter>();
    private CombinationFilterMode  mode  = AND_FILTERS;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>CombinationFilenameFilter</tt> with a mode of
     * {@link #AND_FILTERS}. The mode can be changed later by calling
     * {@link #setMode(CombinationFilterMode) setMode()}.
     *
     * @see #CombinationFilenameFilter(CombinationFilterMode)
     * @see #setMode
     */
    public CombinationFilenameFilter()
    {
        this (AND_FILTERS);
    }

    /**
     * Construct a new <tt>CombinationFilenameFilter</tt> with the specified
     * mode.
     *
     * @param mode  {@link #AND_FILTERS} if a filename must be accepted
     *              by all contained filters. {@link #OR_FILTERS} if a 
     *              filename only needs to be accepted by one of the
     *              contained filters.
     *
     * @see #setMode
     */
    public CombinationFilenameFilter (CombinationFilterMode mode)
    {
        setMode (mode);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the combination mode of this <tt>CombinationFilenameFilter</tt>
     * object.
     *
     * @return  {@link #AND_FILTERS} if a filename must be accepted by all
     *          contained filters. {@link #OR_FILTERS} if a filename only
     *          needs to be accepted by one of the contained filters.
     *
     * @see #setMode
     */
    public CombinationFilterMode getMode()
    {
        return mode;
    }

    /**
     * Change the combination mode of this <tt>CombinationFilenameFilter</tt>
     * object.
     *
     * @param mode  {@link #AND_FILTERS} if a filename must be accepted
     *              by all contained filters. {@link #OR_FILTERS} if a 
     *              filename only needs to be accepted by one of the
     *              contained filters.
     *
     * @return the previous mode
     *
     * @see #getMode
     */
    public void setMode (CombinationFilterMode mode)
    {
        this.mode = mode;
    }

    /**
     * Add a filter to the set of contained filters.
     *
     * @param filter the <tt>FilenameFilter</tt> to add.
     *
     * @see #removeFilter
     */
    public void addFilter (FilenameFilter filter)
    {
        filters.add (filter);
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
     * contained filters and the mode. If this object's mode mode is set to
     * {@link #AND_FILTERS}, then a file must be accepted by all contained
     * filters to be accepted. If this object's mode is set to
     * {@link #OR_FILTERS}, then a file name is accepted if any one of the
     * contained filters accepts it. If the set of contained filters is
     * empty, then this method returns <tt>false</tt>.
     *
     * @param dir   The directory containing the file.
     * @param name  the file name
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File dir, String name)
    {
        boolean         accepted = false;
        Iterator        it = filters.iterator();
        FilenameFilter  filter;  

        if (mode.value == AND_FILTERS.value)
        {
            accepted = true;

            while (accepted && it.hasNext())
            {
                filter = (FilenameFilter) it.next();
                accepted = filter.accept (dir, name);
            }
        }

        else
        {
            accepted = false;

            while ((! accepted) && it.hasNext())
            {
                filter = (FilenameFilter) it.next();
                accepted = filter.accept (dir, name);
            }
        }

        return accepted;
    }
}
