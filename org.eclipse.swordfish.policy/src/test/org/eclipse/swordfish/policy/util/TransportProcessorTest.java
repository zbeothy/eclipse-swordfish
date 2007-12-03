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
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.selector.ClassSelector;

/**
 * The Class TransportProcessorTest.
 */
public class TransportProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/transport";

    /** The http transport. */
    private static PrimitiveAssertion httpTransport =
            new PrimitiveAssertion(new QName(PolicyConstants.SOP_ASSERTION_URI, "HttpTransport"));

    /** The https transport. */
    private static PrimitiveAssertion httpsTransport =
            new PrimitiveAssertion(new QName(PolicyConstants.SOP_ASSERTION_URI, "HttpsTransport"));

    /** The jms transport. */
    private static PrimitiveAssertion jmsTransport =
            new PrimitiveAssertion(new QName(PolicyConstants.SOP_ASSERTION_URI, "JmsTransport"));

    /** QNames of valid transports. */
    private ArrayList validTransports;

    /**
     * Instantiates a new transport processor test.
     * 
     * @param name
     *        the name
     */
    public TransportProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test empty empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testEmptyEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add(httpTransport.getName());
        List alternatives = this.getAlternatives(result);
        assertEquals(1, alternatives.size());
        this.checkResult((Assertion) alternatives.get(0), expected);
    }

    /**
     * Test empty single.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testEmptySingle() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add(httpTransport.getName());
        List alternatives = this.getAlternatives(result);
        assertEquals(1, alternatives.size());
        this.checkResult((Assertion) alternatives.get(0), expected);
    }

    /**
     * Test no match empty multiple.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchEmptyMultiple() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        List alternatives = this.getAlternatives(result);
        assertEquals(0, alternatives.size());
    }

    /**
     * Test no match one multiple.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchOneMultiple() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        List alternatives = this.getAlternatives(result);
        assertEquals(0, alternatives.size());
    }

    /**
     * Test single multiple.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleMultiple() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add(httpsTransport.getName());
        List alternatives = this.getAlternatives(result);
        assertEquals(1, alternatives.size());
        this.checkResult((Assertion) alternatives.get(0), expected);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessorTest#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.validTransports = new ArrayList(3);
        this.validTransports.add(httpTransport.getName());
        this.validTransports.add(httpsTransport.getName());
        this.validTransports.add(jmsTransport.getName());
    }

    /**
     * checks if exactly the assertions listed in expected are included in result.
     * 
     * @param result
     *        the result
     * @param expected
     *        the expected
     * 
     * @throws RuntimeException
     *         if error in assertion list
     */
    private void checkResult(final Assertion result, final ArrayList expected) {
        TermIterator it = new TermIterator(result, new ClassSelector(PrimitiveAssertion.class));
        while (it.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
            if (this.validTransports.contains(assertion.getName())) {
                if (!expected.remove(assertion.getName())) {
                    fail("unexpected assertion found: " + assertion.getName().toString());
                }
            }
        }
        if (0 != expected.size()) {
            StringBuffer missing = new StringBuffer("Expected assertions missing: \n");
            Iterator iter = expected.iterator();
            while (iter.hasNext()) {
                missing.append(((PrimitiveAssertion) iter.next()).getName().toString()).append("\n");
            }
            fail(missing.toString());
        }
    }

}
