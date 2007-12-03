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

import java.util.Iterator;
import java.util.List;
import org.eclipse.swordfish.core.management.operations.Operations;

/**
 * Dispatcher to distribute journal notifications to several backends.
 * 
 */
public class BackendDispatcher implements MessagingMonitorBackend {

    /** The backends. */
    private List backends;

    /**
     * Instantiates a new backend dispatcher.
     * 
     * @param backends
     *        the backends
     */
    public BackendDispatcher(final List backends) {
        this.backends = backends;
    }

    /**
     * Gets the backends.
     * 
     * @return the backends
     */
    public List getBackends() {
        return this.backends;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedApplication(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedApplication(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleAbortedApplication(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedNet(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedNet(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleAbortedNet(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleCompleted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleCompleted(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleCompleted(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDelivered(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDelivered(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleDelivered(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDoneLocal(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDoneLocal(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleDoneLocal(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandback(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandback(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleHandback(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandoff(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandoff(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleHandoff(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleReceived(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleReceived(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleReceived(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleStarted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleStarted(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleStarted(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleUnspecifiedEvent(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleUnspecifiedEvent(final ExchangeJournal journal) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.handleUnspecifiedEvent(journal);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#setOperations(org.eclipse.swordfish.core.management.operations.Operations)
     */
    public void setOperations(final Operations ops) {
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
            backend.setOperations(ops);
        }
    }

}
