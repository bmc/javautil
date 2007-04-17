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

package org.clapper.util.regex.test;

import org.clapper.util.regex.RegexUtil;
import org.clapper.util.regex.RegexException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

/**
 *
 * @version <tt>$Revision$</tt>
 */
public class TestRegexUtil
    extends CommandLineUtility
{
    private String substitution;
    private Collection<String> strings = new ArrayList<String>();

    class LineReaderIterator implements Iterator
    {
        LineNumberReader r;
        String nextLine;

        LineReaderIterator (InputStream is)
            throws IOException
        {
            r = new LineNumberReader (new InputStreamReader (is));

            nextLine = r.readLine();
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            return nextLine != null;
        }

        public Object next()
        {
            String lastLine = nextLine;

            try
            {
                if (lastLine != null)
                    nextLine = r.readLine();
            }

            catch (IOException ex)
            {
                System.err.println ("*** " + ex.toString());
                nextLine = null;
            }

            return lastLine;
        }
    }

    public static void main (String args[])
    {
        TestRegexUtil tester = new TestRegexUtil();

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
            ex.printStackTrace();                                    // NOPMD
            System.exit (1);
        }

        catch (Exception ex)
        {
            ex.printStackTrace (System.err);
            System.exit (1);
        }
    }

    private TestRegexUtil()
    {
        super();
    }

    protected void runCommand()
        throws CommandLineException
    {
        try
        {
            RegexUtil ru = new RegexUtil();
            Iterator  it;

            if (strings.size() == 0)
                it = new LineReaderIterator (System.in);
            else
                it = strings.iterator();

            while (it.hasNext())
            {
                String s1 = (String) it.next();
                String s2 = ru.substitute (substitution, s1);

                System.out.println ();
                System.out.println ("Substitution: " + substitution);
                System.out.println ("Before:       " + s1);
                System.out.println ("After:        " + s2);
                System.out.flush();
            }
        }

        catch (IOException ex)
        {
            throw new CommandLineException (ex);
        }

        catch (RegexException ex)
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
        throw new CommandLineUsageException ("Unrecognized option");
    }
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        substitution = (String) it.next();
        while (it.hasNext())
        {
            strings.add (it.next());
        }
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addParameter ("s/re/repl/[g][i][m][o][x]",
                           "Substitution command to execute on strings",
                           true);
        info.addParameter ("[string] ...",
                           "One or more strings to execute substitution " +
                           "command line. If not present, then lines from " +
                           "standard input are read, instead.",
                           false);
    }
}
