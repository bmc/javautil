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

package org.clapper.util.mail;

import java.io.PrintStream;

/**
 * <p><tt>EmailTransport</tt> defines the interface for classes that can
 * send <tt>EmailMessage</tt> objects.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @see EmailMessage
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public interface EmailTransport
{
    /*----------------------------------------------------------------------*\
                             Required Methods
    \*----------------------------------------------------------------------*/

    /**
     * Send an <tt>EmailMessage</tt>. This <tt>EmailTransport</tt> object
     * is assumed to have been configured with the appropriate parameters.
     *
     * @param message  the message to send
     *
     * @throws EmailException  failed to send message
     */
    public void send (EmailMessage message)
        throws EmailException;

    /**
     * Enable or disable the underlying implementation's debug flag, if
     * there is one. Debug messages are to be written to the supplied
     * <tt>PrintStream</tt>.
     *
     * @param debug    <tt>true</tt> to enable debug, <tt>false</tt> to
     *                 disable it
     * @param out      where to dump debug messages, or null for standard
     *                 output. Ignored unless <tt>debug</tt> is <tt>true</tt>.
     */
    public void setDebug (boolean debug, PrintStream out);
}
