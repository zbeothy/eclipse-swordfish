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

/**
 * Enumeration of possible states for entities like Participants, Services, InternalOperations.
 * 
 */
public class EntityState extends AbstractEnum {

    /** The Constant ADDED. */
    public static final EntityState ADDED = new EntityState("added");

    /** The Constant REMOVED. */
    public static final EntityState REMOVED = new EntityState("removed");

    /**
     * Instantiates a new entity state.
     * 
     * @param name
     *        the name
     */
    private EntityState(final String name) {
        super(name);
    }

    /**
     * Gets the instance by name.
     * 
     * @param name
     *        the name
     * 
     * @return the instance by name
     */
    public EntityState getInstanceByName(final String name) {
        return (EntityState) getInstanceByNameInternal(EntityState.class, name);
    }

}
