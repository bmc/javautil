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

import java.util.*;

/**
 * <p>The <code>MapVariableDereferencer</code> class implements the
 * <code>VariableDereferencer</code> interface and resolves variable
 * references by looking them up in a supplied <code>Map</code> object. By
 * using a <code>Map</code> object, this class can support variable lookups
 * from a variety of existing data structures, including:</p>
 *
 * <ul>
 *   <li><code>java.util.HashMap</code> and <code>java.util.TreeMap</code>
 *       objects
 *   <li><code>java.util.Hashtable</code> objects
 *   <li><code>java.util.Properties</code> objects
 * </ul>
 *
 * <p>The keys in the supplied <code>Map</code> object <b>must</b> be
 * <code>String</code> objects. The values can be anything, though their
 * <code>toString()</code> methods will be called to coerce them to
 * strings.</p>
 * 
 * @see VariableDereferencer
 * @see VariableSubstituter
 *
 * @version $Revision$
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class MapVariableDereferencer implements VariableDereferencer
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    /**
     * Associated Map object.
     */
    private Map map = null;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <code>MapVariableDereferencer</code> object that
     * resolves its variable references from the specified <code>Map</code>
     * object.
     *
     * @param map  The <code>Map</code> object from which to resolve
     *             variable references.
     */
    public MapVariableDereferencer (Map map)
    {
        this.map = map;
    }

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
     *                 Ignored here.
     *
     * @return The variable's value. If the variable has no value, this
     *         method must return the empty string (""). It is important
     *         <b>not</b> to return null.
     */
    public String getVariableValue (String varName, Object context)
    {
        Object result = map.get (varName);

        return (result == null) ? "" : result.toString();
    }
}
