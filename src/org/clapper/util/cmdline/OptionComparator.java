/*---------------------------------------------------------------------------*\
  $Id$
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
