/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config;

import java.util.*;

/**
 * A <tt>NoSuchVariableException</tt> is thrown by the
 * {@link Configuration} class to signify that a requested configuration
 * variable does not exist.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see ConfigurationException
 */
public class NoSuchVariableException extends ConfigurationException
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
        super (NoSuchVariableException.class.getName()
             + ": section "
             + sectionName
             + ", variable "
             + variableName);

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
}
