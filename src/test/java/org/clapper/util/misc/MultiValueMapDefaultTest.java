package org.clapper.util.misc;

/**
 *
 */
public class MultiValueMapDefaultTest extends MultiValueMapTestBase
{
    /*----------------------------------------------------------------------*\
                                 Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    public MultiValueMapDefaultTest()
    {
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    protected MultiValueMap<String,String> newMultiValueMap()
    {
        return new MultiValueMap<String,String>();
    }
}
