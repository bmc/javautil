/*---------------------------------------------------------------------------*\
  $Id$
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
     *
     * @return The variable's value. If the variable has no value, this
     *         method must return the empty string (""). It is important
     *         <b>not</b> to return null.
     */
    public String getValue (String varName)
    {
        Object result = map.get (varName);

        return (result == null) ? "" : result.toString();
    }
}
