/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config;

import java.util.*;

/**
 * A <tt>NoSuchSectionException</tt> is thrown by the
 * {@link Configuration} class to signify that a requested configuration
 * section does not exist.
 *
 * @see ConfigurationException
 *
 * @version <tt>$Revision$</tt>
 */
public class NoSuchSectionException extends ConfigurationException
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private String sectionName = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Constructs an exception.
     *
     * @param sectionName  the section name to which the exception pertains
     */
    public NoSuchSectionException (String sectionName)
    {
        super (NoSuchSectionException.class.getName()
             + ": section "
             + sectionName);

        this.sectionName = sectionName;
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
}
