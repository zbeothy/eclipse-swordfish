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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.command.CallbackCommand;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageExchangePattern;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageFactoryImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtensionFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallRelationImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalParticipantException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * this is the implementation of the oneway operation.
 */
public class CallbackCommandBean extends InOnlyCommandBean implements CallbackCommand {

    /** Logger. */
    private static final Log LOG = SBBLogFactory.getLog(CallbackCommandBean.class);

    /**
     * default constructor for the InOnly command which represents a oneway operation.
     */
    public CallbackCommandBean() {
    }

    /**
     * Generate fault message.
     * 
     * @param e
     *        the e
     * 
     * @return the document
     */
    public Document generateFaultMessage(final Exception e) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        e.printStackTrace(new PrintStream(bos));

        String stackTrace = bos.toString();
        String exceptionMessage =
                "Policy Violated on InternalService Provider while processing " + this.getScope().toString()
                        + " message with error " + e.getMessage();

        try {
            bos.close();
        } catch (Exception exp) {
            LOG.debug(exp.getMessage());
        }

        String faultString =
                "<Fault xmlns=\""
                        + HeaderUtil.SBB_NS
                        + "\" ><Code><Value>ServiceInvocationFailed</Value><Subcode><Value>PolicyProcessingFailed</Value></Subcode>"
                        + "</Code><Reason></Reason><Detail></Detail></Fault>";

        Document faultDocument = TransformerUtil.docFromString(faultString);
        Node reasonNode = faultDocument.getElementsByTagName("Reason").item(0);
        Text reasonText = faultDocument.createTextNode(exceptionMessage);
        reasonNode.appendChild(reasonText);

        faultDocument.getElementsByTagName("Detail").item(0);
        Text detailText = faultDocument.createTextNode(stackTrace);
        reasonNode.appendChild(detailText);
        return faultDocument;
    }

    /**
     * Execute incoming request.
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.command.impl.Abstr
     *      actCommand#executeIncomingRequest()
     */
    @Override
    protected void executeIncomingRequest() throws InternalSBBException {

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
        boolean acknowledged = false;
        try {
            this.applyPolicy();
        } catch (Exception e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            LOG.warn("Exception while processing incoming request.", e);
            try {
                InternalInfrastructureException ex = new InternalInfrastructureException(e);
                this.addRelatedTimestamp();
                this.ackExchange();
                acknowledged = true;
                this.sendFaultToSender(e);
                this.invokeHandleErrorFromHandler(ex, ctxe);
                return;
            } catch (Exception c) {
                if (!acknowledged) {
                    this.ackExchange();
                    acknowledged = true;
                }
                LOG.warn("Participant application returned exception on handler error invocation", c);
                return;
            }
        }

        this.sendManagementNotification(EventType.NET_IN_POST, ExchangeState.ACTIVE);
        NormalizedMessage inNM = this.getCurrentNormalizedMessage();
        Exception exc = null;
        try {
            IncomingMessageBase inMsg = IncomingMessageFactory.createIncomingMessage(inNM);
            inMsg.setCallContext(ctxe);
            this.sendManagementNotification(EventType.APP_OUT_PRE, ExchangeState.ACTIVE);
            this.addRelatedTimestamp();
            this.ackExchange();
            acknowledged = true;
            this.invokeHandleMessageFromHandler(inMsg);
            this.sendManagementNotification(EventType.APP_OUT_POST, ExchangeState.ACTIVE);
            this.sendManagementNotification(ExchangeState.FINISHED);
        } catch (InternalParticipantException e) {
            exc = e;
        } catch (RuntimeException e) {
            exc = e;
        }
        if (exc != null) {
            LOG.warn("Participant application returned message handling exception", exc);
            this.sendManagementNotification(ExchangeState.ABORTED_APP);
            try {
                if (!acknowledged) {
                    this.addRelatedTimestamp();
                    this.ackExchange();
                    acknowledged = true;
                }
                this.sendFaultToSender(exc);
            } catch (Exception c) {
                if (!acknowledged) {
                    this.ackExchange();
                    acknowledged = true;
                }
                LOG.warn("Participant application returned exception on handler error invocation", c);
            }
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
        } catch (Exception e) {
            this.sendManagementNotification(ExchangeState.ABORTED_INTERNAL);
            try {
                this.sendFaultToSender(e);
            } catch (Exception exp) {
                LOG.error(exp.getMessage());
            }
            if (e instanceof InternalAuthenticationException)
                throw (InternalAuthenticationException) e;
            else if (e instanceof InternalAuthorizationException)
                throw (InternalAuthorizationException) e;
            else if (e instanceof InternalSBBException)
                throw (InternalSBBException) e;
            else if (e instanceof PolicyViolatedException) throw (PolicyViolatedException) e;
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
     * Gets the operation name.
     * 
     * @return the operation namee
     */
    @Override
    protected String getOperationName() {
        if (this.getScope().equals(Scope.REQUEST))
            return this.getExchange().getOperation().getLocalPart();
        else {
            this.getExchange().getOperation().getLocalPart();
            String op = this.getOperationDescription().getPartnerOperationName();
            return op;
        }
    }

    /**
     * Method to inform sender about error happened while policy processing primarily used in
     * Request callback case.
     * 
     * @param fault
     *        the fault
     * 
     * @throws Exception
     */
    protected void sendFaultToSender(final Exception fault) throws Exception {
        CompoundServiceDescription csd = this.getOperationDescription().getServiceDescription();
        OperationDescription opDesc = csd.getDefaultFaultOperation();
        if (opDesc != null) {
            LOG.debug("The default fault operation is " + opDesc.getName());
            CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();
            CallContextExtension answerCtx = HeaderUtil.getCallContextExtension(this.getExchange());
            OutgoingMessageBase msgBase =
                    (OutgoingMessageBase) new MessageFactoryImpl().createMessage(this.generateFaultMessage(fault));
            InOnly exchange;
            exchange = this.prepareResponseExchange(msgBase, ctx, answerCtx, opDesc);
            ctx.appendRelations(answerCtx.getRelations());
            ctx.pushRelation(new CallRelationImpl(InternalCallRelation.TYPE_ONEWAY, answerCtx.getMessageID()));
            ctx.setPolicy((answerCtx).getPolicy());
            ctx.setScope(Scope.RESPONSE);
            ctx.setCorrelationID(answerCtx.getFaultCorrelationID());
            ctx.setConsumerCallIdentifier(answerCtx.getFaultConsumerCallIdentifier());
            ctx.setRelatesTo(answerCtx.getMessageID());
            ctx.setPolicy(answerCtx.getPolicy());
            DocumentFragment refParams = answerCtx.getFaultReferenceParameters();
            if (null == refParams) {
                refParams = answerCtx.getReferenceParameters();
            }
            ctx.setReferenceParameters(refParams);
            ctx.setProviderID(answerCtx.getProviderID());
            ctx.setProviderPolicyID(answerCtx.getProviderPolicyID());
            ctx.setUnifiedParticipantIdentity(answerCtx.getUnifiedParticipantIdentity());
            ctx.setCreatedTimestamp(answerCtx.getCreatedTimestamp());
            ctx.setRelatedTimestamp(answerCtx.getRelatedTimestamp());
            this.getDeliveryChannelSender().sendSync(exchange);
            if (ExchangeStatus.ERROR.equals(this.getExchange().getStatus())) {
                InternalInfrastructureException e = new InternalInfrastructureException(this.getExchange().getError());
                throw e;
            }

        }

    }

    /**
     * Prepare response exchange.
     * 
     * @param msgBase
     *        the msg base
     * @param ctx
     *        the ctx
     * @param answerCtx
     *        the answer ctx
     * @param myDescription
     *        the my description
     * 
     * @return the in only
     * 
     * @throws Exception
     */
    private InOnly prepareResponseExchange(final OutgoingMessageBase msgBase, final CallContextExtension ctx,
            final CallContextExtension answerCtx, final OperationDescription myDescription) throws Exception {
        InOnly inOnlyExchange;
        NormalizedMessage inNM = null;

        WSAEndpointReference replyTo = answerCtx.getReplyTo();
        if (replyTo == null) throw new InternalServiceDiscoveryException("no wsa:ReplyTo found for asynchronous response");
        MessageExchangeFactory exchangeFactory = null;
        exchangeFactory = this.getKernel().createMessageExchangeFactory(replyTo);

        MessageExchange exchange = null;
        if (myDescription.getExchangePattern().equals(MessageExchangePattern.IN_OUT_URI)) {
            exchange = exchangeFactory.createInOutExchange();
        } else if (myDescription.getExchangePattern().equals(MessageExchangePattern.IN_ONLY_URI)) {
            exchange = exchangeFactory.createInOnlyExchange();
        } else if (myDescription.getExchangePattern().equals(MessageExchangePattern.OUT_ONLY_URI)) {
            exchange = exchangeFactory.createInOnlyExchange();
        }

        if (exchange == null)
            throw new MessagingException("missing mapping for exchange pattern " + myDescription.getExchangePattern());
        // populate the exchange
        QName wsdlServiceName = myDescription.getServiceDescription().getServiceQName();
        exchange.setService(wsdlServiceName);
        exchange.setInterfaceName(myDescription.getServiceDescription().getPortTypeQName());
        exchange.setOperation(new QName(wsdlServiceName.getNamespaceURI(), myDescription.getName()));

        // TODO populate the InternalCallContext .. are these values correct?
        ctx.setServiceName(exchange.getInterfaceName());
        ctx.setProviderID(exchange.getService());
        ctx.setMessageExchangeId(exchange.getExchangeId());
        ctx.setUnifiedParticipantIdentity(this.getKernel().getParticipant());
        ctx.setCommunicationStyle(InternalCommunicationStyle.ONEWAY);
        ctx.setMessageID(this.getKernel().generateUUID());
        ctx.setOperationName(myDescription.getName());
        ctx.setSOAPAction(myDescription.getSoapAction());
        inOnlyExchange = (InOnly) exchange;
        DocumentFragment refParams = answerCtx.getReferenceParameters();
        if (refParams != null) {
            ctx.setReferenceParameters(refParams);
        }
        HeaderUtil.setCallContextExtension(exchange, ctx);

        inNM = inOnlyExchange.createMessage();
        msgBase.fillMessage(inNM, ctx);
        inOnlyExchange.setInMessage(inNM);
        return inOnlyExchange;
    }

}
