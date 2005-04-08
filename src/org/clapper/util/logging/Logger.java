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

package org.clapper.util.logging;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * <p><tt>Logger</tt> wraps the <tt>java.util.logging</tt> API introduced
 * in JDK 1.4, and provides an intermediary object with an interface that's
 * similar to the Apache Jakarta Commons Logging <tt>Log</tt> class. This
 * class supports most of the logging methods, but it doesn't actually
 * instantiate an underlying <tt>java.util.logging.Logger</tt> object until
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
 * annoying startup messages from Log4J, which insists on writing warning
 * messages to the console when it can't find a configuration file.
 * However, this layer has since been reimplemented solely in terms of the
 * <tt>java.util.logging</tt> API, so that rationale is no longer
 * pertinent. This object's main purpose now is to insulate applications
 * from the underlying logging technology, so that technology can be
 * changed, if necessary, without having an impact on applications that use
 * this class.</p>
 *
 * <p>The level mappings used by this class are identical to those used
 * by Commons Logging. (e.g.,, a "debug" message uses the same level as
 * a Commons Logging "debug" message.) Those mappings are:</p>
 *
 * <table border="1">
 *   <tr valign="top">
 *     <th><tt>org.clapper.util.logging.Logger</tt> method</th>
 *     <th>Corresponding <tt>java.util.logging.Level</tt> value</th>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>debug()</tt></td>
 *     <td><tt>FINE</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>error()</tt></td>
 *     <td><tt>SEVERE</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>fatal()</tt></td>
 *     <td><tt>SEVERE</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>info()</tt></td>
 *     <td><tt>INFO</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>trace()</tt></td>
 *     <td><tt>FINEST</tt></td>
 *   </tr>
 *
 *   <tr valign="top">
 *     <td><tt>warn()</tt></td>
 *     <td><tt>WARNING</tt></td>
 *   </tr>
 * </table>
 *
 * <p>This approach begs the obvious question, "Why not just use Jakarta
 * Commons Logging?" Good point. After all, Commons Logging exists to
 * insulate the application from the underlying logging technology, and it
 * already supports multiple logging implementations (including the
 * <tt>java.util.logging</tt> API). There are several reasons:</p>
 *
 * <ul>
 *   <li> There are reported problems with the Commons Logging approach of
 *        using the class loader to "find" the appropriate logging
 *        infrastructure.
 *   <li> Using this layer, instead of Commons Logging, eliminates another
 *        third-party jar dependency from <tt>org.clapper</tt> applications.
 *   <li> This layer is quite a bit thinner than Commons Logging.
 * </ul>

 * <p>If you prefer Commons Logging, then, by all means, use it for your
 * applications.</p>
 *
 * @see #enableLogging
 * @see <a href="http://jakarta.apache.org/commons/logging/index.html">Jakarta Commons Logging API</a>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class Logger
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * Level parameter to the {@link #message} method.
     */
    public static final LogLevel LEVEL_DEBUG = new LogLevel (Level.FINE);

    /**
     * Level parameter to the {@link #message} method.
     */
    public static final LogLevel LEVEL_ERROR = new LogLevel (Level.SEVERE);

    /**
     * Level parameter to the {@link #message} method.
     */
    public static final LogLevel LEVEL_FATAL = new LogLevel (Level.SEVERE);

    /**
     * Level parameter to the {@link #message} method.
     */
    public static final LogLevel LEVEL_INFO = new LogLevel (Level.INFO);

    /**
     * Level parameter to the {@link #message} method.
     */
    public static final LogLevel LEVEL_TRACE = new LogLevel (Level.FINEST);

    /**
     * Level parameter to the {@link #message} method.
     */
    public static final LogLevel LEVEL_WARNING = new LogLevel (Level.WARNING);

    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    /**
     * Convenience of reference
     */
    private static final Level DEBUG   = LEVEL_DEBUG.getLevel();
    private static final Level ERROR   = LEVEL_ERROR.getLevel();
    private static final Level FATAL   = LEVEL_FATAL.getLevel();
    private static final Level INFO    = LEVEL_INFO.getLevel();
    private static final Level TRACE   = LEVEL_TRACE.getLevel();
    private static final Level WARNING = LEVEL_WARNING.getLevel();

    /*----------------------------------------------------------------------*\
                           Private Instance Data
    \*----------------------------------------------------------------------*/

    /**
     * The real logging object. Not instantiated unless asked for.
     */
    private java.util.logging.Logger realLogger = null;

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
            realLogger.log (DEBUG, message.toString());
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
            realLogger.log (DEBUG, message.toString(), ex);
    }

    /**
     * Log a message with error log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void error (Object message)
    {
        if (realLogger != null)
            realLogger.log (ERROR, message.toString());
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
            realLogger.log (ERROR, message.toString(), ex);
    }

    /**
     * Log a message with fatal log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void fatal (Object message)
    {
        if (realLogger != null)
            realLogger.log (FATAL, message.toString());
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
            realLogger.log (FATAL, message.toString(), ex);
    }

    /**
     * Log a message with info log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void info (Object message)
    {
        if (realLogger != null)
            realLogger.log (INFO, message.toString());
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
            realLogger.log (INFO, message.toString(), ex);
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
            realLogger.log (level.getLevel(), message.toString());
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
        if (realLogger != null)
            realLogger.log (level.getLevel(), message.toString(), ex);
    }

    /**
     * Log a message with trace log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void trace (Object message)
    {
        if (realLogger != null)
            realLogger.log (TRACE, message.toString());
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
            realLogger.log (TRACE, message.toString(), ex);
    }

    /**
     * Log a message with warn log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void warn (Object message)
    {
        if (realLogger != null)
            realLogger.log (WARNING, message.toString());
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
            realLogger.log (WARNING, message.toString(), ex);
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
                                    : realLogger.isLoggable (DEBUG);
    }

    /**
     * Determine whether error logging is currently enabled.
     *
     * @return <tt>true</tt> if error logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isErrorEnabled()
    {
        return (realLogger == null) ? false
                                    : realLogger.isLoggable (ERROR);
    }

    /**
     * Determine whether fatal logging is currently enabled.
     *
     * @return <tt>true</tt> if fatal logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isFatalEnabled()
    {
        return (realLogger == null) ? false
                                    : realLogger.isLoggable (FATAL);
    }

    /**
     * Determine whether info logging is currently enabled.
     *
     * @return <tt>true</tt> if info logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isInfoEnabled()
    {
        return (realLogger == null) ? false
                                    : realLogger.isLoggable (INFO);
    }

    /**
     * Determine whether trace logging is currently enabled.
     *
     * @return <tt>true</tt> if trace logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isTraceEnabled()
    {
        return (realLogger == null) ? false
                                    : realLogger.isLoggable (TRACE);
    }

    /**
     * Determine whether warn logging is currently enabled.
     *
     * @return <tt>true</tt> if warn logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isWarningEnabled()
    {
        return (realLogger == null) ? false
                                    : realLogger.isLoggable (WARNING);
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
                logger.realLogger = java.util.logging.Logger.getLogger
                                                            (logger.className);
            }
        }
    }
}
