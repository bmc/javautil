/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Implements the special "system" section
 *
 * @see Section
 */
class SystemSection extends Section
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>SystemSection</tt> object, loading its values from
     * the system properties list.
     *
     * @param name  the section name
     * @param id    the ID
     */
    SystemSection (String name, int id)
    {
        super (name, id);

        Properties systemProperties = System.getProperties();

        for (Enumeration e = systemProperties.propertyNames();
             e.hasMoreElements(); )
        {
            String varName  = (String) e.nextElement();
            String varValue = systemProperties.getProperty (varName);
            addVariable (varName, varValue);
        }
    }
}
