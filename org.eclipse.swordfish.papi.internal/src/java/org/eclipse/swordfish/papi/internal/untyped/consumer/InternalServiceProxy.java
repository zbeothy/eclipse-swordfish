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

import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalServiceSkeleton;

/**
 * <b>Concrete service proxy for a consumer</b><br>
 * 
 * This interface provides a concrete service proxy for use by a consumer and represents the
 * consumer's perspective on a service to be called.
 * 
 * Note: All methods can throw a runtime exception <code>BadOperationNameException</code> (this
 * indicates an internal issue).
 * 
 * 
 */
public interface InternalServiceProxy extends InternalService {

    /**
     * <p>
     * This method returns the callback service skeleton for this service proxy, if one exists. The
     * callback skeleton defines operations to get a reaction on calls to operations that are
     * correlated to an operation in another service (the "callback service").
     * </p>
     * <p>
     * The callback service skeleton is used to register handlers to receive callbacks.
     * </p>
     * 
     * <p>
     * Note: Getting the callback service is a "uni-directional" operation. Thus, if this proxy was
     * originally obtained from a skeleton by method <code>getCallbackServiceProxy()</code> this
     * method will not return the originating skeleton, but <code>null</code> instead.
     * </p>
     * 
     * @return The skeleton of the callback service or <code>null</code>.
     * @throws InternalInfrastructureException
     *         if an unknown MEP is used.
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalServiceSkeleton#getCallbackServiceProxy()
     */
    InternalServiceSkeleton getCallbackServiceSkeleton() throws InternalSBBException;

    /**
     * <b>Get a concrete Notification operation proxy</b><br>
     * 
     * This method returns the Notification operation proxy for the given name.
     * 
     * @param aOperationName
     *        name of the requested operation.
     * @return An operation for the given name.
     * 
     * Only throws runtime exceptions {@link BadOperationNameException}
     */
    InternalNotificationOperationProxy getNotificationOperation(String aOperationName) throws InternalSBBException;

    /**
     * <b>Get a concrete Oneway operation proxy</b><br>
     * 
     * This method returns the Oneway operation proxy for the given name.
     * 
     * @param aOperationName
     *        name of the requested operation.
     * @return An operation for the given name.
     * 
     * Only throws runtime exceptions {@link BadOperationNameException}
     */
    InternalOnewayOperationProxy getOnewayOperation(String aOperationName) throws InternalSBBException;

    /**
     * <b>Get a concrete Request-Response operation proxy</b><br>
     * 
     * This method returns the Request-Response operation proxy for the given name.
     * 
     * @param aOperationName
     *        name of the requested operation.
     * @return An operation for the given name.
     * 
     * Only throws runtime exceptions {@link BadOperationNameException}
     */
    InternalRequestResponseOperationProxy getRequestResponseOperation(String aOperationName) throws InternalSBBException;
}
