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

package org.clapper.util.io.test;

import java.io.*;
import java.util.*;
import org.clapper.util.io.*;
import org.clapper.util.text.*;

/**
 *
 * @version <tt>$Revision$</tt>
 */
public class TestWordWrapWriter
{
    /*----------------------------------------------------------------------*\
                             Static Variables
    \*----------------------------------------------------------------------*/

    private static String prefix = null;
    private static int indent = 0;
    private static int lineLen = WordWrapWriter.DEFAULT_LINE_LENGTH;
    private static JustifyTextWriter jtWriter = null;
    private static int justification = -1;
    private static InputStream in = System.in;

    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                               Main Program
    \*----------------------------------------------------------------------*/

    public static void main (String args[])
        throws Throwable
    {
        parseParams (args);

        WordWrapWriter wOut;

        if (jtWriter != null)
            wOut = new WordWrapWriter (jtWriter, lineLen);
        else
            wOut = new WordWrapWriter (System.out, lineLen);

        wOut.setPrefix (prefix);
        wOut.setIndentation (indent);

        LineNumberReader r = new LineNumberReader
                                     (new InputStreamReader (in));
        String line;
        String sep = "";

        while ((line = r.readLine()) != null)
        {
            if (line.length() == 0)
            {
                sep = "";
                wOut.println();
                wOut.println();
                wOut.flush();
            }

            else
            {
                String[] words = TextUtil.split (line);
                for (int i = 0; i < words.length; i++)
                {
                    wOut.print (sep);
                    wOut.print (words[i]);
                    sep = " ";
                }
            }
        }

        wOut.flush();
    }

    private static void parseParams (String[] args)
    {
        try
        {
            int i = 0;
            while ((i < args.length) && (args[i].startsWith ("-")))
            {
                if (args[i].equals ("-p"))
                    prefix = args[++i];

                else if (args[i].equals ("-i"))
                    indent = Integer.parseInt (args[++i]);

                else if (args[i].equals ("-l"))
                    lineLen = Integer.parseInt (args[++i]);

                else if (args[i].equals ("-R"))
                    justification = JustifyTextWriter.RIGHT_JUSTIFY;

                else if (args[i].equals ("-C"))
                    justification = JustifyTextWriter.CENTER;

                else if (args[i].equals ("-L"))
                    justification = JustifyTextWriter.LEFT_JUSTIFY;

                else
                    throw new IllegalArgumentException (args[i]);

                i++;
            }

            if (i < args.length)
                in = new FileInputStream (args[i++]);

            if (i < args.length)
            {
                System.err.println ("Too many arguments.");
                usage();
                System.exit (1);
            }

            if (justification != -1)
            {
                jtWriter = new JustifyTextWriter (System.out,
                                                  justification,
                                                  lineLen);
            }
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

        catch (NumberFormatException ex)
        {
            System.err.println ("Bad argument to numeric option: "
                              + ex.toString());
            usage();
            System.exit (1);
        }

        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit (1);
        }
    }

    private static void usage()
    {
        WordWrapWriter err = new WordWrapWriter (System.err);

        err.setPrefix ("Usage: ");
        err.println ("java "
                   + TestWordWrapWriter.class.getName()
                   + " [OPTIONS]");

        err.setPrefix (null);
        String[] remainder = new String[]
        {
            "",
            "OPTIONS",
            "",
            "-p <prefix>    Set the WordWrapWriter prefix to <prefix>",
            "               Default: None.",
            "-i <n>         Set the WordWrapWriter indentation to <n>",
            "               Default: 0",
            "-l <n>         Set the WordWrapWriter line length to <n>",
            "               Default: " + WordWrapWriter.DEFAULT_LINE_LENGTH,
            "-R             Use a JustifyTextWriter to right-justify the",
            "               wrapped lines.",
            "-C             Use a JustifyTextWriter to center the",
            "               wrapped lines.",
            "-L             Use a JustifyTextWriter to left-justify the",
            "               wrapped lines."
        };

        for (int i = 0; i < remainder.length; i++)
            err.println (remainder[i]);
    }
}
