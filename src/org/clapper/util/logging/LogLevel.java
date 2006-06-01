/*---------------------------------------------------------------------------*\
  $Id$
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

package org.clapper.util.logging;

/**
 * Encodes legal logging level constants for the {@link Logger} class.
 *
 * @version <tt>$Revision$</tt>
 */
public enum LogLevel
{
    /*----------------------------------------------------------------------*\
                        Enumeration Constant Values
    \*----------------------------------------------------------------------*/

    /**
     * Log message at "trace" level
     */
    TRACE (org.apache.log4j.Level.TRACE),

    /**
     * Log message at "debug" level
     */
    DEBUG (org.apache.log4j.Level.DEBUG),

    /**
     * Log message at "informational message" level
     */
    INFO (org.apache.log4j.Level.INFO),

    /**
     * Log message at "warning" level
     */
    WARNING (org.apache.log4j.Level.WARN);

    /**
     * Log message at "error" level
     */
    ERROR (org.apache.log4j.Level.ERROR),

    /**
     * Log message at "fatal error" level
     */
    FATAL (org.apache.log4j.Level.FATAL),

    private org.apache.log4j.Level realLevel;

    LogLevel (org.apache.log4j.Level level)
    {
        this.realLevel = level;
    }

    org.apache.log4j.Level getLevel()
    {
        return this.realLevel;
    }
}
