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

import org.clapper.util.misc.MIMETypeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

/**
 *
 * @version <tt>$Revision$</tt>
 */
public class TestMIMEType
    extends CommandLineUtility
{
    private Collection<String> arguments = new ArrayList<String>();

    public static void main (String args[])
    {
        TestMIMEType tester = new TestMIMEType();

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

    private TestMIMEType()
    {
        super();
    }

    protected void runCommand()
        throws CommandLineException
    {
        for (Iterator it = arguments.iterator(); it.hasNext(); )
        {
            String arg = (String) it.next();
            String mimeType;
            String fileName;
            String ext;
            File   f = new File (arg);

            System.out.println ();
            if (f.exists())
            {
                System.out.println ("File name:                " + arg);
                mimeType = MIMETypeUtil.MIMETypeForFileName (arg);
                fileName = arg;
                ext = MIMETypeUtil.fileExtensionForMIMEType (mimeType);
            }

            else
            {
                mimeType = arg;
                ext = MIMETypeUtil.fileExtensionForMIMEType (mimeType);
                fileName = "test." + ext;
            }

            String mimeType2 = MIMETypeUtil.MIMETypeForFileName (fileName);

            System.out.println ("MIME type:                " + mimeType);
            System.out.println ("Extension:                " + ext);
            System.out.println ("Mapped back to MIME type: " + mimeType2);
        }
    }

    protected void parseCustomOption (char             shortOption,
                                      String           longOption,
                                      Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        throw new CommandLineUsageException ("Unrecognized option");
    }
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        do
        {
            arguments.add (it.next());
        }
        while (it.hasNext());
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addParameter ("mimeType|filename ...",
                           "A MIME or file name type to test. " +
                           "May be specified multiple times.",
                           true);
    }
}
