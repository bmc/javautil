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
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public final class BundleUtil
{
    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

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
    public static final String getMessage (String bundleName,
                                           Locale locale,
                                           String key,
                                           String defaultMsg)
    {
        return getMessage (bundleName, locale, key, defaultMsg, null);
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
    public static final String getMessage (String   bundleName,
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
            try
            {
                String fmt = bundle.getString (key);
                if (fmt == null)
                    fmt = defaultMsg;

                if (fmt != null)
                    result = MessageFormat.format (fmt, params);
            }

            catch (MissingResourceException ex)
            {
            }
        }

        if (result == null)
            result = defaultMsg;

        return result;
    }
}
