/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.cmdline;

/**
 * Thrown by a command-line utility when an error occurs while running the
 * command.
 *
 * @version <tt>$Revision$</tt>
 */
public class CommandLineUsageException extends CommandLineException
{
    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public CommandLineUsageException()
    {
	super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public CommandLineUsageException (Throwable exception)
    {
	super (exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public CommandLineUsageException (String message)
    {
        super (message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public CommandLineUsageException (String message, Throwable exception)
    {
	super (message, exception);
    }
}
