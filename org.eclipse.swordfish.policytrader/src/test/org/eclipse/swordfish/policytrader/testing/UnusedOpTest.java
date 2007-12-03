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
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;

/**
 * Test for correct dealing with unused operations.
 */
public class UnusedOpTest extends TestBase {

    /**
     * Constructor.
     * 
     * @param name
     *        test name
     */
    public UnusedOpTest(final String name) {
        super(name);
    }

    public void testUnusedBoth() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestUnusedOpCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestUnusedOpPP_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"removeOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        // no unused operations anymore in agreed policies - gpr 2007-01-11
        this.expectedPT.put("removeOp", UNUSED);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        if (result == ParticipantPolicy.FAILED_AGREEMENT_POLICY) {
            System.out.println("*** Failed Agreement ***");
            throw new RuntimeException("unexpected");
        } else {
            this.printOut(result);
            // no unused operations anymore in agreed policies - gpr 2007-01-11
            assertTrue(null == result.getOperationPolicy("removeOp"));
            // assertSame(result.getOperationPolicy("removeOp"), AgreedPolicy.EMPTY_POLICY);
            assertNotSame(result.getOperationPolicy(DEFAULT_OP), AgreedPolicy.EMPTY_POLICY);
        }
    }

    public void testUnusedConsumer() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestUnusedOpCP_2.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestUnusedOpPP_2.ppdx");
        this.policyResolver.setOperationNames(new String[] {"removeOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("removeOp", UNUSED);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        if (result == ParticipantPolicy.FAILED_AGREEMENT_POLICY) {
            System.out.println("*** Failed Agreement ***");
            throw new RuntimeException("unexpected");
        } else {
            this.printOut(result);
            // no unused operations anymore in agreed policies - gpr 2007-01-11
            assertTrue(null == result.getOperationPolicy("removeOp"));
            // assertSame(result.getOperationPolicy("removeOp"),AgreedPolicy.EMPTY_POLICY);
            assertNotSame(result.getOperationPolicy(DEFAULT_OP), AgreedPolicy.EMPTY_POLICY);
        }
    }

    public void testUnusedConsumerB() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestUnusedOpCP_3.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestUnusedOpPP_3.ppdx");
        this.policyResolver.setOperationNames(new String[] {"removeOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("removeOp", UNUSED);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        if (result == ParticipantPolicy.FAILED_AGREEMENT_POLICY) {
            System.out.println("*** Failed Agreement ***");
            throw new RuntimeException("unexpected");
        } else {
            this.printOut(result);
            // no unused operations anymore in agreed policies - gpr 2007-01-11
            assertTrue(null == result.getOperationPolicy("removeOp"));
            // assertSame(result.getOperationPolicy("removeOp"),
            // AgreedPolicy.EMPTY_POLICY);
            assertNotSame(result.getOperationPolicy(DEFAULT_OP), AgreedPolicy.EMPTY_POLICY);
        }
    }

    public void testUnusedProvider() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestUnusedOpCP_4.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestUnusedOpPP_4.ppdx");
        this.policyResolver.setOperationNames(new String[] {"removeOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("removeOp", FAILURE);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        if (result == ParticipantPolicy.FAILED_AGREEMENT_POLICY) {
            System.out.println("*** Failed Agreement ***");
            System.out.println("... as expected");
        } else
            throw new RuntimeException("unexpected");
    }

    public void testUnusedProviderB() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestUnusedOpCP_5.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestUnusedOpPP_5.ppdx");
        this.policyResolver.setOperationNames(new String[] {"removeOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("removeOp", FAILURE);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        if (result == ParticipantPolicy.FAILED_AGREEMENT_POLICY) {
            System.out.println("*** Failed Agreement ***");
            System.out.println("... as expected");
        } else
            throw new RuntimeException("unexpected");
    }

    public void testUnusedProviderC() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestUnusedOpCP_6.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestUnusedOpPP_5.ppdx");
        this.policyResolver.setOperationNames(new String[] {"removeOp", "unmentionedOp"});
        this.expectedPT = new HashMap();
        this.expectedPT.put("unmentionedOp", SUCCESS);
        this.expectedPT.put("removeOp", UNUSED);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        Policy operationPolicy = result.getOperationPolicy("removeOp");
        assertTrue(null == operationPolicy);
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
