/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.mail;

/**
 * Constant instances of this class are used to tell an
 * <tt>OutgoingMailMessage</tt> object the type of the multipart email message
 * ("mixed" or "alternative").
 *
 * @see OutgoingMailMessage#MULTIPART_MIXED
 * @see OutgoingMailMessage#MULTIPART_ALTERNATIVE
 *
 * @version <tt>$Revision$</tt>
 */
public final class MultipartSubtype
{
    private String subType;

    MultipartSubtype (String subType)
    {
        this.subType = subType;
    }

    String getSubtypeString()
    {
        return subType;
    }

    public boolean equals (Object other)
    {
        MultipartSubtype that = (MultipartSubtype) other;
        return ((this == that) || (this.subType.equals (that.subType)));
    }
}
