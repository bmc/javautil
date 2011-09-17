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
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
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
