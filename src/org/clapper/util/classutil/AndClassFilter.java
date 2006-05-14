/*---------------------------------------------------------------------------*\
  $Id: AndClassFilter.java 5596 2005-08-18 15:34:24Z bmc $
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>An <tt>AndClassFilter</tt> logically ANDs other
 * {@link ClassFilter} objects. When its {@link #accept accept()} 
 * method is called, the <tt>AndClassFilter</tt> object passes
 * the class name through the contained filters. The class name is only
 * accepted if it is accepted by all contained filters. This
 * class conceptually provides a logical "AND" operator for class name
 * filters.</p>
 *
 * <p>The contained filters are applied in the order they were added to
 * the <tt>AndClassFilter</tt> object. This class's
 * {@link #accept accept()} method stops looping over the contained filters
 * as soon as it encounters one whose <tt>accept()</tt> method returns
 * <tt>false</tt> (implementing a "short-circuited AND" operation.) </p>
 *
 * @see ClassFilter
 * @see OrClassFilter
 * @see NotClassFilter
 * @see ClassFinder
 *
 * @version <tt>$Revision: 5596 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class AndClassFilter implements ClassFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private List<ClassFilter> filters = new LinkedList<ClassFilter>();

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>AndClassFilter</tt> with no contained filters.
     */
    public AndClassFilter()
    {
    }

    /**
     * Construct a new <tt>AndClassFilter</tt> with a set of contained
     * filters. Additional filters may be added later, via calls to the
     * {@link #addFilter addFilter()} method.
     *
     * @param filters  filters to add
     */
    public AndClassFilter (ClassFilter... filters)
    {
        for (ClassFilter filter : filters)
            addFilter (filter);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add a filter to the set of contained filters.
     *
     * @param filter the <tt>ClassFilter</tt> to add.
     *
     * @return this object, to permit chained calls.
     *
     * @see #removeFilter
     */
    public AndClassFilter addFilter (ClassFilter filter)
    {
        filters.add (filter);
        return this;
    }

    /**
     * Remove a filter from the set of contained filters.
     *
     * @param filter the <tt>ClassFilter</tt> to remove.
     *
     * @see #addFilter
     */
    public void removeFilter (ClassFilter filter)
    {
        filters.remove (filter);
    }

    /**
     * Get the contained filters, as an unmodifiable collection.
     *
     * @return the unmodifable <tt>Collection</tt>
     */
    public Collection<ClassFilter> getFilters()
    {
        return Collections.unmodifiableCollection (filters);
    }

    /**
     * Get the total number of contained filter objects (not counting any
     * filter objects <i>they</i>, in turn, contain).
     *
     * @return the total
     */
    public int getTotalFilters()
    {
        return filters.size();
    }

    /**
     * <p>Determine whether a class name is to be accepted or not, based on
     * the contained filters. The class name is accepted if any one of the
     * contained filters accepts it. This method stops looping over the
     * contained filters as soon as it encounters one whose
     * {@link ClassFilter#accept accept()} method returns
     * <tt>false</tt> (implementing a "short-circuited AND" operation.)</p>
     *
     * <p>If the set of contained filters is empty, then this method
     * returns <tt>true</tt>.</p>
     *
     * @param className  the class name
     *
     * @return <tt>true</tt> if the name matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (String className)
    {
        boolean accepted = true;

        for (ClassFilter filter : filters)
        {
            accepted = filter.accept (className);
            if (! accepted)
                break;
        }

        return accepted;
    }
}
