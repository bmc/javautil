package org.clapper.util.classutil;

/**
 * Holds information about a field within a class.
 *
 * @see ClassInfo
 */
public class FieldInfo
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private int access = 0;
    private String name = null;
    private String description = null;
    private String signature = null;
    private Object value = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new, empty <tt>FieldInfo</tt> object.
     */
    public FieldInfo()
    {
    }

    /**
     * Create and initialize a new <tt>FieldInfo</tt> object.
     *
     * @param access      field access modifiers, etc.
     * @param name        field name
     * @param description field description
     * @param signature   field signature
     * @param value       field value, if any
     */
    public FieldInfo(int access,
                     String name,
                     String description,
                     String signature,
                     Object value)
    {
        this.access = access;
        this.name = name;
        this.description = description;
        this.signature = signature;
        this.value = value;
    }

    /*----------------------------------------------------------------------*\
                            Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the access modifiers for this field
     *
     * @return the access modifiers, or 0 if none are set.
     */
    public int getAccess()
    {
        return access;
    }

    /**
     * Get the field name.
     *
     * @return the field name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the field description, if any.
     *
     * @return the field description, or null
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Get the field's signature, if any.
     *
     * @return the field signature, or null.
     */
    public String getSignature()
    {
        return signature;
    }

    /**
     * Get the field value, if any.
     *
     * @return the field value, or null.
     */
    public Object getValue()
    {
        return value;
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

