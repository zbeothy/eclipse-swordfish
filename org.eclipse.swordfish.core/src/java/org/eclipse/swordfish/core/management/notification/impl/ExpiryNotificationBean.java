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
package org.eclipse.swordfish.core.management.notification.impl;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.messaging.impl.ResponseTimeMonitoringBackend;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.MonitoringNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * Internal notification that is generated when the agreed maximum response time for an
 * ExchangeJournal is exeeded.
 * 
 */
public class ExpiryNotificationBean implements MonitoringNotification {

    /** The Constant log. */
    protected final static Log LOG = SBBLogFactory.getLog(ExpiryNotificationBean.class);

    /** The initiator. */
    private MonitoringNotification initiator;

    /** The reported timestamp. */
    private long reportedTimestamp;

    /** The backend. */
    private ResponseTimeMonitoringBackend backend;

    /**
     * Instantiates a new expiry notification bean.
     * 
     * @param initiator
     *        the initiator
     * @param backend
     *        the backend
     */
    public ExpiryNotificationBean(final MonitoringNotification initiator, final ResponseTimeMonitoringBackend backend) {
        if (null == initiator) throw new NullPointerException("Null argument not allowed in constructor");
        this.initiator = initiator;
        this.backend = backend;
        this.reportedTimestamp = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("ExpiryNotification for " + initiator.getCorrelationID() + " created at " + this.reportedTimestamp);
        }
    }

    /**
     * prompts this notification to be executed in the originating backend <br/>This method should
     * be called after the notification has been passed through the internal notification queue. The
     * delayed execution ensures that any message processing notifications that relate to the
     * message exchange that were received by the time the expiry notification was generated are
     * processed first.
     */
    public void execute() {
        this.backend.handleExpiryNotification(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ExchangeNotification#getCorrelationID()
     */
    public String getCorrelationID() {
        return this.initiator.getCorrelationID();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MonitoringNotification#getCreatedTimestamp()
     */
    public long getCreatedTimestamp() {
        return this.initiator.getCreatedTimestamp();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotification#getEventType()
     */
    public EventType getEventType() {
        return EventType.INTERNAL_POST;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MonitoringNotification#getMaxResponseTime()
     */
    public int getMaxResponseTime() {
        return this.initiator.getMaxResponseTime();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.OperationNotification#getOperationName()
     */
    public String getOperationName() {
        return this.initiator.getOperationName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotification#getParticipantIdentity()
     */
    public UnifiedParticipantIdentity getParticipantIdentity() {
        return this.initiator.getParticipantIdentity();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ExchangeNotification#getParticipantRole()
     */
    public ParticipantRole getParticipantRole() {
        return this.initiator.getParticipantRole();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MonitoringNotification#getRelatedTimestamp()
     */
    public long getRelatedTimestamp() {
        return this.initiator.getRelatedTimestamp();
    }

    /**
     * Gets the reported timestamp.
     * 
     * @return the reported timestamp
     */
    public long getReportedTimestamp() {
        return this.reportedTimestamp;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ServiceNotification#getServiceName()
     */
    public QName getServiceName() {
        return this.initiator.getServiceName();
    }

}
