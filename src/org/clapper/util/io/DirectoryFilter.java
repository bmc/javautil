/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.io;

import java.io.*;

/**
 * <tt>DirectoryFilter</tt> implements a
 * <tt>java.io.FileFilter</tt> that matches only directories.
 *
 * @version <tt>$Revision$</tt>
 */
public class DirectoryFilter implements FileFilter
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    public DirectoryFilter()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether the specified file is a directory or not.
     *
     * @return <tt>true</tt> if the file is a directory, <tt>false</tt> if not
     */
    public boolean accept (File f)
    {
        return f.isDirectory();
    }
}
