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
package org.eclipse.swordfish.policytrader.callback;

import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;

/**
 * Listener to be implemented by a client of the PolicyTrader in order to receive detail information
 * about an ongoing policy trading.
 */
public interface PolicyTradingListener {

    /**
     * Agreement failed at operation.
     * 
     * @param operationName
     *        the operation name
     * @param consumerOpPolicyID
     *        the consumer op policy ID
     * @param providerOpPolicyID
     *        the provider op policy ID
     */
    void agreementFailedAtOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
            final OperationPolicyIdentity providerOpPolicyID);

    /**
     * Agreement succeeded at operation.
     * 
     * @param operationName
     *        the operation name
     * @param consumerOpPolicyID
     *        the consumer op policy ID
     * @param providerOpPolicyID
     *        the provider op policy ID
     */
    void agreementSucceededAtOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
            final OperationPolicyIdentity providerOpPolicyID);

    /**
     * Agreement unused operation.
     * 
     * @param operationName
     *        the operation name
     * @param consumerOpPolicyID
     *        the consumer op policy ID
     * @param providerOpPolicyID
     *        the provider op policy ID
     */
    void agreementUnusedOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
            final OperationPolicyIdentity providerOpPolicyID);
}
