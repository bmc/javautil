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

package org.clapper.util.text;

/**
 * Static class containing miscellaneous HTML-related utility methods.
 *
 * @deprecated Moved to <tt>org.clapper.util.html</tt> package
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public final class HTMLUtil
{
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
     *
     * @deprecated Use {@link org.clapper.util.html.HTMLUtil#stripHTMLTags}
     */
    public static String stripHTMLTags(String s)
    {
        return org.clapper.util.html.HTMLUtil.stripHTMLTags(s);
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
     *
     * @deprecated Use {@link org.clapper.util.html.HTMLUtil#convertCharacterEntities}
     */
    public static String convertCharacterEntities(String s)
    {
        return org.clapper.util.html.HTMLUtil.convertCharacterEntities(s);
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
     *
     * @deprecated Use {@link org.clapper.util.html.HTMLUtil#makeCharacterEntities}
     */
    public static String makeCharacterEntities(String s)
    {
        return org.clapper.util.html.HTMLUtil.makeCharacterEntities(s);
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
     *
     * @deprecated Use {@link org.clapper.util.html.HTMLUtil#makeCharacterEntities}
     */
    public static String textFromHTML(String s)
    {
        return org.clapper.util.html.HTMLUtil.textFromHTML(s);
    }
}
