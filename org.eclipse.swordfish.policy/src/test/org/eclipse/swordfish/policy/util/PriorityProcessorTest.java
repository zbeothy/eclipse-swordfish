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

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.selector.ClassSelector;
import org.eclipse.swordfish.policy.selector.Selector;

/**
 * The Class PriorityProcessorTest.
 */
public class PriorityProcessorTest extends AssertionProcessorTest {

    /** The Constant MIN_ATTRIBUTE. */
    public static final QName MIN_ATTRIBUTE = new QName("min");

    /** The Constant MAX_ATTRIBUTE. */
    public static final QName MAX_ATTRIBUTE = new QName("max");

    /** The Constant VALUE_ATTRIBUTE. */
    public static final QName VALUE_ATTRIBUTE = new QName("value");

    /** The Constant SUPPORT_ATTRIBUTE. */
    public static final QName SUPPORT_ATTRIBUTE = new QName("support");

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/priority";

    /** The Constant PRIORITY_QNAME. */
    private final static QName PRIORITY_QNAME = PriorityProcessor.PRIORITY_QNAME;

    /** The Constant selector. */
    private static final Selector SELECTOR = new PrimitiveAssertionSelector(PRIORITY_QNAME);

    /**
     * Instantiates a new priority processor test.
     * 
     * @param name
     *        the name
     */
    public PriorityProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test high range empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testHighRangeEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("7");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("6");
        this.checkResult(result, expected);
    }

    /**
     * Test low range empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testLowRangeEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("6");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("4");
        this.checkResult(result, expected);
    }

    /**
     * Test mid range empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMidRangeEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("5");
        this.checkResult(result, expected);
    }

    /**
     * Test no unsupported.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoUnsupported() throws UnexpectedPolicyProcessingException {
        this.readData("12");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iter = new TermIterator(result, SELECTOR, true);
        assertTrue(!iter.hasNext());
        assertTrue(!this.isNullPolicy(result));
    }

    /**
     * Test no value.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoValue() throws UnexpectedPolicyProcessingException {
        this.readData("13");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iter = new TermIterator(result, SELECTOR, true);
        assertTrue(!iter.hasNext());
        assertTrue(!this.isNullPolicy(result));
    }

    /**
     * Test range range no match.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRangeRangeNoMatch() throws UnexpectedPolicyProcessingException {
        this.readData("8");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test range range overlap higher.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRangeRangeOverlapHigher() throws UnexpectedPolicyProcessingException {
        this.readData("9");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("6");
        this.checkResult(result, expected);
    }

    /**
     * Test range range overlap lower.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRangeRangeOverlapLower() throws UnexpectedPolicyProcessingException {
        this.readData("10");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("4");
        this.checkResult(result, expected);
    }

    /**
     * Test range range overlap middle.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRangeRangeOverlapMiddle() throws UnexpectedPolicyProcessingException {
        this.readData("11");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("5");
        this.checkResult(result, expected);
    }

    /**
     * Test single default support.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleDefaultSupport() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("6");
        this.checkResult(result, expected);
    }

    /**
     * Test single empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("6");
        this.checkResult(result, expected);
    }

    /**
     * Test single no support.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleNoSupport() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test single support.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleSupport() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("6");
        this.checkResult(result, expected);
    }

    /**
     * Test somok.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSomok() throws UnexpectedPolicyProcessingException {
        this.readData("somok");
        this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
    }

    /**
     * checks if exactly the assertions listed in expected are included in result
     * 
     * TODO: generalize.
     * 
     * @param result
     *        the result
     * @param expected
     *        ArrayList of destination/rule concatenation
     * 
     * @throws RuntimeException
     *         if error in assertion list
     */
    private void checkResult(final Assertion result, final ArrayList expected) {
        ExactlyOne eo = (ExactlyOne) result.getTerms().get(0);
        TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
        All all = (All) iterAll.next();
        assertFalse(iterAll.hasNext());
        TermIterator iterPrimitives = new TermIterator(all, SELECTOR);
        while (iterPrimitives.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
            String value = assertion.getAttribute(VALUE_ATTRIBUTE);
            if (!expected.contains(value)) {
                fail("Encountered unexpected value " + value);
            } else {
                expected.remove(value);
            }
        }
        if (!expected.isEmpty()) {
            StringBuffer msg = new StringBuffer("Expected results not found:\n");
            Iterator it = expected.iterator();
            while (it.hasNext()) {
                msg.append(it.next().toString()).append("\n");
            }
            fail(msg.toString());
        }
    }

}
