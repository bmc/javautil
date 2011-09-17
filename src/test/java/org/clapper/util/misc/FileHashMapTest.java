package org.clapper.util.misc;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class FileHashMapTest extends MapTestBase
{
    /*----------------------------------------------------------------------*\
                                 Constants
    \*----------------------------------------------------------------------*/

    private static final String FILE_PREFIX = "junit_fhm";

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    public FileHashMapTest()
    {
    }

    /*----------------------------------------------------------------------*\
                               Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Test of close method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    @Test public void close() throws IOException
    {
        FileHashMap<String,String> map =
            new FileHashMap<String,String>(FILE_PREFIX);
        map.close();
        assertFalse (map.isValid());

        map.close();
        map.delete();
   }

    /**
     * Test save/restore of FileHashMap.
     *
     * @throws IOException              error creating/writing/reading map
     * @throws ObjectExistsException    unexpected
     * @throws ClassNotFoundException   can't deserialized object
     * @throws VersionMismatchException bad or unsupported version stamp
     *                                  in <tt>FileHashMap</tt> index file
     */
    @Test public void saveRestore()
        throws IOException,
               ObjectExistsException,
               ClassNotFoundException,
               VersionMismatchException
    {
        FileHashMap<String,Integer> map =
            new FileHashMap<String,Integer>
                (FILE_PREFIX,
                 FileHashMap.FORCE_OVERWRITE);
        try
        {

            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);

            System.out.println("Writing map.");
            map.save();

            System.out.println("Rereading map.");
            FileHashMap<String,Integer> map2 =
                new FileHashMap<String,Integer>(FILE_PREFIX, 0);

            assertEquals("Reloaded map has wrong size",
                         map.size(), map2.size());
            for (String key : map.keySet())
            {
                assertTrue("Reloaded map is missing key \"" + key + "\"",
                           map2.containsKey(key));
                int expected = map.get(key);
                int has = map2.get(key);
                assertEquals("Reloaded map has wrong value for key \"" +
                             key + "\"",
                             expected, has);
            }
        }

        finally
        {
            if (map != null)
                map.delete();
        }
    }

    /**
     * Test concurrent modification.
     * @throws IOException              error creating/writing/reading map
     * @throws ObjectExistsException    unexpected
     * @throws ClassNotFoundException   can't deserialized object
     * @throws VersionMismatchException bad or unsupported version stamp
     *                                  in <tt>FileHashMap</tt> index file
     */
    @Test public void concurrentModification()
        throws IOException,
               ObjectExistsException,
               ClassNotFoundException,
               VersionMismatchException
     {
/*
 DOESN'T WORK YET.

        String filePrefix = getFilePrefix();
        FileHashMap<String,Integer> map1 =
            new FileHashMap<String,Integer>(filePrefix, 0);
        FileHashMap<String,Integer> map2 =
            new FileHashMap<String,Integer>(filePrefix, 0);

        map1.put("a", 1);
        map2.put("b", 2);
        try
        {
            // Sleep a bit, to account for lack of granularity of
            // last modified setting

            try
            {
                Thread.sleep(2000);
            }

            catch (InterruptedException ex)
            {
                fail("Unexpected InterruptedException");
            }

            map1.put("c", 3);
            fail("Expected ConcurrentModificationException to be thrown");
        }

        catch (ConcurrentModificationException ex)
        {
            System.out.println("Caught expected exception");
            ex.printStackTrace(System.out);
        }
 */
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    protected Map<String,String> newMap()
    {
        Map<String,String> result = null;

        try
        {
            result = new FileHashMap<String,String>();
        }

        catch (IOException ex)
        {
            fail("IOException on FileHashMap creation");
        }

        return result;
    }

    /*----------------------------------------------------------------------*\
                               Private Methods
    \*----------------------------------------------------------------------*/

    private String getFilePrefix()
    {
        StringBuilder filePrefixBuf = new StringBuilder();
        filePrefixBuf.append (System.getProperty("java.io.tmpdir"));
        filePrefixBuf.append(System.getProperty("file.separator"));
        filePrefixBuf.append("FileHashMapTest");
        return filePrefixBuf.toString();
    }
}
