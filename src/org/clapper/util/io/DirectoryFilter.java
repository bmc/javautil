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

    public boolean accept (File f)
    {
        return f.isDirectory();
    }
}
