/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms are permitted provided
  that: (1) source distributions retain this entire copyright notice and
  comment; and (2) modifications made to the software are prominently
  mentioned, and a copy of the original software (or a pointer to its
  location) are included. The name of the author may not be used to endorse
  or promote products derived from this software without specific prior
  written permission.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
  WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.

  Effectively, this means you can do what you want with the software except
  remove this notice or take advantage of the author's name. If you modify
  the software and redistribute your modified version, you must indicate that
  your version is a modification of the original, and you must provide either
  a pointer to or a copy of the original.
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
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
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
        super (Package.BUNDLE_NAME,
               "noSuchVariable",
               "Variable \"{0}\" does not exist in configuration section "
             + "\"{1}\"",
               new Object[] {sectionName, variableName});

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
