/*---------------------------------------------------------------------------*\
  $Id: ClassUtil.java 5812 2006-05-12 00:38:16Z bmc $
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

import java.io.FilenameFilter;
import java.io.File;

/**
 * <tt>NotFilenameFilter</tt> is a <tt>FilenameFilter</tt> that wraps another
 * <tt>FilenameFilter</tt> and negates the sense of the wrapped filter's
 * <tt>accept()</tt> method. This class conceptually provides a logical
 * "NOT" operator for file filters. For example, the following code
 * fragment will create a filter that finds all files that do not start with
 * the letter "A".
 *
 * <blockquote><pre>
 * NotFilenameFilter filter = new NotFilenameFilter (new RegexFilenameFilter ("^[Aa]", FileFilterMatchType.NAME));
 * </pre></blockquote>
 *
 * @see FilenameFilter
 * @see AndFilenameFilter
 * @see OrFilenameFilter
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class NotFilenameFilter implements FilenameFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private FilenameFilter filter;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>NotFilenameFilter</tt> that wraps the
     * specified {@link FilenameFilter}.
     *
     * @param filter  The {@link FilenameFilter} to wrap.
     */
    public NotFilenameFilter (FilenameFilter filter)
    {
        this.filter = filter;
    }

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Tests whether a file should be included in a file list.
     *
     * @param dir   The directory containing the file.
     * @param name  the file name
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File dir, String name)
    {
        return ! this.filter.accept (dir, name);
    }
}
