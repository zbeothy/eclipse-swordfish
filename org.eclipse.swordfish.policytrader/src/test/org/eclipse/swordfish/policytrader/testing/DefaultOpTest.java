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
package org.eclipse.swordfish.policytrader.testing;

import java.util.HashMap;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;

/**
 * Test for correct policy caching.
 */
public class DefaultOpTest extends TestBase {

    /**
     * Constructor.
     * 
     * @param name
     *        test name
     */
    public DefaultOpTest(final String name) {
        super(name);
    }

    public void testBothSpecific() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("secureInsertOp", SUCCESS);
        this.expectedPTCLocs = new HashMap();
        this.expectedPTCLocs.put("unmentionedOp", "AnyOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("secureInsertOp", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTPLocs = new HashMap();
        this.expectedPTPLocs.put("unmentionedOp", "AnyOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("secureInsertOp", "SecureInsertOpProviderPolicy_1.opdx");
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        assertNotSame(result, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(result);
        assertNotSame(result.getOperationPolicy("unmentionedOp"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("secureInsertOp"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("unmentionedOp"), result.getOperationPolicy("secureInsertOp"));
    }

    public void testConsumerDefaultOp() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_2.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_2.ppdx");
        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("secureInsertOp", SUCCESS);
        this.expectedPTCLocs = new HashMap();
        this.expectedPTCLocs.put("unmentionedOp", "AnyOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("secureInsertOp", "AnyOpConsumerPolicy_1.opdx");
        this.expectedPTPLocs = new HashMap();
        this.expectedPTPLocs.put("unmentionedOp", "AnyOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("secureInsertOp", "SecureInsertOpProviderPolicy_1.opdx");
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        assertNotSame(result, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(result);
        assertNotSame(result.getOperationPolicy("unmentionedOp"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("secureInsertOp"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("unmentionedOp"), result.getOperationPolicy("secureInsertOp"));
    }

    public void testProviderDefaultOp() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_3.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_3.ppdx");
        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("secureInsertOp", SUCCESS);
        this.expectedPTCLocs = new HashMap();
        this.expectedPTCLocs.put("unmentionedOp", "AnyOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("secureInsertOp", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTPLocs = new HashMap();
        this.expectedPTPLocs.put("unmentionedOp", "AnyOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("secureInsertOp", "AnyOpProviderPolicy_1.opdx");
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        assertNotSame(result, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(result);
        assertNotSame(result.getOperationPolicy("unmentionedOp"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("secureInsertOp"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("unmentionedOp"), result.getOperationPolicy("secureInsertOp"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
