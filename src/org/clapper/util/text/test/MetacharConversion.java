/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.text.test;

import java.util.*;
import org.clapper.util.text.*;
import java.io.*;

/**
 * <p>Test the <code>XStringBuffer</code> class's metacharacter conversion
 * methods.</p>
 *
 * @see XStringBuffer#encodeMetacharConversions()
 * @see XStringBuffer#decodeMetacharConversions()
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
        System.err.println ("Usage: java "
                          + MetacharConversion.class
                          + " metachar_string ...");
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
