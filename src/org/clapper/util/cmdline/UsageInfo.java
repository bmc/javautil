/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.cmdline;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * An instance of this class is used to specify usage information for a
 * command-line utility. See
 * {@link CommandLineUtility#getCustomUsageInfo(UsageInfo)}
 * for more information how to use this class.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see CommandLineUtility
 * @see CommandLineUtility#getCustomUsageInfo(UsageInfo)
 */
public final class UsageInfo
{
    /*----------------------------------------------------------------------*\
                           Private Data Elements
    \*----------------------------------------------------------------------*/

    private Map     optionMap = new TreeMap (new OptionComparator());
    private Map     paramMap = new HashMap();
    private Set     requiredParams = new HashSet();
    private List    paramNames = new ArrayList();
    private String  usageLine = null;
    private String  usagePrologue = null;
    private String  usageTrailer = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor; can only be invoked in this package.
     */
    UsageInfo()
    {
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add an option and its explanation to the usage information.
     *
     * @param option       The option string, with leading "-"
     * @param explanation  A one-line explanation for the option. The line
     *                     can be as long as you want, and can contain multiple
     *                     sentences, but it must not contain a newline.
     *                     It will be automatically broken up into multiple
     *                     lines as necessary.
     */ 
    public void addOption (String option, String explanation)
    {
        if (! option.startsWith ("-"))
        {
            throw new IllegalArgumentException
                ("(BUG) Alleged option \""
               + option
               + "\", registered via UsageInfo.addOption(), doesn't begin "
               + "with \"-\".");
        }

        optionMap.put (option, explanation);
    }

    /**
     * Add a positional parameter (i.e., one that follows the options) and
     * its explanation to the usage information at the end of the list of
     * positional parameters.
     *
     * @param option       The parameter placeholder string
     * @param explanation  A one-line explanation for the parameter. The line
     *                     can be as long as you want, and can contain multiple
     *                     sentences, but it must not contain a newline.
     *                     It will be automatically broken up into multiple
     *                     lines as necessary.
     * @param required     <tt>true</tt> if the parameter is required,
     *                     <tt>false</tt> if the parameter is optional
     */ 
    public void addParameter (String  param,
                              String  explanation,
                              boolean required)
    {
        if (param.startsWith ("-"))
        {
            throw new IllegalArgumentException
                ("(BUG) Option \""
               + param
               + "\" registered via UsageInfo.addParameter(), instead of "
               + "via UsageInfo.addOption().");
        }

        paramMap.put (param, explanation);
        paramNames.add (param);
        if (required)
            requiredParams.add (param);
    }

    /**
     * Add a prologue to be displayed before the standard usage message.
     * The prologue string should be one line; it'll be broken up, wrapped,
     * and displayed as a paragraph that precedes the usage message.
     *
     * @param prologue   the prologue string
     *
     * @see #addUsageTrailer
     */
    public void addUsagePrologue (String prologue)
    {
        this.usagePrologue = prologue;
    }

    /**
     * Add a trailer to be displayed after the standard usage message. The
     * trailer string should be one line; it'll be broken up, wrapped, and
     * displayed as a paragraph following the usage message.
     *
     * @param trailer   the trailer string
     *
     * @see #addUsageTrailer
     */
    public void addUsageTrailer (String trailer)
    {
        this.usageTrailer = trailer;
    }

    /*----------------------------------------------------------------------*\
                              Package Methods
    \*----------------------------------------------------------------------*/

    String[] getParameterNames()
    {
        String[]  names = new String[paramNames.size()];
        int       i;
        Iterator  it;

        for (i = 0, it = paramNames.iterator(); it.hasNext(); i++)
            names[i] = (String) it.next();

        return names;
    }

    boolean parameterIsRequired (String name)
    {
        return requiredParams.contains (name);
    }

    String getParameterExplanation (String name)
    {
        return (String) paramMap.get (name);
    }

    String[] getOptions()
    {
        String[]  options = new String[optionMap.size()];
        int       i;
        Iterator  it;

        for (i = 0, it = optionMap.keySet().iterator(); it.hasNext(); i++)
            options[i] = (String) it.next();

        return options;
    }

    String getOptionExplanation (String option)
    {
        return (String) optionMap.get (option);
    }

    String getUsagePrologue()
    {
        return this.usagePrologue;
    }

    String getUsageTrailer()
    {
        return this.usageTrailer;
    }
}
