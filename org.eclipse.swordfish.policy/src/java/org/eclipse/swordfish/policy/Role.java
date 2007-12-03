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
package org.eclipse.swordfish.policy;

/**
 * Enumeration describing the possible roles a participant can have in respect to a service or
 * service operation.
 * 
 */
public class Role {

    /** The Constant CONSUMER. */
    public final static Role CONSUMER = new Role("consumer", 0);

    /** The Constant PROVIDER. */
    public final static Role PROVIDER = new Role("provider", 1);

    /**
     * Gets the by name.
     * 
     * @param name
     *        the name
     * 
     * @return the by name
     */
    public static Role getByName(final String name) {
        Role ret = null;
        if ("consumer".equals(name)) {
            ret = CONSUMER;
        } else if ("provider".equals(name)) {
            ret = PROVIDER;
        }
        return ret;
    }

    /** The name. */
    private Object name;

    /** The id. */
    private int id;

    /**
     * private constructor to ensure enum characteristics.
     * 
     * @param name
     *        the name
     * @param id
     *        the id
     */
    private Role(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Role)
            return ((this.id == ((Role) obj).id) && (this.name.equals(((Role) obj).name)));
        else if (obj instanceof String) return (this.name.equals(obj));
        return false;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public Object getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.name.toString();
    }
}
