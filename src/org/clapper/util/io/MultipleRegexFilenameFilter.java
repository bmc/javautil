/*---------------------------------------------------------------------------*\
  $Id$
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
import java.io.FilenameFilter;
import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p><tt>MultipleRegexFilenameFilter</tt> implements a
 * <tt>java.io.FilenameFilter</tt> that matches file names and path names
 * using one or more regular expressions. A
 * <tt>MultipleRegexFilenameFilter</tt> contains two sets of regular
 * expressions, an <i>accept</i> set and a <i>reject</i> set. To be
 * accepted, a file name must not match any of the patterns in the
 * <i>reject</i> set, and it <b>must</b> match at least one of the patterns
 * in the <i>accept</i> set. If the <i>reject</i> set is empty, then no
 * explicit rejections are done. However, if the <i>accept</i> set is empty,
 * then all files are assumed to be accepted. (i.e., It's as if the
 * <i>accept</i> set contained a single "^.*$" pattern.)</p>
 *
 * <p>A <tt>MultipleRegexFilenameFilter</tt> can be configured to operate
 * on just the simple file name, or on the file's path.</p>
 *
 * <p><tt>MultipleRegexFilenameFilter</tt> uses the <tt>java.util.regex</tt>
 * regular expression classes, so it requires JDK 1.4 or newer.</p>
 *
 * @see CombinationFilenameFilter
 * @see CombinationFileFilter
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class MultipleRegexFilenameFilter
    implements FilenameFilter, FileFilter
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * Match types
     */
    public enum MatchType
    {
        FILENAME, PATH
    }

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private Collection<Pattern>  acceptPatterns = null;
    private Collection<Pattern>  rejectPatterns = null;
    private int                  regexOptions;
    private MatchType            matchType = MatchType.FILENAME;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>MultipleRegexFilenameFilter</tt>.
     *
     * @param matchType <tt>MatchType.FILENAME</tt> to match just the
     *                  filename, <tt>MatchType.PATH</tt> to match the path
     */
    public MultipleRegexFilenameFilter (MatchType matchType)
    {
        this.matchType = matchType;
        regexOptions   = Pattern.CASE_INSENSITIVE;
        acceptPatterns = new ArrayList<Pattern>();
        rejectPatterns = new ArrayList<Pattern>();
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add an "accept" pattern to this filter. For a file to be accepted:
     *
     * <ul>
     *   <li> it must not match one of the <i>reject</i> patterns, and
     *   <li> either the <i>accept</i> pattern list must be empty, or the
     *        file name must match one of the <i>accept</i> patterns
     * </ul>
     *
     * @param pattern  the regular expression to add
     *
     * @throws PatternSyntaxException  bad regular expression
     *
     * @see #addRejectPattern
     */
    public void addAcceptPattern (String pattern)
        throws PatternSyntaxException
    {
        acceptPatterns.add (Pattern.compile (pattern, regexOptions));
    }

    /**
     * Add an "accept" pattern to this filter. For a file to be accepted:
     *
     * <ul>
     *   <li> it must not match one of the <i>reject</i> patterns, and
     *   <li> either the <i>accept</i> pattern list must be empty, or the
     *        file name must match one of the <i>accept</i> patterns
     * </ul>
     *
     * @param pattern  the regular expression to add
     *
     * @throws PatternSyntaxException  bad regular expression
     *
     * @see #addAcceptPattern
     */
    public void addRejectPattern (String pattern)
        throws PatternSyntaxException
    {
        rejectPatterns.add (Pattern.compile (pattern, regexOptions));
    }

    /**
     * Determine whether a file is to be accepted or not, based on the
     * regular expressions in the <i>reject</i> and <i>accept</i> lists.
     *
     * @param path  The directory containing the file. Ignored if
     *              the match type is <tt>MatchType.FILENAME</tt>. Used to
     *              build the path to match when the match type is
     *              <tt>MatchType.PATH</tt>
     * @param name  the file name
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File path)
    {
        return accept (new File (FileUtil.dirname (path)),
                       FileUtil.basename (path));
    }

    /**
     * Determine whether a file is to be accepted or not, based on the
     * regular expressions in the <i>reject</i> and <i>accept</i> lists.
     *
     * @param dir   The directory containing the file. Ignored if
     *              the match type is <tt>MatchType.FILENAME</tt>. Used to
     *              build the path to match when the match type is
     *              <tt>MatchType.PATH</tt>
     * @param name  the file name
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File dir, String name)
    {
        Iterator  it;
        boolean   match = false;
        boolean   found = false;

        if (matchType == MatchType.PATH)
        {
            name = dir.getPath()
                 + System.getProperty ("file.separator")
                 + name;
        }

        // Check for rejects first.

        for (Pattern pattern : rejectPatterns)
        {
            Matcher matcher = pattern.matcher (name);

            if (matcher.matches())
            {
                match = false;
                found = true;
                break;
            }
        }

        if (! found)
        {
            // Check for accepts.

            if (acceptPatterns.size() == 0)
                match = true;

            else
            {
                for (Pattern pattern : acceptPatterns)
                {
                    Matcher matcher = pattern.matcher (name);

                    if (matcher.find())
                    {
                        match = true;
                        break;
                    }
                }
            }
        }

        return match;
    }
}
