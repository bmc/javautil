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
      without prior written permission of Brian M. Clapper.

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

import java.io.Serializable;

/**
 * Stores the location of an on-disk serialized value. Used by
 * <tt>FileHashMap</tt> for its in-memory index and returned sets and
 * iterators. This class is not publicly accessible. It's stored in a
 * separate file, because inner classes cannot be serialized.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
class FileHashMapEntry<K>
    implements Serializable, Comparable<FileHashMapEntry>
{
    /*----------------------------------------------------------------------*\
                         Private Static Variables
    \*----------------------------------------------------------------------*/

    /**
     * See JDK 1.5 version of java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * The file position.
     */
    private long filePosition = -1;

    /**
     * The length of the stored, serialized object
     */
    private int objectSize = -1;

    /**
     * The caller's key (i.e., the key the caller of FileHashMap.put()
     * specified).
     */
    private K key = null;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>FileHashMapEntry</tt> that records the location
     * and length of an item stored in the data file portion of a
     * <tt>FileHashMap</tt> obejct.
     *
     * @param pos   The object's file position. The object may or may not
     *              actually have been written there yet.
     * @param size  The stored object's serialized size, if known, or -1
     *              if the object has never been written. A non-negative
     *              size value will typically be passed when an existing
     *              <tt>FileHashMap</tt> is being reloaded from disk.
     * @param key   The caller's key (i.e., the key the caller of
     *              <tt>FileHashMap.put()</tt> specified). May be null.
     *
     * @see #getFilePosition
     * @see #getObjectSize
     * @see #setObjectSize
     * @see FileHashMap#put
     */
    FileHashMapEntry (long pos, int size, K key)
    {
        this.filePosition = pos;
        this.objectSize   = size;
        this.key          = key;
    }

    /**
     * Create an entry with no associated key. Used primarily to record
     * file gaps. In that case, the object size is really the gap size.
     *
     * @param pos   The object's file position. The object may or may not
     *              actually have been written there yet.
     * @param size  The gap size.
     *
     * @see #getFilePosition
     * @see #getObjectSize
     * @see #setObjectSize
     */
    FileHashMapEntry (long pos, int size)
    {
        this (pos, size, null);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Compares this object with the specified object for order. Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object. The comparison
     * key for a <tt>FileHashMapEntry</tt> is the file position value.
     *
     * @param o  The other object
     */
    public int compareTo (FileHashMapEntry o)
    {
        FileHashMapEntry  other    = (FileHashMapEntry) o;
        Long              thisPos  = new Long (this.filePosition);
        Long              otherPos = new Long (other.filePosition);

        return thisPos.compareTo (otherPos);
    }

    /**
     * Display a string version of the contents of this object. Mostly
     * useful for debugging.
     *
     * @return a string representation of the contents of this object
     */
    public String toString()
    {
        return ("FileHashMapEntry[filePosition=" +
                filePosition +
                ", objectSize=" +
                objectSize +
                ", key=" +
                ((key == null) ? "<null>" : key) +
                "]");
    }

    /*----------------------------------------------------------------------*\
                          Package-visible Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the caller's key (i.e., the key the caller passed to
     * <tt>FileHashMap.put()</tt>).
     *
     * @return the key
     *
     * @see #setKey
     * @see FileHashMap#put
     */
    K getKey()
    {
        return key;
    }

    /**
     * Change the key for this entry
     *
     * @param newKey  the new key to use
     *
     * @see #getKey
     */
    void setKey (K newKey)
    {
        this.key = newKey;
    }

    /**
     * Get the file position for this entry.
     *
     * @return the file position
     *
     * @see #setFilePosition
     */
    long getFilePosition()
    {
        return this.filePosition;
    }

    /**
     * Set the file position for this entry.
     *
     * @param pos the new file position
     *
     * @see #getFilePosition
     */
    void setFilePosition (long pos)
    {
        this.filePosition = pos;
    }

    /**
     * Get the number of bytes the serialized object occupies in the
     * random access file.
     *
     * @return the number of bytes occupied by the object
     *
     * @see #setObjectSize
     */
    int getObjectSize()
        throws IllegalStateException
    {
        assert (this.objectSize > 0) : "No object stored yet";
        return this.objectSize;
    }

    /**
     * Get the number of bytes the serialized object occupies in the
     * random access file.
     *
     * @param size the number of bytes occupied by the object
     *
     * @see #getObjectSize
     */
    void setObjectSize (int size)
    {
        this.objectSize = size;
    }
}
