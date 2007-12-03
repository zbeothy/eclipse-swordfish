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
package org.eclipse.swordfish.core.papi.impl.untyped.provider;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.command.Command;
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
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalParticipantException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InvalidContextException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.exception.SBBRuntimeException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalRequestResponseOperationSkeleton;

/**
 * Implementation of the request response skeleton.
 */
public class RequestResponseOperationSkeletonImpl extends AbstractOperation implements InternalRequestResponseOperationSkeleton {

    /**
     * this is a optimization to keep the handler in the object itself in order to accelerate has
     * handler responses. If the handler is set but there is no ServiceEndpoint is opened for it
     * than this indicates an errorous consition.
     */
    private IncomingMessageHandlerProxy myHandler;

    /**
     * The Constructor.
     * 
     * @param desc
     *        the descriptor of this operation
     * @param sbb
     *        the managing InternalSBB instance
     * @param parent
     *        the service acting as the parent for this operation
     * @param partnersWith
     *        the partners with
     */
    public RequestResponseOperationSkeletonImpl(final OperationDescription desc, final SBBExtension sbb,
            final AbstractService parent, final AbstractService partnersWith) {
        super(desc, sbb, parent, partnersWith, false);
        this.myHandler = null;
    }

    /**
     * Cleanup.
     * 
     * @param sbbInitiated
     *        the sbb initiated
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation#cleanup(boolean)
     */
    @Override
    public void cleanup(final boolean sbbInitiated) {
        if (this.hasMessageHandler()) {
            try {
                if (this.isCallbackOperation())
                    throw new SBBRuntimeException("this code should never be reached as request responses do not have partners");
                else {
                    this.getKernel().unregisterMessageHandler(
                            this.getOperationDescription().getServiceDescription().getServiceQName(), this.getName(),
                            Role.RECEIVER, sbbInitiated);
                }
            } catch (MessageHandlerRegistrationException e) {
                e.printStackTrace();
                // TODO log the fact of what happened right now
            }
        }
        this.myHandler = null;
        super.cleanup(sbbInitiated);
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
        return InternalCommunicationStyle.REQUEST_RESPONSE;
    }

    /**
     * Gets the message handler.
     * 
     * @return -- the previously registered message handler or null if no message handler was
     *         registered
     */
    public InternalIncomingMessageHandler getMessageHandler() {
        return this.myHandler.getHandler();
    }

    /**
     * Checks for message handler.
     * 
     * @return true, if has message handler
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalOnewayOperationSkeleton#hasMessageHandler()
     */
    public boolean hasMessageHandler() {
        return this.myHandler != null;
    }

    /**
     * Register message handler.
     * 
     * @param handler
     *        the handler
     * 
     * @throws InternalInfrastructureException
     * @throws MessageHandlerRegistrationException
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalOnewayOperationSkeleton#
     *      registerMessageHandler(org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler)
     */
    public void registerMessageHandler(final InternalIncomingMessageHandler handler) throws InternalInfrastructureException,
            MessageHandlerRegistrationException {

        if (this.myHandler != null) throw new MessageHandlerRegistrationException("remove existing message handler first");
        if (handler == null) throw new IllegalArgumentException("A handler to be registered must not be null");
        // remember the handler yourself
        this.myHandler = new IncomingMessageHandlerProxy(this, handler);
        if (this.isCallbackOperation())
            throw new SBBRuntimeException("this code should never be reached as request responses do not have partners");
        else {
            this.getKernel().registerMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                    this.getName(), Role.RECEIVER, this.myHandler);
        }

    }

    /**
     * Release message handler.
     * 
     * @return true, if release message handler
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalOnewayOperationSkeleton#releaseMessageHandler()
     */
    public boolean releaseMessageHandler() {
        boolean success = false;
        try {
            if (this.isCallbackOperation())
                throw new SBBRuntimeException("this code should never be reached as request responses do not have partners");
            else {
                this.getKernel().unregisterMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                        this.getName(), Role.RECEIVER, false);
            }
            success = true;
            this.myHandler = null;
        } catch (MessageHandlerRegistrationException e) {
            e.printStackTrace();
            // TODO log the fact of what happened right now
        }
        return success;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalRequestResponseOperationSkeleton#sendError(org.eclipse.swordfish.core.papi.impl.exception.InternalParticipantException,
     *      org.eclipse.swordfish.papi.untyped.InternalCallContext)
     */
    public InternalCallContext sendError(final InternalParticipantException arg0, final InternalCallContext arg1)
            throws InternalSBBException {
        // dummy method to satisfy compiler while exception refactoring is in
        // progress
        return null;
    }

    /**
     * Send error.
     * 
     * @param except
     *        the except
     * @param answerCtx
     *        the answer ctx
     * 
     * @return the call context
     * 
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalRequestResponseOperationSkeleton#
     *      sendError(org.eclipse.swordfish.papi.exception.ServiceInvocationException,
     *      org.eclipse.swordfish.papi.untyped.InternalCallContext)
     */
    public InternalCallContext sendError(final InternalSBBException except, final InternalCallContext answerCtx)
            throws InternalAuthenticationException, InternalAuthorizationException, InternalInfrastructureException {

        if (!(answerCtx instanceof CallContextImpl))
            throw new IllegalArgumentException("the call context was not created through InternalSBB or is null");

        if (answerCtx == null) throw new IllegalArgumentException("the call context must not be null");

        if (except == null) throw new IllegalArgumentException("the exception to send back must not be null");

        CallContextExtension replyCtx = (CallContextExtension) answerCtx;
        MessageExchange origExchange = this.getKernel().getAndRemoveExchange(replyCtx.getMessageExchangeId());

        if (!(origExchange instanceof InOut))
            throw new InvalidContextException("Bad InternalCallContext. Exchange entry is not InOut");

        InOut exchange = (InOut) origExchange;
        if (exchange == null) throw new InvalidContextException("Bad InternalCallContext. Exchange entry is null");

        CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();
        ctx.appendRelations(answerCtx.getRelations());
        ctx.pushRelation(new CallRelationImpl(InternalCallRelation.TYPE_REQUEST, answerCtx.getMessageID()));
        // TODO populate all other values out of the answerCtx!!! Is this the
        // right place to do this? further more: The list is not complete
        ctx.setMessageExchangeId(exchange.getExchangeId());
        ctx.setMessageID(this.createMessageId());
        ctx.setCorrelationID(replyCtx.getCorrelationID());
        ctx.setConsumerCallIdentifier(replyCtx.getConsumerCallIdentifier());
        ctx.setRelatesTo(replyCtx.getMessageID());
        ctx.setCommunicationStyle(replyCtx.getCommunicationStyle());
        ctx.setOperationName(replyCtx.getOperationName());
        ctx.setServiceName(replyCtx.getServiceName());
        ctx.setPolicy(replyCtx.getPolicy());
        ctx.setReferenceParameters(replyCtx.getReferenceParameters());
        ctx.setProviderID(replyCtx.getProviderID());
        ctx.setProviderPolicyID(replyCtx.getProviderPolicyID());
        ctx.setScope(Scope.RESPONSE);
        ctx.setUnifiedParticipantIdentity(replyCtx.getUnifiedParticipantIdentity());
        ctx.setCallerSubject(replyCtx.getCallerSubject());
        ctx.setSOAPAction(this.getOperationDescription().getSoapAction());
        ctx.setCreatedTimestamp(replyCtx.getCreatedTimestamp());
        ctx.setRelatedTimestamp(replyCtx.getRelatedTimestamp());

        // and finally set the context into the exchange
        HeaderUtil.setCallContextExtension(exchange, ctx);
        try {
            // make sure the out slot is empty
            exchange.setOutMessage(null);
            exchange.setStatus(ExchangeStatus.ERROR);
            exchange.setError(except);
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("cannot create the response content", e);
        }

        this.cleanupCallbacks(exchange);
        this.setAuthCallbacks(exchange, null);

        Command command = this.getKernel().getCommandFactory().createCommand(this.getOperationDescription());
        command.setExchange(exchange);
        if (command == null) throw new InternalInfrastructureException("cannot process the response, got a null command");
        command.setRole(Role.RECEIVER);
        command.setScope(Scope.RESPONSE);
        command.setMessageHandler(this.myHandler);
        // responses are always send sync
        Throwable th = null;
        try {
            command.execute();
        } catch (Exception e) {
            th = e;
        }
        if (command.failed() && (th == null)) {
            th = command.getThrowable();

            if (th instanceof InternalAuthenticationException) throw (InternalAuthenticationException) th;
            if (th instanceof InternalAuthorizationException) throw (InternalAuthorizationException) th;
            if (th instanceof InternalInfrastructureException) throw (InternalInfrastructureException) th;
            throw new InternalInfrastructureException("response call of " + this.getName() + " failed because of ", th);
        }

        CallContextExtension returnCtx = HeaderUtil.getCallContextExtension(exchange);

        return returnCtx;
    }

    /**
     * Send response.
     * 
     * @param outMsg
     *        the out msg
     * @param answerCtx
     *        the answer ctx
     * 
     * @return the call context
     * 
     * @throws InvalidPayloadException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalRequestResponseOperationSkeleton#
     *      sendResponse(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage,
     *      org.eclipse.swordfish.papi.untyped.InternalCallContext)
     */
    public InternalCallContext sendResponse(final InternalOutgoingMessage outMsg, final InternalCallContext answerCtx)
            throws InternalIllegalInputException, InternalAuthenticationException, InternalAuthorizationException,
            InternalInfrastructureException, InternalMessagingException {

        if (!(answerCtx instanceof CallContextImpl))
            throw new IllegalArgumentException("the call context was not created through InternalSBB or is null");
        if (!(outMsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not created through InternalSBB");
        if (outMsg == null) throw new IllegalArgumentException("the outgoing message must not be null");
        if (answerCtx == null) throw new IllegalArgumentException("the call context must not be null");

        OutgoingMessageBase response = (OutgoingMessageBase) outMsg;

        // check header validity
        this.checkUnsupportedMustUnderstandHeaders(response);

        CallContextExtension replyCtx = (CallContextExtension) answerCtx;
        MessageExchange origExchange = this.getKernel().getAndRemoveExchange(replyCtx.getMessageExchangeId());

        if (origExchange == null)
            throw new InternalIllegalInputException(
                    "invalid InternalCallContext, attempt to send the response through a wrong InternalSBB"
                            + " instance or there was already an attempt to send the response.");

        if (!(origExchange instanceof InOut))
            throw new InternalIllegalInputException("Bad InternalCallContext. Exchange type not suitable for response (no InOut)");

        InOut exchange = (InOut) origExchange;

        CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();
        ctx.appendRelations(answerCtx.getRelations());
        ctx.pushRelation(new CallRelationImpl(InternalCallRelation.TYPE_REQUEST, answerCtx.getMessageID()));
        // TODO populate all other values out of the answerCtx!!! Is this the
        // right place to do this? further more: The list is not complete
        ctx.setMessageExchangeId(exchange.getExchangeId());
        ctx.setMessageID(this.createMessageId());
        ctx.setCorrelationID(replyCtx.getCorrelationID());
        ctx.setConsumerCallIdentifier(replyCtx.getConsumerCallIdentifier());
        ctx.setRelatesTo(replyCtx.getMessageID());
        ctx.setCommunicationStyle(replyCtx.getCommunicationStyle());
        ctx.setOperationName(replyCtx.getOperationName());
        ctx.setServiceName(replyCtx.getServiceName());
        ctx.setPolicy(replyCtx.getPolicy());
        ctx.setReferenceParameters(replyCtx.getReferenceParameters());
        ctx.setProviderID(replyCtx.getProviderID());
        ctx.setProviderPolicyID(replyCtx.getProviderPolicyID());
        ctx.setScope(Scope.RESPONSE);
        ctx.setUnifiedParticipantIdentity(replyCtx.getUnifiedParticipantIdentity());
        ctx.setCallerSubject(replyCtx.getCallerSubject());
        ctx.setSOAPAction(this.getOperationDescription().getSoapAction());
        ctx.setCreatedTimestamp(replyCtx.getCreatedTimestamp());
        ctx.setRelatedTimestamp(replyCtx.getRelatedTimestamp());

        // and finally set the context into the exchange
        HeaderUtil.setCallContextExtension(exchange, ctx);
        try {
            if (response.isFaultMessage()) {
                Fault fault = exchange.createFault();
                response.fillMessage(fault, ctx);
                exchange.setFault(fault);
            } else {
                NormalizedMessage nm = exchange.createMessage();
                response.fillMessage(nm, ctx);
                exchange.setOutMessage(nm);
            }
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("cannot create the response content", e);
        }

        this.cleanupCallbacks(exchange);
        this.setAuthCallbacks(exchange, response);

        Command command = this.getKernel().getCommandFactory().createCommand(this.getOperationDescription());
        command.setExchange(exchange);
        if (command == null) throw new InternalInfrastructureException("cannot process the response, got a null command");
        command.setRole(Role.RECEIVER);
        command.setScope(Scope.RESPONSE);
        command.setMessageHandler(this.myHandler);
        // responses are always send sync
        Throwable th = null;
        try {
            command.execute();
        } catch (Exception e) {
            th = e;
        }
        if (command.failed() && (th == null)) {
            th = command.getThrowable();

            if (th instanceof InternalAuthenticationException) throw (InternalAuthenticationException) th;
            if (th instanceof InternalAuthorizationException) throw (InternalAuthorizationException) th;
            if (th instanceof PolicyViolatedException) throw new InternalMessagingException(th);
            if (th instanceof InternalInfrastructureException) throw (InternalInfrastructureException) th;
            throw new InternalInfrastructureException("response call of " + this.getName() + " failed because of ", th);
        }

        CallContextExtension returnCtx = HeaderUtil.getCallContextExtension(exchange);

        return returnCtx;
    }
}
