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

package org.clapper.util.cmdline;

import org.clapper.util.misc.NestedException;

/**
 * Thrown by a command-line utility when an error occurs while running the
 * command.
 *
 * @version <tt>$Revision$</tt>
 */
public class CommandLineException extends NestedException
{
    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public CommandLineException()
    {
	super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public CommandLineException (Throwable exception)
    {
	super (exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public CommandLineException (String message)
    {
        super (message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public CommandLineException (String message, Throwable exception)
    {
	super (message, exception);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, and a default message (in case the resource bundle can't be
     * found). Using this constructor is equivalent to calling the
     * {@link #CommandLineException(String,String,Object[])} constructor,
     * with a null pointer for the <tt>Object[]</tt> parameter.
     * Calls to {@link NestedException#getMessage(Locale)} will attempt to
     * retrieve the top-most message (i.e., the message from this exception,
     * not from nested exceptions) by querying the named resource bundle.
     * Calls to {@link NestedException#printStackTrace(PrintWriter,Locale)}
     * will do the same, where applicable. The message is not retrieved
     * until one of those methods is called, because the desired locale is
     * passed into <tt>getMessage()</tt> and <tt>printStackTrace()</tt>,
     * not this constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     *
     * @see #CommandLineException(String,String,Object[])
     * @see NestedException#getLocalizedMessage
     */
    public CommandLineException (String bundleName,
                                 String messageKey,
                                 String defaultMsg)
    {
        super (bundleName, messageKey, defaultMsg);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, and a default message (in case the resource bundle can't be
     * found). Using this constructor is equivalent to calling the
     * {@link #CommandLineException(String,String,Object[])} constructor,
     * with a null pointer for the <tt>Object[]</tt> parameter.
     * Calls to {@link NestedException#getMessage(Locale)} will attempt to
     * retrieve the top-most message (i.e., the message from this exception,
     * not from nested exceptions) by querying the named resource bundle.
     * Calls to {@link NestedException#printStackTrace(PrintWriter,Locale)}
     * will do the same, where applicable. The message is not retrieved
     * until one of those methods is called, because the desired locale is
     * passed into <tt>getMessage()</tt> and <tt>printStackTrace()</tt>,
     * not this constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     * @param msgParams   parameters to the message, if any, or null
     *
     * @see #CommandLineException(String,String,Object[])
     * @see NestedException#getLocalizedMessage
     */
    public CommandLineException (String   bundleName,
                                 String   messageKey,
                                 String   defaultMsg,
                                 Object[] msgParams)
    {
        super (bundleName, messageKey, defaultMsg, msgParams);
    }
}
