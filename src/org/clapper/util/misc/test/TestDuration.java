/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc.test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.clapper.util.text.Duration;
import org.clapper.util.text.TextUtil;

/**
 * Tester for <tt>Duration</tt> class. Invoke with no parameters for
 * usage summary.
 *
 * @version <tt>$Revision: 5560 $</tt>
 *
 * @see FileHashMap
 */
public class TestDuration
    extends CommandLineUtility
{
    public Collection<String> stringsToParse = new ArrayList<String>();
    private String desiredInputLocale = null;
    private String desiredOutputLocale = null;

    public static void main (String args[])
    {
        TestDuration tester = new TestDuration();

        try
        {
            tester.execute (args);
        }

        catch (CommandLineUsageException ex)
        {
            // Already reported

            System.exit (1);
        }

        catch (CommandLineException ex)
        {
            System.err.println (ex.getMessage());
            ex.printStackTrace();
            System.exit (1);
        }

        catch (Exception ex)
        {
            ex.printStackTrace (System.err);
            System.exit (1);
        }
    }

    private TestDuration()
    {
        super();
    }

    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        do
        {
            stringsToParse.add(it.next());
        }
        while (it.hasNext());
    }

    protected void parseCustomOption (char             shortOption,
                                      String           longOption,
                                      Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        if (longOption.equals("locale-in"))
            desiredInputLocale = it.next();
        else if (longOption.equals("locale-out"))
            desiredOutputLocale = it.next();
        else
            throw new CommandLineUsageException("Bad option.");
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addOption(UsageInfo.NO_SHORT_OPTION, "locale-in", "<locale>",
                       "Specify desired input locale. Format must be \"" +
                       "lang_country\", e.g., \"en_US\"");
        info.addOption(UsageInfo.NO_SHORT_OPTION, "locale-out", "<locale>",
                       "Specify desired output locale. Format must be \"" +
                       "lang_country\", e.g., \"en_US\"");
        info.addParameter ("stringToParse ...",
                           "Duration string to be parsed. May be specified " +
                           "multiple times.",
                           true);
    }

    protected void runCommand()
        throws CommandLineException
    {
        try
        {
            Locale localeIn = getLocale(desiredInputLocale);
            Locale localeOut = getLocale(desiredOutputLocale);

            for (String s : stringsToParse)
            {
                Duration duration = null;

                try
                {
                    long l = Long.parseLong(s);
                    duration = new Duration(l);
                }

                catch (NumberFormatException ex)
                {
                    System.out.println("\nParsing \"" + s + "\"");

                    try
                    {
                        duration = new Duration(s, localeIn);
                        System.out.println("Parses to " + duration.getDuration() +
                                           " milliseconds.");
                    }

                    catch (ParseException ex2)
                    {
                        System.err.println("\"" + s + "\" is a bad duration " +
                            "string: " + ex2.getMessage());
                    }
                }

                System.out.println("Duration " + duration.toString() +
                                   " formats to \"" +
                                   duration.format(localeOut) + "\"");
            }
        }

        catch (Exception ex)
        {
            throw new CommandLineException (ex);
        }
    }

    private Locale getLocale(String desiredLocale) throws CommandLineException
    {
        Locale locale = Locale.getDefault();
        if (desiredLocale != null)
        {
            String tokens[] = TextUtil.split(desiredLocale, '_');
            switch (tokens.length)
            {
                case 3:
                    locale = new Locale(tokens[0], tokens[1], tokens[2]);
                    break;

                case 2:
                    locale = new Locale(tokens[0], tokens[1]);
                    break;

                case 1:
                    locale = new Locale(tokens[0]);
                    break;

                default:
                    throw new CommandLineException("Bad value for locale.");

            }
        }
        return locale;
    }
}
