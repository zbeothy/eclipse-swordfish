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
import javax.xml.soap.SOAPException;
import org.eclipse.swordfish.core.components.command.InOutCommand;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
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
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessageNotAcceptedException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalParticipantException;
import org.eclipse.swordfish.papi.internal.exception.InternalRemoteException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InternalTotallyOutOfOrderException;

/**
 * this is the implementation of a request response interaction style. Note that the thread
 * demacration line is the delivery channel.
 */
public class InOutCommandBean extends AbstractCommand implements InOutCommand {

    /** log. */
    private static final Log LOG = SBBLogFactory.getLog(InOutCommandBean.class);

    /** if true than this exchange is intended to be send as a blocking call, default value is false. */
    private boolean sync;

    /**
     * public constructor for this Bean.
     */
    public InOutCommandBean() {
        this.sync = false;
    }

    /**
     * Checks if is sync.
     * 
     * @return -- if this command is going to block
     */
    public boolean isSync() {
        return this.sync;
    }

    /**
     * Sets the sync.
     * 
     * @param theSync
     *        the the sync
     * 
     * @see org.eclipse.swordfish.core.components.command.InOutCommand#setSync(boolean)
     */
    public void setSync(final boolean theSync) {
        this.sync = theSync;
    }

    /**
     * This method is invoked on the provider side and results in a call to the providers message
     * handler or to providers handler Error method. TODO exception propagation
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

        // error inbound case .. This will happen if the provider has send a
        // response but it
        // bounces back from the binding component!
        if (ExchangeStatus.ERROR.equals(this.getExchange().getStatus())) {
            this.getKernel().storeExchange(this.getExchange());
            InternalInfrastructureException e = new InternalInfrastructureException(this.getExchange().getError());
            try {
                this.addRelatedTimestamp();
                this.sendManagementNotification(ExchangeState.ABORTED_NET);
                this.invokeHandleErrorFromHandler(e, ctxe);
            } catch (InternalParticipantException c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                LOG.warn("Participant application returned exception on handler error invocation", c);
            }
            return;
        }

        // normal inbound case
        try {
            this.getKernel().storeExchange(this.getExchange());
            this.applyPolicy();
        } catch (InternalSBBException e) {
            LOG.warn("Infrastrcture exception when applying policy", e);
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            try {
                this.addRelatedTimestamp();
                this.invokeHandleErrorFromHandler(e, ctxe);
                this.refuseExchangeWithError(e);
                return;
            } catch (InternalParticipantException c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                this.refuseExchangeWithError(new InternalInfrastructureException(
                        "Provider handling error case for the operation failed with error " + this.getExchange().getOperation()));
                LOG.warn("Participant application returned exception on handler error invocation", c);
                return;
            }
        } catch (PolicyViolatedException e) {
            LOG.warn("policy violated during application", e);
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            try {
                InternalMessagingException ex = new InternalMessagingException(e);
                this.addRelatedTimestamp();
                this.invokeHandleErrorFromHandler(ex, ctxe);
                this.refuseExchangeWithError(e);
                return;
            } catch (InternalParticipantException c) {
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                this.refuseExchangeWithError(new InternalInfrastructureException(
                        "Provider handling error case for the operation failed with error " + this.getExchange().getOperation()));
                LOG.warn("Participant application returned exception on handler error invocation", c);
                return;
            }
        }

        this.sendManagementNotification(EventType.NET_IN_POST, ExchangeState.ACTIVE);
        NormalizedMessage inNM = this.getCurrentNormalizedMessage();
        try {
            IncomingMessageBase inMsg = IncomingMessageFactory.createIncomingMessage(inNM);
            inMsg.setCallContext(ctxe);
            this.getKernel().storeExchange(this.getExchange());
            this.sendManagementNotification(EventType.APP_OUT_PRE, ExchangeState.ACTIVE);
            this.addRelatedTimestamp();
            this.invokeHandleMessageFromHandler(inMsg);
            // do not send APP_OUT_POST since FINISHED might already be sent in
            // response branch
        } catch (InternalParticipantException e) {
            LOG.warn("Participant application returned message handling exception", e);
            this.sendManagementNotification(ExchangeState.ABORTED_APP);
            this.refuseExchangeWithError(e);
        } catch (Exception t) {
            LOG.warn("Participant application returned throwable ", t);
            this.sendManagementNotification(ExchangeState.ABORTED_APP);
            this.refuseExchangeWithError(new InternalTotallyOutOfOrderException(t));
        }
    }

    /**
     * Execute incoming response.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#executeIncomingResponse()
     */
    @Override
    protected void executeIncomingResponse() throws InternalSBBException, PolicyViolatedException {
        CallContextExtension ctxe = this.getCallContext();

        if (ExchangeStatus.ERROR.equals(this.getExchange().getStatus())) {
            // if we have an error case we must push the error to the
            // incoming message handler in the case of async and throw it
            // in the case of sync
            final Exception error = this.getExchange().getError();
            InternalSBBException exc;
            if (error instanceof SOAPException) {
                // in this case the problem should come from the remote side
                final String msg = error.getMessage();
                final String lmsg = "Exceptional state propagated from remote side ";
                if (msg.startsWith(InternalMessagingException.class.getName())) {
                    exc = new InternalMessagingException(lmsg, error);
                } else if (msg.startsWith(InternalAuthenticationException.class.getName())) {
                    exc = new InternalAuthenticationException(lmsg, error);
                } else if (msg.startsWith(InternalAuthorizationException.class.getName())) {
                    exc = new InternalAuthorizationException(lmsg, error);
                } else {
                    exc = new InternalRemoteException(lmsg, error);
                }
            } else {
                exc = new InternalInfrastructureException("Operation invocation resulted in an exception ", error);
            }
            this.sendManagementNotification(ExchangeState.ABORTED_NET);
            if (this.isSync())
                throw exc;
            else {
                try {
                    this.invokeHandleErrorFromHandler(exc, ctxe);
                } catch (Exception e) {
                    this.sendManagementNotification(ExchangeState.ABORTED_APP);
                    LOG.warn("Participant application returned exception on handler error invocation", e);
                }
            }
            this.ackExchange();
            return;
        }

        // if we have no error case than we can process the MEP normally
        try {
            this.sendManagementNotification(EventType.NET_IN_PRE, ExchangeState.ACTIVE);
            this.applyPolicy();
        } catch (InternalSBBException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            this.ackExchange();
            if (this.isSync())
                throw e;
            else {
                try {
                    this.invokeHandleErrorFromHandler(e, ctxe);
                    return;
                } catch (Exception e1) {
                    this.sendManagementNotification(ExchangeState.ABORTED_APP);
                    LOG.warn("Participant application returned exception on handler error invocation", e1);
                    return;
                }
            }
        } catch (PolicyViolatedException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            this.ackExchange();
            if (this.isSync())
                throw e;
            else {
                try {
                    this.invokeHandleErrorFromHandler(new InternalMessagingException(e), ctxe);
                    return;
                } catch (Exception e1) {
                    this.sendManagementNotification(ExchangeState.ABORTED_APP);
                    LOG.warn("Participant application returned exception on handler error invocation", e1);
                    return;
                }
            }
        }

        this.sendManagementNotification(EventType.NET_IN_POST, ExchangeState.ACTIVE);
        /*
         * now determin weather this is a sync or async call. for the sync call we have nothing more
         * to do
         */
        if (!this.isSync()) {
            NormalizedMessage outNM = this.getCurrentNormalizedMessage();
            IncomingMessageBase response = IncomingMessageFactory.createIncomingMessage(outNM);
            response.setCallContext(ctxe);
            if (this.isFaultExchange()) {
                response.setFaultMessage(true);
            }
            this.sendManagementNotification(EventType.APP_OUT_PRE, ExchangeState.ACTIVE);
            try {
                this.sendManagementNotification(EventType.APP_OUT_PRE, ExchangeState.ACTIVE);
                this.invokeHandleMessageFromHandler(response);
                this.sendManagementNotification(EventType.APP_OUT_POST, ExchangeState.ACTIVE);
            } catch (InternalParticipantException e) {
                // TODO log the very severe condition
                this.sendManagementNotification(ExchangeState.ABORTED_APP);
                throw new InternalMessagingException(e);
            } finally {
                // TODO check if it is correct not to send this?
                // sendManagementNotification(EventType.APP_OUT_POST,
                // ExchangeState.ACTIVE);
                this.ackExchange();
                this.sendManagementNotification(ExchangeState.FINISHED);
            }
        } else {
            // for the sync case we send the exchange ack, before returning to
            // PAPI
            this.sendManagementNotification(EventType.APP_OUT_PRE, ExchangeState.ACTIVE);
            this.ackExchange();
            this.sendManagementNotification(ExchangeState.FINISHED);
        }
    }

    /**
     * This method is invoked on the consumer side on call and callBlocking for request response
     * proxy.
     * 
     * @throws InternalMessagingException
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
        if (this.isSync()) {
            this.sendManagementNotification(EventType.NET_OUT_PRE, ExchangeState.ACTIVE);
            try {
                this.getDeliveryChannelSender().sendSync(this.getExchange());
            } catch (MessagingException e) {
                throw new InternalInfrastructureException(e);
            }
            this.sendManagementNotification(EventType.NET_OUT_POST, ExchangeState.ACTIVE);
            this.setScope(Scope.RESPONSE);
            this.executeIncomingResponse();
        } else {
            this.sendManagementNotification(EventType.NET_OUT_PRE, ExchangeState.ACTIVE);
            try {
                this.getDeliveryChannelSender().send(this.getExchange());
            } catch (MessagingException e) {
                throw new InternalInfrastructureException(e);
            }
            this.sendManagementNotification(EventType.NET_OUT_POST, ExchangeState.ACTIVE);
        }
    }

    /**
     * Execute outgoing response.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#executeOutgoingResponse()
     */
    @Override
    protected void executeOutgoingResponse() throws InternalSBBException, PolicyViolatedException {
        this.sendManagementNotification(EventType.APP_IN_PRE, ExchangeState.ACTIVE);
        try {
            // bypass sendError from being policy processed
            // TODO determin: what is the impact of passing an ERROR Status to
            // the interceptors?
            if (this.getExchange().getStatus() != ExchangeStatus.ERROR) {
                this.applyPolicy();
            }
        } catch (PolicyViolatedException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            this.refuseExchangeWithError(new InternalMessagingException("provider response for "
                    + this.getExchange().getOperation() + " failed because of a policy violation:", e));
            throw e;

        } catch (InternalSBBException e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            this.refuseExchangeWithError(new InternalMessageNotAcceptedException(
                    "Provider handling error case for the operation failed with error " + this.getExchange().getOperation()));
            throw e;
        }
        this.sendManagementNotification(EventType.APP_IN_POST, ExchangeState.ACTIVE);
        this.sendManagementNotification(EventType.NET_OUT_PRE, ExchangeState.ACTIVE);
        try {
            this.getDeliveryChannelSender().send(this.getExchange());
        } catch (MessagingException e) {
            throw new InternalInfrastructureException(e);
        }
        this.sendManagementNotification(EventType.NET_OUT_POST, ExchangeState.ACTIVE);
        this.sendManagementNotification(ExchangeState.FINISHED);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#getExchangePattern()
     */
    @Override
    protected ExchangePattern getExchangePattern() {
        return ExchangePattern.IN_OUT;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.AbstractCommand#getInteractionStyle()
     */
    @Override
    protected InteractionStyle getInteractionStyle() {
        return this.isSync() ? InteractionStyle.BLOCKING : InteractionStyle.NON_BLOCKING;
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
