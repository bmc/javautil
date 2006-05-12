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

package org.clapper.util.classutil;

import java.io.File;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import org.clapper.util.logging.Logger;

/**
 * A <tt>ClassLoaderBuilder</tt> is used to build an alternate class loader
 * that includes additional jar files, zip files and/or directories in its
 * load path. It's basically a convenient wrapper around
 * <tt>java.net.URLClassLoader</tt>.
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class ClassLoaderBuilder
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private Collection<URL> urlList = new LinkedHashSet<URL>();

    /**
     * For logging
     */
    private static Logger log = new Logger (ClassLoaderBuilder.class);

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>ClassLoaderBuilder</tt>.
     */
    public ClassLoaderBuilder()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add a jar file, zip file or directory to the list of places the
     * not-yet-constructed class loader will search. If the directory or
     * file does not exist, or isn't a jar file, zip file, or directory,
     * this method just ignores it and returns <tt>false</tt>.
     *
     * @param file  the jar file, zip file or directory
     *
     * @return <tt>true</tt> if the file was suitable for adding;
     *         <tt>false</tt> if it was not a jar file, zip file, or
     *         directory.
     */
    public boolean add (File file)
    {
        boolean added    = false;
        String  fileName = file.getPath();

        try
        {
            if (ClassUtil.fileCanContainClasses (file))
            {
                if (file.isDirectory())
                {
                    if (! fileName.endsWith ("/"))
                    {
                        fileName = fileName + "/";
                        file = new File (fileName);
                    }
                }

                urlList.add (file.toURL());
                added = true;
            }
        }

        catch (MalformedURLException ex)
        {
            log.error ("Unexpected exception", ex);
        }

        if (! added)
        {
            log.debug ("Skipping non-jar, non-zip, non-directory \""
                     + fileName
                     + "\"");
        }

        return added;
    }

    /**
     * Add the contents of the classpath.
     */
    public void addClassPath()
    {
        String path = null;

        try
        {
            path = System.getProperty ("java.class.path");
        }

        catch (Exception ex)
        {
            path= "";
            log.error ("Unable to get class path", ex);
        }
    
        StringTokenizer tok = new StringTokenizer (path, File.pathSeparator);

        while (tok.hasMoreTokens())
            add (new File (tok.nextToken()));
    }

    /**
     * Clear the stored files in this object.
     */
    public void clear()
    {
        urlList.clear();
    }

    /**
     * Create and return a class loader that will search the additional
     * places defined in this builder. The resulting class loader uses
     * the default delegation parent <tt>ClassLoader</tt>.
     *
     * @throws SecurityException if a security manager exists and its
     *                           <tt>checkCreateClassLoader()</tt> method
     *                           does not allow creation of a class loader
     */
    public ClassLoader createClassLoader()
        throws SecurityException
    {
        return new URLClassLoader (urlList.toArray (new URL[urlList.size()]),
                                   getClass().getClassLoader());
    }

    /**
     * Create and return a class loader that will search the additional
     * places defined in this builder. The resulting class loader uses
     * the specified parent <tt>ClassLoader</tt>.
     *
     * @param parentLoader the desired parent class loader
     *
     * @throws SecurityException if a security manager exists and its
     *                           <tt>checkCreateClassLoader()</tt> method
     *                           does not allow creation of a class loader
     */
    public ClassLoader createClassLoader (ClassLoader parentLoader)
        throws SecurityException
    {
        return new URLClassLoader (urlList.toArray (new URL[urlList.size()]),
                                                    parentLoader);
    }
}
