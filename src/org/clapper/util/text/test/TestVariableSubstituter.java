/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.text.test;

import java.util.*;
import org.clapper.util.text.*;
import java.io.*;

/**
 * <p>Test the <code>UnixShellVariableSubstituter</code> class.</p>
 *
 * @see UnixShellVariableSubstituter
 * @see VariableDereferencer
 * @see VariableSubstituter
 * @see java.lang.String
 *
 * @version <kbd>$Revision$</kbd>
 */
public class TestVariableSubstituter
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    private VariableSubstituter vsub = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor.
     */
    private TestVariableSubstituter (VariableSubstituter vsub)
    {
        this.vsub = vsub;
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
	if (args.length < 3)
            usage();

        Collection strings = new ArrayList();

        for (int i = 2; i < args.length; i++)
            strings.add (args[i]);

        TestVariableSubstituter tester;

        VariableSubstituter vs = null;

        if (args[0].equals ("unix"))
            vs = new UnixShellVariableSubstituter();
        else if (args[0].equals ("windows"))
            vs = new WindowsCmdVariableSubstituter();
        else
            usage();

        try
        {
            tester = new TestVariableSubstituter (vs);
            tester.runTest (args[1], strings);
            System.exit (0);
        }

        catch (IOException ex)
        {
            System.err.println (ex.getMessage());
            System.exit (1);
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
                          + TestVariableSubstituter.class
                          + " unix|windows properties_file string ...");
        System.err.println ("Uses values from System.properties and "
                          + "the specified properties files.");
        System.exit (1);
    }

    private void runTest (String              propertiesFile,
                          Collection          strings)
        throws FileNotFoundException,
               IOException,
               VariableSubstitutionException
    {
        InputStream  in         = new FileInputStream (propertiesFile);
        Properties   properties = new Properties();

        properties.load (in);
        in.close();

        MapVariableDereferencer vderef;

        properties.putAll (System.getProperties());
        vderef = new MapVariableDereferencer (properties);

        for (Iterator it = strings.iterator(); it.hasNext(); )
        {
            String s  = (String) it.next();
            String s2 = vsub.substitute (s, vderef);

            System.out.println();
            System.out.println ("BEFORE: \"" + s + "\"");
            System.out.println ("AFTER:  \"" + s2 + "\"");
        }
    }
}
