/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.mail.test;

import org.clapper.util.mail.*;
import org.clapper.util.io.WordWrapWriter;
import org.clapper.util.misc.BadCommandLineException;
import org.clapper.util.text.TextUtil;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Test driver for <tt>EmailMessage</tt> class. Invoke this class from
 * the command line, with no parameters, for a usage summary.
 *
 * @version <tt>$Revision$</tt>
 *
 * @see EmailMessage
 */
public class Send
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                  Tester
    \*----------------------------------------------------------------------*/

    private static void usage()
    {
        String   className = Send.class.getName();
        String[] USAGE = new String[]
        {
"Usage: java " + className + " [options] smtphost recipient ...",
"",
"OPTIONS:",
"-alt                  Send message as \"multipart/alternative\", instead of",
"                      \"multipart/mixed\"",
"-A file_attachment    Add the contents of the specified file as an",
"                      attachment. May be specified multiple times",
"-a string_attachment  Use the contents of the specified string as the",
"                      attachment. A newline is automatically appended.",
"                      May be specified multiple times.",
"-bcc email_address    Bcc: the specified email address. May be specified",
"                      multiple times.",
"-cc email_address     Cc: the specified email address. May be specified",
"                      multiple times.",
"-d                    Enable debug.",
"-dt                   Dump the text part, after adding it to the message.",
"                      This is useful primarily for debugging",
"-f from_address       The sender address. If not supplied, the sender",
"                      address is built from the combination of the",
"                      current user name and the SMTP host.\n",
"-i                    For all files (attachments, text), use an InputStream",
"                      instead of a File object. (Tests EmailMessage class's",
"                      handling of InputStream)",
"-m mimeType           Set the MIME type for the text part. Defaults to",
"                      \"text/plain\" if not specified",
"-s subject            Set the message's subject. By default, there's no",
"                      subject.",
"-t text_message       The text for the body of the message. By default,",
"                      there is no text body.",
"-T file               Use the contents of the specified file as the text of",
"                      the message\n",
"<smtphost> is the name of the host through which to send the message.",
"<recipient> is a primary recipient's email address (i.e., someone whose",
"address goes in the \"To:\" header. Multiple recipients are permitted."
        };

        for (int i = 0; i < USAGE.length; i++)
            System.err.println (USAGE[i]);
    }

    /**
     * Test driver for this class. Invoke with no parameters for a usage
     * summary.
     *
     * @param args  Command line.
     */
    public static void main (String args[])
    {
        int             rc = 0;
        int             i = 0;
        boolean         dumpTextPart = false;
        String          textMimeType = "text/plain";
        String          text = null;
        File            textFile = null;
        boolean         useInputStreams = false;
        EmailMessage    msg = new EmailMessage();
        Collection      attachmentFiles = new ArrayList();
        Iterator        it;
        EmailTransport  transport = null;
        boolean         debug = false;
        PrintWriter     out = new WordWrapWriter (System.out, 79);

        try
        {

            // Parse the parameters.

            while ( (i < args.length) && (args[i].startsWith ("-")) )
            {
                if (args[i].equals ("-alt"))
                {
                    msg.setMultipartSubtype (EmailMessage.MULTIPART_ALTERNATIVE);
                }

                else if (args[i].equals ("-a"))
                {
                    msg.addAttachment (args[++i]);
                }

                else if (args[i].equals ("-A"))
                {
                    // Save it. We don't know until we're done parsing the
                    // options whether to add it as a File or an InputStream.

                    attachmentFiles.add (new File (args[++i]));
                }

                else if (args[i].equals ("-f"))
                {
                    msg.setSender (new EmailAddress (args[++i]));
                }

                else if (args[i].equals ("-i"))
                {
                    useInputStreams = true;
                }

                else if (args[i].equals ("-s"))
                {
                    msg.setSubject (args[++i]);
                }

                else if (args[i].equals ("-t"))
                {
                    if (textFile != null)
                    {
                        throw new BadCommandLineException
                            ("Can't specify both -t and -T");
                    }

                    text = args[++i];
                }

                else if (args[i].equals ("-T"))
                {
                    if (text != null)
                    {
                        throw new BadCommandLineException
                            ("Can't specify both -t and -T");
                    }

                    textFile = new File (args[++i]);
                }

                else if (args[i].equals ("-m"))
                {
                    textMimeType = args[++i];
                }

                else if (args[i].equals ("-d"))
                {
                    debug = true;
                }

                else if (args[i].equals ("-dt"))
                {
                    dumpTextPart = true;
                }

                else if (args[i].equals ("-cc"))
                {
                    msg.addCc (args[++i]);
                }

                else if (args[i].equals ("-bcc"))
                {
                    msg.addBcc (args[++i]);
                }

                else
                    throw new BadCommandLineException ( "Bad option: "
                                                      + args[i]);
                i++;
            }

            // Check that we have required remaining arguments.

            int argsLeft = args.length - i;
            if (argsLeft < 2)
                throw new BadCommandLineException ("Missing parameter(s)");

            // Initialize the EmailSender object

            transport = new SMTPEmailTransport (args[i++]);
            transport.setDebug (debug, System.out);

            // Get the recipients

            while (i < args.length)
                msg.addTo (args[i++]);

            // Add the text part.

            if (text != null)
                msg.setText (text, textMimeType);

            else if (textFile != null)
            {
                if (useInputStreams)
                    msg.setText (new FileInputStream (textFile), textMimeType);
                else
                    msg.setText (textFile);
            }

            // Add any attachments specified as files.

            for (it = attachmentFiles.iterator(); it.hasNext(); )
            {
                File f = (File) it.next();
                if (useInputStreams)
                    msg.addAttachment (new FileInputStream (f));
                else
                    msg.addAttachment (f);
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

        catch (ArrayIndexOutOfBoundsException ex)
        {
            System.err.println ("Missing parameter(s).");
            usage();
            rc++;
        }

        catch (BadCommandLineException ex)
        {
            System.err.println (ex.getMessage());
            usage();
            rc++;
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
            rc++;
        }

        System.exit (rc);
    }
}
