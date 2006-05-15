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

import java.io.File;

/**
 * Miscellaneous class-related utility methods.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class ClassUtil
{
    /*----------------------------------------------------------------------*\
                         Package-visible Constants
    \*----------------------------------------------------------------------*/

    static final String BUNDLE_NAME = "org.clapper.util.classutil.Bundle";
    
    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether a file is a jar file, zip file or directory (i.e.,
     * represents places that can be searched for classes).
     *
     * @param file  the file to check
     *
     * @return <tt>true</tt> if the file represents a place that can be
     *         searched for classes, <tt>false</tt> if not
     */
    public static boolean fileCanContainClasses (File file)
    {
        boolean can      = false;
        String  fileName = file.getPath();

        if (file.exists())
        {
            can = ((fileName.toLowerCase().endsWith (".jar")) ||
                   (fileName.toLowerCase().endsWith (".zip")) ||
                   (file.isDirectory()));
        }

        return can;
    }
}
