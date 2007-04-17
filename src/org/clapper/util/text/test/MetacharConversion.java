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

/**
 * <p>Test the <tt>XStringBuffer</tt> class's metacharacter conversion
 * methods.</p>
 *
 * @see XStringBuffer#encodeMetachars()
 * @see XStringBuffer#decodeMetachars()
 *
 * @version <kbd>$Revision$</kbd>
 */
public class MetacharConversion
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor.
     */
    private MetacharConversion()
    {
        // Nothing to do
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
        if (args.length < 1)
            usage();

        try
        {
            MetacharConversion tester = new MetacharConversion();
            tester.runTest (args);
            System.exit (0);
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
                            MetacharConversion.class +
                            " metachar_string ...");
        System.exit (1);
    }

    private void runTest (String[] args)
    {
        XStringBuffer buf = new XStringBuffer();

        for (int i = 0; i < args.length; i++)
        {
            buf.setLength (0);
            buf.append (args[i]);
            System.out.println ("BEFORE DECODING: \"" + buf + "\"");
            buf.decodeMetacharacters();
            System.out.println ("AFTER DECODING: \"" + buf + "\"");
            buf.encodeMetacharacters();
            System.out.println ("AFTER RE-ENCODING: \"" + buf + "\"");
        }
    }
}
