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

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.clapper.util.io.RollingFileWriter;

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
public class RollingFile 
    extends CommandLineUtility
    implements RollingFileWriter.RolloverCallback
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    private String rollOverMsg     = null;
    private long   maxSize         = 0;
    private String fileNamePattern = null;
    private int    maxFiles        = 0;
    private int    totalLines      = Integer.MAX_VALUE;
    private RollingFileWriter.Compression compressionType =
        RollingFileWriter.Compression.DONT_COMPRESS_BACKUPS;

    private SimpleDateFormat  dateFormat  =
                              new SimpleDateFormat ("dd-MMM-yyyy HH:mm:ss");

    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                               Main Program
    \*----------------------------------------------------------------------*/

    public static void main (String args[])
    {
        RollingFile tester = new RollingFile();

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
                                Constructor
    \*----------------------------------------------------------------------*/

    private RollingFile()
    {
        super();
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    public String getRollOverMessage()
    {
        if (rollOverMsg != null)
            return dateFormat.format (new Date()) + " " + rollOverMsg;
        else
            return null;
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    protected void runCommand()
        throws CommandLineException
    {
        try
        {
            RollingFileWriter w = new RollingFileWriter (fileNamePattern,
                                                         null,
                                                         maxSize,
                                                         maxFiles,
                                                         compressionType,
                                                         this);

            for (int i = 0; i < totalLines; i++)
            {
                w.print (dateFormat.format (new Date()));
                w.println (" Test message " + i);
            }
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit (1);
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
                this.totalLines = parseIntOptionArgument (shortOption,
                                                          longOption,
                                                          it.next());
                break;

            case 'z':
                compressionType = RollingFileWriter.Compression.COMPRESS_BACKUPS;
                break;

            case 'r':
                this.rollOverMsg = it.next();
                break;

            default:
                throw new CommandLineUsageException
                    ("Unknown option: " +
                     UsageInfo.LONG_OPTION_PREFIX +
                     longOption +
                     " (" +
                     UsageInfo.SHORT_OPTION_PREFIX +
                     shortOption +
                     ")");
        }
    }
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        this.fileNamePattern = it.next();

        String s = it.next();

        try
        {
            this.maxSize = Long.parseLong (s);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException ("Bad value (\"" +
                                                 s +
                                                 "\" for maximum file size.");
        }

        s = it.next();
        try
        {
            maxFiles = Integer.parseInt (s);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException ("Bad value of \"" + s +
                                                 "\" for maximum number " +
                                                 "of files.");
        }

        if (it.hasNext())
            this.rollOverMsg = it.next();
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addOption ('t', "total-lines", "<n>",
                        "Total lines to write. Defaults to MAXINT");

        info.addOption ('r', "rollover-msg", "<s>",
                        "Roll-over message. Defaults to none.");

        info.addOption ('z', "gzip", null,
                        "Gzipped rolled files.");

        info.addParameter ("filenamePattern",
                           "file name pattern to use",
                           true);
        info.addParameter ("maxsize",
                           "max file size, in bytes",
                           true);
        info.addParameter ("maxfiles",
                           "max files",
                           true);
    }
}
