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

import javax.xml.namespace.QName;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;

/**
 * The Interface PolicyValidator.
 */
public interface PolicyValidator {

    /**
     * Validate.
     * 
     * @param operationPolicy
     *        the operation policy
     * @param operation
     *        the operation
     * @param providerPolicyIdentity
     *        the provider policy identity
     * @param providerId
     *        the provider id
     * @param serviceName
     *        the service name
     * 
     * @throws PolicyViolatedException
     */
    void validate(Policy operationPolicy, String operation, ParticipantPolicyIdentity providerPolicyIdentity, QName providerId,
            QName serviceName) throws PolicyViolatedException;

}
