/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.text;

import org.clapper.util.misc.*;

/**
 * A <tt>VariableSubstitutionException</tt> is thrown by the
 * {@link VariableSubstituter} subclasses and
 * {@link VariableDereferencer} subclasses to indicate a variable
 * substitution problem.
 *
 * @see NestedException
 *
 * @version <tt>$Revision$</tt>
 */
public class VariableSubstitutionException extends NestedException
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public VariableSubstitutionException()
    {
	super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public VariableSubstitutionException (Throwable exception)
    {
	super (exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public VariableSubstitutionException (String message)
    {
        super (message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public VariableSubstitutionException (String message, Throwable exception)
    {
	super (message, exception);
    }
}
