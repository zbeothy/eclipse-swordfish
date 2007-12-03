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

import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;

/**
 * ExchangeJournal that is used if the concrete class of MessageExchange could not be determined.
 * 
 */
public class DummyExchangeJournal extends ExchangeJournal {

    /**
     * Instantiates a new dummy exchange journal.
     * 
     * @param notification
     *        the notification
     */
    public DummyExchangeJournal(final MessageProcessingNotification notification) {
        super(ParticipantRole.UNKNOWN, notification);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#getStyle()
     */
    @Override
    public InternalCommunicationStyle getStyle() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#process(org.eclipse.swordfish.core.management.notification.MessageProcessingNotification)
     */
    @Override
    public void process(final MessageProcessingNotification notification) {
        super.process(notification);
        if (ExchangeState.ACTIVE != notification.getExchangeState()) {
            this.setFinished(true);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Finished dummy log for MessageExchange " + notification.getCorrelationID());
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#replay(org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend)
     */
    @Override
    public void replay(final MessagingMonitorBackend backend) {
        // no-op
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#setCreatedTimestamp(java.lang.Long)
     */
    @Override
    public void setCreatedTimestamp(final Long timestamp) {
        // no-op
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#setRelatedTimestamp(java.lang.Long)
     */
    @Override
    public void setRelatedTimestamp(final Long timestamp) {
        // no-op
    }

}
