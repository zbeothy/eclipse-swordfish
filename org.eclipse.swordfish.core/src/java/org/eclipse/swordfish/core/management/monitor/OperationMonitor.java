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
package org.eclipse.swordfish.core.management.monitor;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * Monitor bean that provides information about usage of one specific service operation
 * 
 * TODO: add role.
 * 
 */
public class OperationMonitor extends BaseMonitor {

    /** The log. */
    private static Log log = SBBLogFactory.getLog(OperationMonitor.class);

    /** The participant. */
    private UnifiedParticipantIdentity participant;

    /** The service. */
    private QName service;

    /** The operation name. */
    private String operationName;

    /** The role. */
    private ParticipantRole role;

    /**
     * Instantiates a new operation monitor.
     * 
     * @param participant
     *        the participant
     * @param service
     *        the service
     * @param operation
     *        the operation
     * @param role
     *        the role
     */
    public OperationMonitor(final UnifiedParticipantIdentity participant, final QName service, final String operation,
            final ParticipantRole role) {
        this.participant = participant;
        this.service = service;
        this.operationName = operation;
        this.role = role;
        if (log.isDebugEnabled()) {
            log.debug("Created " + this.hashCode() + " [" + participant + "]" + service + "{" + operation + "} -- " + role);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @return the operation name
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#getOperationName()
     */
    public String getOperationName() {
        String serviceString = (null != this.service) ? this.service.toString() : "NULL";
        String ret = serviceString + "#" + this.operationName;
        return ret;
    }

    /**
     * Gets the participant id.
     * 
     * @return the participant id
     */
    public String getParticipantId() {
        return String.valueOf(this.participant);
    }

    /**
     * Gets the participant role.
     * 
     * @return the participant role
     */
    public String getParticipantRole() {
        return String.valueOf(this.role);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct app fail
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#getPctAppFail()
     */
    public Integer getPctAppFail() {
        return super.getPctAppFail1();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct app fail period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#getPctAppFailPeriod()
     */
    public Integer getPctAppFailPeriod() {
        return super.getPctAppFail1Period();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the request rate
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#getRequestRate()
     */
    public Integer getRequestRate() {
        return super.getReqRate1();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the request rate period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#getRequestRatePeriod()
     */
    public Integer getRequestRatePeriod() {
        return super.getReqRate1Period();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the request utilization
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#getRequestUtilization()
     */
    public Integer getRequestUtilization() {
        // currently unused
        return new Integer(0);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the request utilization period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#getRequestUtilizationPeriod()
     */
    public Integer getRequestUtilizationPeriod() {
        // currently unused
        return new Integer(0);
    }

    /**
     * Gets the role.
     * 
     * @return the role
     */
    public String getRole() {
        return String.valueOf(this.role);
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#setPctAppFailPeriod(java.lang.Integer)
     */
    public void setPctAppFailPeriod(final Integer minutes) {
        super.setPctAppFail1Period(minutes);
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#setRequestRatePeriod(java.lang.Integer)
     */
    public void setRequestRatePeriod(final Integer minutes) {
        super.setReqRate1Period(minutes);
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.OperationMonitorMBean#setRequestUtilizationPeriod(java.lang.Integer)
     */
    public void setRequestUtilizationPeriod(final Integer minutes) {
        // currently unused
    }
}
