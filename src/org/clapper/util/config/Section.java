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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the contents of a section.
 *
 * @version <tt>$Revision$</tt>
 */
class Section
{
    /*----------------------------------------------------------------------*\
                               Instance Data
    \*----------------------------------------------------------------------*/

    /**
     * Name of section
     */
    private String name;

    /**
     * Names of variables, in order encountered. Contains strings.
     */
    private List variableNames = new ArrayList();

    /**
     * List of Variable objects, indexed by variable name
     */
    private Map valueMap = new HashMap();

    /**
     * The section's unique ID. This ID increases monotonically from 1.
     */
    private int id = 0;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>Section</tt> object.
     *
     * @param name  the section name
     * @param id    the unique numeric ID
     */
    Section (String name, int id)
    {
        this.name = name;
        this.id   = id;
    }

    /*----------------------------------------------------------------------*\
                          Package-visible Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get this section's unique numeric ID.
     *
     * @return the ID
     */
    int getID()
    {
        return id;
    }

    /**
     * Get this section's name
     *
     * @return the name
     */
    String getName()
    {
        return name;
    }

    /**
     * Get the names of all variables defined in this section, in the order
     * they were encountered in the file.
     *
     * @return an unmodifiable <tt>Collection</tt> of <tt>String</tt> variable
     *         names
     */
    Collection getVariableNames()
    {
        return Collections.unmodifiableList (variableNames);
    }

    /**
     * Get a named variable from this section.
     *
     * @param varName  variable name to retrieve
     *
     * @return the <tt>Variable</tt> object, or null if not found
     *
     * @throws ConfigurationException on error
     */
    Variable getVariable (String varName)
        throws ConfigurationException
    {
        return (Variable) valueMap.get (varName);
    }

    /**
     * Add a variable to this section, replacing any existing instance
     * of the variable.
     *
     * @param varName the variable name
     * @param value   its (presumably unexpanded) value
     */
    Variable addVariable (String varName, String value)
    {
        Variable variable = new Variable (varName, value, this);
        valueMap.put (varName, variable);
        variableNames.add (varName);
        return variable;
    }

    /**
     * Add a variable to this section, replacing any existing instance
     * of the variable.
     *
     * @param varName      the variable name
     * @param value        its (presumably unexpanded) value
     * @param lineDefined  line number in the file where it was defined
     */
    Variable addVariable (String varName, String value, int lineDefined)
    {
        Variable variable = addVariable (varName, value);
        variable.setLineWhereDefined (lineDefined);
        return variable;
    }
}
