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
 * @author Copyright &copy; 2004 Brian M. Clapper
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
        throw new IOException ( "Can't have an OutputStream with an "
                              + "StringDataSource object.");
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
