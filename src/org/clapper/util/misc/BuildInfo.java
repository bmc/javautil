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

import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Properties;

import java.net.InetAddress;

/**
 * <p>Contains constants for defining and accessing build info. Also
 * acts as a base class for specific packages' classes that retrieve
 * package build data.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class BuildInfo
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

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

    /*----------------------------------------------------------------------*\
                                Data Items
    \*----------------------------------------------------------------------*/

    private static ResourceBundle buildInfoBundle = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Constructor.
     *
     * @param bundleName  the resource bundle containing the build info
     */
    public BuildInfo (String bundleName)
    {
        getBuildInfoBundle (bundleName);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the build date, as a string, from the resource bundle.
     *
     * @return the build date, as a string
     */
    public String getBuildDate()
    {
        return getBundleString (BUILD_DATE_KEY);
    }

    /**
     * Get the string that identifies the user who built the software.
     * Typically, this string contains both the user ID and the host
     * where the build occurred.
     *
     * @return the user identification string
     */
    public String getBuildUserID()
    {
        return getBundleString (BUILT_BY_KEY);        
    }

    /**
     * Get the string that identifies the operating system where the build
     * occurred.
     *
     * @return the operating system string
     */
    public String getBuildOperatingSystem()
    {
        return getBundleString (BUILD_OS_KEY);
    }

    /**
     * Get the string that identifies the Java virtual machine that was
     * used during the build.
     *
     * @return the Java VM string
     */
    public String getBuildJavaVM()
    {
        return getBundleString (BUILD_VM_KEY);
    }

    /**
     * Get the Java compiler used during the build.
     *
     * @return the Java compiler string
     */
    public String getBuildJavaCompiler()
    {
        return getBundleString (BUILD_COMPILER_KEY);
    }

    /**
     * Get the version of Ant used during the build process.
     *
     * @return the Ant version string
     */
    public String getBuildAntVersion()
    {
        return getBundleString (BUILD_ANT_VERSION_KEY);
    }

    /**
     * Update the build bundle file.
     *
     * @param bundleFile   the path to the properties file
     * @param javaCompiler Java compiler name, or null if not known
     * @param antVersion   Ant version, or null if not known
     *
     * @throws IOException  Can't recreate file.
     */
    public static void makeBuildInfoBundle (File   bundleFile,
                                            String javaCompiler,
                                            String antVersion)
        throws IOException
    {
        // Fill an in-memory Properties object, which will be written to disk.

        Properties  props   = new Properties();

        // BUILD_VM_KEY

        String      vmInfo  = System.getProperty ("java.vm.name")
                            + " "
                            + System.getProperty ("java.vm.version")
                            + " ("
                            + System.getProperty ("java.vm.vendor")
                            + ")";
        props.setProperty (BUILD_VM_KEY, vmInfo);

        // BUILD_OS_KEY

        String      osInfo  = System.getProperty ("os.name")
                            + " "
                            + System.getProperty ("os.version")
                            + " ("
                            + System.getProperty ("os.arch")
                            + ")";
        props.setProperty (BUILD_OS_KEY, osInfo);

        // BUILD_COMPILER_KEY

        if (javaCompiler != null)
            props.setProperty (BUILD_COMPILER_KEY, javaCompiler);

        // BUILD_ANT_VERSION_KEY

        if (antVersion != null)
            props.setProperty (BUILD_ANT_VERSION_KEY, antVersion);

        // BUILD_DATE_KEY

        Date now = new Date();
        DateFormat dateFmt = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss z");
        props.setProperty (BUILD_DATE_KEY, dateFmt.format (now));

        // BUILT_BY_KEY

        String user = System.getProperty ("user.name");
        String host = "localhost";

        try
        {
            InetAddress localhost = InetAddress.getLocalHost();
            host = localhost.getHostName();
        }

        catch (Exception ex)
        {
        }

        props.setProperty (BUILT_BY_KEY, user + "@" + host);

        // Save it.

        String header = "Build information. "
                      + "AUTOMATICALLY GENERATED. DO NOT EDIT!";
        System.out.println ("Updating " + bundleFile);
        FileOutputStream  out  = new FileOutputStream (bundleFile);
        props.store (out, header);
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Attempts to get the resource bundle, if an attempt hasn't already
     * been made.
     *
     * @param bundleName  name of resource bundle
     *
     * @return the bundle, or null if it could not be found.
     */
    private synchronized ResourceBundle getBuildInfoBundle (String bundleName)
    {
        if (buildInfoBundle == null)
        {
            try
            {
                buildInfoBundle = ResourceBundle.getBundle (bundleName);
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
        String result = "";

        try
        {
            if (buildInfoBundle != null)
                result = buildInfoBundle.getString (key);
        }

        catch (MissingResourceException ex)
        {
        }

        return result;
    }
}
