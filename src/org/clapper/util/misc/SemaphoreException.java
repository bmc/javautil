/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc;

/**
 * Thrown by classes that implement the {@link Semaphore} interface, to
 * indicate an error.
 *
 * @version <tt>$Revision$
 */
public class SemaphoreException extends NestedException
{
    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public SemaphoreException()
    {
	super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public SemaphoreException (Throwable exception)
    {
	super (exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public SemaphoreException (String message)
    {
        super (message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public SemaphoreException (String message, Throwable exception)
    {
	super (message, exception);
    }
}
