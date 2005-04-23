/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2005 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.cmdline;

import org.clapper.util.io.WordWrapWriter;

import org.clapper.util.logging.Logger;
import org.clapper.util.misc.ArrayIterator;
import org.clapper.util.misc.BundleUtil;
import org.clapper.util.text.TextUtil;

import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * <p><tt>CommandLineUtility</tt> is an abstract base class for
 * command-line utilities. It provides:</p>
 *
 * <ul>
 *   <li>Parameter-parsing logic, with call-outs for custom parameters
 *   <li>Built-in support for a --logging parameter, which enables logging
 *       via the <tt>java.util.logging</tt> API, by calling
 *       {@link org.clapper.util.logging.Logger#enableLogging()}
 *   <li>Automatic generation of a usage message, with a call-out that
 *       permits subclasses to add subclass-specific usage information.
 *   <li>Automatic reporting of exceptions
 * </ul>
 *
 * To use this class, subclass it, and have the subclass's <tt>main()</tt>
 * method instantiate the subclass, and then call the resulting object's
 * {@link #execute(String[]) execute()} method. The <tt>execute()</tt> method
 * (which resides in this base class) will:</p>
 *
 * <ul>
 *   <li>parse the parameters
 *   <li>call the {@link #runCommand()} method, which must be provided by
 *       the subclass, to initiate processing
 * </ul>
 *
 * <p>Note that this class does <b>not</b> parse options the way GNU
 * <i>getopt()</i> (or even traditional <i>getopt()</i>) does. In particular,
 * it does not permit combining multiple single-character options into one
 * command-line parameter. <tt>CommandLineUtility</tt> may be extended to
 * support that capability in the future; however, it doesn't do that yet.</p>
 *
 * <p>Here's a sample subclass. It takes the usual <tt>--logging</tt>
 * parameter, plus a <tt>-v</tt> (verbose) flag, a numeric count
 * (<tt>-n</tt>) and a file name. (Exactly what it does with those
 * parameters is left as an exercise for the reader.)</p>
 *
 * <blockquote>
 * <pre>
 * public class Foo extends CommandLineUtility
 * {
 *     private boolean verbose  = false;
 *     private int     count    = 1;
 *     private String  filename = null;
 *
 *     public static void main (String args[])
 *     {
 *         try
 *         {
 *             Foo foo = new Foo();
 *             foo.execute (args);
 *         }
 * 
 *         catch (CommandLineUsageException ex)
 *         {
 *             // Already reported
 * 
 *             System.exit (1);
 *         }
 * 
 *         catch (CommandLineException ex)
 *         {
 *             System.err.println (ex.getMessage());
 *             System.exit (1);
 *         }
 * 
 *         System.exit (0);
 *     }
 *
 *     private Foo()
 *     {
 *         super();
 *     }
 *
 *     protected void runCommand() throws CommandLineUtilityException
 *     {
 *         ...
 *     }
 *
 *     protected void parseCustomOption (char shortOption,
 *                                       String longOption,
 *                                       Iterator<String> it)
 *         throws CommandLineUsageException,
 *                NoSuchElementException
 *     {
 *         if (longOption.equals ("verbose") || (shortOption == 'v'))
 *             verbose = true;
 *
 *         else if (longOption.equals ("count") || (shortOption == 'n'))
 *             count = parseIntParameter ((String) it.next());
 *
 *         else
 *             throw new CommandLineUsageException ("Unknown option: " + option);
 *     }
 *
 *     protected void processPostOptionCommandLine (Iterator<String> it)
 *         throws CommandLineUsageException,
 *                NoSuchElementException
 *     {
 *         filename = it.next();
 *     }
 *
 *     protected void getCustomUsageInfo (UsageInfo info)
 *     {
 *         info.addOption ('v', "verbose", null, "Enable verbose messages")
 *         info.addOption ('n', "count", "total", "Read specified file <total> times. Defaults to 1.");
 *         info.addParameter ("filename", "File to process.", false);
 *     }
 * }
 * </pre>
 * </blockquote>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004 Brian M. Clapper
 */
public abstract class CommandLineUtility
{
    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    /**
     * Maximum length of option string (see usage()) that can be concatenated
     * with first line of option's explanation. Strings longer than this are
     * printed on a line by themselves.
     */
    private static final int MAX_OPTION_STRING_LENGTH = 35;

    /*----------------------------------------------------------------------*\
                           Private Data Elements
    \*----------------------------------------------------------------------*/

    private static Logger log  = new Logger (CommandLineUtility.class);

    private UsageInfo usageInfo = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Constructor. Initializes this base class.
     */
    protected CommandLineUtility()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Called to initiate execution of the command line utility. This
     * method
     *
     * <ul>
     *   <li>parse the parameters
     *   <li>call the {@link #runCommand()} method, which must be provided by
     *       the subclass, to initiate processing.
     * </ul>
     *
     * @param args  The command-line parameters
     *
     * @throws CommandLineUtilityException  command failed
     */
    public final void execute (String[] args)
        throws CommandLineException
    {
        boolean ok = true;

        try
        {
            usageInfo = getUsageInfo();
            parseParams (args);
            runCommand();
        }

        catch (CommandLineUsageException ex)
        {
            usage (ex.getMessage());
            throw ex;
        }

        catch (CommandLineException ex)
        {
            throw ex;
        }

        catch (Exception ex)
        {
            throw new CommandLineException (ex);
        }
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    /**
     * Called by <tt>parseParams()</tt> to handle any option it doesn't
     * recognize. If the option takes any parameters, the overridden
     * method must extract the parameter by advancing the supplied
     * <tt>Iterator</tt> (which returns <tt>String</tt> objects). This
     * default method simply throws an exception.
     *
     * @param shortOption  the short option character, or
     *                     {@link UsageInfo#NO_SHORT_OPTION} if there isn't
     *                     one (i.e., if this is a long-only option).
     * @param longOption   the long option string, without any leading
     *                     "-" characters, or null if this is a short-only
     *                     option
     * @param it           the <tt>Iterator</tt> for the remainder of the
     *                     command line, for extracting parameters.
     *
     * @throws CommandLineUsageException  on error
     * @throws NoSuchElementException     overran the iterator (i.e., missing
     *                                    parameter) 
     */
    protected void parseCustomOption (char             shortOption,
                                      String           longOption,
                                      Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        throw new CommandLineUsageException
                                   (Package.BUNDLE_NAME,
                                    "CommandLineUtility.parseCustomOption",
                                    "(BUG) custom option found, but class {0} "
                                  + "provides no parseCustomOption method.",
                                    new Object[] {this.getClass().getName()});
    }

    /**
     * <p>Called by <tt>parseParams()</tt> once option parsing is complete,
     * this method must handle any additional parameters on the command
     * line. It's not necessary for the method to ensure that the iterator
     * has the right number of strings left in it. If you attempt to pull
     * too many parameters from the iterator, it'll throw a
     * <tt>NoSuchElementException</tt>, which <tt>parseParams()</tt> traps
     * and converts into a suitable error message. Similarly, if there are
     * any parameters left in the iterator when this method returns,
     * <tt>parseParams()</tt> throws an exception indicating that there are
     * too many parameters on the command line.</p>
     *
     * <p>This method is called unconditionally, even if there are no
     * parameters left on the command line, so it's a useful place to do
     * post-option consistency checks, as well.</p>
     *
     * @param it   the <tt>Iterator</tt> for the remainder of the
     *             command line
     *
     * @throws CommandLineUsageException  on error
     * @throws NoSuchElementException     attempt to iterate past end of args;
     *                                    <tt>parseParams()</tt> automatically
     *                                    handles this exception, so it's
     *                                    safe for subclass implementations of
     *                                    this method not to handle it
     */
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        if (it.hasNext())
        {
            throw new CommandLineUsageException
                             (Package.BUNDLE_NAME,
                              "CommandLineUtility.extraParams",
                              "Extra command line parameter(s).");
        }
    }

    /**
     * Called by <tt>parseParams()</tt> to get the custom command-line
     * options and parameters handled by the subclass. This list is used
     * solely to build a usage message. The overridden method must fill the
     * supplied <tt>UsageInfo</tt> object:
     *
     * <ul>
     *   <li> Each parameter must be added to the object, via the
     *        {@link UsageInfo#addParameter UsageInfo.addParameter()} method.
     *
     *   <li> Each option must be added to the object, via the
     *        {@link UsageInfo#addOption UsageInfo.addOption()} method.
     * </ul>
     *
     * That information will be combined with the common options supported
     * by the base class, and used to build a usage message.
     *
     * @param info   The <tt>UsageInfo</tt> object to fill.
     *
     * @see UsageInfo#addOption
     * @see UsageInfo#addParameter
     */
    protected void getCustomUsageInfo (UsageInfo info)
    {
    }

    /**
     * Actually runs the command. All subclasses are required to provide this
     * method.
     *
     * @throws CommandLineException  on error
     */
    protected abstract void runCommand() throws CommandLineException;

    /**
     * Convenience method that parses an integer value, throwing a
     * <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>,
     * on error.
     *
     * @param value the string value to parse
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseIntParameter(String,int,int)
     * @see #parseIntOptionArgument(char,String,String)
     * @see #parseDoubleParameter(String)
     * @see #parseFloatParameter(String)
     */
    protected int parseIntParameter (String value)
        throws CommandLineUsageException
    {
        try
        {
            return Integer.parseInt (value);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.badIntegerParam",
                             "Bad integer command line parameter \"{0}\"",
                             new Object[] {value});
        }
    }

    /**
     * Convenience method that parses an integer value, throwing a
     * <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error. This version of the method
     * also does range checking.
     *
     * @param value the string value to parse
     * @param min   the minimum legal value for the result
     * @param max   the maximum legal value for the result
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseIntParameter(String)
     * @see #parseIntOptionArgument(char,String,String,int,int)
     * @see #parseDoubleParameter(String,double,double)
     * @see #parseFloatParameter(String,float,float)
     */
    protected int parseIntParameter (String value, int min, int max)
        throws CommandLineUsageException
    {
        int result = parseIntParameter (value);

        if (result < min)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericParamTooSmall",
                             "Numeric parameter {0} is less than the "
                           + "minimum legal value of {1}",
                             new Object[]
                             {
                                 value,
                                 String.valueOf (min)
                             });
        }

        if (result > max)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericParamTooLarge",
                             "Numeric parameter {0} is greater than the "
                           + "maximum legal value of {1}",
                             new Object[]
                             {
                                 value,
                                 String.valueOf (max)
                             });
        }

        return result;
    }

    /**
     * Convenience method that parses an integer option argument, throwing
     * a <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error.
     *
     * @param shortOption short option, as passed to {@link #parseCustomOption}
     * @param longOption  long option, as passed to {@link #parseCustomOption}
     * @param value       the string value to parse
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseIntOptionArgument(char,String,String,int,int)
     * @see #parseDoubleOptionArgument(char,String,String)
     * @see #parseFloatOptionArgument(char,String,String)
     */
    protected int parseIntOptionArgument (char   shortOption,
                                          String longOption,
                                          String value)
        throws CommandLineUsageException
    {
        try
        {
            return Integer.parseInt (value);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.badIntegerOption",
                             "Bad integer command line parameter \"{0}\" for "
                           + "option {1}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption)
                             });
        }
    }

    /**
     * Convenience method that parses an integer option argument, throwing
     * a <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error. This version of the method
     * also does range checking.
     *
     * @param shortOption short option, as passed to {@link #parseCustomOption}
     * @param longOption  long option, as passed to {@link #parseCustomOption}
     * @param value       the string value to parse
     * @param min         the minimum legal value for the result
     * @param max         the maximum legal value for the result
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseIntOptionArgument(char,String,String,int,int)
     * @see #parseDoubleOptionArgument(char,String,String)
     * @see #parseFloatOptionArgument(char,String,String)
     */
    protected int parseIntOptionArgument (char   shortOption,
                                          String longOption,
                                          String value,
                                          int    min,
                                          int    max)
        throws CommandLineUsageException
    {
        int result = parseIntOptionArgument (shortOption, longOption, value);

        if (result < min)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericOptionParamTooSmall",
                             "Numeric parameter {0} for option {1} is less "
                           + "than the minimum legal value of {2}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption),
                                 String.valueOf (min)
                             });
        }

        if (result > max)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericOptionParamTooLarge",
                             "Numeric parameter {0} for option {1} is greater "
                           + "than the maximum legal value of {2}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption),
                                 String.valueOf (max)
                             });
        }

        return result;
    }

    /**
     * Convenience method that parses a floating point value, throwing a
     * <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error.
     *
     * @param value the string value to parse
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseFloatParameter(String,float,float)
     * @see #parseFloatOptionArgument(char,String,String)
     * @see #parseDoubleParameter(String)
     * @see #parseIntParameter(String)
     */
    protected float parseFloatParameter (String value)
        throws CommandLineUsageException
    {
        try
        {
            return Float.parseFloat (value);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.badFloatParam",
                             "Bad floating point command line parameter "
                           + "\"{0}\"",
                             new Object[] {value});
        }
    }

    /**
     * Convenience method that parses a floating point value, throwing a
     * <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error. This version of the method
     * also does range checking.
     *
     * @param value the string value to parse
     * @param min   the minimum legal value for the result
     * @param max   the maximum legal value for the result
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseFloatParameter(String)
     * @see #parseFloatOptionArgument(char,String,String,float,float)
     * @see #parseDoubleParameter(String,double,double)
     * @see #parseIntParameter(String,int,int)
     */
    protected float parseFloatParameter (String value, float min, float max)
        throws CommandLineUsageException
    {
        float result = parseFloatParameter (value);

        if (result < min)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericParamTooSmall",
                             "Numeric parameter {0} is less than the "
                           + "minimum legal value of {1}",
                             new Object[]
                             {
                                 value,
                                 String.valueOf (min)
                             });
        }

        if (result > max)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericParamTooLarge",
                             "Numeric parameter {0} is greater than the "
                           + "maximum legal value of {1}",
                             new Object[]
                             {
                                 value,
                                 String.valueOf (max)
                             });
        }

        return result;
    }

    /**
     * Convenience method that parses a float point option argument,
     * throwing a <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error.
     *
     * @param shortOption short option, as passed to {@link #parseCustomOption}
     * @param longOption  long option, as passed to {@link #parseCustomOption}
     * @param value       the string value to parse
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseFloatOptionArgument(char,String,String,float,float)
     * @see #parseDoubleOptionArgument(char,String,String)
     * @see #parseIntOptionArgument(char,String,String)
     */
    protected float parseFloatOptionArgument (char   shortOption,
                                              String longOption,
                                              String value)
        throws CommandLineUsageException
    {
        try
        {
            return Integer.parseInt (value);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.badFloatParam",
                             "Bad floating point command line parameter "
                           + "\"{0}\" for option {1}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption)
                             });
        }
    }

    /**
     * Convenience method that parses a floating point option argument,
     * throwing a <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error. This version of the method
     * also does range checking.
     *
     * @param shortOption short option, as passed to {@link #parseCustomOption}
     * @param longOption  long option, as passed to {@link #parseCustomOption}
     * @param value       the string value to parse
     * @param min         the minimum legal value for the result
     * @param max         the maximum legal value for the result
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseFloatOptionArgument(char,String,String,float,float)
     * @see #parseDoubleOptionArgument(char,String,String)
     * @see #parseIntOptionArgument(char,String,String)
     */
    protected float parseFloatOptionArgument (char   shortOption,
                                              String longOption,
                                              String value,
                                              float  min,
                                              float  max)
        throws CommandLineUsageException
    {
        float result = parseFloatOptionArgument (shortOption,
                                                 longOption,
                                                 value);

        if (result < min)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericOptionParamTooSmall",
                             "Numeric parameter {0} for option {1} is less "
                           + "than the minimum legal value of {2}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption),
                                 String.valueOf (min)
                             });
        }

        if (result > max)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericOptionParamTooLarge",
                             "Numeric parameter {0} for option {1} is greater "
                           + "than the maximum legal value of {2}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption),
                                 String.valueOf (max)
                             });
        }

        return result;
    }

    /**
     * Convenience method that parses a double value, throwing a
     * <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error.
     *
     * @param value the string value to parse
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseDoubleParameter(String,double,double)
     * @see #parseDoubleOptionArgument(char,String,String)
     * @see #parseFloatParameter(String)
     * @see #parseIntParameter(String)
     */
    protected double parseDoubleParameter (String value)
        throws CommandLineUsageException
    {
        try
        {
            return Double.parseDouble (value);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.badDoubleParam",
                             "Bad double floating point command line "
                           + "parameter \"{0}\"",
                             new Object[] {value});
        }
    }

    /**
     * Convenience method that parses a double value, throwing a
     * <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error. This version of the method
     * also does range checking.
     *
     * @param value the string value to parse
     * @param min   the minimum legal value for the result
     * @param max   the maximum legal value for the result
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseDoubleParameter(String)
     * @see #parseDoubleOptionArgument(char,String,String,double,double)
     * @see #parseFloatParameter(String,float,float)
     * @see #parseIntParameter(String,int,int)
     */
    protected double parseDoubleParameter (String value,
                                           double  min,
                                           double  max)
        throws CommandLineUsageException
    {
        double result = parseDoubleParameter (value);

        if (result < min)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericParamTooSmall",
                             "Numeric parameter {0} is less than the "
                           + "minimum legal value of {1}",
                             new Object[]
                             {
                                 value,
                                 String.valueOf (min)
                             });
        }

        if (result > max)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericParamTooLarge",
                             "Numeric parameter {0} is greater than the "
                           + "maximum legal value of {1}",
                             new Object[]
                             {
                                 value,
                                 String.valueOf (max)
                             });
        }

        return result;
    }

    /**
     * Convenience method that parses a double option argument, throwing a
     * <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error.
     *
     * @param shortOption short option, as passed to {@link #parseCustomOption}
     * @param longOption  long option, as passed to {@link #parseCustomOption}
     * @param value       the string value to parse
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseDoubleOptionArgument(char,String,String,double,double)
     * @see #parseDoubleOptionArgument(char,String,String)
     * @see #parseIntOptionArgument(char,String,String)
     */
    protected double parseDoubleOptionArgument (char   shortOption,
                                                String longOption,
                                                String value)
        throws CommandLineUsageException
    {
        try
        {
            return Double.parseDouble (value);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.badDoubleOption",
                             "Bad double command line parameter "
                           + "\"{0}\" for option {1}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption)
                             });
        }
    }

    /**
     * Convenience method that parses a double floating point option
     * argument, throwing a <tt>CommandLineUsageException</tt>, not a
     * <tt>NumberFormatException</tt>, on error. This version of the method
     * also does range checking.
     *
     * @param shortOption short option, as passed to {@link #parseCustomOption}
     * @param longOption  long option, as passed to {@link #parseCustomOption}
     * @param value       the string value to parse
     * @param min         the minimum legal value for the result
     * @param max         the maximum legal value for the result
     *
     * @return the numeric value
     *
     * @throws CommandLineUsageException bad numeric value
     *
     * @see #parseDoubleOptionArgument(char,String,String,double,double)
     * @see #parseDoubleOptionArgument(char,String,String)
     * @see #parseIntOptionArgument(char,String,String)
     */
    protected double parseDoubleOptionArgument (char   shortOption,
                                                String longOption,
                                                String value,
                                                double  min,
                                                double  max)
        throws CommandLineUsageException
    {
        double result = parseDoubleOptionArgument (shortOption,
                                                   longOption,
                                                   value);

        if (result < min)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericOptionParamTooSmall",
                             "Numeric parameter {0} for option {1} is less "
                           + "than the minimum legal value of {2}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption),
                                 String.valueOf (min)
                             });
        }

        if (result > max)
        {
            throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.numericOptionParamTooSmall",
                             "Numeric parameter {0} for option {1} is greater "
                           + "than the maximum legal value of {2}",
                             new Object[]
                             {
                                 value,
                                 getOptionStringForError (shortOption,
                                                          longOption),
                                 String.valueOf (max)
                             });
        }

        return result;
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Parse the command-line parameters. This method parses the common
     * options; any other option is passed to the
     * <tt>parseCustomOption()</tt> method, which should throw an exception
     * if the option isn't recognized. When the options have all been
     * satisfied, this method then invokes
     * <tt>processPostOptionCommandLine()</tt>.
     *
     * @param args  the command line parameters
     *
     * @throws CommandLineUsageException command line error
     *
     * @see #processPostOptionCommandLine
     * @see #parseCustomOption
     */
    private void parseParams (String args[])
        throws CommandLineUsageException
    {
        ArrayIterator<String> it = new ArrayIterator<String> (args);

        try
        {
            while (it.hasNext())
            {
                String arg = it.next();

                if (! (arg.charAt (0) == UsageInfo.SHORT_OPTION_PREFIX) )
                {
                    // Move iterator back, since we've already advanced
                    // past the last option and retrieved the first
                    // non-option.

                    it.previous();
                    break;
                }

                // First, verify that the option is legal.

                OptionInfo optionInfo = null;

                if (arg.length() == 2)
                    optionInfo = usageInfo.getOptionInfo (arg.charAt (1));
                else
                {
                    if (! arg.startsWith (UsageInfo.LONG_OPTION_PREFIX))
                    {
                        throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.badLongOption",
                             "Option \"{0}\" is not a single-character short "
                           + "option, but it does not start with \"{1}\", as "
                           + "long options must.",
                             new Object[] {arg, UsageInfo.LONG_OPTION_PREFIX});
                    }

                    optionInfo = usageInfo.getOptionInfo (arg.substring (2));
                }

                if (optionInfo == null)
                {
                    throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.unknownOption",
                             "Unknown option: \"{0}\"",
                             new Object[] {arg});
                }

                // Okay, now handle our options.

                if ((optionInfo.longOption != null) &&
                    (optionInfo.longOption.equals ("logging")))
                {
                    Logger.enableLogging();
                }

                else
                {
                    parseCustomOption (optionInfo.shortOption,
                                       optionInfo.longOption,
                                       it);
                }
            }

            processPostOptionCommandLine (it);

            // Should be no parameters left now.

            if (it.hasNext())
            {
                throw new CommandLineUsageException
                             (Package.BUNDLE_NAME,
                              "CommandLineUtility.tooManyParams",
                              "Too many parameters.");
            }
        }

        catch (NoSuchElementException ex)
        {
            throw new CommandLineUsageException
                             (Package.BUNDLE_NAME,
                              "CommandLineUtility.missingParams",
                              "Missing command line parameter(s).");
        }

        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new CommandLineUsageException
                             (Package.BUNDLE_NAME,
                              "CommandLineUtility.missingParams",
                              "Missing command line parameter(s).");
        }
    }

    /**
     * Print a usage message.
     *
     * @param prefixMsg  a prefix message to display before dumping the
     *                   usage, or null
     */
    private void usage (String prefixMsg)
    {
        WordWrapWriter   out = new WordWrapWriter (System.err);
        String[]         strings;
        int              i;
        int              maxParamLength = 0;
        int              maxOptionLength = 0;
        String           s;
        StringBuffer     usageLine = new StringBuffer();
        OptionInfo[]     options;
        OptionInfo       opt;
        Locale           locale = Locale.getDefault();

        if (prefixMsg != null)
        {
            out.println();
            out.println (prefixMsg);
            out.println();
        }

        // Now, print the summary line.

        usageLine.append ("java ");
        usageLine.append (getClass().getName());
        usageLine.append (' ');
        usageLine.append (BundleUtil.getMessage (Package.BUNDLE_NAME,
                                                 locale,
                                                 "CommandLineUtility.options1",
                                                 "[options]"));
        usageLine.append (" ");

        // Add the parameter placeholders. We'll also calculate the maximum
        // parameter name length in this loop, to save an iteration later.

        strings = usageInfo.getParameterNames();
        if (strings.length > 0)
        {
            for (i = 0; i < strings.length; i++)
            {
                usageLine.append (' ');

                boolean optional = true;
                if (usageInfo.parameterIsRequired (strings[i]))
                    optional = false;

                if (optional)
                    usageLine.append ('[');
                usageLine.append (strings[i]);
                if (optional)
                    usageLine.append (']');
                maxParamLength = Math.max (maxParamLength,
                                           strings[i].length() + 1);
            }
        }

        if ( (s = usageInfo.getUsagePrologue()) != null)
            out.println (s);

        s = BundleUtil.getMessage (Package.BUNDLE_NAME,
                                   locale,
                                   "CommandLineUtility.usage",
                                   "Usage:");
        out.setPrefix (s + " ");
        out.println (usageLine.toString());
        out.setPrefix (null);
        out.println ();

        // Find the largest option name.

        out.println (BundleUtil.getMessage (Package.BUNDLE_NAME,
                                            locale,
                                            "CommandLineUtility.options2",
                                            "OPTIONS:"));
        out.println ();

        maxOptionLength = 2;
        options = usageInfo.getOptions();
        for (i = 0; i < options.length; i++)
        {
            opt = options[i];
            if (opt.longOption != null)
            {
                // Allow room for short option, long option and argument,
                // if any.
                //
                // -x, --long-x <arg>

                int    len = 0;
                String sep = "";
                if (opt.shortOption != UsageInfo.NO_SHORT_OPTION)
                {
                    len = 2;    // -x
                    sep = ", ";
                }

                if (opt.longOption != null)
                {
                    len += (sep.length()
                         + UsageInfo.LONG_OPTION_PREFIX.length()
                         + opt.longOption.length());
                }

                if (opt.argToken != null)
                    len += (opt.argToken.length() + 1);

                maxOptionLength = Math.max (maxOptionLength, len + 1);
            }
        }

        if (maxOptionLength > MAX_OPTION_STRING_LENGTH)
            maxOptionLength = MAX_OPTION_STRING_LENGTH;

        // Now, print the options.

        StringBuffer optString = new StringBuffer();
        for (i = 0; i < options.length; i++)
        {
            // If there's a short option, print it first. Then do the
            // long one.

            opt = options[i];
            optString.setLength (0);
            String sep = "";

            if (opt.shortOption != UsageInfo.NO_SHORT_OPTION)
            {
                optString.append (UsageInfo.SHORT_OPTION_PREFIX);
                optString.append (opt.shortOption);
                sep = ", ";
            }

            if (opt.longOption != null)
            {
                optString.append (sep);
                optString.append (UsageInfo.LONG_OPTION_PREFIX);
                optString.append (opt.longOption);
            }

            if (opt.argToken != null)
            {
                optString.append (' ');
                optString.append (opt.argToken);
            }

            s = optString.toString();    
            if (s.length() > maxOptionLength)
            {
                out.println (s);
                out.setPrefix (padString (" ", maxOptionLength));
            }

            else
            {
                out.setPrefix (padString (optString.toString(),
                                          maxOptionLength));
            }

            out.println (opt.explanation);
            out.setPrefix (null);
        }

        // Print the parameters. We already have size of the the largest
        // parameter name.

        strings = usageInfo.getParameterNames();
        if (strings.length > 0)
        {
            out.println ();
            out.println (BundleUtil.getMessage (Package.BUNDLE_NAME,
                                                locale,
                                                "CommandLineUtility.params",
                                                "PARAMETERS:"));
            out.println ();

            // Now, print the parameters.

            for (i = 0; i < strings.length; i++)
            {
                out.setPrefix (padString (strings[i], maxOptionLength));
                out.println (usageInfo.getParameterExplanation (strings[i]));
                out.setPrefix (null);
            }
        }

        if ( (s = usageInfo.getUsageTrailer()) != null)
            out.println (s);

        out.flush();
    }

    private String padString (String s, int toLength)
    {
        return TextUtil.leftJustifyString (s, toLength);
    }

    private UsageInfo getUsageInfo()
    {
        UsageInfo info = new UsageInfo();

        getCustomUsageInfo (info);

        info.addOption (UsageInfo.NO_SHORT_OPTION,
                        "logging",
                        BundleUtil.getMessage (Package.BUNDLE_NAME,
                                               Locale.getDefault(),
                                               "CommandLineUtility.logging",
                                               "Enable logging via the "
                                             + "java.util.logging API."));

        return info;
    }

    private String getOptionStringForError (char   shortOption,
                                            String longOption)
    {
        StringBuffer buf = new StringBuffer();
        boolean      paren = false;

        if (longOption != null)
        {
            buf.append ("--");
            buf.append (longOption);
            paren = true;
        }

        if (shortOption != UsageInfo.NO_SHORT_OPTION)
        {
            if (paren)
                buf.append (" (");

            buf.append ('-');
            buf.append (shortOption);

            if (paren)
                buf.append (')');
        }

        assert (buf.length() > 0);
        return buf.toString();
    }
}
