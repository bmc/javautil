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

package org.clapper.util.classutil;

/**
 * <tt>NotClassFilter</tt> is a {@link ClassFilter} that
 * wraps another {@link ClassFilter} and negates the sense of the
 * wrapped filter's {@link ClassFilter#accept accept()} method. This
 * class conceptually provides a logical "NOT" operator for class name
 * filters. For example, the following code fragment will create a filter
 * that finds all classes that are not interfaces.
 *
 * <blockquote><pre>
 * NotClassFilter filter = new NotClassFilter (new InterfaceOnlyClassFilter());
 * </pre></blockquote>
 *
 * @see ClassFilter
 * @see AndClassFilter
 * @see OrClassFilter
 * @see ClassFinder
 * @see InterfaceOnlyClassFilter
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2006 Brian M. Clapper
 */
public class NotClassFilter implements ClassFilter
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private ClassFilter filter;

    /*----------------------------------------------------------------------*\
                            Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new <tt>NotClassFilter</tt> that wraps the
     * specified {@link ClassFilter}.
     *
     * @param filter  The {@link ClassFilter} to wrap.
     */
    public NotClassFilter (ClassFilter filter)
    {
        this.filter = filter;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Tests whether a class name should be included in a class name
     * list.
     *
     * @param classInfo   the {@link ClassInfo} object to test
     * @param classFinder the invoking {@link ClassFinder} object
     *
     * @return <tt>true</tt> if and only if the name should be included
     *         in the list; <tt>false</tt> otherwise
     */
    public boolean accept (ClassInfo classInfo, ClassFinder classFinder)
    {
        return ! this.filter.accept (classInfo, classFinder);
    }
}
