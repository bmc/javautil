/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.io;

import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.oro.io.*;
import org.apache.oro.text.*;
import org.apache.oro.text.regex.*;

/**
 * <p><tt>MultipleRegexFilenameFilter</tt> implements a
 * <tt>java.io.FilenameFilter</tt> that matches file names and path names
 * using one or more regular expressions. A
 * <tt>MultipleRegexFilenameFilter</tt> contains two sets of regular
 * expressions, an <i>accept</i> set and a <i>reject</i> set. To be
 * accepted, a file name must not match any of the patterns in the
 * <i>reject</i> set, and it <b>must</b> match at least one of the patterns
 * in the <i>accept</i> set. If the <i>reject</i> set is empty, then no
 * explicit rejections are done. However, if the <i>accept</i> set is empty,
 * then all files are assumed to be accepted. (i.e., It's as if the
 * <i>accept</i> set contained a single "^.*$" pattern.)</p>
 *
 * <p>A <tt>MultipleRegexFilenameFilter</tt> can be configured to operate
 * on just the simple file name, or on the file's path.</p>
 *
 * <p><tt>MultipleRegexFilenameFilter</tt> uses the
 * {@link <a href="http://jakarta.apache.org/oro/">Jakarta ORO</a>}
 * regular expression classes.</p>
 *
 * @version <tt>$Revision$</tt>
 */
public class MultipleRegexFilenameFilter implements FilenameFilter
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    public static final int MATCH_FILENAME = 0;
    public static final int MATCH_PATH     = 1;

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private Collection       acceptPatterns = null;
    private Collection       rejectPatterns = null;
    private PatternMatcher   regexMatcher;
    private PatternCompiler  regexCompiler;
    private int              regexOptions;
    private int              matchType = MATCH_FILENAME;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>MultipleRegexFilenameFilter</tt>.
     *
     * @param matchType <tt>MATCH_FILENAME</tt> to match just the
     *                  filename, <tt>MATCH_PATH</tt> to match the path
     */
    public MultipleRegexFilenameFilter (int matchType)
    {
        switch (matchType)
        {
            case MATCH_FILENAME:
            case MATCH_PATH:
                this.matchType = matchType;
                break;

            default:
                throw new IllegalArgumentException ("Bad matchType parameter: "
                                                  + matchType);
        }
        
        regexCompiler  = new Perl5Compiler();
        regexMatcher   = new Perl5Matcher();
        regexOptions   = Perl5Compiler.CASE_INSENSITIVE_MASK;
        acceptPatterns = new ArrayList();
        rejectPatterns = new ArrayList();
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add an "accept" pattern to this filter. For a file to be accepted:
     *
     * <ul>
     *   <li> it must not match one of the <i>reject</i> patterns, and
     *   <li> either the <i>accept</i> pattern list must be empty, or the
     *        file name must match one of the <i>accept</i> patterns
     * </ul>
     *
     * @param pattern  the regular expression to add
     *
     * @throws MalformedPatternException  bad regular expression
     *
     * @see #addRejectPattern
     */
    public void addAcceptPattern (String pattern)
        throws MalformedPatternException
    {
        acceptPatterns.add (regexCompiler.compile (pattern, regexOptions));
    }

    /**
     * Add an "accept" pattern to this filter. For a file to be accepted:
     *
     * <ul>
     *   <li> it must not match one of the <i>reject</i> patterns, and
     *   <li> either the <i>accept</i> pattern list must be empty, or the
     *        file name must match one of the <i>accept</i> patterns
     * </ul>
     *
     * @param pattern  the regular expression to add
     *
     * @throws MalformedPatternException  bad regular expression
     *
     * @see #addAcceptPattern
     */
    public void addRejectPattern (String pattern)
        throws MalformedPatternException
    {
        rejectPatterns.add (regexCompiler.compile (pattern, regexOptions));
    }

    /**
     * Determine whether a file is to be accepted or not, based on the
     * regular expressions in the <i>reject</i> and <i>accept</i> lists.
     *
     * @param dir   The directory containing the file. Ignored if
     *              the match type is {@link #MATCH_FILENAME}. Used to build
     *              the path to match if the match type is {@link #MATCH_PATH}
     * @param name  the file name
     *
     * @return <tt>true</tt> if the file matches, <tt>false</tt> if it doesn't
     */
    public boolean accept (File dir, String name)
    {
        Iterator  it;
        boolean   match = false;
        boolean   found = false;

        if (matchType == MATCH_PATH)
        {
            name = dir.getPath()
                 + System.getProperty ("file.separator")
                 + name;
        }

        // Check for rejects first.

        for (it = rejectPatterns.iterator(); it.hasNext(); )
        {
            Pattern pattern = (Pattern) it.next();

            if (regexMatcher.contains (name, pattern))
            {
                match = false;
                found = true;
                break;
            }
        }

        if (! found)
        {
            // Check for accepts.

            if (acceptPatterns.size() == 0)
                match = true;

            else
            {
                for (it = acceptPatterns.iterator(); it.hasNext(); )
                {
                    Pattern pattern = (Pattern) it.next();

                    if (regexMatcher.contains (name, pattern))
                    {
                        match = true;
                        break;
                    }
                }
            }
        }

        return match;
    }
}
