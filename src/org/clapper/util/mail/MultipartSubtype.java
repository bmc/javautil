/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.mail;

/**
 * Constant instances of this class are used to tell an
 * <tt>EmailMessage</tt> object the type of the multipart email message
 * ("mixed" or "alternative").
 *
 * @see EmailMessage#MULTIPART_MIXED
 * @see EmailMessage#MULTIPART_ALTERNATIVE
 *
 * @version <tt>$Revision$</tt>
 */
public final class MultipartSubtype
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private String subtype;

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * Constructor. Only accessible within this package.
     *
     * @param subtype  the subtype string, which is meaningful to the Java
     *                 Mail API
     */
    MultipartSubtype (String subtype)
    {
        this.subtype = subtype;
    }

    /**
     * Get the associated subtype string, which is meaningful to the
     * Java Mail API. Only accessible within this package.
     *
     * @return the subtype string
     */
    String getSubtypeString()
    {
        return subtype;
    }

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    /**
     * Determine whether this object is equivalent to another. Note that,
     * when used to define constants, it's generally fine to use Java's
     * "==" operator, since the constants will only be defined once.
     *
     * @param other  the other object
     *
     * @return <tt>true</tt> if they're equivalent, <tt>false</tt> otherwise
     */
    public boolean equals (Object other)
    {
        MultipartSubtype that = (MultipartSubtype) other;
        return ((this == that) || (this.subtype.equals (that.subtype)));
    }
}
