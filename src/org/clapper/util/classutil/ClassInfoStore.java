/*---------------------------------------------------------------------------*\
  $Id: ASMClassVisitor.java 5871 2006-05-15 18:53:05Z bmc $
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
 * <p>Simple interface for something that can "receive" and store a
 * {@link ClassInfo} object.</p>
 *
 * @version <tt>$Revision: 5871 $</tt>
 *
 * @see ClassFinder
 */
interface ClassInfoStore
 {
     /**
      * Store a {@link ClassInfo} object.
      *
      * @param classInfo  the {@link ClassInfo} object
      */
     public void add (ClassInfo classInfo);
}

