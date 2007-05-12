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

package org.clapper.util.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Implements a log formatter for the <tt>java.util.logging</tt>
 * infrastructure that formats each non-exception log record as a simple,
 * single-line text string. (Exception stack traces will cause a message to
 * consume multiple lines.) This formatter currently cannot be configured
 * in any way, though that may change in subsequent releases. The output
 * format is similar to
 * {@link <a href="http://logging.apache.org/log4j/">Log4J's</a>}
 * <tt>PatternLayout</tt> class, with a format of "%d %-5p (%c{1}): %m%n".
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class JavaUtilLoggingTextFormatter extends Formatter
{
    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss.SSS");

    /*----------------------------------------------------------------------*\
                           Private Instance Data
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new JavaUtilLoggingTextLogFormatter.
     */
    public JavaUtilLoggingTextFormatter()
    {
        super();
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * <p>Format the given log record and return the formatted string.</p>
     *
     * <p>The resulting formatted String will normally include a localized
     * and formated version of the LogRecord's message field.</p>
     *
     * @param record the log record to be formatted
     *
     * @return the formatted log record
     */
    public String format (LogRecord record)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter (sw);
        String loggerName = record.getLoggerName();
        Throwable ex = record.getThrown();
        String message = super.formatMessage (record);

        pw.print (DATE_FORMAT.format (new Date (record.getMillis())));
        pw.print (' ');
        pw.print (record.getLevel().toString());
        pw.print (" (");

        // Logger name is a class name. Strip all but the last part of it.

        int i = loggerName.lastIndexOf (".");
        if ((i != -1) && (i < (loggerName.length() - 1)))
            loggerName = loggerName.substring (i + 1);

        pw.print (loggerName);
        pw.print (") ");

        if ((message != null) && (message.trim().length() > 0))
            pw.println (message);

        if (ex != null)
            ex.printStackTrace (pw);

        return sw.toString();
    }
}
