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

import java.lang.reflect.Modifier;

/**
 * <p><tt>ClassModifiersClassFilter</tt> is a {@link ClassFilter} that
 * matches class names that (a) can be loaded and (b) match a set of class
 * modifiers (as defined by the constants in the
 * <tt>java.lang.reflect.Modifier</tt> class). For instance, the the
 * following code fragment defines a filter that will match only public
 * final classes:</p>
 *
 * <blockquote><pre>
 * import java.lang.reflect.Modifier;
 *
 * ...
 *
 * ClassFilter = new ClassModifiersClassFilter (Modifier.PUBLIC | Modifier.FINAL);
 * </pre></blockquote>
 *
 * <p>This class uses the Reflection API, so it actually has to load each
 * class it tests. For maximum flexibility, a
 * <tt>ClassModifiersClassFilter</tt> object can be configured to use a
 * specific class loader.</p>
 *
 * @see ClassFilter
 * @see ClassFinder
 * @see Modifier
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class ClassModifiersClassFilter
    extends ClassLoadingClassFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private int modifiers   = 0;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>ClassModifiersClassFilter</tt> that will accept
     * any classes with the specified modifiers.
     *
     * @param modifiers  the bit-field of modifier flags. See the
     *                   <tt>java.lang.reflect.Modifier</tt> class for
     *                   legal values.
     */
    public ClassModifiersClassFilter (int modifiers)
    {
        super();
        this.modifiers = modifiers;
    }

    /**
     * Construct a new <tt>ClassModifiersClassFilter</tt> that will only
     * accept only abstract classes, and will use the specified class
     * loader to load the classes it finds.
     *
     * @param modifiers   the bit-field of modifier flags. See the
     *                    <tt>java.lang.reflect.Modifier</tt> class for
     *                    legal values.
     * @param classLoader the class loader to use
     */
    public ClassModifiersClassFilter (int modifiers, ClassLoader classLoader)
    {
        super (classLoader);
        this.modifiers = modifiers;
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
    protected boolean acceptClass (Class cls)
    {
        return ((cls.getModifiers() & modifiers) != 0);
    }
}
