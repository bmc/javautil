/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc.test;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;
import org.clapper.util.logging.Logger;
import org.clapper.util.logging.LogLevel;
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
    private static final int           DEFAULT_ENTRY_PADDING = 1024;
    private static final DecimalFormat KEY_FMT = new DecimalFormat ("#00000");
    private static final DecimalFormat AVG_FMT = new DecimalFormat ("#0.000");


    private static String          filePrefix;
    private static long            startTime;
    private static int             fileHashMapFlags = 0;
    private static int             paddingSize = DEFAULT_ENTRY_PADDING;
    private static int             totalValues = 0;
    private static boolean         verbose = false;
    private static boolean         readOnly = false;
    private static boolean         useDisk = true;
    private static boolean         useMemory = true;
    private static boolean         clearFirst = false;
    private static Logger          log = new Logger (TestFileHashMap.class);

    // What gets put into the hash tables. We want something larger than
    // the typical object.

    private static class Entry implements Serializable
    {
        /**
         * See JDK 1.5 version of java.io.Serializable
         */
        private static final long serialVersionUID = 1L;

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
            return new String ("[value=\"" + value + "\", " +
                               "padding=" + paddingSize + " bytes]");
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

    protected void parseCustomOption (char             shortOption,
                                      String           longOption,
                                      Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        switch (shortOption)
        {
            case 'c':
                clearFirst = true;
                break;

            case 'd':
                useMemory = false;
                useDisk = true;
                break;

            case 'g':
                fileHashMapFlags |= FileHashMap.RECLAIM_FILE_GAPS;
                break;

            case 'm':
                useMemory = true;
                useDisk = false;
                break;

            case 'n':
                fileHashMapFlags |= FileHashMap.NO_CREATE;
                break;

            case 'o':
                fileHashMapFlags |= FileHashMap.FORCE_OVERWRITE;
                break;

            case 'p':
                paddingSize = parseIntParameter (it.next());
                break;

            case 'r':
                readOnly = true;
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
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        totalValues = parseIntParameter ((String) it.next());

        if (it.hasNext())
        {
            filePrefix = it.next();
        }

        else if ((fileHashMapFlags & FileHashMap.TRANSIENT) == 0)
        {
            throw new CommandLineUsageException
                ("Must specify a file prefix unless -t is specified.");
        }

        if (readOnly && ((fileHashMapFlags & FileHashMap.TRANSIENT) != 0))
        {
            throw new CommandLineUsageException
                ("You cannot specify both -r and -t");
        }
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addOption ('c', "clear",
                        "Clear the FileHashMap after loading it from disk " +
                        "initially.");
        info.addOption ('d', "disk-only",
                        "Use a FileHashMap table only. Don't use an " +
                        "in-memory hash table.");
        info.addOption ('g', "reuse-gaps",
                        "Pass the RECLAIM_FILE_GAPS flag to the FileHashMap " +
                        "constructor");
        info.addOption ('m', "mem-only",
                        "Use an in-memory hash table only. Don't use a " +
                        "FileHashMap.");
        info.addOption ('n', "no-create",
                        "Pass the NO_CREATE flag to the FileHashMap " +
                        "constructor");
        info.addOption ('p', "padding", "<n>",
                        "Specify the number of bytes by which to pad each " +
                        "entry. This is useful for increasing the footprint " +
                        "of each entry, to test the difference between an " +
                        "in-memory and a disk resident map. Default value: " +
                        DEFAULT_ENTRY_PADDING);
        info.addOption ('o', "overwrite",
                        "Pass the FORCE_OVERWRITE flag to the FileHashMap " +
                        "constructor");
        info.addOption ('r', "read-only",
                        "Display the contents of an existing map, but don't " +
                        "modify it. Cannot be specified with -t");
        info.addOption ('t', "transient", "Use a transient hash map");
        info.addOption ('v', "verbose", "Enable verbose messages");

        info.addParameter ("totalEntries",
                           "Total number of keys and synthesized values to " +
                           "stuff in the hash table.",
                           true);
        info.addParameter ("filePrefix",
                           "File prefix to use. Required unless -t is " +
                           "specified.",
                           false);
    }

    private void runTest()
        throws IOException,
               ClassNotFoundException,
               ObjectExistsException,
               VersionMismatchException
    {
        HashMap<String, Entry>      memoryHash = null;
        FileHashMap<String, Entry>  fileHash = null;
        long         ms;

        try
        {
            msgln ("RUNNING TEST");
            msgln ("");
            msgln ("Total entries to be inserted: " + totalValues);
            msgln ("Element padding size:         " + paddingSize +
                   " bytes");
            msgln ("----------------------------------------------");

            if (useMemory)
            {
                msgln ("Creating in-memory hash table ...");
                startTimer();
                memoryHash = new HashMap<String, Entry>();
                ms = stopTimer();
                msgln ("Done. Elapsed time: " + ms + " ms");
            }

            if (useDisk)
            {
                msgln ("");
                msgln ("----------------------------------------------");
                msgln ("Creating on-disk hash table ... ");
                startTimer();

                if ((fileHashMapFlags & FileHashMap.TRANSIENT) != 0)
                {
                    if (filePrefix == null)
                        fileHash = new FileHashMap<String, Entry>();
                    else
                        fileHash = new FileHashMap<String, Entry> (filePrefix);
                }

                else
                {
                    fileHash = new FileHashMap<String, Entry> (filePrefix,
                                                                fileHashMapFlags);
                }

                ms = stopTimer();
                msgln ("Done. Elapsed time: " + ms + " ms");

                if (fileHash.size() > 0)
                {
                    msgln ("----------------------------------------------");

                    if (clearFirst)
                    {
                        msgln ("File hash isn't empty. Clearing it.");
                        startTimer();
                        fileHash.clear();
                        ms = stopTimer();
                        msgln ("Done. Elapsed time: " + ms + " ms");
                    }

                    else
                    {
                        msgln ("File hash isn't empty. Dumping it.");
                        dumpTableByKeySet (fileHash, "On-disk table");
                    }
                }
            }

            if (! readOnly)
                fillTables (memoryHash, fileHash);

            dumpTables (memoryHash, fileHash);

            if (useDisk)
            {
                msgln ("");
                msgln ("----------------------------------------------");
                msgln ("Closing on-disk hash table ... ");
                startTimer();
                fileHash.close();
                ms = stopTimer();
                msgln ("Done. Elapsed time: " + ms + " ms");
            }
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

    private void fillTables (Map<String, Entry> memoryHash,
                             Map<String, Entry> fileHash)
    {
        if (memoryHash != null)
            fillTable (memoryHash, "in-memory");

        if (fileHash != null)
            fillTable (fileHash, "on-disk");
    }

    private void fillTable (Map<String, Entry> map, String mapName)
    {
        long  msAccum = 0;
        int   i;

        // Make keys at least five characters long.

        msgln ("");
        msgln ("Filling " + mapName + " table ...");
        for (i = 1; i <= totalValues; i++)
        {
            String key = KEY_FMT.format (new Integer (i));
            Entry old;
            long   ms;
            Entry  value = new Entry (key, paddingSize);

            verboseln ("    Putting key=\"" + key + "\", value=" +
                       value.toString() + " into " + mapName);

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
        msgln ("Avg insert time per entry: " +
               getAverage (msAccum, i) + " ms");
    }

    private void dumpTables (Map<String, Entry> memoryHash,
                             Map<String, Entry> fileHash)
    {
        msgln ("");
        msgln ("----------------------------------------------");
        msgln ("Walking hash table(s) using a key set.");

        if (memoryHash != null)
            dumpTableByKeySet (memoryHash, "In-memory table");

        if (fileHash != null)
            dumpTableByKeySet (fileHash, "On-disk table");

        msgln ("");
        msgln ("----------------------------------------------");
        msgln ("Walking hash table(s) using a value set.");

        if (memoryHash != null)
            dumpTableByValueSet (memoryHash, "In-memory table");

        if (fileHash != null)
            dumpTableByValueSet (fileHash, "On-disk table");

        msgln ("");
        msgln ("----------------------------------------------");
        msgln ("Walking hash table(s) using an entry set.");

        if (memoryHash != null)
            dumpTableByEntrySet (memoryHash, "In-memory table");

        if (fileHash != null)
            dumpTableByEntrySet (fileHash, "On-disk table");

        msgln ("");
        msgln ("----------------------------------------------");
        msgln ("Walking hash table(s) non-sequentially.");

        if (memoryHash != null)
            dumpTableNonSequentially (memoryHash, "In-memory table");

        if (fileHash != null)
            dumpTableNonSequentially (fileHash, "On-disk table");
    }

    private void dumpTableByKeySet (Map<String, Entry> map, String label)
    {
        if (map.size() == 0)
            msgln (label + " is empty");

        else
        {
            Iterator<String>  it;
            int               i;
            long              ms;

            msgln ("");
            msgln (label);
            verboseln ("(key -> value)");
            ms = 0;
            for (i = 0, it = map.keySet().iterator(); it.hasNext(); i++)
            {
                String key = it.next();
                startTimer();
                Entry value = map.get (key);
                ms += stopTimer();

                verboseln ("    \"" + key + "\" -> \"" + value + "\"");
            }

            msgln ("");
            msgln ("Total entries:             " + i);
            msgln ("Total access time:         " + ms + " ms");
            msgln ("Avg access time per entry: " +
                   getAverage (ms, i) + " ms");

            if (map.size() != i)
            {
                throw new IllegalStateException ("Dumped more values " +
                                                 "than table says it " +
                                                 "contains! map.size() " +
                                                 "returns " +
                                                 map.size());
            }
        }
    }

    private void dumpTableByValueSet (Map<String, Entry> map, String label)
    {
        if (map.size() == 0)
            msgln (label + " is empty");

        else
        {
            Iterator<Entry>  it;
            int              i;
            long             ms;

            msgln ("");
            msgln (label);
            verboseln ("(values only):");
            ms = 0;
            for (i = 0, it = map.values().iterator(); it.hasNext(); i++)
            {
                startTimer();
                Entry value = it.next();
                ms += stopTimer();

                verboseln ("    \"" + value + "\"");
            }

            msgln ("");
            msgln ("Total entries:             " + i);
            msgln ("Total access time:         " + ms + " ms");
            msgln ("Avg access time per entry: " +
                   getAverage (ms, i) + " ms");

            if (map.size() != i)
            {
                throw new IllegalStateException ("Dumped more values " +
                                                 "than table says it " +
                                                 "contains! map.size() " +
                                                 "returns " +
                                                 map.size());
            }
        }
    }

    private void dumpTableByEntrySet (Map<String, Entry> map, String label)
    {
        if (map.size() == 0)
            msgln (label + " is empty");

        else
        {
            Iterator<Map.Entry<String, Entry>>  it;
            int                  i;
            long                 ms;

            msgln ("");
            msgln (label);
            verboseln ("(key -> value)");
            ms = 0;
            for (i = 0, it = map.entrySet().iterator(); it.hasNext(); i++)
            {
                startTimer();
                Map.Entry<String, Entry> entry = it.next();
                String key = entry.getKey();
                Entry value = entry.getValue();
                ms += stopTimer();

                verboseln ("    \"" + key + "\" -> \"" + value + "\"");
            }

            msgln ("");
            msgln ("Total entries:             " + i);
            msgln ("Total access time:         " + ms + " ms");
            msgln ("Avg access time per entry: " +
                   getAverage (ms, i) + " ms");

            if (map.size() != i)
            {
                throw new IllegalStateException ("Dumped more values " +
                                                 "than table says it " +
                                                 "contains! map.size() " +
                                                 "returns " +
                                                 map.size());
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
                verboseln ("(top) Key #" + top + "=\"" + key + "\" -> \"" +
                           value + "\"");
                total++;
                top++;
            }

            if (m < keys.length)
            {
                key = keys[m];
                startTimer();
                value = map.get (key);
                ms += stopTimer();
                verboseln ("(mid) Key #" + m + "=\"" + key + "\" -> \"" +
                           value + "\"");
                total++;
                m++;
            }
        }

        if (total != keys.length)
        {
            throw new IllegalStateException ("Dumped " +
                                             total +
                                             " values, but table " +
                                             " contains " +
                                             map.size() +
                                             "entries.");
        }

        msgln ("");
        msgln ("Total entries:             " + total);
        msgln ("Total access time:         " + ms + " ms");
        msgln ("Avg access time per entry: " + getAverage (ms, total) + " ms");

    }


    private String getAverage (long ms, int total)
    {
        StringBuffer  result = new StringBuffer();

        if (total == 0)
            result.append ("0.0");
        else
        {
            float avg = ((float) ms) / ((float) total);
            result.append (AVG_FMT.format (avg));
        }

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
        msgln (Logger.LEVEL_INFO, s);
    }

    private void msgln (LogLevel level, String s)
    {
        if (s != null)
        {
            System.out.print (s);
            log.message (level, s);
        }
        System.out.println();
        System.out.flush();
    }

    private void verboseln (String s)
    {
        if (verbose)
            msgln (Logger.LEVEL_DEBUG, s);
    }
}
