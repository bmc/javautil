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

package org.clapper.util.scripting;

import java.io.File;
import java.util.Map;
import org.clapper.util.io.FileUtil;
import org.clapper.util.logging.Logger;
import org.clapper.util.text.TextUtil;

/**
 * A script engine manager that provides a common interface between the
 * Apache Jakarta Bean Scripting Framework (BSF) and the Java 6 JSR 223
 * (<tt>javax.script</tt>) framework, allowing callers to use either
 * underlying framework without changing code. This class is modeled
 * on the JSR 223 interface, though it is much simpler.
 *
 *
 * @version <tt>$Revision$</tt>
 */
public abstract class UnifiedScriptEngineManager
{
    /*----------------------------------------------------------------------*\
                                  Private Data
    \*----------------------------------------------------------------------*/

    private static final Logger log =
        new Logger(UnifiedScriptEngineManager.class);

    /*----------------------------------------------------------------------*\
                                   Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Creates a new instance of UnifiedScriptEngineManager
    */
    protected UnifiedScriptEngineManager()
    {
    }

    /*----------------------------------------------------------------------*\
                                Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the script engine manager for a specific underlying scripting
     * framework.
     *
     * @param type  which underlying scripting framework to use
     *
     * @throws UnifiedScriptException on error
     *
     * @see #getManager(ScriptFrameworkType[])
     */
    public static final UnifiedScriptEngineManager
    getManager(ScriptFrameworkType type)
        throws UnifiedScriptException
    {
        UnifiedScriptEngineManager result = null;
        String className = null;
        String packageName =
            UnifiedScriptEngineManager.class.getPackage().getName();

        switch (type)
        {
            case JAVAX_SCRIPT:
                className = TextUtil.join (".",
                                           packageName,
                                           "javax_script",
                                           "JavaxScriptEngineManager");
                break;

            case BSF:
                className = TextUtil.join (".",
                                           packageName,
                                           "bsf",
                                           "BSFScriptEngineManager");
                break;

            default:
                assert(false);
        }

        try
        {
            Class<?> cls = Class.forName(className);
            result = (UnifiedScriptEngineManager) cls.newInstance();
        }

        catch (ClassNotFoundException ex)
        {
            throw new UnifiedScriptException
                ("Class \"" + className + "\" not found",
                 ex);
        }

        catch (InstantiationException ex)
        {
            throw new UnifiedScriptException
                ("Unable to instantiate class \"" + className + "\"",
                 ex);
        }

        catch (ExceptionInInitializerError ex)
        {
            throw new UnifiedScriptException(ex.getCause());
        }

        catch (IllegalAccessException ex)
        {
            throw new UnifiedScriptException
                ("Default constructor for \"" + className + "\" is not " +
                 "accessible.",
                 ex);
        }

        return result;
    }

    /**
     * Get the <tt>UnifiedScriptEngineManager</tt> for the first available
     * underlying scripting framework. The order the frameworks are checked
     * is the order they appear in the passed-in array.
     *
     * @param types the framework types to check, in order.
     *
     * @return the first framework found
     *
     * @throws UnifiedScriptException if no script managers could be found
     *
     * @see #getType
     * @see #getManager(ScriptFrameworkType)
     */
    public static final UnifiedScriptEngineManager
    getManager(ScriptFrameworkType[] types)
        throws UnifiedScriptException
    {
        UnifiedScriptEngineManager manager = null;

        for (ScriptFrameworkType type : types)
        {
            try
            {
                log.info("Trying to find " + type + " scripting API");
                manager = getManager(type);
                break;
            }

            catch (Exception ex)
            {
                log.error("Can't get script framework of type \"" +
                          type.toString() + "\"",
                          ex);
            }
        }

        if (manager == null)
        {
            String sep = "";
            StringBuilder buf = new StringBuilder(32);
            for (ScriptFrameworkType type : ScriptFrameworkType.values())
            {
                buf.append(sep);
                buf.append(type.toString());
                sep = ", ";
            }

            throw new UnifiedScriptException
                ("Unable to find any of the following script APIs: " +
                 buf.toString());
        }

        return manager;
    }

    /**
     * Get the framework type ({@link ScriptFrameworkType} associated with
     * the <tt>UnifiedScriptEngineManager</tt> object.
     *
     * @return the framework type
     */
    public abstract ScriptFrameworkType getType();

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
    public abstract Map<String,Object> getBindings()
        throws UnifiedScriptException;

    /**
     * Clear all current bindings.
     *
     * @throws UnifiedScriptException on error
     *
     * @see #put
     */
    public abstract void clearBindings()
        throws UnifiedScriptException;


    /**
     * Put an object into the script environment. This operation is also known
     * as "binding" an object to the scripting environment. If the scripting
     * infrastructure supports different scopes (e.g., JSR 223), then
     * this method puts the object in the global scope.
     *
     * @param name   the name by which the object will be known to scripts
     * @param object the object
     *
     * @throws UnifiedScriptException on error
     */
    public abstract void put(String name, Object object)
        throws UnifiedScriptException;

    /**
     * Get the value for a specified key in the object bindings.
     *
     * @param key  the key
     *
     * @return the bound object, or null
     */
    public abstract Object get(String key);

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
    public abstract UnifiedScriptEngine getEngineByName(String language)
        throws UnifiedScriptException;

    /**
     * Get a <tt>UnifiedScriptEngine</tt> for the specified language, by
     * mapping a file name extension to the language.
     *
     * @param extension  the extension
     *
     * @return the script engine, or null if no engine for that language
     *         can be found
     *
     * @throws UnifiedScriptException on error
     */
    public abstract UnifiedScriptEngine getEngineByExtension(String extension)
        throws UnifiedScriptException;

    /**
     * Get a <tt>UnifiedScriptEngine</tt> for a given file, by using the
     * file's extension.
     *
     * @param file  the file
     *
     * @return he script engine, or null if no engine for that language
     *         can be found
     *
     * @throws UnifiedScriptException on error
     *
     * @see #getEngineByExtension
     */
    public UnifiedScriptEngine getEngineForFile(File file)
        throws UnifiedScriptException
    {
        return getEngineByExtension(FileUtil.getFileNameExtension(file));
    }

    /*----------------------------------------------------------------------*\
                               Protected Methods
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Private Methods
    \*----------------------------------------------------------------------*/
}
