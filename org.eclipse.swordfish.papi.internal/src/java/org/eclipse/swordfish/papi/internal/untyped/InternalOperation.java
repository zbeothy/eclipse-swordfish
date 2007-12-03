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

import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.InternalEnvironment;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * This interface contains the common behavior of an operation as seen by both consuming and
 * providing applications.
 * 
 * This interface contains the common functionality of all types of operations. For a particular
 * operation the InternalSBB always provides a concrete subclass of this interface such as
 * <code>InternalOnewayOperationProxy</code> on the consumer side or
 * <code>InternalRequestResponseOperationSkeleton</code> on the provider side.
 * 
 */
public interface InternalOperation {

    /**
     * This method adds an authentication handler to this operation. It works like
     * <code>{@link org.eclipse.swordfish.papi.InternalSBB#addAuthenticationHandler(InternalAuthenticationHandler)}</code>.
     * 
     * <p>
     * Note: The authentication handlers registered for an operation instance are used for calls to
     * this operation. They overrule service and InternalSBB level authentication handlers and are
     * themselves overruled by message level authentication handlers.
     * </p>
     * 
     * @param anAuthenticationHandler
     *        The authentication handler to be added.
     * @throws InternalSBBException
     *         handler for the same authentication mechanism is already registered.
     * @see org.eclipse.swordfish.papi.InternalSBB#addAuthenticationHandler(InternalAuthenticationHandler)
     */
    void addAuthenticationHandler(InternalAuthenticationHandler anAuthenticationHandler) throws InternalSBBException;

    /**
     * This method returns an array of all previously registered authentication handlers for this
     * particular operation.
     * <p>
     * The array is empty (has a size of zero) if there are no authentication handlers registered.
     * </p>
     * Notes:
     * <ul>
     * <li>This method never returns null.</li>
     * <li>The method only returns the registered authentication handlers at the operation level,
     * means for example, not at the InternalSBB or service level.</li>
     * </ul>
     * 
     * @return This method returns an array of all authentication handlers registered for this
     *         operation.
     */
    InternalAuthenticationHandler[] getAuthenticationHandlers();

    /**
     * This method returns the communication style of this operation.
     * 
     * @return Communication style of this operation.
     */
    InternalCommunicationStyle getCommunicationStyle();

    /**
     * This method returns the environment in use for this InternalSBB instance.
     * 
     * @return Reference to environment.
     * @see org.eclipse.swordfish.papi.InternalEnvironment
     */
    InternalEnvironment getEnvironment();

    /**
     * This method returns the name of this operation.
     * 
     * @return Name of this operation.
     */
    String getName();

    /**
     * This method returns the service object this operation is bound to.
     * 
     * @return InternalService object this operation is bound to.
     */
    InternalService getService();

    /**
     * This method removes an authentication handler from this operation. It works like
     * <code>{@link org.eclipse.swordfish.papi.InternalSBB#removeAuthenticationHandler(InternalAuthenticationHandler)}</code>.
     * <p>
     * Note: This will never remove an authentication handler from other levels, for example, one
     * which was registered with the InternalSBB instance. After removal of an authentication
     * handler from this operation, the authentication handler of the service or the InternalSBB (if
     * any) will be used for further calls.
     * </p>
     * 
     * @param anAuthenticationHandler
     *        The authentication handler to be removed.
     * @see org.eclipse.swordfish.papi.InternalSBB#removeAuthenticationHandler(InternalAuthenticationHandler)
     */
    void removeAuthenticationHandler(InternalAuthenticationHandler anAuthenticationHandler) throws InternalSBBException;
}
