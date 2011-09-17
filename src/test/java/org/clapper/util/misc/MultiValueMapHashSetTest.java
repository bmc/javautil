package org.clapper.util.misc;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 */
public class MultiValueMapHashSetTest extends MultiValueMapTestBase
{
    /*----------------------------------------------------------------------*\
                                 Constants
    \*----------------------------------------------------------------------*/

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    public MultiValueMapHashSetTest()
    {
    }

    /*----------------------------------------------------------------------*\
                             Protected Methods
    \*----------------------------------------------------------------------*/

    protected MultiValueMap<String,String> newMultiValueMap()
    {
        return new MultiValueMap<String,String>
            (new MultiValueMap.ValuesCollectionAllocator<String>()
                {
                    public Collection<String> newValuesCollection()
                    {
                        return new HashSet<String>();
                    }
                });
            
    }
}
