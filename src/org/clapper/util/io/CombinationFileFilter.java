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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import java.io.FileFilter;
import java.io.File;

/**
 * <p>A <tt>CombinationFileFilter</tt> contains one or more
 * <tt>java.io.FileFilter</tt> objects. When its {@link #accept accept()}
 * method is called, the <tt>CombinationFileFilter</tt> object passes the
 * file through the contained filters. If the <tt>CombinationFileFilter</tt> 
 * object's mode is set to {@link #AND_FILTERS}, then a file must be
 * accepted by all contained filters to be accepted. If the
 * <tt>CombinationFileFilter</tt> object's mode is set to
 * {@link #OR_FILTERS}, then a file name is accepted if any one of the
 * contained filters accepts it. The default mode is <tt>AND_FILTERS</tt>.</p>
 *
 * <p>The contained filters are applied in the order they were added to
 * the <tt>CombinationFileFilter</tt> object.</p>
 *
 * @deprecated Use {@link AndFileFilter} and {@link OrFileFilter}
 *
 * @see FileFilter
 * @see CombinationFilenameFilter
 * @see MultipleRegexFilenameFilter
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class CombinationFileFilter implements FileFilter
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * Convenience constant for backward compatibility: Mode setting that
     * instructs the filter to <tt>AND</tt> all the contained filters.
     *
     * @see CombinationFilterMode#AND_FILTERS
     */
    public static final CombinationFilterMode AND_FILTERS =
                               CombinationFilterMode.AND_FILTERS;

    /**
     * Convenience constant for backward compatibility: Mode setting that
     * instructs the filter to <tt>OR</tt> all the contained filters.
     *
     * @see CombinationFilterMode#OR_FILTERS
     */
    public static final CombinationFilterMode OR_FILTERS  =
                               CombinationFilterMode.OR_FILTERS;

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private List<FileFilter>       filters = new LinkedList<FileFilter>();
    private CombinationFilterMode  mode    = AND_FILTERS;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>CombinationFileFilter</tt> with a mode of
     * {@link #AND_FILTERS}. The mode can be changed later by calling
     * {@link #setMode(CombinationFilterMode) setMode()}.
     *
     * @see #CombinationFileFilter(CombinationFilterMode)
     * @see #setMode
     */
    public CombinationFileFilter()
    {
        this (AND_FILTERS);
    }

    /**
     * Construct a new <tt>CombinationFileFilter</tt> with the specified
     * mode.
     *
     * @param mode  {@link #AND_FILTERS} if a filename must be accepted
     *              by all contained filters. {@link #OR_FILTERS} if a 
     *              filename only needs to be accepted by one of the
     *              contained filters.
     *
     * @see #setMode
     */
    public CombinationFileFilter (CombinationFilterMode mode)
    {
        setMode (mode);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the combination mode of this <tt>CombinationFileFilter</tt>
     * object.
     *
     * @return  {@link #AND_FILTERS} if a filename must be accepted by all
     *          contained filters. {@link #OR_FILTERS} if a filename only
     *          needs to be accepted by one of the contained filters.
     *
     * @see #setMode
     */
    public CombinationFilterMode getMode()
    {
        return mode;
    }

    /**
     * Change the combination mode of this <tt>CombinationFileFilter</tt>
     * object.
     *
     * @param mode  {@link #AND_FILTERS} if a filename must be accepted
     *              by all contained filters. {@link #OR_FILTERS} if a 
     *              filename only needs to be accepted by one of the
     *              contained filters.
     *
     * @see #getMode
     */
    public void setMode (CombinationFilterMode mode)
    {
        this.mode = mode;
    }

    /**
     * Add a filter to the set of contained filters.
     *
     * @param filter the <tt>FileFilter</tt> to add.
     *
     * @see #removeFilter
     */
    public void addFilter (FileFilter filter)
    {
        filters.add (filter);
    }

    /**
     * Remove a filter from the set of contained filters.
     *
     * @param filter the <tt>FileFilter</tt> to remove.
     *
     * @see #addFilter
     */
    public void removeFilter (FileFilter filter)
    {
        filters.remove (filter);
    }

    /**
     * Determine whether a file is to be accepted or not, based on the
     * contained filters and the mode. If this object's mode mode is set to
     * {@link #AND_FILTERS}, then a file must be accepted by all contained
     * filters to be accepted. If this object's mode is set to
     * {@link #OR_FILTERS}, then a file name is accepted if any one of the
     * contained filters accepts it. If the set of contained filters is
     * empty, then this method returns <tt>false</tt>.
     *
     * @param file  The file to check for acceptance
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File file)
    {
        boolean              accepted = false;
        Iterator<FileFilter> it = filters.iterator();
        FileFilter           filter;  

        if (mode == AND_FILTERS)
        {
            accepted = true;

            while (accepted && it.hasNext())
            {
                filter = it.next();
                accepted = filter.accept (file);
            }
        }

        else
        {
            accepted = false;

            while ((! accepted) && it.hasNext())
            {
                filter = it.next();
                accepted = filter.accept (file);
            }
        }

        return accepted;
    }
}
