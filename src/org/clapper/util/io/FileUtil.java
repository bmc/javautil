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

package org.clapper.util.io;

import java.io.*;

/**
 * Static class containing miscellaneous file utility methods.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class FileUtils
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private FileUtils()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether a string represents an absolute path. On Unix, an
     * absolute path must start with a "/". On Windows, it must begin
     * with one of the machine's valid drive letters.
     *
     * @param path  the path to check
     *
     * @return <tt>true</tt> if it's absolute, <tt>false</tt> if not
     *
     * @throws IOException  on error
     */
    public static boolean isAbsolutePath (String path)
        throws IOException
    {
        // It's important not to use  java.util.File.listRoots(), for two
        // reasons:
        //
        // 1. On Windows, the floppy can be one of the roots. If there isn't
        //    a disk in the floppy drive, some versions of Windows will issue
        //    an "Abort/Continue/Retry" pop-up.
        // 2. If a security manager is installed, listRoots() can return
        //    a null or empty array.
        //
        // So, this version analyzes the pathname textually.

        boolean  isAbsolute = false;
        String   fileSep = System.getProperty ("file.separator");

        if (fileSep.equals ("/"))
        {
            // Unix.

            isAbsolute = path.startsWith ("/");
        }

        else if (fileSep.equals ("\\"))
        {
            // Windows. Must start with something that looks like a drive
            // letter.

            isAbsolute = (Character.isLetter (path.charAt (0))) &&
                         (path.charAt(1) == ':') &&
                         (path.charAt(2) == '\\');
        }

        else
        {
            throw new IOException ("Can't determine operating system from "
                                 + "file separator \""
                                 + fileSep
                                 + "\"");
        }

        return isAbsolute;
    }

    /**
     * Copy an <tt>InputStream</tt> to an <tt>OutputStream</tt>. If either
     * stream is not already buffered, then it's wrapped in the corresponding
     * buffered stream (i.e., <tt>BufferedInputStream</tt> or
     * <tt>BufferedOutputStream</tt>) before copying. Calling this method
     * is equivalent to:
     *
     * <blockquote><pre>copyStream (src, dst, 8192);</pre></blockquote>
     *
     * @param src     the source <tt>InputStream</tt>
     * @param dst     the destination <tt>OutputStream</tt>
     *
     * @throws IOException  on error
     */
    public static void copyStream (InputStream is, OutputStream os)
        throws IOException
    {
        copyStream (is, os, 8192);
    }

    /**
     * Copy an <tt>InputStream</tt> to an <tt>OutputStream</tt>. If either
     * stream is not already buffered, then it's wrapped in the corresponding
     * buffered stream (i.e., <tt>BufferedInputStream</tt> or
     * <tt>BufferedOutputStream</tt>) before copying.
     *
     * @param src        the source <tt>InputStream</tt>
     * @param dst        the destination <tt>OutputStream</tt>
     * @param bufferSize the buffer size to use
     *
     * @throws IOException  on error
     */
    public static void copyStream (InputStream  src,
                                   OutputStream dst,
                                   int          bufferSize)
        throws IOException
    {
        if (! (src instanceof BufferedInputStream))
            src = new BufferedInputStream (src);

        if (! (dst instanceof BufferedOutputStream))
            dst = new BufferedOutputStream (dst);
        
        byte [] buf = new byte [bufferSize];
        int nr = 0;

        while ((nr = src.read (buf)) != -1)
            dst.write (buf, 0, nr); 
            
    }
    /**
     * Copy one file to another.
     *
     * @param src  The file to copy
     * @param dst  Where to copy it. Can be a directory or a file.
     *
     * @throws IOException on error
     */
    public static void copyFile (File src, File dst) throws IOException
    {
        if (dst.isDirectory())
            dst = new File (dst, src.getName());
        
        InputStream   from = null;
        OutputStream  to   = null;

        try
        {
            from = new FileInputStream (src);
            to   = new FileOutputStream (dst);

            copyStream (from, to);
        }

        finally
        {
            if (from != null)
                from.close();

            if (to != null)
                to.close();
        }
    }

    /**
     * Get the extension for a path or file name.
     *
     * @param path  the file or path name
     *
     * @return the extension, or null if there isn't one
     */
    public static String getFileNameExtension (String path)
    {
        String ext = null;
        int    i   = path.indexOf ('.');

        if ((i != -1) && (i != (path.length() - 1)))
            ext = path.substring (i + 1);

        return ext;
    }

    /**
     * Get the name of a file without its extension. Does not remove
     * any parent directory components.
     *
     * @param path  the path
     *
     * @return the path without the extension
     */
    public static String getFileNameNoExtension (String path)
    {
        int i = path.indexOf ('.');

        if (i != -1)
            path = path.substring (0, i);

        return path;
    }

    /**
     * Get the name of a file's parent directory. This is the directory
     * part of the filename. For instance, "/home/foo.zip" would return
     * "/home". This method uses the file's absolute path.
     *
     * @param fileName the file name
     *
     * @return directory name part of the file's absolute pathname
     *
     * @see #dirname(File)
     * @see #basename(String)
     */
    public static String dirname (String fileName)
    {
        return dirname (new File (fileName));
    }

    /**
     * Get the name of a file's parent directory. This is the directory
     * part of the filename. For instance, "/home/foo.zip" would return
     * "/home". This method uses the file's absolute path.
     *
     * @param file  the file whose parent directory is to be returned
     *
     * @return directory name part of the file's absolute pathname
     *
     * @see #dirname(String)
     * @see #basename(File)
     */
    public static String dirname (File file)
    {
        String  absName = file.getAbsolutePath();
        String  fileSep = System.getProperty ("file.separator");
        int     lastSep = absName.lastIndexOf (fileSep);

        return absName.substring (0, lastSep);
    }

    /**
     * Get the base (i.e., simple file) name of a file. This is the file
     * name stripped of any directory information. For instance,
     * "/home/foo.zip" would return "foo.zip".
     *
     * @param fileName name of the file to get the basename for
     *
     * @return file name part of the file
     *
     * @see #dirname(String)
     */
    public static String basename (String fileName)
    {
        String  fileSep = System.getProperty ("file.separator");
        int     lastSep = fileName.lastIndexOf (fileSep);

        if (lastSep == -1)
            return fileName;
        else
            return fileName.substring (lastSep + 1);
    }

    /**
     * Get the base (i.e., simple file) name of a file. This is the file
     * name stripped of any directory information. For instance,
     * "/home/foo.zip" would return "foo.zip".
     *
     * @param file  the file to get the basename for
     *
     * @return file name part of the file
     *
     * @see #basename(String)
     * @see #dirname(File)
     */
    public static String basename (File file)
    {
        return basename (file.getName());
    }
}
