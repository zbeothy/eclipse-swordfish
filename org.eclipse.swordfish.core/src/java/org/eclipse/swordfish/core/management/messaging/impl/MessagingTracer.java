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
import org.eclipse.swordfish.core.management.operations.Operations;

/**
 * Utility backend to trace all messaging events.
 * 
 */
public class MessagingTracer implements MessagingMonitorBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(MessagingTracer.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedApplication(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedApplication(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("exchange " + journal.getCorrelationID() + " aborted (app)");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedNet(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedNet(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("exchange " + journal.getCorrelationID() + " aborted (net)");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleCompleted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleCompleted(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("exchange " + journal.getCorrelationID() + " finished");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDelivered(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDelivered(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("exchange " + journal.getCorrelationID() + " delivered to initiator");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDoneLocal(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDoneLocal(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Exchange done local: " + journal.getCorrelationID());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandback(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandback(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("exchange " + journal.getCorrelationID() + " received");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandoff(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandoff(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("exchange " + journal.getCorrelationID() + " handed of to backend");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleReceived(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleReceived(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Journal created for exchange " + journal.getCorrelationID());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleStarted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleStarted(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Exchange started: " + journal.getCorrelationID());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleUnspecifiedEvent(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleUnspecifiedEvent(final ExchangeJournal journal) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Event for exchange " + journal.getCorrelationID());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#setOperations(org.eclipse.swordfish.core.management.operations.Operations)
     */
    public void setOperations(final Operations ops) {
        // no-op for now
    }

}
