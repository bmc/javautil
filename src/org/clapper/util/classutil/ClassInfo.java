/*---------------------------------------------------------------------------*\
  $Id: ClassUtil.java 5607 2005-11-25 04:32:30Z bmc $
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

import java.io.File;

import java.lang.reflect.Modifier;

import org.objectweb.asm.Opcodes;

/**
 * <p>Holds information about a loaded class in a way that doesn't rely on
 * the underlying API used to load the class information.</p>
 *
 * @version <tt>$Revision: 5607 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class ClassInfo
{
    /*----------------------------------------------------------------------*\
			    Private Data Items
    \*----------------------------------------------------------------------*/

    private int      modifier = 0;
    private String   className = null;
    private String   superClassName = null;
    private String[] implementedInterfaces = null;
    private File     locationFound = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>ClassInfo</tt> object.
     *
     * @param name           the class name
     * @param superClassName the parent class name, or null
     * @param interfaces     the names of interfaces the class implements,
     *                       or null
     * @param asmAccessMask  ASM API's access mask for the class
     * @param location       File (jar, zip) or directory where class was found
     */
    ClassInfo (String   name,
               String   superClassName,
               String[] interfaces,
               int      asmAccessMask,
               File     location)
    {
        this.className = translateInternalClassName (name);
        this.locationFound = location;

        if (! superClassName.equals ("java/lang/Object"))
            this.superClassName = translateInternalClassName (superClassName);

        if (interfaces != null)
        {
            this.implementedInterfaces = new String[interfaces.length];
            for (int i = 0; i < interfaces.length; i++)
            {
                this.implementedInterfaces[i] =
                    translateInternalClassName (interfaces[i]);
            }
        }

        // Convert the ASM access info into Reflection API modifiers.

        if ((asmAccessMask & Opcodes.ACC_FINAL) != 0)
            modifier |= Modifier.FINAL;

        if ((asmAccessMask & Opcodes.ACC_NATIVE) != 0)
            modifier |= Modifier.NATIVE;

        if ((asmAccessMask & Opcodes.ACC_INTERFACE) != 0)
            modifier |= Modifier.INTERFACE;

        if ((asmAccessMask & Opcodes.ACC_ABSTRACT) != 0)
            modifier |= Modifier.ABSTRACT;

        if ((asmAccessMask & Opcodes.ACC_PRIVATE) != 0)
            modifier |= Modifier.PRIVATE;

        if ((asmAccessMask & Opcodes.ACC_PROTECTED) != 0)
            modifier |= Modifier.PROTECTED;

        if ((asmAccessMask & Opcodes.ACC_PUBLIC) != 0)
            modifier |= Modifier.PUBLIC;

        if ((asmAccessMask & Opcodes.ACC_STATIC) != 0)
            modifier |= Modifier.STATIC;

        if ((asmAccessMask & Opcodes.ACC_STRICT) != 0)
            modifier |= Modifier.STRICT;

        if ((asmAccessMask & Opcodes.ACC_SYNCHRONIZED) != 0)
            modifier |= Modifier.SYNCHRONIZED;

        if ((asmAccessMask & Opcodes.ACC_TRANSIENT) != 0)
            modifier |= Modifier.TRANSIENT;

        if ((asmAccessMask & Opcodes.ACC_VOLATILE) != 0)
            modifier |= Modifier.VOLATILE;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the class name.
     *
     * @return the class name
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Get the parent (super) class name, if any. Returns null if the
     * superclass is <tt>java.lang.Object</tt>. Note: To find other
     * ancestor classes, use {@link ClassFinder#findAllSuperClasses}.
     *
     * @return the super class name, or null
     *
     * @see ClassFinder#findAllSuperClasses
     */
    public String getSuperClassName()
    {
        return superClassName;
    }

    /**
     * Get the names of all <i>directly</i> implemented interfaces. To find
     * indirectly implemented interfaces, use
     * {@link ClassFinder#findAllInterfaces}.
     *
     * @return an array of the names of all directly implemented interfaces,
     *         or null if there are none
     *
     * @see ClassFinder#findAllInterfaces
     */
    public String[] getInterfaces()
    {
        return implementedInterfaces;
    }

    /**
     * Get the Reflection API-based modifier bitfield for the class. Use
     * <tt>java.lang.reflect.Modifier</tt> to decode this bitfield.
     *
     * @return the modifier
     */
    public int getModifier()
    {
        return modifier;
    }

    /**
     * Get the location (the jar file, zip file or directory) where the
     * class was found.
     *
     * @return where the class was found
     */
    public File getClassLocation()
    {
        return locationFound;
    }

    /**
     * Get a string representation of this object.
     *
     * @return the string representation
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        if ((modifier & Modifier.PUBLIC) != 0)
            buf.append ("public ");

        if ((modifier & Modifier.ABSTRACT) != 0)
            buf.append ("abstract ");

        if ((modifier & Modifier.INTERFACE) != 0)
            buf.append ("interface ");
        else
            buf.append ("class ");

        buf.append (className);

        String sep = " ";
        if (implementedInterfaces.length > 0)
        {
            buf.append (" implements");
            for (String intf : implementedInterfaces)
            {
                buf.append (sep);
                buf.append (intf);
            }
        }

        if ((superClassName != null) &&
            (! superClassName.equals ("java.lang.Object")))
        {
            buf.append (sep);
            buf.append ("extends ");
            buf.append (superClassName);
        }

        return (buf.toString());
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Translate an internal class/interface name to an external one.
     *
     * @param internalName the internal JVM name, from the ASM API
     *
     * @return the external name
     */
    private String translateInternalClassName (String internalName)
    {
        return internalName.replaceAll ("/", ".");
    }
}
