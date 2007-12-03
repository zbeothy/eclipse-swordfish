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

import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalServiceProxy;

/**
 * <b>General service skeleton for a provider</b><br>
 * 
 * <p>
 * This interface provides the general service skeleton a provider can use to access concrete
 * operations. It is thus derived from the generic service base class and represents a service from
 * the provider's perspective.
 * </p>
 * 
 * <p>
 * Note: All methods can (theoretically) throw an <code>BadOperationNameException</code>. In
 * practice this should not occur within business applications, because it is a runtime exception,
 * which reflects InternalSBB internal issues.
 * </p>
 * 
 */
public interface InternalServiceSkeleton extends InternalService {

    /**
     * This method returns the callback service proxy for this service skeleton.
     * <p>
     * The callback proxy defines operations to send a reaction on calls to those operations of this
     * service which are correlated to another operation in a callback service.
     * </p>
     * <p>
     * Note: Getting the callback service is a "uni-directional" operation. Thus, if this skeleton
     * was originally obtained from a proxy by method <code>getCallbackServiceSkeleton()</code>
     * this method will <b>not</b> return the originating proxy, but <code>null</code> instead.
     * </p>
     * <p>
     * Returns <code>null</code> if no callback service is defined.
     * </p>
     * 
     * @return The proxy of the callback service or <code>null</code>.
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalServiceProxy#getCallbackServiceSkeleton()
     */
    InternalServiceProxy getCallbackServiceProxy() throws InternalSBBException;

    /**
     * This method returns the operation for the given name.
     * <p>
     * If called multiple times with equal parameter this method will return the same (identical)
     * object.
     * </p>
     * 
     * @param aOperationName
     *        name of the requested operation
     * @return an operation for the given name
     * 
     * Only throws RuntimeException of type {@link BadOperationNameException} if the operation with
     * the handed over name or requested type, is not part of the signature of this service.
     * 
     */
    InternalNotificationOperationSkeleton getNotificationOperation(String aOperationName) throws InternalSBBException;

    /**
     * This method returns the operation for the given name.
     * <p>
     * If called multiple times with equal parameter this method will return the same (identical)
     * object.
     * </p>
     * 
     * @param aOperationName
     *        name of the requested operation
     * @return an operation for the given name
     * 
     * Only throws RuntimeException of type {@link BadOperationNameException} if the operation with
     * the handed over name or requested type, is not part of the signature of this service.
     * 
     */
    InternalOnewayOperationSkeleton getOnewayOperation(String aOperationName) throws InternalSBBException;

    /**
     * This method returns the operation for the given name.
     * <p>
     * If called multiple times with equal parameter this method will return the same (identical)
     * object.
     * </p>
     * 
     * @param aOperationName
     *        name of the requested operation
     * @return an operation for the given name
     * 
     * Only throws RuntimeException of type {@link BadOperationNameException} if the operation with
     * the handed over name or requested type, is not part of the signature of this service.
     * 
     */
    InternalRequestResponseOperationSkeleton getRequestResponseOperation(String aOperationName) throws InternalSBBException;
}
