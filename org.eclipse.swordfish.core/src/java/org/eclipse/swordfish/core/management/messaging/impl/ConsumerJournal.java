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
package org.eclipse.swordfish.core.management.messaging.impl;

import org.eclipse.swordfish.core.management.messaging.ExchangeStage;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * The Class ConsumerJournal.
 */
public abstract class ConsumerJournal extends ExchangeJournal {

    /**
     * Instantiates a new consumer journal.
     * 
     * @param notification
     *        the notification
     */
    public ConsumerJournal(final MessageProcessingNotification notification) {
        super(ParticipantRole.CONSUMER, notification);
    }

    /**
     * Constructor to generate a quick clone for replaying notifications.
     * 
     * @param scaffold
     *        the scaffold
     */
    protected ConsumerJournal(final ConsumerJournal scaffold) {
        super(scaffold);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#setCreatedTimestamp(java.lang.Long)
     */
    @Override
    public void setCreatedTimestamp(final Long timestamp) {
        this.getTimes().put(ExchangeStage.RECEIVED, timestamp);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#setRelatedTimestamp(java.lang.Long)
     */
    @Override
    public void setRelatedTimestamp(final Long timestamp) {
        // related timestamp is unused on consumer side
    }
}
