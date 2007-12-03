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

import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.notification.ExchangePattern;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.utils.BeanInspector;

/**
 * The Class ExchangeJournalFactory.
 * 
 */
public final class ExchangeJournalFactory {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ExchangeJournalFactory.class);

    /**
     * Create a new protocol for a given exchange.
     * 
     * @param notification
     *        to determine the kind of protocol to create
     * 
     * @return <code>ExchangeJournal</code> to use for message exchange
     */
    public static ExchangeJournal createJournal(final MessageProcessingNotification notification) {
        ExchangeJournal ret = null;
        if (ExchangePattern.IN_OUT.equals(notification.getExchangePattern())) {
            if (ParticipantRole.CONSUMER.equals(notification.getParticipantRole())) {
                ret = new ConsumerInOutJournal(notification);
            } else if (ParticipantRole.PROVIDER.equals(notification.getParticipantRole())) {
                ret = new ProviderInOutJournal(notification);
            }
        } else if (ExchangePattern.REQUEST_CALLBACK.equals(notification.getExchangePattern())) {
            if (ParticipantRole.CONSUMER.equals(notification.getParticipantRole())) {
                ret = new ConsumerRequestCallbackJournal(notification);
            } else if (ParticipantRole.PROVIDER.equals(notification.getParticipantRole())) {
                ret = new ProviderRequestCallbackJournal(notification);
            }
        } else if (ExchangePattern.IN_ONLY.equals(notification.getExchangePattern())) {
            if (ParticipantRole.CONSUMER.equals(notification.getParticipantRole())) {
                ret = new ConsumerInOnlyJournal(notification);
            } else if (ParticipantRole.PROVIDER.equals(notification.getParticipantRole())) {
                ret = new ProviderInOnlyJournal(notification);
            }
        }
        if (null == ret) {
            // catchall for unknown styles - should not be executed in
            // production
            String details = BeanInspector.beanToString(notification);
            LOG.warn("Could not determine exchange type for exchange - using dummy. Details:\n" + details);
            ret = new DummyExchangeJournal(notification);
        }
        return ret;
    }

    /**
     * private constructor to prevent instantiation.
     */
    private ExchangeJournalFactory() {

    }
}
