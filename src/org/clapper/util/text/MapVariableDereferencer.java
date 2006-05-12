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

import org.clapper.util.misc.PropertiesMap;

import java.util.Map;
import java.util.Properties;

/**
 * <p>The <tt>MapVariableDereferencer</tt> class implements the
 * <tt>VariableDereferencer</tt> interface and resolves variable
 * references by looking them up in a supplied <tt>Map</tt> object. By
 * using a <tt>Map</tt> object, this class can support variable lookups
 * from a variety of existing data structures, including:</p>
 *
 * <ul>
 *   <li><tt>java.util.HashMap</tt> and <tt>java.util.TreeMap</tt>
 *       objects
 *   <li><tt>java.util.Hashtable</tt> objects
 *   <li><tt>java.util.Properties</tt> objects
 * </ul>
 *
 * <p>The keys and values in the supplied <tt>Map</tt> object
 * <b>must</b> be <tt>String</tt> objects. </p>
 *
 * <h3>Example</h3>
 *
 * <p>Perhaps the simplest example is one that uses the environment
 * variables of the running Java VM. (Recall that
 * <tt>java.lang.System.getenv()</tt> is no longer deprecated as of the 1.5
 * JDK.) The following sample program reads strings from the command line
 * and substitutes Unix-style environment variable references.</p>
 *
 * <blockquote>
 * <pre>
 * import org.clapper.util.text.MapVariableDereferencer;
 * import org.clapper.util.text.VariableDereferencer;
 * import org.clapper.util.text.VariableSubstituter;
 * import org.clapper.util.text.UnixShellVariableSubstituter;
 *
 * public class Test
 * {
 *     public static void main (String[] args) throws Throwable
 *     {
 *         VariableDereference vars = new MapVariableDereferencer (System.getenv());
 *         VariableSubstituter sub = new UnixShellVariableSubstituter();
 *
 *         for (int i = 0; i < args.length; i++)
 *         {
 *             System.out.println ("BEFORE: \"" + args[i] + "\"");
 *             System.out.println ("AFTER:  \"" + sub.substitute (args[i], vars, null));
 *         }
 *     }
 * }
 * </pre>
 * </blockquote>
 *
 * @see VariableDereferencer
 * @see VariableSubstituter
 *
 * @version $Revision$
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class MapVariableDereferencer implements VariableDereferencer
{
    /*----------------------------------------------------------------------*\
                               Inner Classes
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    /**
     * Associated Map object.
     */
    private Map<String,String> map = null;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>MapVariableDereferencer</tt> object that
     * resolves its variable references from the specified <tt>Map</tt>
     * object.
     *
     * @param map  The <tt>Map</tt> object from which to resolve
     *             variable references.
     */
    public MapVariableDereferencer (Map<String,String> map)
    {
        this.map = map;
    }

    /**
     * Create a new <tt>MapVariableDereferencer</tt> object that resolves
     * its variable references from the specified <tt>Properties</tt>
     * object.
     *
     * @param properties  The <tt>Properties</tt> object from which to resolve
     *                    variable references.
     */
    public MapVariableDereferencer (Properties properties)
    {
        // Use a type-safe wrapper

        map = new PropertiesMap (properties);
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
        String result = map.get (varName);

        return (result == null) ? "" : result;
    }
}
