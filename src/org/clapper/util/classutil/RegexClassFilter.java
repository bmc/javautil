/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2006 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.classutil;

import java.util.ArrayList;
import java.util.Collection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p><tt>RegexClassFilter</tt> is a {@link ClassFilter} that matches class
 * names using a regular expression. Multiple regular expression filters
 * can be combined using {@link AndClassFilter} and/or
 * {@link OrClassFilter} objects.</p>
 *
 * <p>This class does not have to load the classes it's filtering; it
 * matches on the class name only.</p>
 *
 * <p><tt>RegexClassFilter</tt> uses the <tt>java.util.regex</tt>
 * regular expression classes.</p>
 *
 * @see ClassFilter
 * @see AndClassFilter
 * @see OrClassFilter
 * @see NotClassFilter
 * @see ClassFinder
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class RegexClassFilter
    implements ClassFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private Pattern pattern;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>RegexClassFilter</tt> using the specified
     * pattern.
     *
     * @param regex  the regular expression to add
     *
     * @throws PatternSyntaxException  bad regular expression
     */
    public RegexClassFilter (String regex)
        throws PatternSyntaxException
    {
        pattern = Pattern.compile (regex);
    }

    /**
     * Construct a new <tt>RegexClassFilter</tt> using the specified
     * pattern.
     *
     * @param regex      the regular expression to add
     * @param regexFlags regular expression compilation flags (e.g.,
     *                   <tt>Pattern.CASE_INSENSITIVE</tt>). See
     *                   the Javadocs for <tt>java.util.regex</tt> for
     *                   legal values.
     *
     * @throws PatternSyntaxException  bad regular expression
     */
    public RegexClassFilter (String regex, int regexFlags)
        throws PatternSyntaxException
    {
        pattern = Pattern.compile (regex, regexFlags);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether a class name is to be accepted or not, based on
     * the regular expression specified to the constructor.
     *
     * @param classInfo   the {@link ClassInfo} object to test
     * @param classFinder the invoking {@link ClassFinder} object
     *
     * @return <tt>true</tt> if the class name matches,
     *         <tt>false</tt> if it doesn't
     */
    public boolean accept (ClassInfo classInfo, ClassFinder classFinder)
    {
        return pattern.matcher (classInfo.getClassName()).find();
    }
}
