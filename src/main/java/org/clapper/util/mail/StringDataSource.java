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

package org.clapper.util.mail;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.IOException;

/**
 * <tt>StringDataSource</tt> implements the Java Activation Framework's
 * <tt>DataSource</tt> interface to enable using a string as a data source.
 * It is used primarily within the <tt>EmailMessage</tt> class to handle
 * MIME attachments stored within a <tt>String</tt> or a
 * <tt>StringBuffer</tt>.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class StringDataSource implements DataSource
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * The content type to use.
     */
    private String contentType  = null;

    /**
     * The name to associate with the data source.
     */
    private String name = null;

    /**
     * The string to use as the data source.
     */
    private String buf = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>StringDataSource</tt> object.
     *
     * @param s         The string from which to read
     * @param mimeType  The MIME type (or content type) to associate with
     *                  the contents of the string. If this parameter is
     *                  null, it defaults to "application/octet-stream".
     * @param name      The name to associate with the content. For an
     *                  email attachment, this parameter is typically used
     *                  to name the attachment. If this parameter is
     *                  <tt>null</tt>, a name will be generated.
     */
    public StringDataSource (String s, String mimeType, String name)
    {
        this.buf         = s;
        this.contentType = mimeType;
        this.name        = name;

        if (this.name == null)
            this.name = AttachmentUtils.generateAttachmentName();

        if (contentType == null)
            contentType = "application/octet-stream";
    }

    /*----------------------------------------------------------------------*\
                             Required Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get a new <tt>InputStream</tt> object that will read from this
     * object's string buffer.
     *
     * @return The <tt>InputStream</tt>.
     *
     * @exception java.io.IOException
     *            On error.
     */
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream (buf.getBytes());
    }

    /**
     * According to the documentation for the <tt>DataSource</tt>
     * interface, this method returns an OutputStream where the data can be
     * written and throws the appropriate exception if it can not do so.
     * Since there's no output destination associated with a string, this
     * implementation of <tt>getOutputStream()</tt> automatically throws an
     * exception.
     *
     * @return Nothing.
     *
     * @exception java.io.IOException
     *            Always.
     */
    public OutputStream getOutputStream() throws IOException
    {
        throw new IOException ("Can't have an OutputStream with an " +
                               "StringDataSource object.");
    }

    /**
     * Get the content type (i.e., the MIME type) associated with the
     * underlying string's content.
     *
     * @return The content type.
     */
    public String getContentType()
    {
        return contentType;
    }

    /**
     * Get the name associated with the underlying string's content.
     *
     * @return The name.
     */
    public String getName()
    {
        return name;
    }
}
