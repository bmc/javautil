/*---------------------------------------------------------------------------*\
  $Id: ClassUtil.java 5607 2005-11-25 04:32:30Z bmc $
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2006 Brian M. Clapper. All rights reserved.

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
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.clapper.util.logging.Logger;

import org.clapper.util.io.AndFileFilter;
import org.clapper.util.io.FileOnlyFilter;
import org.clapper.util.io.FileFilterMatchType;
import org.clapper.util.io.RegexFileFilter;
import org.clapper.util.io.RecursiveFileFinder;

/**
 * <p>A <tt>ClassFinder</tt> object is used to find classes. By default, an
 * instantiated <tt>ClassFinder</tt> won't find any classes; you have to
 * add the classpath (via a call to {@link #addClassPath}), add jar files,
 * add zip files, and/or add directories to the <tt>ClassFinder</tt> so it
 * knows where to look. Adding a jar file to a <tt>ClassFinder</tt> causes
 * the <tt>ClassFinder</tt> to look at the jar's manifest for a
 * "Class-Path" entry; if the <tt>ClassFinder</tt> finds such an entry, it
 * adds the contents to the search path, as well.</p>
 *
 * <p>The following example illustrates how you might use a
 * <tt>ClassFinder</tt> to locate all non-abstract classes that implement
 * the <tt>ClassFilter</tt> interface, searching the classpath as well
 * as anything specified on the command line.</p>
 *
 * <blockqutoe><pre>
 * import org.clapper.util.classutil.*;
 *
 * public class Test
 * {
 *     public static void main (String[] args) throws Throwable
 *     {
 *         ClassFinder finder = new ClassFinder();
 *         for (String arg : args)
 *             finder.add (arg);
 *
 *         ClassFilter filter =
 *             new AndClassFilter
 *                 // Must not be an interface
 *                 (new NotClassFilter (new InterfaceOnlyClassFilter()),
 *
 *                 // Must implement the interface class
 *                 new SubclassClassFilter (ClassFilter.class),
 *
 *                 // Must not be abstract
 *                 new NotClassFilter (new AbstractClassFilter()));
 *
 *         Collection<String> classNames = new ArrayList<String>();
 *         finder.findClasses (classNames, filter);
 *
 *         for (String className : classNames)
 *             System.out.println ("Found " + className);
 *     }
 * }
 * </pre></blockquote>
 *
 * @version <tt>$Revision: 5607 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class ClassFinder
{
    /*----------------------------------------------------------------------*\
			    Private Data Items
    \*----------------------------------------------------------------------*/

    private LinkedHashMap<String,File> placesToSearch =
        new LinkedHashMap<String,File>();

    /**
     * For logging
     */
    private static final Logger log = new Logger (ClassFinder.class);

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>ClassFinder</tt> that will search for classes
     * using the default class loader.
     */
    public ClassFinder()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add the contents of the system classpath for classes.
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
     * Add a jar file, zip file or directory to the list of places to search
     * for classes.
     *
     * @param file  the jar file, zip file or directory
     *
     * @return <tt>true</tt> if the file was suitable for adding;
     *         <tt>false</tt> if it was not a jar file, zip file, or
     *         directory.
     */
    public boolean add (File file)
    {
	boolean added = false;

	if (ClassUtil.fileCanContainClasses (file))
        {
            String absPath = file.getAbsolutePath();
            if (placesToSearch.get (absPath) == null)
            {
                placesToSearch.put (absPath, file);
                if (isJar (absPath))
                    loadJarClassPathEntries (file);
            }

            added = true;
        }

        return added;
    }

    /**
     * Clear the finder's notion of where to search.
     */
    public void clear()
    {
        placesToSearch.clear();
    }

    /**
     * Find all classes in the search areas, implicitly accepting all of
     * them.
     *
     * @param classNames where to store the resulting matches
     *
     * @return the number of matched classes added to the collection
     */
    public int findClasses (Collection<String> classNames)
    {
        return findClasses (classNames,
                            new ClassFilter()
                            {
                                public boolean accept (String className)
                                {
                                    return true;
                                }
                            });
    }

    /**
     * Search all classes in the search areas, keeping only those that
     * pass the specified filter.
     *
     * @param classNames where to store the resulting matches
     * @param filter     the filter
     *
     * @return the number of matched classes added to the collection
     */
    public int findClasses (Collection<String> classNames,
                            ClassFilter        filter)
    {
        int total = 0;

        // Dump them into a set, so we don't put the same class in the set
        // twice, even if we find it twice. Can't use the caller's
        // Collection, because it might not be a Set. Use a LinkedHashSet,
        // because we want to maintain the order of the classes as we find
        // them. (Let the caller re-order them, if desired.)

        Set<String> foundClasses = new LinkedHashSet<String>();

        for (File file : placesToSearch.values())
        {
            String name = file.getPath();

            log.debug ("Finding classes in " + name);
            if (isJar (name))
                total += processJar (name, filter, foundClasses);
            else if (isZip (name))
                total += processZip (name, filter, foundClasses);
            else
                total += processDirectory (file, filter, foundClasses);
        }

        classNames.addAll (foundClasses);
        return total;
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private int processJar (String             jarName,
                            ClassFilter        filter,
                            Collection<String> classNames)
    {
        int total = 0;

        try
        {
            total = processOpenZip (new JarFile (jarName), filter, classNames);
        }

        catch (IOException ex)
        {
            log.error ("Can't open jar file \"" + jarName + "\"", ex);
        }

        return total;
    }

    private int processZip (String             zipName,
                            ClassFilter        filter,
                            Collection<String> classNames)
    {
        int total = 0;

        try
        {
            total = processOpenZip (new ZipFile (zipName), filter, classNames);
        }

        catch (IOException ex)
        {
            log.error ("Can't open jar file \"" + zipName + "\"", ex);
        }

        return total;
    }

    private int processOpenZip (ZipFile            zip,
                                ClassFilter        filter,
                                Collection<String> classNames)
    {
        int total = 0;

        for (Enumeration<? extends ZipEntry> e = zip.entries();
             e.hasMoreElements(); )
        {
            ZipEntry entry = e.nextElement();

            if ((! entry.isDirectory()) &&
                (entry.getName().toLowerCase().endsWith (".class")))
            {
                String className = getClassNameFrom (entry.getName());
                if (filter.accept (className))
                {
                    classNames.add (className);
                    total++;
                }
            }
        }

        return total;
    }

    private int processDirectory (File               dir,
                                  ClassFilter        classFilter,
                                  Collection<String> classNames)
    {
        int total = 0;

        RecursiveFileFinder finder = new RecursiveFileFinder();
        RegexFileFilter nameFilter =
            new RegexFileFilter ("\\.class$", FileFilterMatchType.FILENAME);
        AndFileFilter fileFilter = new AndFileFilter (nameFilter,
                                                      new FileOnlyFilter());
        Collection<File> files = new ArrayList<File>();
        finder.findFiles (dir, fileFilter, files);

        for (File f : files)
        {
            String path = f.getPath();
            path = path.replaceFirst ("^" + dir.getPath() + "/?", "");
            String className = getClassNameFrom (path);
            if (classFilter.accept (className))
            {
                classNames.add (className);
                total++;
            }
        }

        return total;
    }

    private void loadJarClassPathEntries (File jarFile)
    {
        try
        {
            JarFile jar = new JarFile (jarFile);
            Manifest manifest = jar.getManifest();
            if (manifest == null)
                return;

            Map map = manifest.getEntries();
            Attributes attrs = manifest.getMainAttributes();
            Set<Object> keys = attrs.keySet();

            for (Object key : keys)
            {
                String value = (String) attrs.get (key);

                if (key.toString().equals ("Class-Path"))
                {
                    String jarName = jar.getName();
                    log.debug ("Adding Class-Path from jar " + jarName);

                    StringBuilder buf = new StringBuilder();
                    StringTokenizer tok = new StringTokenizer (value);
                    while (tok.hasMoreTokens())
                    {
                        buf.setLength (0);
                        String element = tok.nextToken();
                        String parent = jarFile.getParent();
                        if (parent != null)
                        {
                            buf.append (parent);
                            buf.append (File.separator);
                        }

                        buf.append (element);
                    }

                    String element = buf.toString();
                    log.debug ("From " + jarName + ": " + element);

                    add (new File (element));
                }
            }
        }

        catch (IOException ex)
        {
            log.error ("I/O error processing jar file \""
                     + jarFile.getPath()
                     + "\"",
                       ex);
        }
    }

    private String getClassNameFrom (String entryName)
    {
        String s = new String (entryName).replace ('/', '.');
        s = s.replace ('\\', '.');
        return s.substring (0, s.lastIndexOf ( '.' ));
    }

    private boolean isJar (String fileName)
    {
        return fileName.toLowerCase().endsWith (".jar");
    }

    private boolean isZip (String fileName)
    {
        return fileName.toLowerCase().endsWith (".zip");
    }
}
