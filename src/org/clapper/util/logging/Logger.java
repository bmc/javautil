/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.logging;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Level;

/**
 * <p><tt>Logger</tt> wraps the
 * <a href="http://logging.apache.org/">Log4J</a>
 * API and provides a slightly simpler, but similar, interface. This
 * class supports most of the logging methods, but it doesn't actually
 * instantiate an underlying Log4J <tt>Logger</tt> object until
 * (or unless) a thread explicitly calls the {@link #enableLogging} method.
 * The first call to <tt>enableLogging()</tt> traverses the list of
 * instantiated <tt>org.clapper.util.Logger</tt> objects and creates
 * underlying <tt>java.util.logging.Logger</tt> objects for each one. Any
 * <tt>Logger</tt> objects created after <tt>enableLogging()</tt> is called
 * are automatically enabled.</p>
 *
 * <p>This approach prevents any interaction with the real logging layer,
 * unless logging is explicitly enabled (e.g., because a command-line
 * option has been specified). This approach was originally taken to avoid
 * annoying startup messages from the Log4J logging implementation, which
 * insists on writing warning messages to the console when it can't find a
 * configuration file.</p>
 *
 * <p>However, this class can be reimplemented in terms of other logging
 * layers (and, in fact, has been implemented solely in terms of the JDK
 * 1.4 <tt>java.util.logging</tt> library and Jakarta Commons Logging in
 * the past).</p> This object's main purpose now is to insulate
 * applications from the underlying logging technology, so that technology
 * can be changed, if necessary, without having an impact on applications
 * that use this class.</p>
 *
 * <p>This API wraps Log4J, rather than <tt>java.util.logging</tt> or
 * Jakarta Commons Logging, because the Log4J doesn't play odd games with
 * class loaders, as the other two APIs do. If you use your own class loader,
 * or you're running within a framework that substitutes its own class loader,
 * you can have problems with <tt>java.util.logging</tt> (which insists on
 * invoking the system class loader directly) and with Jakarta Commons Logging
 * (which misuses class loaders in its attempt to discover a suitable logging
 * API).</p>
 *
 * <p>The level mappings used by this class are identical to those used by
 * Log4J. (e.g.,, a "debug" message uses the same level as a Log4J "debug"
 * message.) Those mappings are:</p>
 *
 * <table border="1">
 *   <tr valign="top">
 *     <th><tt>org.clapper.util.logging.Logger</tt> method</th>
 *     <th>Corresponding Log4J <tt>Level</tt> value</th>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>debug()</tt></td>
 *     <td><tt>DEBUG</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>error()</tt></td>
 *     <td><tt>ERROR</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>fatal()</tt></td>
 *     <td><tt>FATAL</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>info()</tt></td>
 *     <td><tt>INFO</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>trace()</tt></td>
 *     <td><tt>TRACE</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>warn()</tt></td>
 *     <td><tt>WARNING</tt></td>
 *   </tr>
 * </table>
 *
 * @see #enableLogging
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class Logger
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * Log level constant for debug messages. Defined here for backward
     * compatibility.
     *
     * @see LogLevel#DEBUG
     */
    public static LogLevel LEVEL_DEBUG = LogLevel.DEBUG;

    /**
     * Log level constant for error messages. Defined here for backward
     * compatibility.
     *
     * @see LogLevel#ERROR
     */
    public static LogLevel LEVEL_ERROR = LogLevel.ERROR;

    /**
     * Log level constant for fatal-error messages. Defined here for
     * backward compatibility.
     *
     * @see LogLevel#FATAL
     */
    public static LogLevel LEVEL_FATAL = LogLevel.FATAL;

    /**
     * Log level constant for informational messages. Defined here for
     * backward compatibility.
     *
     * @see LogLevel#INFO
     */
    public static LogLevel LEVEL_INFO = LogLevel.INFO;

    /**
     * Log level constant for trace messages. Defined here for backward
     * compatibility.
     *
     * @see LogLevel#TRACE
     */
    public static LogLevel LEVEL_TRACE = LogLevel.TRACE;

    /**
     * Log level constant for warning messages. Defined here for backward
     * compatibility.
     *
     * @see LogLevel#WARNING
     */
    public static LogLevel LEVEL_WARNING = LogLevel.WARNING;

    /*----------------------------------------------------------------------*\
                           Private Instance Data
    \*----------------------------------------------------------------------*/

    /**
     * The real logging object. Not instantiated unless asked for.
     */
    private org.apache.log4j.Logger realLogger = null;

    /**
     * The class name to use when instantiating the underlying Log object.
     */
    private String className = null;

    /**
     * The list of existing <tt>Logger</tt> objects.
     */
    private static Collection<Logger> loggers = new ArrayList<Logger>();

    /**
     * Whether or not logging is enabled.
     */
    private static boolean enabled = false;

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
                for (Logger logger : loggers)
                    enableLogger (logger);

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
        if (realLogger != null)
            realLogger.debug (message);
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
        if (realLogger != null)
            realLogger.debug (message, ex);
    }

    /**
     * Log a message with error log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void error (Object message)
    {
        if (realLogger != null)
            realLogger.error (message);
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
        if (realLogger != null)
            realLogger.error (message, ex);
    }

    /**
     * Log a message with fatal log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void fatal (Object message)
    {
        if (realLogger != null)
            realLogger.fatal (message);
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
        if (realLogger != null)
            realLogger.fatal (message, ex);
    }

    /**
     * Log a message with info log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void info (Object message)
    {
        if (realLogger != null)
            realLogger.info (message);
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
        if (realLogger != null)
            realLogger.info (message, ex);
    }

    /**
     * Log a message at a specified log level.
     *
     * @param level   the log level
     * @param message the message
     */
    public void message (LogLevel level, Object message)
    {
        if (realLogger != null)
            realLogger.log (level.getLevel(), message);
        switch (level)
        {
            case DEBUG:
                debug (message);
                break;

            case ERROR:
                error (message);
                break;

            case FATAL:
                fatal (message);
                break;

            case INFO:
                info (message);
                break;

            case TRACE:
                trace (message);
                break;

            case WARNING:
                warn (message);
                break;

            default:
                assert (false);
        }
    }

    /**
     * Log a message at a specified log level.
     *
     * @param level   the log level
     * @param message the message
     * @param ex       The exception to log with the message
     */
    public void message (LogLevel level, Object message, Throwable ex)
    {
        switch (level)
        {
            case DEBUG:
                debug (message, ex);
                break;

            case ERROR:
                error (message, ex);
                break;

            case FATAL:
                fatal (message, ex);
                break;

            case INFO:
                info (message, ex);
                break;

            case TRACE:
                trace (message, ex);
                break;

            case WARNING:
                warn (message, ex);
                break;

            default:
                assert (false);
        }
    }

    /**
     * Log a message with trace log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void trace (Object message)
    {
        if (realLogger != null)
            realLogger.trace (message);
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
        if (realLogger != null)
            realLogger.trace (message, ex);
    }

    /**
     * Log a message with warn log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void warn (Object message)
    {
        if (realLogger != null)
            realLogger.warn (message);
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
        if (realLogger != null)
            realLogger.warn (message, ex);
    }

    /**
     * Determine whether debug logging is currently enabled.
     *
     * @return <tt>true</tt> if debug logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isDebugEnabled()
    {
        return (realLogger == null) ? false
                                    : realLogger.isDebugEnabled();
    }

    /**
     * Determine whether error logging is currently enabled.
     *
     * @return <tt>true</tt> if error logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isErrorEnabled()
    {
        return (realLogger == null)
            ? false
            : realLogger.isEnabledFor(LogLevel.ERROR.getLevel());
    }

    /**
     * Determine whether fatal logging is currently enabled.
     *
     * @return <tt>true</tt> if fatal logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isFatalEnabled()
    {
        return (realLogger == null)
            ? false
            : realLogger.isEnabledFor(LogLevel.FATAL.getLevel());
    }

    /**
     * Determine whether info logging is currently enabled.
     *
     * @return <tt>true</tt> if info logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isInfoEnabled()
    {
        return (realLogger == null)
            ? false
            : realLogger.isEnabledFor(LogLevel.INFO.getLevel());
    }

    /**
     * Determine whether trace logging is currently enabled.
     *
     * @return <tt>true</tt> if trace logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isTraceEnabled()
    {
        return (realLogger == null)
            ? false
            : realLogger.isEnabledFor(LogLevel.TRACE.getLevel());
    }

    /**
     * Determine whether warn logging is currently enabled.
     *
     * @return <tt>true</tt> if warn logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isWarningEnabled()
    {
        return (realLogger == null)
            ? false
            : realLogger.isEnabledFor(LogLevel.WARNING.getLevel());
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private static void enableLogger (Logger logger)
        throws UnsupportedOperationException
    {
        synchronized (logger)
        {
            if (logger.realLogger == null)
            {
                logger.realLogger =
                    org.apache.log4j.Logger.getLogger (logger.className);
            }
        }
    }
}
