package org.clapper.util.mail;

import java.io.PrintStream;

/**
 * <p><tt>EmailTransport</tt> defines the interface for classes that can
 * send <tt>EmailMessage</tt> objects.</p>
 *
 * @see EmailMessage
 */
public interface EmailTransport
{
    /*----------------------------------------------------------------------*\
                             Required Methods
    \*----------------------------------------------------------------------*/

    /**
     * Send an <tt>EmailMessage</tt>. This <tt>EmailTransport</tt> object
     * is assumed to have been configured with the appropriate parameters.
     *
     * @param message  the message to send
     *
     * @throws EmailException  failed to send message
     */
    public void send (EmailMessage message)
        throws EmailException;

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
    public void setDebug (boolean debug, PrintStream out);
}
