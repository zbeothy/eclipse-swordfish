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

import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;

/**
 * The Class ProviderInOnlyJournal.
 */
public class ProviderInOnlyJournal extends ProviderJournal {

    /**
     * Instantiates a new provider in only journal.
     * 
     * @param notification
     *        the notification
     */
    public ProviderInOnlyJournal(final MessageProcessingNotification notification) {
        super(notification);
    }

    /**
     * Constructor to generate a quick clone for replaying notifications.
     * 
     * @param scaffold
     *        the scaffold
     */
    protected ProviderInOnlyJournal(final ProviderInOnlyJournal scaffold) {
        super(scaffold);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#getStyle()
     */
    @Override
    public InternalCommunicationStyle getStyle() {
        return InternalCommunicationStyle.ONEWAY;
    }

    /**
     * (non-Javadoc).
     * 
     * @param notification
     *        the notification
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#process(org.eclipse.swordfish.core.management.notification.MessageProcessingNotification)
     */
    @Override
    public void process(final MessageProcessingNotification notification) {
        super.process(notification);
        boolean memorizeEventType = true;
        if (ExchangeState.ACTIVE.equals(notification.getExchangeState())) {
            if (EventType.NET_IN_PRE.equals(notification.getEventType())) {
                if (null != this.getLastEvent()) {
                    this.handleSequenceError(notification);
                }
                this.handleReceived(notification);
            } else if (EventType.APP_OUT_PRE.equals(notification.getEventType())) {
                if (!EventType.NET_IN_PRE.equals(this.getLastEvent())) {
                    this.handleSequenceError(notification);
                }
                this.handleEvent(notification);
            } else if (EventType.APP_OUT_POST.equals(notification.getEventType())) {
                if (!EventType.APP_OUT_PRE.equals(this.getLastEvent())) {
                    this.handleSequenceError(notification);
                }
                this.handleHandoff(notification);
            } else {
                // make sure the first event is defined
                if (null == this.getLastEvent()) {
                    this.handleSequenceError(notification);
                }
                this.handleEvent(notification);
                // don't memorize the event type for unspecified internal events
                memorizeEventType = false;
            }
        } else if (ExchangeState.FINISHED.equals(notification.getExchangeState())) {
            if (!EventType.APP_OUT_POST.equals(this.getLastEvent())) {
                this.handleSequenceError(notification);
            }
            this.handleCompleted(notification);
        } else if ((ExchangeState.ABORTED_APP.equals(notification.getExchangeState()))
                || (ExchangeState.ABORTED_INTERNAL.equals(notification.getExchangeState()))) {
            // ToDo: handle ABORTED_INTERNAL seperately
            this.handleAbortedApplication(notification);
        } else if (ExchangeState.ABORTED_NET.equals(notification.getExchangeState())) {
            this.handleAbortedNet(notification);
        }
        if (memorizeEventType) {
            this.setLastEvent(notification.getEventType());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal#replay(org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend)
     */
    @Override
    public void replay(final MessagingMonitorBackend backend) {
        if ((null != this.getNotifications()) && (this.getNotifications().size() > 0)) {
            ExchangeJournal tempJournal = new ProviderInOnlyJournal(this);
            super.replay(backend, tempJournal);
        }
    }

}
