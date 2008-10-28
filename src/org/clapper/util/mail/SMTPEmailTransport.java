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

package org.clapper.util.mail;

import java.io.PrintStream;

import java.util.Properties;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Date;

import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.Message;

import javax.mail.MessagingException;

import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;

/**
 * <p><tt>SMTPEmailTransport</tt> defines a class that sends
 * <tt>EmailMessage</tt> objects via the Simple Mail Transfer Protocol
 * (SMTP). You instantiate an <tt>SMTPEmailTransport</tt> object by
 * specifying the SMTP host to be used to send messages, and you then use
 * the object to send <tt>EmailMessage</tt> objects.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @see EmailMessage
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class SMTPEmailTransport implements EmailTransport
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * The SMTP host
     */
    private String smtpHost = null;

    /**
     * The Java Mail API session
     */
    private Session session = null;

    /**
     * The SMTP transport object, not connected.
     */
    private Transport transport = null;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Constructs a new <tt>SMTPEmailTransport</tt> object that will use the
     * local host as its SMTP server.
     *
     * @throws EmailException  unable to initialize
     */
    public SMTPEmailTransport()
        throws EmailException
    {
        this("localhost");
    }

    /**
     * Constructs a new <tt>SMTPEmailTransport</tt> object, with the specified
     * SMTP host.
     *
     * @param smtpHost The SMTP host to use to send messages
     *
     * @throws EmailException  unable to initialize
     *
     * @see #send(EmailMessage)
     */
    public SMTPEmailTransport(String smtpHost)
        throws EmailException
    {
        this("localhost", null);
    }

    /**
     * Constructs a new <tt>SMTPEmailTransport</tt> object, with the specified
     * SMTP host.
     *
     * @param smtpHost The SMTP host to use to send messages
     * @param thisHost The name to use for this host. If null, then
     *                 <tt>InetAddress.getLocalHost().getHostName()</tt> is
     *                 used.
     *
     * @throws EmailException  unable to initialize
     *
     * @see #send(EmailMessage)
     */
    public SMTPEmailTransport(String smtpHost, String thisHost)
        throws EmailException
    {
        this.smtpHost = smtpHost;
        try
        {
            Properties props = new Properties();

            if (thisHost != null)
                props.put("mail,smtp.localhost", thisHost);

            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.allow8bitmime", "true");

            session = Session.getDefaultInstance(props, null);
            transport = session.getTransport("smtp");
        }

        catch (MessagingException ex)
        {
            throw new EmailException("Unable to initialize transport to " +
                                     "SMTP host \"" +
                                     smtpHost +
                                     "\"",
                                     ex);
        }
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Enable or disable the underlying implementation's debug flag, if
     * there is one. Debug messages are to be written to the supplied
     * <tt>PrintStream</tt>.
     *
     * @param debug    <tt>true</tt> to enable debug, <tt>false</tt> to
     *                 disable it
     * @param out      where to dump debug messages, or null for standard
     *                 output. Ignored unless <tt>debug</tt> is <tt>true</tt>.
     */
    public void setDebug(boolean debug, PrintStream out)
    {
        session.setDebug(debug);
        if (debug)
            session.setDebugOut(out);
    }

    /**
     * Attempts to deliver the message via the specified SMTP host.
     *
     * @param message the message to send
     *
     * @exception EmailException Failed to send the message.
     */
    @SuppressWarnings("static-access")
    public void send(EmailMessage message)
        throws EmailException
    {
        MimeMessage      javamailMessage;
        InternetAddress  addresses[];
        Iterator         it;
        MimeMultipart    body;
        List             attachments;
        int              totalAttachments;
        Collection       to;
        Collection       cc;
        Collection       bcc;
        MimeBodyPart     textPart;
        MultipartSubtype multipartSubtype;

        try
        {
            // Create the Java Mail API message.

            javamailMessage = new MimeMessage(session);

            // Create the body that will be put in the message, and set the
            // multipart subtype.

            body = new MimeMultipart();
            multipartSubtype = message.getMultipartSubtype();
            body.setSubType(multipartSubtype.getSubtypeString());

            // Set the sender

            EmailAddress senderAddress = message.getSender();
            javamailMessage.setFrom(senderAddress.getInternetAddress());

            // Set the various recipients.

            to = message.getToAddresses();
            cc = message.getCcAddresses();
            bcc = message.getBccAddresses();

            if (to.size() == 0)
            {
                throw new EmailException("No \"To:\" addresses in message");
            }

            setRecipients(javamailMessage, Message.RecipientType.TO, to);
            setRecipients(javamailMessage, Message.RecipientType.CC, cc);
            setRecipients(javamailMessage, Message.RecipientType.BCC, bcc);

            // Set the subject.

            javamailMessage.setSubject(message.getSubject());

            // Note: The Java Mail API wants *some* content. If the text
            // part is null and there are no attachments, build an empty
            // text part, so something is there.

            attachments = message.getAttachments();
            totalAttachments = attachments.size();

            textPart = message.getTextPart();
            if ((textPart == null) && (totalAttachments == 0))
            {
                message.setText("");
                textPart = message.getTextPart();
            }

            // Build the text part of the message.

            if (textPart != null)
                body.addBodyPart(textPart);

            // Build the attachments.

            if (totalAttachments > 0) {
                for (it = attachments.iterator(); it.hasNext();) {
                    body.addBodyPart((MimeBodyPart) it.next());
                }
            }

            // Set the message body, if any.

            if (body.getCount() > 0) {
                javamailMessage.setContent(body);
            }

            // Set additional headers.

            for (it = message.getAdditionalHeaders().iterator();
                    it.hasNext();) {
                javamailMessage.addHeaderLine((String) it.next());
            }

            // Set the sent date.

            javamailMessage.setSentDate(new Date());

            // Send it.

            transport.connect();
            transport.send(javamailMessage);
            transport.close();
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Unable to send message", ex);
	}
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Convenience method to set a list of recipients in a Java Mail API
     * Message object.
     *
     * @param msg           The MimeMessage object
     * @param recipientType The Java Mail API recipient type
     * @param addresses     The collection of EmailAddress objects
     *
     * @throws MessagingException  Java Mail API error
     */
    private void setRecipients(MimeMessage msg,
                               Message.RecipientType recipientType,
                               Collection addresses)
        throws MessagingException
    {
        InternetAddress[] recipients;
        Iterator          it;
        int               i;

        if (addresses.size() > 0)
        {
            recipients = new InternetAddress[addresses.size()];

            for (i = 0, it = addresses.iterator(); it.hasNext(); i++)
            {
                EmailAddress addr = (EmailAddress) it.next();
                recipients[i] = addr.getInternetAddress();
            }

            msg.setRecipients(recipientType, recipients);
        }
    }
}
