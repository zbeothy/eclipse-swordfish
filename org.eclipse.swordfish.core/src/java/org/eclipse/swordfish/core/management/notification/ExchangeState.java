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
 * Typesafe enumeration of states a message can have.
 * 
 */
public final class ExchangeState extends AbstractEnum {

    /** Message exchange is completed. */
    public static final ExchangeState FINISHED = new ExchangeState("FINISHED");

    /** MessageExchange was aborted due to network problems. */
    public static final ExchangeState ABORTED_NET = new ExchangeState("ABORTED_NET");

    /** MessageExchange was aborted due to application problems. */
    public static final ExchangeState ABORTED_APP = new ExchangeState("ABORTED_APP");

    /** MessageExchange was aborted due to sbb internal problems. */
    public static final ExchangeState ABORTED_INTERNAL = new ExchangeState("ABORTED_INTERNAL");

    /** Message exchange is still active. */
    public static final ExchangeState ACTIVE = new ExchangeState("ACTIVE");

    /**
     * enforce that states can only defined here.
     * 
     * @param name
     *        to use for state
     */
    private ExchangeState(final String name) {
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
    public ExchangeState getInstanceByName(final String name) {
        return (ExchangeState) getInstanceByNameInternal(ExchangeState.class, name);
    }

}
