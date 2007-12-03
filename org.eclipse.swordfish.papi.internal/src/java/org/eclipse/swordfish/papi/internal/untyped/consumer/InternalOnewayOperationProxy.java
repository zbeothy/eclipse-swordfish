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

import java.util.Collection;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;

/**
 * This interface implements the concrete <code>InternalOperation</code> to be used by a consumer
 * for calls with the Oneway communication style.
 * 
 */
public interface InternalOnewayOperationProxy extends InternalOperation {

    /**
     * This methods executes a <b>non blocking oneway callback</b> by sending a message which is
     * based on the <code>InternalCallContext</code> that was passed in. This method is invokeable
     * only if <code>{@link #isCallbackOperation()}</code> returns <code>true</code>.
     * <p>
     * The resulting context will contain a Oneway relation to the messageID of aContextToAnswer.
     * </p>
     * <p>
     * This is not meant to be used in chained calls but for sending callbacks.
     * </p>
     * 
     * @param aMessage
     *        message to be sent.
     * @param aContextToAnswer
     *        passed InternalCallContext to answer.
     * @return InternalCallContext of the message, which was sent.
     * 
     * @throws InternalSBBException
     */
    InternalCallContext callbackNonBlocking(InternalOutgoingMessage aMessage, InternalCallContext aContextToAnswer)
            throws InternalSBBException;

    /**
     * This methods executes a <b>non blocking oneway call</b> by sending a message (to a
     * provider). This method can be invoked only if <code>{@link #isCallbackOperation()}</code>
     * returns <code>false</code>.
     * 
     * @param aMessage
     *        message to be send.
     * @param aConsumerCallIdentifier
     *        call identifier of the message to be sent, may be <code>null</code> if not required.
     * @return InternalCallContext of the message that was sent.
     * 
     * @throws InternalSBBException
     */
    InternalCallContext callNonBlocking(InternalOutgoingMessage aMessage, String aConsumerCallIdentifier)
            throws InternalSBBException;

    /**
     * This methods executes a <b>non blocking oneway call</b> by sending a message (to a
     * provider). This method is invokeable only if <code>{@link #isCallbackOperation()}</code>
     * returns <code>false</code>. The resulting InternalCallContext will be related to the
     * contextToRelateTo as "TriggeringCall".
     * 
     * @param aMessage
     *        message to be sent.
     * @param aConsumerCallIdentifier
     *        call identifier of the message to be sent, may be <code>null</code> if not required.
     * @param aContextToRelateTo
     *        passed InternalCallContext to relate to. The resulting context will contain a relation
     *        of type "TriggeringCall" to this context's messageID
     * @return InternalCallContext of the message, which was sent.
     * 
     * @throws InternalSBBException
     */
    InternalCallContext callNonBlocking(InternalOutgoingMessage aMessage, String aConsumerCallIdentifier,
            InternalCallContext aContextToRelateTo) throws InternalSBBException;

    /**
     * This method returns a collection with names of all defined callback operations. If no
     * associated callback operation is defined, the resulting collection is empty.
     * 
     * <p>
     * Notes:
     * <ul>
     * <li>This method will never return null.</li>
     * <li>A name can, for example, be used to get the associated
     * <code>InternalOnewayOperationProxy</code> with
     * {@link org.eclipse.swordfish.papi.untyped.consumer.InternalServiceProxy#getOnewayOperation(String)}.
     * </li>
     * </ul>
     * </p>
     * 
     * @return A collection of strings containing the names of callback operations associated with
     *         this operation. The collection might be empty if there is no callback operation.
     */
    Collection/* <String> */getCallbackOperationNames();

    /**
     * This method checks whether there are any service callback operations defined which the
     * provider can invoke to react to calls of this operation.
     * 
     * @return True if any callback operations are defined, false otherwise.
     */
    boolean hasCallbackOperations();

    /**
     * This method indicates if this operation proxy is a callback operation that is retrived
     * through a callback service.
     * 
     * <p>
     * Callback operation proxies always need a call context that determines the reply direction of
     * a previously incoming message through a Oneway call. Therefore, if this method returns
     * <code>true</code> all invocations on
     * <code>{@link #callNonBlocking(InternalOutgoingMessage, String)}</code> and
     * <code>{@link #callNonBlocking(InternalOutgoingMessage, String, InternalCallContext)}</code>
     * to this operation will fail with a <code>ServiceInvocationException</code>. You must use
     * <code>{@link #callbackNonBlocking(InternalOutgoingMessage, InternalCallContext)}</code> to
     * invoke this operation.
     * 
     * @return True if this operation is a <code>InternalOnewayOperationProxy</code>, which was
     *         retrieved through a callback service, <code>false</code> if this
     *         <code>InternalOnewayOperationProxy</code> was retrieved from a "regular"
     *         <code>InternalServiceProxy</code> through <code>lookupServiceProxy</code>.
     */
    boolean isCallbackOperation();
}
