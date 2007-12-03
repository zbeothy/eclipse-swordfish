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
package org.eclipse.swordfish.core.papi.impl.untyped.consumer;

import java.util.HashMap;
import java.util.Map;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.command.Command;
import org.eclipse.swordfish.core.components.command.CommandFactory;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractService;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtensionFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallRelationImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.MessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalOnewayOperationProxy;
import org.w3c.dom.DocumentFragment;

/**
 * The Class OnewayOperationProxyImpl.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class OnewayOperationProxyImpl extends AbstractOperation implements InternalOnewayOperationProxy {

    /**
     * TODO logger for this class, but why isn't it used?.
     * 
     * @param desc
     *        the desc
     * @param sbb
     *        the sbb
     * @param parent
     *        the parent
     * @param partnersWith
     *        the partners with
     */
    // private static Log log =
    // LogFactory.getLog(OnewayOperationProxyImpl.class);
    /**
     * OnewayOperationProxyImpl.
     * 
     * @param desc
     *        this operations description
     * @param sbb
     *        the managing InternalSBB instance
     * @param parent
     *        the hosting service Object
     * @param partnersWith
     */
    public OnewayOperationProxyImpl(final OperationDescription desc, final SBBExtension sbb, final AbstractService parent,
            final AbstractService partnersWith) {
        super(desc, sbb, parent, partnersWith, true);
    }

    /**
     * Callback non blocking.
     * 
     * @param outmsg
     *        the outmsg
     * @param answerCtx
     *        the answer ctx
     * 
     * @return the call context
     * 
     * @throws InvalidPayloadException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * @throws ServiceInvocationException
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#
     *      callbackNonBlocking(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage,
     *      java.lang.String)
     */
    public InternalCallContext callbackNonBlocking(final InternalOutgoingMessage outmsg, final InternalCallContext answerCtx)
            throws InternalSBBException {
        if (!this.isSupportedOperation())
            throw new InternalMessagingException(this.getName() + " is indicated by the agreed policy "
                    + "to be an invalid operation (not present in agreed policy or set to unused)");

        if (!this.isCallbackOperation())
            throw new InternalMessagingException("this operation is only allowed for operation "
                    + this.getOperationDescription().getPartnerOperationName());
        if (!(answerCtx instanceof CallContextImpl))
            throw new IllegalArgumentException("the call context was not created through InternalSBB");

        if (!(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not created through InternalSBB");

        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");

        if (answerCtx == null) throw new IllegalArgumentException("the call context must not be null");

        if (outmsg == null) throw new IllegalArgumentException("the outgoing message must not be null");

        if (!this.getOperationDescription().getPartnerOperationName().equals(answerCtx.getOperationName()))
            throw new InternalMessagingException("the call context does not belong to an "
                    + "operation of which this operation is a partner operation");

        long timer = System.currentTimeMillis();

        // check header validity
        this.checkUnsupportedMustUnderstandHeaders((MessageBase) outmsg);

        CallContextExtension replyCtx = (CallContextExtension) answerCtx;

        Kernel kernel = this.getKernel();
        if ((kernel == null) || !kernel.isActive())
            throw new InternalInfrastructureException("this InternalSBB instance has been already released");

        OutgoingMessageBase msgBase = (OutgoingMessageBase) outmsg;

        // start preparing the return value.
        CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();

        InOnly exchange;
        try {
            exchange = this.prepareResponseExchange(msgBase, ctx, (CallContextExtension) answerCtx);

            ctx.appendRelations(answerCtx.getRelations());
            ctx.pushRelation(new CallRelationImpl(InternalCallRelation.TYPE_ONEWAY, answerCtx.getMessageID()));
            ctx.setPolicy(((CallContextExtension) answerCtx).getPolicy());
            ctx.setScope(Scope.RESPONSE);
            ctx.setPartnerOperationName(replyCtx.getOperationName());
            ctx.setCorrelationID(replyCtx.getCorrelationID());
            ctx.setConsumerCallIdentifier(replyCtx.getConsumerCallIdentifier());
            ctx.setRelatesTo(replyCtx.getMessageID());
            ctx.setPolicy(replyCtx.getPolicy());
            ctx.setReferenceParameters(replyCtx.getReferenceParameters());
            ctx.setProviderID(replyCtx.getProviderID());
            ctx.setProviderPolicyID(replyCtx.getProviderPolicyID());
            ctx.setUnifiedParticipantIdentity(replyCtx.getUnifiedParticipantIdentity());
            ctx.setCreatedTimestamp(replyCtx.getCreatedTimestamp());
            ctx.setRelatedTimestamp(replyCtx.getRelatedTimestamp());

        } catch (InternalServiceDiscoveryException e) {
            throw new InternalMessagingException(e);
        }

        Command command = this.prepareResponseCommand(exchange);
        command.setExecutionBegin(timer);
        /*
         * this is a blocking call so execute the command within this thread. Note that the command
         * preforms the sending path AND the response path
         */
        Throwable th = null;
        try {
            command.execute();
        } catch (Exception e) {
            th = e;
        }
        if (command.failed() && (th == null)) {
            th = command.getThrowable();

            if (th instanceof InternalIllegalInputException) throw (InternalIllegalInputException) th;
            if (th instanceof InternalConfigurationException) throw (InternalConfigurationException) th;
            if (th instanceof InternalMessagingException) throw (InternalMessagingException) th;
            if (th instanceof InternalAuthenticationException) throw (InternalAuthenticationException) th;
            if (th instanceof InternalAuthorizationException) throw (InternalAuthorizationException) th;
            if (th instanceof PolicyViolatedException) throw new InternalMessagingException(th);
            if (th instanceof InternalInfrastructureException) throw (InternalInfrastructureException) th;
            throw new InternalInfrastructureException("non-blocking callback of " + this.getName() + " failed because of ", th);
        }

        // fill in the remaining stuff into the context before returning it
        CallContextExtension returnCtx = HeaderUtil.getCallContextExtension(exchange);

        return returnCtx;

    }

    /**
     * Call non blocking.
     * 
     * @param outmsg
     *        the outmsg
     * @param consumerCallId
     *        the consumer call id
     * 
     * @return the call context
     * 
     * @throws InvalidPayloadException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * @throws ServiceInvocationException
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalOnewayOperationProxy#callNonBlocking(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage,
     *      java.lang.String)
     */
    public InternalCallContext callNonBlocking(final InternalOutgoingMessage outmsg, final String consumerCallId)
            throws InternalSBBException {

        if ((null == outmsg) || !(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not constructed using the InternalSBB message factory");
        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");

        return this.callNonBlockingInternal(outmsg, consumerCallId, null);
    }

    /**
     * Call non blocking.
     * 
     * @param outmsg
     *        the outmsg
     * @param consumerCallId
     *        the consumer call id
     * @param relationCtx
     *        the relation ctx
     * 
     * @return the call context
     * 
     * @throws InvalidPayloadException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * @throws ServiceInvocationException
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy
     *      #callNonBlocking(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage,
     *      java.lang.String, org.eclipse.swordfish.papi.untyped.InternalCallContext)
     */
    public InternalCallContext callNonBlocking(final InternalOutgoingMessage outmsg, final String consumerCallId,
            final InternalCallContext relationCtx) throws InternalSBBException {

        if ((null == outmsg) || !(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not constructed using the InternalSBB message factory");
        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");
        if (!(relationCtx instanceof CallContextImpl))
            throw new IllegalArgumentException("the InternalCallContext was not constructed using the InternalSBB message factory");

        if (relationCtx == null) throw new IllegalArgumentException("callContext must not be null");

        return this.callNonBlockingInternal(outmsg, consumerCallId, relationCtx);
    }

    /**
     * Gets the communication style.
     * 
     * @return the communication style
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOperation#getCommunicationStyle()
     */
    @Override
    public InternalCommunicationStyle getCommunicationStyle() {
        return InternalCommunicationStyle.ONEWAY;
    }

    /**
     * Creates the exchange.
     * 
     * @param ctxe
     *        the ctxe
     * @param msg
     *        the msg
     * 
     * @return -- a message exchange that can be used by this operation (InOnly)
     * 
     * @throws InternalMessagingException
     *         if the super class cannot construct the exchange
     */
    protected InOnly createExchange(final CallContextExtension ctxe, final OutgoingMessageBase msg) throws MessagingException {
        return (InOnly) super.createMessageExchange(ctxe, msg);
    }

    /**
     * Creates the exchange.
     * 
     * @param epr
     *        the endpoint reference that can be used to send back responses
     * @param ctxe
     *        the ctxe
     * @param msg
     *        the msg
     * 
     * @return a message exchange that can be used by this operation (InOnly)
     * 
     * @throws InternalMessagingException
     */
    protected InOnly createExchange(final CallContextExtension ctxe, final WSAEndpointReference epr, final OutgoingMessageBase msg)
            throws MessagingException {
        return (InOnly) super.createMessageExchange(ctxe, epr, msg);
    }

    /**
     * internal implementation.
     * 
     * @param outmsg
     *        the outmsg
     * @param consumerCallId
     *        the consumer call id
     * @param relationCtx
     *        the relation ctx
     * 
     * @return the call context
     * 
     * @throws InvalidPayloadException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * @throws ServiceInvocationException
     */
    private InternalCallContext callNonBlockingInternal(final InternalOutgoingMessage outmsg, final String consumerCallId,
            final InternalCallContext relationCtx) throws InternalSBBException {
        if (!this.isSupportedOperation())
            throw new InternalMessagingException(this.getName() + " is indicated by the agreed policy "
                    + "to be an invalid operation (not present in agreed policy or set to unused)");

        if (this.isCallbackOperation())
            throw new InternalMessagingException("this operation is not allowed on partner operations of this operation");
        long timer = System.currentTimeMillis();

        Kernel kernel = this.getKernel();
        if ((kernel == null) || !kernel.isActive())
            throw new InternalInfrastructureException("this InternalSBB instance has been already released");

        OutgoingMessageBase msgBase = (OutgoingMessageBase) outmsg;

        // check header validity
        this.checkUnsupportedMustUnderstandHeaders(msgBase);

        // start preparing the return value.
        CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();
        // create the command
        InOnly exchange = this.prepareRequestExchange(msgBase, ctx);
        if (relationCtx != null) {
            // this is the corelation case
            ctx.appendRelations(relationCtx.getRelations());
            ctx.pushRelation(new CallRelationImpl(InternalCallRelation.TYPE_TRIGGERING_CALL, relationCtx.getMessageID()));
        }
        ctx.setConsumerCallIdentifier(consumerCallId);
        ctx.setPolicy(this.getAgreedPolicy());
        ctx.setProviderPolicyID(this.getAgreedPolicy().getProviderPolicyIdentity().getKeyName());
        ctx.setScope(Scope.REQUEST);

        Command command = this.prepareRequestCommand(exchange);
        HeaderUtil.setCallContextExtension(exchange, ctx);
        command.setExecutionBegin(timer);
        /*
         * this is a blocking call so execute the command within this thread. Note that the command
         * preforms the sending path AND the response path
         */
        Throwable th = null;
        try {
            command.execute();
        } catch (Exception e) {
            th = e;
        }
        if (command.failed() && (th == null)) {
            th = command.getThrowable();

            if (th instanceof InternalIllegalInputException) throw (InternalIllegalInputException) th;
            if (th instanceof InternalConfigurationException) throw (InternalConfigurationException) th;
            if (th instanceof InternalMessagingException) throw (InternalMessagingException) th;
            if (th instanceof InternalAuthenticationException) throw (InternalAuthenticationException) th;
            if (th instanceof InternalAuthorizationException) throw (InternalAuthorizationException) th;
            if (th instanceof PolicyViolatedException) throw new InternalMessagingException(th);
            if (th instanceof InternalInfrastructureException) throw (InternalInfrastructureException) th;
            throw new InternalInfrastructureException("non-blocking call of " + this.getName() + " failed because of ", th);
        }

        // fill in the remaining stuff into the context before returning it
        CallContextExtension returnCtx = HeaderUtil.getCallContextExtension(exchange);

        return returnCtx;

    }

    /**
     * Prepare request command.
     * 
     * @param exchange
     *        the exchange
     * 
     * @return the command
     * 
     * @throws InternalInfrastructureException
     */
    private Command prepareRequestCommand(final InOnly exchange) throws InternalInfrastructureException {
        CommandFactory commandFactory = this.getKernel().getCommandFactory();
        Command command = commandFactory.createCommand(this.getOperationDescription());
        if (command == null) throw new InternalInfrastructureException("failed to create a request interaction");
        command.setExchange(exchange);
        command.setRole(Role.SENDER);
        command.setScope(Scope.REQUEST);

        return command;
    }

    /**
     * Prepare request exchange.
     * 
     * @param msgBase
     *        the msg base
     * @param ctx
     *        the ctx
     * 
     * @return the in only
     * 
     * @throws InvalidPayloadException
     * @throws InternalInfrastructureException
     *         TODO what do I do with the call context? what is replicated?
     */
    private InOnly prepareRequestExchange(final OutgoingMessageBase msgBase, final CallContextExtension ctx)
            throws InternalIllegalInputException, InternalInfrastructureException {
        InOnly exchange;
        NormalizedMessage inNM = null;

        if (msgBase.isFaultMessage())
            throw new InternalIllegalInputException("A message on oneway requests must not be declared as fault");

        try {
            exchange = (InOnly) this.createMessageExchange(ctx, msgBase);
            inNM = exchange.createMessage();
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("missed to create a message for blocking invocation of operation "
                    + this.getName());
        }
        try {
            msgBase.fillMessage(inNM, ctx);
            exchange.setInMessage(inNM);
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("missed to fill in the content of the message"
                    + " for blocking invocation of operation " + this.getName());
        }
        return exchange;
    }

    /**
     * Prepare response command.
     * 
     * @param exchange
     *        the exchange
     * 
     * @return the command
     * 
     * @throws InternalInfrastructureException
     */
    private Command prepareResponseCommand(final InOnly exchange) throws InternalInfrastructureException {
        CommandFactory commandFactory = this.getKernel().getCommandFactory();
        Command command = commandFactory.createCommand(this.getOperationDescription());
        if (command == null) throw new InternalInfrastructureException("failed to create a response interaction");
        command.setExchange(exchange);
        command.setRole(Role.RECEIVER);
        command.setScope(Scope.RESPONSE);

        return command;
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
     * 
     * @return the in only
     * 
     * @throws InvalidPayloadException
     * @throws InternalInfrastructureException
     * @throws ServiceAddressingException
     */
    private InOnly prepareResponseExchange(final OutgoingMessageBase msgBase, final CallContextExtension ctx,
            final CallContextExtension answerCtx) throws InternalIllegalInputException, InternalInfrastructureException,
            InternalServiceDiscoveryException {
        InOnly exchange;
        NormalizedMessage inNM = null;

        if (msgBase.isFaultMessage())
            throw new InternalIllegalInputException("A message on oneway callback must not be declared as fault");

        try {

            WSAEndpointReference replyTo = answerCtx.getReplyTo();
            if (replyTo == null) throw new InternalServiceDiscoveryException("no wsa:ReplyTo found for asynchronous response");
            exchange = (InOnly) this.createMessageExchange(ctx, replyTo, msgBase);
            DocumentFragment refParams = answerCtx.getReferenceParameters();
            if (refParams != null) {
                ctx.setReferenceParameters(refParams);
            }
            ctx.setReplyTo(replyTo);

            WSAEndpointReference faultTo = answerCtx.getFaultTo();
            if (faultTo != null) {
                ctx.setFaultTo(faultTo);
                // TODO what should be down with the reference parameters of
                // faultTo
            }

            ctx.setRelatesTo(answerCtx.getRelatesTo());
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("missed to create a message for invocation of operation " + this.getName());
        }

        try {

            if (msgBase.isFaultMessage()) {
                Fault flt = exchange.createFault();
                msgBase.fillMessage(flt, ctx);
                // actually we cannot create a request with a fault slot set.
                // and unfortunatly we cannot send a request with an in message
                // that is a Fault ... So we have a problem here! Do we?
                exchange.setInMessage(flt);
            } else {
                inNM = exchange.createMessage();
                msgBase.fillMessage(inNM, ctx);
                this.setJmsProperties(inNM, ctx);
                exchange.setInMessage(inNM);
            }
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("missed to fill in the content of the message"
                    + " for blocking invocation of operation " + this.getName());
        }
        return exchange;
    }

    /**
     * Sets the jms properties.
     * 
     * @param inNM
     *        the in NM
     * @param ctx
     *        the ctx
     */
    private void setJmsProperties(final NormalizedMessage inNM, final CallContextExtension ctx) {
        WSAEndpointReference replyTo = ctx.getReplyTo();
        String applicationId = null;
        String instanceId = null;
        if (null != replyTo) {
            InternalParticipantIdentity participant = replyTo.getParticipant();
            if (null != participant) {
                applicationId = participant.getApplicationID();
                instanceId = participant.getInstanceID();
            }
        }
        if (null != applicationId) {
            Map jmsProperties = (Map) inNM.getProperty("oracle.jbi.binding.jms");
            if (null == jmsProperties) {
                jmsProperties = new HashMap();
            }
            jmsProperties.put("SBBApplicationId", applicationId);
            if (null != instanceId) {
                jmsProperties.put("SBBInstanceId", instanceId);
            }
            inNM.setProperty("oracle.jbi.binding.jms", jmsProperties);
        }

    }
}
