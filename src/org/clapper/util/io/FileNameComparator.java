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

package org.clapper.util.io;

import java.io.File;
import java.util.Comparator;

/**
 * <p><tt>FileNameComparator</tt> implements a <tt>Comparator</tt> class
 * that compares file names (expressed either as <tt>String</tt> or
 * <tt>File</tt> objects) for sorting. The comparison can be case-sensitive
 * or case-insensitive, and can apply to the entire path (if available) or
 * just the file name part.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class FileNameComparator implements Comparator<Object>
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private boolean foldCase   = false;
    private boolean entirePath = false;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>FileNameComparator</tt> with the default settings.
     * The default settings are:
     *
     * <ul>
     *   <li> Compare only file names, not paths
     *   <li> Use case-sensitive comparison
     * </ul>
     *
     * Calling this constructor is equivalent to:
     *
     * <pre>Comparator cmp = new FileNameComparator (false, false);</pre>
     */
    public FileNameComparator()
    {
        this (false, false);
    }

    /**
     * Construct a new <tt>FileNameComparator</tt>.
     *
     * @param foldCase   <tt>true</tt> for case-insensitive comparison,
     *                   <tt>false</tt> for case-sensitive comparison
     * @param entirePath <tt>true</tt> to compare the entire path (where
     *                   available, <tt>false</tt> to use just the name.
     */
    public FileNameComparator (boolean foldCase,
                               boolean entirePath)
    {
        this.foldCase = foldCase;
        this.entirePath = entirePath;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Compares its two arguments for order. Returns a negative integer,
     * zero, or a positive integer as the first argument is less than,
     * equal to, or greater than the second.
     *
     * @param o1  the first object to be compared
     * @param o2  the second object to be compared
     *
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare (Object o1, Object o2)
    {
        String s1  = getFileName (o1);
        String s2  = getFileName (o2);
        int    cmp = 0;

        if (foldCase)
            cmp = s1.compareToIgnoreCase (s2);
        else
            cmp = s1.compareTo (s2);

        return cmp;
    }

    /**
     * <p>Indicates whether some other object is "equal to" this
     * <tt>Comparator</tt>.</p>
     *
     * @param o  the object to compare
     *
     * @return <tt>true</tt> only if the pecified object is also a comparator
     *         and it imposes the same ordering as this comparator.
     */
    public boolean equals (Object o)
    {
        boolean eq = false;

        if (o instanceof FileNameComparator)
        {
            FileNameComparator other = (FileNameComparator) o;

            eq = (other.foldCase == this.foldCase) &&
                  (other.entirePath == this.entirePath);
        }

        return eq;
    }

    /**
     * Get the hash code for this object.
     *
     * @return the hash code
     */
    public int hashCode()                                             // NOPMD
    {
        return super.hashCode();
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private String getFileName (Object o)
    {
        String name = null;

        if (o instanceof File)
            name = ((File) o).getPath();

        else
            name = (String) o;

        if (! entirePath)
            name = FileUtil.basename (name);

        return name;
    }
}
