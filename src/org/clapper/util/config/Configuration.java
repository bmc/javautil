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

package org.clapper.util.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import java.net.MalformedURLException;
import java.net.URL;

import org.clapper.util.text.TextUtil;
import org.clapper.util.text.UnixShellVariableSubstituter;
import org.clapper.util.text.VariableDereferencer;
import org.clapper.util.text.VariableNameChecker;
import org.clapper.util.text.VariableSubstitutionException;
import org.clapper.util.text.VariableSubstituter;
import org.clapper.util.text.XStringBuffer;

import org.clapper.util.io.FileUtil;

/**
 * <p><tt>Configuration</tt> implements a parser, generator and in-memory
 * store for a configuration file whose syntax is reminiscent of classic
 * Windows .INI files, though with many extensions.</p>
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
 * <p>Notes and caveats:</p>
 *
 * <ul>
 *   <li> At least one section is required.
 *   <li> Sections may be empty.
 *   <li> It is an error to have any variable definitions before the first
 *        section header.
 *   <li> The section name "system" is reserved. It doesn't really exist, but
 *        it's used during variable substitution (see below) to substitute from
 *        <tt>System.properties</tt>.
 *   <li> The section name "program" is reserved. It doesn't really exist, but
 *        it's used during variable substitution to substitute certain canned
 *        values, such as the running process's working directory.
 * </ul>
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
 * are case-sensitive and may contain alphanumerics and periods (".").
 * Variable values may contain anything at all. The parser ignores
 * whitespace on either side of the "=" or ":"; that is, leading whitespace
 * in the value is skipped. The way to include leading whitespace in a value is
 * escape the whitespace characters with backslashes. (See below).</p>
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
 * <p>The special section "system" is reserved. It doesn't actually
 * exist in the configuration; however, you can refer to it during variable
 * substitution to pull in values from <tt>System.properties</tt>.
 *
 * <p>For example:</p>
 *
 * <blockquote><pre>
 * [main]
 * installation.directory=${system:user.home}/this_package
 * program.directory: ${installation.directory}/foo/programs
 *
 * [search]
 * searchCommand: find ${main:installation.directory} -type f -name '*.class'
 *
 * [display]
 * searchFailedMessage=Search failed, sorry.
 * </pre></blockquote>
 *
 * <p>The special section "program" is also reserved. Like "system",
 * "program" doesn't really exist, either. It's a placeholder for various
 * special variables provided by the <tt>Configuration</tt> class. Those
 * variables are:</p>
 *
 * <ul>
 *   <li> <tt>cwd</tt>: the program's current working directory. Thus,
 *        <tt>${program:cwd}</tt> will substitute the working directory,
 *        with the appropriate system-specific file separator. On a Windows
 *        system, the file separator character (a backslash) will be doubled,
 *        to ensure that it is properly interpreted by the configuration file
 *        parsing logic.
 *   <li> <tt>cwdURL</tt>: the program's current working directory as a
 *        <tt>file</tt> URL, without the trailing "/". Useful when you need
 *        to create a URL reference to something relative to the current
 *        directory. This is especially useful on Windows, where
 *        <blockquote><pre>file://${program:cwd}/something.txt</pre></blockquote>
 *         produces an invalid URL, with a mixture of backslashes and
 *         forward slashes.  By contrast,
 *         <blockquote><pre>${program:cwdURL}/something.txt</pre></blockquote>
 *         always produces a valid URL, regardless of the underlying host
 *         operating system.
 * </ul>
 *
 * <p>Notes and caveats:</p>
 *
 * <ul>
 *   <li> <tt>Configuration</tt> uses the
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
 *
 *   <li> To include a literal "$" character in a variable value, escape
 *        it with a backslash, e.g., "<tt>var=value with \$ dollar sign</tt>"
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
 * <tt>Configuration</tt> recognizes a variable syntax that is
 * essentially identical to Java's properties file syntax, it's also legal
 * to include a properties file, provided it's included within a valid
 * section.</p>
 *
 * <p>Note: Attempting to include a file from itself, either directly or
 * indirectly, will cause the parser to throw an exception.</p>
 * 
 * <h4>Comments and Blank Lines</h4>
 *
 * <p>A comment line is a one whose first non-whitespace character is a "#"
 * or a "!". This comment syntax is identical to the one supported by a
 * Java properties file. A blank line is a line containing no content, or
 * one containing only whitespace. Blank lines and comments are ignored.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public class Configuration
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
    private static final String SYSTEM_SECTION_NAME        = "system";
    private static final String PROGRAM_SECTION_NAME       = "program";

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
        String cookedValue;
        String rawValue;
        int    lineWhereDefined = 0; // 0 means unknown

        Variable (String name, String value, int lineWhereDefined)
        {
            this.name = name;
            this.lineWhereDefined = lineWhereDefined;
            setValue (value);
        }

        Variable (String name, String value)
        {
            this.name = name;
            setValue (value);
        }

        void setValue (String value)
        {
            this.rawValue = value;
            this.cookedValue = value;            
        }

        int lineWhereDefined()
        {
            return this.lineWhereDefined;
        }

        void setLineWhereDefined (int lineNumber)
        {
            this.lineWhereDefined = lineNumber;
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

        Variable addVariable (String varName, String value)
        {
            Variable variable = new Variable (varName, value);
            valueMap.put (varName, variable);
            variableNames.add (varName);
            return variable;
        }

        Variable addVariable (String varName, String value, int lineDefined)
        {
            Variable variable = addVariable (varName, value);
            variable.setLineWhereDefined (lineDefined);
            return variable;
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
        private Variable currentVariable = null;

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
     * The URL of the configuration file, if available
     */
    private URL configURL = null;

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
     * Special section for System.properties
     */
    private Section systemSection;

    /**
     * Data used during parsing. Null when parsing isn't being done.
     */
    private ParseData parseData = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct an empty <tt>Configuration</tt> object. The object may
     * later be filled with configuration data via one of the <tt>load()</tt>
     * methods, or by calls to {@link #addSection addSection()} and
     * {@link #setVariable setVariable()}.
     */
    public Configuration()
    {
    }

    /**
     * Construct a <tt>Configuration</tt> object that parses data from
     * the specified file.
     *
     * @param f  The <tt>File</tt> to open and parse
     *
     * @throws IOException             can't open or read file
     * @throws ConfigurationException  error in configuration data
     */
    public Configuration (File f)
        throws IOException,
               ConfigurationException
    {
        load (f);
    }

    /**
     * Construct a <tt>Configuration</tt> object that parses data from
     * the specified file.
     *
     * @param path  the path to the file to parse
     *
     * @throws FileNotFoundException   specified file doesn't exist
     * @throws IOException             can't open or read file
     * @throws ConfigurationException  error in configuration data
     */
    public Configuration (String path)
        throws FileNotFoundException,
               IOException,
               ConfigurationException
    {
        load (path);
    }

    /**
     * Construct a <tt>Configuration</tt> object that parses data from
     * the specified URL.
     *
     * @param url  the URL to open and parse
     *
     * @throws IOException             can't open or read URL
     * @throws ConfigurationException  error in configuration data
     */
    public Configuration (URL url)
        throws IOException,
               ConfigurationException
    {
        load (url);
    }

    /**
     * Construct a <tt>Configuration</tt> object that parses data from
     * the specified <tt>InputStream</tt>.
     *
     * @param iStream  the <tt>InputStream</tt>
     *
     * @throws IOException             can't read from <tt>InputStream</tt>
     * @throws ConfigurationException  error in configuration data
     */
    public Configuration (InputStream iStream)
        throws IOException,
               ConfigurationException
    {
        load (iStream);
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add a new section to this configuration data.
     *
     * @param sectionName  the name of the new section
     *
     * @throws SectionExistsException a section by that name already exists
     *
     * @see #containsSection
     * @see #getSectionNames
     * @see #setVariable
     */
    public void addSection (String sectionName)
        throws SectionExistsException
    {
        if (sectionsByName.get (sectionName) != null)
            throw new SectionExistsException (sectionName);

        makeNewSection (sectionName);
    }

    /**
     * Clear this object of all configuration data.
     */
    public void clear()
    {
        sectionsInOrder.clear();
        sectionsByName.clear();
        configURL = null;
    }

    /**
     * Determine whether this object contains a specified section.
     *
     * @param sectionName  the section name
     *
     * @return <tt>true</tt> if the section exists in this configuration,
     *         <tt>false</tt> if not.
     *
     * @see #getSectionNames
     * @see #addSection
     */
    public boolean containsSection (String sectionName)
    {
        return (sectionsByName.get (sectionName) != null);
    }

    /**
     * Get the URL of the configuration file, if available.
     *
     * @return the URL of the configuration file, or null if the file
     *         was parsed from an <tt>InputStream</tt>
     */
    public URL getConfigurationFileURL()
    {
        return configURL;
    }

    /**
     * Get the names of the sections in this object, in the order they were
     * parsed and/or added.
     *
     * @param collection  the <tt>Collection</tt> to which to add the section
     *                    names. The names are added in the order they were
     *                    parsed and/or added to this object; of course, the
     *                    <tt>Collection</tt> may reorder them.
     *
     * @return the <tt>collection</tt> parameter, for convenience
     *
     * @see #getVariableNames
     */
    public Collection getSectionNames (Collection collection)
    {
        for (Iterator it = sectionsInOrder.iterator(); it.hasNext(); )
            collection.add (((Section) it.next()).name);

        return collection;
    }
    
    /**
     * Get the names of the all the variables in a section, in the order
     * they were parsed and/or added.
     *
     * @param sectionName the name of the section to access
     * @param collection  the <tt>Collection</tt> to which to add the variable
     *                    names. The names are added in the order they were
     *                    parsed and/or added to this object; of course, the
     *                    <tt>Collection</tt> may reorder them.
     *
     * @return the <tt>collection</tt> parameter, for convenience
     *
     * @throws NoSuchSectionException  no such section
     *
     * @see #getSectionNames
     * @see #containsSection
     * @see #getVariableValue
     */
    public Collection getVariableNames (String     sectionName,
                                        Collection collection)
        throws NoSuchSectionException
    {
        Section section = (Section) sectionsByName.get (sectionName);
        if (section == null)
            throw new NoSuchSectionException (sectionName);

        collection.addAll (section.variableNames);

        return collection;
    }
    
    /**
     * Get the value for a variable.
     *
     * @param sectionName   the name of the section containing the variable
     * @param variableName  the variable name
     *
     * @return the value for the variable (which may be the empty string)
     *
     * @throws NoSuchSectionException  the named section does not exist
     * @throws NoSuchVariableException the section has no such variable
     */
    public String getVariableValue (String sectionName, String variableName)
        throws NoSuchSectionException,
               NoSuchVariableException
    {
        Section section = (Section) sectionsByName.get (sectionName);
        if (section == null)
            throw new NoSuchSectionException (sectionName);

        Variable variable = (Variable) section.getVariable (variableName);
        if (variable == null)
            throw new NoSuchVariableException (sectionName, variableName);

        return variable.cookedValue;
    }

    /**
     * Convenience method to get and convert an optional integer parameter.
     * The default value applies if the variable is missing or is there but
     * has an empty value.
     *
     * @param sectionName   section name
     * @param variableName  variable name
     * @param defaultValue  default value if not found
     *
     * @return the value, or the default value if not found
     *
     * @throws NoSuchSectionException no such section
     * @throws ConfigurationException bad numeric value
     */
    public int getOptionalIntegerValue (String sectionName,
                                        String variableName,
                                        int    defaultValue)
        throws NoSuchSectionException,
               ConfigurationException
    {
        try
        {
            return getRequiredIntegerValue (sectionName, variableName);
        }

        catch (NoSuchVariableException ex)
        {
            return defaultValue;
        }
    }

    /**
     * Convenience method to get and convert a required integer parameter.
     *
     * @param sectionName   section name
     * @param variableName  variable name
     *
     * @return the value
     *
     * @throws NoSuchSectionException  no such section
     * @throws NoSuchVariableException no such variable
     * @throws ConfigurationException  bad numeric value
     */
    public int getRequiredIntegerValue (String sectionName,
                                        String variableName)
        throws NoSuchSectionException,
               NoSuchVariableException,
               ConfigurationException
    {
        String sNum = getVariableValue (sectionName, variableName);

        try
        {
            return Integer.parseInt (sNum);
        }

        catch (NumberFormatException ex)
        {
            throw new ConfigurationException ("Bad numeric value \""
                                            + sNum
                                            + "\" for variable \""
                                            + variableName
                                            + "\" in section \""
                                            + sectionName
                                            + "\"");
        }
    }

    /**
     * Convenience method to get and convert an optional floating point
     * numeric parameter. The default value applies if the variable is
     * missing or is there but has an empty value.
     *
     * @param sectionName   section name
     * @param variableName  variable name
     * @param defaultValue  default value if not found
     *
     * @return the value, or the default value if not found
     *
     * @throws NoSuchSectionException no such section
     * @throws ConfigurationException bad numeric value
     */
    public double getOptionalDoubleValue (String sectionName,
                                          String variableName,
                                          double defaultValue)
        throws NoSuchSectionException,
               ConfigurationException
    {
        try
        {
            return getRequiredDoubleValue (sectionName, variableName);
        }

        catch (NoSuchVariableException ex)
        {
            return defaultValue;
        }
    }

    /**
     * Convenience method to get and convert a required floating point
     * numeric parameter.
     *
     * @param sectionName   section name
     * @param variableName  variable name
     *
     * @return the value
     *
     * @throws NoSuchSectionException  no such section
     * @throws NoSuchVariableException no such variable
     * @throws ConfigurationException  bad numeric value
     */
    public double getRequiredDoubleValue (String sectionName,
                                          String variableName)
        throws NoSuchSectionException,
               NoSuchVariableException,
               ConfigurationException
    {
        String sNum = getVariableValue (sectionName, variableName);

        try
        {
            return Double.parseDouble (sNum);
        }

        catch (NumberFormatException ex)
        {
            throw new ConfigurationException ("Bad floating point value \""
                                            + sNum
                                            + "\" for variable \""
                                            + variableName
                                            + "\" in section \""
                                            + sectionName
                                            + "\"");
        }
    }

    /**
     * Convenience method to get and convert an optional boolean parameter.
     * The default value applies if the variable is missing or is there
     * but has an empty value.
     *
     * @param sectionName   section name
     * @param variableName  variable name
     * @param defaultValue  default value if not found
     *
     * @return the value, or the default value if not found
     *
     * @throws NoSuchSectionException no such section
     * @throws ConfigurationException bad numeric value
     */
    public boolean getOptionalBooleanValue (String  sectionName,
                                            String  variableName,
                                            boolean defaultValue)
        throws NoSuchSectionException,
               ConfigurationException
    {
        boolean result = defaultValue;

        try
        {
            String s = getVariableValue (sectionName, variableName);

            if (s.trim().length() == 0)
                result = defaultValue;
            else
                result = TextUtil.booleanFromString (s);
        }

        catch (NoSuchVariableException ex)
        {
            result = defaultValue;
        }

        catch (IllegalArgumentException ex)
        {
            throw new ConfigurationException (ex.getMessage());
        }

        return result;
    }

    /**
     * Convenience method to get and convert a required boolean parameter.
     *
     * @param sectionName   section name
     * @param variableName  variable name
     *
     * @return the value
     *
     * @throws NoSuchSectionException  no such section
     * @throws NoSuchVariableException no such variable
     * @throws ConfigurationException  bad numeric value
     */
    public boolean getRequiredBooleanValue (String sectionName,
                                            String variableName)
        throws NoSuchSectionException,
               ConfigurationException,
               NoSuchVariableException
    {
        return Boolean.valueOf (getVariableValue (sectionName, variableName))
                      .booleanValue();
    }

    /**
     * Convenience method to get an optional string value. The default
     * value applies if the variable is missing or is there but has an
     * empty value.
     *
     * @param sectionName   section name
     * @param variableName  variable name
     * @param defaultValue  default value if not found
     *
     * @return the value, or the default value if not found
     *
     * @throws NoSuchSectionException no such section
     * @throws ConfigurationException bad numeric value
     */
    public String getOptionalStringValue (String sectionName,
                                          String variableName,
                                          String defaultValue)
        throws NoSuchSectionException,
               ConfigurationException
    {
        String result;

        try
        {
            result = getVariableValue (sectionName, variableName);
            if (result.trim().length() == 0)
                result = defaultValue;
        }

        catch (NoSuchVariableException ex)
        {
            result = defaultValue;
        }

        return result;
    }

    /**
     * Get the value associated with a given variable. Required by the
     * {@link VariableDereferencer} interface, this method is used during
     * parsing to handle variable substitutions (but also potentially
     * useful by other applications). See this class's documentation for
     * details on variable references.
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
        if (parseData.currentVariable.name.equals (varName))
        {
            throw new VariableSubstitutionException ("Attempt to substitute "
                                                   + "value for variable \""
                                                   + varName
                                                   + "\" within itself.");
        }

        int      i;
        Section  section = null;
        String   sectionName;
        String   value = null;

        i = varName.indexOf (':');
        if (i == -1)
        {
            section = parseData.currentSection;
        }

        else
        {
            sectionName = varName.substring (0, i);
            varName = varName.substring (i + 1);

            if (sectionName.equals (SYSTEM_SECTION_NAME))
                section = systemSection;

            else if (sectionName.equals (PROGRAM_SECTION_NAME))
                value = getProgramSectionValue (varName);

            else
                section = (Section) sectionsByName.get (sectionName);
        }

        if (section != null)
        {
            Variable var = (Variable) section.getVariable (varName);
            if (var != null)
                value = var.cookedValue;
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

    /**
     * Load configuration from a <tt>File</tt>. Any existing data is
     * discarded.
     *
     * @param file  the file
     *
     * @throws IOException            read error
     * @throws ConfigurationException parse error
     */
    public void load (File file)
        throws IOException,
               ConfigurationException
    {
        clear();
        URL url = file.toURL();
        parse (new FileInputStream (file), url);
        this.configURL = url;
    }

    /**
     * Load configuration from a file specified as a pathname. Any existing
     * data is discarded.
     *
     * @param path  the path
     *
     * @throws FileNotFoundException   specified file doesn't exist
     * @throws IOException             can't open or read file
     * @throws ConfigurationException  error in configuration data
     */
    public void load (String path)
        throws FileNotFoundException,
               IOException,
               ConfigurationException
    {
        clear();
        URL url = new File (path).toURL();
        parse (new FileInputStream (path), url);
        this.configURL = url;
    }

    /**
     * Load configuration from a URL. Any existing data is discarded.
     *
     * @param url  the URL
     *
     * @throws IOException            read error
     * @throws ConfigurationException parse error
     */
    public void load (URL url)
        throws IOException,
               ConfigurationException
    {
        clear();
        parse (url.openStream(), url);
        this.configURL = url;
    }

    /**
     * Load configuration from an <tt>InputStream</tt>. Any existing data
     * is discarded.
     *
     * @param iStream  the <tt>InputStream</tt>
     *
     * @throws IOException             can't open or read URL
     * @throws ConfigurationException  error in configuration data
     */
    public void load (InputStream iStream)
        throws IOException,
               ConfigurationException
    {
        clear();
        parse (iStream, null);
    }

    /**
     * Set a variable's value. If the variable does not exist, it is created.
     * If it does exist, its current value is overwritten with the new one.
     * Metacharacters and variable references are not expanded unless the
     * <tt>expand</tt> parameter is <tt>true</tt>. An <tt>expand</tt> value
     * of <tt>false</tt> is useful when creating new configuration data to
     * be written later.
     *
     * @param sectionName  name of existing section to contain the variable
     * @param variableName name of variable to set
     * @param value        variable's value
     * @param expand       <tt>true</tt> to expand metacharacters and variable
     *                     references in the value, <tt>false</tt> to leave
     *                     the value untouched.
     *
     * @throws NoSuchSectionException        section does not exist
     * @throws VariableSubstitutionException variable substitution error
     */
    public void setVariable (String  sectionName,
                             String  variableName,
                             String  value,
                             boolean expand)
        throws NoSuchSectionException,
               VariableSubstitutionException
    {
        Section section = (Section) sectionsByName.get (sectionName);
        if (section == null)
            throw new NoSuchSectionException (sectionName);

        Variable variable = (Variable) section.getVariable (variableName);
        if (variable != null)
            variable.setValue (value);
        else
            variable = section.addVariable (variableName, value);

        if (expand)
        {
            try
            {
                // substituteVariables() requires that the parseData
                // instance variable be non-null and have valid values for
                // currentSection and currentVariable.

                parseData = new ParseData();
                parseData.currentSection  = section;
                substituteVariables (variable,
                                     new UnixShellVariableSubstituter());
            }

            finally
            {
                parseData = null;
            }
        }
    }
    
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

        out.print (COMMENT_CHARS.charAt (0));
        out.print (" Written by ");
        out.println (this.getClass().getName());
        out.print (COMMENT_CHARS.charAt (0));
        out.print (" on ");
        out.println (new Date().toString());
        out.println();

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
                value.append (var.cookedValue);
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

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

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

        // Now, create the phantom system section.

        loadSystemSection();
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
        String               varName;
        Section              section;
        XStringBuffer        buf = new XStringBuffer();

        // First, expand the the metacharacter sequences.

        for (itSect = sectionsInOrder.iterator(); itSect.hasNext(); )
        {
            parseData.currentSection = (Section) itSect.next();

            for (itVar = parseData.currentSection.variableNames.iterator();
                 itVar.hasNext(); )
            {
                varName = (String) itVar.next();
                section = parseData.currentSection;
                parseData.currentVariable = section.getVariable (varName);

                buf.setLength (0);
                buf.append (parseData.currentVariable.cookedValue);
                buf.decodeMetacharacters();
                parseData.currentVariable.cookedValue = buf.toString();
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
                varName = (String) itVar.next();
                section = parseData.currentSection;
                parseData.currentVariable = section.getVariable (varName);

                try
                {
                    substituteVariables (parseData.currentVariable,
                                         substituter);
                }

                catch (VariableSubstitutionException ex)
                {
                    throw new ConfigurationException (ex);
                }
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

        return makeNewSection (s.substring (1, s.length() - 1));
    }

    /**
     * Handle a new variable during parsing.
     *
     * @param line  line buffer
     * @param url   URL currently being processed, or null if unknown
     *
     * @throws ConfigurationException  configuration error
     */
    private void handleVariable (Line line, URL url)
        throws ConfigurationException
    {
        char[] s = line.buffer.toString().toCharArray();
        int    iSep;

        for (iSep = 0; iSep < s.length; iSep++)
        {
            if ((s[iSep] == ':') || (s[iSep] == '='))
                break;
        }

        if (iSep == s.length)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Missing '=' or ':' for "
                                            + "variable definition.");
        }

        if (iSep == 0)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Missing variable name for "
                                            + "variable definition.");
        }

        int i = 0;
        int j = iSep - 1;
        while (Character.isWhitespace (s[i]))
            i++;

        while (Character.isWhitespace (s[j]))
            j--;

        if (i >= j)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Missing variable name for "
                                            + "variable definition.");
        }

        String varName = new String (s, i, j - i + 1);
        if (varName.length() == 0)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Missing variable name for "
                                            + "variable definition.");
        }

        i = skipWhitespace (s, iSep + 1);
        j = s.length - i;
        String value = new String (s, i, j);
        Variable existing = parseData.currentSection.getVariable (varName);
        if (existing != null)
        {
            throw new ConfigurationException (getExceptionPrefix (line, url)
                                            + "Section \""
                                            + parseData.currentSection.name
                                            + "\": Duplicate definition of "
                                            + "variable \""
                                            + varName
                                            + "\". First instance was defined "
                                            + "on line "
                                            + existing.lineWhereDefined());
        }

        parseData.currentSection.addVariable (varName, value, line.number);
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

            if (FileUtil.isAbsolutePath (includeTarget))
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
     * Load the phantom "system" section from System.properties.
     */
    private void loadSystemSection()
    {
        Properties systemProperties = System.getProperties();

        systemSection = new Section (SYSTEM_SECTION_NAME);
        for (Enumeration e = systemProperties.propertyNames();
             e.hasMoreElements(); )
        {
            String name = (String) e.nextElement();
            systemSection.addVariable (name,
                                       systemProperties.getProperty (name));
        }
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
     * Handle variable substitution for a variable value. NOTE: Requires
     * that the "parseData" instance be non-null, and its "currentSection"
     * item be appropriately set. (This method sets "currentVariable".)
     *
     * @param var         The variable to expand
     * @param substituter VariableSubstituter to use
     *
     * @return the expanded result
     *
     * @throws VariableSubstitutionException variable substitution error
     */
    private void substituteVariables (Variable            var,
                                      VariableSubstituter substituter)
        throws VariableSubstitutionException
    {
        // Keep substituting the current variable's value until there no
        // more substitutions are performed. This handles the case where a
        // dereferenced variable value contains its own variable
        // references.

        parseData.currentVariable = var;
        do
        {
            parseData.totalSubstitutions = 0;
            var.cookedValue = substituter.substitute (var.cookedValue,
                                                      this,
                                                      this);
        }
        while (parseData.totalSubstitutions > 0);
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

    /**
     * Create and save a new Section.
     *
     * @param sectionName the name
     *
     * @return the Section object, which has been saved.
     */
    private Section makeNewSection (String sectionName)
    {
        Section section = new Section (sectionName);
        sectionsInOrder.add (section);
        sectionsByName.put (sectionName, section);

        return section;
    }

    /**
     * Get a value from the phantom "program" section.
     *
     * @param varName the variable name
     *
     * @return the value, or "" if not available or nonexistent variable
     *
     * @throws VariableSubstitutionException error getting value
     */
    private String getProgramSectionValue (String varName)
        throws VariableSubstitutionException
    {
        String value = "";

        try
        {
            if (varName.equals ("cwd"))
            {
                File dir = new File (".");

                value = dir.getCanonicalPath();
            }

            else if (varName.equals ("cwdURL"))
            {
                File dir = new File (".");

                value = dir.getCanonicalFile().toURL().toString();
                if (value.charAt (value.length() - 1) == '/')
                    value = value.substring (0, value.length() - 1);
            }
        }

        catch (IOException ex)
        {
            throw new VariableSubstitutionException (ex);
        }

        return value;
    }
}
