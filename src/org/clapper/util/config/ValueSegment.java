/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.config;

import org.clapper.util.text.XStringBuffer;

/**
 * A variable value segment. This is just a substring, with a flag that
 * indicates whether the segment is literal or not.
 */
class ValueSegment
{
    XStringBuffer  segmentBuf = new XStringBuffer();
    boolean        isLiteral  = false;

    ValueSegment()
    {
    }

    void append (char ch)
    {
        segmentBuf.append (ch);
    }

    int length()
    {
        return segmentBuf.length();
    }
}
