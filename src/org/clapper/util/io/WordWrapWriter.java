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

package org.clapper.util.io;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * <p>The <tt>WordWrapWriter</tt> class is a filter class. A
 * <WordWrapWriter</tt> object wraps a <tt>Writer</tt> or
 * <tt>OutputStream</tt> object, filtering output to the wrapped object so
 * that output lines are broken on word boundaries and fit nicely within
 * the proscribed output width. Messages may be written with prefixes or
 * without them, depending upon various settings in the
 * <tt>WordWrapWriter</tt> object; the columnar width of the output object
 * can be controlled, as well.</p>
 *
 * <p>For example, the long message</p>
 *
 * <blockquote><pre>Unable to open file /usr/local/etc/wombat: No such file or directory</pre></blockquote>
 *
 * <p>might appear like this without a prefix:</p>
 *
 * <blockquote><pre>
 * Unable to open file /usr/local/etc/wombat: No such file or
 * directory
 * </pre></blockquote>
 *
 * <p>and like this if the prefix is "myprog:"</p>
 *
 * <blockquote><pre>
 * myprog: Unable to open file /usr/local/etc/wombat: No such
 *         file or directory
 * </pre></blockquote>
 *
 * <p>Alternatively, if the output width is shortened, the same message
 * can be made to wrap something like this:</p>
 *
 * <blockquote><pre>
 * myprog: Unable to open file
 *         /usr/local/etc/wombat:
 *         No such file or
 *         directory
 * </pre></blockquote>
 *
 * <p>Note how the <tt>WordWrapWriter</tt> output's logic will "tab" past the
 * prefix on wrapped lines.</p>
 *
 * <p><tt>WordWrapWriter</tt> objects also support the notion of an
 * indentation level, which is independent of the prefix. A non-zero
 * indentation level causes each line, including the first line, to be
 * indented that many characters. Thus, calling
 * {@link #setIndentation(int) setIndentation()} with a value of
 * 4 will cause each output line to be preceded by 4 blanks. (It's also
 * possible to change the indentation character from a blank to any other
 * character. See {@link #setIndentationChar(char) setIndentationChar()}
 * for details.)</p>
 *
 * <p>The logic is predicated on the notion of a "message"; that is, a
 * <tt>WordWrapWriter</tt> object has to know where a message begins and
 * ends, so it can tell when to write the prefix, reset its internal column
 * count, etc. The class's notion of a message is straightforward: A
 * message consists of all characters written to the object via one of the
 * <tt>write()</tt> methods, up to and including either an embedded newline
 * or a call to the {@link #flush()} method. The <tt>println()</tt> methods
 * implicitly call <tt>flush()</tt>, as does any <tt>write()</tt> method
 * that encounters an embedded newline. Thus, it's rarely necessary to call
 * <tt>flush()</tt> manually.</p>
 *
 * <p><b>Notes</b></b>
 *
 * <ol>
 *   <li> The class does not do any special processing of tab characters.
 *        Embedded tab characters can have surprising (and unwanted) effects
 *        on the rendered output.
 *   <li> Wrapping another <tt>WordWrapWriter</tt> object is an invitation to
 *        trouble.
 *   <li> Technically, this class probably ought to extend the
 *        <tt>java.io.FilterWriter</tt> class, since performs filtering
 *        of output written to another underlying <tt>Writer</tt> or
 *        <tt>OutputStream</tt>. However, I wanted <tt>WordWrapWriter</tt>
 *        to be polymorphically compatible with <tt>java.io.PrintWriter</tt>,
 *        so it could be passed to methods expecting a <tt>PrintWriter</tt>
 *        object; there are more methods that expect a <tt>PrintWriter</tt>
 *        than there are methods that expect a <tt>FilterWriter</tt>.
 * </ol>
 *
 * @see java.io.Writer
 * @see java.io.PrintWriter
 *
 * @version $Revision$
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class WordWrapWriter extends PrintWriter
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * The default line length.
     */
    public static final int DEFAULT_LINE_LENGTH = 79;

    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    /**
     * Character that denotes an instream newline. Since we're inserting
     * this character ourselves, in response to a println() call, it
     * doesn't really matter what we use, as long as it doesn't match
     * something else we *do* care about.
     */
    private static final char NEWLINE_MARKER = '\n';

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * Where the output is really going.
     */
    private PrintWriter writer = null;

    /**
     * The line length to use.
     */
    private int lineLength = DEFAULT_LINE_LENGTH;

    /**
     * Current string being assembled. <code>flush</code> clears this
     * buffer.
     */
    private StringBuffer buffer = null;

    /**
     * How many spaces to indent subsequent lines after the first line
     * wraps. Defaults to 0, or no indentation.
     */
    private int indentation = 0;

    /**
     * The indentation character to use.
     */
    private char indentChar = ' ';

    /**
     * Prefix to use. Indentation is tabbed past this prefix.
     */
    private String prefix = null;

    /**
     * Whether or not we've already emitted the prefix. A given prefix is
     * only emitted once, no matter how many lines are written/wrapped. If
     * the user changes the prefix, then we start fresh.
     */
    private boolean emittedPrefix = false;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Build an <code>WordWrapWriter</code> object that will write its
     * output to the specified <code>Writer</code> object, using the
     * default line length of 79.
     *
     * @param output      Where the output goes.
     *
     * @see #DEFAULT_LINE_LENGTH
     * @see java.io.Writer
     */
    public WordWrapWriter (Writer output)
    {
        this (output, DEFAULT_LINE_LENGTH);
    }

    /**
     * Build an <code>WordWrapWriter</code> object that will write its
     * output to the specified <code>OutputStream</code> object, using the
     * default line length of 79.
     *
     * @param output      Where the output goes.
     *
     * @see #DEFAULT_LINE_LENGTH
     * @see java.io.OutputStream
     */
    public WordWrapWriter (OutputStream output)
    {
        this (output, DEFAULT_LINE_LENGTH);
    }

    /**
     * Build an <code>WordWrapWriter</code> object that will write its
     * output to the specified <code>Writer</code> object, using the
     * specified line length.
     *
     * @param output      Where the output goes.
     * @param lineLength  The desired line length.
     *
     * @see #DEFAULT_LINE_LENGTH
     * @see java.io.Writer
     */
    public WordWrapWriter (Writer output, int lineLength)
    {
        this (output, lineLength, 0);
    } 

    /**
     * Build an <code>WordWrapWriter</code> object that will write its
     * output to the specified <code>OutputStream</code> object, using the
     * specified line length.
     *
     * @param output      Where the output goes.
     * @param lineLength  The desired line length.
     *
     * @see #DEFAULT_LINE_LENGTH
     * @see java.io.Writer
     */
    public WordWrapWriter (OutputStream output, int lineLength)
    {
        this (output, lineLength, 0);
    } 

    /**
     * Build an <code>WordWrapWriter</code> object that will write its
     * output to the specified <code>Writer</code> object, using the
     * specified line length. In addition, wrapped lines will be indented
     * by the indicated number of spaces.
     *
     * @param output       Where the output goes.
     * @param lineLength   The desired line length.
     * @param indentSpaces How many blanks to indent lines that are wrapped.
     *
     * @see #DEFAULT_LINE_LENGTH
     * @see #setIndentationChar
     * @see java.io.Writer
     */
    public WordWrapWriter (Writer output, int lineLength, int indentSpaces)
    {
        super (output);
        writer = new PrintWriter (output);
        setLineLength (lineLength);
        setIndentation (indentSpaces);
    } 

    /**
     * Build an <code>WordWrapWriter</code> object that will write its
     * output to the specified <code>OutputStream</code> object, using the
     * specified line length. In addition, wrapped lines will be indented
     * by the indicated number of spaces.
     *
     * @param output       Where the output goes.
     * @param lineLength   The desired line length.
     * @param indentSpaces How many blanks to indent lines that are wrapped.
     *
     * @see #DEFAULT_LINE_LENGTH
     * @see #setIndentationChar
     * @see java.io.Writer
     */
    public WordWrapWriter (OutputStream output,
                           int          lineLength,
                           int          indentSpaces)
    {
        this (new OutputStreamWriter (output), lineLength, indentSpaces);
    } 

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Flush the stream and check its error state.
     */
    public boolean checkError()
    {
        return super.checkError();
    }

    /**
     * Close the stream, flushing it first. Closing a previously-closed
     * stream has no effect.
     */
    public void close()
    {
        super.close();
    }

    /**
     * Flush the stream. If the stream has saved any characters from the
     * various write() methods in a buffer, write them immediately to their
     * intended destination. Then, if that destination is another character
     * or byte stream, flush it. Thus one flush() invocation will flush all
     * the buffers in a chain of Writers and OutputStreams.
     */
    public synchronized void flush()
    {
        if ( (buffer != null) && (buffer.length() > 0) )
        {
            flushBuffer (buffer);
            buffer = null;
        }
    }

    /**
     * Retrieve the current indentation setting for wrapped lines.
     *
     * @return The indentation setting, a count that indicates how many
     *         leading spaces will be emitted on wrapped lines.
     */
    public int getIndentation()
    {
        return indentation;
    }

    /**
     * Get the current indentation character. By default, the indentation
     * character is a blank.
     *
     * @return   The current indentation character.
     *
     * @see #setIndentationChar
     */
    public char getIndentationChar()
    {
        return indentChar;
    }

    /**
     * Get the current prefix. The prefix is the string that's displayed before
     * the first line of output; subsequent wrapped lines are tabbed past the
     * prefix.
     *
     * @return the current prefix, or null if there isn't one.
     *
     * @see #setPrefix
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Retrieve the current line length.
     *
     * @return The line length.
     */
    public int getLineLength()
    {
        return lineLength;
    }

    /**
     * Set the indentation value for wrapped lines.
     *
     * @param newIndentation The indentation setting, a count that
     *                       indicates how many leading spaces will be emitted
     *                       on wrapped lines. A value of 0 disables the
     *                       feature.
     *
     * @throws IndexOutOfBoundsException the value is negative
     */
    public void setIndentation (int newIndentation)
    {
        if (newIndentation < 0)
        {
            throw new IndexOutOfBoundsException ( "Indentation of "
                                                + newIndentation
                                                + " is negative.");
        }

        indentation = newIndentation;
    }

    /**
     * Change the indentation character. By default, the indentation
     * character is a blank.
     *
     * @param c  The new indentation character.
     *
     * @see #getIndentationChar
     */
    public void setIndentationChar (char c)
    {
        indentChar = c;
    }

    /**
     * Set the line length.
     *
     * @param newLineLength The new line length to use. A value of 0
     *                      disables wrapping.
     *
     * @throws IndexOutOfBoundsException the value is negative
     */
    public void setLineLength (int newLineLength)
        throws IndexOutOfBoundsException
    {
        if (newLineLength < 0)
        {
            throw new IndexOutOfBoundsException ( "Line length of "
                                                + newLineLength
                                                + " is negative.");
        }

        lineLength = newLineLength;
    }

    /**
     * Set the current prefix. The prefix is a string that's displayed before
     * the first line of output; subsequent wrapped lines are tabbed past the
     * prefix. For instance, suppose the prefix is set to "foo: ". A message
     * emitted while "foo: " is the prefix might look like this:
     *
     * <blockquote><pre>
     * foo: Unable to access the frammistat: No
     *      such file or device.
     * </pre></blockquote>
     *
     * @param newPrefix  The new prefix to be set
     *
     * @return the old prefix, or null if there isn't one.
     *
     * @see #getPrefix
     */
    public String setPrefix (String newPrefix)
    {
        String result = prefix;

        prefix        = newPrefix;
        emittedPrefix = false;

        return result;
    }

    /**
     * Print a boolean.
     *
     * @param b  The boolean to print
     */
    public void print (boolean b)
    {
        Boolean B = new Boolean (b);

        write (B.toString());
    }

    /**
     * Print a character.
     *
     * @param c  The character to print
     */
    public void print (char c)
    {
        write (c);
    }

    /**
     * Print an array of characters.
     *
     * @param s  The array of characters to print
     */
    public void print (char s[])
    {
        write (s, 0, s.length);
    }

    /**
     * Print a double.
     *
     * @param d  The double floating point number to print
     */
    public void print (double d)
    {
        Double D = new Double (d);

        write (D.toString());
    }

    /**
     * Print a float.
     *
     * @param f  The floating point number to print
     */
    public void print (float f)
    {
        Float F = new Float (f);

        write (F.toString());
    }

    /**
     * Print an integer.
     *
     * @param i  The integer to print
     */
    public void print (int i)
    {
        Integer I = new Integer (i);

        write (I.toString());
    }

    /**
     * Print a long.
     *
     * @param l  The long to print
     */
    public void print (long l)
    {
        Long L = new Long (l);

        write (L.toString());
    }

    /**
     * Print a short.
     *
     * @param s  The short to print
     */
    public void print (short s)
    {
        Short S = new Short (s);

        write (S.toString());
    }

    /**
     * Print a String.
     *
     * @param s  The String to print.
     */
    public void print (String s)
    {
        write (s);
    }

    /**
     * Print an Object.
     *
     * @param x The object to print.
     */
    public void print (Object x)
    {
        write (x.toString());
    }

    /**
     * End the current line.
     */
    public void println()
    {
        write (NEWLINE_MARKER);
    }

    /**
     * Print a boolean and finish the line.
     *
     * @param b  The boolean to print
     */
    public void println (boolean b)
    {
        Boolean B = new Boolean (b);

        println (B.toString());
    }

    /**
     * Print a character and finish the line.
     *
     * @param c  The character to print
     */
    public void println (char c)
    {
        println (c);
    }

    /**
     * Print an array of characters.
     *
     * @param s  The array of characters to print
     */
    public void println (char s[])
    {
        for (int i = 0; i < s.length; i++)
            print (s[i]);
        println();
    }

    /**
     * Print a double and finish the line.
     *
     * @param d  The double floating point number to print
     */
    public void println (double d)
    {
        Double D = new Double (d);

        println (D.toString());
    }

    /**
     * Print a float and finish the line.
     *
     * @param f  The floating point number to print
     */
    public void println (float f)
    {
        Float F = new Float (f);

        println (F.toString());
    }

    /**
     * Print an integer.
     *
     * @param i  The integer to print
     */
    public void println (int i)
    {
        Integer I = new Integer (i);

        println (I.toString());
    }

    /**
     * Print a long and finish the line.
     *
     * @param l  The long to print
     */
    public void println (long l)
    {
        Long L = new Long (l);

        println (L.toString());
    }

    /**
     * Print a short and finish the line.
     *
     * @param s  The short to print
     */
    public void println (short s)
    {
        Short S = new Short (s);

        println (S.toString());
    }

    /**
     * Print a String and finish the line.
     *
     * @param s  The String to print.
     */
    public void println (String s)
    {
        print (s);
        println();
    }

    /**
     * Print an Object and finish the line.
     *
     * @param x The object to print.
     */
    public void println (Object x)
    {
        println (x.toString());
    }

    /**
     * Write a single character. This method is called by all other
     * <tt>write()</tt>, <tt>print()</tt> and <tt>println()</tt> methods.
     * Thus, subclasses that wish to preprocess (or intercept) the output
     * only have to override this method.
     *
     * @param c Character to write
     */
    public synchronized void write (int c)
    {
        if (buffer == null)
            buffer = new StringBuffer (lineLength * 2);

        buffer.append ((char) c);
        if (c == NEWLINE_MARKER)
            flush();
    }

    /**
     * Write a portion of an array of characters to the underlying
     * output object. Assumes the characters represent the start of
     * a new line. Each line is indented according to this object's
     * defined indentation level.
     *
     * @param cbuf Array of characters 
     * @param off  Offset from which to start writing characters 
     * @param len  Number of characters to write
     */
    public void write (char cbuf[], int off, int len)
    {
        for (; (off < cbuf.length) && (len > 0); len--, off++)
            write (cbuf[off]);
    }

    /**
     * Write a portion of a String of characters to the underlying
     * output object. Assumes the characters represent the start of
     * a new line. Each line is indented according to this object's
     * defined indentation level.
     *
     * @param s    String from which to write
     * @param off  Offset from which to start writing characters 
     * @param len  Number of characters to write
     */
    public void write (String s, int off, int len)
    {
        char[] cbuf = s.toCharArray();

        this.write (cbuf, off, len);
    }

    /**
     * Write a string.
     *
     * @param s String to write
     */
    public void write (String s)
    {
        char[] cbuf = s.toCharArray();

        this.write (cbuf, 0, cbuf.length);
    }
    
    /**
     * Write an array of characters.
     *
     * @param cbuf Array of characters to write
     */
    public void write (char[] cbuf)
    {
        this.write (cbuf, 0, cbuf.length);
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Handles the details of flushing specified output buffer to the
     * output stream.
     *
     * @param buf The buffer
     */
    private void flushBuffer (StringBuffer buf)
    {
        int           currentLength = 0;
        int           length        = buf.length();
        int           tokenLength;
        char          contents[];
        StringBuffer  currentWord   = new StringBuffer();
        boolean       lastWasNewline= true;
        int           lines = 0;
        int           tokensOnLine  = 0;

        if (prefix != null)
        {
            // Emit the prefix, unless the line is empty.
            if ( (length > 1) || (buf.charAt(0) != NEWLINE_MARKER) )
            {
                // Non empty line. We want the prefix.

                if (emittedPrefix)
                {
                    for (int i = 0; i < prefix.length(); i++)
                        writer.print (' ');
                }

                else
                {
                    writer.print (prefix);
                }

                // We've emitted a prefix, which by definition is at the
                // beginning of a line. Pretend we've emitted a newline.

                currentLength += prefix.length();
                lastWasNewline = false;
                emittedPrefix = true;
            }
        }

        contents = buf.toString().toCharArray();
        for (int i = 0; i < length; i++)
        {
            char c = contents[i];
            if ( (c == NEWLINE_MARKER) || (Character.isSpaceChar (c)))
            {
                // Words are delimited by white space, so if there's a
                // current word, we have to emit it at this point, before
                // handling the white space character.
                    
                if ( (tokenLength = currentWord.length()) > 0 )
                {
                    // We have a word buffered up. Emit it.

                    if ( ((currentLength + tokenLength) > lineLength) &&
                         (tokensOnLine > 0) )
                    {
                        // Token would overflow the line AND we're not at
                        // the beginning of a new line. Wrap first. (If we
                        // were at the beginning of a new line, forcing a
                        // "wrap" would introduce a spurious newline.)

                        writer.println();
                        lines++;
                        lastWasNewline = true;
                        currentLength = 0;
                        tokensOnLine = 0;
                    }

                    if (lastWasNewline && (lines > 0))
                        currentLength += indent();

                    writer.print (currentWord.toString());
                    lastWasNewline = false;
                    currentLength += tokenLength;
                    currentWord.setLength (0);
                    tokensOnLine++;
                }

                // Now emit the white space character, wrapping the line if
                // necessary first.

                if ((c == NEWLINE_MARKER) || (currentLength >= lineLength))
                {
                    writer.println();
                    lines++;
                    currentLength = 0;
                    tokensOnLine = 0;
                    lastWasNewline = true;
                }

                if (c != NEWLINE_MARKER)
                {
                    if (lastWasNewline)
                        currentLength += indent();
                    else
                    {
                        writer.print (c);
                        currentLength++;
                    }

                    lastWasNewline = false;
                }
            }

            else
            {
                // Not a white space character. Add it to the current word.

                currentWord.append (c);
            }
        } // end for

        if ( (tokenLength = currentWord.length()) > 0 )
        {
            // Leftover word. Put it out, too.

            if ( (! lastWasNewline) &&
                 ((currentLength + tokenLength) > lineLength) )
            {
                writer.println();
                lines++;
                lastWasNewline = true;
                tokensOnLine = 0;
            }

            if (lastWasNewline)
                currentLength += indent();

            writer.print (currentWord.toString());
            lastWasNewline = false;
        }

        // Issue a final newline and flush the output stream.

        if (! lastWasNewline)
            writer.println();

        writer.flush();
    }

    /**
     * Emit indentation to the current output object.
     *
     * @return Number of characters emitted.
     */
    private int indent()
    {
        int result = indentation;
        int i;

        if (prefix != null)
        {
            for (i = 0; i < prefix.length(); i++)
                writer.write (indentChar);
            result += prefix.length();
        }

        for (i = 0; i < indentation; i++)
            writer.write (indentChar);

        return result;
    }
}
