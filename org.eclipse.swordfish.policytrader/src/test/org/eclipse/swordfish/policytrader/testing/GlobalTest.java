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
 * Test for correct dealing with unused operations.
 */
public class GlobalTest extends TestBase {

    /**
     * Constructor.
     * 
     * @param name
     *        test name
     */
    public GlobalTest(final String name) {
        super(name);
    }

    public void testGlobalA() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestConsumerPolicy_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestProviderPolicy_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "secureUpdateOp", "trackedOp", "fastOp", "removeOp",
                "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("secureInsertOp", SUCCESS);
        this.expectedPT.put("secureUpdateOp", SUCCESS);
        this.expectedPT.put("trackedOp", FAILURE);
        this.expectedPT.put("fastOp", SUCCESS);
        this.expectedPT.put("removeOp", UNUSED);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        if (result == ParticipantPolicy.FAILED_AGREEMENT_POLICY) {
            System.out.println("*** Failed Agreement ***");
            System.out.println("... as expected");
        } else
            throw new RuntimeException("unexpected");
    }

    public void testGlobalB() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestConsumerPolicy_2.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestProviderPolicy_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "secureUpdateOp", "trackedOp", "fastOp", "removeOp",
                "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("secureInsertOp", SUCCESS);
        this.expectedPT.put("secureUpdateOp", SUCCESS);
        this.expectedPT.put("trackedOp", SUCCESS);
        this.expectedPT.put("fastOp", SUCCESS);
        this.expectedPT.put("removeOp", UNUSED);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        if (result == ParticipantPolicy.FAILED_AGREEMENT_POLICY) {
            System.out.println("*** Failed Agreement ***");
            throw new RuntimeException("unexpected");
        } else {
            this.printOut(result);
            assertTrue(null == result.getOperationPolicy("removeOp"));
            assertNotSame(result.getOperationPolicy(DEFAULT_OP), AgreedPolicy.EMPTY_POLICY);
            assertNotSame(result.getOperationPolicy("secureInsertOp"), AgreedPolicy.EMPTY_POLICY);
            assertNotSame(result.getOperationPolicy("secureUpdateOp"), AgreedPolicy.EMPTY_POLICY);
            assertNotSame(result.getOperationPolicy("trackedOp"), AgreedPolicy.EMPTY_POLICY);
            assertNotSame(result.getOperationPolicy("fastOp"), AgreedPolicy.EMPTY_POLICY);
        }
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
