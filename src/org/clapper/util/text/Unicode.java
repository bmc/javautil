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

package org.clapper.util.text;

/**
 * Symbolic names for certain Unicode constants. The constant
 * names are adapted from the Unicode standard. See
 * <a href="http://charts.unicode.org/"><i>charts.unicode.org</i></a>.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public final class Unicode
{
    /*----------------------------------------------------------------------*\
                                 Constants
    \*----------------------------------------------------------------------*/

    /**
     * Copyright symbol.
     */
    public static char COPYRIGHT_SIGN = '\u00a9';

    /**
     * The "registered" symbol ("R" in a circle).
     */
    public static char REGISTERED_SIGN = '\u00ae';

    /**
     * "One-quarter" (1/4) symbol.
     */
    public static char ONE_QUARTER_FRACTION = '\u00bc';

    /**
     * "One-half" (1/2) symbol.
     */
    public static char ONE_HALF_FRACTION = '\u00bd';

    /**
     * "Three-quarters" (3/4) symbol.
     */
    public static char THREE_QUARTERS_FRACTION = '\u00be';

    /**
     * Classic mathematical multiplication ("times") symbol.
     */
    public static char MULTIPLICATION_SIGN = '\u00d7';

    /**
     * Classic mathematical division symbol.
     */
    public static char DIVISION_SIGN = '\u00f7';

    /**
     * Symbol for degrees
     */
    public static char DEGREE_SIGN = '\u00b0';

    /**
     * Non-breaking space.
     */
    public static char NBSP = '\u00a0';
}
