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

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Abstract base class for {@link MultipleRegexFilenameFilter} and
 * {@link MultipleRegexFileFilter}, allowing logic to be shared.
 *
 * @see MultipleRegexFileFilter
 * @see MultipleRegexFilenameFilter
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public abstract class AbstractMultipleRegexFileFilter
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private Collection<Pattern> acceptPatterns = null;
    private Collection<Pattern> rejectPatterns = null;
    private int                 regexOptions;
    private FileFilterMatchType matchType = FileFilterMatchType.FILENAME;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>AbstractMultipleRegexFileFilter</tt>.
     *
     * @param matchType <tt>FileFilterMatchType.FILENAME</tt> to match just
     *                  the filename, <tt>FileFilterMatchType.PATH</tt>
     *                  to match the path
     */
    protected AbstractMultipleRegexFileFilter (FileFilterMatchType matchType)
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
     * Add a "reject" pattern to this filter. For a file to be accepted:
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

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether a file is to be accepted or not, based on the
     * regular expressions in the <i>reject</i> and <i>accept</i> lists.
     * This method is useful for a <tt>java.io.FileFilter</tt> class.
     *
     * @param path  The file to be tested. If the match type is
     *              <tt>FileFilterMatchType.FILENAME</tt>, the regular
     *              expressions will be matched against
     *              <tt>file.getName()</tt>. If the match type is
     *              <tt>FileFilterMatchType.PATH</tt>, the regular expressions
     *              will be matched against <tt>file.getPath()</tt>.
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    protected boolean acceptFile (File path)
    {
        return doAcceptanceCheck (path.getPath());
    }

    /**
     * Determine whether a file is to be accepted or not, based on the
     * regular expressions in the <i>reject</i> and <i>accept</i> lists.
     * This method is useful for a <tt>java.io.FilenameFilter</tt> class.
     *
     * @param dir   The directory containing the file. Ignored if
     *              the match type is <tt>FileFilterMatchType.FILENAME</tt>.
     *              Used to build the path to match when the match type is
     *              <tt>MatchType.PATH</tt>
     * @param name  the file name
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    protected boolean acceptFilename (File dir, String name)
    {
        boolean match;

        if (matchType == FileFilterMatchType.PATH)
            match = doAcceptanceCheck (new File (dir, name).getPath());
        else
            match = doAcceptanceCheck (name);

        return match;
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private boolean doAcceptanceCheck (String nameOrPath)
    {
        boolean match = false;
        boolean found = false;

        // Check for rejects first.

        for (Pattern pattern : rejectPatterns)
        {
            Matcher matcher = pattern.matcher (nameOrPath);

            if (matcher.find())
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
                    Matcher matcher = pattern.matcher (nameOrPath);

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
