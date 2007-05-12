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

/**
 * Constant instances of this class are used to tell an
 * <tt>EmailMessage</tt> object the type of the multipart email message
 * ("mixed" or "alternative").
 *
 * @see EmailMessage#MULTIPART_MIXED
 * @see EmailMessage#MULTIPART_ALTERNATIVE
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
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
                              Public Methods
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

    /**
     * Get the hash code for this object.
     *
     * @return the hash code
     */
    public int hashCode()
    {
        return subtype.hashCode();
    }
}
