/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.cmdline;

import org.clapper.util.misc.NestedException;

/**
 * Thrown by a command-line utility when an error occurs while running the
 * command.
 *
 * @version <tt>$Revision$</tt>
 */
public class CommandLineException extends NestedException
{
    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public CommandLineException()
    {
	super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public CommandLineException (Throwable exception)
    {
	super (exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public CommandLineException (String message)
    {
        super (message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public CommandLineException (String message, Throwable exception)
    {
	super (message, exception);
    }
}
