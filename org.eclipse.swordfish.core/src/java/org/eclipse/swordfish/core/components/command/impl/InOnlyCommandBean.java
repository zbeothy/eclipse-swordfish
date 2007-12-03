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
package org.eclipse.swordfish.core.components.command.impl;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.command.InOnlyCommand;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.ExchangePattern;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.InteractionStyle;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageFactory;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalParticipantException;
import org.eclipse.swordfish.papi.internal.exception.InternalRemoteException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * this is the implementation of the oneway operation.
 */
public class InOnlyCommandBean extends AbstractCommand implements InOnlyCommand {

    /** Logger. */
    private static final Log LOG = SBBLogFactory.getLog(InOnlyCommandBean.class);

    /**
     * default constructor for the InOnly command which represents a oneway operation.
     */
    public InOnlyCommandBean() {
    }

    /**
     * Execute incoming request.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#executeIncomingRequest()
     */
    @Override
    protected void executeIncomingRequest() throws InternalSBBException, PolicyViolatedException {

        this.sendManagementNotification(EventType.NET_IN_PRE, ExchangeState.ACTIVE);
        CallContextExtension ctxe = this.getCallContext();

        // error inbound case
        if (ExchangeStatus.ERROR.equals(this.getExchange().getStatus())) {
            this.sendManagementNotification(ExchangeState.ABORTED_NET);
            InternalInfrastructureException e = new InternalInfrastructureException(this.getExchange().getError());
            try {
                this.invokeHandleErrorFromHandler(e, ctxe);
            } catch (Exception c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                LOG.warn("Participant application returned exception on handler error invocation", c);
            }
            return;
        }

        // normal inbound case
        try {
            this.applyPolicy();
        } catch (InternalSBBException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            LOG.warn("Infrastructure exception when applying policy", e);
            try {
                InternalInfrastructureException ex = new InternalInfrastructureException(e);
                this.addRelatedTimestamp();
                this.invokeHandleErrorFromHandler(ex, ctxe);
                this.ackExchange();
                return;
            } catch (Exception c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                this.ackExchange();
                LOG.warn("Participant application returned exception on handler error invocation", c);
                return;
            }
        } catch (PolicyViolatedException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            LOG.warn("policy violated during application", e);
            try {
                InternalInfrastructureException ex = new InternalInfrastructureException(e);
                this.addRelatedTimestamp();
                this.invokeHandleErrorFromHandler(ex, ctxe);
                this.ackExchange();
                return;
            } catch (Exception c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                this.ackExchange();
                LOG.warn("Participant application returned exception on handler error invocation", c);
                return;
            }
        }

        this.sendManagementNotification(EventType.NET_IN_POST, ExchangeState.ACTIVE);
        NormalizedMessage inNM = this.getCurrentNormalizedMessage();
        try {
            IncomingMessageBase inMsg = IncomingMessageFactory.createIncomingMessage(inNM);
            inMsg.setCallContext(ctxe);
            this.sendManagementNotification(EventType.APP_OUT_PRE, ExchangeState.ACTIVE);
            this.addRelatedTimestamp();
            this.ackExchange();
            this.invokeHandleMessageFromHandler(inMsg);
            this.sendManagementNotification(EventType.APP_OUT_POST, ExchangeState.ACTIVE);
            this.sendManagementNotification(ExchangeState.FINISHED);
        } catch (InternalParticipantException e) {
            LOG.warn("Participant application returned message handling exception", e);
            this.sendManagementNotification(ExchangeState.ABORTED_APP);
        } catch (Exception t) {
            LOG.warn("Participant application returned throwable ", t);
            this.sendManagementNotification(ExchangeState.ABORTED_APP);
        }

    }

    /**
     * Execute incoming response.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#executeIncomingResponse()
     *      this is executed for the real async case on the consumer side
     */
    @Override
    protected void executeIncomingResponse() throws InternalSBBException, PolicyViolatedException {
        this.sendManagementNotification(EventType.NET_IN_PRE, ExchangeState.ACTIVE);

        CallContextExtension ctxe = this.getCallContext();

        // error inbound case
        if (ExchangeStatus.ERROR.equals(this.getExchange().getStatus())) {
            InternalInfrastructureException e = new InternalInfrastructureException(this.getExchange().getError());
            this.sendManagementNotification(ExchangeState.ABORTED_NET);
            try {
                this.invokeHandleErrorFromHandler(e, ctxe);
            } catch (Exception c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                this.ackExchange();
                LOG.warn("Participant application returned exception on handler error invocation", c);
            }
            return;
        }

        IncomingMessageBase response = null;
        NormalizedMessage inNM = this.getCurrentNormalizedMessage();
        boolean invokeFaultOperation = false;
        response = IncomingMessageFactory.createIncomingMessage(inNM);
        response.setCallContext(ctxe);
        if (this.isFaultExchange()) {
            response.setFaultMessage(true);
        }
        this.sendManagementNotification(EventType.APP_OUT_PRE, ExchangeState.ACTIVE);

        if (this.getOperationDescription().isDefaultFaultOperation()) {
            Document doc = TransformerUtil.docFromString(response.getXMLString());
            NodeList ndList = doc.getElementsByTagNameNS(HeaderUtil.SBB_NS, "Fault");
            if ((ndList != null) && (ndList.getLength() > 0)) {
                invokeFaultOperation = true;
            }
        }

        try {
            if (!invokeFaultOperation) {
                this.applyPolicy();
            }
        } catch (InternalSBBException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            LOG.warn("Infrastrcture exception when applying policy", e);
            try {
                InternalInfrastructureException ex = new InternalInfrastructureException(e);
                this.invokeHandleErrorFromHandler(ex, ctxe);
            } catch (Exception c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                this.ackExchange();
                LOG.warn("Participant application returned exception on handler error invocation", c);
                return;
            }
        } catch (PolicyViolatedException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            LOG.warn("Infrastrcture exception when applying policy", e);
            try {
                InternalInfrastructureException ex = new InternalInfrastructureException(e);
                this.invokeHandleErrorFromHandler(ex, ctxe);
            } catch (Exception c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                this.ackExchange();
                LOG.warn("Participant application returned exception on handler error invocation", c);
                return;
            }
        }

        try {
            this.ackExchange();
            if (invokeFaultOperation) {
                final String msg = response.getXMLString();
                final InternalSBBException ex;
                if (msg.indexOf(InternalMessagingException.class.getName()) > 0) {
                    ex = new InternalMessagingException(msg);
                } else if (msg.indexOf(InternalAuthenticationException.class.getName()) > 0) {
                    ex = new InternalAuthenticationException(msg);
                } else if (msg.indexOf(InternalAuthorizationException.class.getName()) > 0) {
                    ex = new InternalAuthorizationException(msg);
                } else {
                    ex = new InternalRemoteException(msg);
                }
                this.invokeHandleErrorFromHandler(ex, ctxe);
            } else {
                this.invokeHandleMessageFromHandler(response);
            }
            this.sendManagementNotification(EventType.APP_OUT_POST, ExchangeState.ACTIVE);
            this.sendManagementNotification(EventType.NET_IN_POST, ExchangeState.ACTIVE);
        } catch (InternalParticipantException e) {
            LOG.warn("Participant application returned message handling exception", e);
            this.sendManagementNotification(ExchangeState.ABORTED_APP);
        }
        // TODO is this correct? we are actuyll finished after we send a done to
        // the BC
        this.sendManagementNotification(ExchangeState.FINISHED);
    }

    /**
     * Execute outgoing request.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#executeOutgoingRequest()
     */
    @Override
    protected void executeOutgoingRequest() throws InternalSBBException, PolicyViolatedException {
        this.sendManagementNotification(EventType.APP_IN_PRE, ExchangeState.ACTIVE);
        this.addCreatedTimestamp();
        try {
            this.applyPolicy();
        } catch (InternalSBBException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            throw e;
        } catch (PolicyViolatedException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            throw e;
        }
        this.sendManagementNotification(EventType.APP_IN_POST, ExchangeState.ACTIVE);
        this.sendManagementNotification(EventType.NET_OUT_PRE, ExchangeState.ACTIVE);
        try {
            this.getDeliveryChannelSender().sendSync(this.getExchange());
            if (ExchangeStatus.ERROR.equals(this.getExchange().getStatus())) {
                InternalInfrastructureException e = new InternalInfrastructureException(this.getExchange().getError());
                // FIXED: This will appear twice as we catch our own exception
                // and reproduce
                // the management notification again!
                // sendManagementNotification(ExchangeState.ABORTED_NET);
                // sendManagementNotification(ExchangeState.FINISHED);
                throw e;
            }
            this.sendManagementNotification(EventType.NET_OUT_POST, ExchangeState.ACTIVE);
            this.sendManagementNotification(ExchangeState.FINISHED);
        } catch (InternalSBBException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_NET);
            this.sendManagementNotification(ExchangeState.FINISHED);
            throw e;
        } catch (MessagingException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_NET);
            this.sendManagementNotification(ExchangeState.FINISHED);
            throw new InternalInfrastructureException(e);
        }
    }

    /**
     * Execute outgoing response.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#executeOutgoingResponse()
     *      this is only called for the real async use case
     */
    @Override
    protected void executeOutgoingResponse() throws InternalSBBException, PolicyViolatedException {
        try {
            this.applyPolicy();
        } catch (PolicyViolatedException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            throw e;
        } catch (InternalSBBException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            throw e;
        }
        try {
            this.getDeliveryChannelSender().sendSync(this.getExchange());
            if (ExchangeStatus.ERROR.equals(this.getExchange().getStatus())) {
                InternalInfrastructureException e = new InternalInfrastructureException(this.getExchange().getError());
                throw e;
            }
        } catch (InternalSBBException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_NET);
            this.sendManagementNotification(ExchangeState.FINISHED);
            throw e;
        } catch (MessagingException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_NET);
            this.sendManagementNotification(ExchangeState.FINISHED);
            throw new InternalInfrastructureException(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#getExchangePattern()
     */
    @Override
    protected ExchangePattern getExchangePattern() {
        return ExchangePattern.IN_ONLY;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#getInteractionStyle()
     */
    @Override
    protected InteractionStyle getInteractionStyle() {
        return InteractionStyle.NON_BLOCKING;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#getOperationName()
     */
    @Override
    protected String getOperationName() {
        return this.getExchange().getOperation().getLocalPart();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#getParticipantRole()
     */
    @Override
    protected ParticipantRole getParticipantRole() {
        return this.getRole() == Role.SENDER ? ParticipantRole.CONSUMER : ParticipantRole.PROVIDER;
    }
}
