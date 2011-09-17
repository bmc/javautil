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

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is an extended version of the JDK's <tt>ResourceBundle</tt>
 * class, providing some extra methods. It can be instantiated by wrapping
 * an existing <tt>ResourceBundle</tt> object, or by using the
 * static <tt>getBundle()</tt> methods, which are identical to the
 * <tt>ResourceBundle</tt> versions.
 *
 * @version <tt>$Revision: 6687 $</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public final class XResourceBundle extends ResourceBundle
{
    /*----------------------------------------------------------------------*\
                             Private Data Items
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>XResourceBundle</tt> that wraps an existing
     * <tt>ResourceBundle</tt>.
     *
     * @param bundle the <tt>ResourceBundle</tt> to wrap.
     */
    public XResourceBundle(ResourceBundle bundle)
    {
        setParent(bundle);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Analogous to the equivalent <tt>getBundle</tt> method in the JDK's
     * <tt>ResourceBundle</tt> class.
     *
     * @param baseName  base name of the bundle to retrieve, a fully
     *                  qualified class name
     *
     * @return the bundle
     *
     * @throws NullPointerException     if <tt>baseName</tt> is null
     * @throws MissingResourceException no bundle available
     */
    public static XResourceBundle getXResourceBundle(String baseName)
    {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName);
        return (bundle == null) ? null : new XResourceBundle(bundle);
    }

    /**
     * Analogous to the equivalent <tt>getBundle</tt> method in the JDK's
     * <tt>ResourceBundle</tt> class.
     *
     * @param baseName  base name of the bundle to retrieve, a fully
     *                  qualified class name
     * @param locale    the locale for which a resource bundle is desired
     *
     * @return the bundle
     *
     * @throws NullPointerException     if <tt>baseName</tt> is null
     * @throws MissingResourceException no bundle available
     */
    public static XResourceBundle getXResourceBundle(String baseName,
                                                     Locale locale)
    {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        return (bundle == null) ? null : new XResourceBundle(bundle);
    }

    /**
     * Analogous to the equivalent <tt>getBundle</tt> method in the JDK's
     * <tt>ResourceBundle</tt> class.
     *
     * @param baseName    base name of the bundle to retrieve, a fully
     *                    qualified class name
     * @param locale      the locale for which a resource bundle is desired
     * @param classLoader class loader to use
     *
     * @return the bundle
     *
     * @throws NullPointerException     if <tt>baseName</tt> is null
     * @throws MissingResourceException no bundle available
     */
    public static XResourceBundle getXResourceBundle(String      baseName,
                                                     Locale      locale,
                                                     ClassLoader classLoader)
    {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName,
                                                         locale,
                                                         classLoader);
        return (bundle == null) ? null : new XResourceBundle(bundle);
    }

    /**
     * Returns an enumeration of the keys.
     *
     * @return the enumeration of the keys
     */
    public Enumeration<String> getKeys()
    {
        return super.parent.getKeys();
    }

    /**
     * Get a string for the given key from this resource bundle, applying a
     * default if not found. There's no equivalent for this method in the
     * <tt>ResourceBundle</tt> class.
     *
     * @param key          the key for the desired string
     * @param defaultValue the default value, if not found
     *
     * @return the value for the key, which may be the default
     */
    public String getString(String key, String defaultValue)
    {
        String result = null;

        try
        {
            result = super.parent.getString(key);
        }

        catch (MissingResourceException ex)
        {
            result = defaultValue;
        }

        return result;
    }

    /*----------------------------------------------------------------------*\
                            Protected Methods
    \*----------------------------------------------------------------------*/

    /**
     * Gets an object for the given key from this resource bundle.
     * Returns null if this resource bundle does not contain an
     * object for the given key.
     *
     * @param key the key for the desired object
     * @exception NullPointerException if <code>key</code> is <code>null</code>
     * @return the object for the given key, or null
     */
    protected Object handleGetObject(String key)
    {
        // Always delegate to the parent (wrapped) bundle.

        return null;
    }
}
