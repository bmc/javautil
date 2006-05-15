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

package org.clapper.util.classutil;

/**
 * <tt>NotClassFilter</tt> is a {@link ClassFilter} that
 * wraps another {@link ClassFilter} and negates the sense of the
 * wrapped filter's {@link ClassFilter#accept accept()} method. This
 * class conceptually provides a logical "NOT" operator for class name
 * filters. For example, the following code fragment will create a filter
 * that finds all classes that are not interfaces.
 *
 * <blockquote><pre>
 * NotClassFilter filter = new NotClassFilter (new InterfaceOnlyClassFilter());
 * </pre></blockquote>
 *
 * @see ClassFilter
 * @see AndClassFilter
 * @see OrClassFilter
 * @see ClassFinder
 * @see InterfaceOnlyClassFilter
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class NotClassFilter implements ClassFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private ClassFilter filter;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>NotClassFilter</tt> that wraps the
     * specified {@link ClassFilter}.
     *
     * @param filter  The {@link ClassFilter} to wrap.
     */
    public NotClassFilter (ClassFilter filter)
    {
        this.filter = filter;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Tests whether a class name should be included in a class name
     * list.
     *
     * @param classInfo   the {@link ClassInfo} object to test
     * @param classFinder the invoking {@link ClassFinder} object
     *
     * @return <tt>true</tt> if and only if the name should be included
     *         in the list; <tt>false</tt> otherwise
     */
    public boolean accept (ClassInfo classInfo, ClassFinder classFinder)
    {
        return ! this.filter.accept (classInfo, classFinder);
    }
}
