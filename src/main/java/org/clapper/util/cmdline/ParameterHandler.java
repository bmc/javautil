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

package org.clapper.util.cmdline;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Thie interface defines the callback methods used by a
 * {@link ParameterParser} object, when its 
 * {@link ParameterParser#parse parse()} method is called.
 *
 * @see ParameterParser
 * @see CommandLineUtility
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public interface ParameterHandler
{
    /*----------------------------------------------------------------------*\
                                 Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Handles a parsed option.
     *
     * @param shortOption  the (character) short option, if any; otherwise,
     *                     the constant {@link UsageInfo#NO_SHORT_OPTION}.
     * @param longOption   the (string) long option, if any; otherwise,
     *                     null.
     * @param it           An <tt>Iterator</tt> from which to retrieve any
     *                     value(s) for the option
     *
     * @throws CommandLineUsageException  on error
     * @throws NoSuchElementException     attempt to iterate past end of args;
     *                                    {@link ParameterParser#parse}
     *                                    automatically handles this exception,
     *                                    so it's safe for implementations of
     *                                    this method not to handle it
     */
    public void parseOption(char             shortOption,
                            String           longOption, 
                            Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException;

    /**
     * Handles all parameters that appear after the end of the options. If there
     * are no such parameters, the implementation of this method should just
     * return without doing anything.
     *
     * @param it  the <tt>Iterator</tt> containing the parameters
     *
     * @throws CommandLineUsageException  on error
     * @throws NoSuchElementException     attempt to iterate past end of args;
     *                                    {@link ParameterParser#parse}
     *                                    automatically handles this exception,
     *                                    so it's safe for implementations of
     *                                    this method not to handle it
     */
    public void parsePostOptionParameters(Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException;
}
