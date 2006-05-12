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

import java.io.FileFilter;
import java.io.File;

/**
 * <tt>NotFileFilter</tt> is a <tt>FileFilter</tt> that wraps another
 * <tt>FileFilter</tt> and negates the sense of the wrapped filter's
 * <tt>accept()</tt> method. This class conceptually provides a logical
 * "NOT" operator for file filters. For example, the following code
 * fragment will create a filter that finds all files that are not
 * directories.
 *
 * <blockquote><pre>
 * NotFileFilter filter = new NotFileFilter (new DirectoryFilter());
 * </pre></blockquote>
 *
 * @see FileFilter
 * @see AndFileFilter
 * @see OrFileFilter
 * @see DirectoryFilter
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class NotFileFilter implements FileFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private FileFilter filter;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>NotFileFilter</tt> that wraps the
     * specified {@link FileFilter}.
     *
     * @param filter  The {@link FileFilter} to wrap.
     */
    public NotFileFilter (FileFilter filter)
    {
        this.filter = filter;
    }

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Tests whether a file should be included in a file list.
     *
     * @param file  The file to check for acceptance
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File file)
    {
        return ! this.filter.accept (file);
    }
}
