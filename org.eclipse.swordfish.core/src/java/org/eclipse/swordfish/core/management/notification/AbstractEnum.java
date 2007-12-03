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
package org.eclipse.swordfish.core.management.notification;

import java.util.HashMap;

/**
 * abstract base for enum classes, handles naming of instances.
 * 
 */
public abstract class AbstractEnum {

    /** The instance maps. */
    private static HashMap instanceMaps = new HashMap();

    /**
     * Gets the instance by name internal.
     * 
     * @param clazz
     *        the clazz
     * @param name
     *        the name
     * 
     * @return the instance by name internal
     */
    protected static Object getInstanceByNameInternal(final Class clazz, final String name) {
        Object ret = null;
        HashMap instances = (HashMap) instanceMaps.get(clazz);
        if (null != instances) {
            ret = instances.get(name);
        }
        return ret;
    }

    /** name to use for state. */
    private String name;

    /**
     * The Constructor.
     * 
     * @param name
     *        to use for instance
     */
    protected AbstractEnum(final String name) {
        this.name = name;
        Class clazz = this.getClass();
        HashMap instances = (HashMap) instanceMaps.get(clazz);
        if (null == instances) {
            instances = new HashMap();
            instanceMaps.put(clazz, instances);
        }
        instances.put(name, this);
    }

    /**
     * To string.
     * 
     * @return name of state
     */
    @Override
    public String toString() {
        return this.name;
    }

}
