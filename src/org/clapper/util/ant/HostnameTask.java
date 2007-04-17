/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2007 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M.a Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
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
