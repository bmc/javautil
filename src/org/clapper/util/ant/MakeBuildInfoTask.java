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

import java.io.File;
import java.io.IOException;

import org.clapper.util.misc.BuildInfo;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * <p>{@link <a href="http://ant.apache.org/">Ant</a>} task to create a build
 * info properties file. A build info properties file contains a series of
 * properties containing information about the build time, build environment,
 * etc. The file is actually created by the static
 * {@link BuildInfo#makeBuildInfoBundle BuildInfo.makeBuildInfoBundle()}
 * method. This Ant task is simply a thin wrapper around that method.</p>
 *
 * <p>The Ant build file logic necessary to define and invoke this task
 * is:</p>
 *
 * <blockquote><pre>
 * &lt;taskdef name="make_build_info" 
 *          classname="org.clapper.util.ant.MakeBuildInfoTask"
 *          classpath="${build}"/&gt;
 * &lt;make_build_info file="${build}/org/example/BuildInfoBundle.properties
 *                  antversion="${ant.version}"
 *                  compiler="${build.compiler}"
 * /&gt;
 * </pre></blockquote>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class MakeBuildInfoTask extends Task
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private File    file          = null;
    private String  antVersion    = null;
    private String  buildCompiler = null;

    /*----------------------------------------------------------------------*\
                              Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor
     */
    public MakeBuildInfoTask()
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
        if (file == null)
            throw new BuildException ("file attribute not set.");

        try
        {
            BuildInfo.makeBuildInfoBundle (file, buildCompiler, antVersion);
        }

        catch (IOException ex)
        {
            throw new BuildException ("Can't create build info file \"" +
                                      file.getPath() + "\": " + ex.toString(),
                                      ex);
        }
    }

    /**
     * Called by Ant to set the "file" attribute.
     *
     * @param file  the file
     */
    public void setFile (File file)
    {
        this.file = file;
    }

    /**
     * Called by Ant to set the "antversion" attribute.
     *
     * @param s  the version string
     */
    public void setAntversion (String s)
    {
        this.antVersion = s;
    }

    /**
     * Called by Ant to set the "compiler" attribute.
     *
     * @param s the compiler string
     */
    public void setCompiler (String s)
    {
        this.buildCompiler = s;
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/
}
