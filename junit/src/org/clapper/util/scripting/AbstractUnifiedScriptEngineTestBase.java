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

package org.clapper.util.scripting;

import junit.framework.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Tester for script frameworks.
 */
public class AbstractUnifiedScriptEngineTestBase extends TestCase
{
    private static final String EVAL_LANG = "javascript";
    private static final String EVAL_EXT = ".js";

    private ScriptFrameworkType frameworkType;
    private boolean enabled = true;

    public AbstractUnifiedScriptEngineTestBase(String testName,
                                               ScriptFrameworkType frameworkType)
    {
        super(testName);
        this.frameworkType = frameworkType;
    }

    protected void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void testGetScriptExecutor() throws Exception
    {
        if (enabled)
        {
            UnifiedScriptEngineManager manager =
                UnifiedScriptEngineManager.getManager(frameworkType);
            assertNotNull(manager);
        }
    }

    public void testPut() throws Exception
    {
        if (enabled)
        {
            UnifiedScriptEngineManager manager =
                UnifiedScriptEngineManager.getManager(frameworkType);
            assertNotNull(manager);

            manager.put("foo", "10");
            UnifiedScriptEngine engine = manager.getEngineByName(EVAL_LANG);
            assertNotNull(engine);
            Object result = engine.eval("foo");
            assertEquals("put or eval failed", result.toString(), "10");
        }
    }
    public void testClearBindings() throws Exception
    {
        if (enabled)
        {
            UnifiedScriptEngineManager manager =
                UnifiedScriptEngineManager.getManager(frameworkType);
            assertNotNull(manager);

            manager.put("foo", "10");
            UnifiedScriptEngine engine = manager.getEngineByName(EVAL_LANG);
            Object result = engine.eval("foo");
            assertEquals("put or eval failed", result.toString(), "10");

            manager.clearBindings();
            try
            {
                engine.eval("foo");
                fail("Expected exception referencing undefined variable.");
            }

            catch (Exception ex)
            {
            }
        }
    }

    public void testCompileScript() throws Exception
    {
        if (enabled)
        {
            UnifiedScriptEngineManager manager =
                UnifiedScriptEngineManager.getManager(frameworkType);
            assertNotNull(manager);

            manager.put("foo", "10");
            UnifiedScriptEngine engine = manager.getEngineByName(EVAL_LANG);

            UnifiedCompiledScript compiledScript =
                engine.compile("println(\"The cow says, 'foo'\")");

            if (compiledScript != null)
            {
                manager.put("foo", "1000");
                engine.exec(compiledScript);
            }
        }
    }

    public void testExecScript() throws Exception
    {
       if (enabled)
        {
            UnifiedScriptEngineManager manager =
                UnifiedScriptEngineManager.getManager(frameworkType);
            assertNotNull(manager);

            File scriptFile = File.createTempFile("junit", EVAL_EXT);
            scriptFile.deleteOnExit();
            try
            {
                FileWriter so = new FileWriter(scriptFile);
                PrintWriter sop = new PrintWriter(so);

                sop.println("out.println(\"foo=\" + foo)");
                sop.println("out.println(\"bar=\" + bar)");
                sop.println("if (foo == bar) out.println(\"They're equal\")");

                so.close();

                manager.put("foo", Integer.valueOf(1));
                manager.put("bar", Integer.valueOf(100));
                manager.put("out", System.out);
                UnifiedScriptEngine engine = manager.getEngineByName(EVAL_LANG);
                engine.exec(scriptFile);
                manager.put("bar", Integer.valueOf(1));
                engine.exec(scriptFile);
            }

            finally
            {
                scriptFile.delete();
            }
        }
    }
}
