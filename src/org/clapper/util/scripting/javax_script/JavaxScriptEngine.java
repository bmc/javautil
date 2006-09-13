/*---------------------------------------------------------------------------*\
 $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.scripting.javax_script;

import java.io.IOException;
import java.io.Reader;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.clapper.util.scripting.UnifiedCompiledScript;
import org.clapper.util.scripting.UnifiedScriptEngine;
import org.clapper.util.scripting.UnifiedScriptException;

/**
 * @version <tt>$Revision$</tt>
 */
public class JavaxScriptEngine extends UnifiedScriptEngine
{
    /*----------------------------------------------------------------------*\
                                Inner Classes
    \*----------------------------------------------------------------------*/

    private class JavaxCompiledScript implements UnifiedCompiledScript
    {
        CompiledScript compiledScript = null;
    }

    /*----------------------------------------------------------------------*\
                               Private Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                             Private Instance Data
    \*----------------------------------------------------------------------*/

    private ScriptEngine scriptEngine;

     /*----------------------------------------------------------------------*\
                                   Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Creates a new instance of JavaxScriptEngine
     *
     * @param scriptEngine the real script engine
     */
    JavaxScriptEngine(ScriptEngine scriptEngine)
    {
        this.scriptEngine = scriptEngine;
    }

    /*----------------------------------------------------------------------*\
                                Public Methods
    \*----------------------------------------------------------------------*/

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
     */
    public UnifiedCompiledScript compile(Reader scriptReader)
        throws IOException,
               UnifiedScriptException
    {
        JavaxCompiledScript result = null;

        if (scriptEngine instanceof Compilable)
        {
            try
            {
                Compilable compiler = (Compilable) scriptEngine;
                result = new JavaxCompiledScript();
                result.compiledScript = compiler.compile(scriptReader);
            }

            catch (ScriptException ex)
            {
                throw new UnifiedScriptException
                    ("Unable to compile script", ex);
            }
        }

        return result;
    }

    /**
     * Execute a previously compiled script.
     *
     * @param compiledScript  the compiled script
     *
     * @throws IOException            error reading script
     * @throws UnifiedScriptException compilation error
     *
     * @see #compile(File)
     * @see #compile(Reader)
     */
    public void exec(UnifiedCompiledScript compiledScript)
        throws IOException,
               UnifiedScriptException
    {
        assert(compiledScript instanceof JavaxCompiledScript);

        CompiledScript realCompiledScript =
            ((JavaxCompiledScript) compiledScript).compiledScript;
        try
        {
            realCompiledScript.eval();
        }

        catch (ScriptException ex)
        {
            throw new UnifiedScriptException ("Error running compiled script",
                                               ex);
        }
    }

    /**
     * Execute a script.
     *
     *
     * @param scriptReader  a <tt>Reader</tt> that will produce the script
     *
     * @throws IOException             error reading script
     * @throws UnifiedScriptException compilation error
     */
    public void exec(Reader scriptReader)
        throws IOException,
               UnifiedScriptException
    {
        try
        {
            scriptEngine.eval(scriptReader);
        }

        catch (ScriptException ex)
        {
            throw new UnifiedScriptException ("Error running script", ex);
        }
    }

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
    public Object eval(String scriptString)
        throws UnifiedScriptException
    {
         try
        {
            return scriptEngine.eval(scriptString);
        }

        catch (ScriptException ex)
        {
            throw new UnifiedScriptException
                ("Can't evaluate script \"" + scriptString + "\"",
                 ex);
        }
    }

    /*----------------------------------------------------------------------*\
                               Protected Methods
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Private Methods
    \*----------------------------------------------------------------------*/

}
