/*---------------------------------------------------------------------------*\
 $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2006 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M.a Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*---------------------------------------------------------------------------*/

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
            case JAVAX_SCRIPT:
                className = TextUtil.join (".",
                                           packageName,
                                           "javax_script",
                                           "JavaxScriptExecutor");
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
     * @see #compileScript(String,String)
     * @see #compileScript(File)
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
     * @see #compileScript(File)
     * @see #compileScript(Reader,String)
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
     * @see #compileScript(String,String)
     * @see #compileScript(Reader,String)
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
     * @see #execScript(String,String)
     * @see #execScript(File)
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
     * @see #execScript(String,String)
     * @see #execScript(Reader,String)
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
     * @see #execScript(File)
     * @see #execScript(Reader,String)
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
     * @see #execScript(File)
     * @see #execScript(Reader,String)
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
