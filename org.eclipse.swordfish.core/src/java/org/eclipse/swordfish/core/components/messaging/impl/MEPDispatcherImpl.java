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
package org.eclipse.swordfish.core.components.messaging.impl;

import java.net.URI;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import org.eclipse.swordfish.core.components.command.Command;
import org.eclipse.swordfish.core.components.endpointmanager.EndpointManager;
import org.eclipse.swordfish.core.components.headerprocessing.HeaderProcessor;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.instancemanager.InstanceManager;
import org.eclipse.swordfish.core.components.messaging.MEPDispatcher;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageExchangePattern;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtensionFactory;
import org.eclipse.swordfish.core.trace.TraceIdentifier;
import org.eclipse.swordfish.core.utils.ExchangeProperties;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.exception.InfrastructureRuntimeException;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * The Class MEPDispatcherImpl.
 */
public class MEPDispatcherImpl implements MEPDispatcher {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(MEPDispatcher.class);

    /** The exchange. */
    private MessageExchange exchange;

    /** The channel. */
    private DeliveryChannel channel;

    /** The instance manager. */
    private InstanceManager instanceManager;

    /** The endpoint manager. */
    private EndpointManager endpointManager;

    /** The hp. */
    private HeaderProcessor hp;

    /** The timer. */
    private long timer;

    /** The operation desc. */
    private OperationDescription operationDesc;

    /** The ctxe. */
    private CallContextExtension ctxe;

    /**
     * The Constructor.
     * 
     * @param exchange
     *        the exchange
     * @param channel
     *        the channel
     * @param instManager
     *        the inst manager
     * @param endpManager
     *        the endp manager
     * @param headerProcessor
     *        the header processor
     */
    public MEPDispatcherImpl(final DeliveryChannel channel, final InstanceManager instManager, final EndpointManager endpManager,
            final HeaderProcessor headerProcessor, final MessageExchange exchange) {
        this.endpointManager = endpManager;
        this.exchange = exchange;
        this.instanceManager = instManager;
        this.hp = headerProcessor;
        this.timer = System.currentTimeMillis();
    }

    /**
     * Map headers.
     * 
     * @throws InternalSBBException
     */
    public void mapHeaders() throws InternalSBBException {
        CallContextExtension callCtxe = HeaderUtil.getCallContextExtension(this.exchange);
        if (callCtxe != null) {
            Scope scope = callCtxe.getScope();
            if (scope != null) {
                if (scope.equals(Scope.REQUEST)) {
                    this.hp.mapIncomingRequest(HeaderUtil.getLatestValidNormalizedMessage(this.exchange, scope), callCtxe);
                } else {
                    this.hp.mapIncomingResponse(HeaderUtil.getLatestValidNormalizedMessage(this.exchange, scope), callCtxe);
                }
            } else
                throw new InfrastructureRuntimeException("missing Scope while sending " + this.exchange.getOperation().toString());
        } else
            throw new InfrastructureRuntimeException("missing call context while sending "
                    + this.exchange.getOperation().toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        LOG.debug("Inbound exchange for " + this.exchange.getOperation().toString() + " in role "
                + ((this.exchange.getRole().equals(MessageExchange.Role.CONSUMER)) ? "CONSUMER" : "PROVIDER") + " side in state "
                + this.exchange.getStatus().toString());

        // check for existing call context in incoming exchange
        this.ctxe = (CallContextExtension) this.exchange.getProperty(ExchangeProperties.CALL_CONTEXT);
        if (this.ctxe == null) {
            // if no context was found, create a new one and fill it with
            // initial information from the exchange
            this.ctxe = CallContextExtensionFactory.createCallContextExtension();
            this.ctxe.setProviderID(this.exchange.getService());
            this.ctxe.setOperationName(this.exchange.getOperation().getLocalPart());
            this.ctxe.setMessageExchangeId(this.exchange.getExchangeId());
            HeaderUtil.setCallContextExtension(this.exchange, this.ctxe);
            UnifiedParticipantIdentity participantIdentityUnifier = this.findParticipantIdentity();
            if (participantIdentityUnifier == null) {
                this.refuseExchangeWithError(this.exchange, new InternalConfigurationException(
                        "cannot find a participant for this request"));

                LOG.warn("cannot assign the exchange for operation " + this.exchange.getOperation().toString()
                        + " to any participant.");
                return;
            }
            this.ctxe.setUnifiedParticipantIdentity(participantIdentityUnifier);
            // This value is now bound to this thread
            TraceIdentifier.set(participantIdentityUnifier.toString());

            // find the serviceDescription for this exchange
            CompoundServiceDescription desc;
            desc = this.endpointManager.getServiceDescription(this.exchange.getEndpoint());
            if (desc == null) {
                // we must get the service QName
                desc = this.getServiceDescriptionResolver().getServiceDescription(this.exchange.getService());
            }
            // now get the operation description
            this.operationDesc = desc.getOperation(this.exchange.getOperation().getLocalPart());
            this.ctxe.setServiceName(desc.getPortTypeQName());
            String partnerOperationName = this.operationDesc.getPartnerOperationName();
            if (null != partnerOperationName) {
                this.ctxe.setPartnerOperationName(partnerOperationName);
            }
            // this is the most ugliest way to find out the sbb communication
            // style
            if (this.ctxe.getCommunicationStyle() == null) {
                InternalCommunicationStyle style = InternalCommunicationStyle.REQUEST_RESPONSE;
                URI uri = this.operationDesc.getExchangePattern();
                if (MessageExchangePattern.OUT_ONLY_URI.equals(uri)) {
                    style = InternalCommunicationStyle.NOTIFICATION;
                } else if (MessageExchangePattern.IN_ONLY_URI.equals(uri)) {
                    style = InternalCommunicationStyle.ONEWAY;
                } else if (MessageExchangePattern.IN_OUT_URI.equals(uri)) {
                    style = InternalCommunicationStyle.REQUEST_RESPONSE;
                }
                this.ctxe.setCommunicationStyle(style);
            }
        } else {
            CompoundServiceDescription desc;
            desc = this.endpointManager.getServiceDescription(this.exchange.getEndpoint());
            if (desc == null) {
                desc = this.getServiceDescriptionResolver().getServiceDescription(this.exchange.getService());
            }
            this.operationDesc = desc.getOperation(this.exchange.getOperation().getLocalPart());
            this.exchange.setProperty("old_context", this.ctxe);
        }
        try {
            if (MessageExchange.Role.PROVIDER.equals(this.exchange.getRole())) {
                this.handleServicer();
            } else {
                this.handleInitiator();
            }
        } catch (Exception e) {
            LOG.warn("incoming exchange processing failed with ", e);
            this.refuseExchangeWithError(this.exchange, e);
        }
    }

    /**
     * Adds the agreed policy.
     */
    private void addAgreedPolicy() {
        CompoundServiceDescription csd = this.operationDesc.getServiceDescription();
        if (null != csd) {
            AgreedPolicy policy = csd.getAgreedPolicy();
            this.ctxe.setPolicy(policy);
            this.ctxe.setProviderPolicyID(policy.getProviderPolicyIdentity().getKeyName());
        }
    }

    /**
     * Creates the command.
     * 
     * @return a Command object constructed based on the operation desc deduced
     */
    private Command createCommand() {
        SBBExtension anSBB = this.instanceManager.query(this.ctxe.getUnifiedParticipantIdentity().getParticipantIdentity());
        Command command = anSBB.getKernel().getCommandFactory().createCommand(this.operationDesc);
        command.setExchange(this.exchange);
        return command;
    }

    /**
     * for an MEP the participant Identity is either given through the fact that we have been
     * opening an Endpoint do a participant OR we have been sending a MEP to the BC previously that
     * in now responsing. In the first case we can get the participant from the endpoint manager. In
     * the latter case we have been setting a context into the MEP which we can quers for the
     * participant.
     * 
     * @return the unified participant identity or null if none is found
     */
    private UnifiedParticipantIdentity findParticipantIdentity() {
        CallContextExtension context = HeaderUtil.getCallContextExtension(this.exchange);
        UnifiedParticipantIdentity participantIdentityUnifier;

        participantIdentityUnifier = context.getUnifiedParticipantIdentity();

        if (participantIdentityUnifier == null) {
            ServiceEndpoint se = this.exchange.getEndpoint();
            participantIdentityUnifier = this.endpointManager.getParticipantIdentityUnifier(se);
        }

        return participantIdentityUnifier;
    }

    /**
     * Gets the service description resolver.
     * 
     * @return the service description resolver
     */
    private ServiceDescriptionResolver getServiceDescriptionResolver() {
        SBBExtension anSBB = this.instanceManager.query(this.ctxe.getUnifiedParticipantIdentity().getParticipantIdentity());
        return anSBB.getKernel().getSdResolver();
    }

    /**
     * consumer responses for non-real async calls.
     * 
     * @throws InternalSBBException
     */
    private void handleInitiator() throws InternalSBBException {
        Command command = this.createCommand();
        Role role = Role.SENDER;
        Scope scope = Scope.RESPONSE;
        this.ctxe.setScope(scope);

        this.mapHeaders();

        command.setRole(role);
        command.setScope(scope);
        command.setRethrowException(true);
        command.setExecutionBegin(this.timer);
        try {
            command.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * this method will be called in the case of provider requests and consumer notifications and
     * real async responses.
     * 
     * @throws InternalSBBException
     */
    private void handleServicer() throws InternalSBBException {

        Command command = this.createCommand();
        Role role;
        Scope scope;
        if (this.operationDesc.getServiceDescription().isPartnerDescription()) {
            role = Role.SENDER;
            scope = Scope.RESPONSE;
        } else {
            role = Role.RECEIVER;
            scope = Scope.REQUEST;
        }
        this.ctxe.setScope(scope);

        this.mapHeaders();
        if (null == this.ctxe.getPolicy()) {
            this.addAgreedPolicy();
        }
        command.setRole(role);
        command.setScope(scope);
        command.setRethrowException(true);
        command.setExecutionBegin(this.timer);
        try {
            command.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * sends back the exchange to the delivery channel with an error status.
     * 
     * @param msgExchange
     *        the exchange that is subject of refusal
     * @param e
     *        the exception that is indicating the error
     */
    private void refuseExchangeWithError(final MessageExchange msgExchange, final Exception e) {
        try {
            LOG.error("refusing exchange processing because of ", e);
            /**
             * This is a work around! If there is an out message existing in the exchange than
             * remove it. This prevents a BC to try to send the msg instead of the error
             */
            if (msgExchange instanceof InOut) {
                ((InOut) msgExchange).setOutMessage(null);
            }
            msgExchange.setError(e);
            msgExchange.setStatus(ExchangeStatus.ERROR);
            this.channel.send(msgExchange);
        } catch (MessagingException e1) {
            LOG.error("cannot send the exchange back to the delivery channel", e);
        }
    }

    /**
     * for cases where we are done with the exchange (no matter if positive or negative) we must
     * send an ack with the exchange to the delivery channel. This method does exactlty this.
     */
    // private void ackExchange(final MessageExchange exchange) {
    // try {
    // exchange.setStatus(ExchangeStatus.DONE);
    // channel.send(exchange);
    // } catch (JBIException e) {
    // // FIXME .. What are we going to do in this case just log the
    // // stuff???
    // }
    // }
}
