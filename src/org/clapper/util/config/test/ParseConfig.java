/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config.test;

import java.util.*;
import org.clapper.util.text.*;
import org.clapper.util.io.*;
import org.clapper.util.config.*;
import java.io.*;
import java.net.*;

/**
 * <p>Test the <code>Configuration</code> class.</p>
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
	if (args.length < 1)
            usage();

        String file = args[0];
        Collection vars = new ArrayList();
        for (int i = 1; i < args.length; i++)
            vars.add (args[i]);

        try
        {
            ParseConfig tester = new ParseConfig();
            tester.runTest (args[0], vars);
            System.exit (0);
        }

        catch (ConfigurationException ex)
        {
            new WordWrapWriter (System.err).println (ex.getMessage());
            System.exit (1);
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit (1);
        }
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private static void usage()
    {
        System.err.println ("Usage: java " + ParseConfig.class +
                            " file|url [section:var=value] ... ");
        System.exit (1);
    }

    private void runTest (String thing, Collection vars)
        throws FileNotFoundException,
               IOException,
	       ConfigurationException,
               SectionExistsException,
               NoSuchElementException,
               VariableSubstitutionException
    {
        Configuration config = null;

	try
        {
            config = new Configuration (new URL (thing));
        }

        catch (MalformedURLException ex)
        {
            config = new Configuration (new File (thing));
        }

        for (Iterator it = vars.iterator(); it.hasNext(); )
        {
            String s = (String) it.next();

            int i = s.indexOf (':');
            if (i == -1)
            {
                throw new ConfigurationException ("Bad variable setting: \""
                                                + s
                                                + "\"");
            }

            String section = s.substring (0, i);

            i++;
            int j = s.indexOf ('=', i);
            if (i == -1)
            {
                throw new ConfigurationException ("Bad variable setting: \""
                                                + s
                                                + "\"");
            }

            String varName = s.substring (i, j);
            String value = s.substring (j + 1);

            if (! config.containsSection (section))
                config.addSection (section);

            config.setVariable (section, varName, value, true);
        }

        config.write (System.out);
    }
}
