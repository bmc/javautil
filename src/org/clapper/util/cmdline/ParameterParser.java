/*---------------------------------------------------------------------------*\
  $Id: UsageInfo.java 6735 2007-05-12 11:30:05Z bmc $
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

package org.clapper.util.cmdline;

import java.io.StringWriter;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.clapper.util.io.WordWrapWriter;
import org.clapper.util.misc.ArrayIterator;
import org.clapper.util.misc.BundleUtil;
import org.clapper.util.text.TextUtil;

/**
 * An instance of this class is used to specify usage information for a
 * command-line utility. See
 * {@link CommandLineUtility#getCustomUsageInfo(UsageInfo)}
 * for more information how to use this class.
 *
 * @version <tt>$Revision: 6735 $</tt>
 *
 * @see CommandLineUtility
 * @see CommandLineUtility#getCustomUsageInfo(UsageInfo)
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public final class ParameterParser
{
    /*----------------------------------------------------------------------*\
                                 Constants
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

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Construct a new <tt>ParameterParser</tt>.
     */
    public ParameterParser()
    {
        // Nothing to do
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Parse a set of command-line parameters. The {@link UsageInfo}
     * object dictates the arguments to be parsed. The {@link ParameterHandler}
     * object handles the encountered parameters.
     *
     * @param params       parameters to parse
     * @param usageInfo    describes the arguments expected
     * @param paramHandler handles the arguments
     *
     * @throws CommandLineUsageException on error
     */
    public void parse(String[]         params,
                      UsageInfo        usageInfo,
                      ParameterHandler paramHandler)
        throws CommandLineUsageException
    {
        ArrayIterator<String> it = new ArrayIterator<String>(params);

        try
        {
            while (it.hasNext())
            {
                String arg = it.next();

                if (! (arg.charAt(0) == UsageInfo.SHORT_OPTION_PREFIX) )
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
                    optionInfo = usageInfo.getOptionInfo(arg.charAt(1));
                else
                {
                    if (! arg.startsWith(UsageInfo.LONG_OPTION_PREFIX))
                    {
                        throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.badLongOption",
                             "Option \"{0}\" is not a single-character " +
                             "short option, but it does not start with " +
                             "\"{1}\", as long options must.",
                             new Object[] {arg, UsageInfo.LONG_OPTION_PREFIX});
                    }

                    optionInfo = usageInfo.getOptionInfo(arg.substring(2));
                }

                if (optionInfo == null)
                {
                    throw new CommandLineUsageException
                            (Package.BUNDLE_NAME,
                             "CommandLineUtility.unknownOption",
                             "Unknown option: \"{0}\"",
                             new Object[] {arg});
                }

                // Okay, now handle the options.

                paramHandler.parseOption(optionInfo.shortOption,
                                         optionInfo.longOption,
                                         it);
            }

            paramHandler.parsePostOptionParameters(it);

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
     * Generate a usage message.
     *
     * @param prefixMsg     prefix (e.g., error) message to use, or null
     * @param usageInfo     the usage info
     * @param maxLineLength maximum output line length
     *
     * @return the usage string, with embedded newlines
     */
    public String getUsageMessage(String    prefixMsg,
                                  UsageInfo usageInfo,
                                  int       maxLineLength)
    {
        StringWriter     buf = new StringWriter();
        WordWrapWriter   out = new WordWrapWriter (buf, maxLineLength);
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
            out.println(prefixMsg);
            out.println();
        }

        // Now, print the summary line.

        String commandName = usageInfo.getCommandName();
        if (commandName != null)
        {
            usageLine.append (commandName);
        }

        else
        {
            usageLine.append("java ");
            usageLine.append(getClass().getName());
        }

        usageLine.append(' ');
        usageLine.append(BundleUtil.getMessage(Package.BUNDLE_NAME,
                                               locale,
                                               "CommandLineUtility.options1",
                                               "[options]"));
        usageLine.append(' ');

        // Add the parameter placeholders. We'll also calculate the maximum
        // parameter name length in this loop, to save an iteration later.

        strings = usageInfo.getParameterNames();
        if (strings.length > 0)
        {
            for (i = 0; i < strings.length; i++)
            {
                usageLine.append(' ');

                boolean optional = true;
                if (usageInfo.parameterIsRequired(strings[i]))
                    optional = false;

                if (optional)
                    usageLine.append('[');
                usageLine.append(strings[i]);
                if (optional)
                    usageLine.append(']');
                maxParamLength = Math.max(maxParamLength,
                                          strings[i].length() + 1);
            }
        }

        if ( (s = usageInfo.getUsagePrologue()) != null)
            out.println(s);

        s = BundleUtil.getMessage(Package.BUNDLE_NAME,
                                  locale,
                                  "CommandLineUtility.usage",
                                  "Usage:");
        out.setPrefix(s + " ");
        out.println(usageLine.toString());
        out.setPrefix (null);
        out.println();

        // Find the largest option name.

        out.println(BundleUtil.getMessage(Package.BUNDLE_NAME,
                                          locale,
                                          "CommandLineUtility.options2",
                                          "OPTIONS:"));
        out.println();

        maxOptionLength = 2;
        options = usageInfo.getOptions();
        for (i = 0; i < options.length; i++)
        {
            opt = options[i];

            // An option with a null explanation is hidden.

            if (opt.explanation == null)
                continue;

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

                maxOptionLength = Math.max(maxOptionLength, len + 1);
            }
        }

        if (maxOptionLength > MAX_OPTION_STRING_LENGTH)
            maxOptionLength = MAX_OPTION_STRING_LENGTH;

        // Now, print the options.

        StringBuffer optString = new StringBuffer();
        for (i = 0; i < options.length; i++)
        {
            opt = options[i];

            // An option with a null explanation is hidden.

            if (opt.explanation == null)
                continue;

            // If there's a short option, print it first. Then do the
            // long one.

            optString.setLength (0);
            String sep = "";

            if (opt.shortOption != UsageInfo.NO_SHORT_OPTION)
            {
                optString.append(UsageInfo.SHORT_OPTION_PREFIX);
                optString.append(opt.shortOption);
                sep = ", ";
            }

            if (opt.longOption != null)
            {
                optString.append(sep);
                optString.append(UsageInfo.LONG_OPTION_PREFIX);
                optString.append(opt.longOption);
            }

            if (opt.argToken != null)
            {
                optString.append(' ');
                optString.append(opt.argToken);
            }

            s = optString.toString();
            if (s.length() > maxOptionLength)
            {
                out.println (s);
                out.setPrefix(padString(" ", maxOptionLength));
            }

            else
            {
                out.setPrefix(padString(optString.toString(),
                                        maxOptionLength));
            }

            out.println(opt.explanation);
            out.setPrefix(null);
        }

        // Print the parameters. We already have size of the the largest
        // parameter name.

        strings = usageInfo.getParameterNames();
        if (strings.length > 0)
        {
            out.println();
            out.println(BundleUtil.getMessage(Package.BUNDLE_NAME,
                                              locale,
                                              "CommandLineUtility.params",
                                              "PARAMETERS:"));
            out.println();

            // Now, print the parameters.

            for (i = 0; i < strings.length; i++)
            {
                out.setPrefix(padString(strings[i], maxParamLength));
                out.println(usageInfo.getParameterExplanation(strings[i]));
                out.setPrefix(null);
            }
        }

        if ( (s = usageInfo.getUsageTrailer()) != null)
        {
            out.println();
            out.println(s);
        }

        out.flush();

        return buf.toString();
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private String padString(String s, int toLength)
    {
        return TextUtil.leftJustifyString(s, toLength);
    }
}
