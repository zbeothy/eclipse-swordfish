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
package org.eclipse.swordfish.core.interceptor.monitor.impl;

import java.util.Collection;
import javax.jbi.messaging.MessageExchange;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.interceptor.monitor.MonitoringProcessor;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.notification.ManagementNotificationListener;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.notification.impl.MonitoringNotificationBean;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * The Class MonitoringProcessorBean.
 */
public class MonitoringProcessorBean extends AbstractProcessingComponent implements MonitoringProcessor {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(MonitoringProcessorBean.class);

    /** The listener. */
    private ManagementNotificationListener listener;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#canHandle(java.util.Collection)
     */
    public boolean canHandle(final Collection/* <Assertion> */assertions) throws InternalSBBException {
        return true;
    }

    /**
     * Gets the content action.
     * 
     * @return the content action
     * 
     * @see org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent#getContentAction()
     */
    @Override
    public ContentAction getContentAction() {
        return ContentAction.NONE;
    }

    /**
     * Gets the supported sources.
     * 
     * @return the supported sources
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#getSupportedSources()
     */
    @Override
    public Class[] getSupportedSources() {
        return new Class[] {DOMSource.class, StreamSource.class, SAXSource.class};
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleFault(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        // no-op
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        ParticipantRole participantRole = Role.SENDER.equals(role) ? ParticipantRole.CONSUMER : ParticipantRole.PROVIDER;
        PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.REQUEST);
        if (null != assertion) {
            this.handle(context, assertion, participantRole);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        // if you are confused by this, ask OWO or RME for an explanation
        ParticipantRole participantRole = Role.SENDER.equals(role) ? ParticipantRole.CONSUMER : ParticipantRole.PROVIDER;
        PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.RESPONSE);
        if (null != assertion) {
            this.handle(context, assertion, participantRole);
        }
    }

    /**
     * Sets the listener.
     * 
     * @param lsnr
     *        the new listener
     */
    public void setListener(final ManagementNotificationListener lsnr) {
        this.listener = lsnr;
    }

    /**
     * Generig handling function.
     * 
     * @param context
     *        the context
     * @param assertion
     *        the assertion
     * @param participantRole
     *        the participant role
     */
    private void handle(final MessageExchange context, final PrimitiveAssertion assertion, final ParticipantRole participantRole) {
        MonitoringNotificationBean notification = new MonitoringNotificationBean(context);
        String value = this.getAttribute(assertion, "value", null);
        if (null != value) {
            try {
                int val = new Integer(value).intValue();
                notification.setMaxResponseTime(val);
                notification.setParticipantRole(participantRole);
                this.listener.sendNotification(notification);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Sent MonitoringNotification for " + notification.getCorrelationID() + " - Role "
                            + participantRole.toString() + " - value " + value);
                }
            } catch (NumberFormatException e) {
                LOG.warn("Illegal value specified for MaxResponseTime: " + value);
                // TODO: operational logging, extract participant policy info
            }
        }
    }
}
