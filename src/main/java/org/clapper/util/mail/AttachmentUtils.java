package org.clapper.util.mail;

import java.util.Date;

/**
 * Singleton class containing attachment-related methods.
 */
class AttachmentUtils
{
    /*----------------------------------------------------------------------*\
                             Private Constants
    \*----------------------------------------------------------------------*/

    /**
     * The extension to use on generated names.
     */
    private static final String DEFAULT_EXTENSION = ".dat";

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * A counter that is unique within the VM.
     */
    private static int counter = 0;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private AttachmentUtils()
    {
        // Can't be instantiated.
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    /**
     * Generate a unique attachment name.
     *
     * @return The name.
     */
    protected static synchronized String generateAttachmentName()
    {
        Date now = new Date();

        counter++;

        return new String (now.getTime() + counter + DEFAULT_EXTENSION);
    }
}
