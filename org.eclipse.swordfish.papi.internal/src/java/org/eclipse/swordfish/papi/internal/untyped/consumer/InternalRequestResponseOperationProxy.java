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
package org.eclipse.swordfish.papi.internal.untyped.consumer;

import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;

/**
 * This interface implements the concrete <code>InternalOperation</code> to be used by a consumer
 * for calls with the request-response communication style.
 * 
 */
public interface InternalRequestResponseOperationProxy extends InternalOperation {

    /**
     * This methods executes a <b>blocking request-response call</b> by sending a message (to a
     * service provider).
     * 
     * @param aMessage
     *        message to be send as request.
     * @return The incoming (response) message.
     * @throws ServiceInvocationException
     * @throws InternalInfrastructureException
     * @throws InternalAuthorizationException
     * @throws InternalAuthenticationException
     * @throws InvalidPayloadException
     * 
     * @throws InternalSBBException
     */
    InternalIncomingMessage callBlocking(InternalOutgoingMessage aMessage) throws InternalSBBException;

    /**
     * This methods executes a <b>blocking request-response call</b> by sending a message (to a
     * service provider); the call is being explicitly related to a previous call.
     * 
     * @param aMessage
     *        message to be send as request.
     * @param aContextToRelateTo
     *        passed InternalCallContext to relate to. The resulting context will contain a relation
     *        of type "TriggeringCall" to this contexts messageID.
     * @return The incoming (response) message.
     * @throws ServiceInvocationException
     * @throws InternalInfrastructureException
     * @throws InternalAuthorizationException
     * @throws InternalAuthenticationException
     * @throws InvalidPayloadException
     */
    InternalIncomingMessage callBlocking(InternalOutgoingMessage aMessage, InternalCallContext aContextToRelateTo)
            throws InternalSBBException;

    /**
     * This methods executes a <b>non blocking request-response call</b> by sending a message (to a
     * service provider).
     * 
     * <p>
     * It returns a <code>InternalCallContext</code> for potential use call chains. A
     * <code>ConsumerCallIdentifier</code> is passed in to allow for application-level tracking of
     * calls.
     * </p>
     * 
     * @param aMessage
     *        message to be send as request.
     * @param aConsumerCallIdentifier
     *        call identifier of the message to be sent, may be <code>null</code> if not required.
     * @return InternalCallContext of the message, which was sent.
     * @throws ServiceInvocationException
     * @throws InternalInfrastructureException
     * @throws InternalAuthorizationException
     * @throws InternalAuthenticationException
     * @throws InvalidPayloadException
     */
    InternalCallContext callNonBlocking(InternalOutgoingMessage aMessage, String aConsumerCallIdentifier)
            throws InternalSBBException;

    /**
     * This methods executes a <b>non blocking request-response call</b> by sending a message (to a
     * service provider); the call is being explicitly related to a previous call.
     * 
     * <p>
     * Its context is cloned from the <code>InternalCallContext</code> that was passed in. A
     * <code>ConsumerCallIdentifier</code> is passed in to allow for application-level tracking of
     * calls. It returns a <code>InternalCallContext</code> for potential use in subsequent calls,
     * enabling the application to create "call chains".
     * </p>
     * 
     * @param aMessage
     *        message to be send as request.
     * @param aConsumerCallIdentifier
     *        call identifier of the message to be sent, may be <code>null</code> if not required.
     * @param aContextToRelateTo
     *        passed InternalCallContext to relate to. The resulting context will contain a relation
     *        of type "TriggeringCall" to this contexts messageID.
     * @return InternalCallContext of the message, which was sent.
     * @throws ServiceInvocationException
     * @throws InternalInfrastructureException
     * @throws InternalAuthorizationException
     * @throws InternalAuthenticationException
     * @throws InvalidPayloadException
     */
    InternalCallContext callNonBlocking(InternalOutgoingMessage aMessage, String aConsumerCallIdentifier,
            InternalCallContext aContextToRelateTo) throws InternalSBBException;

    /**
     * This method returns the previously registered <code>InternalIncomingMessageHandler</code>
     * for this operation.
     * 
     * @return The previously registered message handler or null if no message handler was
     *         registered
     */
    InternalIncomingMessageHandler getMessageHandler();

    /**
     * This method checks whether a message handler is registered for this particular
     * <code>InternalRequestResponseOperationProxy</code>.
     * 
     * @return Whether a message handler is registered.
     */
    boolean hasMessageHandler();

    /**
     * This method registers a <b>message handler for incoming messages</b> according to this
     * <code>InternalRequestResponseOperationProxy</code>.<br>
     * 
     * For request-response operations this will enable the consumer to receive answers to non
     * blocking calls.
     * 
     * Note: Message delivery may start during execution of this method and will occur in concurrent
     * threads. There must not be more than one message handler registered.
     * <p>
     * 
     * @param aMessageHandler
     *        handler for incoming messages.
     * @throws MessageHandlerRegistrationException
     * @throws InternalInfrastructureException
     */
    void registerMessageHandler(InternalIncomingMessageHandler aMessageHandler) throws InternalSBBException;

    /**
     * This method unregisters the message handler for this particular
     * <code>InternalRequestResponseOperationProxy</code>.
     * 
     * <p>
     * Nothing will happen, if no message handler is registered. A flag is returned, indicating
     * whether a message handler was deregistered.
     * </p>
     * 
     * @throws InternalSBBException
     * 
     * @return Whether a message handler has been unregistered.
     */
    boolean releaseMessageHandler() throws InternalSBBException;

}
