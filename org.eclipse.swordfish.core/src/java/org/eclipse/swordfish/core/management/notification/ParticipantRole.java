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
 * The Class ParticipantRole.
 */
public class ParticipantRole extends AbstractEnum {

    /** The Constant CONSUMER. */
    public static final ParticipantRole CONSUMER = new ParticipantRole("consumer");

    /** The Constant PROVIDER. */
    public static final ParticipantRole PROVIDER = new ParticipantRole("provider");

    /** The Constant UNKNOWN. */
    public static final ParticipantRole UNKNOWN = new ParticipantRole("unknown");

    /**
     * Gets the instance by name.
     * 
     * @param name
     *        the name
     * 
     * @return the instance by name
     */
    public static ParticipantRole getInstanceByName(final String name) {
        return (ParticipantRole) getInstanceByNameInternal(ParticipantRole.class, name);
    }

    /**
     * Instantiates a new participant role.
     * 
     * @param name
     *        the name
     */
    public ParticipantRole(final String name) {
        super(name);
    }

}
