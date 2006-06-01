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

import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.ClassVisitor;

import java.io.File;
import java.util.Map;

/**
 * <p>An ASM <tt>ClassVisitor</tt> that records the appropriate class
 * information for a {@link ClassFinder} object.</p>
 *
 * <p>This class relies on the ASM byte-code manipulation library. If that
 * library is not available, this package will not work. See
 * <a href="http://asm.objectweb.org"><i>asm.objectweb.org</i></a>
 * for details on ASM.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @see ClassFinder
 */
class ClassInfoClassVisitor extends EmptyVisitor
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private Map<String,ClassInfo> foundClasses;
    private File                  location;

    /*----------------------------------------------------------------------*\
                               Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Constructor
     *
     * @param foundClasses  where to store the class information. The
     *                      {@link ClassInfo} records are stored in the map,
     *                      indexed by class name.
     * @param location      file (jar, zip) or directory containing classes
     *                      being processed by this visitor
     * 
     */
    ClassInfoClassVisitor (Map<String,ClassInfo> foundClasses, File location)
    {
        this.foundClasses = foundClasses;
        this.location = location;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * "Visit" a class. Required by ASM <tt>ClassVisitor</tt> interface.
     *
     * @param version     class version
     * @param access      class access modifiers, etc.
     * @param name        internal class name
     * @param signature   class signature (not used here)
     * @param superName   internal super class name
     * @param interfaces  internal names of all directly implemented
     *                    interfaces
     */
    public void visit (int      version,
                       int      access,
                       String   name,
                       String   signature,
                       String   superName,
                       String[] interfaces)
    {
        ClassInfo classInfo = new ClassInfo (name,
                                             superName,
                                             interfaces,
                                             access,
                                             location);
        // Be sure to use the converted name from classInfo.getName(), not
        // the internal value in "name".

        foundClasses.put (classInfo.getClassName(), classInfo);
    }

    /**
     * Get the location (the jar file, zip file or directory) containing
     * the classes processed by this visitor.
     *
     * @return where the class was found
     */
    public File getClassLocation()
    {
        return location;
    }
}
