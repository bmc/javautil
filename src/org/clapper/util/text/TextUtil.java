/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.text;

/**
 * Static class containing miscellaneous text utility methods.
 *
 * @version <tt>$Revision$</tt>
 */
public class TextUtils
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private TextUtils()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Convert a boolean string to a boolean value. This method is more
     * generous than <tt>java.lang.Boolean.booleanValue()</tt>. The following
     * strings (in upper or lower case) are recognized as <tt>true</tt>:
     * "1", "true", "yes", "y". The following
     * strings (in upper or lower case) are recognized as <tt>false</tt>:
     * "0", "false", "no", "n".
     *
     * @param s   string to convert
     *
     * @return <tt>true</tt> or <tt>false</tt>
     *
     * @throws IllegalArgumentException  string isn't a boolean
     */
    public static boolean booleanToString (String s)
        throws IllegalArgumentException
    {
        boolean result;

        s = s.toLowerCase();

        if ((s.equals ("true") ||
             s.equals ("1")    ||
             s.equals ("yes")  ||
             s.equals ("y")))
        {
            result = true;
        }

        else if ((s.equals ("false") ||
                  s.equals ("0")     ||
                  s.equals ("no")    ||
                  s.equals ("n")))
        {
            result = false;
        }

        else
        {
            throw new IllegalArgumentException ("Bad boolean string: \""
                                              + s
                                              + "\"");
        }

        return result;
    }
}
