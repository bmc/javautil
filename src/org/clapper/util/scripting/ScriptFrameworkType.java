/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.scripting;

/**
 * Defines the set of script frameworks supported by the
 * {@link ScriptExecutor} class. There are currently two legal values:
 *
 * <ul>
 *   <li> <tt>JAVAX_SCRIPT</tt>: use the <tt>javax.script</tt> (a.k.a.,
 *        JSR 223) scripting infrastructure, available as of Java 6.
 *   <li> <tt>BSF</tt>: Use the Apache Jakarta Bean Scripting Framework (BSF).
 * </ul>
 *
 * @version <tt>$Revision$</tt>
 * @see ScriptExecutor#getScriptExecutor
 */
public enum ScriptFrameworkType
{
    JAVAX_SCRIPT,
    BSF
}
