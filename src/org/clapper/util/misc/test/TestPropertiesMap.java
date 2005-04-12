/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc.test;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;
import org.clapper.util.misc.PropertiesMap;
import org.clapper.util.text.XStringBuffer;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

/**
 * Tester for <tt>FileHashMap</tt> class. Invoke with no parameters for
 * usage summary.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see FileHashMap
 */
public class TestPropertiesMap
    extends CommandLineUtility
{
    public boolean useEnvironment = false;

    public static void main (String args[])
    {
        TestPropertiesMap tester = new TestPropertiesMap();

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

    private TestPropertiesMap()
    {
        super();
    }

    protected void runCommand()
        throws CommandLineException
    {
        try
        {
            runTest();
        }

        catch (Exception ex)
        {
            throw new CommandLineException (ex);
        }
    }

    private void runTest()
    {
        PropertiesMap map = new PropertiesMap (System.getProperties());
        XStringBuffer buf = new XStringBuffer();

        System.out.println ("---------------------------------------" +
                            "---------------------------------------");
        System.out.println ("*** Looping over properties by key set.");
        System.out.println ("---------------------------------------" +
                            "---------------------------------------");
        for (String key : map.keySet())
        {
            buf.clear();
            buf.append (map.get (key));
            buf.encodeMetacharacters();
            System.out.println (key + "=" + buf.toString());
        }

        System.out.println ("---------------------------------------" +
                            "---------------------------------------");
        System.out.println ("*** Looping over properties by entry set.");
        System.out.println ("---------------------------------------" +
                            "---------------------------------------");
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            buf.clear();
            buf.append (entry.getValue());
            buf.encodeMetacharacters();
            System.out.println (entry.getKey() + "=" + buf.toString());
        }
    }
}
