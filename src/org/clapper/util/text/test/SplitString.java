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

package org.clapper.util.text.test;

import org.clapper.util.text.*;
import org.apache.oro.text.regex.*;

/**
 * <p>Test the <tt>TextUtil</tt> class's <tt>split()</tt> methods.</p>
 *
 * @version <kbd>$Revision$</kbd>
 */
public class SplitString
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    private String delims = null;
    private boolean useRegexp = false;
    private int limit = TextUtil.SPLIT_ALL;
    private String[] strings = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor.
     */
    private SplitString()
    {
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
        try
        {
            SplitString tester = new SplitString();
            tester.runTest (args);
            System.exit (0);
        }

        catch (IllegalArgumentException ex)
        {
            System.err.println (ex.getMessage());
            usage();
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
        final String[] USAGE = new String[]
        {
"Usage: java " + SplitString.class.getName() + " [OPTIONS] string ...",
"",
"OPTIONS",
"",
"-l <n>             Set the split limit to <n>. If not specified, all ",
"                   instances of the pattern are used. Only applicable when ",
"                   -r is used.",
"-r <regexp>        Regular expression to use to split the strings. If ",
"                   neither -r nor -s is specified, white space is used.",
"-s <delimset>      Set of delimiters to use to split the strings. If ",
"                   neither -s nor -r is specified, white space is used.",
        };

        for (int i = 0; i < USAGE.length; i++)
            System.err.println (USAGE[i]);
    }

    private void parseParams (String[] args)
        throws IllegalArgumentException
    {
        try
        {
            int i = 0;

            while ((i < args.length) && (args[i].startsWith ("-")))
            {
                if (args[i].equals ("-l"))
                {
                    try
                    {
                        limit = Integer.parseInt (args[++i]);
                    }

                    catch (NumberFormatException ex)
                    {
                        throw new IllegalArgumentException ("Bad numeric "
                                                          + "parameter: \""
                                                          + args[i]
                                                          + "\"");
                    }
                }

                else if (args[i].equals ("-r"))
                {
                    delims = args[++i];
                    useRegexp = true;
                }

                else if (args[i].equals ("-s"))
                {
                    delims = args[++i];
                    useRegexp = false;
                }

                else
                {
                    throw new IllegalArgumentException ("Unknown option: "
                                                      + args[i]);
                }

                i++;
            }

            int totalStrings = args.length - i;

            if (totalStrings == 0)
                throw new ArrayIndexOutOfBoundsException();

            strings = new String[totalStrings];

            for (int j = 0; j < strings.length; j++)
                strings[j] = args[i++];
        }

        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new IllegalArgumentException ("Missing parameters(s)");
        }
    }

    private void runTest (String[] args)
        throws MalformedPatternException
    {
        parseParams (args);

        Pattern pattern = null;

        if (useRegexp)
        {
            Perl5Compiler compiler = new Perl5Compiler();
            pattern = compiler.compile (delims);
        }

        XStringBuffer printableDelims = new XStringBuffer();
        if (delims == null)
            printableDelims.append ("white space");

        else
        {
            printableDelims.append (delims);
            printableDelims.encodeMetacharacters();
        }

        for (int i = 0; i < strings.length; i++)
        {
            String[] splitStrings = null;

            if (pattern != null)
                splitStrings = TextUtil.split (strings[i], pattern, limit);
            else if (delims == null)
                splitStrings = TextUtil.split (strings[i]);
            else
                splitStrings = TextUtil.split (strings[i], delims);

            System.out.println ();
            System.out.println ("String:   \""
                              + strings[i]
                              + "\"");
            System.out.print ("Split by: \""
                            + printableDelims.toString()
                            + "\"");
            if (useRegexp)
                System.out.print (" (regular expression)");
            System.out.println();

            if (splitStrings == null)
                System.out.println ("    <null> result");

            else if (splitStrings.length == 0)
                System.out.println ("    empty array result");

            else
            {
                for (int j = 0; j < splitStrings.length; j++)
                    System.out.println ("    " + splitStrings[j]);
            }
        }
    }
}
