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
 * @author Copyright &copy; 2004 Brian M. Clapper
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
            throw new BuildException ("Can't create build info file \""
                                    + file.getPath()
                                    + "\": "
                                    + ex.toString());
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
