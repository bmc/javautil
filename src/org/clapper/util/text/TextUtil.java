/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.text;

import java.util.StringTokenizer;
import java.util.Collection;
import java.util.Iterator;

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
    public static boolean booleanFromString (String s)
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

    /**
     * Split a string into one or more strings, based on a delimiter. This
     * method is intended to be reminiscent of the corresponding perl or
     * awk <i>split()</i> function, though without regular expression
     * support. This method uses a <tt>StringTokenizer</tt> to do the
     * actual work.
     *
     * @param s     the string to split
     * @param delim the delimiter
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String,String)
     * @see #split(String,char,Collection)
     */
    public static String[] split (String s, char delim)
    {
        return split (s, "" + delim);
    }

    /**
     * Split a string into one or more strings, based on a set of
     * delimiter. This method is intended to be reminiscent of the
     * corresponding perl or awk <i>split()</i> function, though without
     * regular expression support. This method uses a
     * <tt>StringTokenizer</tt> to do the actual work.
     *
     * @param s         the string to split
     * @param delimSet  set of delimiters
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String,char)
     * @see #split(String,String,Collection)
     */
    public static String[] split (String s, String delimSet)
    {
        String[]        result = null;
        StringTokenizer tok    = new StringTokenizer (delimSet);

        result = new String[tok.countTokens()];

        for (int i = 0; i < result.length; i++)
            result[i] = tok.nextToken();

        return result;
    }

    /**
     * Split a string into one or more strings, based on a delimiter. This
     * method is intended to be reminiscent of the corresponding perl or
     * awk <i>split()</i> function, though without regular expression
     * support. This method uses a <tt>StringTokenizer</tt> to do the
     * actual work.
     *
     * @param s          the string to split
     * @param delim      the delimiter
     * @param collection where to store the split strings
     *
     * @return the number of <tt>String</tt> objects added to the collection
     *
     * @see #split(String,String)
     * @see #split(String,String,Collection)
     */
    public static int split (String s, char delim, Collection collection)
    {
        return split (s, "" + delim, collection);
    }

    /**
     * Split a string into one or more strings, based on a set of
     * delimiter. This method is intended to be reminiscent of the
     * corresponding perl or awk <i>split()</i> function, though without
     * regular expression support. This method uses a
     * <tt>StringTokenizer</tt> to do the actual work.
     *
     * @param s          the string to split
     * @param delimSet   set of delimiters
     * @param collection where to store the split strings
     *
     * @return the number of <tt>String</tt> objects added to the collection
     *
     * @see #split(String,char)
     * @see #split(String,char,Collection)
     */
    public static int split (String s, String delimSet, Collection collection)
    {
        String[] strs = split (s, delimSet);

        for (int i = 0; i < strs.length; i++)
            collection.add (strs[i]);

        return strs.length;
    }

    /**
     * Join a set of strings into one string, putting the specified delimiter
     * between adjacent strings.
     *
     * @param strings  the strings to be joined
     * @param delim    the delimiter string
     *
     * @return the joined string, or "" if the array is empty.
     *
     * @see #split(String,String)
     * @see #join(String[],char)
     */
    public static String join (String[] strings, String delim)
    {
        StringBuffer  result = new StringBuffer();
        String        sep    = "";

        for (int i = 0; i < strings.length; i++)
        {
            result.append (sep);
            result.append (strings[i]);
            sep = delim;
        }

        return result.toString();
    }

    /**
     * Join a set of strings into one string, putting the specified delimiter
     * between adjacent strings.
     *
     * @param strings  the strings to be joined
     * @param delim    the delimiter character
     *
     * @return the joined string, or "" if the array is empty.
     *
     * @see #split(String,char)
     * @see #join(String[],String)
     */
    public static String join (String[] strings, char delim)
    {
        return join (strings, "" + delim);
    }

    /**
     * Join a set of strings into one string, putting the specified delimiter
     * between adjacent strings.
     *
     * @param objects  A collection the items to be joined. This collection
     *                 can contain objects of any type; each object's
     *                 <tt>toString()</tt> method is called to produce the
     *                 string to be joined.
     * @param delim    the delimiter string
     *
     * @return the joined string, or "" if the collection is empty.
     *
     * @see #split(String,String,Collection)
     * @see #join(Collection,char)
     */
    public static String join (Collection objects, String delim)
    {
        String result = "";

        if (objects.size() > 0)
        {
            String[] array = new String[objects.size()];
            int      i;
            Iterator it;

            for (i = 0, it = objects.iterator(); it.hasNext(); i++)
                array[i] = it.next().toString();

            result = join (array, delim);
        }

        return result;
    }

    /**
     * Join a set of strings into one string, putting the specified delimiter
     * between adjacent strings.
     *
     * @param objects  A collection the items to be joined. This collection
     *                 can contain objects of any type; each object's
     *                 <tt>toString()</tt> method is called to produce the
     *                 string to be joined.
     * @param delim    the delimiter character
     *
     * @return the joined string, or "" if the collection is empty.
     *
     * @see #split(String,char,Collection)
     * @see #join(Collection,String)
     */
    public static String join (Collection objects, char delim)
    {
        return join (objects, "" + delim);
    }
}
