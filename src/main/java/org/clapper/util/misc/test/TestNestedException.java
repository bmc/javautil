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

package org.clapper.util.misc.test;

import java.io.IOException;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Locale;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

import org.clapper.util.misc.NestedException;

/**
 * @version <tt>$Revision$</tt>
 */
public class TestNestedException extends CommandLineUtility
{
    private String language = null;
    private String country  = null;

    private static Locale locale   = null;

    public static void main (String args[])
    {
        TestNestedException tester = new TestNestedException();

        try
        {
            tester.execute (args);
        }

        catch (CommandLineUsageException ex)
        {
            // Already reported

            System.exit (1);
        }

        catch (Exception ex)
        {
            if (ex instanceof NestedException)
            {
                NestedException nex = (NestedException) ex;
                System.err.println (nex.getMessages (false, locale));
                nex.printStackTrace (System.err, locale);
            }
            else
            {
                System.err.println (ex.getMessage());
                ex.printStackTrace (System.err);
            }

            System.exit (1);
        }
    }

    private TestNestedException()
    {
        super();
    }

    protected void processPostOptionCommandLine (Iterator it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        language = (String) it.next();
        country  = (String) it.next();
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addParameter ("<language>", "Locale language to use for errors.",
                           true);
        info.addParameter ("<country>", "Locale country code.",
                           true);
    }

    protected void runCommand()
        throws CommandLineException
    {
        locale = new Locale (language, country);

        try
        {
            foo();
        }

        catch (Exception ex)
        {
            throw new CommandLineException ("org.clapper.util.misc.test.Test",
                                            "error",
                                            "Error (default)",
                                            ex);
        }
    }

    private void foo()
        throws CommandLineException
    {
        try
        {
            throw new IOException ("Oops.");
        }

        catch (IOException ex)
        {
            throw new CommandLineException ("org.clapper.util.misc.test.Test",
                                            "pageNotFound",
                                            "default message",
                                            ex);
        }
    }
}
