/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.io.test;

import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.oro.io.*;
import org.apache.oro.text.*;
import org.apache.oro.text.regex.*;
import org.clapper.util.io.*;

/**
 *
 * @version <tt>$Revision$</tt>
 */
public class FindFiles
{
    /*----------------------------------------------------------------------*\
                             Static Variables
    \*----------------------------------------------------------------------*/

    private static String directory = ".";

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
    {
        FindFiles   prog           = new FindFiles();
        Collection  acceptPatterns = new ArrayList();
        Collection  rejectPatterns = new ArrayList();

        try
        {
            parseParams (args, acceptPatterns, rejectPatterns);
            prog.findFiles (acceptPatterns, rejectPatterns);
        }

        catch (MalformedPatternException ex)
        {
            System.err.println (ex);
            System.exit (1);
        }
    }

    private static void parseParams (String[]   args,
                                     Collection acceptPatterns,
                                     Collection rejectPatterns)
        throws MalformedPatternException
    {
        try
        {
            int i = 0;
            while ((i < args.length) && (args[i].startsWith ("-")))
            {
                if (args[i].equals ("-e"))
                    rejectPatterns.add (args[++i]);

                else if (args[i].equals ("-i"))
                    acceptPatterns.add (args[++i]);

                else
                    throw new IllegalArgumentException (args[i]);

                i++;
            }

            int argsLeft = args.length - i;

            if (argsLeft > 1)
            {
                System.err.println ("Too many arguments.");
                usage();
                System.exit (1);
            }

            if (argsLeft == 1)
                directory = args[i];
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
    }

    private static void usage()
    {
        System.err.println ("Usage: "
                          + FindFiles.class.getName()
                          + " [-e exclude_pattern] ... "
                          + " [-i include_pattern] ... "
                          + "[directory]");
    }

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    FindFiles()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private void findFiles (Collection acceptPatterns,
			    Collection rejectPatterns)
        throws MalformedPatternException
    {
        MultipleRegexFilenameFilter filter;
        Iterator                    it;
	RecursiveFileFinder         finder = new RecursiveFileFinder();
	Collection                  files  = new ArrayList();

	filter = new MultipleRegexFilenameFilter
	                       (MultipleRegexFilenameFilter.MATCH_PATH);

        for (it = acceptPatterns.iterator(); it.hasNext(); )
            filter.addAcceptPattern ((String) it.next());

        for (it = rejectPatterns.iterator(); it.hasNext(); )
            filter.addRejectPattern ((String) it.next());

        finder.findFiles (new File (directory), filter, files);

	if (files.size() == 0)
	    System.out.println ("*** No matches.");
	else
        {
            for (it = files.iterator(); it.hasNext(); )
                System.out.println (it.next());
	}    
    }
}
