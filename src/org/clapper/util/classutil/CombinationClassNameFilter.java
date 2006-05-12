/*---------------------------------------------------------------------------*\
  $Id: CombinationClassNameFilter.java 5596 2005-08-18 15:34:24Z bmc $
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2006 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.classutil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>A <tt>CombinationClassNameFilter</tt> contains one or more
 * {@link ClassNameFilter} objects. When its {@link #accept accept()} 
 * method is called, the <tt>CombinationClassNameFilter</tt> object passes
 * the file through the contained filters. If the
 * <tt>CombinationClassNameFilter</tt> object's mode is set to
 * <tt>AND_FILTERS</tt>, then a file must be accepted by all contained
 * filters to be accepted. If the <tt>CombinationClassNameFilter</tt>
 * object's mode is set to <tt>OR_FILTERS</tt>, then a file name is
 * accepted if any one of the contained filters accepts it. The default
 * mode is <tt>AND_FILTERS</tt>.</p>
 *
 * <p>The contained filters are applied in the order they were added to
 * the <tt>CombinationClassNameFilter</tt> object.</p>
 *
 * @see ClassNameFilter
 *
 * @version <tt>$Revision: 5596 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class CombinationClassNameFilter implements ClassNameFilter
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    public enum Mode
    {
        AND_FILTERS,
        OR_FILTERS
    }

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private List<ClassNameFilter> filters = new LinkedList<ClassNameFilter>();
    private Mode                  mode    = Mode.AND_FILTERS;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>CombinationClassNameFilter</tt> with a mode of
     * <tt>AND_FILTERS</tt>. The mode can be changed later by calling
     * {@link #setMode setMode()}.
     *
     * @see #CombinationClassNameFilter(CombinationFilterMode)
     * @see #setMode
     */
    public CombinationClassNameFilter()
    {
        this (Mode.AND_FILTERS);
    }

    /**
     * Construct a new <tt>CombinationClassNameFilter</tt> with the specified
     * mode.
     *
     * @param mode  <tt>AND_FILTERS</tt> if a filename must be accepted
     *              by all contained filters. <tt>OR_FILTERS</tt> if a 
     *              filename only needs to be accepted by one of the
     *              contained filters.
     *
     * @see #setMode
     */
    public CombinationClassNameFilter (Mode mode)
    {
        setMode (mode);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the combination mode of this <tt>CombinationClassNameFilter</tt>
     * object.
     *
     * @return  <tt>AND_FILTERS</tt> if a filename must be accepted by all
     *          contained filters. <tt>OR_FILTERS</tt> if a filename only
     *          needs to be accepted by one of the contained filters.
     *
     * @see #setMode
     */
    public Mode getMode()
    {
        return mode;
    }

    /**
     * Change the combination mode of this <tt>CombinationClassNameFilter</tt>
     * object.
     *
     * @param mode  <tt>AND_FILTERS</tt> if a filename must be accepted
     *              by all contained filters. <tt>OR_FILTERS</tt> if a 
     *              filename only needs to be accepted by one of the
     *              contained filters.
     *
     * @see #getMode
     */
    public void setMode (Mode mode)
    {
        this.mode = mode;
    }

    /**
     * Add a filter to the set of contained filters.
     *
     * @param filter the <tt>ClassNameFilter</tt> to add.
     *
     * @see #removeFilter
     */
    public void addFilter (ClassNameFilter filter)
    {
        filters.add (filter);
    }

    /**
     * Remove a filter from the set of contained filters.
     *
     * @param filter the <tt>ClassNameFilter</tt> to remove.
     *
     * @see #addFilter
     */
    public void removeFilter (ClassNameFilter filter)
    {
        filters.remove (filter);
    }

    /**
     * Determine whether a class name is to be accepted or not, based on
     * the contained filters and the mode. If this object's mode mode is
     * set to <tt>AND_FILTERS</tt>, then a class name must be accepted by
     * all contained filters to be accepted. If this object's mode is set
     * to <tt>OR_FILTERS</tt>, then a class name name is accepted if any
     * one of the contained filters accepts it. If the set of contained
     * filters is empty, then this method returns <tt>false</tt>.
     *
     * @param className  the class name
     *
     * @return <tt>true</tt> if the name matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (String className)
    {
        boolean accepted = false;

        switch (mode)
        {
            case AND_FILTERS:
                accepted = true;
                for (ClassNameFilter filter : filters)
                {
                    accepted = filter.accept (className);
                    if (! accepted)
                        break;
                }
                break;

            case OR_FILTERS:
                accepted = false;
                for (ClassNameFilter filter : filters)
                {
                    accepted = filter.accept (className);
                    if (accepted)
                        break;
                }
                break;

            default:
                assert (false);
        }

        return accepted;
    }
}
