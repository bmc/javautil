/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.ant;

import java.net.InetAddress;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * {@link <a href="http://ant.apache.org/">Ant</a>} task to retrieve the
 * name of the host on which the build is running, storing the name in a
 * property. Example:
 *
 * <blockquote><pre>
 * &lt;taskdef name="host"
 *          classname="org.clapper.util.ant.HostnameTask"
 *          classpath="${build}"/&gt;
 * &lt;host property="build.host"/&gt;
 * </pre></blockquote>
 *
 * The specified property is always set to something. If the task cannot
 * retrieve the current host name, it displays a message to standard error
 * and sets the property to "localhost".
 *
 * @version <tt>$Id$</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class HostnameTask extends Task
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private String host = null;
    private String propertyName = null;

    /*----------------------------------------------------------------------*\
                              Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor
     */
    public HostnameTask()
    {
        // Nothing to do
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/
   /**
     * Execute the Ant task, retrieving the host name and storing it in
     * the property.
     *
     * @throws BuildException on error
     */
    public void execute() throws BuildException
    {
        if (propertyName == null)
            throw new BuildException ("property attribute not set.");

        if (host == null)
        {
            try
            {
                InetAddress localhost = InetAddress.getLocalHost();
                host = localhost.getHostName();
            }

            catch (Exception ex)
            {
                host = "localhost";
            }
        }

        addProperty (propertyName, host);
    }

    /**
     * Called by Ant to set the "property" attribute.
     *
     * @param prop  the name of the property to be set to the host name
     */
    public void setProperty (String prop)
    {
        this.propertyName = prop;
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * add a name value pair to the project property set
     * @param n name of property
     * @param v value to set
     */
    private void addProperty (String n, String v)
    {
        getProject().setNewProperty(n, v);
    }
}
