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
import org.eclipse.swordfish.policytrader.impl.StandardOperationPolicyIdentity;

/**
 * Test for correct policy caching.
 */
public class CacheTest extends TestBase {

    private static final StandardOperationPolicyIdentity CONSUMER_OPI =
            new StandardOperationPolicyIdentity("http://policies.test.org/operation/TestService/consumer/secureInsertOp");

    private static final StandardOperationPolicyIdentity CONSUMER_OPI_A =
            new StandardOperationPolicyIdentity("http://policies.test.org/operation/TestService/consumer/secureInsertOpA");

    private static final StandardOperationPolicyIdentity PROVIDER_OPI =
            new StandardOperationPolicyIdentity("http://policies.test.org/operation/TestService/provider/secureInsertOp");

    private static final StandardOperationPolicyIdentity PROVIDER_OPI_B =
            new StandardOperationPolicyIdentity("http://policies.test.org/operation/TestService/provider/secureInsertOpB");

    /**
     * Constructor.
     * 
     * @param name
     *        test name
     */
    public CacheTest(final String name) {
        super(name);
    }

    public void testInnerCaching() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestCacheCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestCachePP_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"op1", "op2", "op3", "op4"});
        this.expectedPR = new HashMap();
        this.expectedPR.put(consumerPolicyID, cnt(1));
        this.expectedPR.put(providerPolicyID, cnt(1));
        this.expectedPR.put(CONSUMER_OPI, cnt(1));
        this.expectedPR.put(CONSUMER_OPI_A, cnt(1));
        this.expectedPR.put(PROVIDER_OPI, cnt(1));
        this.expectedPR.put(PROVIDER_OPI_B, cnt(1));
        this.expectedPT = new HashMap();
        this.expectedPT.put("op1", SUCCESS);
        this.expectedPT.put("op2", SUCCESS);
        this.expectedPT.put("op3", SUCCESS);
        this.expectedPT.put("op4", SUCCESS);
        this.expectedPTCLocs = new HashMap();
        this.expectedPTCLocs.put("op1", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op2", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op3", "SecureInsertOpAConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op4", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTPLocs = new HashMap();
        this.expectedPTPLocs.put("op1", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op2", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op3", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op4", "SecureInsertOpBProviderPolicy_1.opdx");
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        assertNotSame(result, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(result);
        assertNotSame(result.getOperationPolicy("op1"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op2"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op3"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op4"), AgreedPolicy.EMPTY_POLICY);
        assertSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op2"));
        assertNotSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op3"));
        assertNotSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op4"));
        assertNotSame(result.getOperationPolicy("op3"), result.getOperationPolicy("op4"));
        final AgreedPolicy resultA = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Repeated Agreed Policy");
        assertNotSame(resultA, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(resultA);
        assertSame(result.getOperationPolicy("op1"), resultA.getOperationPolicy("op1"));
        assertSame(result.getOperationPolicy("op2"), resultA.getOperationPolicy("op2"));
        assertSame(result.getOperationPolicy("op3"), resultA.getOperationPolicy("op3"));
        assertSame(result.getOperationPolicy("op4"), resultA.getOperationPolicy("op4"));
        assertFalse(((CountDown) this.expectedPR.get(consumerPolicyID)).dec());
        assertFalse(((CountDown) this.expectedPR.get(providerPolicyID)).dec());
        assertFalse(((CountDown) this.expectedPR.get(CONSUMER_OPI)).dec());
        assertFalse(((CountDown) this.expectedPR.get(CONSUMER_OPI_A)).dec());
        assertFalse(((CountDown) this.expectedPR.get(PROVIDER_OPI)).dec());
        assertFalse(((CountDown) this.expectedPR.get(PROVIDER_OPI_B)).dec());
    }

    public void testInvalidatedCaching() throws Exception {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestCacheCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestCachePP_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"op1", "op2", "op3", "op4"});
        this.expectedPR = new HashMap();
        this.expectedPR.put(consumerPolicyID, cnt(2));
        this.expectedPR.put(providerPolicyID, cnt(2));
        this.expectedPR.put(CONSUMER_OPI, cnt(2));
        this.expectedPR.put(CONSUMER_OPI_A, cnt(2));
        this.expectedPR.put(PROVIDER_OPI, cnt(2));
        this.expectedPR.put(PROVIDER_OPI_B, cnt(2));
        this.expectedPT = new HashMap();
        this.expectedPT.put("op1", SUCCESS);
        this.expectedPT.put("op2", SUCCESS);
        this.expectedPT.put("op3", SUCCESS);
        this.expectedPT.put("op4", SUCCESS);
        this.expectedPTCLocs = new HashMap();
        this.expectedPTCLocs.put("op1", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op2", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op3", "SecureInsertOpAConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op4", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTPLocs = new HashMap();
        this.expectedPTPLocs.put("op1", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op2", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op3", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op4", "SecureInsertOpBProviderPolicy_1.opdx");
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        assertNotSame(result, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(result);
        assertNotSame(result.getOperationPolicy("op1"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op2"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op3"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op4"), AgreedPolicy.EMPTY_POLICY);
        assertSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op2"));
        assertNotSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op3"));
        assertNotSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op4"));
        assertNotSame(result.getOperationPolicy("op3"), result.getOperationPolicy("op4"));
        this.policyTrader.invalidateAll();
        final AgreedPolicy resultA = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Repeated Agreed Policy");
        assertNotSame(resultA, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(resultA);
        assertNotSame(result.getOperationPolicy("op1"), resultA.getOperationPolicy("op1"));
        assertNotSame(result.getOperationPolicy("op2"), resultA.getOperationPolicy("op2"));
        assertNotSame(result.getOperationPolicy("op3"), resultA.getOperationPolicy("op3"));
        assertNotSame(result.getOperationPolicy("op4"), resultA.getOperationPolicy("op4"));
        assertNotSame(resultA.getOperationPolicy("op1"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op2"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op3"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op4"), AgreedPolicy.EMPTY_POLICY);
        assertSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op2"));
        assertNotSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op3"));
        assertNotSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op4"));
        assertNotSame(resultA.getOperationPolicy("op3"), resultA.getOperationPolicy("op4"));
        assertFalse(((CountDown) this.expectedPR.get(consumerPolicyID)).dec());
        assertFalse(((CountDown) this.expectedPR.get(providerPolicyID)).dec());
        assertFalse(((CountDown) this.expectedPR.get(CONSUMER_OPI)).dec());
        assertFalse(((CountDown) this.expectedPR.get(CONSUMER_OPI_A)).dec());
        assertFalse(((CountDown) this.expectedPR.get(PROVIDER_OPI)).dec());
        assertFalse(((CountDown) this.expectedPR.get(PROVIDER_OPI_B)).dec());
    }

    public void testInvalidatedCachingA() throws Exception {
        ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestCacheCP_1.ppdx");
        ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestCachePP_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"op1", "op2", "op3", "op4"});
        this.expectedPR = new HashMap();
        this.expectedPR.put(consumerPolicyID, cnt(2));
        this.expectedPR.put(providerPolicyID, cnt(2));
        this.expectedPR.put(CONSUMER_OPI, cnt(2));
        this.expectedPR.put(CONSUMER_OPI_A, cnt(2));
        this.expectedPR.put(PROVIDER_OPI, cnt(2));
        this.expectedPR.put(PROVIDER_OPI_B, cnt(2));
        this.expectedPT = new HashMap();
        this.expectedPT.put("op1", SUCCESS);
        this.expectedPT.put("op2", SUCCESS);
        this.expectedPT.put("op3", SUCCESS);
        this.expectedPT.put("op4", SUCCESS);
        this.expectedPTCLocs = new HashMap();
        this.expectedPTCLocs.put("op1", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op2", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op3", "SecureInsertOpAConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op4", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTPLocs = new HashMap();
        this.expectedPTPLocs.put("op1", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op2", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op3", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op4", "SecureInsertOpBProviderPolicy_1.opdx");
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        assertNotSame(result, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(result);
        assertNotSame(result.getOperationPolicy("op1"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op2"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op3"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op4"), AgreedPolicy.EMPTY_POLICY);
        assertSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op2"));
        assertNotSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op3"));
        assertNotSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op4"));
        assertNotSame(result.getOperationPolicy("op3"), result.getOperationPolicy("op4"));
        consumerPolicyID = this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestCacheCP_2.ppdx");
        this.expectedPTCLocs.put("op1", "SecureInsertOpConsumerPolicy_2.opdx");
        this.expectedPTCLocs.put("op2", "SecureInsertOpConsumerPolicy_2.opdx");
        this.expectedPTCLocs.put("op4", "SecureInsertOpConsumerPolicy_2.opdx");
        this.policyTrader.invalidateAll();
        final AgreedPolicy resultA = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Repeated Agreed Policy");
        assertNotSame(resultA, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(resultA);
        assertNotSame(result.getOperationPolicy("op1"), resultA.getOperationPolicy("op1"));
        assertNotSame(result.getOperationPolicy("op2"), resultA.getOperationPolicy("op2"));
        assertNotSame(result.getOperationPolicy("op3"), resultA.getOperationPolicy("op3"));
        assertNotSame(result.getOperationPolicy("op4"), resultA.getOperationPolicy("op4"));
        assertNotSame(resultA.getOperationPolicy("op1"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op2"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op3"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op4"), AgreedPolicy.EMPTY_POLICY);
        assertSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op2"));
        assertNotSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op3"));
        assertNotSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op4"));
        assertNotSame(resultA.getOperationPolicy("op3"), resultA.getOperationPolicy("op4"));
        assertFalse(((CountDown) this.expectedPR.get(consumerPolicyID)).dec());
        assertFalse(((CountDown) this.expectedPR.get(providerPolicyID)).dec());
        assertFalse(((CountDown) this.expectedPR.get(CONSUMER_OPI)).dec());
        assertFalse(((CountDown) this.expectedPR.get(CONSUMER_OPI_A)).dec());
        assertFalse(((CountDown) this.expectedPR.get(PROVIDER_OPI)).dec());
        assertFalse(((CountDown) this.expectedPR.get(PROVIDER_OPI_B)).dec());
    }

    public void testInvalidatedCachingB() throws Exception {
        ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestCacheCP_1.ppdx");
        ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestCachePP_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"op1", "op2", "op3", "op4"});
        this.expectedPR = new HashMap();
        this.expectedPR.put(consumerPolicyID, cnt(2));
        this.expectedPR.put(providerPolicyID, cnt(2));
        this.expectedPR.put(CONSUMER_OPI, cnt(2));
        this.expectedPR.put(CONSUMER_OPI_A, cnt(2));
        this.expectedPR.put(PROVIDER_OPI, cnt(2));
        this.expectedPR.put(PROVIDER_OPI_B, cnt(2));
        this.expectedPT = new HashMap();
        this.expectedPT.put("op1", SUCCESS);
        this.expectedPT.put("op2", SUCCESS);
        this.expectedPT.put("op3", SUCCESS);
        this.expectedPT.put("op4", SUCCESS);
        this.expectedPTCLocs = new HashMap();
        this.expectedPTCLocs.put("op1", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op2", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op3", "SecureInsertOpAConsumerPolicy_1.opdx");
        this.expectedPTCLocs.put("op4", "SecureInsertOpConsumerPolicy_1.opdx");
        this.expectedPTPLocs = new HashMap();
        this.expectedPTPLocs.put("op1", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op2", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op3", "SecureInsertOpProviderPolicy_1.opdx");
        this.expectedPTPLocs.put("op4", "SecureInsertOpBProviderPolicy_1.opdx");
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        assertNotSame(result, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(result);
        assertNotSame(result.getOperationPolicy("op1"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op2"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op3"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(result.getOperationPolicy("op4"), AgreedPolicy.EMPTY_POLICY);
        assertSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op2"));
        assertNotSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op3"));
        assertNotSame(result.getOperationPolicy("op1"), result.getOperationPolicy("op4"));
        assertNotSame(result.getOperationPolicy("op3"), result.getOperationPolicy("op4"));
        providerPolicyID = this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestCachePP_2.ppdx");
        this.expectedPTPLocs.put("op1", "SecureInsertOpProviderPolicy_2.opdx");
        this.expectedPTPLocs.put("op2", "SecureInsertOpProviderPolicy_2.opdx");
        this.expectedPTPLocs.put("op3", "SecureInsertOpProviderPolicy_2.opdx");
        this.policyTrader.invalidateAll();
        final AgreedPolicy resultA = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Repeated Agreed Policy");
        assertNotSame(resultA, ParticipantPolicy.FAILED_AGREEMENT_POLICY);
        this.printOut(resultA);
        assertNotSame(result.getOperationPolicy("op1"), resultA.getOperationPolicy("op1"));
        assertNotSame(result.getOperationPolicy("op2"), resultA.getOperationPolicy("op2"));
        assertNotSame(result.getOperationPolicy("op3"), resultA.getOperationPolicy("op3"));
        assertNotSame(result.getOperationPolicy("op4"), resultA.getOperationPolicy("op4"));
        assertNotSame(resultA.getOperationPolicy("op1"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op2"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op3"), AgreedPolicy.EMPTY_POLICY);
        assertNotSame(resultA.getOperationPolicy("op4"), AgreedPolicy.EMPTY_POLICY);
        assertSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op2"));
        assertNotSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op3"));
        assertNotSame(resultA.getOperationPolicy("op1"), resultA.getOperationPolicy("op4"));
        assertNotSame(resultA.getOperationPolicy("op3"), resultA.getOperationPolicy("op4"));
        assertFalse(((CountDown) this.expectedPR.get(consumerPolicyID)).dec());
        assertFalse(((CountDown) this.expectedPR.get(providerPolicyID)).dec());
        assertFalse(((CountDown) this.expectedPR.get(CONSUMER_OPI)).dec());
        assertFalse(((CountDown) this.expectedPR.get(CONSUMER_OPI_A)).dec());
        assertFalse(((CountDown) this.expectedPR.get(PROVIDER_OPI)).dec());
        assertFalse(((CountDown) this.expectedPR.get(PROVIDER_OPI_B)).dec());
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
