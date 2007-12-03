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

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.command.Command;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.messaging.DeliveryChannelSender;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.ExchangePattern;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.InteractionStyle;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.notification.impl.CoreMessageProcessingNotification;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalMessageNotAcceptedException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalParticipantException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InternalTemporaryOutOfOrderException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * This class represents the capabilities of all commands. It provides the infra structure for its
 * subclasses, implementing the MessageHandler resolving and dispatching of the Message exchange to
 * the right method with respect to the role and scope set.
 */
public abstract class AbstractCommand implements Command {

    /** indicates the system time in millis as this command started execution. */
    private long executionBegin;

    /** indicates the end of the execution of this command. */
    private long executionEnd;

    /** current role. */
    private Role role;

    /** current scope. */
    private Scope scope;

    /**
     * access for all subclasses for the delivery channel sender to manage sending messages.
     */
    private DeliveryChannelSender deliveryChannelSender;

    /** Comment for <code>handlerRegistry</code>. */
    private Kernel kernel;

    /** indicate if the execution of this command has failed. */
    private boolean failed;

    /** the reason why this command failed. */
    private Exception toThrow;

    /** The unit of work for this particular command. */
    private MessageExchange exchange;

    /** convenience to have a link to the participant message messageHandler. */
    private IncomingMessageHandlerProxy messageHandler;

    /**
     * indicates if the catched exception in the run method should be rethrown or not.
     */
    private boolean rethrowFlag;

    /**
     * the meta data describing the operation for whic a message is processed in this command.
     */
    private OperationDescription operationDescription;

    /**
     * the constructor Xo|.
     */
    public AbstractCommand() {
        this.failed = false;
        this.toThrow = null;
        this.executionBegin = 0;
        this.executionEnd = 0;
        this.rethrowFlag = false;
    }

    /**
     * Execute.
     * 
     * @throws Exception
     * 
     * @see java.lang.Runnable#run()
     */
    public void execute() throws Exception {
        if (this.role == null) throw new ComponentRuntimeException("commands Role is not set");

        if (this.scope == null) throw new ComponentRuntimeException("commands scope is not set");
        try {
            this.executionBegin = this.executionBegin == 0 ? System.currentTimeMillis() : this.executionBegin;
            /*
             * Consumer case
             */
            if (this.role.equals(Role.SENDER)) {
                if (this.scope.equals(Scope.REQUEST)) {
                    this.executeOutgoingRequest();
                } else {
                    this.executeIncomingResponse();
                }
            } else {
                /*
                 * provider case
                 */
                if (this.scope.equals(Scope.REQUEST)) {
                    if (!this.refuseExchangeOnMissingMessageHandler()) {
                        this.executeIncomingRequest();
                    }
                } else {
                    this.executeOutgoingResponse();
                }
            }
        } catch (Throwable t) {
            this.failed = true;
            this.toThrow = t instanceof Exception ? (Exception) t : new Exception("preventing throwable from being lost", t);
            if (this.role.equals(Role.SENDER)) {
                if (this.scope.equals(Scope.REQUEST)) {
                    if (this.rethrowFlag) throw this.toThrow;
                } else
                    throw this.toThrow;

            } else { // role.equals(Role.RECEIVER)
                if (this.scope.equals(Scope.REQUEST)) {
                    this.refuseExchangeWithError(this.toThrow);
                } else {
                    if (this.rethrowFlag) throw this.toThrow;
                }

            }
        } finally {
            // clean up references
            this.deliveryChannelSender = null;
            this.kernel = null;
            this.messageHandler = null;
            this.operationDescription = null;
            this.executionEnd = System.currentTimeMillis();
        }
    }

    /**
     * Failed.
     * 
     * @return true, if failed
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#failed()
     */
    public boolean failed() {
        return this.failed;
    }

    /**
     * release all references at the finalization time.
     * 
     * @throws Throwable
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalize() throws Throwable {
        this.scope = null;
        this.role = null;
        this.exchange = null;
        this.deliveryChannelSender = null;
        this.kernel = null;
        super.finalize();
    }

    /**
     * Gets the call context.
     * 
     * @return the call Context in the current Exchange
     */
    public CallContextExtension getCallContext() {
        return HeaderUtil.getCallContextExtension(this.getExchange());
    }

    /**
     * Gets the exchange.
     * 
     * @return the exchange
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#getExchange()
     */
    public MessageExchange getExchange() {
        return this.exchange;
    }

    /**
     * Gets the execution begin.
     * 
     * @return Returns the executionBegin
     */
    public long getExecutionBegin() {
        return this.executionBegin;
    }

    /**
     * Gets the execution end.
     * 
     * @return -- the execution duration of the still executing command
     */
    public long getExecutionEnd() {
        return this.executionEnd;
    }

    /**
     * Gets the kernel.
     * 
     * @return the kernel
     */
    public Kernel getKernel() {
        return this.kernel;
    }

    /**
     * Gets the throwable.
     * 
     * @return the throwable
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#getThrowable()
     */
    public Throwable getThrowable() {
        return this.toThrow;
    }

    /**
     * Sets the delivery channel sender.
     * 
     * @param deliveryChannelSender
     *        The deliveryChannelSender to set.
     */
    public void setDeliveryChannelSender(final DeliveryChannelSender deliveryChannelSender) {
        this.deliveryChannelSender = deliveryChannelSender;
    }

    /**
     * Sets the exchange.
     * 
     * @param exchange
     *        the exchange
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#setExchange(javax.jbi.messaging.MessageExchange)
     */
    public void setExchange(final MessageExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * sets the time of when this command is supposed to be begun.
     * 
     * @param begin
     *        the value of time in millis
     */
    public void setExecutionBegin(final long begin) {
        this.executionBegin = begin;
    }

    /**
     * Sets the kernel.
     * 
     * @param kernel
     *        the new kernel
     */
    public void setKernel(final Kernel kernel) {
        this.kernel = kernel;
    }

    // changes for splitting

    /**
     * Sets the message handler.
     * 
     * @param handler
     *        the handler
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#
     *      setMessageHandler(org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler)
     */
    public void setMessageHandler(final IncomingMessageHandlerProxy handler) {
        this.messageHandler = handler;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#setOperationDescription(org.eclipse.swordfish.core.components.iapi.OperationDescription)
     */
    public void setOperationDescription(final OperationDescription desc) {
        this.operationDescription = desc;
    }

    /**
     * Sets the rethrow exception.
     * 
     * @param flag
     *        the flag
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#setRethrowException(boolean)
     */
    public void setRethrowException(final boolean flag) {
        this.rethrowFlag = flag;
    }

    /**
     * Sets the role.
     * 
     * @param role
     *        The role to set.
     */
    public void setRole(final Role role) {
        this.role = role;
    }

    /**
     * Sets the scope.
     * 
     * @param scope
     *        The scope to set.
     */
    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    /**
     * for cases where we are done with the exchange (no matter if positive or negative) we must
     * send an ack with the exchange to the delivery channel. This method does exactlty this.
     */
    protected void ackExchange() {
        this.getDeliveryChannelSender().ackExchange(this.getExchange());
    }

    /**
     * Adds the created timestamp.
     */
    protected void addCreatedTimestamp() {
        CallContextExtension callCtx = HeaderUtil.getCallContextExtension(this.getExchange());
        if (callCtx.getCreatedTimestamp() == 0) {
            callCtx.setCreatedTimestamp(System.currentTimeMillis());
        }
    }

    /**
     * Adds the related timestamp.
     */
    protected void addRelatedTimestamp() {
        CallContextExtension callCtx = HeaderUtil.getCallContextExtension(this.getExchange());
        if (callCtx.getRelatedTimestamp() == 0) {
            callCtx.setRelatedTimestamp(System.currentTimeMillis());
        }
    }

    /**
     * applies the assertions in the current agreed policy to the message Exchange. the request
     * scope makes this method to run the handleRequest method of the policy processor, and the
     * response scope make the handleResponse path tp be applied.
     * 
     * @throws InternalSBBException
     *         if the policy processing fails
     * @throws PolicyViolatedException
     */
    protected void applyPolicy() throws InternalSBBException, PolicyViolatedException {
        if (Scope.REQUEST.equals(this.getScope())) {
            this.getKernel().getPolicyRouter().handleRequest(this.getExchange(), this.getRole(), this.getAgreedPolicy());
        } else {
            this.getKernel().getPolicyRouter().handleResponse(this.getExchange(), this.getRole(), this.getAgreedPolicy());
        }
    }

    /**
     * Execute incoming request.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#executeIncomingRequest()
     */
    protected abstract void executeIncomingRequest() throws InternalSBBException, PolicyViolatedException;

    /**
     * Execute incoming response.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#executeIncomingResponse()
     */
    protected abstract void executeIncomingResponse() throws InternalSBBException, PolicyViolatedException;

    /**
     * Execute outgoing request.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#executeOutgoingRequest()
     */
    protected abstract void executeOutgoingRequest() throws InternalSBBException, PolicyViolatedException;

    /**
     * Execute outgoing response.
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.command.Command#executeOutgoingResponse()
     */
    protected abstract void executeOutgoingResponse() throws InternalSBBException, PolicyViolatedException;

    /**
     * Gets the agreed policy.
     * 
     * @return -- the agreed policy if present, otherwise this method returns always a new instance
     *         of an empty agreed policy
     */
    protected AgreedPolicy getAgreedPolicy() {
        return this.getCallContext().getPolicy();
    }

    /**
     * Gets the current normalized message.
     * 
     * @return -- the normalized message in the current scope or null if the mep is null (this
     *         happend for instance after deserialization)
     */
    protected NormalizedMessage getCurrentNormalizedMessage() {
        return HeaderUtil.getLatestValidNormalizedMessage(this.getExchange(), this.getScope());
    }

    /**
     * Gets the delivery channel sender.
     * 
     * @return Returns the deliveryChannelSender.
     */
    protected DeliveryChannelSender getDeliveryChannelSender() {
        return this.deliveryChannelSender;
    }

    /**
     * Gets the exchange pattern.
     * 
     * @return the exchange pattern
     */
    protected abstract ExchangePattern getExchangePattern();

    /**
     * Gets the interaction style.
     * 
     * @return the interaction style
     */
    protected abstract InteractionStyle getInteractionStyle();

    /**
     * Gets the operation description.
     * 
     * @return the operation description
     */
    protected OperationDescription getOperationDescription() {
        return this.operationDescription;
    }

    /**
     * Gets the operation name.
     * 
     * @return the operation name
     */
    protected abstract String getOperationName();

    /**
     * Gets the participant role.
     * 
     * @return the participant role
     */
    protected abstract ParticipantRole getParticipantRole();

    /**
     * Gets the role.
     * 
     * @return Returns the role.
     */
    protected Role getRole() {
        return this.role;
    }

    /**
     * Gets the scope.
     * 
     * @return Returns the scope.
     */
    protected Scope getScope() {
        return this.scope;
    }

    /**
     * invokes the participants HandleError method but previously setting the correct class loaders.
     * 
     * @param excp
     *        the excp
     * @param callCtx
     *        the ctx
     * 
     * @throws ErrorConditionHandlingException *
     * @throws InternalSBBException
     * @throws InternalParticipantException
     */
    protected void invokeHandleErrorFromHandler(final InternalSBBException excp, final InternalCallContext callCtx)
            throws InternalParticipantException, InternalSBBException {
        InternalIncomingMessageHandler handlerProxy = this.resolveMessageHandler();
        if (handlerProxy == null) throw new InternalMessagingException("no message handler could be resolved.");
        // class loader switch is done in the proxy class
        handlerProxy.handleError(excp, callCtx);
    }

    /**
     * invokes the participants HandleMessage method but previously setting the correct class
     * loaders.
     * 
     * @param inMsg
     *        the message to be passed to the participant code
     * 
     * @throws MessageHandlingException
     *         rethrown exception if the participant throws this
     * @throws InternalSBBException
     *         if no message Handler could be resolved.
     * @throws InternalParticipantException
     */
    protected void invokeHandleMessageFromHandler(final InternalIncomingMessage inMsg) throws InternalParticipantException,
            InternalSBBException {
        // this is going to return a proxys
        IncomingMessageHandlerProxy handlerProxy = this.resolveMessageHandler();
        if (handlerProxy == null)
            throw new InternalTemporaryOutOfOrderException("No message handler found to handle incoming message for operation "
                    + this.getExchange().getOperation().toString());

        AbstractOperation theOp = handlerProxy.getOperation();
        if (theOp != null) {
            theOp.checkUnsupportedMustUnderstandHeaders((IncomingMessageBase) inMsg);
        }
        // classloader switch is done in the handlerProxy class
        handlerProxy.handleMessage(inMsg);

    }

    /**
     * Checks if is fault exchange.
     * 
     * @return true if the current exchange indicates a fault message
     */
    protected boolean isFaultExchange() {
        return (this.getExchange().getFault() != null);
    }

    /**
     * sends the MEP back to the initiator setting an exception as the cause for the error.
     * 
     * @param theError
     *        the error condition met
     */
    protected void refuseExchangeWithError(final Exception theError) {
        this.getDeliveryChannelSender().refuseExchangeWithError(this.getExchange(), theError);
    }

    /**
     * resolves the handler that is responsible to handle this message. It is either the message
     * handler that is set through the PAPI-Impl using setIncomingMessageHandler, or it is resolved
     * using the Handler registry. <br>
     * 
     * @return -- an incoming message messageHandler for this exchange which might be null
     */
    protected IncomingMessageHandlerProxy resolveMessageHandler() {
        if (this.messageHandler == null) {
            this.messageHandler =
                    this.getKernel().getHandlerRegistry().getHandler(this.getRole(), this.getExchange().getService(),
                            this.getExchange().getOperation().getLocalPart());
        }
        return this.messageHandler;
    }

    /**
     * Send management notification.
     * 
     * @param type
     *        the type
     * @param state
     *        the state
     */
    protected void sendManagementNotification(final EventType type, final ExchangeState state) {
        CoreMessageProcessingNotification notification = new CoreMessageProcessingNotification();
        // notification.setReporter(this);
        notification.setEventType(type);
        CallContextExtension callCtx = HeaderUtil.getCallContextExtension(this.getExchange());
        String correlationID = callCtx.getCorrelationID();
        notification.setCorrelationID(correlationID);
        String messageID = callCtx.getMessageID();
        notification.setMessageID(messageID);
        notification.setExchangePattern(this.getExchangePattern());
        notification.setInteractionStyle(this.getInteractionStyle());
        notification.setExchangeState(state);
        notification.setOperationName(this.getOperationName());
        notification.setParticipantRole(this.getParticipantRole());
        notification.setParticipantIdentity(this.getKernel().getParticipant());
        notification.setServiceName(callCtx.getServiceName());
        notification.setTimestamp(System.currentTimeMillis());
        // added by GPR to include policy ids in tracking metadata notification
        AgreedPolicy agreedPolicy = callCtx.getPolicy();
        notification.setConsumerPolicyID(agreedPolicy.getConsumerPolicyIdentity().getKeyName());
        notification.setProviderPolicyID(agreedPolicy.getProviderPolicyIdentity().getKeyName());
        this.getKernel().getManagementNotificationListener().sendNotification(notification);
    }

    /**
     * Send management notification.
     * 
     * @param state
     *        the state
     */
    protected void sendManagementNotification(final ExchangeState state) {
        CoreMessageProcessingNotification notification = new CoreMessageProcessingNotification();
        // notification.setReporter(this);
        CallContextExtension callCtx = HeaderUtil.getCallContextExtension(this.getExchange());
        String correlationID = callCtx.getCorrelationID();
        notification.setCorrelationID(correlationID);
        notification.setParticipantRole(this.getParticipantRole());
        notification.setExchangeState(state);
        notification.setTimestamp(System.currentTimeMillis());
        // added by GPR to include policy ids in tracking metadata notification
        AgreedPolicy agreedPolicy = callCtx.getPolicy();
        notification.setConsumerPolicyID(agreedPolicy.getConsumerPolicyIdentity().getKeyName());
        notification.setProviderPolicyID(agreedPolicy.getProviderPolicyIdentity().getKeyName());
        this.getKernel().getManagementNotificationListener().sendNotification(notification);
    }

    /**
     * This is just a helper method which decides wether to refuse an exchange because the
     * participant code will not be able to handle it.
     * 
     * @return true if the exchange was refused becasuse of a missing message handler
     */
    private boolean refuseExchangeOnMissingMessageHandler() {
        if (this.resolveMessageHandler() == null) {
            // FIXME this will cause advancing problems on incoming
            // exchanges.
            // Test them
            this.refuseExchangeWithError(new InternalMessageNotAcceptedException(
                    "Provider has no handler registered for the operation " + this.getExchange().getOperation()));
            return true;
        } else
            return false;
    }

}
