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

package org.clapper.util.misc;

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.text.MessageFormat;

/**
 * <tt>ResourceBundle</tt> utilities to aid in localization.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public final class BundleUtil
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private BundleUtil()
    {
        // Can't be instantiated
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get a string from a bundle, using the default locale.
     *
     * @param bundleName the bundle name
     * @param key        the key to look up
     *
     * @return the value for the key, or the default value
     */
    public static String getString (String bundleName, String key)
    {
        return getMessage (bundleName, null, key, (String) null);
    }

    /**
     * Get a string from a bundle, using the default locale.
     *
     * @param bundleName the bundle name
     * @param key        the key to look up
     * @param defaultMsg the default value, or null
     *
     * @return the value for the key, or the default value
     */
    public static String getString (String bundleName,
                                    String key,
                                    String defaultMsg)
    {
        return getMessage (bundleName, null, key, defaultMsg);
    }

    /**
     * Get a localized message from a bundle.
     *
     * @param bundleName the name of the resource bundle
     * @param locale     the locale
     * @param key        the key
     * @param defaultMsg the default message
     *
     * @return the message, or the default
     */
    public static String getMessage (String bundleName,
                                     Locale locale,
                                     String key,
                                     String defaultMsg)
    {
        return getMessage (bundleName, locale, key, defaultMsg, null);
    }

    /**
     * Get a message from the bundle using the default locale.
     *
     * @param bundleName the name of the resource bundle
     * @param key        the key
     * @param params     parameters for the message
     *
     * @return the message, or the default
     */
    public static String getMessage (String   bundleName,
                                     String   key,
                                     Object[] params)
    {
        return getMessage (bundleName, Locale.getDefault(), key, params);
    }

    /**
     * Get a localized message from the bundle.
     *
     * @param bundleName the name of the resource bundle
     * @param locale     the locale
     * @param key        the key
     * @param defaultMsg the default message
     * @param params     parameters for the message
     *
     * @return the message, or the default
     */
    public static String getMessage (String   bundleName,
                                     Locale   locale,
                                     String   key,
                                     String   defaultMsg,
                                     Object[] params)
    {
        ResourceBundle bundle;
        String         result = null;

        if (locale == null)
            locale = Locale.getDefault();

        bundle = ResourceBundle.getBundle (bundleName, locale);
        if (bundle != null)
        {
            String fmt = null;
            try
            {
                fmt = bundle.getString (key);
            }
            catch (MissingResourceException ex)
            {
            }

            if (fmt == null)
                fmt = defaultMsg;

            if (fmt != null)
                result = MessageFormat.format(fmt, params);
        }

        if (result == null)
            result = defaultMsg;

        return result;
    }

    /**
     * Get a localized message from the bundle.
     *
     * @param bundleName the name of the resource bundle
     * @param locale     the locale
     * @param key        the key
     * @param params     parameters for the message
     *
     * @return the message, or the default
     */
    public static String getMessage (String   bundleName,
                                     Locale   locale,
                                     String   key,
                                     Object[] params)
    {
        ResourceBundle bundle;
        String         result = null;

        if (locale == null)
            locale = Locale.getDefault();

        bundle = ResourceBundle.getBundle (bundleName, locale);
        if (bundle != null)
        {
            try
            {
                String fmt = bundle.getString (key);
                if (fmt != null)
                    result = MessageFormat.format (fmt, params);
            }

            catch (MissingResourceException ex)
            {
            }
        }

        return result;
    }
}
