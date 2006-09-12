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

package org.clapper.util.scripting.bsf;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.clapper.util.scripting.ScriptExecutor;
import org.clapper.util.scripting.ScriptExecutorCompiledScript;
import org.clapper.util.scripting.ScriptExecutorException;

/**
 * {@link ScriptExecutor} that uses the Apache Jakarta Bean Scripting
 * Framework.
 *
 * @version <tt>$Revision$</tt>
 */
public class BSFScriptExecutor extends ScriptExecutor
{
    /*----------------------------------------------------------------------*\
                               Private Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                             Private Instance Data
    \*----------------------------------------------------------------------*/

    /**
     * Bean Scripting Framework manager
     */
    private BSFManager bsfManager = null;

    /*----------------------------------------------------------------------*\
                                   Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Creates a new instance of BSFScriptExecutor
     */
    public BSFScriptExecutor()
    {
        super();
    }

    /*----------------------------------------------------------------------*\
                                Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Clear all current bindings.
     *
     * @see #put
     * @throws ScriptExecutorException on error
     */
    public void clearBindings() throws ScriptExecutorException
    {
    }

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
     *               underlying scripting engine does not support compilation
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #compileScript(File)
     * @see #compileScript(String,String)
     */
    public ScriptExecutorCompiledScript
    compileScript(Reader scriptReader, String language)
        throws IOException,
               ScriptExecutorException
    {
        // Compilation is too funky with BSF.

        return null;
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
     *               underlying scripting engine does not support compilation
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #compileScript(String,String)
     * @see #compileScript(Reader,String)
     */
    public ScriptExecutorCompiledScript compileScript(File scriptFile)
        throws IOException,
               ScriptExecutorException
    {
        // Compilation is too funky with BSF.

        return null;
    }

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
    public void execScript(File scriptFile)
        throws IOException,
               ScriptExecutorException
    {
        try
        {
            String language = BSFManager.getLangFromFilename(scriptFile.getName());
            if (language == null)
            {
                throw new ScriptExecutorException
                    ("Can't find script language for file name \"" +
                     scriptFile.getName() + "\"");
            }

            execScript(new FileReader(scriptFile), language);
        }

        catch (BSFException ex)
        {
            throw new ScriptExecutorException(ex);
        }
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
     * @see #compileScript(Reader,String)
     */
    public void execScript(ScriptExecutorCompiledScript compiledScript)
        throws IOException,
               ScriptExecutorException
    {
        throw new ScriptExecutorException("Compilation not supported with " +
                                          "BSF scripting engine.");
    }

    /**
     * Execute a script.
     *
     * @param scriptReader  a <tt>Reader</tt> that will produce the script
     * @param language      a recognized script language name
     *
     * @throws IOException             error reading script
     * @throws ScriptExecutorException compilation error
     *
     * @see #execScript(File)
     * @see #execScript(String,String)
     */
    public void execScript(Reader scriptReader, String language)
        throws IOException,
               ScriptExecutorException
    {
        BSFEngine engine;
        try
        {
            engine = bsfManager.loadScriptingEngine(language);
        }

        catch (BSFException ex)
        {
            throw new ScriptExecutorException
                ("Can't load BSF scripting engine for language \"" +
                 language + "\"",
                 ex);
        }

        try
        {
            String scriptString = loadScript(scriptReader);
            engine.exec("", 0, 0, scriptString);
        }

        catch (BSFException ex)
        {
            throw new ScriptExecutorException ("Error running script", ex);
        }
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
    public void put(String name, Object object)
        throws ScriptExecutorException
    {
        try
        {
            bsfManager.declareBean(name, object, object.getClass());
        }

        catch (BSFException ex)
        {
            throw new ScriptExecutorException("Can't declare BSF bean \"" + 
                                              name + "\"",
                                              ex);
        }
    }

    /*----------------------------------------------------------------------*\
                               Protected Methods
    \*----------------------------------------------------------------------*/

    /**
     * Initialize the <tt>ScriptExecutor</tt>.
     *
     * @throws ScriptExecutorException on error
     */
    protected void init() throws ScriptExecutorException
    {
        // Create the BSFManager

        bsfManager = new BSFManager();

        // Register some additional scripting languages.

        BSFManager.registerScriptingEngine("ObjectScript",
                                           "oscript.bsf.ObjectScriptEngine",
                                           new String[] {"os"});
        BSFManager.registerScriptingEngine("groovy",
                                           "org.codehaus.groovy.bsf.GroovyEngine",
                                           new String[] {"groovy", "gy"});
        BSFManager.registerScriptingEngine("beanshell",
                                           "bsh.util.BeanShellBSFEngine",
                                           new String[] {"bsh"});
    }

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

