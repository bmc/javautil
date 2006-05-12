/*---------------------------------------------------------------------------*\
  $Id: MultipleRegexFilenameFilter.java 5812 2006-05-12 00:38:16Z bmc $
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.io;

import java.io.FileFilter;
import java.io.File;

/**
 * <p><tt>MultipleRegexFileFilter</tt> implements a
 * <tt>java.io.FileFilter</tt> that matches file names and path names
 * using one or more regular expressions. A
 * <tt>MultipleRegexFileFilter</tt> contains two sets of regular
 * expressions, an <i>accept</i> set and a <i>reject</i> set. To be
 * accepted, a file name must not match any of the patterns in the
 * <i>reject</i> set, and it <b>must</b> match at least one of the patterns
 * in the <i>accept</i> set. If the <i>reject</i> set is empty, then no
 * explicit rejections are done. However, if the <i>accept</i> set is empty,
 * then all files are assumed to be accepted. (i.e., It's as if the
 * <i>accept</i> set contained a single "^.*$" pattern.)</p>
 *
 * <p>A <tt>MultipleRegexFileFilter</tt> can be configured to operate
 * on just the simple file name, or on the file's path.</p>
 *
 * <p><tt>MultipleRegexFileFilter</tt> uses the <tt>java.util.regex</tt>
 * regular expression classes, so it requires JDK 1.4 or newer.</p>
 *
 * @see MultipleRegexFilenameFilter
 * @see CombinationFileFilter
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class MultipleRegexFileFilter
    extends AbstractMultipleRegexFileFilter
    implements FileFilter
{
    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>MultipleRegexFileFilter</tt>.
     *
     * @param matchType <tt>FileFilterMatchType.FILENAME</tt> to match just the
     *                  filename, <tt>FileFilterMatchType.PATH</tt> to match
     *                  the path (via <tt>java.io.File.getPath()</tt>)
     */
    public MultipleRegexFileFilter (FileFilterMatchType matchType)
    {
        super (matchType);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether a file is to be accepted or not, based on the
     * regular expressions in the <i>reject</i> and <i>accept</i> lists.
     *
     * @param path  The directory containing the file. Ignored if
     *              the match type is <tt>MatchType.FILENAME</tt>. Used to
     *              build the path to match when the match type is
     *              <tt>MatchType.PATH</tt>
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File path)
    {
        return acceptFile (path);
    }
}
