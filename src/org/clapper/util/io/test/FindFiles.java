/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.io.test;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.clapper.util.io.*;

/**
 *
 * @version <tt>$Revision$</tt>
 */
public class FindFiles
{
    /*----------------------------------------------------------------------*\
                             Static Variables
    \*----------------------------------------------------------------------*/

    private static String directory = ".";
    private static FileFilterMatchType matchType = FileFilterMatchType.PATH;

    /*----------------------------------------------------------------------*\
                               Main Program
    \*----------------------------------------------------------------------*/

    public static void main (String args[])
    {
        FindFiles           prog           = new FindFiles();
        Collection<String>  acceptPatterns = new ArrayList<String>();
        Collection<String>  rejectPatterns = new ArrayList<String>();

        try
        {
            parseParams (args, acceptPatterns, rejectPatterns);
            prog.findFiles (acceptPatterns, rejectPatterns);
        }

        catch (PatternSyntaxException ex)
        {
            System.err.println (ex);
            System.exit (1);
        }
    }

    private static void parseParams (String[]           args,
                                     Collection<String> acceptPatterns,
                                     Collection<String> rejectPatterns)
        throws PatternSyntaxException
    {
        try
        {
            int i = 0;
            while ((i < args.length) && (args[i].startsWith ("-")))
            {
                if (args[i].equals ("-e"))
                    rejectPatterns.add (args[++i]);

                else if (args[i].equals ("-i"))
                    acceptPatterns.add (args[++i]);

                else if (args[i].equals ("-p"))
                    matchType = FileFilterMatchType.PATH;

                else if (args[i].equals ("-n"))
                    matchType = FileFilterMatchType.FILENAME;

                else
                    throw new IllegalArgumentException (args[i]);

                i++;
            }

            int argsLeft = args.length - i;

            if (argsLeft > 1)
            {
                System.err.println ("Too many arguments.");
                usage();
                System.exit (1);
            }

            if (argsLeft == 1)
                directory = args[i];
        }

        catch (ArrayIndexOutOfBoundsException ex)
        {
            System.err.println ("Missing argument(s)");
            usage();
            System.exit (1);
        }

        catch (IllegalArgumentException ex)
        {
            System.err.println ("Bad option: " + ex.getMessage());
            usage();
            System.exit (1);
        }
    }

    private static void usage()
    {
        System.err.println ("Usage: " +
                            FindFiles.class.getName() +
                            " [-e exclude_pattern] ... " +
                            " [-i include_pattern] ... " +
                            " [-n|-p] " +
                            "[directory]");
    }

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    FindFiles()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private void findFiles (Collection<String> acceptPatterns,
			    Collection<String> rejectPatterns)
        throws PatternSyntaxException
    {
	RecursiveFileFinder finder = new RecursiveFileFinder();
	Collection<File>    files  = new ArrayList<File>();

        System.out.println ("*** Using match type of " + matchType);

        AndFilenameFilter masterFilter = new AndFilenameFilter();

        for (String pattern : acceptPatterns)
        {
            masterFilter.addFilter
                (new RegexFilenameFilter (pattern, matchType));
        }

        for (String pattern : rejectPatterns)
        {
            masterFilter.addFilter
                (new NotFilenameFilter
                    (new RegexFilenameFilter (pattern, matchType)));
        }

        finder.findFiles (new File (directory), masterFilter, files);

	if (files.size() == 0)
	    System.out.println ("*** No matches.");

	else
        {
            for (File file : files)
                System.out.println (file);
	}    
    }
}
