/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms are permitted provided
  that: (1) source distributions retain this entire copyright notice and
  comment; and (2) modifications made to the software are prominently
  mentioned, and a copy of the original software (or a pointer to its
  location) are included. The name of the author may not be used to endorse
  or promote products derived from this software without specific prior
  written permission.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
  WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.

  Effectively, this means you can do what you want with the software except
  remove this notice or take advantage of the author's name. If you modify
  the software and redistribute your modified version, you must indicate that
  your version is a modification of the original, and you must provide either
  a pointer to or a copy of the original.
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;

/**
 * <p><tt>Logger</tt> wraps the Apache Jakarta Commons Logging <tt>Log</tt>
 * class. This class supports most of the logging methods, but it doesn't
 * actually instantiate any Jakarta Commons Logging <tt>Log</tt> objects
 * unless logging is explicitly enabled via a call to the
 * {@link #enableLogging enableLogging()} method. The first call to
 * <tt>enableLogging()</tt> traverses the list of instantiated
 * <tt>Logger</tt> objects and creates underlying Jakarta Commons Logging
 * <tt>Log</tt> objects for each <tt>Logger</tt>. Any <tt>Logger</tt> objects
 * created after <tt>enableLogging()</tt> is called are automatically
 * enabled.</p>
 *
 * <p>This approach prevents any interaction with the real logging layer,
 * unless logging is explicitly enabled (e.g., because a command-line
 * option has been specified). Among other things, this avoids annoying
 * startup messages from Log4J, which insists on writing warning messages
 * to the console when it can't find a configuration file.</p>
 *
 * <p>The Jakarta Commons Logging API does not need to be present for this
 * class to compile. The <tt>enableLogging()</tt> method will use the
 * default class loader to locate the appropriate Commons Logging classes
 * at runtime.</p>
 *
 * @see #enableLogging
 * @see <a href="http://jakarta.apache.org/commons/logging/index.html">Jakarta Commons Logging API</a>
 *
 * @version <tt>$Revision$</tt>
 */
public class Logger
{
    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    /**
     * Fully-qualified name of Commons Logging API's LogFactory class.
     */
    private static final String LOG_FACTORY_CLASS_NAME =
                                     "org.apache.commons.logging.LogFactory";

    /*----------------------------------------------------------------------*\
                           Private Instance Data
    \*----------------------------------------------------------------------*/

    /**
     * The real commons logging object. Not instantiated unless asked for.
     */
    private Object log = null;

    /**
     * The class name to use when instantiating the underlying Log object.
     */
    private String className = null;

    /**
     * The list of existing <tt>Logger</tt> objects.
     */
    private static Collection loggers = new ArrayList();

    /**
     * Whether or not logging is enabled.
     */
    private static boolean enabled = false;

    /**
     * The Commons Logging API LogFactory class, as a Class object.
     */
    private static Class commonsLogFactory = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>Logger</tt> object
     *
     * @param className  the class name to associate with the object
     */
    public Logger (String className)
    {
        this.className = className;

        synchronized (loggers)
        {
            loggers.add (this);

            // Handle the case where the logger is instantiated after all
            // the loggers are enabled.

            if (enabled)
                enableLogger (this);
        }
    }

    /**
     * Construct a new <tt>Logger</tt> object
     *
     * @param cls  the class whose name should be associated with the object
     */
    public Logger (Class cls)
    {
        this (cls.getName());
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Enable logging by instantiating the underlying <tt>Log</tt> objects.
     * Enables logging for all instantiated <tt>Logger</tt> objects, and
     * ensures that any subsequently instantiated <tt>Logger</tt> objects will
     * be enabled.
     *
     * @throws UnsupportedOperationException  could not find Commons Logging
     *                                        API
     */
    public static void enableLogging()
        throws UnsupportedOperationException
    {
        synchronized (loggers)
        {
            if (! enabled)
            {
                for (Iterator it = loggers.iterator(); it.hasNext(); )
                    enableLogger ((Logger) it.next());

                enabled = true;
            }
        }
    }

    /**
     * Log a message with debug log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void debug (Object message)
    {
        if (log != null)
            logMessage (log, "debug", message);
    }

    /**
     * Log an error with debug log level.
     *
     * @param message  The message to convert to a string and log
     * @param ex       The exception to log with the message
     *
     */
    public void debug (Object message, Throwable ex)
    {
        if (log != null)
            logMessage (log, "debug", message, ex);
    }

    /**
     * Log a message with error log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void error (Object message)
    {
        if (log != null)
            logMessage (log, "error", message);
    }

    /**
     * Log an error with error log level.
     *
     * @param message  The message to convert to a string and log
     * @param ex       The exception to log with the message
     *
     */
    public void error (Object message, Throwable ex)
    {
        if (log != null)
            logMessage (log, "error", message, ex);
    }

    /**
     * Log a message with fatal log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void fatal (Object message)
    {
        if (log != null)
            logMessage (log, "fatal", message);
    }

    /**
     * Log an error with fatal log level.
     *
     * @param message  The message to convert to a string and log
     * @param ex       The exception to log with the message
     *
     */
    public void fatal (Object message, Throwable ex)
    {
        if (log != null)
            logMessage (log, "fatal", message, ex);
    }

    /**
     * Log a message with info log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void info (Object message)
    {
        if (log != null)
            logMessage (log, "info", message);
    }

    /**
     * Log an error with info log level.
     *
     * @param message  The message to convert to a string and log
     * @param ex       The exception to log with the message
     *
     */
    public void info (Object message, Throwable ex)
    {
        if (log != null)
            logMessage (log, "info", message, ex);
    }

    /**
     * Log a message with trace log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void trace (Object message)
    {
        if (log != null)
            logMessage (log, "trace", message);
    }

    /**
     * Log an error with trace log level.
     *
     * @param message  The message to convert to a string and log
     * @param ex       The exception to log with the message
     *
     */
    public void trace (Object message, Throwable ex)
    {
        if (log != null)
            logMessage (log, "trace", message, ex);
    }

    /**
     * Log a message with warn log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void warn (Object message)
    {
        if (log != null)
            logMessage (log, "warn", message);
    }

    /**
     * Log an error with warn log level.
     *
     * @param message  The message to convert to a string and log
     * @param ex       The exception to log with the message
     *
     */
    public void warn (Object message, Throwable ex)
    {
        if (log != null)
            logMessage (log, "warn", message, ex);
    }

    /**
     * Determine whether debug logging is currently enabled.
     *
     * @return <tt>true</tt> if debug logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isDebugEnabled()
    {
        return (log == null) ? false : isEnabled (log, "isDebugEnabled");
    }

    /**
     * Determine whether error logging is currently enabled.
     *
     * @return <tt>true</tt> if error logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isErrorEnabled()
    {
        return (log == null) ? false : isEnabled (log, "isErrorEnabled");
    }

    /**
     * Determine whether fatal logging is currently enabled.
     *
     * @return <tt>true</tt> if fatal logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isFatalEnabled()
    {
        return (log == null) ? false : isEnabled (log, "isFatalEnabled");
    }

    /**
     * Determine whether info logging is currently enabled.
     *
     * @return <tt>true</tt> if info logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isInfoEnabled()
    {
        return (log == null) ? false : isEnabled (log, "isInfoEnabled");
    }

    /**
     * Determine whether trace logging is currently enabled.
     *
     * @return <tt>true</tt> if trace logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isTraceEnabled()
    {
        return (log == null) ? false : isEnabled (log, "isTraceEnabled");
    }

    /**
     * Determine whether warn logging is currently enabled.
     *
     * @return <tt>true</tt> if warn logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isWarnEnabled()
    {
        return (log == null) ? false : isEnabled (log, "isWarnEnabled");
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private static void enableLogger (Logger logger)
        throws UnsupportedOperationException
    {
        try
        {
            if (commonsLogFactory == null)
                commonsLogFactory = Class.forName (LOG_FACTORY_CLASS_NAME);
        }

        catch (ClassNotFoundException ex)
        {
            throw new UnsupportedOperationException
                ("Can't find Jakarta Commons Logging API class \""
               + LOG_FACTORY_CLASS_NAME
               + "\"");
        }

        // Now, attempt to find the class-level getLog() method.

        Method getLogMethod = null;

        try
        {
            getLogMethod = commonsLogFactory.getMethod ("getLog",
                                                        new Class[]
                                                        {
                                                            String.class
                                                        });
        }

        catch (NoSuchMethodException ex)
        {
            throw new UnsupportedOperationException
                ("Commons Logging class \""
               + LOG_FACTORY_CLASS_NAME
               + "\" does not contain an accessible method getLog(String)");
        }

        catch (SecurityException ex)
        {
            throw new UnsupportedOperationException
                ("Commons Logging class \""
               + LOG_FACTORY_CLASS_NAME
               + "\" does not contain an accessible method getLog(String)");
        }

        if (! Modifier.isStatic (getLogMethod.getModifiers()))
        {
            throw new UnsupportedOperationException
                ("Commons Logging method "
               + LOG_FACTORY_CLASS_NAME
               + ".getLog(String) is not static.");
        }

        try
        {
            logger.log = getLogMethod.invoke (null,
                                              new Object[] {logger.className});
        }

        catch (Exception ex)
        {
            throw new UnsupportedOperationException
                ("Can't invoke method "
               + LOG_FACTORY_CLASS_NAME
               + ".getLog() for class \""
               + logger.className
               + "\": "
               + ex.toString());
        }
    }

    /**
     * Uses reflection to invoke a message method on an instantiated
     * Commons Logging <tt>Log</tt> object.
     *
     * @param log         the <tt>Log</tt> object
     * @param methodName  the method name
     * @param message     the message
     */
    private void logMessage (Object   log,
                             String   methodName,
                             Object   message)
    {
        Method method = null;

        try
        {
            Class logClass = log.getClass();
            method = logClass.getMethod (methodName,
                                         new Class[] {Object.class});
        }

        catch (NoSuchMethodException ex)
        {
            throw new UnsupportedOperationException
                ("Commons Logging object \""
               + log.getClass().getName()
               + "\" does not contain an accessible method "
               + methodName
               + "(String)");
        }

        catch (SecurityException ex)
        {
            throw new UnsupportedOperationException
                ("Commons Logging class \""
               + log.getClass()
               + "\" does not contain an accessible method "
               + methodName
               + "(String)");
        }

        try
        {
            method.invoke (log, new Object[] {message});
        }

        catch (Exception ex)
        {
            throw new UnsupportedOperationException
                ("Can't invoke method "
               + log.getClass().getName()
               + "."
               + methodName
               + "(): "
               + ex.toString());
        }
    }

    /**
     * Uses reflection to invoke a message method on an instantiated
     * Commons Logging <tt>Log</tt> object.
     *
     * @param log         the <tt>Log</tt> object
     * @param methodName  the method name
     * @param message     the message
     * @param t           a Throwable to go with the message
     */
    private void logMessage (Object    log,
                             String    methodName,
                             Object    message,
                             Throwable t)
    {
        Method method = null;

        try
        {
            Class logClass = log.getClass();
            method = logClass.getMethod (methodName,
                                         new Class[]
                                         {
                                             Object.class,
                                             Throwable.class
                                         });
        }

        catch (NoSuchMethodException ex)
        {
            throw new UnsupportedOperationException
                ("Commons Logging object \""
               + log.getClass().getName()
               + "\" does not contain an accessible method "
               + methodName
               + "(String,Throwable)");
        }

        catch (SecurityException ex)
        {
            throw new UnsupportedOperationException
                ("Commons Logging class \""
               + log.getClass()
               + "\" does not contain an accessible method "
               + methodName
               + "(String,Throwable)");
        }

        try
        {
            method.invoke (log, new Object[] {message, t});
        }

        catch (Exception ex)
        {
            throw new UnsupportedOperationException
                ("Can't invoke method "
               + log.getClass().getName()
               + "."
               + methodName
               + "(): "
               + ex.toString());
        }
    }

    /**
     * Uses reflection to determine whether a specific log level is enabled.
     *
     * @param log         the <tt>Log</tt> object
     * @param methodName  the method name
     *
     * @return <tt>true</tt> or <tt>false</tt>
     */
    private boolean isEnabled (Object log, String methodName)
    {
        Method method = null;

        try
        {
            Class logClass = log.getClass();
            method = logClass.getMethod (methodName, null);
        }

        catch (NoSuchMethodException ex)
        {
            throw new UnsupportedOperationException
                ("Commons Logging object \""
               + log.getClass().getName()
               + "\" does not contain an accessible method "
               + methodName
               + "()");
        }

        catch (SecurityException ex)
        {
            throw new UnsupportedOperationException
                ("Commons Logging class \""
               + log.getClass()
               + "\" does not contain an accessible method "
               + methodName
               + "()");
        }

        try
        {
            Boolean result = (Boolean) method.invoke (log, null);
            return result.booleanValue();
        }

        catch (Exception ex)
        {
            throw new UnsupportedOperationException
                ("Can't invoke method "
               + log.getClass().getName()
               + "."
               + methodName
               + "(): "
               + ex.toString());
        }
    }
}
