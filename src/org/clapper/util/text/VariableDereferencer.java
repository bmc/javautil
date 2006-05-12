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
 * <p>The <code>VariableDereferencer</code> interface defines the behavior
 * of classes that can look up variables by name, returning their values.
 * It is used primarily to mark classes that can work hand-in-hand with
 * <code>VariableSubstituter</code> objects to resolve variable references
 * in strings.</p>
 *
 * <p>The values for referenced variables can come from anywhere (in a
 * <code>Properties</code> object, via direct method calls, from a symbol
 * table, etc.), provided the values can be located using only the
 * variable's name.</p>
 *
 * @see MapVariableDereferencer
 * @see VariableSubstituter
 *
 * @version $Revision$
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public interface VariableDereferencer
{
    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the value associated with a given variable.
     *
     * @param varName  The name of the variable for which the value is
     *                 desired.
     * @param context  a context object, passed through from the caller
     *                 to the dereferencer, or null if there isn't one.
     *
     * @return The variable's value. If the variable has no value, this
     *         method must return the empty string (""). It is important
     *         <b>not</b> to return null.
     *
     * @throws VariableSubstitutionException  substitution error
     */
    public String getVariableValue (String varName, Object context)
        throws VariableSubstitutionException;
}
