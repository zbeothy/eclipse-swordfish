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

import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;

/**
 * This interface provides the concrete <code>InternalOperation</code> to be used by a consumer
 * for incoming messages with the Notification communication style.
 * 
 */
public interface InternalNotificationOperationProxy extends InternalOperation {

    /**
     * This method returns the previously registered <code>InternalIncomingMessageHandler</code>
     * for this operation.
     * 
     * @return The previously registered message handler or null if no message handler was
     *         registered
     */
    InternalIncomingMessageHandler getMessageHandler();

    /**
     * This method querys whether a message handler is registered for the
     * <code>InternalNotificationOperationProxy</code>.
     * 
     * @return Whether a message handler is registered.
     */
    boolean hasMessageHandler();

    /**
     * This method registers a <b><code>MessageHandler</code> for incoming messages</b>
     * according to this <code>InternalNotificationOperationProxy</code>.
     * 
     * It will enable the consumer to receive Notification messages published by the provider.
     * 
     * <p>
     * Note: Message delivery may start during execution of this method and will occur in concurrent
     * threads. There must not be more than one <code>MessageHandler</code> registered.
     * </p>
     * 
     * @param aMessageHandler
     *        handler for incoming messages.
     * @throws InternalSBBException
     *         error occurs within the InternalSBB core infrastructure
     * @throws InternalSBBException
     */
    void registerMessageHandler(InternalIncomingMessageHandler aMessageHandler) throws InternalSBBException;

    /**
     * This method unregisters the message handler for this particular
     * <code>InternalNotificationOperationProxy</code>.
     * 
     * Nothing will happen, if no message handler is registered.
     * 
     * A flag is returned, indicating whether a message handler was deregistered
     * 
     * @return Whether a message handler has been unregistered.
     * @throws InternalSBBException
     */
    boolean releaseMessageHandler() throws InternalSBBException;
}
