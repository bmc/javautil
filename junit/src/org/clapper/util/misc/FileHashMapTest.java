/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2007 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M.a Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc;

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

    public FileHashMapTest(String testName)
    {
        super(testName);
    }

    /*----------------------------------------------------------------------*\
                               Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Test of close method, of class org.clapper.util.misc.FileHashMap.
     *
     * @throws IOException error initializing map
     */
    public void testClose()
        throws IOException
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
    public void testSaveRestore()
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
    public void testConcurrentModification()
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
