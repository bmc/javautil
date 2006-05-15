/*---------------------------------------------------------------------------*\
  $Id$
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

import java.util.Map;
import java.util.HashMap;

/**
 * <p><tt>SubclassClassFilter</tt> is a {@link ClassFilter} that matches
 * class names that (a) can be loaded and (b) extend a given subclass or
 * implement a specified interface, directly or indirectly. It uses the
 * <tt>java.lang.Class.isAssignableFrom()</p> method, so it actually has to
 * load each class it tests. For maximum flexibility, a
 * <tt>SubclassClassFilter</tt> can be configured to use a specific class
 * loader.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class SubclassClassFilter implements ClassFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private Class baseClass;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>SubclassClassFilter</tt> that will accept
     * only classes that extend the specified class or implement the
     * specified interface.
     *
     * @param baseClassOrInterface  the base class or interface
     */
    public SubclassClassFilter (Class baseClassOrInterface)
    {
        this.baseClass = baseClassOrInterface;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Perform the acceptance test on the loaded <tt>Class</tt> object.
     *
     * @param classInfo   the {@link ClassInfo} object to test
     * @param classFinder the invoking {@link ClassFinder} object
     *
     * @return <tt>true</tt> if the class name matches,
     *         <tt>false</tt> if it doesn't
     */
    public boolean accept (ClassInfo classInfo, ClassFinder classFinder)
    {
        boolean               match = false;
        Map<String,ClassInfo> superClasses = new HashMap<String,ClassInfo>();

        if (baseClass.isInterface())
            classFinder.findAllInterfaces (classInfo, superClasses);
        else
            classFinder.findAllSuperClasses (classInfo, superClasses);

        return superClasses.keySet().contains (baseClass.getName());
    }
}
