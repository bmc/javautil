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

package org.clapper.util.html.test;

import org.clapper.util.html.*;
import java.io.*;

/**
 * Test the <tt>HTMLUtil.convertCharacterEntities()</tt> method.
 *
 * @see HTMLUtil#convertCharacterEntities
 *
 * @version <kbd>$Revision$</kbd>
 */
public class ConvertHTMLCharEntities
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
    private ConvertHTMLCharEntities()
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
        throws IOException
    {
        System.err.println ("Reading and converting standard input.");
        System.err.println ("Writing to standard output.");

        LineNumberReader in = new LineNumberReader
                                 (new InputStreamReader (System.in));
        String line;

        while ((line = in.readLine()) != null)
            System.out.println (HTMLUtil.convertCharacterEntities (line));

        System.exit (0);
    }
}
