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

import java.lang.reflect.Modifier;

/**
 * <p><tt>AbstractClassFilter</tt> implements a {@link ClassFilter}
 * that matches class names that (a) can be loaded and (b) are abstract. It
 * relies on the pool of classes read by a {@link ClassFinder}; it's
 * not really useful by itself.</p>
 *
 * <p>This class is really just a convenient specialization of the
 * {@link ClassModifiersClassFilter} class.</p>
 *
 * @see ClassFilter
 * @see ClassModifiersClassFilter
 * @see ClassFinder
 * @see Modifier
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class AbstractClassFilter
    extends ClassModifiersClassFilter
{
    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>AbstractClassFilter</tt> that will accept
     * only abstract classes.
     */
    public AbstractClassFilter()
    {
        super (Modifier.ABSTRACT);
    }
}
