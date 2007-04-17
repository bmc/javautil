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

import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 */
public class UnixShellVariableSubstituterTest extends TestCase
{
    public UnixShellVariableSubstituterTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    /**
     * Test of variable substitution.
     */
    public void testSubstitution()
        throws VariableSubstitutionException
    {
        UnixShellVariableSubstituter sub = new UnixShellVariableSubstituter();
        Map<String,String> vars = new HashMap<String,String>();
        vars.put("a", "1");
        vars.put("b", "2");
        vars.put("foo", "barsky");
        vars.put("bar", "a very long value that goes from here to there");
        vars.put("y", "");
        vars.put("longerVariableName", " ");
        MapVariableDereferencer deref = new MapVariableDereferencer(vars);

        String s = sub.substitute("ab$a", deref);
        assertEquals("Substitution failure", "ab1", s);

        s = sub.substitute("${a", deref);
        assertEquals("Substitution failure", "${a", s);

        s = sub.substitute("$a$b$c", deref);
        assertEquals("Substitution failure", "12", s);

        s = sub.substitute("$a$b$foo", deref);
        assertEquals("Substitution failure", "12barsky", s);

        s = sub.substitute("$x", deref);
        assertEquals("Substitution failure", "", s);

        s = sub.substitute("$x?abc", deref);
        assertEquals("Substitution failure", "?abc", s);

        s = sub.substitute("${x?abc}", deref);
        assertEquals("Substitution failure", "abc", s);

        s = sub.substitute("${y?abc def ghi}", deref);
        assertEquals("Substitution failure", "abc def ghi", s);

        s = sub.substitute("${y??}", deref);
        assertEquals("Substitution failure", "?", s);

        s = sub.substitute("${longerVariableName?foo}", deref);
        assertFalse("Substitution failure", "foo".equals(s));
        assertEquals("Substitution failure", " ", s);

        sub.setAbortOnUndefinedVariable(true);
        try
        {
            s = sub.substitute("${blah}", deref);
            fail("Expected UnknownVariableException");
        }
        catch (UndefinedVariableException ex)
        {
            System.out.println("Got expected exception:");
            ex.printStackTrace(System.out);
        }

        sub.setAbortOnSyntaxError(true);
        try
        {
            s = sub.substitute("${foo", deref);
            fail("Expected VariableSyntaxException");
        }

        catch (VariableSyntaxException ex)
        {
            System.out.println("Got expected exception:");
            ex.printStackTrace(System.out);
        }
    }
}