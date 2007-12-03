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

import java.util.Map;

/**
 * The Class TradingResult.
 */
public class TradingResult extends AbstractAgreedPolicy {

    /**
     * Internally used constructor for empty instance used as query key.
     * 
     * @param consumerPID
     *        consumer-side participant policy identifier
     * @param providerPID
     *        provider-side participant policy identifier
     * @param sid
     *        the sid
     */
    protected TradingResult(final StandardParticipantPolicyIdentity consumerPID,
            final StandardParticipantPolicyIdentity providerPID, final StandardServiceDescriptionIdentity sid) {
        super(consumerPID, providerPID, sid.getKeyName(), null, null);
    }

    /**
     * Internally used constructor for result of policy trading.
     * 
     * @param consumerPID
     *        consumer-side participant policy identifier
     * @param providerPID
     *        provider-side participant policy identifier
     * @param operationPolicies
     *        map of traded operation policies which has been set up completely
     * @param service
     *        the service
     * @param provider
     *        the provider
     */
    protected TradingResult(final StandardParticipantPolicyIdentity consumerPID,
            final StandardParticipantPolicyIdentity providerPID, final String service, final String provider,
            final Map operationPolicies) {
        super(consumerPID, providerPID, service, provider, operationPolicies);
    }

    /**
     * Copy constructor creating a read-only copy.
     * 
     * @param src
     *        from where to copy
     */
    protected TradingResult(final TradingResult src) {
        super(src);
    }

    /**
     * Overridden equality comparation. Two TradedPolicy objects are equal if consumer-side and
     * provider-side participant policy identifier are equal.
     * 
     * @param other
     *        the other TradedPolicy
     * 
     * @return equality
     */
    @Override
    public boolean equals(final Object other) {
        if ((null == other) || !(other instanceof TradingResult)) return false;
        final TradingResult o = (TradingResult) other;
        if (null == this.getConsumerPID()) {
            if (null != o.getConsumerPID()) return false;
        } else {
            if (!this.getConsumerPID().equals(o.getConsumerPID())) return false;
        }
        if (null == this.getProviderPID()) {
            if (null != o.getProviderPID()) return false;
        } else {
            if (!this.getProviderPID().equals(o.getProviderPID())) return false;
        }
        if (null == this.getService()) {
            if (null != o.getService()) return false;
        } else {
            if (!this.getService().equals(o.getService())) return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (null == this.getConsumerPID() ? 0 : this.getConsumerPID().hashCode())
                ^ (null == this.getProviderPID() ? 0 : this.getProviderPID().hashCode()
                        ^ (null == this.getService() ? 0 : this.getService().hashCode()));
    }

}
