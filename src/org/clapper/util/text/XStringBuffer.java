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

import java.net.URL;

import java.util.Collection;

import java.io.StringReader;
import java.io.PushbackReader;
import java.io.IOException;

import org.clapper.util.text.TextUtil;

/**
 * An <tt>XStringBuffer</tt> objects wraps a standard Java
 * <tt>StringBuffer</tt> object, providing a superset of
 * <tt>StringBuffer</tt>'s functionality. (<tt>XStringBuffer</tt>
 * cannot actually subclass <tt>StringBuffer</tt>, since
 * <tt>StringBuffer</tt> is final.) Among the additional methods that this
 * class provides are:
 *
 * <ul>
 *   <li> A set of {@link #split()} methods, to split the contents of the
 *        buffer on a delimiter
 *   <li> A {@link #delete(String) delete()} method that deletes the first
 *        occurrence of a substring. (<tt>StringBuffer</tt> only provides
 *        a <tt>delete()</tt> method that takes a starting and ending index.)
 *   <li> A {@link #replace(String,String) replace()} method that replaces
 *        the first occurrence of a substring. (<tt>StringBuffer</tt> only
 *        provides a <tt>replace()</tt> method that takes a starting and
 *        ending index.)
 *   <li> A {@link #replaceAll(String,String) replaceAll()} method to
 *        replace all occurrences of a substring with something else
 *   <li> Methods to encode and decode metacharacter sequences in place.
 *        (See {@link #encodeMetacharacters()} and
 *        {@link #decodeMetacharacters()}.)
 * </ul>
 *
 * @see java.lang.StringBuffer
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class XStringBuffer
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    /**
     * The underlying string buffer.
     */
    private StringBuffer buf = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct an empty <tt>XStringBuffer</tt> object with a default
     * initial capacity (the same initial capacity as an empty
     * <tt>StringBuffer</tt> object).
     */
    public XStringBuffer()
    {
        buf = new StringBuffer();
    }

    /**
     * Construct an empty <tt>XStringBuffer</tt> object with the specified
     * initial capacity.
     *
     * @param length  The initial capacity
     */
    public XStringBuffer (int length)
    {
        buf = new StringBuffer (length);
    }

    /**
     * Construct a <tt>XStringBuffer</tt> object so that it represents the
     * same sequence of characters as the <tt>String</tt> argument. (The
     * <tt>String</tt> contrents are copied into the <tt>XStringBuffer</tt>
     * object.)
     *
     * @param initialContents  The initial contents
     */
    public XStringBuffer (String initialContents)
    {
        if (initialContents == null)
            buf = new StringBuffer ();
        else
            buf = new StringBuffer (initialContents);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Append the string representation of a <tt>boolean</tt> value to
     * the buffer.
     *
     * @param val  The boolean value.
     */
    public void append (boolean val)
    {
        buf.append (val);
    }

    /**
     * Append the specified character to the buffer. If the buffer is already
     * at its maximum length, the character is silently ignored.
     *
     * @param c  The character to append.
     */
    public void append (char c)
    {
        buf.append (c);
    }

    /**
     * Append the specified array of characters to the buffer. This method
     * appends only as much of the string as will fit without causing the
     * buffer to exceed its maximum length.
     *
     * @param chars  The characters to append.
     */
    public void append (char chars[])
    {
        buf.append (chars);
    }

    /**
     * Append the specified characters in a character array to the buffer.
     * This method appends only as much of the string as will fit without
     * causing the buffer to exceed its maximum length.
     *
     * @param chars  The characters to append.
     * @param offset The index of the first character to append
     * @param len    The maximum number of characters to append
     */
    public void append (char chars[], int offset, int len)
    {
        buf.append (chars, offset, len);
    }

    /**
     * Append the string representation of a <tt>double</tt> value to
     * the buffer.
     *
     * @param val  The double value.
     */
    public void append (double val)
    {
        buf.append (val);
    }

    /**
     * Append the string representation of a <tt>float</tt> value to
     * the buffer.
     *
     * @param val  The float value.
     */
    public void append (float val)
    {
        buf.append (val);
    }

    /**
     * Append the string representation of a <tt>int</tt> value to
     * the buffer.
     *
     * @param val  The int value.
     */
    public void append (int val)
    {
        buf.append (val);
    }

    /**
     * Append the string representation of a <tt>long</tt> value to
     * the buffer.
     *
     * @param val  The long value.
     */
    public void append (long val)
    {
        buf.append (val);
    }

    /**
     * Append the string representation of an object to the buffer.
     *
     * @param obj  The object whose string value is to be appended.
     */
    public void append (Object obj)
    {
        buf.append (obj);
    }

    /**
     * Append the string representation of a <tt>short</tt> value to
     * the buffer.
     *
     * @param val  The short value.
     */
    public void append (short val)
    {
        buf.append (val);
    }

    /**
     * Append the specified string to the buffer. This method appends only
     * as much of the string as will fit without causing the buffer to
     * exceed its maximum length.
     *
     * @param s  The string to append.
     */
    public void append (String s)
    {
        buf.append (s);
    }

    /**
     * Return the current capacity of the buffer (i.e., the number of
     * characters left before truncation will occur).
     *
     * @return The capacity. A 0 means no more room is left, and all further
     *         inserted or appended characters will cause truncation.
     *
     * @see #length
     */
    public int capacity()
    {
        return buf.capacity();
    }

    /**
     * Return the character at a specified index in the buffer. Indexes begin
     * at 0. (i.e., The first character in the buffer has index 0.)
     *
     * @param index  The index of the character to return
     *
     * @return  The character at the specified index.
     *
     * @throws IndexOutOfBoundsException if <tt>index</tt> is negative or
     *                                   greater than or equal to
     *                                   <tt>length()</tt>
     */
    public char charAt (int index)
        throws IndexOutOfBoundsException
    {
        return buf.charAt (index);
    }

    /**
     * Removes all characters from the buffer leaving it empty.
     */
    public void clear ()
    {
        buf.delete (0, buf.length());
    }

    /**
     * Removes all existing characters from the buffer and loads the 
     * string into the buffer.
     *
     *  @param str <tt>String</tt> object to be loaded into the cleared buffer.
     */
    public void reset (String str)
    {
        clear();
        append (str);
    }

    /**
     * Remove the characters in a substring of this
     * <tt>XStringBuffer</tt>. The substring begins at the specified
     * <tt>start</tt> and extends to the character at index
     * <tt>end - 1</tt>, or to the end of the string, if no such
     * character exists. If <tt>start</tt> is equal to <tt>end</tt>,
     * no changes are made.
     *
     * @param start  The beginning index, inclusive
     * @param end    The ending index, exclusive
     *
     * @return This object
     *
     * @throws StringIndexOutOfBoundsException  if <tt>start</tt> is negative,
     *                                          greater than <tt>length()<tt>,
     *                                          or greater than <tt>end</tt>
     */
    public XStringBuffer delete (int start, int end)
        throws StringIndexOutOfBoundsException
    {
        buf.delete (start, end);

        return this;
    }

    /**
     * Delete the first occurrence of a given substring in the buffer.
     * with another substring.
     *
     * @param substring   The substring to find and replace
     *
     * @return <tt>true</tt> if the replacement succeeded,
     *         <tt>false</tt> otherwise.
     */
    public boolean delete (String substring)
    {
        int      i;
        boolean  deleted = false;

        i = buf.toString().indexOf (substring);
        if (i > -1)
        {
            buf.delete (i, i + substring.length());
            deleted = true;
        }

        return deleted;
    }

    /**
     * Remove the character at the specified position in this object,
     * shortening this object by one character.
     *
     * @param index  Index of the character to remove
     *
     * @return This object
     *
     * @throws StringIndexOutOfBoundsException if <tt>index</tt> is negative,
     *                                         or greater than or equal to
     *                                         <tt>length()<tt>.
     */
    public XStringBuffer deleteCharAt (int index)
        throws StringIndexOutOfBoundsException
    {
        buf.deleteCharAt (index);
        return this;
    }

    /**
     * <p>Replaces certain characters in the string buffer with Java
     * metacharacter ("backslash") sequences.</p>
     *
     * <ul>
     *    <li> A horizontal tab is replaced with <tt>\t</tt>.
     *    <li> A line feed is replaced with <tt>\n</tt>.
     *    <li> A carriage return is replaced with <tt>\r</tt>.
     *    <li> A form feed is replaced with <tt>\f</tt>.
     *    <li> A backslash is replaced by two backslashes.
     *    <li> Nonprintable characters are replaced with a
     *         <tt>&#92;u</tt><i>xxxx</i> sequence.
     * </ul>
     *
     * <p>This method uses a simple definition of "non-printable" that
     * doesn't take into account specific locales. A character is assumed
     * to be printable if (a) it's in the Basic Latin, Latin 1 Supplement,
     * or Extended Latin A Unicode block, and (b) its type, as returned by
     * <tt>java.lang.Character.getType()</tt> is one of:</p>
     *
     * <ul>
     *    <li><tt>Character.OTHER_PUNCTUATION</tt>
     *    <li><tt>Character.START_PUNCTUATION</tt>
     *    <li><tt>Character.END_PUNCTUATION</tt>
     *    <li><tt>Character.CONNECTOR_PUNCTUATION</tt>
     *    <li><tt>Character.CURRENCY_SYMBOL</tt>
     *    <li><tt>Character.MATH_SYMBOL</tt>
     *    <li><tt>Character.MODIFIER_SYMBOL</tt>
     *    <li><tt>Character.UPPERCASE_LETTER</tt>
     *    <li><tt>Character.LOWERCASE_LETTER</tt>
     *    <li><tt>Character.DECIMAL_DIGIT_NUMBER</tt>
     *    <li><tt>Character.SPACE_SEPARATOR</tt>
     *    <li><tt>Character.DASH_PUNCTUATION</tt>
     * </ul>
     *
     * <p>All other characters are assumed to be non-printable, even if
     * they could actually be printed in the current locale or on some
     * printer.</p>
     *
     * @param start  The beginning index, inclusive
     * @param end    The ending index, exclusive
     *
     * @throws StringIndexOutOfBoundsException if <tt>start</tt> is negative,
     *                                         or greater than
     *                                         <tt>length()<tt>, or greater
     *                                         than <tt>end<tt>
     *
     * @see #encodeMetacharacters()
     * @see #decodeMetacharacters(int, int)
     */
    public void encodeMetacharacters (int start, int end)
        throws StringIndexOutOfBoundsException
    {
        char          chars[] = buf.toString().toCharArray();
        int           i       = 0;
        StringBuffer  newBuf  = new StringBuffer();
        StringBuffer  scratch = new StringBuffer();

        try
        {
            while (i < start)
                newBuf.append (chars[i++]);

            while (i < end)
                newBuf.append (encodeOneMetacharacter (chars[i++], scratch));

            while (i < chars.length)
                newBuf.append (chars[i++]);
        }

        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new StringIndexOutOfBoundsException (String.valueOf (i));
        }

        buf = newBuf;
    }

    /**
     * A version of {@link #encodeMetacharacters(int,int) encodeMetacharacters}
     * that processes the entire string buffer. Calling this method is
     * equivalent to:
     *
     * <blockquote>
     * <pre>
     * buf.encodeMetacharacters (0, buf.length())
     * </pre>
     * </blockquote>
     *
     * @see #encodeMetacharacters(int, int)
     * @see #decodeMetacharacters()
     */
    public void encodeMetacharacters() 
    {
        try
        {
            encodeMetacharacters (0, this.length());
        }

        catch (StringIndexOutOfBoundsException ex)
        {
            // Should never happen
        }
    }

    /**
     * Replaces any metacharacter sequences in a portion of the string
     * buffer (such as those produced by {@link #encodeMetacharacters()}
     * with their actual characters.
     *
     * @param start  The beginning index, inclusive
     * @param end    The ending index, exclusive
     *
     * @throws StringIndexOutOfBoundsException if <tt>start</tt> is negative,
     *                                         or greater than
     *                                         <tt>length()<tt>, or greater
     *                                         than <tt>end<tt>
     *
     * @see #decodeMetacharacters()
     * @see #encodeMetacharacters(int,int)
     */
    public void decodeMetacharacters (int start, int end)
        throws StringIndexOutOfBoundsException
    {
        char          chars[] = buf.toString().toCharArray();
        int           i       = 0;
        StringBuffer  newBuf  = new StringBuffer();
        try
        {
            // Copy verbatim the region of characters prior to "start"

            while (i < start)
                newBuf.append (chars[i++]);

            // Process the region. First, allocate a PushbackReader than
            // can handle up to 5 characters (4 characters for a Unicode
            // code, plus the preceding "u").
            
            String         region = new String (chars, i, end - i);
            StringReader   sr     = new StringReader (region);
            PushbackReader pb     = new PushbackReader (sr, 5);

            // Now, process the region.

            for (;;)
            {
                int c;

                if ((c = pb.read()) == -1)
                    break;

                if (c == '\\')
                {
                    if ((c = pb.read()) == -1)
                    {
                        // Incomplete metacharacter sequence at end of
                        // region. Just pass along the backslash as is.

                        newBuf.append ((char) c);
                    }

                    else
                    {
                        c = decodeMetacharacter (c, pb);
                        if (c == -1)
                            break;

                        if (c == -2) // Bad unicode sequence
                            newBuf.append ('\\');
                        else
                            newBuf.append ((char) c);
                    }
                }

                else
                {
                    newBuf.append ((char) c);
                }
            }

            // Copy verbatim the region of characters after "end"

            i = end;
            while (i < chars.length)
                newBuf.append (chars[i++]);
        }

        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new StringIndexOutOfBoundsException (String.valueOf (i));
        }

        catch (IOException ex)
        {
            throw new StringIndexOutOfBoundsException();
        }

        buf = newBuf;
    }


    /**
     * A version of {@link #decodeMetacharacters(int,int) decodeMetacharacters}
     * that processes the entire string buffer. Calling this method is
     * equivalent to:
     *
     * <blockquote>
     * <pre>
     * buf.decodeMetacharacters (0, buf.length())
     * </pre>
     * </blockquote>
     *
     * @see #encodeMetacharacters(int, int)
     * @see #decodeMetacharacters()
     */
    public void decodeMetacharacters()
    {
        try
        {
            decodeMetacharacters (0, this.length());
        }

        catch (StringIndexOutOfBoundsException ex)
        {
            // Should never happen
        }
    }

    /**
     * Ensure that the capacity of the buffer is at least equal to the
     * specified minimum. If the current capacity of this string buffer is
     * less than the argument, then a new internal buffer is allocated with
     * greater capacity. The new capacity is the larger of:
     *
     * <ul type="disc">
     *   <li>The <tt>minimumCapacity</tt> argument
     *   <li>Twice the old capacity, plus 2.
     * </ul>
     *
     * If the <tt>minimumCapacity</tt> argument is non-positive, this
     * method returns without doing anything.
     *
     * @param minimumCapacity  The minimum desirec capacity
     */
    public void ensureCapacity (int minimumCapacity)
    {
        buf.ensureCapacity (minimumCapacity);
    }

    /**
     * Copy the some or all of the contents of the buffer into a character
     * array. The first character to be copied is at index
     * <tt>srcBegin</tt>; the last character to be copied is at index
     * (<tt>srcEnd - 1</tt>). The total number of characters to be
     * copied is (<tt>srcEnd - srcBegin</tt>). The characters are
     * copied into the subarray of <tt>dst</tt> starting at index
     * <tt>dstBegin</tt> and ending at index
     * <tt>(dstBegin + (srcEnd - srcBegin) - 1)</tt>.
     *
     * @param srcBegin  Start copy from this offset in the string buffer
     * @param srcEnd    Stop copy from this offset in the string buffer
     * @param dst       Where to copy the characters.
     * @param dstBegin  Offset into <tt>dst</tt>
     *
     * @throws StringIndexOutOfBoundsException invalid index
     */
    public void getChars (int   srcBegin,
                          int   srcEnd,
                          char  dst[],
                          int   dstBegin)
        throws StringIndexOutOfBoundsException
    {
        buf.getChars (srcBegin, srcEnd, dst, dstBegin);
    }

    /**
     * Insert the string representation of a <tt>boolean</tt>value into
     * the buffer at a specified position. Note that an insertion operation
     * may push characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param val    The <tt>boolean</tt> value
     */
    public void insert (int index, boolean val)
    {
        buf.insert (index, val);
    }

    /**
     * Insert a single character at a specified position in the buffer.
     * Note that an insertion operation may push * characters off the end
     * of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param ch     The character to insert.
     */
    public void insert (int index, char ch)
    {
        buf.insert (index, ch);
    }

    /**
     * Insert the contents of a character array into the buffer at a
     * specified position. Note that an insertion operation may push
     * characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param chars  The character array.
     */
    public void insert (int index, char chars[])
    {
        buf.insert (index, chars);
    }

    /**
     * Insert characters from a character array into the buffer at a
     * specified position. Note that an insertion operation may push
     * characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param chars  The character array.
     * @param offset The index of the first character to insert
     * @param len    The maximum number of characters to insert
     */
    public synchronized void insert (int  index,
                                     char chars[],
                                     int  offset,
                                     int  len)
    {
        buf.insert (index, chars, offset, len);
    }

    /**
     * Insert the string representation of a <tt>double</tt>value into
     * the buffer at a specified position. Note that an insertion operation
     * may push characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param val    The <tt>double</tt> value
     */
    public void insert (int index, double val)
    {
        buf.insert (index, val);
    }

    /**
     * Insert the string representation of a <tt>float</tt>value into
     * the buffer at a specified position. Note that an insertion operation
     * may push characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param val    The <tt>float</tt> value
     */
    public void insert (int index, float val)
    {
        buf.insert (index, val);
    }

    /**
     * Insert the string representation of a <tt>int</tt>value into
     * the buffer at a specified position. Note that an insertion operation
     * may push characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param val    The <tt>int</tt> value
     */
    public void insert (int index, int val)
    {
        buf.insert (index, val);
    }

    /**
     * Insert the string representation of a <tt>long</tt>value into
     * the buffer at a specified position. Note that an insertion operation
     * may push characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param val    The <tt>long</tt> value
     */
    public void insert (int index, long val)
    {
        buf.insert (index, val);
    }

    /**
     * Insert the string representation of a <tt>short</tt>value into
     * the buffer at a specified position. Note that an insertion operation
     * may push characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param val    The <tt>short</tt> value
     */
    public void insert (int index, short val)
    {
        buf.insert (index, val);
    }

    /**
     * Insert the string representation of an arbitrary object into the
     * buffer at a specified position. Note that an insertion operation may
     * push characters off the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param obj    The object whose string representation is to be inserted
     */
    public void insert (int index, Object obj)
    {
        buf.insert (index, obj);
    }

    /**
     * Insert the contents of a string into the buffer at a specified
     * position. Note that an insertion operation may push characters off
     * the end of the buffer.
     *
     * @param index  Where to start inserting in the string buffer
     * @param s      The string to insert
     */
    public void insert (int index, String s)
    {
        buf.insert (index, s);
    }

    /**
     * Return the number of characters currently in the buffer.
     *
     * @return The number of characters in the buffer.
     *
     * @see #capacity
     */
    public int length()
    {
        return buf.length();
    }

    /**
     * Replace the characters in a substring of this buffer with
     * characters in the specified <tt>String</tt>. The substring
     * begins at the specified <tt>start</tt> and extends to the
     * character at index <tt>end - 1</tt>, or to the end of the
     * <tt>XStringBuffer</tt> if no such character exists. First the
     * characters in the substring are removed and then the specified
     * <tt>String</tt> is inserted at <tt>start</tt>. (The
     * <tt>XStringBuffer</tt> will be lengthened to accommodate the
     * specified <tt>String</tt> if necessary.)
     *
     * @param start  The beginning index, inclusive
     * @param end    The ending index, exclusive
     * @param str    The string that will replace the previous contents
     *
     * @return This object
     *
     * @throws StringIndexOutOfBoundsException if <tt>start</tt> is negative,
     *                                         or greater than
     *                                         <tt>length()<tt>, or greater
     *                                         than <tt>end<tt>
     */
    public XStringBuffer replace (int start, int end, String str)
        throws StringIndexOutOfBoundsException
    {
        buf.replace (start, end, str);
        return this;
    }

    /**
     * Replace the first occurrence of a given substring in the buffer
     * with another substring.
     *
     * @param substring   The substring to find and replace
     * @param replacement The replacement string
     *
     * @return <tt>true</tt> if the replacement succeeded,
     *         <tt>false</tt> otherwise.
     */
    public boolean replace (String substring, String replacement)
    {
        int      i;
        boolean  replaced = false;

        i = buf.toString().indexOf (substring);
        if (i > -1)
        {
            buf.replace (i, i + substring.length(), replacement);
            replaced = true;
        }

        return replaced;
    }

    /**
     * Replace the first occurrence of a given substring in the buffer
     * with a given character
     *
     * @param substring   The substring to find and replace
     * @param replacement The replacement char
     *
     * @return <tt>true</tt> if the replacement succeeded,
     *         <tt>false</tt> otherwise.
     */
    public boolean replace (String substring, char replacement)
    {
        int      i;
        boolean  replaced = false;

        i = buf.toString().indexOf (substring);
        if (i > -1)
        {
            buf.delete (i, i+substring.length());
            buf.insert(i, replacement);
            replaced = true;
        }

        return replaced;
    }

    /**
     * Replace the all occurrences of a given substring in the buffer
     * with another substring. This method avoids recursion; that is, it's
     * safe even if the replacement string contains the source string.
     *
     * @param substring   The substring to find and replace
     * @param replacement The replacement string
     *
     * @return the number of replacements made
     */
    public int replaceAll (String substring, String replacement)
    {
        int  i;
        int  start;
        int  total = 0;

        start = 0;
        while ( (start < buf.length()) &&
                ((i = buf.toString().indexOf (substring, start)) >= 0) )
        {
            buf.replace (i, i + substring.length(), replacement);
            total++;
            start = i + replacement.length();
        }

        return total;
    }

    /**
     * Replace the all occurrences of a given character in the buffer
     * with another character.
     *
     * @param ch          The character to find and replace
     * @param replacement The replacement character
     *
     * @return the number of replacements made
     */
    public int replaceAll (char ch, char replacement)
    {
        int    len   = buf.length();
        char[] chars = new char[len];
        int    i;
        int    total = 0;

        buf.getChars (0, len, chars, 0);
        for (i = 0; i < len; i++)
        {
            if (chars[i] == ch)
            {
                chars[i] = replacement;
                total++;
            }
        }

        if (total > 0)
        {
            buf.setLength (0);
            buf.append (chars);
        }

        return total;
    }

    /**
     * Replace the all occurrences of a given character in the buffer
     * with string. This method avoids recursion; that is, it's
     * safe even if the replacement string contains the character being
     * replaced.
     *
     * @param ch          The character to find and replace
     * @param replacement The replacement string
     *
     * @return the number of replacements made
     */
    public int replaceAll (char ch, String replacement)
    {
        return replaceAll ("" + ch, replacement);
    }

    /**
     * Reverse the contents of the buffer.
     */
    public void reverse()
    {
        buf.reverse();
    }

    /**
     * Set the character at a specified index. The <tt>index</tt>
     * parameter must be greater than or equal to 0, and less than the
     * length of this string buffer.
     *
     * @param index  The index at which to set the value.
     * @param ch     The character to store
     *
     * @throws StringIndexOutOfBoundsException index out of range
     */
    public synchronized void setCharAt (int index, char ch)
        throws StringIndexOutOfBoundsException
    {
        buf.setCharAt (index, ch);
    }

    /**
     * Set the length of this <tt>XStringBuffer</tt>. This string
     * buffer is altered to represent a new character sequence whose length
     * is specified by the argument. If the <tt>newLength</tt> argument
     * is less than the current length of the string buffer, the string
     * buffer is truncated to contain exactly the number of characters
     * given by the <tt>newLength</tt> argument. If the
     * <tt>newLength</tt> argument is greater than the current length
     * of the string buffer, the buffer is padded with null characters out
     * to the new length.
     *
     * @param newLength   the new length of the buffer
     *
     * @throws IndexOutOfBoundsException  <tt>newLength</tt> is negative
     */
    public void setLength (int newLength) throws IndexOutOfBoundsException
    {
        buf.setLength (newLength);
    }

    /**
     * Split the contents of a buffer on white space, and return the
     * resulting strings. This method is a convenient front-end to
     * {@link TextUtil#split(String)}.
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String)
     * @see TextUtil#split(String,char)
     */
    public String[] split()
    {
        return TextUtil.split (this.toString());
    }

    /**
     * Split the contents of a buffer on a delimiter, and return the
     * resulting strings. This method is a convenient front-end to
     * {@link TextUtil#split(String,char)}.
     *
     * @param delim the delimiter
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(String)
     * @see TextUtil#split(String,char)
     */
    public String[] split (char delim)
    {
        return TextUtil.split (this.toString(), delim);
    }

    /**
     * Split the contents of a buffer on a delimiter, and return the
     * resulting strings. This method is a convenient front-end to
     * {@link TextUtil#split(String,String)}
     *
     * @param delimSet the delimiter set
     *
     * @return an array of <tt>String</tt> objects
     *
     * @see #split(char)
     * @see TextUtil#split(String,String)
     */
    public String[] split (String delimSet)
    {
        return TextUtil.split (this.toString(), delimSet);
    }

    /**
     * Split the contents of a buffer on a delimiter, and store the
     * resulting strings in a specified <tt>Collection</tt>. This method
     * is a convenient front-end for
     * {@link TextUtil#split(String,char,Collection)}.
     *
     * @param delim      the delimiter
     * @param collection where to store the resulting strings
     *
     * @return the number of strings added to the collection
     *
     * @see #split(String,Collection)
     * @see #split(char)
     * @see TextUtil#split(String,char)
     * @see TextUtil#split(String,String,Collection)
     */
    public int split (char delim, Collection collection)
    {
        return TextUtil.split (this.toString(), delim, collection);
    }

    /**
     * Split the contents of a buffer on a delimiter, and store the
     * resulting strings in a specified <tt>Collection</tt>. This method
     * is a convenient front-end for
     * {@link TextUtil#split(String,char,Collection)}.
     *
     * @param delimSet   the set of delimiters
     * @param collection where to store the resulting strings
     *
     * @return the number of strings added to the collection
     *
     * @see #split(char,Collection)
     * @see #split(String)
     * @see TextUtil#split(String,String)
     * @see TextUtil#split(String,char,Collection)
     */
    public int split (String delimSet, Collection collection)
    {
        return TextUtil.split (this.toString(), delimSet, collection);
    }

    /**
     * Return a new <tt>String</tt> that contains a subsequence of
     * characters currently contained in this buffer. The substring
     * begins at the specified index and extends to the end of the
     * StringBuffer.
     *
     * @param index  The beginning index, inclusive
     *
     * @return the substring
     *
     * @throws StringIndexOutOfBoundsException index out of range
     */
    public String substring (int index)
        throws StringIndexOutOfBoundsException
    {
        return buf.substring (index);
    }

    /**
     * Return a new <tt>String</tt> that contains a subsequence of
     * characters currently contained in this buffer. The substring begins
     * at the specified <tt>start</tt> and extends to the character at
     * index <tt>end - 1</tt>.
     *
     * @param start  The beginning index, inclusive
     * @param end    The beginning index, exclusive
     *
     * @return the substring
     *
     * @throws StringIndexOutOfBoundsException if <tt>start</tt> is negative,
     *                                         greater than <tt>length()<tt>,
     *                                         or greater than <tt>end</tt>
     */
    public String substring (int start, int end)
        throws StringIndexOutOfBoundsException
    {
        return buf.substring (start, end);
    }

    /**
     * Return the <tt>String</tt> representation of this buffer.
     *
     * @return The string.
     */
    public String toString()
    {
        return buf.toString();
    }

    /**
     * Return a standard <tt>StringBuffer</tt> containing a copy of the
     * contents of this buffer.
     *
     * @return The string buffer
     */
    public StringBuffer toStringBuffer()
    {
        return new StringBuffer (buf.toString());
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Escape a specific character, if it's non-printable. See the
     * documentation for encodeMetacharacters() for the definition of
     * non-printable.
     *
     * @param c    the character to escape
     * @param buf  a scratch buffer, to avoid allocating a new one on each call
     *
     * @return the escape string (which might contain only the character passed
     *         in, if the character is printable)
     */
    private static String encodeOneMetacharacter (char c, StringBuffer buf)
    {
        Character.UnicodeBlock  ublock;
        StringBuffer            result = new StringBuffer();

        ublock = Character.UnicodeBlock.of (c);
        if ((ublock == Character.UnicodeBlock.BASIC_LATIN) ||
            (ublock == Character.UnicodeBlock.LATIN_1_SUPPLEMENT) ||
            (ublock == Character.UnicodeBlock.LATIN_EXTENDED_A))
        {
            int type = Character.getType (c);

            switch (type)
            {
                case Character.OTHER_PUNCTUATION:
                case Character.START_PUNCTUATION:
                case Character.END_PUNCTUATION:
                    if (c == '\\')
                        result.append ("\\\\");
                    else
                        result.append (c);
                    break;

                case Character.CONNECTOR_PUNCTUATION:
                case Character.CURRENCY_SYMBOL:
                case Character.MATH_SYMBOL:
                case Character.MODIFIER_SYMBOL:
                case Character.UPPERCASE_LETTER:
                case Character.LOWERCASE_LETTER:
                case Character.DECIMAL_DIGIT_NUMBER:
                case Character.DASH_PUNCTUATION:
                case Character.SPACE_SEPARATOR:
                    result.append (c);
                    break;

                default:
                    switch (c)
                    {
                        case '\r':
                            result.append ("\\r");
                            break;

                        case '\n':
                            result.append ("\\n");
                            break;

                        case '\t':
                            result.append ("\\t");
                            break;

                        case '\f':
                            result.append ("\\f");
                            break;

                        default:
                            result.append (toUnicodeEscape (c, buf));
                    }
            }
        }

        return result.toString();
    }

    /**
     * Convert a character to a Unicode escape string.
     *
     * @param c    the character to escape
     * @param buf  a scratch buffer, to avoid allocating a new one on each call
     *
     * @return the Unicode escape string
     */
    private static String toUnicodeEscape (char c, StringBuffer buf)
    {
        buf.setLength (0);

        buf.append (Integer.toHexString ((int) c));

        while (buf.length() < 4)
            buf.insert (0, "0");

        buf.insert (0, "\\u");
        return buf.toString();
    }

    /**
     * Decode a metacharacter sequence.
     *
     * @param c   the character after the backslash
     * @param pb  a PushbackReader representing the remainder of the region
     *            being processed (necessary for Unicode sequences)
     *
     * @return the decoded metacharacter, -1 on EOF, -2 for unknown or
     *         bad sequence
     *
     * @throws IOException read error
     */
    private int decodeMetacharacter (int c, PushbackReader pb)
        throws IOException
    {
        switch (c)
        {
            case 't':
                c = '\t';
                break;

            case 'n':
                c = '\n';
                break;

            case 'r':
                c = '\r';
                break;

            case '\\':
                c = '\\';
                break;

            case 'u':
                c = decodeUnicodeSequence (pb);
                if (c == -2)
                {
                    pb.unread ('u');
                    c = '\\';
                }
                break;

            default:
                // An escaped "regular" character is just the character.
                break;
        }

        return c;
    }

    /**
     * Parse the next four characters and attempt to decode them as a Unicode
     * character code.
     *
     * @param pb  a PushbackReader representing the remainder of the region
     *            being processed (necessary for Unicode sequences)
     *
     * @return the decoded character, -1 on EOF, -2 for a bad Unicode sequence.
     *         If -2 is returned, the 4-character Unicode code is pushed
     *         back on the input stream. (The leading backslash and "u" are
     *         not pushed back, however).
     *
     * @throws IOException  on error
     */
    private int decodeUnicodeSequence (PushbackReader pb)
        throws IOException
    {
        int      c          = -1;
        boolean  incomplete = false;

        synchronized (buf)
        {
            buf.setLength (0);

            // Read four characters, each of which represents a single
            // hex digit.

            for (int i = 0; i < 4; i++)
            {
                if ( (c = pb.read()) == -1 )
                {
                    // Incomplete Unicode escape sequence at EOF. Just
                    // swallow it.

                    incomplete = true;
                    break;
                }

                buf.append ((char) c);
            }

            if (incomplete)
            {
                // Push the entire buffered sequence back onto the input
                // stream.

                unread (buf.toString(), pb);
            }

            else
            {
                int      code = 0;
                boolean  error = false;

                try
                {
                    code = Integer.parseInt (buf.toString(), 16);
                    if (code < 0)
                        throw new NumberFormatException();
                }

                catch (NumberFormatException ex)
                {
                    // Bad hexadecimal value in Unicode escape sequence.
                    // Push it all back.

                    unread (buf.toString(), pb);
                    error = true;
                }

                if (! Character.isDefined ((char) code))
                {
                    // Invalid Unicode character. Push it all back.

                    unread (buf.toString(), pb);
                    error = true;
                }

                c = (error ? -2 : ((char) code));
            }
        }

        return c;
    }

    /**
     * Push a string back on the input stream.
     *
     * @param s  the string
     * @param pb the PushbackReader onto which to push the characters
     *
     * @throws IOException if an I/O error occurs or if the pushback buffer is
     *                     full
     */
    private void unread (String s, PushbackReader pb)
        throws IOException
    {
        for (int i = s.length() - 1; i >= 0; i--)
            pb.unread (s.charAt (i));
    }
}
