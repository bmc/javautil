/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2006 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M.a Clapper.

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
 * <p>The <tt>WindowsCmdVariableSubstituter</tt> class implements the
 * <tt>VariableSubstituter</tt> interface and provides an inline
 * variable substitution capability using a syntax that's reminiscent of
 * the Microsoft Windows <tt>cmd.exe</tt> command interpreter.
 * This syntax assumes that variable references are surrounded by "%"
 * characters. For example, given the string</p>
 *
 * <blockquote>
 * <pre>
 * file:///%user.home%/profiles/%PLATFORM%/config.txt
 * </pre>
 * </blockquote>
 *
 * <p>a <tt>WindowsStyleVarSubstituter</tt> will attempt to produce a
 * result string by substituting values for the <tt>user.home</tt> and
 * <tt>PLATFORM</tt> variables.</p>
 *
 * <b><u>Notes and Caveats</u></b>
 *
 * <ol>
 *    <li>To include a literal "%" character in a string, double it.
 *
 *    <li>If a variable doesn't have a value, its reference is replaced
 *        by an empty string.
 *
 *    <li>As with all <tt>VariableSubstituter</tt> classes, an instance
 *        of the <tt>WindowsCmdVariableSubstituter</tt> class enforces
 *        its own variable syntax but defers actual variable value
 *        resolution to a separate <tt>VariableDereferencer</tt> object.
 *        One consequence of that approach is that variable names may be
 *        case-sensitive or case-insensitive, depending on how the supplied
 *        <tt>VariableDereferencer</tt> object interprets variable names.
 * </ol>
 *
 * @see UnixShellVariableSubstituter
 * @see VariableDereferencer
 * @see VariableSubstituter
 * @see java.lang.String
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public class WindowsCmdVariableSubstituter
    implements VariableSubstituter, VariableNameChecker
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor.
     */
    public WindowsCmdVariableSubstituter()
    {
        // Nothing to do
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * <p>Substitute all variable references in the supplied string, using
     * a Unix Bourne Shell-style variable syntax. This method uses a
     * supplied <tt>VariableDereferencer</tt> object to resolve variable
     * values. Note that this method throws no exceptions. Syntax errors in
     * the variable references are silently ignored. Variables that have no
     * value are substituted as the empty string. This method assumes that
     * variable names may consist solely of alphanumeric characters and
     * underscores. If you want more control over the legal characters, use
     * the second <tt>substitute</tt> method.</p>
     *
     * @param s       the string containing possible variable references
     * @param deref   the <tt>VariableDereferencer</tt> object
     *                to use to resolve the variables' values.
     * @param context an optional context object, passed through unmodified
     *                to the <tt>deref</tt> object's
     *                {@link VariableDereferencer#getVariableValue} method.
     *                This object can be anything at all (and, in fact, may
     *                be null if you don't care.) It's primarily useful
     *                for passing context information from the caller to
     *                the (custom) <tt>VariableDereferencer</tt>.
     *
     * @return The (possibly) expanded string.
     *
     * @throws VariableSubstitutionException  substitution error
     *
     * @see #substitute(String,VariableDereferencer,VariableNameChecker,Object)
     * @see VariableDereferencer#getVariableValue(String,Object)
     */
    public String substitute (String               s,
                              VariableDereferencer deref,
                              Object               context)
        throws VariableSubstitutionException
    {
        return substitute (s, deref, null);
    }

    /**
     * <p>Substitute all variable references in the supplied string, using
     * a Unix Bourne Shell-style variable syntax. This method uses a
     * supplied <tt>VariableDereferencer</tt> object to resolve variable
     * values. Note that this method throws no exceptions. Syntax errors in
     * the variable references are silently ignored. Variables that have no
     * value are substituted as the empty string. If the
     * <tt>nameChecker</tt> parameter is not null, this method calls its
     * {@link VariableNameChecker#legalVariableCharacter(char)} method to
     * determine whether a given character is a legal part of a variable
     * name. If <tt>nameChecker</tt> is null, then this method assumes
     * that variable names may consist solely of alphanumeric characters and
     * underscores.</p>
     *
     * @param s            the string containing possible variable references
     * @param deref        the <tt>VariableDereferencer</tt> object
     *                     to use to resolve the variables' values.
     * @param nameChecker  the <tt>VariableNameChecker</tt> object to be
     *                     used to check for legal variable name characters,
     *                     or null
     * @param context      an optional context object, passed through
     *                     unmodified to the <tt>deref</tt> object's
     *                     {@link VariableDereferencer#getVariableValue}
     *                     method. This object can be anything at all (and,
     *                     in fact, may be null if you don't care.) It's
     *                     primarily useful for passing context information
     *                     from the caller to the
     *                     <tt>VariableDereferencer</tt>.
     *
     * @return The (possibly) expanded string.
     *
     * @throws VariableSubstitutionException  substitution error
     *
     * @see #substitute(String,VariableDereferencer,Object)
     * @see VariableDereferencer#getVariableValue(String,Object)
     */
    public String substitute (      String               s,
                              final VariableDereferencer deref,
                              final VariableNameChecker  nameChecker,
                              final Object               context)
        throws VariableSubstitutionException
    {
        if (s != null)
            s = doSubstitution(s, context, nameChecker, deref);

        return s;
    }


    /**
     * Determine whether a character is a legal variable identifier character.
     *
     * @param c  The character
     *
     * @return <tt>true</tt> if the character is legal, <tt>false</tt>
     *         otherwise.
     */
    public boolean legalVariableCharacter (char c)
    {
        // Must be a letter, digit or underscore.

        return (Character.isLetterOrDigit (c) || (c == '_') || (c == '.'));
    }

    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    /**
     * Worker routine called by substitute() to perform the actual
     * substitution on a non-null string.
     *
     * @param s            the string containing possible variable references
     * @param deref        the <tt>VariableDereferencer</tt> object
     *                     to use to resolve the variables' values.
     * @param nameChecker  the <tt>VariableNameChecker</tt> object to be
     *                     used to check for legal variable name characters,
     *                     or null
     * @param context      an optional context object, passed through
     *                     unmodified to the <tt>deref</tt> object's
     *                     {@link VariableDereferencer#getVariableValue}
     *                     method. This object can be anything at all (and,
     *                     in fact, may be null if you don't care.) It's
     *                     primarily useful for passing context information
     *                     from the caller to the
     *                     <tt>VariableDereferencer</tt>.
     *
     * @return The (possibly) expanded string.
     *
     * @throws VariableSubstitutionException  substitution error
     */
    private String doSubstitution (final String s,
                                   final Object context,
                                         VariableNameChecker nameChecker,
                                   final VariableDereferencer deref)
        throws VariableSubstitutionException
    {

        StringBuffer  result      = new StringBuffer();
        int           len         = s.length();
        char          prev        = '\0';
        StringBuffer  var         = new StringBuffer();
        boolean       inVar       = false;
        boolean       syntaxError = false;
        char          ch[];

        if (nameChecker == null)
            nameChecker = this;

        ch = s.toCharArray();
        for (int i = 0; i < len; i++)
        {
            char c = ch[i];

            if (c == '%')
            {
                if (inVar)
                {
                    if (prev == '%')
                    {
                        // Doubled "%". Insert one literal "%".

                        inVar = false;
                        result.append ('%');
                    }

                    else
                    {
                        // End of variable reference. If the variable name
                        // is syntactically incorrect, just store the
                        // entire original sequence in the result string.

                        String varName = var.toString();
                        if (syntaxError)
                            result.append ('%' + varName + '%');
                        else
                            result.append (deref.getVariableValue (varName,
                                                                   context));

                        var.setLength (0);
                        inVar       = false;
                        syntaxError = false;
                        prev        = '\0';  // prevent match on trailing "%"
                    }
                }

                else
                {
                    // Possible start of a new variable.

                    inVar = true;
                    prev = c;
                }
            }

            else
            {
                // Not a '%'

                if (inVar)
                {
                    var.append (c);
                    if (! nameChecker.legalVariableCharacter (c))
                        syntaxError = true;
                }

                else
                {
                    result.append (c);
                }
                prev = c;
            }
        }

        if (inVar)
        {
            // Never saw the trailing "%" for the last variable reference.
            // Transfer the characters buffered in 'var' into the result,
            // without modification.

            result.append ('%');
            result.append (var.toString());
        }

        return result.toString();
    }
}
