/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc.test;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

import org.clapper.util.misc.FileHashMap;
import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.text.DecimalFormat;

/**
 * Tester for <tt>FileHashMap</tt> class. Invoke with no parameters for
 * usage summary.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see FileHashMap
 */
public class TestFileHashMap
    extends CommandLineUtility
{
    private static final int       DEFAULT_ENTRY_PADDING = 1024;

    private static String          filePrefix;
    private static long            startTime;
    private static int             fileHashMapFlags = 0;
    private static int             paddingSize = DEFAULT_ENTRY_PADDING;
    private static int             totalValues = 0;
    private static boolean         verbose = false;

    // What gets put into the hash tables. We want something larger than
    // the typical object.

    private static class Entry implements Serializable
    {
        private int paddingSize;
        private String value;
        private byte padding[];

        Entry (String value, int paddingSize)
        {
            this.paddingSize = paddingSize;
            this.value       = value;
            this.padding     = new byte[paddingSize];
        }

        public String toString()
        {
            return new String ("[value=\"" + value + "\", "
                               + "padding=" + paddingSize + " bytes]");
        }
    }

    public static void main (String args[])
    {
        TestFileHashMap tester = new TestFileHashMap();

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

    private TestFileHashMap()
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

    protected void parseCustomOption (char     shortOption,
                                      String   longOption,
                                      Iterator it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        switch (shortOption)
        {
            case 'n':
                fileHashMapFlags |= FileHashMap.NO_CREATE;
                break;

            case 'o':
                fileHashMapFlags |= FileHashMap.FORCE_OVERWRITE;
                break;

            case 'p':
                paddingSize = parseIntParameter ((String) it.next());
                break;

            case 't':
                fileHashMapFlags |= FileHashMap.TRANSIENT;
                break;

            case 'v':
                verbose = true;
                break;

            default:
                throw new CommandLineUsageException ("Unrecognized option");
        }
    }
    
    protected void processPostOptionCommandLine (Iterator it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        totalValues = parseIntParameter ((String) it.next());

        if (it.hasNext())
        {
            filePrefix = (String) it.next();
        }

        else if ((fileHashMapFlags & FileHashMap.TRANSIENT) == 0)
        {
            throw new CommandLineUsageException ("Must specify a file prefix "
                                               + "unless -t is specified.");
        }
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addOption ('n', "no-create",
                        "Set the NO_CREATE flag on the FileHashMap "
                      + "constructor");
        info.addOption ('p', "padding",
                        "Specify the number of bytes by which to pad each "
                      + "entry. This is useful for increasing the footprint "
                      + "of each entry, to test the difference between an "
                      + "in-memory and a disk resident map. Default value is "
                      + DEFAULT_ENTRY_PADDING);
        info.addOption ('o', "overwrite",
                        "Set the FORCE_OVERWRITE flag on the FileHashMap "
                      + "constructor");
        info.addOption ('t', "transient", "Use a transient hash map");
        info.addOption ('v', "verbose", "Enable verbose messages");

        info.addParameter ("totalEntries",
                           "Total number of keys and synthesized values to "
                         + "stuff in the hash table.",
                           true);
        info.addParameter ("filePrefix",
                           "File prefix to use. Required unless -t is "
                         + "specified.",
                           false);
    }

    private void runTest()
        throws IOException,
               ClassNotFoundException,
               ObjectExistsException,
               VersionMismatchException
    {
        HashMap      memoryHash = null;
        FileHashMap  fileHash = null;
        long         ms;

        try
        {
            msgln ("RUNNING TEST");
            msgln ("");
            msgln ("Total entries to be inserted: " + totalValues);
            msgln ("Element padding size:         " + paddingSize +
                   " bytes");
            msgln ("--------------------------------------------------");
            msgln ("Creating in-memory hash table ...");
            startTimer();
            memoryHash = new HashMap();
            ms = stopTimer();
            msgln ("Done. Elapsed time: " + ms + " ms");

            msgln ("");
            msgln ("--------------------------------------------------");
            msgln ("Creating on-disk hash table ... ");
            startTimer();

            if ((fileHashMapFlags & FileHashMap.TRANSIENT) != 0)
            {
                if (filePrefix == null)
                    fileHash = new FileHashMap();
                else
                    fileHash = new FileHashMap (filePrefix);
            }

            else
            {
                fileHash = new FileHashMap (filePrefix, fileHashMapFlags);
            }

            ms = stopTimer();
            msgln ("Done. Elapsed time: " + ms + " ms");

            if (fileHash.size() > 0)
            {
                msgln ("--------------------------------------------------");
                msgln ("File hash isn't empty. Dumping it.");
                dumpTableByKeySet (fileHash, "On-disk table");
            }

            fillTables (memoryHash, fileHash);
            dumpTables (memoryHash, fileHash);

            msgln ("");
            msgln ("--------------------------------------------------");
            msgln ("Closing on-disk hash table ... ");
            startTimer();
            fileHash.close();
            ms = stopTimer();
            msgln ("Done. Elapsed time: " + ms + " ms");
        }

        finally
        {
            if (fileHash != null)
            {
                fileHash.close();
                fileHash = null;
            }
        }
    }

    private void fillTables (Map memoryHash, Map fileHash)
    {
        fillTable (memoryHash, "in-memory table");
        fillTable (fileHash, "on-disk table");
    }

    private void fillTable (Map map, String mapName)
    {
        long  msAccum = 0;
        int   i;

        // Make keys at least five characters long.

        DecimalFormat keyFormatter = new DecimalFormat ("#00000");

        msgln ("");
        msgln ("Filling " + mapName + " table ...");
        for (i = 1; i <= totalValues; i++)
        {
            String key = keyFormatter.format (new Integer (i));
            Object old;
            long   ms;
            Entry  value = new Entry (key, paddingSize);

            verboseln ("    Putting key=\""
                       + key
                       + "\", value="
                       + value.toString()
                       + " into "
                       + mapName);

            startTimer();
            old = map.put (key, value);
            ms = stopTimer();
            verboseln ("    Elapsed time: " + ms + " ms");
            if (old != null)
                verboseln ("    (previous value was \"" + old + "\")");

            msAccum += ms;
        }

        msgln ("(Done.)");
        msgln ("Total entries:             " + totalValues);
        msgln ("Total insert time:         " + msAccum + " ms");
        msgln ("Avg insert time per entry: "
               + getAverage (msAccum, i) + " ms");
    }

    private void dumpTables (Map memoryHash, Map fileHash)
    {
        msgln ("");
        msgln ("--------------------------------------------------");
        msgln ("Walking both hash tables using a key set.");
        dumpTableByKeySet (memoryHash, "In-memory table");
        dumpTableByKeySet (fileHash, "On-disk table");

        msgln ("");
        msgln ("--------------------------------------------------");
        msgln ("Walking both hash tables using a value set.");
        dumpTableByValueSet (memoryHash, "In-memory table");
        dumpTableByValueSet (fileHash, "On-disk table");

        msgln ("");
        msgln ("--------------------------------------------------");
        msgln ("Walking both hash tables using an entry set.");
        dumpTableByEntrySet (memoryHash, "In-memory table");
        dumpTableByEntrySet (fileHash, "On-disk table");

        msgln ("");
        msgln ("--------------------------------------------------");
        msgln ("Walking both hash tables non-sequentially.");
        dumpTableNonSequentially (memoryHash, "In-memory table");
        dumpTableNonSequentially (fileHash, "On-disk table");
    }

    private void dumpTableByKeySet (Map map, String label)
    {
        if (map.size() == 0)
            msgln (label + " is empty");

        else
        {
            Iterator  it;
            int       i;
            long      ms;

            msgln ("");
            msgln (label);
            verboseln ("(key -> value)");
            ms = 0;
            for (i = 0, it = map.keySet().iterator(); it.hasNext(); i++)
            {
                Object key = it.next();
                startTimer();
                Entry value = (Entry) map.get (key);
                ms += stopTimer();

                verboseln ("    \"" + key + "\" -> \"" + value + "\"");
            }

            msgln ("");
            msgln ("Total entries:             " + i);
            msgln ("Total access time:         " + ms + " ms");
            msgln ("Avg access time per entry: "
                   + getAverage (ms, i) + " ms");

            if (map.size() != i)
            {
                throw new IllegalStateException ("Dumped more values "
                                                 + "than table says it "
                                                 + "contains! map.size() "
                                                 + "returns "
                                                 + map.size());
            }
        }
    }

    private void dumpTableByValueSet (Map map, String label)
    {
        if (map.size() == 0)
            msgln (label + " is empty");

        else
        {
            Iterator  it;
            int       i;
            long      ms;

            msgln ("");
            msgln (label);
            verboseln ("(values only):");
            ms = 0;
            for (i = 0, it = map.values().iterator(); it.hasNext(); i++)
            {
                startTimer();
                Object value = it.next();
                ms += stopTimer();

                verboseln ("    \"" + value + "\"");
            }

            msgln ("");
            msgln ("Total entries:             " + i);
            msgln ("Total access time:         " + ms + " ms");
            msgln ("Avg access time per entry: "
                   + getAverage (ms, i) + " ms");

            if (map.size() != i)
            {
                throw new IllegalStateException ("Dumped more values "
                                                 + "than table says it "
                                                 + "contains! map.size() "
                                                 + "returns "
                                                 + map.size());
            }
        }
    }

    private void dumpTableByEntrySet (Map map, String label)
    {
        if (map.size() == 0)
            msgln (label + " is empty");

        else
        {
            Iterator  it;
            int       i;
            long      ms;

            msgln ("");
            msgln (label);
            verboseln ("(key -> value)");
            ms = 0;
            for (i = 0, it = map.entrySet().iterator(); it.hasNext(); i++)
            {
                startTimer();
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                ms += stopTimer();

                verboseln ("    \"" + key + "\" -> \"" + value + "\"");
            }

            msgln ("");
            msgln ("Total entries:             " + i);
            msgln ("Total access time:         " + ms + " ms");
            msgln ("Avg access time per entry: "
                   + getAverage (ms, i) + " ms");

            if (map.size() != i)
            {
                throw new IllegalStateException ("Dumped more values "
                                                 + "than table says it "
                                                 + "contains! map.size() "
                                                 + "returns "
                                                 + map.size());
            }
        }
    }

    private void dumpTableNonSequentially (Map map, String label)
    {
        // Try to dump the table in a way that maximizes seeking on
        // the disk. We keep two pointers: One in the middle, and
        // one at the top. We'll alternate between them.

        Object  keys[] = map.keySet().toArray();
        int     top    = 0;
        int     middle = keys.length / 2;
        int     m      = middle;
        long    ms     = 0;
        int     total  = 0;

        while ( (top < middle) || (m < keys.length) )
        {
            Object key;
            Object value;

            if (top < middle)
            {
                key = keys[top];
                startTimer();
                value = map.get (key);
                ms += stopTimer();
                verboseln ("(top) Key #" + top + "=\"" + key + "\" -> \""
                           + value + "\"");
                total++;
                top++;
            }

            if (m < keys.length)
            {
                key = keys[m];
                startTimer();
                value = map.get (key);
                ms += stopTimer();
                verboseln ("(mid) Key #" + m + "=\"" + key + "\" -> \""
                           + value + "\"");
                total++;
                m++;
            }
        }

        if (total != keys.length)
        {
            throw new IllegalStateException ("Dumped "
                                             + total
                                             + " values, but table "
                                             + " contains "
                                             + map.size()
                                             + "entries.");
        }

        msgln ("");
        msgln ("Total entries:             " + total);
        msgln ("Total access time:         " + ms + " ms");
        msgln ("Avg access time per entry: "
               + getAverage (ms, total) + " ms");

    }


    private String getAverage (long ms, int total)
    {
        StringBuffer  result = new StringBuffer();

        result.append (ms / total);
        result.append (".");
        result.append (ms % total);

        return result.toString();
    }

    private void startTimer()
    {
        startTime = System.currentTimeMillis();
    }

    private long stopTimer()
    {
        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    private void msgln (String s)
    {
        if (s != null)
            System.out.print (s);
        System.out.println();
        System.out.flush();
    }

    private void msg (String s)
    {
        System.out.print (s);
        System.out.flush();
    }

    private void verbose (String s)
    {
        if (verbose)
            msg (s);
    }

    private void verboseln (String s)
    {
        if (verbose)
            msgln (s);
    }
}
