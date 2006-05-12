/*---------------------------------------------------------------------------*\
  $Id: SubclassClassFilter.java 5812 2006-05-12 00:38:16Z bmc $
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

/**
 * <p>This abstract base class implements the {@link ClassFilter} interface
 * and simplifies the task of writing a <tt>ClassFilter</tt> that must load
 * the class to determine whether to accept it or not. This method provides
 * the required {@link #accept(String) accept()} method, taking the class
 * name. The <tt>accept()</tt> method attempts to load the class; if
 * the load succeeeds, <tt>accept()</tt> then calls the (abstract)
 * {@link #acceptClass acceptClass()} method to perform whatever acceptance
 * tests are necessary.</p>
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public abstract class ClassLoadingClassFilter
    implements ClassFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private ClassLoader classLoader = null;

    /**
     * For logging
     */
    private static final Logger log =
        new Logger (ClassLoadingClassFilter.class);

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>ClassLoadingClassFilter</tt> that will accept
     * only classes that are interfaces.
     */
    protected ClassLoadingClassFilter()
    {
        this.classLoader = ClassLoadingClassFilter.class.getClassLoader();
    }

    /**
     * Construct a new <tt>ClassLoadingClassFilter</tt> that will
     * accept only classes that are interfaces and will use the specified
     * class loader to load the classes it finds.
     *
     * @param classLoader the class loader to use
     */
    protected ClassLoadingClassFilter (ClassLoader classLoader)
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
    public final boolean accept (String className)
    {
        boolean match = false;

        try
        {
            Class cls = classLoader.loadClass (className);
            match = acceptClass (cls);
        }

        catch (ClassNotFoundException ex)
        {
            log.warn ("Can't load class \""
                    + className
                    + "\": class not found");
        }
        
        catch (Throwable ex)
        {
            log.warn ("Can't load class \""
                    + className
                    + "\""
                    + ex.toString());
        }

        return match;
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    /**
     * Perform the acceptance test on the loaded <tt>Class</tt> object.
     *
     * @param cls  the loaded <tt>Class</tt> object
     *
     * @return <tt>true</tt> if the class name matches,
     *         <tt>false</tt> if it doesn't
     */
    protected abstract boolean acceptClass (Class cls);
}
