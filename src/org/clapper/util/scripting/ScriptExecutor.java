/*---------------------------------------------------------------------------*\
 $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.scripting;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.clapper.util.text.TextUtil;

/**
 * Abstract base class for classes that can execute scripts. This class
 * defines a common interface between the Bean Scripting Framework
 * and the Java 6 JSR 223 scripting framework, allowing callers to use
 * a common approach to interact with either one.
 *
 * @version <tt>$Revision$</tt>
 */
public abstract class ScriptExecutor
{
    /*----------------------------------------------------------------------*\
                               Private Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                             Private Instance Data
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                   Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Cannot be instantiated directly.
     */
    protected ScriptExecutor()
    {
    }

    /*----------------------------------------------------------------------*\
                                Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Factory method to initialize the requested scripting framework.
     * Throws an exception if the framework cannot be created (which can
     * happen, for instance, when running on a Java 5 platform, or when
     * running in an environment where the Bean Scripting Framework does
     * not exist.
     *
     * @param type  the framework type
     *
     * @return the appropriate <tt>ScriptExecutor</tt> object.
     *
     * @throws ScriptExecutorException on error
     */
    public static ScriptExecutor getScriptExecutor(ScriptFrameworkType type)
        throws ScriptExecutorException
    {
        ScriptExecutor result = null;
        String className = null;
        String packageName = ScriptExecutor.class.getPackage().getName();

        switch (type)
        {
            case JSR_223:
                className = TextUtil.join (".",
                                           packageName,
                                           "jsr223",
                                           "JSR223ScriptExecutor");
                break;

            case BSF:
                className = TextUtil.join (".",
                                           packageName,
                                           "bsf",
                                           "BSFScriptExecutor");
                break;

            default:
                assert(false);
        }

        try
        {
            Class<?> cls = Class.forName(className);
            result = (ScriptExecutor) cls.newInstance();
            result.init();
        }

        catch (ClassNotFoundException ex)
        {
            throw new ScriptExecutorException
                ("Class \"" + className + "\" not found",
                 ex);
        }

        catch (InstantiationException ex)
        {
            throw new ScriptExecutorException
                ("Unable to instantiate class \"" + className + "\"",
                 ex);
        }

        catch (ExceptionInInitializerError ex)
        {
            throw new ScriptExecutorException(ex.getCause());
        }

        catch (IllegalAccessException ex)
        {
            throw new ScriptExecutorException
                ("Default constructor for \"" + className + "\" is not " +
                 "accessible.",
                 ex);
        }

        return result;
    }

    /**
     * Put an object into the script environment. This operation is also known
     * as "binding" an object to the scripting environment. If the scripting
     * infrastructure supports different scopes (e.g., JSR 223), then
     * this method puts the object in the global scope.
     *
     * @param name   the name by which the object will be known to scripts
     * @param object the object
     *
     * @throws ScriptExecutorException on error
     */
    public abstract void put(String name, Object object)
        throws ScriptExecutorException;

    /**
     * Clear all current bindings.
     *
     * @see #put
     *
     * @throws ScriptExecutorException on error
     */
    public abstract void clearBindings()
        throws ScriptExecutorException;


    /**
     * Compile a script, if possible, returning an object that implements
     * the {@link ScriptExecutorCompiledScript} interface. (The interface's
     * name was deliberately chosen to avoid conflicts with the JSR 223
     * <tt>CompiledScript</tt> interface. If the underlying script engine
     * does not support compilation, then this routine simply returns null
     * (rather than throwing an exception).
     *
     * @param scriptReader  a <tt>Reader</tt> that will produce the script
     * @param language      a recognized script language name
     *
     * @return a representation of the compiled script, or null if the
               underlying scripting engine does not support compilation
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #compileScript(String)
     */
    public abstract ScriptExecutorCompiledScript
    compileScript(Reader scriptReader, String language)
        throws IOException,
               ScriptExecutorException;

    /**
     * Compile a script, if possible, returning an object that implements
     * the {@link ScriptExecutorCompiledScript} interface. (The interface's
     * name was deliberately chosen to avoid conflicts with the JSR 223
     * <tt>CompiledScript</tt> interface. If the underlying script engine
     * does not support compilation, then this routine simply returns null
     * (rather than throwing an exception).
     *
     * @param scriptString  a <tt>String</tt> containing the script
     * @param language      a recognized script language name
     *
     * @return a representation of the compiled script, or null if the
               underlying scripting engine does not support compilation
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #compileScript(String)
     */
    public ScriptExecutorCompiledScript compileScript(String scriptString,
                                                      String language)
        throws IOException,
               ScriptExecutorException
    {
        return compileScript(new StringReader(scriptString), language);
    }

    /**
     * Compile a script, if possible, returning an object that implements
     * the {@link ScriptExecutorCompiledScript} interface. (The interface's
     * name was deliberately chosen to avoid conflicts with the JSR 223
     * <tt>CompiledScript</tt> interface. If the underlying script engine
     * does not support compilation, then this routine simply returns null
     * (rather than throwing an exception).
     *
     * @param scriptFile  file containing the script; the file's extension
     *                    is used to determine the language
     *
     * @return a representation of the compiled script, or null if the
               underlying scripting engine does not support compilation
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #compileScript(String)
     */
    public abstract ScriptExecutorCompiledScript compileScript(File scriptFile)
        throws IOException,
               ScriptExecutorException;

    /**
     * Execute a script.
     *
     * @param scriptReader  a <tt>Reader</tt> that will produce the script
     * @param language      a recognized script language name
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #execScript(String)
     */
    public abstract void execScript(Reader scriptReader, String language)
        throws IOException,
               ScriptExecutorException;

    /**
     * Execute a script.
     *
     * @param scriptFile  file containing the script; the file's extension
     *                    is used to determine the language
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #execScript(String)
     */
    public abstract void execScript(File scriptFile)
        throws IOException,
               ScriptExecutorException;

    /**
     * Execute a script.
     *
     * @param scriptString string containing the script
     * @param language     the scripting language
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #execScript(String)
     */
    public void execScript(String scriptString, String language)
        throws IOException,
               ScriptExecutorException
    {
        execScript(new StringReader(scriptString), language);
    }

    /**
     * Execute a previously compiled script.
     *
     * @param compiledScript  the compiled script
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #compileScript(File)
     * @see #compileScript(Reader)
     */
    public abstract void execScript(ScriptExecutorCompiledScript compiledScript)
        throws IOException,
               ScriptExecutorException;

    /*----------------------------------------------------------------------*\
                               Protected Methods
    \*----------------------------------------------------------------------*/

    /**
     * Initialize the <tt>ScriptExecutor</tt>.
     *
     * @throws ScriptExecutorException on error
     */
    protected abstract void init()
        throws ScriptExecutorException;

    /*----------------------------------------------------------------------*\
                                Private Methods
    \*----------------------------------------------------------------------*/

}
