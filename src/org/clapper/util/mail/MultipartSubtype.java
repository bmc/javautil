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

package org.clapper.util.mail;

/**
 * Constant instances of this class are used to tell an
 * <tt>EmailMessage</tt> object the type of the multipart email message
 * ("mixed" or "alternative").
 *
 * @see EmailMessage#MULTIPART_MIXED
 * @see EmailMessage#MULTIPART_ALTERNATIVE
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public final class MultipartSubtype
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private String subtype;

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * Constructor. Only accessible within this package.
     *
     * @param subtype  the subtype string, which is meaningful to the Java
     *                 Mail API
     */
    MultipartSubtype (String subtype)
    {
        this.subtype = subtype;
    }

    /**
     * Get the associated subtype string, which is meaningful to the
     * Java Mail API. Only accessible within this package.
     *
     * @return the subtype string
     */
    String getSubtypeString()
    {
        return subtype;
    }

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether this object is equivalent to another. Note that,
     * when used to define constants, it's generally fine to use Java's
     * "==" operator, since the constants will only be defined once.
     *
     * @param other  the other object
     *
     * @return <tt>true</tt> if they're equivalent, <tt>false</tt> otherwise
     */
    public boolean equals (Object other)
    {
        MultipartSubtype that = (MultipartSubtype) other;
        return ((this == that) || (this.subtype.equals (that.subtype)));
    }
}
