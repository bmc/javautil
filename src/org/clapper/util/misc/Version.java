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

import java.util.ResourceBundle;
import java.util.MissingResourceException;

/**
 * <p>Contains the software version for the <i>org.clapper.util</i>
 * library. Also contains a main program which, invoked, displays the
 * name of the API and the version on standard output.</p>
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

    public static final String API_VERSION = "0.5";

    /**
     * The build ID key within the bundle.
     */
    public static final String BUILD_ID_KEY = "build.id";

    /**
     * The key to retrieve the operating system where the build occurred.
     */
    public static final String BUILD_OS_KEY = "build.os";

    /**
     * The key to retrieve the VM used for the build.
     */
    public static final String BUILD_VM_KEY = "build.vm";

    /**
     * The key to retrieve the compiler used for the build.
     */
    public static final String BUILD_COMPILER_KEY = "build.compiler";

    /**
     * The key to retrieve the version of Ant used for the build.
     */
    public static final String BUILD_ANT_VERSION_KEY = "build.ant.version";

    /**
     * The build date, in "raw" (internal, numeric) form.
     */
    public static final String BUILD_DATE_KEY = "build.date";

    /**
     * The user and host where the build occurred.
     */
    public static final String BUILT_BY_KEY = "built.by";

    /**
     * The name of the resource bundle containing the build info.
     */
    public static final String BUNDLE_NAME = "org.clapper.util.misc.BuildInfo";

    /*----------------------------------------------------------------------*\
                                Data Items
    \*----------------------------------------------------------------------*/

    private static ResourceBundle buildInfoBundle = null;

    /*----------------------------------------------------------------------*\
                               Main Program
    \*----------------------------------------------------------------------*/

    /**
     * Display the API name and version on standard output.
     *
     * @param args  command-line parameters (ignored)
     */
    public static void main (String[] args)
    {
        ResourceBundle bundle = getBuildInfoBundle();

        System.out.println ("org.clapper.util library, version " +
                            API_VERSION);
        System.out.println ();
        System.out.println ("Build date:     " +
                            getBundleString (BUILD_DATE_KEY));
        System.out.println ("Built by:       " +
                            getBundleString (BUILT_BY_KEY));
        System.out.println ("Built on:       " +
                            getBundleString (BUILD_OS_KEY));
        System.out.println ("Build Java VM:  " +
                            getBundleString (BUILD_VM_KEY));
        System.out.println ("Build compiler: " +
                            getBundleString (BUILD_COMPILER_KEY));
        System.out.println ("Ant version:    " +
                            getBundleString (BUILD_ANT_VERSION_KEY));
        System.exit (0);
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Attempts to get the resource bundle, if an attempt hasn't already
     * been made.
     *
     * @return the bundle, or null if it could not be found.
     */
    private synchronized static ResourceBundle getBuildInfoBundle()
    {
        if (buildInfoBundle == null)
        {
            try
            {
                buildInfoBundle = ResourceBundle.getBundle (BUNDLE_NAME);
            }

            catch (MissingResourceException ex)
            {
            }
        }

        return buildInfoBundle;
    }

    /**
     * Get a string from the bundle.
     *
     * @param key   The key for the string to be retrieved.
     *
     * @return  The string, or null if unavailable
     */
    private static String getBundleString (String key)
    {
        String result = null;

        try
        {
            ResourceBundle bundle = getBuildInfoBundle();
            if (bundle != null)
                result = bundle.getString (key);
        }

        catch (MissingResourceException ex)
        {
        }

        return result;
    }
}
