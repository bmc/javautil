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

import org.clapper.util.misc.MultiIterator;
import org.clapper.util.misc.EnumerationIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

/**
 *
 * @version <tt>$Revision$</tt>
 */
public class TestMultiIterator extends CommandLineUtility
{
    private Collection<String> args = new ArrayList<String>();

    public static void main (String args[])
    {
        TestMultiIterator tester = new TestMultiIterator();

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

    private TestMultiIterator()
    {
        super();
    }

    protected void runCommand()
        throws CommandLineException
    {
        MultiIterator<String> mit = new MultiIterator<String>();

        mit.addCollection (args);
        mit.addIterator (args.iterator());

        Vector<String> v = new Vector<String> (args);
        mit.addEnumeration (v.elements());

        for (String s : mit)
            System.out.println (s);
    }

    protected void parseCustomOption (char             shortOption,
                                      String           longOption,
                                      Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        throw new IllegalStateException ("(BUG) Unknown option: " +
                                         shortOption);
    }
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        do
        {
            args.add (it.next());
        }
        while (it.hasNext());
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addParameter ("string", "A string to print", true);
    }
}
