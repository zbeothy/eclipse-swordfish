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
package org.eclipse.swordfish.core.components.processing;

import javax.jbi.messaging.MessageExchange;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * This is interface defines Policy router.
 * 
 */
public interface PolicyRouter {

    /** the role of this component. */
    String ROLE = PolicyRouter.class.getName();

    /**
     * This method processes faults on the base of the context information.
     * 
     * @param context
     *        the processing context.
     * @param role
     *        the caller role - initiator/servicer
     * @param agreedPolicy
     *        the agreed policy to process.
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    void handleFault(MessageExchange context, Role role, AgreedPolicy agreedPolicy) throws InternalSBBException,
            PolicyViolatedException;

    /**
     * This method processes requests on the base of the context information.
     * 
     * @param context
     *        the processing context.
     * @param role
     *        the caller role - initiator/servicer
     * @param agreedPolicy
     *        the agreed policy to process.
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    void handleRequest(MessageExchange context, Role role, AgreedPolicy agreedPolicy) throws InternalSBBException,
            PolicyViolatedException;

    /**
     * This method processes responses on the base of the context information.
     * 
     * @param context
     *        the processing context.
     * @param role
     *        the caller role - initiator/servicer
     * @param agreedPolicy
     *        the agreed policy to process.
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    void handleResponse(MessageExchange context, Role role, AgreedPolicy agreedPolicy) throws InternalSBBException,
            PolicyViolatedException;

}
