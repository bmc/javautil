/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.io;

import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.oro.io.*;
import org.apache.oro.text.*;
import org.apache.oro.text.regex.*;

/**
 * A <tt>RecursiveFileFinder</tt> walks a directory tree and finds all
 * files (or directories) that satisfy caller-supplied criteria. The
 * match criteria are specified via either a <tt>FileFilter</tt> or
 * <tt>FilenameFilter</tt> object.
 *
 * @version <tt>$Revision$</tt>
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
    public int findFiles (File directory, Collection collection)
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
    public int findFiles (File           directory,
                          FilenameFilter filter,
                          Collection     collection)
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
    public int findFiles (File       directory,
                          FileFilter filter,
                          Collection collection)
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
