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
package org.eclipse.swordfish.core.components.processing.impl;

import javax.jbi.messaging.MessageExchange;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.processing.PolicyRouter;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * The Class MockPolicyRouterBean.
 */
public class MockPolicyRouterBean implements PolicyRouter {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.PolicyRouter#handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.policytrader.AgreedPolicy)
     */
    public void handleFault(final MessageExchange context, final Role role, final AgreedPolicy agreedPolicy)
            throws InternalSBBException, PolicyViolatedException {
        // Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.PolicyRouter#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.policytrader.AgreedPolicy)
     */
    public void handleRequest(final MessageExchange context, final Role role, final AgreedPolicy agreedPolicy)
            throws InternalSBBException, PolicyViolatedException {
        // Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.PolicyRouter#handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.policytrader.AgreedPolicy)
     */
    public void handleResponse(final MessageExchange context, final Role role, final AgreedPolicy agreedPolicy)
            throws InternalSBBException, PolicyViolatedException {
        // Auto-generated method stub

    }

}
