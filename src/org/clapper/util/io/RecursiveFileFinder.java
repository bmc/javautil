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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import java.util.Collection;

/**
 * A <tt>RecursiveFileFinder</tt> walks a directory tree and finds all
 * files (or directories) that satisfy caller-supplied criteria. The
 * match criteria are specified via either a <tt>FileFilter</tt> or
 * <tt>FilenameFilter</tt> object.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class RecursiveFileFinder
{
    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>RecursiveFileFinder</tt> object. The various
     * <tt>find()</tt> methods actually provide the searching capabilities.
     */
    public RecursiveFileFinder()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Find all files beneath a given directory. This version of
     * <tt>find()</tt> takes no filter, so every file and directory is
     * matched.
     *
     * @param directory  the starting directory
     * @param collection where to store the found <tt>File</tt> objects
     *
     * @return the number of <tt>File</tt> objects found
     */
    public int findFiles (File directory, Collection<File> collection)
    {
        return findFiles (directory, (FileFilter) null, collection);
    }

    /**
     * Find all files beneath a given directory, filtered by the specified
     * <tt>FilenameFilter</tt>.
     *
     * @param directory  the starting directory
     * @param filter     the <tt>FilenameFilter</tt> to use to filter the
     *                   file names, or null to accept all files
     * @param collection where to store the found <tt>File</tt> objects
     *
     * @return the number of <tt>File</tt> objects found
     */
    public int findFiles (File             directory,
                          FilenameFilter   filter,
                          Collection<File> collection)
    {
        int    total = 0;
        File[] files = directory.listFiles (filter);

        if (files != null)
        {
            for (int i = 0; i < files.length; i++)
                collection.add (files[i]);

            total = files.length;
        }

        File[] dirs = directory.listFiles (new DirectoryFilter());
        if (dirs != null)
        {
            for (int i = 0; i < dirs.length; i++)
                total += findFiles (dirs[i], filter, collection);
        }

        return total;
    }

    /**
     * Find all files beneath a given directory, filtered by the specified
     * <tt>FilenameFilter</tt>.
     *
     * @param directory  the starting directory
     * @param filter     the <tt>FilenameFilter</tt> to use to filter the
     *                   file names, or null to accept all files
     * @param collection where to store the found <tt>File</tt> objects
     *
     * @return the number of <tt>File</tt> objects found
     */
    public int findFiles (File             directory,
                          FileFilter       filter,
                          Collection<File> collection)
    {
        int    total = 0;
        File[] files = directory.listFiles (filter);

        if (files != null)
        {
            for (int i = 0; i < files.length; i++)
                collection.add (files[i]);

            total = files.length;
        }

        File[] dirs = directory.listFiles (new DirectoryFilter());
        if (dirs != null)
        {
            for (int i = 0; i < dirs.length; i++)
                total += findFiles (dirs[i], filter, collection);
        }

        return total;
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/
}
