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
package org.eclipse.swordfish.papi.internal.untyped.provider;

import org.eclipse.swordfish.papi.internal.exception.InternalParticipantException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;

/**
 * This interface provides the concrete <code>InternalOperation</code> skeleton to be used by a
 * provider for responding to messages using the request-response communication style.
 * 
 */
public interface InternalRequestResponseOperationSkeleton extends InternalOperation {

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
     * <code>InternalRequestResponseOperationSkeleton</code>.
     * 
     * @return Whether a message handler is registered
     */
    boolean hasMessageHandler();

    /**
     * This method registers a <b>message handler for incoming messages</b> to the operation
     * encapsulated by this <code>InternalRequestResponseOperationSkeleton</code>.<br>
     * 
     * Note: Message delivery may start during execution of this method and will occur in concurrent
     * threads. There must not be more than one MessageHandler registered.
     * <p>
     * 
     * @param aMessageHandler
     *        the handler to handler incoming messages
     * 
     * @throws InternalSBBException
     */
    void registerMessageHandler(InternalIncomingMessageHandler aMessageHandler) throws InternalSBBException;

    /**
     * This method unregisters the message handler for this particular
     * <code>InternalRequestResponseOperationSkeleton</code>.
     * 
     * <p>
     * Nothing will happen, if no message handler is registered. A flag is returned, indicating
     * whether a message handler was deregistered.
     * </p>
     * 
     * @return Whether a message handler has been deregistered.
     * @throws InternalSBBException
     */
    boolean releaseMessageHandler() throws InternalSBBException;

    /**
     * <b>Sends a technical error</b> by delivering it to the requester.
     * 
     * @param anError
     *        the indicated error
     * @param aCall
     *        the call to which anError is the response
     * @return call context of the sent message
     * 
     * @throws InternalSBBException
     *         error occurs within the InternalSBB core infrastructure
     */
    InternalCallContext sendError(InternalParticipantException anError, InternalCallContext aCall) throws InternalSBBException;

    /**
     * <b>Sends a response message</b> to the requester.
     * 
     * Note: The <code>InternalCallContext</code> to send the response must be the same
     * <code>InternalCallContext</code> that was retrieved through the incoming message.
     * 
     * @param aMessage
     *        the response message
     * @param aCall
     *        the call for which the aMessage is the response
     * @return call context of the sent message
     * 
     * @throws InternalSBBException
     */
    InternalCallContext sendResponse(InternalOutgoingMessage aMessage, InternalCallContext aCall) throws InternalSBBException;
}
