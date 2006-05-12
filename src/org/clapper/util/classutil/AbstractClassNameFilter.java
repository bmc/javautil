/*---------------------------------------------------------------------------*\
  $Id: SubclassClassNameFilter.java 5812 2006-05-12 00:38:16Z bmc $
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2006 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.classutil;

import org.clapper.util.logging.Logger;

import java.lang.reflect.Modifier;

/**
 * <p><tt>AbstractClassNameFilter</tt> implements a {@link ClassNameFilter}
 * that matches class names that (a) can be loaded and (b) are abstract.
 * subclass or implement a specified interface, directly or indirectly. It
 * uses the Reflection API, so it actually has to load each class it tests.
 * For maximum flexibility, a <tt>AbstractClassNameFilter</tt> can be
 * configured to use a specific class loader.</p>
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class AbstractClassNameFilter
    implements ClassNameFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private ClassLoader classLoader = null;

    /**
     * For logging
     */
    private static final Logger log =
        new Logger (AbstractClassNameFilter.class);

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>AbstractClassNameFilter</tt> that will accept
     * only abstract classes.
     */
    public AbstractClassNameFilter()
    {
        this.classLoader = AbstractClassNameFilter.class.getClassLoader();
    }

    /**
     * Construct a new <tt>AbstractClassNameFilter</tt> that will only
     * accept only abstract classes, and will use the specified class
     * loader to load the classes it finds.
     *
     * @param classLoader the class loader to use
     */
    public AbstractClassNameFilter (ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether a class name is to be accepted or not, based on
     * whether it implements the interface that was pass to the
     * constructor.
     *
     * @param className  the class name
     *
     * @return <tt>true</tt> if the class name matches,
     *         <tt>false</tt> if it doesn't
     */
    public boolean accept (String className)
    {
        boolean match = false;

        try
        {
            Class cls = classLoader.loadClass (className);
            match = ((cls.getModifiers() & Modifier.ABSTRACT) != 0);
        }

        catch (ClassNotFoundException ex)
        {
            log.error ("Can't load class \""
                     + className
                     + "\": class not found");
        }
        
        return match;
    }
}
