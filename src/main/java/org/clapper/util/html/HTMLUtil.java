/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2007 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M. Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
\*---------------------------------------------------------------------------*/

package org.clapper.util.html;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.clapper.util.text.TextUtil;
import org.clapper.util.text.Unicode;
import org.clapper.util.text.XStringBuffer;
import org.clapper.util.text.XStringBuilder;

/**
 * Static class containing miscellaneous HTML-related utility methods.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public final class HTMLUtil
{
    /*----------------------------------------------------------------------*\
                            Private Constants
    \*----------------------------------------------------------------------*/

    /**
     * Resource bundle containing the character entity code mappings.
     */
    private static final String BUNDLE_NAME = "org.clapper.util.html.HTMLUtil";

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private static ResourceBundle resourceBundle = null;

    /**
     * For regular expression substitution. Instantiated first time it's
     * needed.
     */
    private static Pattern entityPattern = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private HTMLUtil()
    {
        // Can't be instantiated
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Removes all HTML element tags from a string, leaving just the character
     * data. This method does <b>not</b> touch any inline HTML character
     * entity codes. Use
     * {@link #convertCharacterEntities convertCharacterEntities()}
     * to convert HTML character entity codes.
     *
     * @param s  the string to adjust
     *
     * @return the resulting, possibly modified, string
     *
     * @see #convertCharacterEntities
     */
    public static String stripHTMLTags (String s)
    {
        char[]         ch = s.toCharArray();
        boolean        inElement = false;
        XStringBuilder buf = new XStringBuilder();

        for (int i = 0; i < ch.length; i++)
        {
            switch (ch[i])
            {
                case '<':
                    inElement = true;
                    break;

                case '>':
                    if (inElement)
                        inElement = false;
                    else
                        buf.append (ch[i]);
                    break;

                default:
                    if (! inElement)
                        buf.append (ch[i]);
                    break;
            }
        }

        return buf.toString();
    }

    /**
     * Escape characters that are special in HTML, so that the resulting
     * string can be included in HTML (or XML). For instance, this method
     * will convert an embedded "&amp;" to "&amp;amp;".
     *
     * @param s  the string to convert
     *
     * @return the converted string
     */
    public static String escapeHTML(String s)
    {
        StringBuilder buf = new StringBuilder();

        for (char c : s.toCharArray())
        {
            switch (c)
            {
                case '&':
                    buf.append("&amp;");
                    break;
                case '<':
                    buf.append("&lt;");
                    break;
                case '>':
                    buf.append("&gt;");
                    break;
                default:
                    buf.append(c);
            }
        }

        return buf.toString();
    }

    /**
     * Converts all inline HTML character entities (c.f.,
     * <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">http://www.w3.org/TR/REC-html40/sgml/entities.html</a>)
     * to their Unicode character counterparts, if possible.
     *
     * @param s the string to convert
     *
     * @return the resulting, possibly modified, string
     *
     * @see #stripHTMLTags
     * @see #makeCharacterEntities
     */
    public static String convertCharacterEntities(String s)
    {
        // The resource bundle contains the mappings for symbolic entity
        // names like "amp". Note: Must protect matching and MatchResult in
        // a critical section, for thread-safety. See javadocs for
        // Perl5Util.

        synchronized (HTMLUtil.class)
        {
            try
            {
                if (entityPattern == null)
                    entityPattern = Pattern.compile ("&(#?[^;\\s&]+);?");
            }

            catch (PatternSyntaxException ex)
            {
                // Should not happen unless I've screwed up the pattern.
                // Throw a runtime error.

                assert (false);
            }
        }

        XStringBuffer buf = new XStringBuffer();
        Matcher matcher = null;

        synchronized (HTMLUtil.class)
        {
            matcher = entityPattern.matcher (s);
        }

        for (;;)
        {
            String match = null;
            String preMatch = null;
            String postMatch = null;
            if (! matcher.find())
                break;

            match = matcher.group(1);
            preMatch = s.substring (0, matcher.start (1) - 1);

            if (preMatch != null)
                buf.append(preMatch);

            if (s.charAt(matcher.end() - 1) != ';')
            {
                // Not a well-formed entity. Copy into the buffer.
                buf.append(s.substring(matcher.start(), matcher.end()));
                postMatch = s.substring(matcher.end(1)); 
            }

            else
            {
                // Well-formed entity.
                postMatch = s.substring(matcher.end(1) + 1); 
                buf.append(convertEntity(match));
            }

            if (postMatch == null)
                break;

            s = postMatch;
            matcher.reset (s);
        }

        if (s.length() > 0)
            buf.append (s);

        return buf.toString();
    }

    /**
     * Converts appropriate Unicode characters to their HTML character entity
     * counterparts (c.f.,
     * <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">http://www.w3.org/TR/REC-html40/sgml/entities.html</a>).
     *
     * @param s the string to convert
     *
     * @return the resulting, possibly modified, string
     *
     * @see #stripHTMLTags
     *
     * @see #convertCharacterEntities
     */
    public static String makeCharacterEntities (String s)
    {
        // First, make a character-to-entity-name map from the resource bundle.

        ResourceBundle bundle = getResourceBundle();
        Map<Character,String> charToEntityName =
            new HashMap<Character,String>();
        Enumeration<String> keys = bundle.getKeys();
        XStringBuffer buf = new XStringBuffer();

        while (keys.hasMoreElements())
        {
            String key = keys.nextElement();
            String sChar = bundle.getString (key);
            char c = sChar.charAt (0);

            // Transform the bundle key into an entity name by removing the
            // "html_" prefix.

            buf.clear();
            buf.append (key);
            buf.delete ("html_");

            charToEntityName.put (c, buf.toString());
        }

        char[] chars = s.toCharArray();
        buf.clear();

        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];

            String entity = charToEntityName.get (c);
            if (entity == null)
            {
                if (! TextUtil.isPrintable(c))
                {
                    buf.append("&#");
                    buf.append(Integer.valueOf(c));
                    buf.append(';');
                }
                else
                {
                    buf.append(c);
                }
            }

            else
            {
                buf.append ('&');
                buf.append(entity);
                buf.append(';');
            }
        }

        return buf.toString();
    }

    /**
     * Convenience method to convert embedded HTML to text. This method:
     *
     * <ul>
     *   <li> Strips embedded HTML tags via a call to
     *        {@link #stripHTMLTags #stripHTMLTags()}
     *   <li> Uses {@link #convertCharacterEntities convertCharacterEntities()}
     *        to convert HTML entity codes to appropriate Unicode characters.
     *   <li> Converts certain Unicode characters in a string to plain text
     *        sequences.
     * </ul>
     *
     * @param s  the string to parse
     *
     * @return the resulting, possibly modified, string
     *
     * @see #convertCharacterEntities
     * @see #stripHTMLTags
     */
    public static String textFromHTML(String s)
    {
        String        stripped = convertCharacterEntities (stripHTMLTags (s));
        char[]        ch = stripped.toCharArray();
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < ch.length; i++)
        {
            switch (ch[i])
            {
                case Unicode.LEFT_SINGLE_QUOTE:
                case Unicode.RIGHT_SINGLE_QUOTE:
                    buf.append ('\'');
                    break;

                case Unicode.LEFT_DOUBLE_QUOTE:
                case Unicode.RIGHT_DOUBLE_QUOTE:
                    buf.append ('"');
                    break;

                case Unicode.EM_DASH:
                    buf.append ("--");
                    break;

                case Unicode.EN_DASH:
                case Unicode.NON_BREAKING_HYPHEN:
                    buf.append ('-');
                    break;

                case Unicode.ZERO_WIDTH_JOINER:
                case Unicode.ZERO_WIDTH_NON_JOINER:
                    break;

                case Unicode.TRADEMARK:
                    buf.append ("[TM]");
                    break;

                case Unicode.NBSP:
                case Unicode.THIN_SPACE:
                case Unicode.HAIR_SPACE:
                case Unicode.EM_SPACE:
                case Unicode.EN_SPACE:
                    buf.append(' ');
                    break;

                default:
                    buf.append (ch[i]);
                    break;
            }
        }

        return buf.toString();
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Match an entity, minus the leading "&" and ";" characters.
     */
    private static String convertEntity(String s)
    {
        StringBuilder buf = new StringBuilder();
        ResourceBundle bundle = getResourceBundle();

        if (s.charAt(0) == '#')
        {
            if (s.length() == 1)
                buf.append('#');

            else
            {
                // It might be a numeric entity code. Try to parse it as a
                // number. If the parse fails, just put the whole string in the
                // result, as is. Be sure to handle both the decimal form
                // (e.g., &#8482;) and the hexadecimal form (e.g., &#x2122;).

                int cc;
                boolean isHex = (s.length() > 2) && (s.charAt(1) == 'x');
                boolean isLegal = false;
                try
                {
                    if (isHex)
                        cc = Integer.parseInt(s.substring(2), 16);
                    else
                        cc = Integer.parseInt(s.substring(1));

                    // It parsed. Is it a valid Unicode character?

                    if (Character.isDefined((char) cc))
                    {
                        buf.append((char) cc);
                        isLegal = true;
                    }
                }

                catch (NumberFormatException ex)
                {
                }

                if (! isLegal)
                {
                    buf.append("&#");
                    if (isHex)
                        buf.append('x');
                    buf.append(s + ";");
                }
            }
        }

        else
        {
            // Not a numeric entity. Try to find a matching symbolic
            // entity.

            try
            {
                buf.append(bundle.getString("html_" + s));
            }

            catch (MissingResourceException ex)
            {
                buf.append("&" + s + ";");
            }
        }

        return buf.toString();
    }

    /**
     * Load the resource bundle, if it hasn't already been loaded.
     */
    private static ResourceBundle getResourceBundle()
    {
        synchronized (HTMLUtil.class)
        {
            if (resourceBundle == null)
                resourceBundle = ResourceBundle.getBundle (BUNDLE_NAME);
        }

        return resourceBundle;
    }
}
