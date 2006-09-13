/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.scripting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * A script engine interface that provides a common set of methods that can
 * map both the Apache Jakarta Bean Scripting Framework (BSF) and the Java 6 
 * JSR 223 (<tt>javax.script</tt>) framework, allowing callers to use either
 * underlying framework without changing code. This class is modeled
 * on the JSR 223 interface, though it is much simple
 *
 * @version <tt>$Revision$</tt>
 */
public abstract class UnifiedScriptEngine
{
    /**
     * Compile a script, if possible, returning an object that implements
     * the {@link UnifiedCompiledScript} interface. (The interface's
     * name was deliberately chosen to avoid conflicts with the JSR 223
     * <tt>CompiledScript</tt> interface. If the underlying script engine
     * does not support compilation, then this routine simply returns null
     * (rather than throwing an exception).
     *
     * @param scriptReader  a <tt>Reader</tt> that will produce the script
     *
     * @return a representation of the compiled script, or null if the
     *         underlying scripting engine does not support compilation
     *
     * @throws IOException             error reading script
     * @throws UnifiedScriptException compilation error
     *
     * @see #compile(String,String)
     * @see #compile(File)
     */
    public abstract UnifiedCompiledScript compile(Reader scriptReader)
        throws IOException,
               UnifiedScriptException;

    /**
     * Compile a script, if possible, returning an object that implements
     * the {@link UnifiedCompiledScript} interface. (The interface's
     * name was deliberately chosen to avoid conflicts with the JSR 223
     * <tt>CompiledScript</tt> interface. If the underlying script engine
     * does not support compilation, then this routine simply returns null
     * (rather than throwing an exception).
     *
     * @param scriptString  a <tt>String</tt> containing the script
     *
     * @return a representation of the compiled script, or null if the
     *         underlying scripting engine does not support compilation
     *
     * @throws IOException             error reading script
     * @throws UnifiedScriptException compilation error
     *
     * @see #compile(File)
     * @see #compile(Reader,String)
     */
    public final UnifiedCompiledScript compile(String scriptString)
        throws IOException,
               UnifiedScriptException
    {
        return compile(new StringReader(scriptString));
    }

    /**
     * Compile a script, if possible, returning an object that implements
     * the {@link UnifiedCompiledScript} interface. (The interface's
     * name was deliberately chosen to avoid conflicts with the JSR 223
     * <tt>CompiledScript</tt> interface. If the underlying script engine
     * does not support compilation, then this routine simply returns null
     * (rather than throwing an exception).
     *
     * @param scriptFile  file containing the script; the file's extension
     *                    is used to determine the language
     *
     * @return a representation of the compiled script, or null if the
     *           underlying scripting engine does not support compilation
     *
     * @throws IOException            error reading script
     * @throws UnifiedScriptException compilation error
     *
     * @see #compile(String,String)
     * @see #compile(Reader,String)
     */
    public final UnifiedCompiledScript compile(File scriptFile)
        throws IOException,
               UnifiedScriptException
    {
        return compile(new FileReader(scriptFile));
    }

    /**
     * Execute a script.
     *
     * @param scriptReader  a <tt>Reader</tt> that will produce the script
     *
     * @throws IOException            error reading script
     * @throws UnifiedScriptException compilation error
     *
     * @see #exec(String,String)
     * @see #exec(File)
     */
    public abstract void exec(Reader scriptReader)
        throws IOException,
               UnifiedScriptException;

    /**
     * Execute a script.
     *
     * @param scriptFile  file containing the script; the file's extension
     *                    is used to determine the language
     *
     * @throws IOException            error reading script
     * @throws UnifiedScriptException compilation error
     *
     * @see #exec(String,String)
     * @see #exec(Reader,String)
     */
    public final void exec(File scriptFile)
        throws IOException,
               UnifiedScriptException
    {
        exec(new FileReader(scriptFile));
    }

    /**
     * Execute a script.
     *
     * @param scriptString string containing the script
     * @param language     the scripting language
     *
     * @throws IOException             error reading script
     * @throws UnifiedScriptException compilation error
     *
     * @see #exec(File)
     * @see #exec(Reader,String)
     */
    public final void exec(String scriptString)
        throws IOException,
               UnifiedScriptException
    {
        exec(new StringReader(scriptString));
    }

    /**
     * Execute a previously compiled script.
     *
     * @param compiledScript  the compiled script
     *
     * @throws IOException            error reading script
     * @throws UnifiedScriptException compilation error
     *
     * @see #exec(File)
     * @see #exec(Reader,String)
     */
    public abstract void exec(UnifiedCompiledScript compiledScript)
        throws IOException,
               UnifiedScriptException;

    /**
     * Evaluate a script or a script snippet and return the value of the
     * evaluation.
     *
     * @param scriptString the script string
     *
     * @return the result of the evaluation, if anything
     *
     * @throws UnifiedScriptException on error
     */
    public abstract Object eval(String scriptString)
        throws UnifiedScriptException;
}
