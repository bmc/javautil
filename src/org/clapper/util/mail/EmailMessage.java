/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.mail;

import java.net.InetAddress;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Reader;
import java.io.InputStreamReader;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.activation.DataHandler;
import javax.activation.FileTypeMap;
import javax.activation.FileDataSource;

import javax.mail.internet.InternetAddress;
import javax.mail.MessagingException;
import javax.mail.Message;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

/**
 * <p>The <tt>EmailMessage</tt> class is a simplified front-end to a
 * portion of Sun's Java Mail API. It provides an easy-to-use API for
 * composing and sending an email message, including simple single-part and
 * more complicated multipart email messages. Using an
 * <tt>EmailMessage</tt> object, a caller can compose and send a message
 * consisting of an optional text part and zero or more MIME-encoded
 * attachments. The message can have any number of recipients. The
 * recipients can be</p>
 *
 * <ul>
 *   <li> primary ("To:") recipients
 *   <li> "carbon copy" ("Cc:") recipients
 *   <li> blind copy ("Bcc:") recipient
 * </ul>
 *
 * <p>in any combination.</p>
 *
 * <p>Once created and filled, an <tt>EmailMessage</tt> can be sent
 * multiple times, through different SMTP servers if desired. To send
 * an <tt>EmailMessage</tt>, use an <tt>EmailSender</tt> object.</p>
 *
 * <p>The <tt>EmailMessage</tt> class supports both the MIME
 * "multipart/mixed" and "multipart/alternative" message types. According to
 * {@link <a href="http://www.ietf.org/rfc/rfc1341.txt">RFC 1341</a>},
 * the differences between the two types are as follows:</p>
 *
 * <table border="1">
 *   <tr valign="top">
 *     <td>multipart/mixed</td>
 *     <td>Intended for use when the body parts (i.e., the text and the
 *         attachments) are independent and intended to be displayed serially.
 *         For example, to send a text message with an attached image, you
 *         would use a "multipart/mixed" message.
 *   </tr>
 *
 *   <tr valign="top">
 *     <td>multipart/alternative</td>
 *     <td>Each of the parts (i.e., the main text part and the attachments)
 *         is an alternative version of the same information. The most typical
 *         "multipart/alternative" message contains a plain text part (i.e.,
 *         MIME type "text/plain") and an HTML text part. Both contain the
 *         same text, but the HTML part has a "richer" version of it. The
 *         recipient's mail client should either display the "best" version
 *         of the message, based on the user's environments and
 *         preferences; or, it should offer the user a choice of which part
 *         to view. A mail reader that's capable of displaying HTML might
 *         choose to ignore the plain text part and display only the HTML
 *         attachment; by contrast, a mail reader that cannot render HTML
 *         might choose to display only the plain text part.</td>
 *   </tr>
 * </table>
 *
 * <p>By default, an <tt>EmailMessage</tt> uses the "multipart/mixed"
 * mode. To use "multipart/alternative", use the
 * {@link #setMultipartSubtype(MultipartSubtype) setMultipartSubtype()}
 * method, passing it the constant value
 * {@link #MULTIPART_ALTERNATIVE}. Note: You <b>cannot</b> change the
 * subtype once you've added content to the message.</p>
 *
 * <p>As a simplified front-end, this class does have some restrictions,
 * including the following.</p>
 *
 * <ul>
 *   <li> It cannot currently be used to read incoming email messages
 *        (e.g., from a POP3 or IMAP service).
 *   <li> An <tt>EmailMessage</tt> object cannot be saved to "normal" email
 *        stores, though it can be serialized and unserialized.
 *   <li> It doesn't handle multipart types other than "multipart/mixed"
 *        and "multipart/alternative".
 * </ul>
 *
 * <p>For more complicated email interactions, use the Java Mail API
 * directly.</p>
 *
 * <p><b>Note:</b> This class requires the use of the Java Activation
 * Framework (JAF) classes (package <tt>javax.activation</tt>) and the
 * Java Mail API (package <tt>javax.mail</tt>). It was tested with
 * the following Java Mail API and JAF versions:</p>
 *
 * <ul>
 *   <li>Java Mail API 1.1.2 and Java Activation Framework API 1.0
 *   <li>Java Mail API 1.3.1 and Java Activation Framework API 1.0.2
 * </ul>
 *
 * <p>Both APIs are available from
 * {@link <a href="http://java.sun.com/">java.sun.com</a>}.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @see EmailSender
 * @see SMTPEmailSender
 * @see <a href="http://java.sun.com/javamail/">The Java Mail API home page</a>
 */
public class EmailMessage
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * Denotes a "multipart/mixed" message. This is the default.
     *
     * @see #setMultipartSubtype(EmailMessage.MultipartSubtype)
     */
    public static final MultipartSubtype MULTIPART_MIXED =
        new MultipartSubtype ("mixed");

    /**
     * Denotes a "multipart/alternative" message.
     *
     * @see #setMultipartSubtype(EmailMessage.MultipartSubtype)
     */
    public static final MultipartSubtype MULTIPART_ALTERNATIVE =
        new MultipartSubtype ("alternative");

    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    /**
     * Additional headers to add to each message
     */
    private static final String[] CUSTOM_HEADERS = new String[]
    {
        "X-Mailer: " + EmailMessage.class.getName()
    };

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * Sender, as passed to the constructor.
     */
    private EmailAddress sender = null;

    /**
     * The text portion of the message. May be null.
     */
    private MimeBodyPart textPart = null;

    /**
     * Additional headers.
     */
    private List additionalHeaders = new LinkedList();

    /**
     * Attachments. The list contains MimeBodyPart objects.
     */
    private List attachments = new ArrayList();

    /**
     * List of primary ("To:") recipients. Each element is an
     * EmailAddress object.
     */
    private List to = new ArrayList();

    /**
     * List of Cc: recipients. Each element is an EmailAddress object.
     */
    private List cc = new ArrayList();

    /**
     * List of Bcc: recipients. Each element is an EmailAddress object.
     */
    private List bcc = new ArrayList();

    /**
     * The subject.
     */
    private String subject = "";

    /**
     * The multipart subtype.
     */
    private MultipartSubtype multipartSubtype = MULTIPART_MIXED;

    /**
     * Temporary files that need to be deleted. (Used when processing
     * InputStream content. The Collection contains File objects.
     */
    private Collection temporaryFiles = new ArrayList();

    /**
     * Used by generateAttachmentName()
     */
    private int attachmentCounter = 0;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Constructs a new <tt>EmailMessage</tt> object. The sender's
     * address is built from the system's <tt>user.name</tt> property
     * and the SMTP host name.
     */
    public EmailMessage()
    {
        // Prime the additionalHeaders list with our additional headers.

        initAdditionalHeaders();
    }

    /**
     * Constructs a new <tt>EmailMessage</tt> object, with the specified
     * sender address.
     *
     * @param sender   The email address of the sender. If this parameter is
     *                 <tt>null</tt>, the sender's address is built
     *                 from the system's <tt>user.name</tt> property
     *                 and the host name of the SMTP server used to send the
     *                 message.
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #setSender(String)
     * @see #send(String)
     */
    public EmailMessage (String sender)
        throws EmailException
    {
        this();
        setSender (sender);
    }

    /*----------------------------------------------------------------------*\
                                Destructor
    \*----------------------------------------------------------------------*/

    /**
     * Destructor.
     */
    protected void finalize()
    {
        // Clean up any temporary files.

        for (Iterator it = temporaryFiles.iterator(); it.hasNext(); )
        {
            File f = (File) it.next();

            try
            {
                f.delete();
            }

            catch (Exception ex)
            {
                // Not much we can do about it here.
            }

            it.remove();
        }

        temporaryFiles = null;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add a header to the outgoing message. Note that callers should
     * <i>never</i> use this method to add "Received" headers, "To"
     * headers, "Cc" headers, or any other "official" header. Non-standard
     * headers should typically be prefixed with "X-" (e.g.,
     * "X-Mailing-List"). The specified header replaces any existing header
     * with the same name. This class defines its own default "X-Mailer"
     * header; however, the calling application is free to override that
     * default header by adding its own.
     *
     * @param header  the header name (e.g., "X-Mailer", "X-Mailing-List",
     *                etc."), without the trailing ":".
     * @param value   the value for the header
     */
    public void addHeader (String header, String value)
    {
        header = header + ": ";

        for (Iterator it = additionalHeaders.iterator(); it.hasNext(); )
        {
            String h = (String) it.next();

            if (h.startsWith (header))
                it.remove();
        }

        additionalHeaders.add (header + value);
    }

    /**
     * Add an email address to the list of "To" addresses to receive this
     * message. The address can be any RFC822-compliant email address.
     * Examples include:
     *
     * <blockquote>
     * <pre>
     * moe@example.com
     * Moe Howard <moe@example.com>
     * "Curley Howard" <curley@example.com>
     * larry@example.com (Larry Fine)
     * </pre>
     * </blockquote>
     *
     * <p>If you want to validate the address before calling this method,
     * simply pass it to the constructor of an <tt>EmailAddress</tt>
     * object.</p>
     *
     * @param emailAddress  the email address to add
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addTo(String[])
     * @see #addTo(EmailAddress)
     * @see #getTo
     * @see #clearTo
     * @see #addCc
     * @see #addBcc
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addTo (String emailAddress)
        throws EmailException
    {
        addEmailAddress (new EmailAddress (emailAddress), to);
    }

    /**
     * Add an email address, in the form of an <tt>EmailAddress</tt>
     * object, to the list of "To" addresses to receive this message.
     *
     * @param emailAddress  the email address to add
     *
     * @see #addTo(String[])
     * @see #addTo(String)
     * @see #getTo
     * @see #clearTo
     * @see #addCc
     * @see #addBcc
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addTo (EmailAddress emailAddress)
        throws EmailException
    {
        addEmailAddress (emailAddress, to);
    }

    /**
     * Add multiple email addresses to the list of "To" addresses to
     * receive this message.
     *
     * @param emailAddresses  the email addresses to add
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addTo(String)
     * @see #addTo(Collection)
     * @see #getTo
     * @see #clearTo
     * @see #addCc(String[])
     * @see #addBcc(String[])
     * @see #clearAllRecipients
     */
    public void addTo (String[] emailAddresses)
        throws EmailException
    {
        addEmailAddresses (emailAddresses, to);
    }

    /**
     * Add multiple email addresses to the list of "To" addresses to
     * receive this message.
     *
     * @param emailAddresses  A <tt>Collection</tt> of the email addresses.
     *                        The <tt>Collection</tt> can contain
     *                        <tt>String</tt> or <tt>EmailAddress</tt>
     *                        objects.
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addTo(String[])
     * @see #addTo(String)
     * @see #addTo(EmailAddress)
     * @see #getTo
     * @see #clearTo
     * @see #addCc(Collection)
     * @see #addBcc(Collection)
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addTo (Collection emailAddresses)
        throws EmailException
    {
        addEmailAddresses (emailAddresses, to);
    }

    /**
     * Get the list of "To" addresses to which this message will be sent.
     *
     * @return a <tt>Collection</tt> of email addresses. Each object in the
     *         returned <tt>Collection</tt> is an <tt>EmailAddress</tt>
     *         object. If there are no "To" addresses, an empty
     *         <tt>Collection</tt> is returned.
     *
     * @see #addTo(String)
     * @see #addTo(String[])
     * @see #clearTo
     *
     * @throws EmailException  error retrieving addresses
     *
     * @see EmailAddress
     */
    public Collection getTo()
        throws EmailException
    {
        return getEmailAddresses (to);
    }

    /**
     * Clear the list of "To" addresses in this message, without clearing
     * the "Cc" or "Bcc" lists.
     *
     * @see #addTo(String)
     * @see #addTo(String[])
     * @see #getTo
     * @see #clearCc
     * @see #clearBcc
     * @see #clearAllRecipients
     */
    public void clearTo()
    {
        to.clear();
    }

    /**
     * Add an email address to the list of "Cc" addresses to receive this
     * message. The address can be any RFC822-compliant email address.
     * Examples include:
     *
     * <blockquote>
     * <pre>
     * moe@example.com
     * Moe Howard <moe@example.com>
     * "Curley Howard" <curley@example.com>
     * larry@example.com (Larry Fine)
     * </pre>
     * </blockquote>
     *
     * <p>If you want to validate the address before calling this method,
     * simply pass it to the constructor of an <tt>EmailAddress</tt>
     * object.</p>
     *
     * @param emailAddress  the email address to add
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addCc(String[])
     * @see #addCc(EmailAddress)
     * @see #getCc
     * @see #clearCc
     * @see #addTo(String)
     * @see #addBcc(String)
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addCc (String emailAddress)
        throws EmailException
    {
        addEmailAddress (new EmailAddress (emailAddress), cc);
    }

    /**
     * Add an email address, in the form of an <tt>EmailAddress</tt>
     * object, to the list of "Cc" addresses to receive this message.
     *
     * @param emailAddress  the email address to add
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addCc(String[])
     * @see #addCc(String)
     * @see #getCc
     * @see #clearCc
     * @see #addTo(String)
     * @see #addBcc(String)
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addCc (EmailAddress emailAddress)
        throws EmailException
    {
        addEmailAddress (emailAddress, cc);
    }

    /**
     * Add multiple email addresses to the list of "Cc" addresses to
     * receive this message.
     *
     * @param emailAddresses  the email addresses to add
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addCc(String)
     * @see #addCc(Collection)
     * @see #getTo
     * @see #clearTo
     * @see #addTo(String[])
     * @see #addBcc(String[])
     * @see #clearAllRecipients
     */
    public void addCc (String[] emailAddresses)
        throws EmailException
    {
        addEmailAddresses (emailAddresses, cc);
    }

    /**
     * Add multiple email addresses to the list of "Cc" addresses to
     * receive this message.
     *
     * @param emailAddresses  A <tt>Collection</tt> of the email addresses.
     *                        The <tt>Collection</tt> can contain
     *                        <tt>String</tt> or <tt>EmailAddress</tt>
     *                        objects.
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addCc(String[])
     * @see #addCc(String)
     * @see #addCc(EmailAddress)
     * @see #getCc
     * @see #clearCc
     * @see #addTo(Collection)
     * @see #addBcc(Collection)
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addCc (Collection emailAddresses)
        throws EmailException
    {
        addEmailAddresses (emailAddresses, cc);
    }

    /**
     * Get the list of "Cc" addresses to which this message will be sent.
     *
     * @return a <tt>Collection</tt> of email addresses. Each object in the
     *         returned <tt>Collection</tt> is an <tt>EmailAddress</tt>
     *         object. If there are no "Cc" addresses, an empty
     *         <tt>Collection</tt> is returned.
     *
     * @see #addCc(String)
     * @see #addCc(String[])
     * @see #clearCc
     *
     * @throws EmailException  error retrieving addresses
     *
     * @see EmailAddress
     */
    public Collection getCc()
        throws EmailException
    {
        return getEmailAddresses (cc);
    }

    /**
     * Clear the list of "Cc" addresses in this message, without clearing
     * the "To" or "Bcc" lists.
     *
     * @see #addCc(String)
     * @see #addCc(String[])
     * @see #getCc
     * @see #clearTo
     * @see #clearBcc
     * @see #clearAllRecipients
     */
    public void clearCc()
    {
        cc.clear();
    }

    /**
     * Add an email address to the list of "Bcc" (blind carbon copy)
     * addresses to receive this message. The address can be any
     * RFC822-compliant email address. Examples include:
     *
     * <blockquote>
     * <pre>
     * moe@example.com
     * Moe Howard <moe@example.com>
     * "Curley Howard" <curley@example.com>
     * larry@example.com (Larry Fine)
     * </pre>
     * </blockquote>
     *
     * <p>If you want to validate the address before calling this method,
     * simply pass it to the constructor of an <tt>EmailAddress</tt>
     * object.</p>
     *
     * @param emailAddress  the email address to add
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addBcc(String[])
     * @see #addBcc(EmailAddress)
     * @see #getBcc
     * @see #clearBcc
     * @see #addCc(String)
     * @see #addTo(String)
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addBcc (String emailAddress)
        throws EmailException
    {
        addEmailAddress (new EmailAddress (emailAddress), bcc);
    }

    /**
     * Add an email address to the list of "Bcc" (blind carbon copy)
     * addresses to receive this message.
     *
     * @param emailAddress  the email address to add
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addBcc(String[])
     * @see #addBcc(String)
     * @see #getBcc
     * @see #clearBcc
     * @see #addCc(String)
     * @see #addTo(String)
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addBcc (EmailAddress emailAddress)
        throws EmailException
    {
        addEmailAddress (emailAddress, bcc);
    }

    /**
     * Add multiple email addresses to the list of "Bcc" addresses to
     * receive this message.
     *
     * @param emailAddresses  the email addresses to add
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addBcc(String)
     * @see #addBcc(Collection)
     * @see #getTo
     * @see #clearTo
     * @see #addTo(Collection)
     * @see #addCc(Collection)
     * @see #clearAllRecipients
     */
    public void addBcc (String[] emailAddresses)
        throws EmailException
    {
        addEmailAddresses (emailAddresses, bcc);
    }

    /**
     * Add multiple email addresses to the list of "Bcc" addresses to
     * receive this message.
     *
     * @param emailAddresses  A <tt>Collection</tt> of the email addresses.
     *                        The <tt>Collection</tt> can contain
     *                        <tt>String</tt> or <tt>EmailAddress</tt>
     *                        objects.
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #addBcc(String[])
     * @see #addBcc(String)
     * @see #addBcc(EmailAddress)
     * @see #getBcc
     * @see #clearBcc
     * @see #addTo(Collection)
     * @see #addCc(Collection)
     * @see #clearAllRecipients
     * @see EmailAddress
     */
    public void addBcc (Collection emailAddresses)
        throws EmailException
    {
        addEmailAddresses (emailAddresses, bcc);
    }

    /**
     * Get the list of "Bcc" addresses to which this message will be sent.
     *
     * @return a <tt>Collection</tt> of email addresses. Each object in the
     *         returned <tt>Collection</tt> is an <tt>EmailAddress</tt>
     *         object. If there are no "Bcc" addresses, an empty
     *         <tt>Collection</tt> is returned.
     *
     * @see #addBcc(String)
     * @see #addBcc(String[])
     * @see #clearBcc
     *
     * @throws EmailException  error retrieving addresses
     *
     * @see EmailAddress
     */
    public Collection getBcc()
        throws EmailException
    {
        return getEmailAddresses (bcc);
    }

    /**
     * Clear the list of "Bcc" addresses in this message, without clearing
     * the "Cc" or "To" lists.
     *
     * @see #addBcc(String)
     * @see #addBcc(String[])
     * @see #getBcc
     * @see #clearCc
     * @see #clearTo
     * @see #clearAllRecipients
     */
    public void clearBcc()
    {
        bcc.clear();
    }

    /**
     * Clear all recipient addresses from this message. Calling this method
     * is equivalent to:
     *
     * <blockquote>
     * <pre>
     * message.clearTo();
     * message.clearCc();
     * message.clearBcc();
     * </pre>
     * </blockquote>
     */
    public void clearAllRecipients()
    {
        clearTo();
        clearCc();
        clearBcc();
    }

    /**
     * <p>Set the multipart subtype. By default, the multipart subtype is
     * set to <tt>MultipartSubtype.MIXED</tt>; that is, the text and the
     * attachments are distinct, separate items. The only other possible
     * value is <tt>MultipartSubtype.ALTERNATIVE</tt>, which signifies that
     * each of the parts is an "alternative" version of the same
     * information. See the class documentation for more details.</p>
     *
     * <p>Note: You <b>must</b> set the subtype before adding the text part
     * and any attachments! The attachments are encoded as you add them to
     * the message, and the encoded format can differ depending on
     * the subtype.</p>
     *
     * @param subType  the desired subtype
     *
     * @throws EmailException  message already has attachments, so subtype
     *                         cannot be changed
     *
     * @see #getMultipartSubtype
     * @see #MULTIPART_MIXED
     * @see #MULTIPART_ALTERNATIVE
     */
    public void setMultipartSubtype (MultipartSubtype subType)
        throws EmailException
    {
        if (attachments.size() > 0)
        {
            throw new EmailException ("Can't set multipart subtype once "
                                    + "message has attachments.");
        }

        this.multipartSubtype = subType;
    }

    /**
     * Get this message's multipart subtype value.
     *
     * @return the multipart subtype
     *
     * @see #setMultipartSubtype
     */
    public MultipartSubtype getMultipartSubtype()
    {
        return multipartSubtype;
    }
     
    /**
     * Get the text portion of the message.
     *
     * @return a <tt>String</tt> containing the text, or null if there's no
     *         text part in the message
     *
     * @throws EmailException  on error
     *
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(File)
     */
    public String getText()
        throws EmailException
    {
        String text = null;

        try
        {
            if (textPart != null)
            {
                Reader in = new InputStreamReader (textPart.getInputStream());
                StringWriter sw = new StringWriter();
                int c;

                while ( (c = in.read()) != -1 )
                    sw.write (c);

                text = sw.toString();
            }
        }

        catch (IOException ex)
        {
            throw new EmailException ("Cannot retrieve text portion of email "
                                    + "message",
                                      ex);
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot retrieve text portion of email "
                                    + "message",
                                      ex);
        }

        return text;
    }

    /**
     * Set the text part of the message from a <tt>String</tt> object.
     * The corresponding MIME type is assumed to be "text/plain". If this
     * message already has a text part, it will be replaced.
     *
     * @param text   The string to use as the text of the message. It may
     *               contain multiple lines.
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(String,String)
     * @see #setText(String[])
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(Iterator,String)
     * @see #setText(File)
     * @see #setText(File,String)
     * @see #clearText()
     */
    public void setText (String text)
        throws EmailException
    {
        try
        {
            this.textPart = makeTextBodyPart (text);
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }
    }

    /**
     * Set the text part of the message from a <tt>String</tt> object. If
     * this message already has a text part, it will be replaced.
     *
     * @param text     The string to use as the text of the message. It may
     *                 contain multiple lines.
     * @param mimeType The MIME type for the text
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(String)
     * @see #setText(String[])
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(Iterator,String)
     * @see #setText(File)
     * @see #setText(File,String)
     * @see #clearText()
     */
    public void setText (String text, String mimeType)
        throws EmailException
    {
        try
        {
            this.textPart = makeTextBodyPart (text, mimeType);
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }
    }

    /**
     * Set the text part of the message from an array of <tt>String</tt>
     * objects. Each element in the array is assumed to represent a single
     * line of text. The corresponding MIME type is assumed to be
     * "text/plain". If this message already has a text part, it will be
     * replaced.
     *
     * @param text The strings to use as the text of the message.
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(Iterator,String)
     * @see #setText(File)
     * @see #setText(File,String)
     * @see #clearText()
     */
    public void setText (String[] text)
        throws EmailException
    {
        try
        {
            this.textPart = makeTextBodyPart (text);
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }
    }

    /**
     * Set the text part of the message from an array of <tt>String</tt>
     * objects. Each element in the array is assumed to represent a single
     * line of text. If this message already has a text part, it will be
     * replaced.
     *
     * @param text     The strings to use as the text of the message.
     * @param mimeType The MIME type for the text
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(Iterator,String)
     * @see #setText(File)
     * @see #setText(File,String)
     * @see #clearText()
     */
    public void setText (String[] text, String mimeType)
        throws EmailException
    {
        try
        {
            this.textPart = makeTextBodyPart (text, mimeType);
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }
    }

    /**
     * Set the text part of the message from an <tt>InputStream</tt>
     * object. The corresponding MIME type is assumed to be "text/plain".
     * If this message already has a text part, it will be replaced.
     *
     * @param is  The <tt>InputStream</tt> whose contents are to be read
     *            and fed into the text part of this message
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(Iterator,String)
     * @see #setText(File,String)
     * @see #setText(File)
     * @see #clearText()
     */
    public void setText (InputStream is)
        throws EmailException
    {
        setText (is, "text/plain");
    }

    /**
     * Set the text part of the message from an <tt>InputStream</tt>
     * object. If this message already has a text part, it will be
     * replaced.
     *
     * @param is       The <tt>InputStream</tt> whose contents are to be read
     *                 and fed into the text part of this message
     * @param mimeType The MIME type for the text
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(Iterator,String)
     * @see #setText(File)
     * @see #setText(File,String)
     * @see #clearText()
     */
    public void setText (InputStream is, String mimeType)
        throws EmailException
    {
        try
        {
            this.textPart = makeBodyPart (is, mimeType);
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }

        catch (IOException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }
    }

    /**
     * Set the text part of the message from <tt>Iterator</tt> of
     * <tt>String</tt> objects. Each element returned by the iterator is
     * assumed to represent a single line of text. The corresponding MIME
     * type is assumed to be "text/plain". If this message already has a
     * text part, it will be replaced.
     *
     * @param iterator The <tt>Iterator</tt> that will return the
     *                 <tt>String</tt> objects that represent the text lines
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator,String)
     * @see #setText(File)
     * @see #setText(File,String)
     * @see #clearText()
     */
    public void setText (Iterator iterator)
        throws EmailException
    {
        try
        {
            this.textPart = makeTextBodyPart (iterator);
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }
    }

    /**
     * Set the text part of the message from an <tt>Iterator</tt> of
     * <tt>String</tt> objects. Each element returned by the iterator is
     * assumed to represent a single line of text. If this message already
     * has a text part, it will be replaced.
     *
     * @param iterator The <tt>Iterator</tt> that will return the
     *                 <tt>String</tt> objects that represent the text lines
     * @param mimeType The MIME type for the text
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(File)
     * @see #setText(File,String)
     * @see #clearText()
     */
    public void setText (Iterator iterator, String mimeType)
        throws EmailException
    {
        try
        {
            this.textPart = makeTextBodyPart (iterator, mimeType);
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }
    }

    /**
     * Set the text part of the message from the contents of a file. The
     * MIME type is taken from the file's extension. If this message
     * already has a text part, it will be replaced.
     *
     * @param file   The <tt>File</tt> object from which to get the text
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(File,String)
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #clearText()
     */
    public void setText (File file)
        throws EmailException
    {
        setText (file, null);
    }

    /**
     * Set the text part of the message from the contents of a file. If
     * this message already has a text part, it will be replaced.
     *
     * @param file     The <tt>File</tt> object from which to get the text
     * @param mimeType The MIME type to associate with the text part, or
     *                 null to assume the default based on the file extension
     *
     * @throws EmailException  error setting text part of message
     *
     * @see #getText()
     * @see #setText(File)
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #clearText()
     */
    public void setText (File file, String mimeType)
        throws EmailException
    {
        try
        {
            MimeBodyPart    bodyPart   = makeBodyPart (file, mimeType);
            int             slash;
            String          mainType;

            // Get the assigned MIME part, in case the caller passed in a
            // null.
            mimeType = bodyPart.getContentType();

            // Ensure that the MIME type is text. Barf if it isn't.

            slash = mimeType.indexOf ('/');
            if (slash == -1)
                slash = mimeType.length();

            mainType = mimeType.substring (0, slash);
            if (! mainType.equals ("text"))
            {
                throw new EmailException ("Bad MIME type (\""
					+ mimeType
					+ "\") for text "
					+ "part of email message");
            }

            this.textPart = bodyPart;
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot set text part of email message",
                                      ex);
        }
    }

    /**
     * Clear the text portion of this message. Note: Removing the text
     * portion does not remove any attachments.
     *
     * @see #getText()
     * @see #setText(String)
     * @see #setText(String,String)
     * @see #setText(String[],String)
     * @see #setText(InputStream)
     * @see #setText(InputStream,String)
     * @see #setText(Iterator)
     * @see #setText(File)
     * @see #clearAllAttachments()
     */
    public void clearText()
    {
        textPart = null;
    }

    /**
     * Get an attachment from the message. Since the attachment can consist
     * of any type of data, this method simply returns an <tt>InputStream</tt>
     * that can be used to read the bytes of the attachment.
     *
     * @param index  The 0-based index of the attachment. Index 0 corresponds
     *               to the first attachment (not including the text part),
     *               index 1 corresponds to the second attachment, etc.
     *
     * @return an <tt>InputStream</tt> for reading the attachment's contents
     *
     * @throws ArrayIndexOutOfBoundsException <tt>index</tt> is out of range
     * @throws EmailException                 error retrieving attachment
     *
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[])
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     */
    public InputStream getAttachment (int index)
        throws ArrayIndexOutOfBoundsException,
               EmailException
    {
        MimeBodyPart attachment = (MimeBodyPart) attachments.get (index);

	try
	{
	    return attachment.getInputStream();
	}

	catch (IOException ex)
	{
	    throw new EmailException ("Cannot get attachment #"
				    + String.valueOf (index)
				    + " from message",
				      ex);
        }

	catch (MessagingException ex)
	{
	    throw new EmailException ("Cannot get attachment #"
				    + String.valueOf (index)
				    + " from message",
				      ex);
        }
    }

    /**
     * Get the content type (i.e., MIME type) of a given attachment.
     *
     * @param index  The 0-based index of the attachment. Index 0 corresponds
     *               to the first attachment (not including the text part),
     *               index 1 corresponds to the second attachment, etc.
     *
     * @return the attachment's MIME type
     *
     * @throws ArrayIndexOutOfBoundsException <tt>index</tt> is out of range
     * @throws EmailException                 error retrieving attachment
     *
     * @see #getAttachment(int)
     * @see #totalAttachments()
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[])
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     */
    public String getAttachmentContentType (int index)
        throws ArrayIndexOutOfBoundsException,
               EmailException
    {
        MimeBodyPart  attachment = (MimeBodyPart) attachments.get (index);

        try
        {
            return attachment.getContentType();
        }

        catch (MessagingException ex)
	{
	    throw new EmailException ("Cannot get attachment #"
				    + String.valueOf (index)
				    + " from message",
				      ex);
        }
    }

    /**
     * Get the total number of attachments, not counting the text part,
     * in this message.
     *
     * @return the total number of attachments in this message
     *
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[])
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     */
    public int totalAttachments()
    {
        return attachments.size();
    }

    /**
     * Add a text attachment to this message. The attachment's MIME type is
     * assumed to be "text/plain". The attachment is added onto the end of
     * the attachments in the message.
     *
     * @param contents The string to use as the attachment's contents. It may
     *                 contain multiple lines.
     *
     * @throws EmailException  error creating the attachment
     *
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[])
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (String contents)
        throws EmailException
    {
        try
        {
            attachments.add (makeTextBodyPart (contents));
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }
    }

    /**
     * Add a text attachment to this message. The attachment is added onto
     * the end of the attachments in the message.
     *
     * @param contents The string to use as the attachment's contents. It may
     *                 contain multiple lines.
     * @param mimeType The MIME type for the attachment
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(String)
     * @see #addAttachment(String[])
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (String contents, String mimeType)
        throws EmailException
    {
        try
        {
            attachments.add (makeTextBodyPart (contents, mimeType));
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }
    }

    /**
     * Add a text attachment to this message from an array of
     * <tt>String</tt> objects. Each element in the array is assumed to
     * represent a single line of text. The corresponding MIME type is
     * assumed to be "text/plain".
     *
     * @param contents The strings to use as the attachment's contents
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (String[] contents)
        throws EmailException
    {
        try
        {
            attachments.add (makeTextBodyPart (contents));
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }
    }

    /**
     * Add a text attachment to this message from an array of
     * <tt>String</tt> objects. Each element in the array is assumed to
     * represent a single line of text.
     *
     * @param text     The strings to use as the text of the message.
     * @param mimeType The MIME type for the text
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (String[] text, String mimeType)
        throws EmailException
    {
        try
        {
            attachments.add (makeTextBodyPart (text, mimeType));
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }
    }

    /**
     * Add an attachment to this message from an <tt>InputStream</tt>
     * object. The corresponding MIME type is assumed to be
     * "application/octet-stream".
     *
     * @param is  The <tt>InputStream</tt> whose contents are to be read
     *            to create the attachment
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (InputStream is)
        throws EmailException
    {
        addAttachment (is, "application/octet-stream");
    }

    /**
     * Add a attachment to this message from an  <tt>InputStream</tt>
     * object.
     *
     * @param is       The <tt>InputStream</tt> whose contents are to be read
     *                 to create the attachment
     * @param mimeType The MIME type
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (InputStream is, String mimeType)
        throws EmailException
    {
        try
        {
            attachments.add (makeBodyPart (is, mimeType));
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }

        catch (IOException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }
    }

    /**
     * Add a text attachment to this message from an <tt>Iterator</tt> of
     * <tt>String</tt> objects. Each element in the array is assumed to
     * represent a single line of text. The corresponding MIME type is
     * assumed to be "text/plain".
     *
     * @param iterator The <tt>Iterator</tt> that will return the
     *                 <tt>String</tt> objects that represent the text lines
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator,String)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (Iterator iterator)
        throws EmailException
    {
        try
        {
            attachments.add (makeTextBodyPart (iterator));
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }
    }

    /**
     * Add a text attachment to this message from an <tt>Iterator</tt> of
     * <tt>String</tt> objects. Each element in the array is assumed to
     * represent a single line of text. The corresponding MIME type is
     * assumed to be "text/plain".
     *
     * Set the text part of the message from an <tt>Iterator</tt> of
     * <tt>String</tt> objects. Each element in the array is assumed to
     * represent a single line of text.
     *
     * @param iterator The <tt>Iterator</tt> that will return the
     *                 <tt>String</tt> objects that represent the text lines
     * @param mimeType The MIME type for the text
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(File)
     * @see #addAttachment(File,String)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (Iterator iterator, String mimeType)
        throws EmailException
    {
        try
        {
            attachments.add (makeTextBodyPart (iterator, mimeType));
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }
    }

    /**
     * Add an attachment from the contents of a file. The MIME type is
     * taken from the file's extension.
     *
     * @param file   The <tt>File</tt> to read to create the attachment
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(File,String)
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (File file)
        throws EmailException
    {
        addAttachment (file, null);
    }

    /**
     * Add an attachment from the contents of a file.
     *
     * @param file     The <tt>File</tt> to read to create the attachment
     * @param mimeType The MIME type to associate with the file, or null
     *                 to infer the MIME type from the file's extension
     *
     * @throws EmailException  error adding the attachment
     *
     * @see #addAttachment(File,String)
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #getAttachment(int)
     * @see #getAttachmentContentType(int)
     * @see #totalAttachments()
     */
    public void addAttachment (File file, String mimeType)
        throws EmailException
    {
        try
        {
            attachments.add (makeBodyPart (file, mimeType));
        }

        catch (MessagingException ex)
        {
            throw new EmailException ("Cannot add attachment to email message",
                                      ex);
        }
    }

    /**
     * Clear all attachments from this message. Note: Removing the attachments
     * does not remove the text part of the message.
     *
     * @see #addAttachment(String)
     * @see #addAttachment(String,String)
     * @see #addAttachment(String[],String)
     * @see #addAttachment(InputStream)
     * @see #addAttachment(InputStream,String)
     * @see #addAttachment(Iterator)
     * @see #addAttachment(File)
     * @see #clearText()
     */
    public void clearAllAttachments()
    {
        attachments.clear();
    }

    /**
     * Set the subject of this message.
     *
     * @param subject  The text subject of the message, or null to clear
     *                 the subject
     *
     * @see #getSubject()
     * @see #clearSubject()
     */
    public void setSubject (String subject)
    {
        this.subject = subject;
    }

    /**
     * Get the subject of this message.
     *
     * @return The text subject of the message, or null if there is no subject
     *
     * @see #setSubject(String)
     * @see #clearSubject()
     */
    public String getSubject()
    {
        return this.subject;
    }

    /**
     * Clear this message's subject. Calling this method is equivalent to
     * calling <tt>setSubject()</tt> with a null parameter.
     *
     * @see #setSubject(String)
     * @see #getSubject()
     */
    public void clearSubject()
    {
        setSubject (null);
    }

    /**
     * Set the sender's email address. If not set, the sender's address is
     * computed, by <tt>send()</tt>, from the Java "user.name" property
     * and the SMTP host name. The address can be any RFC822-compliant
     * email address. Examples include:
     *
     * <blockquote>
     * <pre>
     * vangogh@example.com
     * Vincent Van Gogh <vangogh@example.com>
     * "Vincent Van Gogh" <vangogh@example.com>
     * vangogh@example.com (Vincent Van Gogh)
     * </pre>
     * </blockquote>
     *
     * @param senderAddress   the new sender address value, or null to
     *                        clear the sender (and force it to be computed
     *                        as described above)
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #setSender(EmailAddress)
     */
    public void setSender (String senderAddress)
        throws EmailException
    {
        if (senderAddress == null)
            this.sender = null;
        else
            setSender (new EmailAddress (senderAddress));
    }

    /**
     * Set the sender's email address. If not set, the sender's address is
     * computed, by <tt>send()</tt>, from the Java "user.name" property
     * and the SMTP host name. The address can be any RFC822-compliant
     * email address. Examples include:
     *
     * <blockquote>
     * <pre>
     * vangogh@example.com
     * Vincent Van Gogh <vangogh@example.com>
     * "Vincent Van Gogh" <vangogh@example.com>
     * vangogh@example.com (Vincent Van Gogh)
     * </pre>
     * </blockquote>
     *
     * @param senderAddress   the new sender address value, or null to
     *                        clear the sender (and force it to be computed
     *                        as described above)
     *
     * @throws EmailException  improperly formed email address
     *
     * @see #setSender(String)
     */
    public void setSender (EmailAddress senderAddress)
        throws EmailException
    {
        this.sender = senderAddress;
    }

    /**
     * Get the email address that will be used as the sender. If
     * <tt>setSender</tt> has not been called, then the sender address is
     * computed from the Java "user.name" property and the current host
     * name.
     *
     * @return the <tt>EmailAddress</tt> of the sender
     *
     * @throws EmailException   error calculating sender address
     *
     * @see EmailAddress
     */
    public EmailAddress getSender()
        throws EmailException
    {
        EmailAddress result = null;

        if (this.sender != null)
            result = this.sender;

        else
        {
            String userName = System.getProperty ("user.name");

            if (userName == null)
                userName = "unknown";

            try
            {
                InetAddress localhost = InetAddress.getLocalHost();
                result = new EmailAddress (userName
                                         + "@"
                                         + localhost.getHostName());
            }

            catch (UnknownHostException ex)
            {
                throw new EmailException ("Can't get name of localhost", ex);
            }
        }

        return result;
    }

    /**
     * Clear the message. This method clears all fields except the SMTP
     * host. Specifically, it:
     *
     * <ul>
     *   <li> Clears the lists of To, Cc and Bcc recipients
     *   <li> Clears the text part
     *   <li> Clears and removes all attachments
     *   <li> Clears the sender field
     * </ul>
     *
     * @see #clearTo()
     * @see #clearBcc()
     * @see #clearCc()
     * @see #clearAllRecipients()
     * @see #clearText
     */
    public void clear()
    {
        clearTo();
        clearBcc();
        clearCc();
        clearAllAttachments();
        clearText();
        this.sender = null;
    }

    /*----------------------------------------------------------------------*\
                          Package-visible Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the attachments for this message. Each element in the returned
     * <tt>List</tt> is a Java Mail API <tt>MimeBodyPart</tt> object.
     *
     * @return the list of <tt>MimeBodyPart</tt> objects. The list will
     *         be empty (but not null) if there are no attachments
     */
    List getAttachments()
    {
        return attachments;
    }

    /**
     * Get the raw list of "To:" addresses, without making a copy of its
     * contents, as <tt>getTo()</tt> does.
     *
     * @return the list of "To:" addresses
     */
    Collection getToAddresses()
    {
        return to;
    }

    /**
     * Get the raw list of "Cc:" addresses, without making a copy of its
     * contents, as <tt>getCc()</tt> does.
     *
     * @return the list of "Cc:" addresses
     */
    Collection getCcAddresses()
    {
        return cc;
    }

    /**
     * Get the raw list of "Bcc:" addresses, without making a copy of its
     * contents, as <tt>getBcc()</tt> does.
     *
     * @return the list of "Bcc:" addresses
     */
    Collection getBccAddresses()
    {
        return bcc;
    }

    /**
     * Get the text part of the message, as a <tt>MimeBodyPart</tt>
     *
     * @return the text part, or null if there isn't one
     */
    MimeBodyPart getTextPart()
    {
        return textPart;
    }

    /**
     * Get the list of additional headers.
     *
     * @return the headers
     */
    List getAdditionalHeaders()
    {
        return additionalHeaders;
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add an email address to a collection of addresses.
     *
     * @param emailAddress  the email address
     * @param coll          the Collection to receive the resulting
     *                      EmailAddress object
     *
     * @throws EmailException  improperly formed email address
     */
    private void addEmailAddress (EmailAddress emailAddress,
                                  Collection   coll)
        throws EmailException
    {
        coll.add (emailAddress);
    }

    /**
     * Add an array of email addresses to a collection.
     *
     * @param addresses the string array of email addresses
     * @param coll      where to store the resulting EmailAddress objects
     *
     * @throws EmailException  improperly formed email address
     */
    private void addEmailAddresses (String[]   addresses,
                                    Collection coll)
        throws EmailException
    {
        for (int i = 0; i < addresses.length; i++)
            addEmailAddress (new EmailAddress (addresses[i]), coll);
    }

    /**
     * Traverse a collection containing either String or EmailAddress
     * objects, and copy each address into the target collection as an
     * EmailAddress.
     *
     * @param sourceColl    the source Collection, containing either String
     *                      or EmailAddress objects
     * @param targetColl    the target Collection, to receive corresponding
     *                      EmailAddress objects
     *
     * @throws EmailException  improperly formed email address
     */
    private void addEmailAddresses (Collection sourceColl,
                                    Collection targetColl)
        throws EmailException
    {
        for (Iterator it = sourceColl.iterator(); it.hasNext(); )
        {
            Object o = it.next();

            if (o instanceof String)
                targetColl.add (new EmailAddress ((String) o));
            else if (o instanceof EmailAddress)
                targetColl.add (o);
            else
            {
                throw new EmailException ("BUG: Expected object of type "
                                        + "\"String\" or \"EmailAddress\" "
                                        + "in Collection of email addresses, "
                                        + "but found object of type \""
                                        + o.getClass().getName());
            }
        }
    }

    /**
     * Take a Collection of EmailAddress objects and transform it into
     * one that can safely be returned to the caller.
     *
     * @param coll   The Collection to transform
     *
     * @return A new Collection containing EmailAddress objects.
     *
     * @throws EmailException  error on copy
     */
    private Collection getEmailAddresses (Collection coll)
        throws EmailException
    {
        Collection result = new ArrayList();

        for (Iterator it = coll.iterator(); it.hasNext(); )
            result.add (new EmailAddress ((EmailAddress) it.next()));

        return result;
    }

    /**
     * Initialize the list of additional headers with ours. The calling
     * application is free to add other headers of its own.
     */
    private void initAdditionalHeaders()
    {
        for (int i = 0; i < CUSTOM_HEADERS.length; i++)
            additionalHeaders.add (CUSTOM_HEADERS[i]);
    }

    /**
     * Make a MIME body part from a File object. The MIME type is taken
     * from the file's extension.
     *
     * @param file   The <tt>File</tt> object from which to get the text
     *
     * @throws EmailException      file doesn't exist
     * @throws MessagingException  Java Mail API error
     *
     * @see makeTextBodyPart(String)
     * @see makeTextBodyPart(String,String)
     * @see makeTextBodyPart(String[],String)
     * @see makeBodyPart(InputStream,String)
     * @see makeTextBodyPart(Iterator)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeBodyPart (File file)
        throws MessagingException,
               EmailException
    {
        return makeBodyPart (file, null);
    }

    /**
     * Make a MIME body part from a File object. The MIME type is taken
     * from the file's extension.
     *
     * @param file      The <tt>File</tt> object from which to get the text
     * @param mimeType  The MIME type (or content type) to associate with
     *                  the contents of the file. If this parameter is
     *                  null, the content type will be inferred from the
     *                  file's extension.
     *
     * @throws EmailException      file doesn't exist
     * @throws MessagingException  Java Mail API error
     *
     * @see makeTextBodyPart(String)
     * @see makeTextBodyPart(String,String)
     * @see makeTextBodyPart(String[],String)
     * @see makeBodyPart(InputStream,String)
     * @see makeTextBodyPart(Iterator)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeBodyPart (File file, String mimeType)
        throws MessagingException,
               EmailException
    {
        // A file type map that unconditionally returns a specified MIME type
        class MyFileTypeMap extends FileTypeMap
        {
            private String mimeType;

            MyFileTypeMap (String mimeType)
            {
                this.mimeType = mimeType;
            }

            public String getContentType (File file)
            {
                return mimeType;
            }

            public String getContentType (String path)
            {
                return mimeType;
            }
        }

        if (! file.exists())
        {
            throw new EmailException ("File \""
                                    + file.getAbsolutePath()
                                    + "\" does not exist");
        }

        MimeBodyPart    bodyPart    = new MimeBodyPart();
        FileDataSource  dataSource  = new FileDataSource (file);
        FileTypeMap     fileTypeMap = null;

        if (mimeType == null)
            fileTypeMap = FileTypeMap.getDefaultFileTypeMap();
        else
            fileTypeMap = new MyFileTypeMap (mimeType);

        dataSource.setFileTypeMap (fileTypeMap);
        bodyPart.setDataHandler (new DataHandler (dataSource));

        // Only add the file name if we're not composing a
        // multipart/alternative message.

        if (multipartSubtype != MULTIPART_ALTERNATIVE)
            bodyPart.setFileName (file.getName());

        return bodyPart;
    }

    /**
     * Make a body part from an <tt>InputStream</tt> object.
     *
     * @param is       The <tt>InputStream</tt> whose contents are to be read
     *                 and used to create the body part
     * @param mimeType The MIME type for the contents
     *
     * @return the MimeBodyPart
     *
     * @throws IOException        Can't create temporary file
     * @throws MessagingException Java Mail API exception
     * @throws EmailException     Some other error
     *
     * @see makeTextBodyPart(String)
     * @see makeTextBodyPart(String,String)
     * @see makeTextBodyPart(String[],String)
     * @see makeTextBodyPart(Iterator)
     * @see makeTextBodyPart(Iterator,String)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeBodyPart (InputStream is, String mimeType)
        throws EmailException,
               MessagingException,
               IOException
    {
        // The attachment must be stored as a DataHandler, which contains a
        // DataSource. We could write an InputStreamDataSource that wraps
        // the input stream, but that wouldn't work in all cases. The
        // documentation for DataSource.getInputStream() requires that each
        // call to the method produce a new InputStream that's positioned
        // at the beginning--which implies that DataSource.getInputStream()
        // might be called more than once. Since there's no reliable way to
        // rewind an arbitrary InputStream, we have to copy the contents of
        // the InputStream to a temporary file, and wrap the temporary file
        // in a FileDataSource. The destructor for this class ensures that
        // the temporary files are deleted.

        File tempFile = File.createTempFile ("mail", null);
        tempFile.deleteOnExit();
        temporaryFiles.add (tempFile);

        OutputStream os = new BufferedOutputStream
                                    (new FileOutputStream (tempFile));

        int b;

        while ( (b = is.read()) != -1 )
            os.write (b);

        os.close();

        // Now, create the attachment from the temporary file.

        if (mimeType == null)
            mimeType = "application/octet-stream";

        return makeBodyPart (tempFile, mimeType);
    }

    /**
     * Make a text body part from a <tt>String</tt> object. The
     * corresponding MIME type is assumed to be "text/plain".
     *
     * @param text   The string to use as the body part's contents. It may
     *               contain multiple lines.
     *
     * @return the MimeBodyPart for the text
     *
     * @throws MessagingException Java Mail API error
     *
     * @see makeTextBodyPart(String,String)
     * @see makeTextBodyPart(String[])
     * @see makeTextBodyPart(String[],String)
     * @see makeBodyPart(InputStream,String)
     * @see makeTextBodyPart(Iterator)
     * @see makeTextBodyPart(Iterator,String)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeTextBodyPart (String text)
        throws MessagingException
    {
        return makeTextBodyPart (text, "text/plain");
    }

    /**
     * Make a text body part from a <tt>String</tt> object.
     *
     * @param text     The string to use as the text of the message. It may
     *                 contain multiple lines.
     * @param mimeType The MIME type for the text
     *
     * @return the MimeBodyPart for the text
     *
     * @throws MessagingException Java Mail API error
     *
     * @see makeTextBodyPart(String)
     * @see makeTextBodyPart(String[])
     * @see makeTextBodyPart(String[],String)
     * @see makeBodyPart(InputStream,String)
     * @see makeTextBodyPart(Iterator)
     * @see makeTextBodyPart(Iterator,String)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeTextBodyPart (String text, String mimeType)
        throws MessagingException
    {
        MimeBodyPart      bodyPart   = new MimeBodyPart();
        StringDataSource  dataSource;

        text = newlineTerminatedString (text);
        dataSource = new StringDataSource (text, mimeType, null);

        bodyPart.setDataHandler (new DataHandler (dataSource));
        return bodyPart;
    }

    /**
     * Make a text body part from an array of <tt>String</tt>
     * object, each of which is assumed to represent a single line. The
     * corresponding MIME type is assumed to be "text/plain".
     *
     * @param text The strings to use as the text of the message.
     *
     * @return the MimeBodyPart for the text
     *
     * @throws MessagingException Java Mail API error
     *
     * @see makeTextBodyPart(String)
     * @see makeTextBodyPart(String,String)
     * @see makeTextBodyPart(String[],String)
     * @see makeBodyPart(InputStream,String)
     * @see makeTextBodyPart(Iterator)
     * @see makeTextBodyPart(Iterator,String)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeTextBodyPart (String[] text)
        throws MessagingException
    {
        // Quick and dirty approach

        StringWriter  w  = new StringWriter();
        PrintWriter   pw = new PrintWriter (w);

        for (int i = 0; i < text.length; i++)
            pw.println (text[i]);

        return makeTextBodyPart (w.toString());
    }

    /**
     * Make a text body part from an array of <tt>String</tt>
     * objects, each of which is assumed to represent a single line. 
     *
     * @param text     The strings to use as the text of the message.
     * @param mimeType The MIME type for the text
     *
     * @return the MimeBodyPart for the text
     *
     * @throws MessagingException Java Mail API error
     *
     * @see makeTextBodyPart(String)
     * @see makeTextBodyPart(String,String)
     * @see makeTextBodyPart(String[],String)
     * @see makeBodyPart(InputStream,String)
     * @see makeTextBodyPart(Iterator)
     * @see makeTextBodyPart(Iterator,String)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeTextBodyPart (String[] text, String mimeType)
        throws MessagingException
    {
        // Quick and dirty approach

        StringWriter  w  = new StringWriter();
        PrintWriter   pw = new PrintWriter (w);

        for (int i = 0; i < text.length; i++)
            pw.println (text[i]);

        return makeTextBodyPart (w.toString(), mimeType);
    }

    /**
     * Make a text body part from an <tt>Iterator</tt> of <tt>String</tt>
     * objects, each of which is assumed to represent a single line. The
     * corresponding MIME type is assumed to be "text/plain".
     *
     * @param iterator The <tt>Iterator</tt> that will return the
     *                 <tt>String</tt> objects that represent the text lines
     *
     * @return the MimeBodyPart for the text
     *
     * @throws MessagingException Java Mail API error
     *
     * @see makeTextBodyPart(String)
     * @see makeTextBodyPart(String,String)
     * @see makeTextBodyPart(String[],String)
     * @see makeBodyPart(InputStream,String)
     * @see makeTextBodyPart(Iterator,String)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeTextBodyPart (Iterator iterator)
        throws MessagingException
    {
        // Quick and dirty approach

        StringWriter  w  = new StringWriter();
        PrintWriter   pw = new PrintWriter (w);

        while (iterator.hasNext())
            pw.println ((String) iterator.next());

        return makeTextBodyPart (w.toString());
    }

    /**
     * Make a text body part from an <tt>Iterator</tt> of <tt>String</tt>
     * objects, each of which is assumed to represent a single line.
     *
     * @param iterator The <tt>Iterator</tt> that will return the
     *                 <tt>String</tt> objects that represent the text lines
     * @param mimeType The MIME type for the text
     *
     * @return the MimeBodyPart for the text
     *
     * @throws MessagingException Java Mail API error
     *
     * @see makeTextBodyPart(String)
     * @see makeTextBodyPart(String,String)
     * @see makeTextBodyPart(String[],String)
     * @see makeBodyPart(InputStream,String)
     * @see makeTextBodyPart(Iterator)
     * @see makeTextBodyPart(File)
     */
    private MimeBodyPart makeTextBodyPart (Iterator iterator, String mimeType)
        throws MessagingException
    {
        // Quick and dirty approach

        StringWriter  w = new StringWriter();
        PrintWriter   pw = new PrintWriter (w);

        while (iterator.hasNext())
            pw.println ((String) iterator.next());

        return makeTextBodyPart (w.toString(), mimeType);
    }

    /**
     * Ensures that a string has a terminating newline.
     *
     * @param s  The string to check.
     *
     * @return The (possibly new) string.
     */
    private String newlineTerminatedString (String s)
    {
        StringWriter  w  = new StringWriter();
        PrintWriter   pw = new PrintWriter (w);

        pw.println (s);

        return w.toString();
    }
}
