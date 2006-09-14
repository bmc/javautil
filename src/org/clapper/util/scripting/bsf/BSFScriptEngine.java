/*---------------------------------------------------------------------------*\
 $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.scripting.bsf;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.clapper.util.scripting.UnifiedCompiledScript;
import org.clapper.util.scripting.UnifiedScriptEngine;
import org.clapper.util.scripting.UnifiedScriptException;

/**
 * @version <tt>$Revision$</tt>
 */
public class BSFScriptEngine extends UnifiedScriptEngine
{
    /*----------------------------------------------------------------------*\
                               Private Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                             Private Instance Data
    \*----------------------------------------------------------------------*/

    private BSFEngine bsfEngine = null;

    /*----------------------------------------------------------------------*\
                                   Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Creates a new instance of BSFScriptEngine
     *
     * @param bsfEngine the real BSF engine
     */
    BSFScriptEngine(BSFEngine bsfEngine)
    {
        this.bsfEngine = bsfEngine;
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
     * @throws UnifiedScriptException compilation error
     */
    public UnifiedCompiledScript compile(Reader scriptReader)
        throws UnifiedScriptException
    {
        // Compilation is too funky with BSF.

        return null;
    }

    /**
     * Execute a previously compiled script.
     *
     * @param compiledScript  the compiled script
     *
     * @throws UnifiedScriptException compilation error
     */
    public void exec(UnifiedCompiledScript compiledScript)
        throws UnifiedScriptException
    {
        throw new UnifiedScriptException("Compilation not supported with " +
                                          "BSF scripting engine.");
    }

    /**
     * Execute a script.
     *
     * @param scriptReader  a <tt>Reader</tt> that will produce the script
     *
     * @throws UnifiedScriptException compilation error
     */
    public void exec(Reader scriptReader)
        throws UnifiedScriptException
    {
        try
        {
            String scriptString = loadScript(scriptReader);
            bsfEngine.exec("", 0, 0, scriptString);
        }

        catch (BSFException ex)
        {
            throw new UnifiedScriptException("Error running script", ex);
        }

        catch (IOException ex)
        {
            throw new UnifiedScriptException("I/O error reading script", ex);
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
            return bsfEngine.eval("", 0, 0, scriptString);
        }

        catch (BSFException ex)
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

    /**
     * Load the contents of the external script (any file, really) into an
     * in-memory buffer.
     *
     * @param r  the reader
     *
     * @return the string representing the loaded script
     *
     * @throws IOException on error
     */
    private String loadScript (Reader r)
        throws IOException
    {
        StringWriter w = new StringWriter();
        int          c;

        while ((c = r.read()) != -1)
            w.write(c);

        r.close();

        return w.toString();
    }
}
