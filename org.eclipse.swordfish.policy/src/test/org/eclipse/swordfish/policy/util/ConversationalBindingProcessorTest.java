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
 * The Class ConversationalBindingProcessorTest.
 */
public class ConversationalBindingProcessorTest extends AssertionProcessorTest {

    /** The Constant SCOPE_ATTRIBUTE. */
    public static final QName SCOPE_ATTRIBUTE = ConversationalBindingProcessor.SCOPE_ATTRIBUTE;

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/conversationalBinding";

    /** The Constant CONVERSATIONAL_BINDING_QNAME. */
    private final static QName CONVERSATIONAL_BINDING_QNAME = ConversationalBindingProcessor.CONVERSATIONAL_BINDING_QNAME;

    /** The Constant selector. */
    private static final Selector SELECTOR = new PrimitiveAssertionSelector(CONVERSATIONAL_BINDING_QNAME);

    /**
     * Instantiates a new conversational binding processor test.
     * 
     * @param name
     *        the name
     */
    public ConversationalBindingProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test agreed scope.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testAgreedScope() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("agreed");
        this.checkResult(result, expected);
    }

    /**
     * Test default scope.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testDefaultScope() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add(null);
        this.checkResult(result, expected);
    }

    /**
     * Test empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iter = new TermIterator(result, SELECTOR);
        assertTrue(!iter.hasNext());
    }

    /**
     * Test provider scope.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testProviderScope() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("provider");
        this.checkResult(result, expected);
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
            String scope = assertion.getAttribute(SCOPE_ATTRIBUTE);
            if (!expected.contains(scope)) {
                fail("Encountered unexpected value " + scope);
            } else {
                expected.remove(scope);
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
