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

package org.clapper.util.ant;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.SummaryJUnitResultFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;

/**
 * JUnit output formatter that summarizes the results slightly differently
 * than the default summary formatter.
 *
 * @version <tt>$Revision$</tt>
 */
public class JUnitSummaryFormatter
    extends SummaryJUnitResultFormatter
{
    /*----------------------------------------------------------------------*\
                               Private Constants
    \*----------------------------------------------------------------------*/

    private OutputStream out;
    private PrintWriter  pOut;
    private NumberFormat nf = NumberFormat.getInstance();

    /*----------------------------------------------------------------------*\
                             Private Instance Data
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                 Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Creates a new instance of SummaryJUnitFormatter
     */
    public JUnitSummaryFormatter()
    {
    }

    /*----------------------------------------------------------------------*\
                                Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Set the output object.
     *
     * @param out  the output object
     */
    public void setOutput(OutputStream out)
    {
        this.out = out;
        this.pOut = new PrintWriter(out);
    }

    /**
     * Extends the base class's method to display the test suite name
     * before each summary result.
     *
     * @param suite  the test suite
     */
    public void endTestSuite(JUnitTest suite)
    {
        pOut.println(suite.getName());
        pOut.print(" Tests run: ");
        pOut.print(suite.runCount());
        pOut.print(", Failures: ");
        pOut.print(suite.failureCount());
        pOut.print(", Errors: ");
        pOut.print(suite.errorCount());
        pOut.print(", Time elapsed: ");
        pOut.print(nf.format(suite.getRunTime() / 1000.0));
        pOut.println(" sec");
        pOut.flush();

        if ((out != System.out) && (out != System.err))
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }
    }

    /*----------------------------------------------------------------------*\
                               Protected Methods
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Private Methods
    \*----------------------------------------------------------------------*/

}
