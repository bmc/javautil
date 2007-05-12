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
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
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
     *         method must return null.
     */
    public String getVariableValue (String varName, Object context)
    {
        return map.get(varName);
    }
}
