/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config;

import java.io.*;
import java.util.*;
import java.net.*;
import org.clapper.util.text.*;
import org.clapper.util.io.*;

/**
 * <p><tt>ConfigurationParser</tt> implements a parser for a configuration file
 * whose syntax is reminiscent of classic Windows .INI files.
 *
 * <h3>Syntax</h3>
 *
 * <p>A configuration file is broken into sections, and each section is
 * introduced by a section name in brackets. For example:</p>
 *
 * <blockquote><pre>
 * [main]
 * installation.directory=/usr/local/foo
 * program.directory: /usr/local/foo/programs
 *
 * [search]
 * searchCommand: find /usr/local/foo -type f -name '*.class'
 *
 * [display]
 * searchFailedMessage=Search failed, sorry.
 * </pre></blockquote>
 *
 * <p>At least one section is required. It is an error to have any variable
 * definitions before the first section header. Sections may be empty.</p>
 *
 * <h4>Section Name Syntax</h4>
 *
 * <p>There can be any amount of whitespace before and after the brackets
 * in a section name; the whitespace is ignored. Section names may consist
 * of alphanumeric characters and periods. Anything else is not
 * permitted.</p>
 *
 * <h4>Variable Syntax</h4>
 *
 * <p>Each section contains zero or more variable settings. Similar to a
 * <tt>Properties</tt> file, the variables are specified as name/value
 * pairs, separated by an equal sign ("=") or a colon (":"). Variable names
 * may contain alphanumerics and periods ("."). Variable values may contain
 * anything at all. The parser ignores whitespace on either side of the "="
 * or ":"; that is, leading whitespace in the value is skipped. Values may
 * be single- or double-quoted; the quotes are stripped. The way to include
 * leading whitespace in a value is to quote the value or escape the
 * whitespace (see below).</p>
 *
 * <h4>Continuation Lines</h4>
 *
 * <p>Variable definitions may span multiple lines; each line to be
 * continued must end with a backslash ("\") character, which escapes the
 * meaning of the newline, causing it to be treated like a space character.
 * The following line is treated as a logical continuation of the first
 * line; however, any leading whitespace is removed from continued lines.
 * For example, the following four variable assignments all have the
 * same value:
 *
 * <blockquote><pre>
 * [test]
 * a: one two three
 * b:            one two three
 * c: one two \
 * three
 * d:        one \
 *                         two \
 *    three
 * </pre></blockquote>
 *
 * <p>Because leading whitespace is skipped, all four variables have the
 * value "one two three".</p>
 *
 * <p>Only variable definition lines may be continued. Section header
 * lines, comment lines (see below) and include directives (see below)
 * cannot span multiple lines.</p>
 *
 * <h4>Metacharacters</h4>
 *
 * <p>Within a variable's value, Java-style ASCII escape sequences
 * <tt>\t</tt>, <tt>\n</tt>, <tt>\r</tt>, <tt>\\</tt>, <tt>\"</tt>,
 * <tt>\'</tt>, <tt>\ </tt> (a backslash and a space), and
 * <tt>&#92;u</tt><i>xxxx</i> are recognized and converted to single
 * characters. Note that metacharacter expansion is performed <i>before</i>
 * variable substitution.</p>
 *
 * <h4>Variable Substitution</h4>
 *
 * <p>A variable value can interpolate the values of other variables,
 * using a variable substitution syntax. The general form of a variable
 * reference is <tt>${sectionName:varName}</tt>. <tt>sectionName</tt> is
 * the name of the section containing the variable to substitute; if
 * omitted, it defaults to the current section. <tt>varName</tt> is the
 * name of the variable to substitute. If the variable doesn't exist, or
 * has no value, an empty string is substituted.</p>
 *
 * <p>For example:</p>
 *
 * <blockquote><pre>
 * [main]
 * installation.directory=/usr/local/foo
 * program.directory: ${installation.directory}/foo/programs
 *
 * [search]
 * searchCommand: find ${main:installation.directory} -type f -name '*.class'
 *
 * [display]
 * searchFailedMessage=Search failed, sorry.
 * </pre></blockquote>
 *
 * <p>Notes and caveats:</p>
 *
 * <ul>
 *   <li> <tt>ConfigurationParser</tt> uses the
 *        {@link UnixShellVariableSubstituter} class to do variable
 *        substitution, so it honors all the syntax conventions supported
 *        by that class.
 *
 *   <li> A variable that directly or indirectly references itself via
 *        variable substitution will cause the parser to throw an exception.
 *
 *   <li> Variable substitutions are only permitted within variable
 *        values and include targets (see below). They are ignored in variable
 *        names, section names, and comments.
 *
 *   <li> Variable substitution is performed <i>after</i> metacharacter
 *        expansion (so don't include metacharacter sequences in your variable
 *        names).
 * </ul>
 *
 * <h4>Includes</h4>
 *
 * <p>A special include directive permits inline inclusion of another
 * configuration file. The include directive takes two forms:
 *
 * <blockquote><pre>
 * %include "path"
 * %include "URL"
 * </pre></blockquote>
 *
 * <p>For example:</p>
 *
 * <blockquote><pre>
 * %include "/home/bmc/mytools/common.cfg"
 * %include "http://configs.example.com/mytools/common.cfg"
 * </pre></blockquote>
 *
 * <p>The included file may contain any content that is valid for this
 * parser. It may contain just variable definitions (i.e., the contents of
 * a section, without the section header), or it may contain a complete
 * configuration file, with individual sections. Since
 * <tt>ConfigurationParser</tt> recognizes a variable syntax that is
 * essentially identical to Java's properties file syntax, it's also legal
 * to include a properties file, provided it's included within a valid
 * section.</p>
 *
 * <p>Note: Attempting to include a file from itself, either directly or
 * indirectly, will cause the parser to throw an exception.</p>
 * 
 * <h4>Comments</h4>
 *
 * <p>A comment line is a one whose first non-whitespace character is a "#"
 * or a "!". This comment syntax is identical to the one supported by a
 * Java properties file.</p>
 *
 * @version <tt>$Revision$</tt>
 */
public class ConfigurationParser
    implements VariableDereferencer, VariableNameChecker
{
    /*----------------------------------------------------------------------*\
                                 Constants
    \*----------------------------------------------------------------------*/

    private static final String COMMENT_CHARS              = "#!";
    private static final char   SECTION_START              = '[';
    private static final char   SECTION_END                = ']';
    private static final String INCLUDE                    = "%include";
    private static final int    MAX_INCLUDE_NESTING_LEVEL  = 50;

    /*----------------------------------------------------------------------*\
                                  Classes
    \*----------------------------------------------------------------------*/

    /**
     * Contains one logical input line.
     */
    private class Line
    {
        static final int COMMENT  = 0;
        static final int INCLUDE  = 1;
        static final int SECTION  = 2;
        static final int VARIABLE = 3;
        static final int BLANK    = 4;

        int           number = 0;
        int           type   = COMMENT;
        StringBuffer  buffer = new StringBuffer();

        Line()
        {
        }

        void newLine()
        {
            buffer.setLength (0);
        }
    }

    /**
     * Contents of a variable. Mostly exists to make replacing a variable
     * value easier while looping over a section.
     */
    private class Variable
    {
        String name;
        String value;

        Variable (String name, String value)
        {
            this.name = name;
            this.value = value;
        }
    }

    /**
     * Contents of a section
     */
    private class Section
    {
        /**
         * Name of section
         */
        String name;

        /**
         * Names of variables, in order encountered. Contains strings.
         */
        List variableNames = new ArrayList();

        /**
         * List of Variable objects, indexed by variable name
         */
        Map valueMap = new HashMap();

        Section (String name)
        {
            this.name = name;
        }

        Variable getVariable (String varName)
        {
            return (Variable) valueMap.get (varName);
        }

        void addVariable (String varName, String value)
        {
            valueMap.put (varName, new Variable (varName, value));
            variableNames.add (varName);
        }
    }

    /**
     * Container for data used only during parsing.
     */
    private class ParseData
    {
        /**
         * Current section. Only set during parsing.
         */
        private Section currentSection = null;

        /**
         * Current variable name being processed. Used during the variable
         * substitution parsing phase.
         */
        private String currentVariable = null;

        /**
         * Total number of variable substitutions performed on the current
         * variable's value during one substitution round. Used during the
         * variable substitution parsing phase.
         */
        private int totalSubstitutions = 0;

        /**
         * Current include file nesting level. Used as a fail-safe during
         * parsing.
         */
        private int includeFileNestingLevel = 0;

        /**
         * Table of files/URLs currently open. Used during include
         * processing.
         */
        private Set openURLs = new HashSet();

        ParseData()
        {
        }
    }

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * List of sections, in order encountered. Each element is a reference to
     * a Section object.
     */
    private List sectionsInOrder = new ArrayList();

    /**
     * Sections by name. Each index is a string. Each value is a reference to
     * a Section object.
     */
    private Map sectionsByName = new HashMap();

    /**
     * Data used during parsing. Null when parsing isn't being done.
     */
    private ParseData parseData = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a <tt>ConfigurationParser</tt> object that parses data from
     * the specified file.
     *
     * @param f  The <tt>File</tt> to open and parse
     *
     * @throws IOException             can't open or read file
     * @throws ConfigurationException  error in configuration data
     */
    public ConfigurationParser (File f)
        throws IOException,
               ConfigurationException
    {
        load (f);
    }

    /**
     * Construct a <tt>ConfigurationParser</tt> object that parses data from
     * the specified file.
     *
     * @param path  the path to the file to parse
     *
     * @throws FileNotFoundException   specified file doesn't exist
     * @throws IOException             can't open or read file
     * @throws ConfigurationException  error in configuration data
     */
    public ConfigurationParser (String path)
        throws FileNotFoundException,
               IOException,
               ConfigurationException
    {
        load (path);
    }

    /**
     * Construct a <tt>ConfigurationParser</tt> object that parses data from
     * the specified URL.
     *
     * @param url  the URL to open and parse
     *
     * @throws IOException             can't open or read URL
     * @throws ConfigurationException  error in configuration data
     */
    public ConfigurationParser (URL url)
        throws IOException,
               ConfigurationException
    {
        load (url);
    }

    /**
     * Construct a <tt>ConfigurationParser</tt> object that parses data from
     * the specified <tt>InputStream</tt>.
     *
     * @param iStream  the <tt>InputStream</tt>
     *
     * @throws IOException             can't open or read URL
     * @throws ConfigurationException  error in configuration data
     */
    public ConfigurationParser (InputStream iStream)
        throws IOException,
               ConfigurationException
    {
        parse (iStream, null);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Writes the configuration data to a <tt>PrintWriter</tt>. The sections
     * and variables within the sections are written in the order they were
     * originally read from the file. Non-printable characters (and a few
     * others) are encoded into metacharacter sequences. Comments and
     * variable references are not propagated, since they are not retained
     * when the data is parsed.
     *
     * @param out  where to write the configuration data
     *
     * @see XStringBuffer#encodeMetacharacters()
     */
    public void write (PrintWriter out)
    {
        Iterator       itSect;
        Iterator       itVar;
        Section        section;
        String         varName;
        Variable       var;
        XStringBuffer  value = new XStringBuffer();
        boolean        firstSection = true;

        for (itSect = sectionsInOrder.iterator(); itSect.hasNext(); )
        {
            section = (Section) itSect.next();

            if (! firstSection)
                out.println();

            out.println (SECTION_START + section.name + SECTION_END);
            firstSection = false;

            for (itVar = section.variableNames.iterator(); itVar.hasNext(); )
            {
                varName = (String) itVar.next();
                var     = (Variable) section.getVariable (varName);

                value.setLength (0);
                value.append (var.value);
                value.encodeMetacharacters();

                out.println (varName + ": " + value.toString());
            }
        }
    }

    /**
     * Writes the configuration data to a <tt>PrintStream</tt>. The sections
     * and variables within the sections are written in the order they were
     * originally read from the file. Non-printable characters (and a few
     * others) are encoded into metacharacter sequences. Comments and
     * variable references are not propagated, since they are not retained
     * when the data is parsed.
     *
     * @param out  where to write the configuration data
     *
     * @see XStringBuffer#encodeMetacharacters()
     */
    public void write (PrintStream out)
    {
        PrintWriter w = new PrintWriter (out);
        write (w);
        w.flush();
    }

    /**
     * Get the value associated with a given variable. Required by the
     * {@link VariableDereferencer} interface, this method is used during
     * parsing to handle variable substitutions (but also potentially
     * useful by other applications).
     *
     * @param varName  The name of the variable for which the value is
     *                 desired.
     *
     * @return The variable's value. If the variable has no value, this
     *         method must return the empty string (""). It is important
     *         <b>not</b> to return null.
     *
     * @throws VariableSubstitutionException  variable references itself
     */
    public String getValue (String varName)
        throws VariableSubstitutionException
    {
        if (parseData.currentVariable.equals (varName))
        {
            throw new VariableSubstitutionException ("Attempt to substitute "
                                                   + "value for variable \""
                                                   + varName
                                                   + "\" within itself.");
        }

        int     i;
        Section section;
        String  value = null;

        i = varName.indexOf (':');
        if (i == -1)
        {
            section = parseData.currentSection;
        }

        else
        {
            section = (Section) sectionsByName.get (varName.substring (0, i));
            varName = varName.substring (i + 1);
        }

        if (section != null)
        {
            Variable var = (Variable) section.getVariable (varName);
            if (var != null)
                value = var.value;
        }

        parseData.totalSubstitutions++;

        return (value == null) ? "" : value;
    }

    /**
     * Required by the {@link VariableNameChecker} interface, this method
     * determines whether a character may legally be used in a variable name
     * or not.
     *
     * @param c   The character to test
     *
     * @return <tt>true</tt> if the character may be part of a variable name,
     *         <tt>false</tt> otherwise
     *
     * @see VariableSubstituter#substitute
     */
    public boolean legalVariableCharacter (char c)
    {
        return (Character.isLetterOrDigit (c) ||
                (c == '_') ||
                (c == '.') ||
                (c == ':'));
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Load configuration from a File. (Front-end to parse().)
     *
     * @param file  the file
     *
     * @throws IOException            read error
     * @throws ConfigurationException parse error
     */
    private void load (File file)
        throws IOException,
               ConfigurationException
    {
        parse (new FileInputStream (file), file.toURL());
    }

    /**
     * Load configuration from a file specified as a pathname. (Front-end
     * to parse().)
     *
     * @param path  the path
     *
     * @throws FileNotFoundException   specified file doesn't exist
     * @throws IOException             can't open or read file
     * @throws ConfigurationException  error in configuration data
     */
    private void load (String path)
        throws FileNotFoundException,
               IOException,
               ConfigurationException
    {
        parse (new FileInputStream (path), new File (path).toURL());
    }

    /**
     * Load configuration from a URL. (Front-end to parse().)
     *
     * @param url  the URL
     *
     * @throws IOException            read error
     * @throws ConfigurationException parse error
     */
    private void load (URL url)
        throws IOException,
               ConfigurationException
    {
        parse (url.openStream(), url);
    }

    /**
     * Parse configuration data from the specified stream.
     *
     * @param in    the input stream
     * @param url   the URL associated with the stream, or null if not known
     *
     * @throws ConfigurationException parse error
     */
    private synchronized void parse (InputStream in, URL url)
        throws ConfigurationException
    {
        parseData = new ParseData();

        try
        {
            loadConfiguration (in, url);
            postProcessParsedData();
        }

        finally
        {
            parseData = null;
        }
    }

    /**
     * Load the configuration data into memory, without processing
     * metacharacters or variable substitution. Includes are processed,
     * though.
     *
     * @param in    the input stream
     * @param url   the URL associated with the stream, or null if not known
     *
     * @throws IOException            read error
     * @throws ConfigurationException parse error
     */
    private void loadConfiguration (InputStream in, URL url)
        throws ConfigurationException
    {
        BufferedReader r    = new BufferedReader (new InputStreamReader (in));
        Line           line = new Line();
        String         sURL = url.toExternalForm();

        if (parseData.openURLs.contains (sURL))
        {
            throw new ConfigurationException
                            (getExceptionPrefix (line, url)
                          + "Attempt to include \""
                          + sURL
                           + "\" from itself, either directly or indirectly.");
        }

        parseData.openURLs.add (sURL);

        // Parse the entire file into memory before doing variable
        // substitution and metacharacter expansion.

        while (readLogicalLine (r, line))
        {
            try
            {
                switch (line.type)
                {
                    case Line.COMMENT:
                    case Line.BLANK:
                        break;

                    case Line.INCLUDE:
                        handleInclude (line, url);
                        break;

                    case Line.SECTION:
                        parseData.currentSection = handleNewSection (line,
                                                                     url);
                        break;

                    case Line.VARIABLE:
                        if (parseData.currentSection == null)
                        {
                            throw new ConfigurationException
                                       (getExceptionPrefix (line, url)
                                      + "Variable assignment before "
                                      + "first section.");
                        }

                        handleVariable (line, url);
                        break;

                    default:
                        throw new IllegalStateException
                                          ("Bug: line.type=" + line.type);
                }
            }

            catch (IOException ex)
            {
                throw new ConfigurationException
                                     (getExceptionPrefix (line, url)
                                    + ex.toString());
            }
        }

        parseData.openURLs.remove (sURL);
    }

    /**
     * Post-process the loaded configuration, doing variable substitution
     * and metacharacter expansion.
     *
     * @throws ConfigurationException  configuration error
     */
    private void postProcessParsedData()
        throws ConfigurationException
    {
        VariableSubstituter  substituter;
        Iterator             itSect;
        Iterator             itVar;
        Variable             var;
        XStringBuffer        buf = new XStringBuffer();

        // First, expand the the metacharacter sequences.

        for (itSect = sectionsInOrder.iterator(); itSect.hasNext(); )
        {
            parseData.currentSection = (Section) itSect.next();

            for (itVar = parseData.currentSection.variableNames.iterator();
                 itVar.hasNext(); )
            {
                parseData.currentVariable = (String) itVar.next();
                var = parseData.currentSection.getVariable
                                                  (parseData.currentVariable);

                buf.setLength (0);
                buf.append (var.value);
                buf.decodeMetacharacters();
                var.value = buf.toString();
            }
        }

        // Now, do variable substitution.

        parseData.currentSection = null;
        substituter = new UnixShellVariableSubstituter();

        for (itSect = sectionsInOrder.iterator(); itSect.hasNext(); )
        {
            parseData.currentSection = (Section) itSect.next();

            for (itVar = parseData.currentSection.variableNames.iterator();
                 itVar.hasNext(); )
            {
                parseData.currentVariable = (String) itVar.next();
                var = parseData.currentSection.getVariable
                                                  (parseData.currentVariable);

                // Keep substituting the current variable's value until
                // there no more substitutions are performed. This handles
                // the case where a dereferenced variable value contains
                // its own variable references.

                do
                {
                    parseData.totalSubstitutions = 0;

                    try
                    {
                        var.value = substituter.substitute (var.value,
                                                            this,
                                                            this);
                    }
                    catch (VariableSubstitutionException ex)
                    {
                        throw new ConfigurationException (ex);
                    }
                }
                while (parseData.totalSubstitutions > 0);
            }
        }
    }

    /**
     * Handle a new section.
     *
     * @param line  line buffer
     * @param url   URL currently being processed, or null if unknown
     *
     * @return a new Section object, which has been stored in the appropriate
     *         places
     *
     * @throws ConfigurationException  configuration error
     */
    private Section handleNewSection (Line line, URL url)
        throws ConfigurationException
    {
        String s = line.buffer.toString().trim();

        if (s.charAt (0) != SECTION_START)
        {
            throw new ConfigurationException
                (getExceptionPrefix (line, url)
               + "Section does not begin with '"
               + SECTION_START
               + "'");
        }

        else if (s.charAt (s.length() - 1) != SECTION_END)
        {
            throw new ConfigurationException
                (getExceptionPrefix (line, url)
               + "Section does not end with '"
               + SECTION_END
               + "'");
        }

        String  name    = s.substring (1, s.length() - 1);
        Section section = new Section (name);

        sectionsInOrder.add (section);
        sectionsByName.put (name, section);

        return section;
    }

    /**
     * Handle a new variable.
     *
     * @param line  line buffer
     * @param url   URL currently being processed, or null if unknown
     *
     * @throws ConfigurationException  configuration error
     */
    private void handleVariable (Line line, URL url)
        throws ConfigurationException
    {
        String s = line.buffer.toString();
        int    i;

        if ( ((i = s.indexOf ('=')) == -1) &&
             ((i = s.indexOf (':')) == -1) )
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Missing '=' or ':' for "
                                            + "variable definition.");
        }

        if (i == 0)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Missing variable name for "
                                            + "variable definition.");
        }

        String varName = s.substring (0, i);
        String value   = s.substring (skipWhitespace (s, i + 1));

        if (parseData.currentSection.getVariable (varName) != null)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Section \""
                                            + parseData.currentSection.name
                                            + "\": Duplicate definition of "
                                            + "variable \""
                                            + varName
                                            + "\".");
        }

        parseData.currentSection.addVariable (varName, value);
    }

    /**
     * Handle an include directive.
     *
     * @param line  line buffer
     * @param url   URL currently being processed, or null if unknown
     *
     * @throws IOException             I/O error opening or reading include
     * @throws ConfigurationException  configuration error
     */
    private void handleInclude (Line line, URL url)
        throws IOException,
               ConfigurationException
    {
        if (parseData.includeFileNestingLevel >= MAX_INCLUDE_NESTING_LEVEL)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Exceeded maximum nested "
                                            + "include level of "
                                            + MAX_INCLUDE_NESTING_LEVEL
                                            + ".");
        }

        parseData.includeFileNestingLevel++;

        String s = line.buffer.toString();

        // Parse the file name.

        String includeTarget = s.substring (INCLUDE.length() + 1).trim();
        int len = includeTarget.length();

        // Make sure double quotes surround the file or URL.

        if ((len < 2) ||
            (! includeTarget.startsWith ("\"")) ||
            (! includeTarget.endsWith ("\"")))
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Malformed "
                                            + INCLUDE
                                            + " directive.");
        }

        // Extract the file.

        includeTarget = includeTarget.substring (1, len - 1);
        if (includeTarget.length() == 0)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Missing file name or URL in "
                                            + INCLUDE
                                            + " directive.");
        }

        // Process the include

        try
        {
            loadInclude (new URL (includeTarget));
        }

        catch (MalformedURLException ex)
        {
            // Not obviously a URL. First, determine whether it has
            // directory information or not. If not, try to use the
            // parent's directory information.

            if (FileUtils.isAbsolutePath (includeTarget))
            {
                loadInclude (new URL (url.getProtocol(),
                                      url.getHost(),
                                      url.getPort(),
                                      includeTarget));
            }

            else
            {
                // It's relative to the parent. If the parent URL is not
                // specified, then we can't do anything except try to load
                // the include as is. It'll probably fail...

                if (url == null)
                {
                    loadInclude (new File (includeTarget).toURL());
                }

                else
                {
                    String parent = new File (url.getFile()).getParent();

                    if (parent == null)
                        parent = "";

                    loadInclude (new URL (url.getProtocol(),
                                          url.getHost(),
                                          url.getPort(),
                                          parent + "/" + includeTarget));
                }
            }
        }

        parseData.includeFileNestingLevel--;
    }

    /**
     * Actually attempts to load an include reference. This is basically just
     * a simplified front-end to loadConfiguration().
     *
     * @param url  the URL to be included
     *
     * @throws IOException  I/O error
     * @throws ConfigurationException configuration error
     */
    private void loadInclude (URL url)
        throws IOException,
               ConfigurationException
    {
        loadConfiguration (url.openStream(), url);
    }

    /**
     * Read the next logical line of input from a config file.
     *
     * @param r    the reader
     * @param line where to store the line. The line number in this
     *             object is incremented, the "buffer" field is updated,
     *             and the "type" field is set appropriately.
     *
     * @return <tt>true</tt> if a line was read, <tt>false</tt> for EOF.
     *
     * @throws ConfigurationException read error
     */
    private boolean readLogicalLine (BufferedReader r, Line line)
        throws ConfigurationException
    {
        boolean  continued    = false;
        boolean  gotSomething = false;

        line.newLine();
        for (;;)
        {
            String s;

            try
            {
                s = r.readLine();
            }

            catch (IOException ex)
            {
                throw new ConfigurationException (ex);
            }

            if (s == null)
                break;

            gotSomething = true;
            line.number++;

            // Strip leading white space on all lines.

            int i;
            char[] chars = s.toCharArray();

            i = skipWhitespace (chars, 0);
            if (i < chars.length)
                s = s.substring (i);
            else
                s = "";

            if (! continued)
            {
                // First line. Determine what it is.

                char firstChar;

                if (s.length() == 0)
                    line.type = Line.BLANK;

                else if (COMMENT_CHARS.indexOf (s.charAt (0)) != -1)
                    line.type = Line.COMMENT;

                else if (s.charAt (0) == SECTION_START)
                    line.type = Line.SECTION;

                else if (new StringTokenizer (s).nextToken().equals (INCLUDE))
                    line.type = Line.INCLUDE;

                else
                    line.type = Line.VARIABLE;
            }

            if ((line.type == Line.VARIABLE) && (hasContinuationMark (s)))
            {
                continued = true;
                line.buffer.append (s.substring (0, s.length() - 1));
            }

            else
            {
                line.buffer.append (s);
                break;
            }
        }

        return gotSomething;
    }

    /**
     * Determine whether a line has a continuation mark or not.
     *
     * @param s  the line
     *
     * @return true if there's a continuation mark, false if not
     */
    private boolean hasContinuationMark (String s)
    {
        boolean has = false;

        if (s.length() > 0)
        {
            char[] chars = s.toCharArray();

            if (chars[chars.length-1] == '\\')
            {
                // Possibly. See if there are an odd number of them.

                int total = 0;
                for (int i = chars.length - 1; i >= 0; i--)
                {
                    if (chars[i] != '\\')
                        break;

                    total++;
                }

                has = ((total % 2) == 1);
            }
        }

        return has;
    }

    /**
     * Get an appropriate exception prefix (e.g., line number, etc.)
     *
     * @param line  line buffer
     * @param url   URL currently being processed, or null if unknown
     *
     * @return a suitable string
     */
    private String getExceptionPrefix (Line line, URL url)
    {
        StringBuffer buf = new StringBuffer();

        if (url != null)
        {
            buf.append (url.toExternalForm());
            buf.append (", line ");
        }

        else
        {
            buf.append ("Line ");
        }

        buf.append (line.number);
        buf.append (": ");

        return buf.toString();
    }

    /**
     * Get index of first non-whitespace character.
     *
     * @param s     string to check
     * @param start starting point
     *
     * @return index of first non-whitespace character past "start", or -1
     */
    private int skipWhitespace (String s, int start)
    {
        return skipWhitespace (s.toCharArray(), start);
    }

    /**
     * Get index of first non-whitespace character.
     *
     * @param chars character array to check
     * @param start starting point
     *
     * @return index of first non-whitespace character past "start", or -1
     */
    private int skipWhitespace (char[] chars, int start)
    {
        while (start < chars.length)
        {
            if (! Character.isWhitespace (chars[start]))
                break;

            start++;
        }

        return start;
    }
}
