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

package org.clapper.util.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Pattern;

/**
 * Static class containing miscellaneous text utility methods.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public final class TextUtil
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * Used with the regular expression-based versions of <tt>split()</tt>
     * as a <tt>limit</tt> parameter that indicates no limit (i.e., split
     * the entire string).
     *
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection,int)
     */
    public static final int SPLIT_ALL =
                                     org.apache.oro.text.regex.Util.SPLIT_ALL;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private TextUtil()
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
     * <p>Split a string on white space, into one or more strings. This
     * method is intended to be reminiscent of the corresponding perl or
     * awk <i>split()</i> function, though without regular expression
     * support. This method uses a <tt>StringTokenizer</tt> to do the
     * actual work.</p>
     *
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.
     * Similarly, the Jakarta ORO regular expression library contains
     * a regular-expression based <tt>split()</tt> method, in
     * <tt>org.apache.oro.text.regex.Util</tt>. See also this class's
     * {@link #split(String,Pattern)}, {@link #split(String,Pattern,int)},
     * {@link #split(String,Pattern,Collection,int)} and
     * {@link #split(String,Pattern,Collection)} methods, which use the
     * Jakarta ORO library.</p>
     *
     * @param s  the string to split
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String,String)
     * @see #split(String,char)
     * @see #split(String,Collection)
     * @see #split(String,char,Collection)
     * @see #split(String,String,Collection)
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection)
     * @see #split(String,Pattern,Collection,int)
     */
    public static String[] split (String s)
    {
        return split (s, (String) null);
    }

    /**
     * <p>Split a string on white space, into one or more strings. This
     * method is intended to be reminiscent of the corresponding perl or
     * awk <i>split()</i> function, though without regular expression
     * support. This method uses a <tt>StringTokenizer</tt> to do the
     * actual work.</p>
     *
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.
     * Similarly, the Jakarta ORO regular expression library contains
     * a regular-expression based <tt>split()</tt> method, in
     * <tt>org.apache.oro.text.regex.Util</tt>. See also this class's
     * {@link #split(String,Pattern)}, {@link #split(String,Pattern,int)},
     * {@link #split(String,Pattern,Collection,int)} and
     * {@link #split(String,Pattern,Collection)} methods, which use the
     * Jakarta ORO library.</p>
     *
     * @param s          the string to split
     * @param collection where to store the split strings
     *
     * @return the number of strings added to the collection
     *
     * @see #split(String)
     * @see #split(String,String)
     * @see #split(String,char)
     * @see #split(String,char,Collection)
     * @see #split(String,String,Collection)
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection)
     * @see #split(String,Pattern,Collection,int)
     */
    public static int split (String s, Collection collection)
    {
        return split (s, (String) null, collection);
    }

    /**
     * <p>Split a string into one or more strings, based on a delimiter.
     * This method is intended to be reminiscent of the corresponding perl
     * or awk <i>split()</i> function, though without regular expression
     * support. This method uses a <tt>StringTokenizer</tt> to do the
     * actual work.</p>
     *
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.
     * Similarly, the Jakarta ORO regular expression library contains
     * a regular-expression based <tt>split()</tt> method, in
     * <tt>org.apache.oro.text.regex.Util</tt>. See also this class's
     * {@link #split(String,Pattern)}, {@link #split(String,Pattern,int)},
     * {@link #split(String,Pattern,Collection,int)} and
     * {@link #split(String,Pattern,Collection)} methods, which use the
     * Jakarta ORO library.</p>
     *
     * @param s     the string to split
     * @param delim the delimiter
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String)
     * @see #split(String,String)
     * @see #split(String,Collection)
     * @see #split(String,char,Collection)
     * @see #split(String,String,Collection)
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection)
     * @see #split(String,Pattern,Collection,int)
     */
    public static String[] split (String s, char delim)
    {
        return split (s, "" + delim);
    }

    /**
     * <p>Split a string into one or more strings, based on a set of
     * delimiter. This method is intended to be reminiscent of the
     * corresponding perl or awk <i>split()</i> function, though without
     * regular expression support. This method uses a
     * <tt>StringTokenizer</tt> to do the actual work.</p>
     *
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.
     * Similarly, the Jakarta ORO regular expression library contains
     * a regular-expression based <tt>split()</tt> method, in
     * <tt>org.apache.oro.text.regex.Util</tt>. See also this class's
     * {@link #split(String,Pattern)}, {@link #split(String,Pattern,int)},
     * {@link #split(String,Pattern,Collection,int)} and
     * {@link #split(String,Pattern,Collection)} methods, which use the
     * Jakarta ORO library.</p>
     *
     * @param s         the string to split
     * @param delimSet  set of delimiters, or null to use white space
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String)
     * @see #split(String,char)
     * @see #split(String,Collection)
     * @see #split(String,char,Collection)
     * @see #split(String,String,Collection)
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection)
     * @see #split(String,Pattern,Collection,int)
     */
    public static String[] split (String s, String delimSet)
    {
        String[]        result = null;
        StringTokenizer tok;

        if (delimSet != null)
            tok = new StringTokenizer (s, delimSet);
        else
            tok = new StringTokenizer (s);

        result = new String[tok.countTokens()];

        for (int i = 0; i < result.length; i++)
            result[i] = tok.nextToken();

        return result;
    }

    /**
     * <p>Split a string into one or more strings, based on a delimiter. This
     * method is intended to be reminiscent of the corresponding perl or
     * awk <i>split()</i> function, though without regular expression
     * support. This method uses a <tt>StringTokenizer</tt> to do the
     * actual work.</p>
     *
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.
     * Similarly, the Jakarta ORO regular expression library contains
     * a regular-expression based <tt>split()</tt> method, in
     * <tt>org.apache.oro.text.regex.Util</tt>. See also this class's
     * {@link #split(String,Pattern)}, {@link #split(String,Pattern,int)},
     * {@link #split(String,Pattern,Collection,int)} and
     * {@link #split(String,Pattern,Collection)} methods, which use the
     * Jakarta ORO library.</p>
     *
     * @param s          the string to split
     * @param delim      the delimiter
     * @param collection where to store the split strings
     *
     * @return the number of <tt>String</tt> objects added to the collection
     *
     * @see #split(String)
     * @see #split(String,char)
     * @see #split(String,Collection)
     * @see #split(String,String)
     * @see #split(String,String,Collection)
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection)
     * @see #split(String,Pattern,Collection,int)
     */
    public static int split (String s, char delim, Collection collection)
    {
        return split (s, "" + delim, collection);
    }

    /**
     * <p>Split a string into one or more strings, based on a set of
     * delimiter. This method is intended to be reminiscent of the
     * corresponding perl or awk <i>split()</i> function, though without
     * regular expression support. This method uses a
     * <tt>StringTokenizer</tt> to do the actual work.</p>
     *
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.</p>
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.
     * Similarly, the Jakarta ORO regular expression library contains
     * a regular-expression based <tt>split()</tt> method, in
     * <tt>org.apache.oro.text.regex.Util</tt>. See also this class's
     * {@link #split(String,Pattern)}, {@link #split(String,Pattern,int)},
     * {@link #split(String,Pattern,Collection,int)} and
     * {@link #split(String,Pattern,Collection)} methods, which use the
     * Jakarta ORO library.</p>
     *
     * @param s          the string to split
     * @param delimSet   set of delimiters
     * @param collection where to store the split strings
     *
     * @return the number of <tt>String</tt> objects added to the collection
     *
     * @see #split(String)
     * @see #split(String,char)
     * @see #split(String,Collection)
     * @see #split(String,String)
     * @see #split(String,char,Collection)
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection)
     * @see #split(String,Pattern,Collection,int)
     */
    public static int split (String s, String delimSet, Collection collection)
    {
        String[] strs = split (s, delimSet);

        for (int i = 0; i < strs.length; i++)
            collection.add (strs[i]);

        return strs.length;
    }

    /**
     * <p>Split a string on a regular expression, into one or more strings.
     * This method uses the Jakarta ORO regular expression library. Calling
     * this method is equivalent to calling {@link #split(String,Pattern,int)}
     * with a <tt>limit</tt> parameter of {@link #SPLIT_ALL}.</p>
     *
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.</p>
     *
     * @param s       string to split
     * @param pattern the Jakarta ORO compiled regular expression to use
     *                to split the string
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection)
     * @see #split(String,Pattern,Collection,int)
     * @see #split(String)
     * @see #split(String,String)
     * @see #split(String,char)
     * @see #split(String,Collection)
     * @see #split(String,char,Collection)
     * @see #split(String,String,Collection)
     */
    public static String[] split (String s, Pattern pattern)
    {
        return split (s, pattern, SPLIT_ALL);
    }

    /**
     * <p>Split a string on a regular expression, into one or more strings.
     * This method uses the Jakarta ORO regular expression library. Calling
     * this method is equivalent to calling
     * {@link #split(String,Pattern,Collection,int)}
     * with a <tt>limit</tt> parameter of {@link #SPLIT_ALL}.</p>
     *
     * <p>Note that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.</p>
     *
     * @param s          string to split
     * @param pattern    the Jakarta ORO compiled regular expression to use
     *                   to split the string
     * @param collection where to store the resulting split strings
     *
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection,int)
     * @see #split(String)
     * @see #split(String,String)
     * @see #split(String,char)
     * @see #split(String,Collection)
     * @see #split(String,char,Collection)
     * @see #split(String,String,Collection)
     */
    public static void split (String s, Pattern pattern, Collection collection)
    {
        split (s, pattern, collection, SPLIT_ALL);
    }

    /**
     * <p>Split a string on a regular expression, into one or more strings.
     * The <tt>limit</tt> parameter essentially says to split the string
     * only on at most the first <tt>(limit - 1)</tt> number of pattern
     * occurences.</p>
     *
     * <p>This method uses the Jakarta ORO regular expression library. Note
     * that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.</p>
     *
     * @param s       string to split
     * @param pattern the Jakarta ORO compiled regular expression to use
     *                to split the string
     * @param limit   The limit on the number of resulting split elements.
     *                Values <= 0 produce the same behavior as using the
     *                {@link #SPLIT_ALL} constant, which causes the limit to
     *                be ignored and splits to be performed on all occurrences
     *                of the pattern. You should use the <tt>SPLIT_ALL</tt>
     *                constant to achieve this behavior, instead of relying
     *                on the default behavior associated with non-positive
     *                limit values.
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,Collection)
     * @see #split(String,Pattern,Collection,int)
     * @see #split(String)
     * @see #split(String,String)
     * @see #split(String,char)
     * @see #split(String,Collection)
     * @see #split(String,char,Collection)
     * @see #split(String,String,Collection)
     */
    public static String[] split (String s, Pattern pattern, int limit)
    {
        ArrayList collection = new ArrayList();

        split (s, pattern, collection, limit);

        String[] result = new String[collection.size()];

        return (String[]) collection.toArray (result);
    }

    /**
     * <p>Split a string on a regular expression, into one or more strings.
     * The <tt>limit</tt> parameter essentially says to split the string
     * only on at most the first <tt>(limit - 1)</tt> number of pattern
     * occurences.</p>
     *
     * <p>This method uses the Jakarta ORO regular expression library. Note
     * that the 1.4 JDK introduces a regular expression-based
     * <tt>split()</tt> method in the <tt>java.lang.String</tt> class.</p>
     *
     * @param s          string to split
     * @param pattern    the Jakarta ORO compiled regular expression to use
     *                   to split the string
     * @param collection where to store the split strings
     * @param limit      The limit on the number of resulting split elements.
     *                   Values <= 0 produce the same behavior as using the
     *                   {@link #SPLIT_ALL} constant, which causes the
     *                   limit to be ignored and splits to be performed on
     *                   all occurrences of the pattern. You should use the
     *                   <tt>SPLIT_ALL</tt> constant to achieve this
     *                   behavior, instead of relying on the default
     *                   behavior associated with non-positive limit
     *                   values.
     *
     * @see #split(String,Pattern)
     * @see #split(String,Pattern,int)
     * @see #split(String,Pattern,Collection)
     * @see #split(String)
     * @see #split(String,String)
     * @see #split(String,char)
     * @see #split(String,Collection)
     * @see #split(String,char,Collection)
     * @see #split(String,String,Collection)
     */
    public static void split (String     s,
                              Pattern    pattern,
                              Collection collection,
                              int        limit)
    {
        org.apache.oro.text.regex.Util.split (collection,
                                              new Perl5Matcher(),
                                              pattern,
                                              s,
                                              limit);
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

    /**
     * Determine whether a given string is empty. A string is empty if it
     * is null, zero-length, or comprised entirely of white space.
     *
     * @param s  the string to test
     *
     * @return <tt>true</tt> if it's empty, <tt>false</tt> if not.
     */
    public static boolean stringIsEmpty (String s)
    {
        return ((s == null) || (s.trim().length() == 0));
    }

    /**
     * Right justify a string in a fixed-width field, using blanks for
     * padding. If the string is already longer than the field width, it is
     * returned unchanged.
     *
     * @param s      the string
     * @param width  the desired field width
     *
     * @return a right-justified version of the string
     *
     * @see #rightJustifyString(String,int,char)
     * @see #leftJustifyString(String,int)
     * @see #centerString(String,int)
     */
    public static String rightJustifyString (String s, int width)
    {
        return rightJustifyString (s, width, ' ');
    }

    /**
     * Right justify a string in a fixed-width field, using the specified
     * character for padding. If the string is already longer than the
     * field width, it is returned unchanged.
     *
     * @param s      the string
     * @param width  the desired field width
     * @param c      the pad character
     *
     * @return a right-justified version of the string
     *
     * @see #rightJustifyString(String,int)
     * @see #leftJustifyString(String,int,char)
     * @see #centerString(String,int,char)
     */
    public static String rightJustifyString (String s, int width, char c)
    {
        StringBuffer  paddedString = new StringBuffer (width);
        int           paddingNeeded;
        int           len = s.length();

        paddingNeeded = (width < len) ? 0 : (width - len);

        for (int i = 0; i < paddingNeeded; i++)
            paddedString.append (c);

        paddedString.append (s);

        return paddedString.toString();
    }

    /**
     * Left justify a string in a fixed-width field, using blanks for
     * padding. If the string is already longer than the field width, it is
     * returned unchanged.
     *
     * @param s      the string
     * @param width  the desired field width
     *
     * @return a left-justified version of the string
     *
     * @see #leftJustifyString(String,int,char)
     * @see #rightJustifyString(String,int)
     * @see #centerString(String,int)
     */
    public static String leftJustifyString (String s, int width)
    {
        return leftJustifyString (s, width, ' ');
    }

    /**
     * Left justify a string in a fixed-width field, using the specified
     * character for padding. If the string is already longer than the
     * field width, it is returned unchanged.
     *
     * @param s      the string
     * @param width  the desired field width
     * @param c      the pad character
     *
     * @return a left-justified version of the string
     *
     * @see #leftJustifyString(String,int)
     * @see #rightJustifyString(String,int,char)
     * @see #centerString(String,int,char)
     */
    public static String leftJustifyString (String s, int width, char c)
    {
        StringBuffer  paddedString = new StringBuffer (width);
        int           paddingNeeded;
        int           len = s.length();

        paddingNeeded = (width < len) ? 0 : (width - len);
        paddedString.append (s);

        for (int i = 0; i < paddingNeeded; i++)
            paddedString.append (c);

        return paddedString.toString();
    }

    /**
     * Center a string in a fixed-width field, using blanks for padding. If
     * the string is already longer than the field width, it is returned
     * unchanged.
     *
     * @param s      the string
     * @param width  the desired field width
     *
     * @return a centered version of the string
     *
     * @see #centerString(String,int,char)
     * @see #rightJustifyString(String,int)
     * @see #leftJustifyString(String,int)
     */
    public static String centerString (String s, int width)
    {
        return centerString (s, width, ' ');
    }

    /**
     * Right justify a string in a fixed-width field, using the specified
     * character for padding. If the string is already longer than the
     * field width, it is returned unchanged.
     *
     * @param s      the string
     * @param width  the desired field width
     * @param c      the pad character
     *
     * @return a right-justified version of the string
     *
     * @see #centerString(String,int,char)
     * @see #leftJustifyString(String,int,char)
     * @see #rightJustifyString(String,int,char)
     */
    public static String centerString (String s, int width, char c)
    {
        StringBuffer  paddedString = new StringBuffer (width);
        int           paddingNeeded;
        int           len = s.length();
        int           frontPadding;
        int           tailPadding;
        int           i;

        paddingNeeded = (width < len) ? 0 : (width - len);
        i = paddingNeeded / 2;
        frontPadding = i;
        tailPadding  = i + (paddingNeeded % 2);

        for (i = 0; i < frontPadding; i++)
            paddedString.append (c);

        paddedString.append (s);

        for (i = 0; i < tailPadding; i++)
            paddedString.append (c);

        return paddedString.toString();
    }
}
