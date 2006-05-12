/*---------------------------------------------------------------------------*\
  $Id: ClassUtil.java 5812 2006-05-12 00:38:16Z bmc $
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

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

/**
 * Instances of classes that implement this interface are used, with a
 * {@link ClassFinder} object, to filter class names. This interface is
 * deliberately reminiscent of the <tt>java.io.FilenameFilter</tt>
 * interface.
 *
 * @see ClassFinder
 *
 * @version <tt>$Revision: 5812 $</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public interface ClassNameFilter
{
    /**
     * Tests whether a class name should be included in a class name
     * list.
     *
     * @param className  the name of the class
     *
     * @return <tt>true</tt> if and only if the name should be included
     *         in the list; <tt>false</tt> otherwise
     */
    public boolean accept (String className);
}
