/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.curn;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p><tt>Logger</tt> wraps the Apache Jakarta Commons Logging <tt>Log</tt>
 * class. This class supports most of the logging methods, but it doesn't
 * actually instantiate any Jakarta Commons Logging <tt>Log</tt> objects
 * unless logging is explicitly enabled via a call to the
 * {@link #enableAllLoggers enableAllLoggers()} method. The first call to
 * <tt>enableAllLoggers()</tt> traverses the list of instantiated
 * <tt>Logger</tt> objects and creates underlying Jakarta Commons Logging
 * <tt>Log</tt> objects for each <tt>Logger</tt>. Any <tt>Logger</tt> objects
 * created after <tt>enableAllLoggers()</tt> is called are automatically
 * enabled.</p>
 *
 * <p>This approach prevents any interaction with the real logging layer,
 * unless logging is explicitly enabled (e.g., becaus a command-line option
 * has been specified). Among other things, this avoids annoying startup
 * messages from Log4J, which insists on writing warning messages to the
 * console when it can't find a configuration file.</p>
 *
 * @see curn
 * @see org.clapper.curn.parser.RSSChannel
 *
 * @version <tt>$Revision$</tt>
 */
public class Logger
{
    /*----------------------------------------------------------------------*\
                           Private Instance Data
    \*----------------------------------------------------------------------*/

    /**
     * The real commons logging object. Not instantiated unless asked for.
     */
    private Log log = null;

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
     * Enables logging for all instantiated <tt>Logger</tt> objects. Thus,
     */
    public static void enableAllLoggers()
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
            log.debug (message);
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
            log.debug (message, ex);
    }

    /**
     * Log a message with error log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void error (Object message)
    {
        if (log != null)
            log.error (message);
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
            log.error (message, ex);
    }

    /**
     * Log a message with fatal log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void fatal (Object message)
    {
        if (log != null)
            log.fatal (message);
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
            log.fatal (message, ex);
    }

    /**
     * Log a message with info log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void info (Object message)
    {
        if (log != null)
            log.info (message);
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
            log.info (message, ex);
    }

    /**
     * Log a message with trace log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void trace (Object message)
    {
        if (log != null)
            log.trace (message);
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
            log.trace (message, ex);
    }

    /**
     * Log a message with warn log level.
     *
     * @param message  The message to convert to a string and log
     */
    public void warn (Object message)
    {
        if (log != null)
            log.warn (message);
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
            log.warn (message, ex);
    }

    /**
     * Determine whether debug logging is currently enabled.
     *
     * @return <tt>true</tt> if debug logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isDebugEnabled()
    {
        return (log == null) ? false : log.isDebugEnabled();
    }

    /**
     * Determine whether error logging is currently enabled.
     *
     * @return <tt>true</tt> if error logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isErrorEnabled()
    {
        return (log == null) ? false : log.isErrorEnabled();
    }

    /**
     * Determine whether fatal logging is currently enabled.
     *
     * @return <tt>true</tt> if fatal logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isFatalEnabled()
    {
        return (log == null) ? false : log.isFatalEnabled();
    }

    /**
     * Determine whether info logging is currently enabled.
     *
     * @return <tt>true</tt> if info logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isInfoEnabled()
    {
        return (log == null) ? false : log.isInfoEnabled();
    }

    /**
     * Determine whether trace logging is currently enabled.
     *
     * @return <tt>true</tt> if trace logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isTraceEnabled()
    {
        return (log == null) ? false : log.isTraceEnabled();
    }

    /**
     * Determine whether warn logging is currently enabled.
     *
     * @return <tt>true</tt> if warn logging is enabled,
     *         <tt>false</tt> otherwise
     */
    public boolean isWarnEnabled()
    {
        return (log == null) ? false : log.isWarnEnabled();
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private static void enableLogger (Logger logger)
    {
        logger.log = LogFactory.getLog (logger.className);
    }
}
