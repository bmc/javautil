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

package org.clapper.util.config;

import java.util.Locale;

import org.clapper.util.misc.NestedException;

/**
 * A <tt>ConfigurationException</tt> is thrown by the
 * {@link Configuration} class to signify errors in a configuration file.
 *
 * @see NestedException
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class ConfigurationException extends NestedException
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public ConfigurationException()
    {
	super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public ConfigurationException (Throwable exception)
    {
	super (exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public ConfigurationException (String message)
    {
        super (message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public ConfigurationException (String message, Throwable exception)
    {
	super (message, exception);
    }

    /**
     * Constructs an exception containing a configuration file section name
     * and a message.
     *
     * @param sectionName the section name
     * @param message     the message to associate with this exception
     */
    public ConfigurationException (String sectionName, String message)
    {
	super ("Section [" + sectionName + "]: " + message);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, and a default message (in case the resource bundle can't be
     * found). Using this constructor is equivalent to calling the
     * {@link #ConfigurationException(String,String,Object[])} constructor,
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
     * @see #ConfigurationException(String,String,Object[])
     * @see NestedException#getMessage(Locale)
     */
    public ConfigurationException (String bundleName,
                                   String messageKey,
                                   String defaultMsg)
    {
        super (bundleName, messageKey, defaultMsg);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, and a default message (in case the resource bundle can't be
     * found). Using this constructor is equivalent to calling the
     * {@link #ConfigurationException(String,String,Object[])} constructor,
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
     * @see #ConfigurationException(String,String,Object[])
     * @see NestedException#getMessage(Locale)
     */
    public ConfigurationException (String   bundleName,
                                   String   messageKey,
                                   String   defaultMsg,
                                   Object[] msgParams)
    {
        super (bundleName, messageKey, defaultMsg, msgParams);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, a default message (in case the resource bundle can't be found),
     * and another exception. Using this constructor is equivalent to
     * calling the {@link #ConfigurationException(String,String,Object[])}
     * constructor, with a null pointer for the <tt>Object[]</tt>
     * parameter. Calls to {@link #getMessage(Locale)} will attempt to
     * retrieve the top-most message (i.e., the message from this
     * exception, not from nested exceptions) by querying the named
     * resource bundle. Calls to
     * {@link #printStackTrace(PrintWriter,Locale)} will do the same, where
     * applicable. The message is not retrieved until one of those methods
     * is called, because the desired locale is passed into
     * <tt>getMessage()</tt> and <tt>printStackTrace()</tt>, not this
     * constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     * @param exception   the exception to nest
     *
     * @see #ConfigurationException(String,String,Object[])
     * @see NestedException#getMessage(Locale)
     */
    public ConfigurationException (String    bundleName,
                                   String    messageKey,
                                   String    defaultMsg,
                                   Throwable ex)
    {
        this (bundleName, messageKey, defaultMsg, null, ex);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, a default message format (in case the resource bundle can't be
     * found), arguments to be incorporated in the message via
     * <tt>java.text.MessageFormat</tt>, and another exception.
     * Calls to {@link #getMessage(Locale)} will attempt to retrieve the
     * top-most message (i.e., the message from this exception, not from
     * nested exceptions) by querying the named resource bundle. Calls to
     * {@link #printStackTrace(PrintWriter,Locale)} will do the same, where
     * applicable. The message is not retrieved until one of those methods
     * is called, because the desired locale is passed into
     * <tt>getMessage()</tt> and <tt>printStackTrace()</tt>, not this
     * constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     * @param msgParams   parameters to the message, if any, or null
     * @param ex          exception to be nested
     *
     * @see #ConfigurationException(String,String,Object[])
     * @see NestedException#getMessage(Locale)
     */
    public ConfigurationException (String    bundleName,
                                   String    messageKey,
                                   String    defaultMsg,
                                   Object[]  msgParams,
                                   Throwable ex)
    {
        super (bundleName, messageKey, defaultMsg, msgParams, ex);
    }
}
