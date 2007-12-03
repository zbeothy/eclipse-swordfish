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
package org.eclipse.swordfish.core.management.messaging;

import org.eclipse.swordfish.core.management.notification.AbstractEnum;

/**
 * The Class ExchangeStage.
 */
public class ExchangeStage extends AbstractEnum {

    /** Exchange is received from consumer application or consumer by InternalSBB. */
    public static final ExchangeStage RECEIVED = new ExchangeStage("RECEIVED");

    /** Exchange is handed off to provider InternalSBB or provider application. */
    public static final ExchangeStage HANDOFF = new ExchangeStage("HANDOFF");

    /** Exchanged delivered back to consumer InternalSBB or consumer application. */
    public static final ExchangeStage DELIVERED = new ExchangeStage("DELIVERED");

    /** Exchange is received back by provider InternalSBB or consumer InternalSBB. */
    public static final ExchangeStage HANDBACK = new ExchangeStage("HANDBACK");

    /** Exchange is completed. */
    public static final ExchangeStage COMPLETED = new ExchangeStage("COMPLETED");

    /** Processing of exchange is finished for local participant, but will continue elsewhere. */
    public static final ExchangeStage DONE_LOCAL = new ExchangeStage("DONE_LOCAL");

    /** Most recent stage of an exchange. */
    public static final ExchangeStage CURRENT = new ExchangeStage("CURRENT");

    /**
     * Instantiates a new exchange stage.
     * 
     * @param name
     *        the name
     */
    private ExchangeStage(final String name) {
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
    public ExchangeStage getInstanceByName(final String name) {
        return (ExchangeStage) getInstanceByNameInternal(ExchangeStage.class, name);
    }

}
