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

/**
 * The Class SdxValidationProcessorTest.
 */
public class SdxValidationProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/sdxvalidation";

    /** The Constant CUSTOM_VALIDATION_QNAME. */
    private static final QName CUSTOM_VALIDATION_QNAME = SdxValidationProcessor.SDX_VALIDATION_QNAME;

    /** The Constant MESSAGE_ATTRIBUTE. */
    private static final QName MESSAGE_ATTRIBUTE = SdxValidationProcessor.MESSAGE_ATTRIBUTE;

    /** The Constant LOCATION_ATTRIBUTE. */
    private static final QName LOCATION_ATTRIBUTE = SdxValidationProcessor.LOCATION_ATTRIBUTE;

    /**
     * Instantiates a new sdx validation processor test.
     * 
     * @param name
     *        the name
     */
    public SdxValidationProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test optional both mandatory single.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalBothMandatorySingle() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("request/consumer");
        this.checkResult(result, expected);
    }

    /**
     * Test optional only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalOnly() throws UnexpectedPolicyProcessingException {
        this.readData("6");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(0);
        this.checkResult(result, expected);
        assertFalse(this.isNullPolicy(result));
    }

    /**
     * Test request consumer response provider.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestConsumerResponseProvider() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("request/consumer");
        expected.add("response/provider");
        this.checkResult(result, expected);
    }

    /**
     * Test request response on consumer.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestResponseOnConsumer() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("request/consumer");
        expected.add("response/consumer");
        this.checkResult(result, expected);
    }

    /**
     * Test request response sender.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestResponseSender() throws UnexpectedPolicyProcessingException {
        ArrayList expected = new ArrayList(2);
        expected.add("request/consumer");
        expected.add("response/provider");
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        this.checkResult(result, (ArrayList) expected.clone());
        this.readData("4a");
        result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        this.checkResult(result, (ArrayList) expected.clone());
        this.readData("4b");
        result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        this.checkResult(result, (ArrayList) expected.clone());
    }

    /**
     * Test response consumer.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testResponseConsumer() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("response/consumer");
        this.checkResult(result, expected);
    }

    /**
     * checks if exactly the assertions listed in expected are included in result.
     * 
     * @param result
     *        the result
     * @param expected
     *        ArrayList of destination/schema concatenation
     * 
     * @throws RuntimeException
     *         if error in assertion list
     */
    private void checkResult(final Assertion result, final ArrayList expected) {
        ExactlyOne eo = (ExactlyOne) result.getTerms().get(0);
        TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
        All all = (All) iterAll.next();
        assertFalse(iterAll.hasNext());
        TermIterator iterPrimitives = new TermIterator(all, new PrimitiveAssertionSelector(CUSTOM_VALIDATION_QNAME));
        while (iterPrimitives.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
            String value = assertion.getAttribute(MESSAGE_ATTRIBUTE) + "/" + assertion.getAttribute(LOCATION_ATTRIBUTE);
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
