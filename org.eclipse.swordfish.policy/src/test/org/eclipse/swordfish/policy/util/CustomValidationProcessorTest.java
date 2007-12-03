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
 * The Class CustomValidationProcessorTest.
 */
public class CustomValidationProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/customvalidation";

    /** The Constant CUSTOM_VALIDATION_QNAME. */
    private static final QName CUSTOM_VALIDATION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "CustomValidation");

    /**
     * Instantiates a new custom validation processor test.
     * 
     * @param name
     *        the name
     */
    public CustomValidationProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test consumer both.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testConsumerBoth() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("consumer/legacyRequest.xsd");
        expected.add("consumer/legacyResponse.xsd");
        this.checkResult(result, expected);
    }

    /**
     * Test consumer both provider both.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testConsumerBothProviderBoth() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(4);
        expected.add("consumer/legacyRequest.xsd");
        expected.add("consumer/legacyResponse.xsd");
        expected.add("provider/legacyRequest.xsd");
        expected.add("provider/legacyResponse.xsd");
        this.checkResult(result, expected);
    }

    /**
     * Test consumer both provider single.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testConsumerBothProviderSingle() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(3);
        expected.add("consumer/legacyRequest.xsd");
        expected.add("consumer/legacyResponse.xsd");
        expected.add("provider/providerLegacyResponse.xsd");
        this.checkResult(result, expected);
    }

    /**
     * Test empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(0);
        this.checkResult(result, expected);
    }

    /**
     * Test provider single.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testProviderSingle() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("provider/legacyRequest.xsd");
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
            String value = assertion.getAttribute(new QName("location")) + "/" + assertion.getAttribute(new QName("schemaId"));
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
