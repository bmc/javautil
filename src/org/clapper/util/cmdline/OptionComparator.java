/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.cmdline;

import java.util.Comparator;

/**
 * Used solely by <tt>UsageInfo</tt>, this method compares strings
 * in a case-insensitive manner.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see UsageInfo
 */
final class OptionComparator implements Comparator
{
    public int compare (Object o1, Object o2)
    {
        return (((String) o1).compareToIgnoreCase ((String) o2));
    }

    public boolean equals (Object o)
    {
        return (compare (this, o) == 0);
    }
}
