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

package org.clapper.util.text;

/**
 * <p>This interface defines the methods for a class that checks characters
 * to determine whether they're legal for a variable name that's to be
 * substituted by a {@link VariableSubstituter} object. It has a single
 * method, {@link #legalVariableCharacter}, which determines whether a
 * specified character is a legal part of a variable name or not. This
 * capability provides additional flexibility by allowing callers to define
 * precisely what characters constitute legal variable names.</p>
 *
 * @see VariableSubstituter
 * @see VariableDereferencer
 *
 * @version $Revision$
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public interface VariableNameChecker
{
    /*----------------------------------------------------------------------*\
                             Required Methods
    \*----------------------------------------------------------------------*/

    /**
     * <p>Determine whether a character may legally be used in a variable
     * name or not.</p>
     *
     * @param c   The character to test
     *
     * @return <tt>true</tt> if the character may be part of a variable name,
     *         <tt>false</tt> otherwise
     *
     * @see VariableSubstituter#substitute
     */
    public boolean legalVariableCharacter (char c);
}
