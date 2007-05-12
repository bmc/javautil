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

package org.clapper.util.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * <tt>XMLWriter</tt> is a filtering <tt>Writer</tt> class, designed
 * to be used to write XML output. Basically, it performs some simple-minded
 * indentation, to make the written XML a little more readable.
 *
 * @see java.io.Writer
 *
 * @version <tt>$Revision$</tt>
 */
public class XMLWriter extends Writer
{
    /*----------------------------------------------------------------------*\
                           Private Instance Data
    \*----------------------------------------------------------------------*/

    /**
     * The actual writer, wrapped in a PrintWriter
     */
    private PrintWriter out;

    /**
     * The previously written character, for keeping track of state
     */
    private char prev = '\0';

    /**
     * The indentation level
     */
    private int indentation = 0;

    /**
     * Whether the last element written was an XML processing instruction
     * or not. We don't indent after writing in XML PI.
     */
    private boolean lastWasPI = true;

    /*----------------------------------------------------------------------*\
                            Public Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>XMLWriter</tt> that will write its output to the
     * specified <tt>Writer</tt> object.
     *
     * @param out  where the output should really go
     */
    public XMLWriter (Writer out)
    {
        this.out = new PrintWriter (out);
    }
    /**
     * Construct a new <tt>XMLWriter</tt> that will write its output to the
     * specified <tt>PrintWriter</tt> object.
     *
     * @param out  where the output should really go
     */
    public XMLWriter (PrintWriter out)
    {
        this.out = out;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Close this <tt>Writer</tt>
     *
     * @throws IOException I/O error
     */
    public void close()
        throws IOException
    {
        out.close();
    }

    /**
     * Flush this <tt>Writer</tt>
     *
     * @throws IOException I/O error
     */
    public void flush()
        throws IOException
    {
        out.flush();
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param ch   array of characters
     * @param off  offset from which to start writing characters
     * @param len  number of characters to write
     *
     * @throws IOException  I/O error
     */
    public void write (char[] ch, int off, int len)
        throws IOException
    {
        while (off < len)
        {
            char c = ch[off++];

            switch (c)
            {
                case '<':
                    // Don't write here. It's buffered until the
                    // "default" case. How we indent depends on the
                    // character that follows the '<'.
                    break;

                case '>':
                    if (prev == '/')
                        indentation--;

                    lastWasPI = (prev == '?');
                    out.write (c);
                    out.write ('\n');
                    break;

                default:
                    if (prev == '<')
                    {
                        if (c == '/') // Closing element. Un-indent.
                            indentation--;
                        else if (! lastWasPI)
                            indentation++;

                        indent (indentation);
                        out.write (prev);
                    }

                    out.write (c);
                    break;
            }

            prev = c;
        }
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private void indent (int indentation)
        throws IOException
    {
        while (indentation-- > 0)
            out.write ("    ");
    }
}
