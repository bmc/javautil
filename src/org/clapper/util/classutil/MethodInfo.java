package org.clapper.util.classutil;

/**
 * Holds information about a method within a class.
 *
 * @see ClassInfo
 */
public class MethodInfo
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private int access = 0;
    private String name = null;
    private String description = null;
    private String signature = null;
    private String[] exceptions = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new, empty <tt>MethodInfo</tt> object.
     */
    public MethodInfo()
    {
    }

    /**
     * Create and initialize a new <tt>MethodInfo</tt> object.
     *
     * @param access      method access modifiers, etc.
     * @param name        method name
     * @param description method description
     * @param signature   method signature
     * @param exceptions  list of thrown exceptions (by name)
     */
    public MethodInfo(int access,
                      String name,
                      String description,
                      String signature,
                      String[] exceptions)
    {
        this.access = access;
        this.name = name;
        this.description = description;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    /*----------------------------------------------------------------------*\
                            Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the access modifiers for this method.
     *
     * @return the access modifiers, or 0 if none are set.
     */
    public int getAccess()
    {
        return access;
    }

    /**
     * Get the method name.
     *
     * @return the method name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the method description, if any.
     *
     * @return the method description, or null
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Get the method's signature, if any.
     *
     * @return the method signature, or null.
     */
    public String getSignature()
    {
        return signature;
    }

    /**
     * Get the class names of the thrown exceptions
     *
     * @return the names of the thrown exceptions, or null.
     */
    public String[] getExceptions()
    {
        return exceptions;
    }

    /**
     * Get the hash code. The hash code is based on the field's name.
     *
     * @return the hash code
     */
    public int hashCode()
    {
        return name.hashCode();
    }
}
