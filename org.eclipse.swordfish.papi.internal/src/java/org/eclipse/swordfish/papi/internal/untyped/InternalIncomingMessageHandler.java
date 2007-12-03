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
package org.eclipse.swordfish.papi.internal.untyped;

import org.eclipse.swordfish.papi.internal.exception.InternalParticipantException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * This interface provides a handler for incoming messages. It is symmetric for the consumer and the
 * provider side.
 * 
 * This interface has to be implemented by a business application (be it a consumer or a provider)
 * to receive messages.
 * 
 * Incoming message handlers are registered and released using the registerMessageHandler and
 * releaseMessageHandler methods in the operation proxy and skeleton interfaces.
 * 
 * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#registerMessageHandler(InternalIncomingMessageHandler)
 * @see org.eclipse.swordfish.papi.untyped.consumer.InternalRequestResponseOperationProxy#releaseMessageHandler()
 * @see org.eclipse.swordfish.papi.untyped.consumer.InternalNotificationOperationProxy#registerMessageHandler(InternalIncomingMessageHandler)
 * @see org.eclipse.swordfish.papi.untyped.consumer.InternalNotificationOperationProxy#releaseMessageHandler()
 * @see org.eclipse.swordfish.papi.untyped.provider.InternalRequestResponseOperationSkeleton#registerMessageHandler(InternalIncomingMessageHandler)
 * @see org.eclipse.swordfish.papi.untyped.provider.InternalRequestResponseOperationSkeleton#releaseMessageHandler()
 * @see org.eclipse.swordfish.papi.untyped.provider.InternalOnewayOperationSkeleton#registerMessageHandler(InternalIncomingMessageHandler)
 * @see org.eclipse.swordfish.papi.untyped.provider.InternalOnewayOperationSkeleton#releaseMessageHandler()
 * 
 */
public interface InternalIncomingMessageHandler {

    /**
     * This method handles an incoming error.
     * 
     * <p>
     * On the consumer side this happens if a non blocking request could not be fullfilled due to
     * whatever reasons.
     * </p>
     * 
     * <p>
     * On the provider side, this indicates an incoming message which could not be processed
     * completely by the InternalSBB. So this can be in the context of an incoming request for a
     * Oneway or for a request-response operation. It will be called if there is any error in the
     * InternalSBB internal processing of the message before it is ready to be delivered to the
     * provider, for example, if validation of the incoming message fails.
     * </p>
     * 
     * <p>
     * This method informs a participant that an inbound message not delivered because of some
     * errors (passed as an <code>InvocationException</code>). There can't be any "business
     * processing" because the payload of the message is not accessible. But the participant might
     * log this event or inform an operator. If the method is invoked on the provider side then the
     * provider will not be able to respond to the incoming message, because this is already handled
     * by InternalSBB.
     * </p>
     * 
     * @param aCall
     *        the inbound <code>InternalCallContext</code>.
     * 
     * @param sbbException
     * 
     * @throws InternalParticipantException
     *         if participant is not able to process this error. This exception will always cause
     *         the call to be discarded.
     */
    void handleError(InternalSBBException sbbException, InternalCallContext aCall) throws InternalParticipantException;

    /**
     * This method handles an incoming message.
     * 
     * @param aMessage ,
     *        the incoming message. The call context of the <code>aMessage</code> is accessed
     *        using {@link InternalIncomingMessage#getCallContext()}.
     * 
     * @throws InternalParticipantException
     *         can be thrown by the application to indicate a technical error during message
     *         handling.
     */
    void handleMessage(InternalIncomingMessage aMessage) throws InternalParticipantException;

    /**
     * This method is called by the InternalSBB once this particular message handler is about to be
     * de-registered.
     * 
     * <p>
     * It gives the application code the opportunity to clean up resources.
     * </p>
     * 
     * @param sbbInitiated
     *        is true if InternalSBB is about to de-register this handler (for example at shutdown
     *        time) and false if the initiating de-registration was done through the participant
     *        application (removeHandler or releaseService).
     * 
     */
    void onRelease(boolean sbbInitiated);

}
