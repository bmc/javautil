/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms are permitted provided
  that: (1) source distributions retain this entire copyright notice and
  comment; and (2) modifications made to the software are prominently
  mentioned, and a copy of the original software (or a pointer to its
  location) are included. The name of the author may not be used to endorse
  or promote products derived from this software without specific prior
  written permission.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
  WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.

  Effectively, this means you can do what you want with the software except
  remove this notice or take advantage of the author's name. If you modify
  the software and redistribute your modified version, you must indicate that
  your version is a modification of the original, and you must provide either
  a pointer to or a copy of the original.
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
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
class FileHashMapEntry implements Serializable, Comparable
{
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
    private Object key = null;

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
    FileHashMapEntry (long pos, int size, Object key)
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
    public int compareTo (Object o)
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
        return ("FileHashMapEntry[filePosition="
              + filePosition
              + ", objectSize="
              + objectSize
              + ", key="
              + ((key == null) ? "<null>" : key)
              + "]");
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
    Object getKey()
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
    void setKey (Object newKey)
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
