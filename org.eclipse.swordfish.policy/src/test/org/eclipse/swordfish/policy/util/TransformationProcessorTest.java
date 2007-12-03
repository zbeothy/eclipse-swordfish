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
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.selector.ClassSelector;

/**
 * The Class TransformationProcessorTest.
 */
public class TransformationProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/transformation";

    /** The Constant TRANSFORMATION_QNAME. */
    private static final QName TRANSFORMATION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Transformation");

    /**
     * Instantiates a new transformation processor test.
     * 
     * @param name
     *        the name
     */
    public TransformationProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test multi consumer multi provider.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMultiConsumerMultiProvider() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(4);
        expected.add("consumer/consumerReq");
        expected.add("provider/providerReq");
        expected.add("consumer/consumerRes");
        expected.add("provider/providerRes");
        this.checkResult(result, expected);
    }

    /**
     * Test multi consumer single provider.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMultiConsumerSingleProvider() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(4);
        expected.add("consumer/consumerReq");
        expected.add("provider/providerRes");
        expected.add("consumer/consumerRes");
        this.checkResult(result, expected);
    }

    /**
     * Test single consumer.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleConsumer() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("consumer/migrationRequest.xslt");
        this.checkResult(result, expected);
    }

    /**
     * Test single consumer multi provider.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleConsumerMultiProvider() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(4);
        expected.add("consumer/consumerReq");
        expected.add("provider/providerReq");
        expected.add("provider/providerRes");
        this.checkResult(result, expected);
    }

    /**
     * Test single consumer provider.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleConsumerProvider() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("consumer/consumer");
        expected.add("provider/provider");
        this.checkResult(result, expected);
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
    private void checkResult(final Assertion result, final ArrayList expected) {
        ExactlyOne eo = (ExactlyOne) result.getTerms().get(0);
        TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
        All all = (All) iterAll.next();
        assertFalse(iterAll.hasNext());
        TermIterator iterPrimitives = new TermIterator(all, new PrimitiveAssertionSelector(TRANSFORMATION_QNAME));
        while (iterPrimitives.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
            String value = assertion.getAttribute(new QName("location")) + "/" + assertion.getAttribute(new QName("ruleId"));
            if (!expected.remove(value)) {
                fail("Result " + value + " not expected");
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
