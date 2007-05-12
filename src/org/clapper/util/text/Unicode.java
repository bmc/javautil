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

package org.clapper.util.text;

/**
 * Symbolic names for certain Unicode constants. The constant
 * names are adapted from the Unicode standard. See
 * <a href="http://charts.unicode.org/"><i>charts.unicode.org</i></a>.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public final class Unicode
{
    /*----------------------------------------------------------------------*\
                                 Constants
    \*----------------------------------------------------------------------*/

    /**
     * Copyright symbol.
     */
    public static final char COPYRIGHT_SIGN = '\u00a9';

    /**
     * The "registered" symbol ("R" in a circle).
     */
    public static final char REGISTERED_SIGN = '\u00ae';

    /**
     * "One-quarter" (1/4) symbol.
     */
    public static final char ONE_QUARTER_FRACTION = '\u00bc';

    /**
     * "One-half" (1/2) symbol.
     */
    public static final char ONE_HALF_FRACTION = '\u00bd';

    /**
     * "Three-quarters" (3/4) symbol.
     */
    public static final char THREE_QUARTERS_FRACTION = '\u00be';

    /**
     * Classic mathematical multiplication ("times") symbol.
     */
    public static final char MULTIPLICATION_SIGN = '\u00d7';

    /**
     * Classic mathematical division symbol.
     */
    public static final char DIVISION_SIGN = '\u00f7';

    /**
     * Symbol for degrees
     */
    public static final char DEGREE_SIGN = '\u00b0';

    /**
     * Non-breaking space.
     */
    public static final char NBSP = '\u00a0';

    /**
     * En space
     */
    public static final char EN_SPACE = '\u2002';

    /**
     * Em space
     */
    public static final char EM_SPACE = '\u2003';

    /**
     * Thin space (defined as a fifth of an em)
     */
    public static final char THIN_SPACE = '\u2009';

    /**
     * Hair space. Thinner than a <tt>THIN_SPACE</tt>, in traditional
     * typography a hair space is the thinnest space available.
     */
    public static final char HAIR_SPACE = '\u200a';

    /**
     * Zero-width non-joiner.
     */
    public static final char ZERO_WIDTH_NON_JOINER = '\u200c';

    /**
     * Synonym for {@link #ZERO_WIDTH_NON_JOINER}
     */
    public static final char ZWNJ = ZERO_WIDTH_NON_JOINER;

    /**
     * Zero-width joiner.
     */
    public static final char ZERO_WIDTH_JOINER = '\u200d';

    /**
     * Synonym for {@link #ZERO_WIDTH_JOINER}
     */
    public static final char ZWJ = ZERO_WIDTH_JOINER;

    /**
     * Hyphen (normal encoding, i.e., hyphen-minus)
     */
    public static final char HYPHEN = '\u002d';

    /**
     * Synonym for {@link #HYPHEN}
     */
    public static final char HYPHEN_MINUS = HYPHEN;

    /**
     * Non-breaking hyphen
     */
    public static final char NON_BREAKING_HYPHEN = '\u2011';

    /**
     * En dash
     */
    public static final char EN_DASH = '\u2013';

    /**
     * Em dash
     */
    public static final char EM_DASH = '\u2014';

    /**
     * Left single quotation mark
     */
    public static final char LEFT_SINGLE_QUOTE = '\u2018';

    /**
     * Right single quotation mark
     */
    public static final char RIGHT_SINGLE_QUOTE = '\u2019';

    /**
     * Single low-9 quotation mark, used as an opening single quote in
     * some languages.
     */
    public static final char SINGLE_LOW_9_QUOTE = '\u201a';

    /**
     * Left double quotation mark
     */
    public static final char LEFT_DOUBLE_QUOTE = '\u201c';

    /**
     * Right double quotation mark
     */
    public static final char RIGHT_DOUBLE_QUOTE = '\u201d';

    /**
     * Trademark symbol
     */
    public static final char TRADEMARK = '\u2122';

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private Unicode()
    {
        // Cannot be instantiated
    }
}
