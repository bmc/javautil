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

package org.clapper.util.text;

import org.clapper.util.misc.*;

/**
 * A <tt>VariableSubstitutionException</tt> is thrown by the
 * {@link VariableSubstituter} subclasses and
 * {@link VariableDereferencer} subclasses to indicate a variable
 * substitution problem.
 *
 * @see NestedException
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class VariableSubstitutionException extends NestedException
{
    /*----------------------------------------------------------------------*\
                         Private Static Variables
    \*----------------------------------------------------------------------*/

    /**
     * See JDK 1.5 version of java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public VariableSubstitutionException()
    {
        super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public VariableSubstitutionException (Throwable exception)
    {
        super (exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public VariableSubstitutionException (String message)
    {
        super (message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public VariableSubstitutionException (String message, Throwable exception)
    {
        super (message, exception);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, and a default message (in case the resource bundle can't be
     * found). Using this constructor is equivalent to calling the
     * {@link #VariableSubstitutionException(String,String,String,Object[])}
     * constructor, with a null pointer for the <tt>Object[]</tt> parameter.
     * Calls to {@link #getMessage(Locale)} will attempt to retrieve
     * the top-most message (i.e., the message from this exception, not
     * from nested exceptions) by querying the named resource bundle.
     * Calls to {@link #printStackTrace(PrintWriter,Locale)} will do the same,
     * where applicable. The message is not retrieved until one of those
     * methods is called, because the desired locale is passed into
     * <tt>getMessage()</tt> and <tt>printStackTrace()</tt>, not this
     * constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     *
     * @see #VariableSubstitutionException(String,String,String,Object[])
     * @see NestedException#getMessage(Locale)
     */
    public VariableSubstitutionException (String bundleName,
                                          String messageKey,
                                          String defaultMsg)
    {
        this (bundleName, messageKey, defaultMsg, null, null);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, a default message format (in case the resource bundle can't be
     * found), and arguments to be incorporated in the message via
     * <tt>java.text.MessageFormat</tt>. Calls to {@link #getMessage(Locale)}
     * will attempt to retrieve the top-most message (i.e., the message from
     * this exception, not from nested exceptions) by querying the named
     * resource bundle. Calls to {@link #printStackTrace(PrintWriter,Locale)}
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
     * @see #VariableSubstitutionException(String,String,String,Object[])
     * @see NestedException#getMessage(Locale)
     */
    public VariableSubstitutionException (String   bundleName,
                                          String   messageKey,
                                          String   defaultMsg,
                                          Object[] msgParams)
    {
        this (bundleName, messageKey, defaultMsg, msgParams, null);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, a default message (in case the resource bundle can't be found), and
     * another exception. Using this constructor is equivalent to calling the
     * {@link #VariableSubstitutionException(String,String,String,Object[])}
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
     * @see #VariableSubstitutionException(String,String,String,Object[])
     * @see #getMessage(Locale)
     */
    public VariableSubstitutionException (String    bundleName,
                                          String    messageKey,
                                          String    defaultMsg,
                                          Throwable exception)
    {
        this (bundleName, messageKey, defaultMsg, null, exception);
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
     * @param exception   exception to be nested
     *
     * @see #VariableSubstitutionException(String,String,String,Object[])
     * @see #getMessage(Locale)
     */
    public VariableSubstitutionException (String    bundleName,
                                          String    messageKey,
                                          String    defaultMsg,
                                          Object[]  msgParams,
                                          Throwable exception)
    {
        super (bundleName, messageKey, defaultMsg, msgParams, exception);
    }
}
