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
package org.eclipse.swordfish.policytrader.impl;

import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.policytrader.OperationPolicy;

/**
 * Operation policy object.
 */
public class OperationPolicyImpl implements OperationPolicy {

    /** Apache Neethi policy object. */
    private final Policy wsPolicy;

    /**
     * Constructor used by PolicyFactory.
     * 
     * @param wsPolicy
     *        underlying Neethi policy object
     */
    public OperationPolicyImpl(final Policy wsPolicy) {
        super();
        this.wsPolicy = wsPolicy;
    }

    /**
     * Get the underlying Neethi policy object.
     * 
     * @return Neethi policy object
     */
    public Policy getWsPolicy() {
        return this.wsPolicy;
    }
}
