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
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 \*---------------------------------------------------------------------------*/

package org.clapper.util.scripting.javax_script;

import java.io.File;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.clapper.util.io.FileUtil;
import org.clapper.util.scripting.UnifiedScriptEngine;
import org.clapper.util.scripting.UnifiedScriptEngineManager;
import org.clapper.util.scripting.UnifiedScriptException;

/**
 * {@link ScriptExecutor} that uses the Apache Jakarta Bean Scripting
 * Framework.
 *
 * @version <tt>$Revision$</tt>
 */
public class JavaxScriptEngineManager extends UnifiedScriptEngineManager
{
    /*----------------------------------------------------------------------*\
                             Private Instance Data
    \*----------------------------------------------------------------------*/

    private ScriptEngineManager scriptManager = null;

    /*----------------------------------------------------------------------*\
                                   Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Creates a new instance of JavaxScriptEngineManager
     */
    public JavaxScriptEngineManager()
    {
        super();
        scriptManager = new ScriptEngineManager();
    }

    /*----------------------------------------------------------------------*\
                                Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the global object bindings. Unlike JSR 223, this interface only
     * supports global bindings (largely because BSF doesn't easily support
     * anything other than global bindings.
     *
     * @return a Map of global bindings
     *
     * @throws UnifiedScriptException on error
     *
     * @see #clearBindings
     * @see #put
     */
    public Map<String,Object> getBindings()
        throws UnifiedScriptException
    {
        return scriptManager.getBindings();
    }

    /**
     * Clear all current bindings.
     *
     * @see #put
     * @throws UnifiedScriptException on error
     */
    public void clearBindings() throws UnifiedScriptException
    {
        Bindings bindings = scriptManager.getBindings();
        if (bindings != null)
            bindings.clear();
    }

    /**
     * Put an object into the script environment. This operation is also known
     * as "binding" an object to the scripting environment. If the scripting
     * infrastructure supports different scopes (e.g., JSR 223), then
     * this method puts the object in the global scope.
     *
     * @param name   the name by which the object will be known to scripts
     * @param object the object
     * @throws UnifiedScriptException on error
     */
    public void put(String name, Object object)
        throws UnifiedScriptException
    {
        // Put the variable in the global scope.

        scriptManager.put(name, object);
    }

    /**
     * Get the value for a specified key in the object bindings.
     *
     * @param key  the key
     *
     * @return the bound object, or null
     */
    public Object get(String key)
    {
        return scriptManager.get(key);
    }

    /**
     * Get a <tt>UnifiedScriptEngine</tt> for the specified language.
     *
     * @param language the language name
     *
     * @return the script engine, or null if no engine for that language
     *         can be found
     *
     * @throws UnifiedScriptException on error
     */
    public UnifiedScriptEngine getEngineByName(String language)
        throws UnifiedScriptException
    {
        return new JavaxScriptEngine(scriptManager.getEngineByName(language));
    }

    /**
     * Get a <tt>UnifiedScriptEngine</tt> for the specified language, by
     * mapping a file name extension to the language.
     *
     * @param ext  the extension
     *
     * @return the script engine, or null if no engine for that language
     *         can be found
     *
     * @throws UnifiedScriptException on error
     */
    public UnifiedScriptEngine getEngineByExtension(String ext)
        throws UnifiedScriptException
    {
        return new JavaxScriptEngine(scriptManager.getEngineByExtension(ext));
    }

    /*----------------------------------------------------------------------*\
                               Protected Methods
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Private Methods
    \*----------------------------------------------------------------------*/
}

