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
      without prior written permission of Brian M. Clapper.

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

package org.clapper.util.config;

import org.clapper.util.text.TextUtil;

import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;

import java.io.File;
import java.io.IOException;

/**
 * Implements the special "program" section.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see Section
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
class ProgramSection extends Section
{
    /*----------------------------------------------------------------------*\
                         Package-visible Constants
    \*----------------------------------------------------------------------*/

    static final String PROGRAM_NOW_VAR     = "now";
    static final String PROGRAM_CWD_VAR     = "cwd";
    static final String PROGRAM_CWD_URL_VAR = "cwd.url";

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>ProgramSection</tt> object, loading its values from
     * the system properties list.
     *
     * @param name  the section name
     * @param id    the unique numeric ID
     *
     * @throws ConfigurationException  failed to initialize object
     */
    ProgramSection (String name, int id)
        throws ConfigurationException
    {
        super (name, id);
        loadProgramSectionStaticValues();
    }

    /*----------------------------------------------------------------------*\
                          Package-visible Methods
    \*----------------------------------------------------------------------*/

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
        Variable result;

        // Handle the dynamic ones explicitly.

        if (varName.startsWith (PROGRAM_NOW_VAR))
        {
            result = new Variable (varName, substituteDatetime (varName),
                                   this, 0);
        }
        else
        {
            result = super.getVariable (varName);
        }

        return result;
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private void loadProgramSectionStaticValues() throws ConfigurationException
    {
        try
        {
            File dir = new File (".");
            String value = dir.getCanonicalPath();
            addVariable (PROGRAM_CWD_VAR, value);

            value = dir.getCanonicalFile().toURI().toURL().toString();
            if (value.charAt (value.length() - 1) == '/')
                value = value.substring (0, value.length() - 1);
            addVariable (PROGRAM_CWD_URL_VAR, value);
        }

        catch (IOException ex)
        {
            throw new ConfigurationException (ex);
        }
    }

    /**
     * Handle substitution of a date variable in the [program] section.
     *
     * @param varName  the variable name
     *
     * @return the formatted date/time value
     *
     * @throws ConfigurationException bad date format, or something
     */
    private String substituteDatetime (String varName)
         throws ConfigurationException
    {
        String value = "";
        Date   now   = new Date();

        if (varName.equals (PROGRAM_NOW_VAR))
        {
            value = now.toString();
        }

        else
        {
            char delim = varName.charAt (PROGRAM_NOW_VAR.length());
            String[] tokens = TextUtil.split (varName, delim);
            if ((tokens.length != 2) && (tokens.length != 4))
            {
                throw new ConfigurationException
                    (Package.BUNDLE_NAME, "ProgramSection.badNowFieldCount",
                     "Section \"{0}\", variable reference \"{1}\": " +
                     "Incorrect number of fields in extended version of " +
                     "\"{2}\" variable. Found {3} fields, expected either " +
                     "{4} or {5}.",
                     new Object[]
                     {
                         this.getName(),
                         varName,
                         PROGRAM_NOW_VAR,
                         String.valueOf (tokens.length),
                         "2",
                         "4"
                     });
            }

            Locale locale = null;

            if (tokens.length == 2)
                locale = Locale.getDefault();
            else
                locale = new Locale (tokens[2], tokens[3]);

            try
            {
                SimpleDateFormat fmt = new SimpleDateFormat (tokens[1],
                                                             locale);
                value = fmt.format (now);
            }

            catch (IllegalArgumentException ex)
            {
                throw new ConfigurationException (ex.toString());
            }
        }

        return value;
    }
}
