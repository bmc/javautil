/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:
 
  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.
 
  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:
 
  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
 
  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:
 
        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2006 Brian M. Clapper."
 
      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.
 
  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.
 
  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M.a Clapper.
 
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

package org.clapper.util.text;

import junit.framework.*;

/**
 *
 */
public class TextUtilRomanNumberTest extends TestCase
{
    public TextUtilRomanNumberTest(String testName)
    {
        super(testName);
    }

    public void testRomanNumerals()
    {
        assertEquals("Failed", "I", TextUtil.romanNumeralsForNumber(1));
        assertEquals("Failed", "IV", TextUtil.romanNumeralsForNumber(4));
        assertEquals("Failed", "V", TextUtil.romanNumeralsForNumber(5));
        assertEquals("Failed", "IX", TextUtil.romanNumeralsForNumber(9));
        assertEquals("Failed", "X", TextUtil.romanNumeralsForNumber(10));
        assertEquals("Failed", "XV", TextUtil.romanNumeralsForNumber(15));
        assertEquals("Failed", "XVIII", TextUtil.romanNumeralsForNumber(18));
        assertEquals("Failed", "XIX", TextUtil.romanNumeralsForNumber(19));
        assertEquals("Failed", "XX", TextUtil.romanNumeralsForNumber(20));
        assertEquals("Failed", "XL", TextUtil.romanNumeralsForNumber(40));
        assertEquals("Failed", "L", TextUtil.romanNumeralsForNumber(50));
        assertEquals("Failed", "LXXI", TextUtil.romanNumeralsForNumber(71));
        assertEquals("Failed", "CD", TextUtil.romanNumeralsForNumber(400));
        assertEquals("Failed", "D", TextUtil.romanNumeralsForNumber(500));
        assertEquals("Failed", "CM", TextUtil.romanNumeralsForNumber(900));
        assertEquals("Failed", "M", TextUtil.romanNumeralsForNumber(1000));
        assertEquals("Failed", "MCMLXI", TextUtil.romanNumeralsForNumber(1961));
    }
}
