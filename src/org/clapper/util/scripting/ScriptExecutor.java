/*---------------------------------------------------------------------------*\
 $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.scripting;

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
     * Put an object into the script environment. If the scripting
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
