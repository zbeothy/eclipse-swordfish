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
package org.eclipse.swordfish.policy.util;

import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * Test to verify the final selection of alternatives after matching is done Expected behaviour is
 * to have the cheapest alternative for each transport in the result policy.
 * 
 */
public class SelectionTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/selection";

    /**
     * Instantiates a new selection test.
     * 
     * @param name
     *        the name
     */
    public SelectionTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test authentication selection.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testAuthenticationSelection() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy allResults = this.getProcessor().matchAllResults(this.getConsumerPolicy(), this.getProviderPolicy());
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator it = new TermIterator(result, new PrimitiveAssertionSelector(AuthenticationProcessor.AUTHENTICATION_QNAME));
        while (it.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
            assertEquals("SAMLToken", assertion.getAttribute(AuthenticationProcessor.TYPE_ATTRIBUTE));
        }
        assertFalse(this.areAssertionsEqual(allResults, result));
    }

    /**
     * Test empty policy.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testEmptyPolicy() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy allResults = this.getProcessor().matchAllResults(this.getConsumerPolicy(), this.getProviderPolicy());
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        this.checkEquals(allResults, result);
    }

    /**
     * Test optional SDX validation.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalSDXValidation() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy allResults = this.getProcessor().matchAllResults(this.getConsumerPolicy(), this.getProviderPolicy());
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        this.checkEquals(allResults, result);
    }

    /**
     * Test two specific transports.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testTwoSpecificTransports() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy allResults = this.getProcessor().matchAllResults(this.getConsumerPolicy(), this.getProviderPolicy());
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        this.checkEquals(allResults, result);
    }

    /**
     * Test with dummy assertion.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testWithDummyAssertion() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy allResults = this.getProcessor().matchAllResults(this.getConsumerPolicy(), this.getProviderPolicy());
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ExactlyOne eo = (ExactlyOne) result.getTerms().get(0);
        assertEquals(2, eo.getTerms().size());
        // -TODO: test to see that each alternative has different transport
        // (verified manually)
        assertFalse(this.areAssertionsEqual(allResults, result));
    }

    /**
     * Check equals.
     * 
     * @param a
     *        the a
     * @param b
     *        the b
     */
    private void checkEquals(final Policy a, final Policy b) {
        if (!this.areAssertionsEqual(a, b)) {
            System.out.println("Policies do not match as expected");
            System.out.println(org.eclipse.swordfish.policy.util.Analyzer.dump(a));
            System.out.println(org.eclipse.swordfish.policy.util.Analyzer.dump(b) + "\n");
            fail("Unexpected mismatch of policies");
        }
    }

}
