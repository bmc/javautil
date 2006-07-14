/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

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
