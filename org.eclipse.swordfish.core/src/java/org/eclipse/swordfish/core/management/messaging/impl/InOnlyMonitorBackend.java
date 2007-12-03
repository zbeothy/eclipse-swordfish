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

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.messages.TrackingMessage;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * The Class InOnlyMonitorBackend.
 */
public class InOnlyMonitorBackend extends OperationMonitorBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(InOnlyMonitorBackend.class);

    /** The history. */
    /**
     * 
     */
    private InOnlyHistory history;

    /**
     * Instantiates a new in only monitor backend.
     * 
     * @param participant
     *        the participant
     * @param serviceName
     *        the service name
     * @param opName
     *        the op name
     * @param role
     *        the role
     * @param instrumentationManager
     *        the instrumentation manager
     */
    public InOnlyMonitorBackend(final UnifiedParticipantIdentity participant, final QName serviceName, final String opName,
            final ParticipantRole role, final InstrumentationManagerBean instrumentationManager) {
        super(participant, serviceName, opName, role, instrumentationManager);
        this.history = new InOnlyHistory();
        this.history.setTspStart(System.currentTimeMillis());
        this.setTrackingMessage(ParticipantRole.CONSUMER.equals(this.getRole()) ? TrackingMessage.TRACKING_SUMMARY_CONSUMER_ONEWAY
                : TrackingMessage.TRACKING_SUMMARY_PROVIDER_ONEWAY);
    }

    public InOnlyHistory getHistory() {
        return this.history;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.OperationMonitorBackend#handleAbortedApplication(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    @Override
    public void handleAbortedApplication(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling aborted app/" + String.valueOf(this.getRole()) + " for " + journal.getCorrelationID());
        }
        super.handleAbortedApplication(journal);
        long count = this.history.getAppFail();
        this.history.setAppFail(count++);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.OperationMonitorBackend#handleAbortedNet(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    @Override
    public void handleAbortedNet(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling aborted net/" + String.valueOf(this.getRole()) + " for " + journal.getCorrelationID());
        }
        super.handleAbortedNet(journal);
        long count = this.history.getNetFail();
        this.history.setNetFail(count++);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.OperationMonitorBackend#handleCompleted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    @Override
    public void handleCompleted(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling completed/" + String.valueOf(this.getRole()) + " for " + journal.getCorrelationID());
        }
        super.handleCompleted(journal);
        long count = this.history.getCompleted();
        this.history.setCompleted(count++);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.OperationMonitorBackend#handleDoneLocal(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    @Override
    public void handleDoneLocal(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling done local/" + String.valueOf(this.getRole()) + " for " + journal.getCorrelationID());
        }
        super.handleDoneLocal(journal);
        long count = this.history.getCompleted();
        this.history.setCompleted(count++);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.OperationMonitorBackend#handleHandoff(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    @Override
    public void handleHandoff(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling handoff/" + String.valueOf(this.getRole()) + " for " + journal.getCorrelationID());
        }
        super.handleHandoff(journal);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.OperationMonitorBackend#handleReceived(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    @Override
    public void handleReceived(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling received/" + String.valueOf(this.getRole()) + " for " + journal.getCorrelationID());
        }
        super.handleReceived(journal);
        long count = this.history.getCalls();
        this.history.setCalls(count++);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.OperationMonitorBackend#publishSummary()
     */
    @Override
    public void publishSummary() {
        InOnlyHistory oldHistory = this.history;
        this.history = new InOnlyHistory();
        this.history.setTspStart(System.currentTimeMillis());
        oldHistory.setTspStop(this.history.getTspStart());
        if (0 != oldHistory.getCalls()) {
            Object[] params =
                    {String.valueOf(this.getParticipant()), String.valueOf(this.getService()),
                            String.valueOf(this.getOperationName()), String.valueOf(this.getRole()),
                            new Long(oldHistory.getCalls()), new Long(oldHistory.getCompleted()),
                            new Long(oldHistory.getNetFail()), new Long(oldHistory.getAppFail()),
                            this.timestampToDatestring(oldHistory.getTspStart()),
                            this.timestampToDatestring(oldHistory.getTspStop())};
            this.getOperations().notify(this.getTrackingMessage(), params);
        }
    }

    public void setHistory(final InOnlyHistory history) {
        this.history = history;
    }

}
