/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.scripting;

/**
 * Defines the set of script frameworks supported by the
 * {@link UnifiedScriptEngineManager} class. There are currently two legal
 * values:
 *
 * <ul>
 *   <li> <tt>JAVAX_SCRIPT</tt>: use the <tt>javax.script</tt> (a.k.a.,
 *        JSR 223) scripting infrastructure, available as of Java 6.
 *   <li> <tt>BSF</tt>: Use the Apache Jakarta Bean Scripting Framework (BSF).
 * </ul>
 *
 * @version <tt>$Revision$</tt>
 * @see UnifiedScriptEngineManager#getManager(ScriptFrameworkType)
 * @see UnifiedScriptEngineManager#getManager(ScriptFrameworkType[])
 */
public enum ScriptFrameworkType
{
    JAVAX_SCRIPT,
    BSF;

    /**
     * Converts a string to a <tt>ScriptFrameworkType</tt> value, with the
     * following features:
     *
     * <ul>
     *  <li> Comparison is case-blind.
     *  <li> Treats "." as "_" for the purposes of comparison. (That is,
     *       "javax.script" and "javax_script" both match the value
     *       {@link #JAVAX_SCRIPT}.)
     * </ul>
     *
     * @param s  the string to convert
     *
     * @return the corresponding value, or null if no match
     */
    public static final ScriptFrameworkType getTypeFromString(String s)
    {
        ScriptFrameworkType result = null;

        s = s.replaceAll("\\.", "_");
        for (ScriptFrameworkType type : ScriptFrameworkType.values())
        {
            if (s.equalsIgnoreCase(type.toString()))
            {
                result = type;
                break;
            }
        }

        return result;
    }
}
