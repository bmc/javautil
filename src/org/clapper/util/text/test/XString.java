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
public class XString extends CommandLineUtility
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    private enum Type {BUILDER, BUFFER};

    private Type type = Type.BUILDER;
    private Collection<String> strings = new ArrayList<String>();

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor.
     */
    private XString()
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
        XString tester = new XString();
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
            case 't':
                String sType = it.next();
                if (sType.equals ("XStringBuffer"))
                    type = Type.BUFFER;
                else if (sType.equals ("XStringBuilder"))
                    type = Type.BUILDER;
                else
                {
                    throw new CommandLineUsageException ("Bad value of \"" +
                                                         type +
                                                         "\" for -t");
                }
                break;

            default:
                throw new CommandLineUsageException ("Unrecognized option");
        }
    }
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        do
        {
            strings.add (it.next());
        }
        while (it.hasNext());
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addOption ('t', "type", "XStringBuffer|XStringBuilder",
                        "Specify the type of object to test. Defaults to " +
                        "XStringBuilder");

        info.addParameter ("string ...",
                           "String to append to buffer. May be specified " +
                           "more than once.",
                           true);
    }


    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private void runTest()
        throws PatternSyntaxException
    {
        XStringBufBase buf = null;

        switch (type)
        {
            case BUFFER:
                buf = new XStringBuffer();
                break;

            case BUILDER:
                buf = new XStringBuilder();
                break;

            default:
                assert (false);
        }

        // Assemble a "normal" StringBuilder, for comparison

        StringBuilder control = new StringBuilder();
        for (String s : strings)
            control.append (s);

        System.out.println ("Testing " + buf.getClass());

        System.out.println ();
        System.out.println ("Testing append(String)");
        for (String s : strings)
        {
            System.out.println ("Appending \"" + s + "\"");
            System.out.println ("Before: \"" + buf + "\"");
            buf.append (s);
            System.out.println ("After: \"" + buf + "\"");
        }

        assert (control.toString().equals (buf.toString()));

        buf.clear();
        System.out.println ();
        System.out.println ("Testing append(char)");
        for (String s : strings)
        {
            System.out.println ("Appending \"" + s + "\"");
            System.out.println ("Before: \"" + buf + "\"");

            char[] ch = s.toCharArray();
            for (int i = 0; i < ch.length; i++)
                buf.append (ch[i]);

            System.out.println ("After: \"" + buf + "\"");
        }

        assert (control.toString().equals (buf.toString()));
    }
}
