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

package org.clapper.util.misc;

import java.lang.System;

/**
 * <p>Contains the software version for the <i>org.clapper.util</i>
 * library. Also contains a main program which, invoked, displays the name
 * of the API, the version, and detailed build information on standard
 * output.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public final class Version
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    public static final String API_VERSION = "1.1";

    /**
     * The name of the resource bundle containing the build info.
     */
    public static final String BUILD_INFO_BUNDLE_NAME
        = "org.clapper.util.misc.BuildInfoBundle";

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private Version()
    {
    }

    /*----------------------------------------------------------------------*\
                               Main Program
    \*----------------------------------------------------------------------*/

    /**
     * Display the build information
     *
     * @param args  command-line parameters (ignored)
     */
    public static void main (String[] args)
    {
        BuildInfo buildInfo = new BuildInfo (BUILD_INFO_BUNDLE_NAME);

        System.out.println ("org.clapper.util library, version " +
                            API_VERSION);
        System.out.println ();
        System.out.println ("Build date:     " +
                            buildInfo.getBuildDate());
        System.out.println ("Built by:       " +
                            buildInfo.getBuildUserID());
        System.out.println ("Built on:       " +
                            buildInfo.getBuildOperatingSystem());
        System.out.println ("Build Java VM:  " +
                            buildInfo.getBuildJavaVM());
        System.out.println ("Build compiler: " +
                            buildInfo.getBuildJavaCompiler());
        System.out.println ("Ant version:    " +
                            buildInfo.getBuildAntVersion());
        System.exit (0);
    }
}
