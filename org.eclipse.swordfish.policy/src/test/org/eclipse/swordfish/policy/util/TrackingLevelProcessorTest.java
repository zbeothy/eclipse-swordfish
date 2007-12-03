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

import java.io.InputStream;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.selector.ClassSelector;

/**
 * The Class TrackingLevelProcessorTest.
 */
public class TrackingLevelProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/trackinglevel";

    /** The Constant TRACKING_LEVEL_QNAME. */
    private static final QName TRACKING_LEVEL_QNAME = TrackingLevelProcessor.TRACKING_LEVEL_QNAME;

    /** The Constant VALUE_ATTRIBUTE. */
    private static final QName VALUE_ATTRIBUTE = TrackingLevelProcessor.VALUE_ATTRIBUTE;

    /**
     * Instantiates a new tracking level processor test.
     * 
     * @param name
     *        the name
     */
    public TrackingLevelProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test all levels.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testAllLevels() throws UnexpectedPolicyProcessingException {
        String providerLocation = this.getDataLocation() + "/provider_" + "6" + ".xml";
        InputStream is = this.getClass().getResourceAsStream(providerLocation);
        this.setProviderPolicy(this.getReader().readPolicy(is));
        this.testSingleLevel("1", "none");
        this.testSingleLevel("2", "summary");
        this.testSingleLevel("3", "operation");
        this.testSingleLevel("4", "trace");
        this.testSingleLevel("5", "detail");
    }

    /**
     * Test capability both.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testCapabilityBoth() throws UnexpectedPolicyProcessingException {
        this.readData("10");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertEquals(0, this.numAssertionsOfType(result, TRACKING_LEVEL_QNAME));
    }

    /**
     * Test capability only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testCapabilityOnly() throws UnexpectedPolicyProcessingException {
        this.readData("9");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertEquals(0, this.numAssertionsOfType(result, TRACKING_LEVEL_QNAME));
    }

    /**
     * Test match capability.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchCapability() throws UnexpectedPolicyProcessingException {
        this.readData("7");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        String expected = "trace";
        this.checkResult(result, expected);
    }

    /**
     * Test match empty range.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchEmptyRange() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        String expected = "operation";
        this.checkResult(result, expected);
    }

    /**
     * Test match single range.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchSingleRange() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        String expected = "trace";
        this.checkResult(result, expected);
    }

    /**
     * Test match value empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchValueEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        String expected = "summary";
        this.checkResult(result, expected);
    }

    /**
     * Test no match capability.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchCapability() throws UnexpectedPolicyProcessingException {
        this.readData("8");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test no match range range.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchRangeRange() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test no match range single.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchRangeSingle() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessorTest#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * checks if exactly the assertions listed in expected are included in result.
     * 
     * @param result
     *        the result
     * @param expected
     *        ArrayList of destination/rule concatenation
     * 
     * @throws RuntimeException
     *         if error in assertion list
     */
    private void checkResult(final Assertion result, final String expected) {
        ExactlyOne eo = (ExactlyOne) result.getTerms().get(0);
        TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
        All all = (All) iterAll.next();
        assertFalse(iterAll.hasNext());
        TermIterator iterPrimitives = new TermIterator(all, new PrimitiveAssertionSelector(TRACKING_LEVEL_QNAME));
        PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
        assertFalse(iterPrimitives.hasNext());
        String value = assertion.getAttribute(VALUE_ATTRIBUTE);
        if (!expected.equals(value)) {
            fail("Result " + value + " not expected");
        }
    }

    /**
     * Test single level.
     * 
     * @param id
     *        the id
     * @param expected
     *        the expected
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private void testSingleLevel(final String id, final String expected) throws UnexpectedPolicyProcessingException {
        String consumerLocation = this.getDataLocation() + "/consumer_6_" + id + ".xml";
        InputStream is = this.getClass().getResourceAsStream(consumerLocation);
        this.setConsumerPolicy(this.getReader().readPolicy(is));
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        this.checkResult(result, expected);
    }

}
