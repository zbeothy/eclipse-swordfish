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
 * The Class SubscriptionProcessorTest.
 */
public class SubscriptionProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/subscription";

    /** The Constant SUBSCRIPTION_QNAME. */
    private final static QName SUBSCRIPTION_QNAME = SubscriptionProcessor.SUBSCRIPTION_QNAME;

    /** The Constant TYPE_ATTRIBUTE. */
    private final static QName TYPE_ATTRIBUTE = SubscriptionProcessor.TYPE_ATTRIBUTE;

    /** The Constant selector. */
    private static final Selector SELECTOR = new PrimitiveAssertionSelector(SUBSCRIPTION_QNAME);

    /**
     * Instantiates a new subscription processor test.
     * 
     * @param name
     *        the name
     */
    public SubscriptionProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test empty any.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testEmptyAny() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iter = new TermIterator(result, SELECTOR, true);
        assertTrue(!iter.hasNext());
    }

    /**
     * Test spec empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSpecEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("non-durable");
        this.checkResult(result, expected);
    }

    /**
     * Test spec match.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSpecMatch() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("non-durable");
        this.checkResult(result, expected);
    }

    /**
     * Test spec no match.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSpecNoMatch() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
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
            String type = assertion.getAttribute(TYPE_ATTRIBUTE);
            if (!expected.contains(type)) {
                fail("Encountered unexpected value " + type);
            } else {
                expected.remove(type);
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
