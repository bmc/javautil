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

package org.clapper.util.classutil;

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
