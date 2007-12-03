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

import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * The Class MaxResponseTimeTest.
 */
public class MaxResponseTimeTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/maxresponse";

    /**
     * Instantiates a new max response time test.
     * 
     * @param name
     *        the name
     */
    public MaxResponseTimeTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test match different location.
     * 
     * @throws Exception
     */
    public void testMatchDifferentLocation() throws Exception {
        this.readData("1_3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator it =
                new TermIterator(result, new PrimitiveAssertionSelector(MaxResponseTimeProcessor.MAXRESPONSETIME_ASSERTION), true);
        PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
        assertEquals(MaxResponseTimeProcessor.MAXRESPONSETIME_ASSERTION, assertion.getName());
        assertEquals("4000", assertion.getAttribute(MaxResponseTimeProcessor.MAXRESPONSETIME_VALUE_ATTRIBUTE));
        assertEquals("consumer", assertion.getAttribute(MaxResponseTimeProcessor.MAXRESPONSETIME_LOCATION_ATTRIBUTE));
        assertFalse(it.hasNext());
    }

    /**
     * Test match empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertFalse(this.isNullPolicy(result));
    }

    /**
     * Test match provider only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchProviderOnly() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertFalse(this.isNullPolicy(result));
    }

    /**
     * Test match same location.
     * 
     * @throws Exception
     */
    public void testMatchSameLocation() throws Exception {
        this.readData("1_1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator it =
                new TermIterator(result, new PrimitiveAssertionSelector(MaxResponseTimeProcessor.MAXRESPONSETIME_ASSERTION), true);
        PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
        assertEquals(MaxResponseTimeProcessor.MAXRESPONSETIME_ASSERTION, assertion.getName());
        assertEquals("4000", assertion.getAttribute(MaxResponseTimeProcessor.MAXRESPONSETIME_VALUE_ATTRIBUTE));
        assertEquals("consumer", assertion.getAttribute(MaxResponseTimeProcessor.MAXRESPONSETIME_LOCATION_ATTRIBUTE));
        assertFalse(it.hasNext());
    }

    /**
     * Test no match consumer only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchConsumerOnly() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test no match different location.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchDifferentLocation() throws UnexpectedPolicyProcessingException {
        this.readData("1_4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator it =
                new TermIterator(result, new PrimitiveAssertionSelector(MaxResponseTimeProcessor.MAXRESPONSETIME_ASSERTION), true);
        assertFalse(it.hasNext());
    }

    /**
     * Test no match same location.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchSameLocation() throws UnexpectedPolicyProcessingException {
        this.readData("1_2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator it =
                new TermIterator(result, new PrimitiveAssertionSelector(MaxResponseTimeProcessor.MAXRESPONSETIME_ASSERTION), true);
        assertFalse(it.hasNext());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessorTest#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.setProcessor(new PolicyProcessor());
    }

}
