/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config;

import java.util.*;

/**
 * A <tt>NoSuchVariableException</tt> is thrown by the
 * {@link ConfigurationParser} class to signify that a requested
 * configuration variable does not exist.
 *
 * @see NestedException
 *
 * @version <tt>$Revision$</tt>
 */
public class NoSuchVariableException extends NoSuchElementException
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private String variableName = null;
    private String sectionName  = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Constructs an exception.
     *
     * @param sectionName   the section that doesn't have the variable
     * @param variableName  the variable name to which the exception pertains
     */
    public NoSuchVariableException (String sectionName, String variableName)
    {
        super();

        this.sectionName  = sectionName;
        this.variableName = variableName;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Gets the section name associated with this exception.
     *
     * @return the section name
     */
    public String getSectionName()
    {
        return sectionName;
    }

    /**
     * Gets the variable name associated with this exception.
     *
     * @return the variable name
     */
    public String getVariableName()
    {
        return variableName;
    }

    /**
     * Gets the message associated with this exception.
     *
     * @return the message
     */
    public String getMessage()
    {
        return this.getClass().getName() + ": section " + sectionName +
               ", variable " + variableName;
    }

    /**
     * Gets a string representation of this exception.
     *
     * @return the string representation
     */
    public String toString()
    {
        return getMessage();
    }
}
