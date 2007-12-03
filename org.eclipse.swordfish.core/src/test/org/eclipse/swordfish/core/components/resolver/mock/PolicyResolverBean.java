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
package org.eclipse.swordfish.core.components.resolver.mock;

import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.resolver.PolicyResolver;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;

/**
 * The Class PolicyResolverBean.
 */
public class PolicyResolverBean implements PolicyResolver {

    /** The Constant PARTICIPANT_POLICY_STRING. */
    static final String PARTICIPANT_POLICY_STRING =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ParticipantPolicy xmlns=\"http://www.servicebackbone.org/qos/ParticipantPolicy/1.0\"\n    xmlns:spol=\"http://www.servicebackbone.org/qos/SopPolicy/1.0\"\n    xmlns:sopa=\"http://www.servicebackbone.org/qos/SOPAssertions/1.0\"\n    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n    xsi:schemaLocation=\"http://www.servicebackbone.org/qos/ParticipantPolicy/1.0 ./ParticipantPolicy.xsd\"\n    serviceID=\"http://www.sbb.org/domain/service/1.0\"\n    participantPolicyID=\"urn://ConsumerPolicyId#version\"\n    version=\"23\">\n    \n    <Policies>\n        <spol:Policy id=\"id1\">\n            <spol:All>\n                <sopa:MaxResponseTime value=\"3000\"/>\n                <sopa:Compression/>\n            </spol:All>\n        </spol:Policy>\n        <spol:Policy id=\"id2\">\n            <spol:All>\n                <sopa:Compression message=\"request\" size=\"2000\"/>\n            </spol:All>\n        </spol:Policy>\n         <spol:Policy id=\"id3\">\n            <spol:All>\n                <sopa:validation/>\n            </spol:All>\n        </spol:Policy>\n    </Policies>\n    \n    <InternalOperations>\n        <InternalOperation name=\"opName1\">\n            <Capabilities ref=\"id1\"/>\n            <Capabilities ref=\"id3\"/>\n            <Requirements ref=\"id2\"/>\n        </InternalOperation>\n        <InternalOperation name=\"opName3\">\n            <Capabilities ref=\"id1\"/>\n        </InternalOperation>\n        <InternalOperation name=\"opName2\">\n            <unused/>\n        </InternalOperation>\n    </InternalOperations>\n    \n</ParticipantPolicy>";

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getAssignedPolicyIds(javax.xml.namespace.QName)
     */
    public List getAssignedPolicyIds(final QName providerId) throws BackendException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getDefaultConsumerPolicyID()
     */
    public String getDefaultConsumerPolicyID() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getDefaultPolicyID()
     */
    public String getDefaultPolicyID() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveOperationPolicy(org.eclipse.swordfish.policytrader.OperationPolicyIdentity)
     */
    public OperationPolicy resolveOperationPolicy(final OperationPolicyIdentity identity) throws BackendException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveParticipantPolicy(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity)
     */
    public ParticipantPolicy resolveParticipantPolicy(final ParticipantPolicyIdentity identity) throws BackendException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#resolvePolicyID(java.lang.String)
     */
    public String resolvePolicyID(final String policyName) {
        return "http://types.sopware.org/qos/ParticipantPolicy/1.1/DefaultConsumerPolicy";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveServiceDescriptor(org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity)
     */
    public ServiceDescriptor resolveServiceDescriptor(final ServiceDescriptionIdentity identity) throws BackendException {
        return null;
    }

}
