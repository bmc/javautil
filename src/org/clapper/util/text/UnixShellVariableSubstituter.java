/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.text;

import java.util.Properties;
import java.io.*;

/**
 * <p>The <tt>UnixShellVariableSubstituter</tt> class implements the
 * <tt>VariableSubstituter</tt> interface and provides an inline
 * variable substitution capability using a simplified Unix Bourne (or GNU
 * bash) shell variable syntax. This syntax recognizes the
 * "<tt>$var</tt>" and "<tt>${var}</tt>" sequences as variable
 * references. For example, given the string:</p>
 *
 * <blockquote>
 * <pre>
 * file:///$user.home/profiles/$PLATFORM/config.txt
 * </pre>
 * </blockquote>
 *
 * <p>a <tt>UnixShellVariableSubstituter</tt> will attempt to produce
 * a result string by substituting values for the <tt>user.home</tt>,
 * <tt>PLATFORM</tt> variables.</p>
 *
 * <b><u>Notes and Caveats</u></b>
 *
 * <ol>
 *    <li>To include a literal "$" character in a string, precede it with
 *        a backslash.
 *
 *    <li>If a variable doesn't have a value, its reference is replaced
 *        by an empty string.
 *
 *    <li>As with all <tt>VariableSubstituter</tt> classes, an instance
 *        of the <tt>UnixShellVariableSubstituter</tt> class enforces
 *        its own variable syntax (Unix Bourne shell-style, in this case),
 *        but defers actual variable value resolution to a separate
 *        <tt>VariableDereferencer</tt> object. One consequence of that
 *        approach is that variable names may be case-sensitive or
 *        case-insensitive, depending on how the supplied
 *        <tt>VariableDereferencer</tt> object interprets variable
 *        names.
 * </ol>
 *
 * @see WindowsCmdVariableSubstituter
 * @see VariableDereferencer
 * @see VariableSubstituter
 * @see java.lang.String
 *
 * @version <tt>$Revision$</tt>
 */
public class UnixShellVariableSubstituter
    implements VariableSubstituter, VariableNameChecker
{
    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor.
     */
    public UnixShellVariableSubstituter()
    {
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
     * variable names may consist solely of alphanumeric characters,
     * underscores and periods. This syntax is sufficient to substitute
     * variables from <tt>System.properties</tt>, for instance. If you want
     * more control over the legal characters, use the second
     * <tt>substitute</tt> method.</p>
     *
     * @param s      the string containing possible variable references
     * @param deref  the <tt>VariableDereferencer</tt> object
     *               to use to resolve the variables' values.
     *
     * @return The (possibly) expanded string.
     *
     * @throws VariableSubstitutionException  substitution error
     *
     * @see #substitute(String,VariableDereferencer,VariableNameChecker)
     * @see VariableDereferencer#getValue(String)
     */
    public String substitute (String               s,
                              VariableDereferencer deref)
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
     * name. If <tt>nameChecker</tt> is null, then this method assumes that
     * variable names may consist solely of alphanumeric characters,
     * underscores and periods. This syntax is sufficient to substitute
     * variables from <tt>System.properties</tt>, for instance.</p>
     *
     * @param s            the string containing possible variable references
     * @param deref        the <tt>VariableDereferencer</tt> object
     *                     to use to resolve the variables' values.
     * @param nameChecker  the <tt>VariableNameChecker</tt> object to be
     *                     used to check for legal variable name characters,
     *                     or null
     *
     * @return The (possibly) expanded string.
     *
     * @throws VariableSubstitutionException  substitution error
     *
     * @see #substitute(String,VariableDereferencer)
     * @see VariableDereferencer#getValue(String)
     */
    public String substitute (String               s,
                              VariableDereferencer deref,
                              VariableNameChecker  nameChecker)
        throws VariableSubstitutionException
    {
        StringBuffer  result        = new StringBuffer();
        int           len           = s.length();
        StringBuffer  var           = new StringBuffer();
        boolean       inVar         = false;
        boolean       braces        = false;
        boolean       nextIsLiteral = false;
        int           i;
        char          ch[];

        if (nameChecker == null)
            nameChecker = this;

        ch = s.toCharArray();
        i = 0;
        while (i < len)
        {
            char c = ch[i++];

            if (nextIsLiteral)
            {
                // Literal

                result.append (c);
                nextIsLiteral = false;
            }

            else if (! inVar)
            {
                // Not in a variable.

                if (c == '$')                // Possible start of new variable.
                    inVar = true;
                else if (c == '\\')          // escape; next char is literal
                    nextIsLiteral = true;
                else                         // Just a regular, old character.
                    result.append (c);
            }

            // If we get here, we're currently assembling a variable name.

            else if ( (var.length() == 0) && (c == '{') )
            {
                // start of ${...} sequence
                braces = true;
            }

            else if (nameChecker.legalVariableCharacter (c))
            {
                var.append (c);
            }

            else
            {
                // Not a legal variable character, so we're done assembling
                // this variable name.

                String varName = var.toString();

                if (braces)
                {
                    if (c == '}')            // final brace; substitute
                        result.append (deref.getValue (varName));

                    else   // Missing trailing '}'. No substitution.
                    {
                        result.append ("${" + var.toString());
                        i--;             // push 'c' back on the stack
                    }

                    braces = false;
                }

                else if (var.length() == 0)
                {
                    // '$' followed by something illegal. Syntax error.

                    result.append ('$');
                    i--;                 // push 'c' back on the stack
                }

                else
                {
                    // Legal, non-bracketed variable. Substitute.

                    result.append (deref.getValue (varName));
                    i--;             // push 'c' back on the stack
                }

                inVar = false;
                var.setLength (0);
            }
        }           // end while

        if (inVar)
        {
            // One last variable to handle.

            if (braces) // No trailing '}'. Syntax error.
                result.append ("${" + var.toString());

            else if (var.length() == 0)      // just a trailing "$"
                result.append ('$');

            else
                result.append (deref.getValue (var.toString()));
        }

        return result.toString();
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
}
