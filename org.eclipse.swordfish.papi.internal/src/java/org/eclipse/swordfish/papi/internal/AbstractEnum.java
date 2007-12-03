/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.papi.internal;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Package visible class for type-safe enumerations.<br>
 * This class has been derived from the the Apache type-safe enumeration implementation available as
 * part of Commons Lang ( http://jakarta.apache.org/commons/lang/ )
 * 
 */
abstract class AbstractEnum implements Comparable, Serializable {

    /**
     * <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -712945951170455942L;

    // After discussion, the default size for HashMaps is used, as the
    // sizing algorithm changes across the JDK versions
    /**
     * An empty <code>Map</code>, as JDK1.2 didn't have an empty map.
     */
    private static final Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap(0));

    /**
     * <code>Map</code>, key of class name, value of <code>Entry</code>.
     */
    private static Map cEnumClasses = new HashMap();

    /**
     * @param clazz
     *        <code>Class</code> to get name from
     * @return The unqualified class name
     */
    public static String getShortClassName(final Class clazz) {
        String s = clazz.getName();
        int i = s.lastIndexOf('.');
        if (0 == i) return s;
        return s.substring(i + 1);
    }

    /**
     * <p>
     * Gets an <code>Enum</code> object by class and value.
     * </p>
     * 
     * <p>
     * This method loops through the list of <code>Enum</code>, thus if there are many
     * <code>Enum</code>s this will be slow.
     * </p>
     * 
     * @param enumClass
     *        the class of the <code>Enum</code> to get must not be <code>null</code> and must
     *        be a subclass of <code>Enum</code>, otherwise an
     *        <code>IllegalArgumentException</code> is thrown.
     * @param value
     *        the value of the <code>Enum</code> to get
     * @return the enum object, or null if the enum does not exist
     */
    protected static AbstractEnum getEnum(final Class enumClass, final int value) {
        if (enumClass == null) throw new IllegalArgumentException("The Enum Class must not be null");
        List list = AbstractEnum.getEnumList(enumClass);
        for (Iterator it = list.iterator(); it.hasNext();) {
            AbstractEnum anEnum = (AbstractEnum) it.next();
            if (anEnum.getValue() == value) return anEnum;
        }
        return null;
    }

    /**
     * <p>
     * Gets an <code>Enum</code> object by class and name.
     * </p>
     * 
     * @param enumClass
     *        the class of the Enum to get, must not be <code>null</code>, otherwise an
     *        <code>IllegalArgumentException</code> is thrown.
     * @param name
     *        the name of the <code>Enum</code> to get, may be <code>null</code>
     * @return the enum object, or null if the enum does not exist
     */
    protected static AbstractEnum getEnum(final Class enumClass, final String name) {
        Entry entry = getEntry(enumClass);
        if (entry == null) return null;
        return (AbstractEnum) entry.map.get(name);
    }

    /**
     * <p>
     * Gets the <code>List</code> of <code>Enum</code> objects using the <code>Enum</code>
     * class.
     * </p>
     * 
     * <p>
     * The list is in the order that the objects were created (source code order). If the requested
     * class has no enum objects an empty <code>List</code> is returned.
     * </p>
     * 
     * @param enumClass
     *        the class of the <code>Enum</code> to get, must not be <code>null</code> and must
     *        be a subclass of <code>Enum</code>, otherwise an
     *        <code>IllegalArgumentException</code> is thrown.
     * @return the enum object Map
     */
    protected static List getEnumList(final Class enumClass) {
        Entry entry = getEntry(enumClass);
        if (entry == null) return Collections.EMPTY_LIST;
        return entry.unmodifiableList;
    }

    /**
     * <p>
     * Gets the <code>Map</code> of <code>Enum</code> objects by name using the
     * <code>Enum</code> class.
     * </p>
     * 
     * <p>
     * If the requested class has no enum objects an empty <code>Map</code> is returned.
     * </p>
     * 
     * @param enumClass
     *        the class of the <code>Enum</code> to get, must not be <code>null</code> and must
     *        be a subclass of <code>Enum</code>, otherwise an
     *        <code>IllegalArgumentException</code> is thrown.
     * @return the enum object Map
     */
    protected static Map getEnumMap(final Class enumClass) {
        Entry entry = getEntry(enumClass);
        if (entry == null) return EMPTY_MAP;
        return entry.unmodifiableMap;
    }

    /**
     * <p>
     * Gets an <code>Iterator</code> over the <code>Enum</code> objects in an <code>Enum</code>
     * class.
     * </p>
     * 
     * <p>
     * The <code>Iterator</code> is in the order that the objects were created (source code
     * order). If the requested class has no enum objects an empty <code>Iterator</code> is
     * returned.
     * </p>
     * 
     * @param enumClass
     *        the class of the <code>Enum</code> to get, must not be <code>null</code> and must
     *        be a subclass of <code>Enum</code>, otherwise an
     *        <code>IllegalArgumentException</code> is thrown.
     * @return an iterator of the Enum objects
     */
    protected static Iterator iterator(final Class enumClass) {
        return AbstractEnum.getEnumList(enumClass).iterator();
    }

    /**
     * <p>
     * Creates an <code>Entry</code> for storing the Enums.
     * </p>
     * 
     * <p>
     * This accounts for subclassed Enums.
     * </p>
     * 
     * @param enumClass
     *        the class of the <code>Enum</code> to get
     * @return the enum entry
     */
    private static Entry createEntry(final Class enumClass) {
        Entry entry = new Entry();
        Class cls = enumClass.getSuperclass();
        while ((cls != null) && (cls != AbstractEnum.class)) {
            Entry loopEntry = (Entry) cEnumClasses.get(cls);
            if (loopEntry != null) {
                entry.list.addAll(loopEntry.list);
                entry.map.putAll(loopEntry.map);
                break; // stop here, as this will already have had superclasses
                // added
            }
            cls = cls.getSuperclass();
        }
        return entry;
    }

    // ============================
    /**
     * <p>
     * Gets an <code>Entry</code> from the map of Enums.
     * </p>
     * 
     * @param enumClass
     *        the class of the <code>Enum</code> to get
     * @return the enum entry
     */
    private static Entry getEntry(final Class enumClass) {
        if (enumClass == null) throw new IllegalArgumentException("The Enum Class must not be null");
        if (AbstractEnum.class.isAssignableFrom(enumClass) == false)
            throw new IllegalArgumentException("The Class must be a subclass of Enum");
        Entry entry = (Entry) cEnumClasses.get(enumClass);
        return entry;
    }

    // ============================

    /**
     * The string representation of the Enum.
     */
    private final String iName;

    /**
     * The hashcode representation of the Enum.
     */
    private final transient int iHashCode;

    /**
     * The toString representation of the Enum.
     */
    private transient String iToString = null;

    /**
     * The value contained in enum.
     */
    private final int iValue;

    /**
     * <p>
     * Constructor to add a new named item to the enumeration.
     * </p>
     * <p>
     * Remark: Bad return values of an overridden <code>getEnumClass()</code> method will show up
     * here with an <code>IllegalArgumentException</code>
     * </p>
     * 
     * @param name
     *        the name of the enum object, must not be empty or <code>null</code>, otherwise an
     *        <code>IllegalArgumentException</code> is thrown
     * @param value
     *        the value of enum item
     */
    protected AbstractEnum(final String name, final int value) {
        super();
        this.init(name);
        this.iName = name;
        // beginn Checkstyle Wahnsinn :-)
        final int three = 3;
        final int seven = 7;
        this.iHashCode = seven + this.getEnumClass().hashCode() + three * name.hashCode();
        // end Checkstyle Wahnsinn
        this.iValue = value;
    }

    /**
     * <p>
     * Tests for order.
     * </p>
     * 
     * <p>
     * The default ordering is numeric by value, but this can be overridden by subclasses.
     * </p>
     * 
     * @see java.lang.Comparable#compareTo(Object)
     * @param other
     *        The other object to compare to, must not be <code>null</code>, otherwise a
     *        <code>NullPointerException</code> is thrown, and must be an instance of the present
     *        class or a subclass, otherwise a <code>ClassCastException</code> is thrown.
     * @return -ve if this is less than the other object, +ve if greater than, <code>0</code> of
     *         equal
     */
    public int compareTo(final Object other) {
        return this.iValue - ((AbstractEnum) other).iValue;
    }

    /**
     * <p>
     * Tests for equality.
     * </p>
     * 
     * <p>
     * Two Enum objects are considered equal if they have the same class names and the same names.
     * Identity is tested first, so this method usually runs fast.
     * </p>
     * 
     * @param other
     *        The other object to compare for equality
     * @return <code>True</code> if the Enums are equal
     */
    @Override
    public final boolean equals(final Object other) {
        if (other == this)
            return true;
        else if (other == null)
            return false;
        else if (other.getClass() == this.getClass())
            // shouldn't happen, but...
            return this.iName.equals(((AbstractEnum) other).iName);
        else if (((AbstractEnum) other).getEnumClass().getName().equals(this.getEnumClass().getName())) {
            // different classloaders
            try {
                // try to avoid reflection
                return this.iName.equals(((AbstractEnum) other).iName);

            } catch (ClassCastException ex) {
                // use reflection
                try {
                    Method mth = other.getClass().getMethod("getName", (Class[]) null);
                    String name = (String) mth.invoke(other, (Object[]) null);
                    return this.iName.equals(name);
                } catch (NoSuchMethodException ex2) {
                    // considered unequal, go on
                    return false;
                } catch (IllegalAccessException ex2) {
                    // considered unequal, go on
                    return false;
                } catch (InvocationTargetException ex2) {
                    // considered unequal, go on
                    return false;
                }
            }
        } else
            return false;
    }

    /**
     * <p>
     * Retrieves the Class of this Enum item, set in the constructor.
     * </p>
     * 
     * <p>
     * This is normally the same as <code>getClass()</code>, but for advanced Enums may be
     * different. If overridden, it must return a constant value.
     * </p>
     * 
     * @return The <code>Class</code> of the enum
     */
    public Class getEnumClass() {
        return this.getClass();
    }

    // ============================
    /**
     * <p>
     * Retrieves the name of this Enum item, set in the constructor.
     * </p>
     * 
     * @return The <code>String</code> name of this Enum item
     */
    public final String getName() {
        return this.iName;
    }

    /**
     * <p>
     * Get value of enum item.
     * </p>
     * 
     * @return The enum item's value.
     */
    public final int getValue() {
        return this.iValue;
    }

    /**
     * <p>
     * Returns a suitable hashCode for the enumeration.
     * </p>
     * 
     * @return A hashcode based on the name
     */
    @Override
    public final int hashCode() {
        return this.iHashCode;
    }

    /**
     * <p>
     * Human readable description of this <code>Enum</code> item.
     * </p>
     * 
     * @return String in the form <code>type[name=value]</code>, for example:
     *         <code>JavaVersion[Java 1.0=100]</code>. Note that the package name is stripped
     *         from the type name.
     */
    @Override
    public String toString() {
        if (this.iToString == null) {
            String shortName = AbstractEnum.getShortClassName(this.getEnumClass());
            this.iToString = shortName + "[" + this.getName() + "=" + this.getValue() + "]";
        }
        return this.iToString;
    }

    /**
     * <p>
     * Handle the deserialization of the class to ensure that multiple copies are not wastefully
     * created, or illegal enum types created.
     * </p>
     * 
     * @return the resolved object
     */
    protected Object readResolve() {
        Entry entry = (Entry) cEnumClasses.get(this.getEnumClass());
        if (entry == null) return null;
        return entry.map.get(this.getName());
    }

    /**
     * Initializes the enumeration.
     * <p>
     * Remark: an invalid <code>enumClass</code> will cause an
     * <code>IllegalArgumentException</code> in this method
     * </p>
     * 
     * @param name
     *        the enum name, must not be <code>null</code>, and empty String, or duplicate,
     *        otherwise an <code>IllegalArgumentException</code> is thrown.
     */
    private void init(final String name) {
        if ((name == null) || (name.trim().length() == 0))
            throw new IllegalArgumentException("The Enum name must not be empty or null");

        Class enumClass = this.getEnumClass();
        if (enumClass == null) throw new IllegalArgumentException("getEnumClass() must not be null");
        Class cls = this.getClass();
        boolean ok = false;
        while ((cls != null) && (cls != AbstractEnum.class)) {
            if (cls == enumClass) {
                ok = true;
                break;
            }
            cls = cls.getSuperclass();
        }
        if (ok == false) throw new IllegalArgumentException("getEnumClass() must return a superclass of this class");

        // create entry
        Entry entry = (Entry) cEnumClasses.get(enumClass);
        if (entry == null) {
            entry = createEntry(enumClass);
            cEnumClasses.put(enumClass, entry);
        }
        if (entry.map.containsKey(name))
            throw new IllegalArgumentException("The Enum name must be unique, '" + name + "' has already been added");
        entry.map.put(name, this);
        entry.list.add(this);
    }

    /**
     * <p>
     * Enable the iterator to retain the source code order.
     * </p>
     */
    private static final class Entry {

        /**
         * Map of Enum name to Enum.
         */
        private final Map map = new HashMap();

        /**
         * Map of Enum name to Enum.
         */
        private final Map unmodifiableMap = Collections.unmodifiableMap(this.map);

        /**
         * List of Enums in source code order.
         */
        private final List list = new ArrayList(25);

        /**
         * Map of Enum name to Enum.
         */
        private final List unmodifiableList = Collections.unmodifiableList(this.list);

        /**
         * <p>
         * Restrictive constructor.
         * </p>
         */
        private Entry() {
        }
    }
}
