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

import org.clapper.util.text.*;
import java.util.regex.*;
import java.util.*;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.UsageInfo;

/**
 * <p>Test the <tt>TextUtil</tt> class's <tt>split()</tt> methods.</p>
 *
 * @version <kbd>$Revision$</kbd>
 */
public class SplitString extends CommandLineUtility
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    private String delims = null;
    private boolean useRegexp = false;
    private int limit = 0;
    private String[] strings = null;
    private boolean preserveEmpty = false;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor.
     */
    private SplitString()
    {
        super();
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
        SplitString tester = new SplitString();
        try
        {
            tester.execute (args);
        }

        catch (CommandLineUsageException ex)
        {
            // Already reported

            System.exit (1);
        }

        catch (CommandLineException ex)
        {
            System.err.println (ex.getMessage());
            ex.printStackTrace();
            System.exit (1);
        }

        catch (Exception ex)
        {
            ex.printStackTrace (System.err);
            System.exit (1);
        }
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    protected void runCommand()
        throws CommandLineException
    {
        try
        {
            runTest();
        }

        catch (Exception ex)
        {
            throw new CommandLineException (ex);
        }
    }

    protected void parseCustomOption (char             shortOption,
                                      String           longOption,
                                      Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        switch (shortOption)
        {
            case 'l':
                limit = parseIntOptionArgument (shortOption,
                                                longOption,
                                                it.next());
                break;

            case 'r':
                delims = it.next();
                useRegexp = true;
                break;

            case 'd':
                delims = it.next();
                useRegexp = false;
                break;

            case 'p':
                preserveEmpty = true;
                break;

            default:
                throw new CommandLineUsageException ("Unrecognized option");
        }
    }
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        Collection<String> temp = new ArrayList<String>();

        do
        {
            temp.add (it.next());
        }
        while (it.hasNext());

        strings = new String[temp.size()];

        int i;
        for (i = 0, it = temp.iterator(); it.hasNext(); i++)
            strings[i] = it.next();
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addOption ('l', "limit", "n",
                        "Set the split limit to <n>. If not specified, all " +
                        "instances of the pattern are used. Only applicable " +
                        "when -r is used.");
        info.addOption ('r', "regexp", "regexp",
                        "Regular expression to use to split the string. " +
                        "If neither -r nor -s is specified, white space " +
                        "is used.");
        info.addOption ('p', "preserve",
                        "Preserve empty strings (i.e., don't parse through " +
                        "adjacent delimiters). Defaults to off.");
        info.addOption ('d', "delims", "delims",
                        "Set of delimiters to use to split the strings. " +
                        "If neither -r nor -s is specified, white space " +
                        "is used.");

        info.addParameter ("string ...",
                           "String to split. May be specified more than once.",
                           true);
    }


    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private void runTest()
        throws PatternSyntaxException
    {
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

            if (useRegexp)
                splitStrings = strings[i].split(delims, limit);

            else
            {
                if (delims == null)
                    delims = " \t\n\r";

                // Explicitly call the other methods, to test them.

                if (preserveEmpty)
                    splitStrings = TextUtil.split (strings[i],
                                                   delims,
                                                   preserveEmpty);
                else
                    splitStrings = TextUtil.split (strings[i],
                                                   delims);
            }

            System.out.println ();
            System.out.println ("String:   \"" + strings[i] + "\"");
            System.out.print ("Split by: \"" + printableDelims.toString() +
                              "\"");
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
                    System.out.println ("    \"" + splitStrings[j] + "\"");
            }
        }
    }
}
