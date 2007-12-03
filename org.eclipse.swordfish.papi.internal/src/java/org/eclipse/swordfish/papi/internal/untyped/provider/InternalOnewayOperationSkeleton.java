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

import java.util.Collection;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;

/**
 * 
 * This interface provides the concrete <code>InternalOperation</code> skeleton to be used by a
 * provider to handle Oneway messages.
 * 
 */
public interface InternalOnewayOperationSkeleton extends InternalOperation {

    /**
     * This method returns a collection with the names of all defined callback operations. If no
     * associated callback operation is defined, the resulting collection is empty.
     * 
     * <p>
     * Note:
     * <ul>
     * <li>This method will never return null.</li>
     * <li>A name can for example be used to get the associated
     * <code>InternalOnewayOperationSkeleton</code> with
     * {@link org.eclipse.swordfish.papi.untyped.provider.InternalServiceSkeleton#getOnewayOperation(String)}.
     * </li>
     * </ul>
     * </p>
     * 
     * @return A collection of strings containing the names of callback operations associated with
     *         this operation. The collection might be empty if there is no callback operation.
     */
    Collection/* <String> */getCallbackOperationNames();

    /**
     * This method returns the previously registered incoming message handler for this operation.
     * 
     * @return The previously registered message handler or <code>null</code> if no message
     *         handler was registered.
     */
    InternalIncomingMessageHandler getMessageHandler();

    /**
     * This method checks whether there are any service callback operations defined, which the
     * provider can invoke to react on calls to this operation.
     * 
     * @return True if any callback operations are defined, false otherwise.
     */
    boolean hasCallbackOperations();

    /**
     * This method checks whether a message handler is registered for this particular
     * <code>InternalOnewayOperationSkeleton</code>.
     * 
     * @return Whether a message handler is registered.
     */
    boolean hasMessageHandler();

    /**
     * This method indicates if this operation skeleton is a callback operation that is retrieved
     * through a callback service.
     * 
     * <p>
     * Callback operation proxies always need a call context that determines the reply direction of
     * a previously incoming message through a Oneway call. Therefore, if this method returns
     * <code>true</code> all invocations on <code>callNonBlocking</code> to this operation will
     * fail with a <code>ServiceInvocationException</code>.
     * 
     * @return True if this operation is a <code>InternalOnewayOperationSkeleton</code> which was
     *         retrieved through a callback service, <code>false</code> if this
     *         <code>InternalOnewayOperationSkeleton</code> was retrieved from a regular
     *         <code>InternalServiceSkeleton</code> through
     *         {@link InternalServiceSkeleton#getOnewayOperation(String)}
     */
    boolean isCallbackOperation();

    /**
     * This method allows to register an <code>InternalIncomingMessageHandler</code> according to
     * this operation.
     * 
     * This method registers a message handler for incoming Oneway messages for the operation
     * encapsulated by this <code>InternalOnewayOperationSkeleton</code>.<br>
     * 
     * Note: Message delivery may start during execution of this method and will happen in
     * concurrent threads. There must not be more than one MessageHandler registered.
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
     * <code>InternalOnewayOperationSkeleton</code>.
     * 
     * <p>
     * Nothing will happen, if no message handler is registered. A flag is returned, indicating
     * whether a message handler was deregistered.
     * </p>
     * 
     * @return Whether a message handler has been unregistered.
     * @throws InternalSBBException
     */
    boolean releaseMessageHandler() throws InternalSBBException;
}
