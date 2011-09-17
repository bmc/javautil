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

package org.clapper.util.classutil;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>An <tt>OrClassFilter</tt> contains logically ORs other
 * {@link ClassFilter} objects. When its {@link #accept accept()} 
 * method is called, the <tt>OrClassFilter</tt> object passes
 * the class name through the contained filters. The class name is
 * accepted if it is accepted by any one of the contained filters. This
 * class conceptually provides a logical "OR" operator for class name
 * filters.</p>
 *
 * <p>The contained filters are applied in the order they were added to
 * the <tt>OrClassFilter</tt> object. This class's
 * {@link #accept accept()} method stops looping over the contained filters
 * as soon as it encounters one whose <tt>accept()</tt> method returns
 * <tt>true</tt> (implementing a "short-circuited OR" operation.) </p>
 *
 * @see ClassFilter
 * @see OrClassFilter
 * @see NotClassFilter
 * @see ClassFinder
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public final class OrClassFilter implements ClassFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private List<ClassFilter> filters = new LinkedList<ClassFilter>();

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>OrClassFilter</tt> with no contained filters.
     */
    public OrClassFilter()
    {
        // Nothing to do
    }

    /**
     * Construct a new <tt>OrClassFilter</tt> with two contained filters.
     * Additional filters may be added later, via calls to the
     * {@link #addFilter addFilter()} method.
     *
     * @param filters  filters to add
     */
    public OrClassFilter (ClassFilter... filters)
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
    public OrClassFilter addFilter (ClassFilter filter)
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
     * the contained filters. The class name name is accepted if any
     * one of the contained filters accepts it. This method stops
     * looping over the contained filters as soon as it encounters one
     * whose {@link ClassFilter#accept accept()} method returns
     * <tt>true</tt> (implementing a "short-circuited OR" operation.)</p>
     *
     * <p>If the set of contained filters is empty, then this method
     * returns <tt>true</tt>.</p>
     *
     * @param classInfo   the {@link ClassInfo} object to test
     * @param classFinder the invoking {@link ClassFinder} object
     *
     * @return <tt>true</tt> if the name matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (ClassInfo classInfo, ClassFinder classFinder)
    {
        boolean accepted = false;

        if (filters.size() == 0)
            accepted = true;

        else
        {
            for (ClassFilter filter : filters)
            {
                accepted = filter.accept (classInfo, classFinder);
                if (accepted)
                    break;
            }
        }

        return accepted;
    }
}
