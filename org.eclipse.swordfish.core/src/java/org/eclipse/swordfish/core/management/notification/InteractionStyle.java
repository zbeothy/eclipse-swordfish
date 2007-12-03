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
 * Enum describing invocation- (consumer) or excecution- (provider) styles.
 * 
 */
public final class InteractionStyle extends AbstractEnum {

    /** caller is blocked for duration of processing. */
    public static final InteractionStyle BLOCKING = new InteractionStyle("BLOCKING");

    /** method returns to caller immediatly, responses are handled by callback object. */
    public static final InteractionStyle NON_BLOCKING = new InteractionStyle("NON_BLOCKING");

    /**
     * The Constructor.
     * 
     * @param name
     *        for style
     */
    private InteractionStyle(final String name) {
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
    public InteractionStyle getInstanceByName(final String name) {
        return (InteractionStyle) getInstanceByNameInternal(InteractionStyle.class, name);
    }

}
