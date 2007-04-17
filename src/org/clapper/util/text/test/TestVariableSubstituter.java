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

package org.clapper.util.text.test;

import java.util.*;
import org.clapper.util.text.*;
import java.io.*;

/**
 * <p>Test the <code>UnixShellVariableSubstituter</code> class.</p>
 *
 * @see UnixShellVariableSubstituter
 * @see VariableDereferencer
 * @see VariableSubstituter
 * @see java.lang.String
 *
 * @version <kbd>$Revision$</kbd>
 */
public class TestVariableSubstituter
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    private VariableSubstituter vsub = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor.
     */
    private TestVariableSubstituter (VariableSubstituter vsub)
    {
        this.vsub = vsub;
    }

    /*----------------------------------------------------------------------*\
			       Main Program
    \*----------------------------------------------------------------------*/

    /**
     * Tester for this class. Invoke with no parameters for usage.
     *
     * @param args  Parameters.
     */
    public static void main (String args[])
    {
	if (args.length < 3)
            usage();

        Collection<String> strings = new ArrayList<String>();

        for (int i = 2; i < args.length; i++)
            strings.add (args[i]);

        TestVariableSubstituter tester;

        VariableSubstituter vs = null;

        if (args[0].equals ("unix"))
            vs = new UnixShellVariableSubstituter();
        else if (args[0].equals ("windows"))
            vs = new WindowsCmdVariableSubstituter();
        else
            usage();

        try
        {
            tester = new TestVariableSubstituter (vs);
            tester.runTest (args[1], strings);
            System.exit (0);
        }

        catch (IOException ex)
        {
            System.err.println (ex.getMessage());
            System.exit (1);
        }

        catch (Throwable ex)
        {
            ex.printStackTrace (System.err);
            System.exit (1);
        }
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private static void usage()
    {
        System.err.println ("Usage: java " +
                            TestVariableSubstituter.class +
                            " unix|windows properties_file string ...");
        System.err.println ("Uses values from System.properties and " +
                            "the specified properties files.");
        System.exit (1);
    }

    private void runTest (String              propertiesFile,
                          Collection          strings)
        throws FileNotFoundException,
               IOException,
               VariableSubstitutionException
    {
        InputStream  in         = new FileInputStream (propertiesFile);
        Properties   properties = new Properties();

        properties.load (in);
        in.close();

        MapVariableDereferencer vderef;

        properties.putAll (System.getProperties());
        vderef = new MapVariableDereferencer (properties);

        for (Iterator it = strings.iterator(); it.hasNext(); )
        {
            String s  = (String) it.next();
            String s2 = vsub.substitute (s, vderef, null);

            System.out.println();
            System.out.println ("BEFORE: \"" + s + "\"");
            System.out.println ("AFTER:  \"" + s2 + "\"");
        }
    }
}
