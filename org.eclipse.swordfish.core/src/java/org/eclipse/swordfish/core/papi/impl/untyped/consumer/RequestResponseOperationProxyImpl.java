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

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.command.Command;
import org.eclipse.swordfish.core.components.command.CommandFactory;
import org.eclipse.swordfish.core.components.command.InOutCommand;
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
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalRemoteException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalRequestResponseOperationProxy;

/**
 * The Class RequestResponseOperationProxyImpl.
 * 
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RequestResponseOperationProxyImpl extends AbstractOperation implements InternalRequestResponseOperationProxy {

    /** private reference to this operations incoming message handler. */
    private IncomingMessageHandlerProxy myHandler;

    /**
     * the constructor to create a request-response proxy operation.
     * 
     * @param desc
     *        this operations description
     * @param sbb
     *        the managing InternalSBB instance
     * @param parent
     *        the InternalServiceProxy object that contains this operation
     * @param partnersWith
     *        the partners with
     */
    public RequestResponseOperationProxyImpl(final OperationDescription desc, final SBBExtension sbb, final AbstractService parent,
            final AbstractService partnersWith) {
        super(desc, sbb, parent, partnersWith, true);
        this.myHandler = null;
    }

    /**
     * Call blocking.
     * 
     * @param outmsg
     *        the outmsg
     * 
     * @return the incoming message
     * 
     * @throws InvalidPayloadException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * @throws ServiceInvocationException
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#
     *      callBlocking(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage,
     *      org.eclipse.swordfish.papi.untyped.InternalCallContext)
     */
    public InternalIncomingMessage callBlocking(final InternalOutgoingMessage outmsg) throws InternalSBBException {

        if (!(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not contructed using the InternalSBB message factory");
        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");
        if ((outmsg == null)) throw new IllegalArgumentException("the outgoing message was null");
        return this.callBlockingInternal(outmsg, null);
    }

    /**
     * this method can throw an OperationException (RuntimeException) if the operation is not
     * supported because of it being marked as "unused" in the agreed policy <br>.
     * 
     * @param outmsg
     *        the outmsg
     * @param relationCtx
     *        the relation ctx
     * 
     * @return the incoming message
     * 
     * @throws InvalidPayloadException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * @throws ServiceInvocationException
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#
     *      callBlocking(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage)
     */
    public InternalIncomingMessage callBlocking(final InternalOutgoingMessage outmsg, final InternalCallContext relationCtx)
            throws InternalSBBException {
        // Check parameter validity and pass the call to the internal
        // implementation

        if (!(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not contructed using the InternalSBB message factory");
        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");
        if (!(relationCtx instanceof CallContextImpl))
            throw new IllegalArgumentException("the context is not constructed through InternalSBB");

        if ((outmsg == null)) throw new IllegalArgumentException("the outgoing message must not be null");

        if ((relationCtx == null)) throw new IllegalArgumentException("the context must not be null");

        return this.callBlockingInternal(outmsg, relationCtx);
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
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy
     *      #callNonBlocking(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage,
     *      java.lang.String, org.eclipse.swordfish.papi.untyped.InternalCallContext)
     */
    public InternalCallContext callNonBlocking(final InternalOutgoingMessage outmsg, final String consumerCallId)
            throws InternalSBBException {

        if (!(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not contructed using the InternalSBB message factory");
        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");
        if ((outmsg == null)) throw new IllegalArgumentException("the outgoing message must not be null");

        return this.callNonBlockingInternal(outmsg, consumerCallId, null);
    }

    /**
     * Call non blocking.
     * 
     * @param outmsg
     *        the outmsg
     * @param consumerCallIdentifier
     *        the consumer call identifier
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
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#
     *      callNonBlocking(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage,
     *      java.lang.String)
     */
    public InternalCallContext callNonBlocking(final InternalOutgoingMessage outmsg, final String consumerCallIdentifier,
            final InternalCallContext relationCtx) throws InternalSBBException {

        if (!(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not contructed using the InternalSBB message factory");
        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");
        if (!(relationCtx instanceof CallContextImpl))
            throw new IllegalArgumentException("the context is not constructed through InternalSBB");

        if ((outmsg == null)) throw new IllegalArgumentException("the outgoing message must not be null");

        if ((relationCtx == null)) throw new IllegalArgumentException("the context must not be null");

        return this.callNonBlockingInternal(outmsg, consumerCallIdentifier, relationCtx);
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
                this.getKernel().getHandlerRegistry().remove(Role.SENDER,
                        this.getOperationDescription().getServiceDescription().getServiceQName(), this.getName(), sbbInitiated);
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
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#hasMessageHandler()
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
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#
     *      registerMessageHandler(org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler)
     */
    public void registerMessageHandler(final InternalIncomingMessageHandler handler) throws InternalInfrastructureException,
            MessageHandlerRegistrationException {

        if (handler == null) throw new IllegalArgumentException("InternalIncomingMessageHandler must not be null.");

        if (!this.isSupportedOperation())
            throw new MessageHandlerRegistrationException(this.getName() + " is indicated by the agreed policy "
                    + "to be an invalid operation (not present in agreed policy or set to unused)");

        if (this.myHandler != null)
            throw new MessageHandlerRegistrationException("remove existing message handlerfirst");
        else {
            this.myHandler = new IncomingMessageHandlerProxy(this, handler);
            this.getKernel().registerMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                    this.getName(), Role.SENDER, this.myHandler);
        }

    }

    /**
     * Release message handler.
     * 
     * @return true, if release message handler
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#releaseMessageHandler()
     */
    public boolean releaseMessageHandler() {
        this.getKernel().unregisterMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                this.getName(), Role.SENDER, false);
        this.myHandler = null;
        return true;

    }

    /**
     * this method can throw an OperationException (RuntimeException) if the operation is not
     * supported because of it being marked as "unused" in the agreed policy <br>.
     * 
     * @param outmsg
     *        the outmsg
     * @param relationCtx
     *        the relation ctx
     * 
     * @return the incoming message
     * 
     * @throws InvalidPayloadException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * @throws InternalInfrastructureException
     * @throws ServiceInvocationException
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#
     *      callBlocking(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage)
     */
    private InternalIncomingMessage callBlockingInternal(final InternalOutgoingMessage outmsg, final InternalCallContext relationCtx)
            throws InternalSBBException {
        long timer = System.currentTimeMillis();

        if (!this.isSupportedOperation())
            throw new InternalMessagingException(this.getName() + " is indicated by the agreed policy "
                    + "to be an invalid operation (not present in agreed policy or set to unused)");

        Kernel kernel = this.getKernel();
        if ((kernel == null) || !kernel.isActive())
            throw new InternalInfrastructureException("this InternalSBB instance has been already released");

        OutgoingMessageBase msgBase = (OutgoingMessageBase) outmsg;

        // check header validity
        this.checkUnsupportedMustUnderstandHeaders(msgBase);

        // create the relation array for sending reasons
        CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();

        // create the command
        InOut requestExchange = this.prepareExchange(msgBase, ctx);
        ctx.setScope(Scope.REQUEST);
        ctx.setPolicy(this.getAgreedPolicy());
        ctx.setProviderPolicyID(this.getAgreedPolicy().getProviderPolicyIdentity().getKeyName());
        if (relationCtx != null) {
            ctx.appendRelations(relationCtx.getRelations());
            ctx.pushRelation(new CallRelationImpl(InternalCallRelation.TYPE_TRIGGERING_CALL, relationCtx.getMessageID()));
        }
        Command command = this.prepareCommand(requestExchange, true);
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
        if (command.failed()) { // && th == null) {
            th = command.getThrowable();

            if (th instanceof InternalIllegalInputException) throw (InternalIllegalInputException) th;
            if (th instanceof InternalConfigurationException) throw (InternalConfigurationException) th;
            if (th instanceof InternalMessagingException) throw (InternalMessagingException) th;
            if (th instanceof InternalAuthenticationException) throw (InternalAuthenticationException) th;
            if (th instanceof InternalAuthorizationException) throw (InternalAuthorizationException) th;
            if (th instanceof InternalRemoteException) throw (InternalRemoteException) th;
            if (th instanceof PolicyViolatedException) throw new InternalMessagingException(th);
            if (th instanceof InternalInfrastructureException) throw (InternalInfrastructureException) th;
            throw new InternalInfrastructureException("Blocking call of " + this.getName() + " failed. ", th);
        }
        // do result handling
        InOut exchange = (InOut) command.getExchange();
        if (exchange == null) throw new InternalInfrastructureException("the unit of messaging has become null!?");

        if (ExchangeStatus.ERROR.equals(exchange.getStatus())) throw new InternalInfrastructureException(exchange.getError());

        NormalizedMessage outNM = null;
        IncomingMessageBase inMsg = null;
        if (exchange.getFault() != null) {
            outNM = exchange.getFault();
            inMsg = IncomingMessageFactory.createIncomingMessage(outNM);
            inMsg.setFaultMessage(true);
        } else {
            outNM = exchange.getOutMessage();
            inMsg = IncomingMessageFactory.createIncomingMessage(outNM);
        }
        if (outNM == null) throw new InternalInfrastructureException("the incoming message content disappeared!!");

        // this is hopefully the new context
        CallContextExtension returnCtx = HeaderUtil.getCallContextExtension(exchange);

        inMsg.setCallContext(returnCtx);

        return inMsg;
    }

    /**
     * The internal implementation.
     * 
     * @param outmsg
     *        the outmsg
     * @param consumerCallIdentifier
     *        the consumer call identifier
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
    private InternalCallContext callNonBlockingInternal(final InternalOutgoingMessage outmsg, final String consumerCallIdentifier,
            final InternalCallContext relationCtx) throws InternalSBBException {
        long timer = System.currentTimeMillis();

        if (!this.isSupportedOperation())
            throw new InternalMessagingException(this.getName() + " is indicated by the agreed policy "
                    + "to be an invalid operation (not present in agreed policy or set to unused)");

        Kernel kernel = this.getKernel();
        if ((kernel == null) || !kernel.isActive())
            throw new InternalInfrastructureException("this InternalSBB instance has been already released");

        OutgoingMessageBase msgBase = (OutgoingMessageBase) outmsg;

        // check header validity
        this.checkUnsupportedMustUnderstandHeaders(msgBase);

        // create the relation array for sending reasons
        CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();
        // create the exchange and set the sedired values to the context
        // afterwards

        InOut exchange = this.prepareExchange(msgBase, ctx);
        if (relationCtx != null) {
            ctx.appendRelations(relationCtx.getRelations());
            ctx.pushRelation(new CallRelationImpl(InternalCallRelation.TYPE_TRIGGERING_CALL, relationCtx.getMessageID()));
        }
        ctx.setScope(Scope.REQUEST);
        ctx.setConsumerCallIdentifier(consumerCallIdentifier);
        ctx.setPolicy(this.getAgreedPolicy());
        ctx.setProviderPolicyID(this.getAgreedPolicy().getProviderPolicyIdentity().getKeyName());
        // prepare the command
        Command command = this.prepareCommand(exchange, false);
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
            throw new InternalInfrastructureException("Non-blocking call of " + this.getName() + " failed. ", th);
        }

        CallContextExtension returnCtx = HeaderUtil.getCallContextExtension(exchange);
        return returnCtx;

    }

    /**
     * Prepare command.
     * 
     * @param exchange
     *        the exchange
     * @param isSync
     *        the is sync
     * 
     * @return the in out command
     * 
     * @throws InvalidPayloadException
     * @throws InternalInfrastructureException
     */
    private InOutCommand prepareCommand(final InOut exchange, final boolean isSync) throws InternalInfrastructureException {
        CommandFactory commandFactory = this.getKernel().getCommandFactory();
        InOutCommand command = (InOutCommand) commandFactory.createCommand(this.getOperationDescription());
        if (command == null) throw new InternalInfrastructureException("failed to create a request response interaction");
        command.setExchange(exchange);
        command.setRole(Role.SENDER);
        command.setScope(Scope.REQUEST);
        command.setSync(isSync);

        return command;
    }

    /**
     * Prepare exchange.
     * 
     * @param msgBase
     *        the msg base
     * @param ctx
     *        the ctx
     * 
     * @return the in out
     * 
     * @throws InvalidPayloadException
     * @throws InternalInfrastructureException
     *         TODO what do I do with the call context? what is replicated?
     */
    // FIXME InvalidPayloadException ist bloede! Lass die ConstructioException
    // fliegen!
    private InOut prepareExchange(final OutgoingMessageBase msgBase, final CallContextExtension ctx)
            throws InternalIllegalInputException, InternalInfrastructureException {
        InOut exchange;
        NormalizedMessage inNM = null;

        if (msgBase.isFaultMessage()) throw new InternalIllegalInputException("A request message must not be declared as fault");

        try {
            exchange = (InOut) super.createMessageExchange(ctx, msgBase);
            inNM = exchange.createMessage();
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("missed to create a message for blocking invocation of operation "
                    + this.getName(), e);
        }
        try {
            msgBase.fillMessage(inNM, ctx);
            exchange.setInMessage(inNM);
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("missed to fill in the content of the message"
                    + " for blocking invocation of operation " + this.getName(), e);
        }
        return exchange;
    }
}
