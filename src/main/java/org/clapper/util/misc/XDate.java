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

package org.clapper.util.misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Version of <tt>java.util.Date</tt> that provides some extra utility
 * functions.
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class XDate extends Date
{
    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>XDate</tt> so that it represents the time the
     * object was constructed, measure to the nearest millisecond.
     */
    public XDate()
    {
        super();
    }

    /**
     * Create a new <tt>XDate</tt> object and initialize it to represent the
     * specified number of milliseconds since the epoch.
     *
     * @param millis  the milliseconds
     */
    public XDate (long millis)
    {
        super (millis);
    }

    /**
     * Create a new <tt>XDate</tt> object and initialize it to represent the
     * time contained in the specified, existing <tt>Date</tt> object (which
     * may, itself, be an <tt>XDate</tt>).
     *
     * @param date  the date
     */
    public XDate (Date date)
    {
        super (date.getTime());
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Convert this date from its time zone to another. Sample use:
     *
     * <pre>
     * // Convert time in default time zone to UTC
     *
     * XDate now = new XDate();
     * TimeZone tzUTC = TimeZone.getTimeZone ("UTC");
     * Date utc = now.convertToTimeZone (tzUTC);
     * DateFormat fmt = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss z");
     * fmt.setTimeZone (tzUTC);
     * System.out.println (fmt.format (utc));
     * </pre>
     *
     * <p>Or, more simply:</p>
     *
     * <pre>
     * XDate now = new XDate();
     * System.out.println (now.formatInTimeZone ("yyyy/MM/dd HH:mm:ss z",
     *                                           TimeZone.getTimeZone ("UTC")));
     * </pre>
     *
     * @param tz  the time zone to convert the date to
     *
     * @return a new <tt>XDate</tt> object, appropriately converted. This
     *         result can safely be stored in a <tt>java.util.Date</tt>
     *         reference.
     *
     * @see #formatInTimeZone
     */
    public XDate convertToTimeZone (TimeZone tz)
    {
        Calendar calFrom = new GregorianCalendar (TimeZone.getDefault());
        Calendar calTo = new GregorianCalendar (tz);
        calFrom.setTimeInMillis (getTime());
        calTo.setTimeInMillis (calFrom.getTimeInMillis());
        return new XDate (calTo.getTime());
    }

    /**
     * Convenience method to produce a printable date in a specified time
     * zone, using a <tt>SimpleDateFormat</tt>. Calling this method is
     * roughly equivalent to:
     *
     * <pre>
     * XDate now = new XDate();
     * TimeZone tzUTC = TimeZone.getTimeZone ("UTC");
     * Date utc = now.convertToTimeZone (tzUTC);
     * DateFormat fmt = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss z");
     * fmt.setTimeZone (tzUTC);
     * String formattedDate = fmt.format (utc);
     * </pre>
     *
     * @param dateFormat  the date format string to use, in a form that's
     *                    compatible with <tt>java.text.SimpleDateFormat</tt>
     * @param tz          the desired time zone
     *
     * @return the formatted date string
     */
    public String formatInTimeZone (String dateFormat, TimeZone tz)
    {
        Date tzDate = convertToTimeZone (tz);
        DateFormat fmt = new SimpleDateFormat (dateFormat);
        fmt.setTimeZone (tz);
        return fmt.format (tzDate);
    }
}
