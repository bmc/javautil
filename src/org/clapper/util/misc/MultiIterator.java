/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The <tt>MultiIterator</tt> class provides a way to iterate over multiple
 * <tt>Collection</tt>, <tt>Enumeration</tt> and <tt>Iterator</tt> objects
 * at once. You instantiate a <tt>MultiIterator</tt> object and add one or
 * more <tt>Collection</tt>, <tt>Enumeration</tt> or <tt>Iterator</tt>
 * objects to it; when you use the iterator, it iterates over the contents
 * of each contained composite object, one by one, in the order they were
 * added to the <tt>MultiIterator</tt>. When the iterator reaches the end
 * of one object's contents, it moves on to the next object, until no more
 * composite objects are left.
 *
 * @see java.util.Iterator
 * @see java.util.Enumeration
 * @see java.util.Collection
 *
 * @version <tt>$Revision$</tt>
 */
public class MultiIterator implements Iterator
{
    /*----------------------------------------------------------------------*\
                           Private Data Elements
    \*----------------------------------------------------------------------*/

    /**
     * The underlying objects being iterated over, stored in a Collection
     */
    private Collection aggregation = new ArrayList();

    /**
     * The iterator for the list of aggregation
     */
    private Iterator aggregationIterator = null;

    /**
     * The iterator for the current object
     */
    private Iterator it = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Allocate a new <tt>MultiIterator</tt> object.
     */
    public MultiIterator()
    {
    }

    /**
     * Allocate a new <tt>MultiIterator</tt> object that will iterate, in
     * turn, over the contents of each <tt>Collection</tt> in the supplied
     * array.
     *
     * @param array  The <tt>Collection</tt>s over which to iterate
     *
     * @see #addCollection(Collection)
     */
    public MultiIterator (Collection array[])
    {
        for (int i = 0; i < array.length; i++)
            aggregation.add (array[i]);
    }

    /**
     * Allocate a new <tt>MultiIterator</tt> object that will iterate, in
     * turn, over the contents of each <tt>Collection</tt> in the supplied
     * <tt>Collection</tt>
     *
     * @param coll  A <tt>Collection</tt> of <tt>Collection</tt> objects
     *
     * @see #addCollection(Collection)
     */
    public MultiIterator (Collection coll)
    {
        for (Iterator iterator = coll.iterator(); iterator.hasNext(); )
            aggregation.add ((Collection) iterator.next());
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Add a <tt>Collection</tt> to the end of the list of composite
     * objects being iterated over. It's safe to call this method while
     * iterating, as long as you haven't reached the end of the last
     * composite object currently in the iterator.
     *
     * @param collection  The <tt>Collection</tt> to add.
     *
     * @see #addIterator
     * @see #addEnumeration
     */
    public void addCollection (Collection collection)
    {
        aggregation.add (collection);
    }

    /**
     * Add an <tt>Iterator</tt> to the end of the list of composite objects
     * being iterated over. It's safe to call this method while iterating,
     * as long as you haven't reached the end of the last composite object
     * currently in the iterator.
     *
     * @param iterator  The <tt>Iterator</tt> to add.
     *
     * @see #addCollection
     * @see #addEnumeration
     */
    public void addIterator (Iterator iterator)
    {
        aggregation.add (iterator);
    }

    /**
     * Add an <tt>Enumeration</tt> to the end of the list of composite
     * objects being iterated over. It's safe to call this method while
     * iterating, as long as you haven't reached the end of the last
     * composite object currently in the iterator.
     *
     * @param enumeration  The <tt>Enumeration</tt> to add.
     *
     * @see #addCollection
     * @see #addIterator
     */
    public void addEnumeration (Enumeration enumeration)
    {
        aggregation.add (enumeration);
    }

    /**
     * Determine whether the underlying <tt>Iterator</tt> has more
     * elements.
     *
     * @return <tt>true</tt> if and only if a call to
     *         <tt>nextElement()</tt> will return an element,
     *         <tt>false</tt> otherwise.
     *
     * @see #nextElement()
     */
    public boolean hasNext()
    {
        boolean someLeft = false;

        checkIterator();
        if (it != null)
            someLeft = it.hasNext();

        return someLeft;
    }

    /**
     * Get the next element from the underlying array.
     *
     * @return the next element from the underlying array
     *
     * @throws NoSuchElementException No more elements exist
     *
     * @see #previous()
     * @see java.util.Iterator#next
     */
    public Object next() throws NoSuchElementException
    {
        Object result = null;

        checkIterator();
        if (it != null)
            result = it.next();

        return result;
    }

    /**
     * Remove the object most recently extracted from the iterator.
     * The object is removed from whatever underlying <tt>Collection</tt>
     * is currently being traversed.
     */
    public void remove()
    {
        checkIterator();
        if (it != null)
            it.remove();
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    private synchronized void checkIterator()
    {
        Collection collection;

        if (aggregationIterator == null) 
        {
            it = null;
            aggregationIterator = aggregation.iterator();
            if (aggregationIterator.hasNext())
                it = nextIterator();
        }

        while ( (it != null) && (! it.hasNext()) )
        {
            if (! aggregationIterator.hasNext())
                it = null;

            else
                it = nextIterator();
        }
    }

    private Iterator nextIterator()
    {
        Object    obj = aggregationIterator.next();
        Iterator  it  = null;

        if (obj instanceof Collection)
            it = ((Collection) obj).iterator();

        else if (obj instanceof Iterator)
            it = (Iterator) obj;

        else if (obj instanceof Enumeration)
            it = new EnumerationIterator ((Enumeration) obj);

        else
            throw new IllegalArgumentException (obj.getClass().getName());

        return it;
    }
}
