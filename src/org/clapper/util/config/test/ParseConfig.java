/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config.test;

import java.util.*;
import org.clapper.util.text.*;
import org.clapper.util.config.*;
import java.io.*;
import java.net.*;

/**
 * <p>Test the <code>ConfigurationParser</code> class.</p>
 *
 * @see UnixShellVariableSubstituter
 * @see VariableDereferencer
 * @see VariableSubstituter
 * @see java.lang.String
 *
 * @version <kbd>$Revision$</kbd>
 */
public class ParseConfig
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private ParseConfig()
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
	if (args.length != 1)
            usage();

        try
        {
            ParseConfig tester = new ParseConfig();
            tester.runTest (args[0]);
            System.exit (0);
        }

        catch (IOException ex)
        {
            System.err.println (ex.getMessage());
            System.exit (1);
        }

        catch (ConfigurationException ex)
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
        System.err.println ("Usage: java " + ParseConfig.class + " file|url");
        System.exit (1);
    }

    private void runTest (String thing)
        throws FileNotFoundException,
               IOException,
	       ConfigurationException
    {
        ConfigurationParser parser = null;

	try
        {
            parser = new ConfigurationParser (new URL (thing));
        }

        catch (MalformedURLException ex)
        {
            parser = new ConfigurationParser (new File (thing));
        }

        parser.write (System.out);
    }
}
