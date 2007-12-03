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

import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.command.Command;
import org.eclipse.swordfish.core.components.command.CommandFactory;
import org.eclipse.swordfish.core.components.command.InOnlyCommand;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.iapi.Transport;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractService;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtensionFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallRelationImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.core.utils.ExchangeProperties;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalNotificationOperationSkeleton;

/**
 * Implementation of the notification operation.
 */
public class NotificationOperationSkeletonImpl extends AbstractOperation implements InternalNotificationOperationSkeleton {

    /**
     * The Constructor.
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
    public NotificationOperationSkeletonImpl(final OperationDescription desc, final SBBExtension sbb, final AbstractService parent,
            final AbstractService partnersWith) {
        super(desc, sbb, parent, partnersWith, true);

        SPDXPort[] ports = desc.getServiceDescription().getSupportedPorts(this.getName());
        for (int i = 0; i < ports.length; i++) {
            // FIXME: Bad encapsulation:
            // PAPI implementation should not make such assumption
            // about capabilities of underlying layers:
            // Either query or leave it to the object in charge
            final Transport tp = ports[i].getTransport();
            if (!(Transport.JMS.equals(tp)))
                throw new ComponentRuntimeException("Notification operation " + this.getName() + " is only supported on JMS");
        }
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
        return InternalCommunicationStyle.NOTIFICATION;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalNotificationOperationSkeleton#sendNotification(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage)
     */
    public InternalCallContext sendNotification(final InternalOutgoingMessage outmsg) throws InternalIllegalInputException,
            InternalAuthenticationException, InternalAuthorizationException, InternalInfrastructureException,
            InternalMessagingException {
        if (!(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not contructed using the InternalSBB message factory");
        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");
        if ((outmsg == null)) throw new IllegalArgumentException("the outgoing message must not be null");

        return this.sendNotificationInternal(outmsg, null);
    }

    /**
     * Send notification.
     * 
     * @param outmsg
     *        the outmsg
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
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalNotificationOperationSkeleton#
     *      sendNotification(org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage)
     */
    public InternalCallContext sendNotification(final InternalOutgoingMessage outmsg, final InternalCallContext relationCtx)
            throws InternalIllegalInputException, InternalAuthenticationException, InternalAuthorizationException,
            InternalInfrastructureException, InternalMessagingException {
        if (!(outmsg instanceof OutgoingMessageBase))
            throw new IllegalArgumentException("the outgoing message was not contructed using the InternalSBB message factory");
        if (((OutgoingMessageBase) outmsg).isFaultMessage())
            throw new IllegalArgumentException("the outgoing message must not be fault in this " + "sending direction");
        if (!(relationCtx instanceof CallContextImpl))
            throw new IllegalArgumentException("the context is not constructed through InternalSBB");

        if ((outmsg == null)) throw new IllegalArgumentException("the outgoing message must not be null");

        if ((relationCtx == null)) throw new IllegalArgumentException("the context must not be null");

        return this.sendNotificationInternal(outmsg, relationCtx);
    }

    /**
     * Prepare request command.
     * 
     * @param exchange
     *        the exchange
     * 
     * @return the in only command
     * 
     * @throws InternalInfrastructureException
     */
    private InOnlyCommand prepareRequestCommand(final InOnly exchange) throws InternalInfrastructureException {
        CommandFactory commandFactory = this.getKernel().getCommandFactory();
        InOnlyCommand command = (InOnlyCommand) commandFactory.createCommand(this.getOperationDescription());
        if (command == null) throw new InternalInfrastructureException("failed to create a notification interaction");
        command.setExchange(exchange);
        command.setRole(Role.SENDER);
        command.setScope(Scope.REQUEST);
        return command;
    }

    /**
     * returns a message exchange that contains all headers and the message body.
     * 
     * @param msgBase
     *        the message to be set into the message exchange
     * @param ctx
     *        the ctx
     * 
     * @return an inOnly exchange that is ready for submission
     * 
     * @throws InvalidPayloadException
     * @throws InternalInfrastructureException
     */
    private InOnly prepareRequestExchange(final OutgoingMessageBase msgBase, final CallContextExtension ctx)
            throws InternalIllegalInputException, InternalInfrastructureException {
        InOnly exchange;
        NormalizedMessage inNM = null;
        try {
            exchange = (InOnly) this.createMessageExchange(ctx, msgBase);
            inNM = exchange.createMessage();
        } catch (MessagingException e) {
            throw new InternalInfrastructureException("missed to create a message notification " + this.getName());
        }
        try {
            msgBase.fillMessage(inNM, ctx);
            exchange.setInMessage(inNM);
        } catch (javax.jbi.messaging.MessagingException e) {
            throw new InternalIllegalInputException(e);
        }
        return exchange;
    }

    // internal implementation
    /**
     * Send notification internal.
     * 
     * @param outmsg
     *        the outmsg
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
    private InternalCallContext sendNotificationInternal(final InternalOutgoingMessage outmsg, final InternalCallContext relationCtx)
            throws InternalIllegalInputException, InternalAuthenticationException, InternalAuthorizationException,
            InternalInfrastructureException, InternalMessagingException {
        long timer = System.currentTimeMillis();

        if (!this.isSupportedOperation())
            throw new InternalMessagingException(this.getName() + " is indicated by the agreed policy "
                    + "to be an invalid operation (not present in agreed policy or set to unused)");

        Kernel kernel = this.getKernel();
        if ((kernel == null) || !kernel.isActive())
            throw new InternalInfrastructureException("this InternalSBB instance has been already released");

        OutgoingMessageBase msgBase = (OutgoingMessageBase) outmsg;
        // start preparing the return value.

        // check header validity
        this.checkUnsupportedMustUnderstandHeaders(msgBase);

        CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();
        ctx.setScope(Scope.REQUEST);
        ctx.setPolicy(this.getAgreedPolicy());
        ctx.setProviderPolicyID(this.getAgreedPolicy().getProviderPolicyIdentity().getKeyName());

        // create the command
        InOnly exchange = this.prepareRequestExchange(msgBase, ctx);
        if (relationCtx != null) {
            // this is the corelation case
            ctx.appendRelations(relationCtx.getRelations());
            ctx.pushRelation(new CallRelationImpl(InternalCallRelation.TYPE_TRIGGERING_CALL, relationCtx.getMessageID()));
        }
        HeaderUtil.setCallContextExtension(exchange, ctx);
        // let us use the inonly command also for notifications ... igiiiit
        Command command = this.prepareRequestCommand(exchange);
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

            if (th instanceof InternalAuthenticationException) throw (InternalAuthenticationException) th;
            if (th instanceof InternalAuthorizationException) throw (InternalAuthorizationException) th;
            if (th instanceof PolicyViolatedException) throw new InternalMessagingException(th);
            if (th instanceof InternalInfrastructureException) throw (InternalInfrastructureException) th;
            throw new InternalInfrastructureException("sending enumeration of " + this.getName() + " failed because of ", th);
        }

        CallContextExtension returnCtx = (CallContextExtension) exchange.getProperty(ExchangeProperties.CALL_CONTEXT);
        return returnCtx;
    }
}
