/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config;

import org.clapper.util.misc.*;

/**
 * A <tt>ConfigurationException</tt> is thrown by the
 * {@link ConfigurationParser} class to signify errors in a configuration
 * file.
 *
 * @see NestedException
 *
 * @version <tt>$Revision$</tt>
 */
public class ConfigurationException extends NestedException
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public ConfigurationException()
    {
	super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public ConfigurationException (Throwable exception)
    {
	super (exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public ConfigurationException (String message)
    {
        super (message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public ConfigurationException (String message, Throwable exception)
    {
	super (message, exception);
    }
}
