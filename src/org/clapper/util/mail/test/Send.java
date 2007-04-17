/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

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

package org.clapper.util.mail.test;

import org.clapper.util.mail.*;
import org.clapper.util.io.WordWrapWriter;
import org.clapper.util.misc.MIMETypeUtil;
import org.clapper.util.text.TextUtil;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

/**
 * Test driver for <tt>EmailMessage</tt> class. Invoke this class from
 * the command line, with no parameters, for a usage summary.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see EmailMessage
 */
public class Send extends CommandLineUtility
{
    /*----------------------------------------------------------------------*\
                            Instance Variables
    \*----------------------------------------------------------------------*/

    private EmailMessage     msg = new EmailMessage();
    private EmailTransport   transport = null;
    private boolean          dumpTextPart = false;
    private String           textMimeType = "text/plain";
    private String           text = null;
    private File             textFile = null;
    private boolean          useInputStreams = false;
    private boolean          debug = false;
    private PrintWriter      out = new WordWrapWriter (System.out, 79);
    private Collection<File> attachmentFiles = new ArrayList<File>();

    /*----------------------------------------------------------------------*\
                                  Tester
    \*----------------------------------------------------------------------*/

    /**
     * Test driver for this class. Invoke with no parameters for a usage
     * summary.
     *
     * @param args  Command line.
     */
    public static void main (String args[])
    {
        Send tester = new Send();

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

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private Send()
    {
        super();
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    protected void runCommand()
        throws CommandLineException
    {
        try
        {
            sendEmail();
        }

        catch (EmailException ex)
        {
            throw new CommandLineException (ex);
        }

        catch (IOException ex)
        {
            throw new CommandLineException (ex);
        }
    }

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
        try
        {
            if (longOption == null)
                longOption = "";

            if (longOption.equals ("alt"))
                msg.setMultipartSubtype (EmailMessage.MULTIPART_ALTERNATIVE);

            else if (longOption.equals ("dt"))
                dumpTextPart = true;

            else if (longOption.equals ("cc"))
                msg.addCc ((String) it.next());

            else if (longOption.equals ("bcc"))
                msg.addBcc ((String) it.next());

            else
            {
                switch (shortOption)
                {
                    case 'a':
                        msg.addAttachment (it.next());
                        break;

                    case 'A':
                        // Save it. We don't know until we're done parsing
                        // the options whether to add it as a File or an
                        // InputStream.

                        attachmentFiles.add (new File ((String) it.next()));
                        break;

                    case 'f':
                        msg.setSender (new EmailAddress ((String) it.next()));
                        break;

                    case 'i':
                        useInputStreams = true;
                        break;

                    case 's':
                        msg.setSubject ((String) it.next());
                        break;

                    case 't':
                        if (textFile != null)
                        {
                            throw new CommandLineUsageException
                                ("Can't specify both -t and -T");
                        }

                        text = (String) it.next();
                        break;

                    case 'T':
                        if (text != null)
                        {
                            throw new CommandLineUsageException
                                ("Can't specify both -t and -T");
                        }

                        textFile = new File ((String) it.next());
                        break;

                    case 'm':
                        textMimeType = (String) it.next();
                        break;

                    case 'd':
                        debug = true;
                        break;

                    default:
                        // Should not happen.
                        throw new IllegalStateException ("(BUG) Bad option. " +
                                                           "Why am I here?");
                }
            }
        }

        catch (EmailException ex)
        {
            throw new CommandLineUsageException (ex);
        }
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
        try
        {
            transport = new SMTPEmailTransport ((String) it.next());
            transport.setDebug (debug, System.out);

            while (it.hasNext())
                msg.addTo (it.next());
        }

        catch (EmailException ex)
        {
            throw new CommandLineUsageException (ex);
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
     *        <tt>UsageInfo.addParameter()</tt> method. The first argument
     *        to <tt>addParameter()</tt> is the parameter string (e.g.,
     *        "<dbCfg>" or "input_file"). The second parameter is the
     *        one-line description. The description may be of any length,
     *        but it should be a single line.
     *
     *   <li> Each option must be added to the object, via the
     *        <tt>UsageInfo.addOption()</tt> method. The first argument to
     *        <tt>addOption()</tt> is the option string (e.g., "-x" or
     *        "-version"). The second parameter is the one-line
     *        description. The description may be of any length, but it
     *        should be a single line.
     * </ul>
     *
     * That information will be combined with the common options supported
     * by the base class, and used to build a usage message.
     *
     * @param info   The <tt>UsageInfo</tt> object to fill.
     */
    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addOption (UsageInfo.NO_SHORT_OPTION, "alt",
                        "Send message as \"multipart/atlernative\", instead " +
                        "of \"multipart/mixed\".");
        info.addOption ('A', null, "<file>",
                        "Add the contents of <file> as an attachment. May " +
                        "be specified multiple times");
        info.addOption ('a', null, "<string>",
                        "Add the <string> as an attachment. May be " +
                        "specified multiple times");
        info.addOption (UsageInfo.NO_SHORT_OPTION, "bcc", "<address>",
                        "Bcc: the specified email address. May be specified " +
                         "multiple times.");
        info.addOption (UsageInfo.NO_SHORT_OPTION, "cc", "<address>",
                        "Cc: the specified email address. May be specified " +
                         "multiple times.");
        info.addOption ('d', null, "Enable debug");
        info.addOption (UsageInfo.NO_SHORT_OPTION, "dt",
                        "Dump the text part, after adding it to the message." +
                        "This is useful primarily for debugging");
        info.addOption ('f', "from", "<address>",
                        "Use the specified address as the sender");
        info.addOption ('i', null,
                        "For all files (attachments, text), use an " +
                        "InputStream instead of a File object. (Tests " +
                        "EmailMessage class's handling of InputStream)");
        info.addOption ('m', "mime-type", "<mimeType>",
                        "Set the MIME type for the text part.");
        info.addOption ('s', "subject", "<string>",
                        "Set the message subject.");
        info.addOption ('t', "text", "<string>",
                        "Use <string> as the text for the body of the " +
                        "message");
        info.addOption ('T', "text-file", "<file>",
                        "Use the contents of <file> as the body of the " +
                        "message.");

        info.addParameter ("<smtphost>",
                           "The SMTP host through which to send the message.",
                           true);
        info.addParameter ("<address> ...",
                           "One or more email addresses to receive the " +
                           "message",
                           true);
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private void sendEmail()
        throws EmailException,
               IOException
    {
        // Add the text part.

        if (text != null)
            msg.setText (text, null, textMimeType);

        else if (textFile != null)
        {
            if (useInputStreams)
            {
                msg.setText (new FileInputStream (textFile),
                             null,
                             textMimeType);
            }

            else
            {
                msg.setText (textFile);
            }
        }

        // Add any attachments specified as files.

        for (Iterator it = attachmentFiles.iterator(); it.hasNext(); )
        {
            File f = (File) it.next();
            if (useInputStreams)
            {
                msg.addAttachment (new FileInputStream (f),
                                   null,
                                   MIMETypeUtil.MIMETypeForFile (f));
            }
            else
            {
                msg.addAttachment (f);
            }
        }

        // Do any dumping...

        if (dumpTextPart)
        {
            if ( (text = msg.getText()) != null )
            {
                out.println ("------------------");
                out.println ("Text part follows:");
                out.println ("------------------");
                System.out.print (text);
                System.out.flush();
                out.println ("------------------");
                out.println ("End of Text part");
                out.println ("------------------");
            }
        }

        // Send the message.

        String sep;

        out.println ("Sending message");
        EmailAddress sender = msg.getSender();
        out.println ("From: " + sender.toString());
        out.println ("To: " + TextUtil.join (msg.getTo(), ", "));

        if (msg.getCc().size() > 0)
            out.println ("Cc: " + TextUtil.join (msg.getCc(), ", "));

        if (msg.getBcc().size() > 0)
            out.println ("Bcc: " + TextUtil.join (msg.getBcc(), ", "));

        out.println ("Subject: " + msg.getSubject());

        transport.send (msg);
    }
}
