/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.cmdline;

import java.util.Comparator;

/**
 * Used solely by <tt>UsageInfo</tt>, an instance of this class compares
 * <tt>OptionInfo</tt> items, by short option name or long option name, in
 * a case-insensitive manner.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see UsageInfo
 * @see OptionInfo
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
final class OptionComparator implements Comparator
{
    private boolean ignoreCase = false;

    public OptionComparator()
    {
    }

    public OptionComparator (boolean ignoreCase)
    {
        this.ignoreCase = ignoreCase;
    }

    public int compare (Object o1, Object o2)
    {
        String s1 = getComparisonString ((OptionInfo) o1);
        String s2 = getComparisonString ((OptionInfo) o2);

        return ignoreCase ? s1.compareToIgnoreCase (s2) : s1.compareTo (s2);
    }

    public boolean equals (Object o)
    {
        return (compare (this, o) == 0);
    }

    public int hashCode()
    {
        return super.hashCode();
    }

    private String getComparisonString (OptionInfo opt)
    {
        String result = "";

        if (opt.shortOption != UsageInfo.NO_SHORT_OPTION)
            result = String.valueOf (opt.shortOption);
        else if (opt.longOption != null)
            result = opt.longOption;

        return result;
    }
}
