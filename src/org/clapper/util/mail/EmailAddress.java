/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.mail;

import java.util.Collection;

/*
import java.io.*;
import java.net.*;
import javax.mail.*;
*/
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
//import javax.activation.*;

/**
 * This class encodes an RFC822-compliant email address is a simplified
 * container, hiding the gory details of the underlying Java Mail API.
 *
 * @see EmailMessage
 *
 * @version <tt>$Revision$</tt>
 * @level developer
 */
public class EmailAddress implements Cloneable, Comparable
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * The address. The user doesn't get to see this.
     */
    private InternetAddress emailAddress = null;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

    /**
     * Constructs a new <tt>EmailMessage</tt> object from an
     * <tt>InternetAddress</tt> object. Only used internally.
     *
     * @param address   the address
     *
     * @throws EmailException  bad email address
     */
    EmailAddress (InternetAddress address)
        throws EmailException
    {
        this.emailAddress = address;
    }

    /**
     * Constructs a new <tt>EmailMessage</tt> object from an email address
     * string. The address string can be any RFC822-compliant email address.
     * Examples include:
     *
     * <blockquote>
     * <pre>
     * renoir@example.com
     * Pierre Auguste Renoir <renoir@example.com>
     * "Pierre Auguste Renoir" <renoir@example.com>
     * renoir@example.com (Pierre Auguste Renoir)
     * </pre>
     * </blockquote>
     *
     * The <i>@domain</i> portion must be present.
     *
     * @param address   the address
     *
     * @throws EmailException  bad email address
     */
    public EmailAddress (String address)
        throws EmailException
    {
        try
        {
            // The InternetAddress class in the Java Mail 1.3 API has a
            // constructor that does strict parsing. But the 1.1 version
            // doesn't. However, we can use the parse() method to do the
            // checking; it does do strict RFC822 syntax checks.

            InternetAddress[] addresses = InternetAddress.parse (address);

            if (addresses.length != 1)
            {
                throw new EmailException ("\""
                                        + address
                                        + "\" is an improperly formed "
                                        + "email address");
            }

            this.emailAddress = addresses[0];
        }

        catch (AddressException ex)
        {
            throw new EmailException ("\""
                                    + address
                                    + "\" is an improperly formed "
                                    + "email address",
                                      ex);
        }
    }

    /**
     * Constructs a new copy of an existing <tt>EmailMessage</tt> object.
     *
     * @param emailAddress The <tt>EmailMessage</tt> object to copy
     *
     * @throws EmailException  bad email address
     */
    public EmailAddress (EmailAddress emailAddress)
        throws EmailException
    {
        this (emailAddress.getInternetAddress());
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether this email address is equivalent to another email
     * address, by comparing the normalized address strings.
     *
     * @param obj  the other email address. Must be an <tt>EmailAddress</tt>
     *             object.
     *
     * @return <tt>true</tt> if the addresses are equivalent, <tt>false</tt>
     *         otherwise
     */
    public boolean equals (Object obj)
    {
        return (this.compareTo (obj) == 0);
    }

    /**
     * Compare two email addresses.
     *
     * @param obj  the other email address. Must be an <tt>EmailAddress</tt>
     *             object.
     *
     * @return A negative number if this email address is lexicographically
     *         less than <tt>obj</tt>; 0 if the two addresses are
     *         equivalent; a postive number if this email address is
     *         lexicographically greater than <tt>obj</tt>.
     */
    public int compareTo (Object obj)
    {
        EmailAddress other = (EmailAddress) obj;

        return this.getAddress().compareToIgnoreCase (other.getAddress());
    }

    /**
     * Get the RFC822-compliant email address string associated with this
     * <tt>EmailAddress</tt> object.
     *
     * @return the email address string. This method will never return null
     *
     * @see #getInternetAddress()
     * @see #getPersonalName()
     */
    public String getAddress()
    {
        return this.emailAddress.getAddress();
    }

    /**
     * Get the user friend personal name associated with this
     * <tt>EmailAddress</tt> object.
     *
     * @return the personal name, or null if not present
     *
     * @see #getAddress()
     */
    public String getPersonalName()
    {
        return this.emailAddress.getPersonal();
    }

    /**
     * Parse a sequence of comma-separated email addresses into individual
     * <tt>EmailAddress</tt> objects, enforcing strict Internet RFC822
     * email address syntax requirements. The resulting
     * <tt>EmailAddress</tt> objects are stored in a supplied
     * <tt>Collection</tt>.
     *
     * @param s           comma-separated address strings
     * @param collection  where to stored the parsed <tt>EmailAddress</tt>
     *                    objects
     *
     * @return the number of addresses parsed from the string
     *
     * @throws EmailException  if a bad address is encountered
     */
    public static int parse (String s, Collection collection)
        throws EmailException
    {
        int total = 0;

        try
        {
            InternetAddress[] ia = InternetAddress.parse (s, true);
            total = ia.length;
            for (int i = 0; i < total; i++)
                collection.add (new EmailAddress (ia[i]));
        }

        catch (AddressException ex)
        {
            throw new EmailException ("One or more email addresses in the "
                                    + "string \""
                                    + s
                                    + "\" cannot be parsed.",
                                      ex);
        }

        return total;
    }

    /**
     * Convert this object into an RFC822-compliant email address.
     *
     * @return the email address
     */
    public String toString()
    {
        return this.emailAddress.toString();
    }

    /**
     * Clone this object.
     *
     * @return the clone
     */
    public Object clone() throws CloneNotSupportedException
    {
        Object result = null;

        try
        {
            result = new EmailAddress (this);
        }

        catch (EmailException ex)
        {
            result = null;
        }

        return result;
    }

    /*----------------------------------------------------------------------*\
                          Package-visible Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the RFC822-compliant email address associated with this
     * <tt>EmailAddress</tt> object, as an <tt>InternetAddress</tt> object.
     *
     * @return the email address string. This method will never return null
     *
     * @see #getAddress()
     * @see #getPersonalName()
     */
    InternetAddress getInternetAddress()
    {
        return this.emailAddress;
    }
}
