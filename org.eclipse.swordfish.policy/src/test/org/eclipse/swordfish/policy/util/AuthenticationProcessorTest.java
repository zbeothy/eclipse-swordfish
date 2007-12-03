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
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.Role;

/**
 * The Class AuthenticationProcessorTest.
 */
public class AuthenticationProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/authentication";

    /**
     * Instantiates a new authentication processor test.
     * 
     * @param name
     *        the name
     */
    public AuthenticationProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test empty optional.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testEmptyOptional() throws UnexpectedPolicyProcessingException {
        this.readData("6");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        QName httpTransport = new QName(PolicyConstants.SOP_ASSERTION_URI, "HttpTransport");
        TermIterator it = new TermIterator(result, new PrimitiveAssertionSelector(httpTransport), true);
        it.next();
        assertFalse(it.hasNext());
        // TODO: check form of result
    }

    /**
     * Test expand list.
     */
    public void testExpandList() {
        AuthenticationProcessor authProc = new AuthenticationProcessor();
        List input = new ArrayList();
        PrimitiveAssertion auth = new PrimitiveAssertion(AuthenticationProcessor.AUTHENTICATION_QNAME);
        auth.addAttribute(AuthenticationProcessor.TYPE_ATTRIBUTE, "foo");
        input.add(auth);
        authProc.expandList(input, Role.PROVIDER);
        // request mandatory, response optional
        authProc.expandList(input, Role.CONSUMER);
        // response mandatory, request optional
    }

    /**
     * consumer presents an agreed policy that tries to satisfy a provider demand for request
     * authentication with an assertion for response authentication sample case for run-time
     * validation of agreed policy.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testForged1() throws UnexpectedPolicyProcessingException {
        this.readData("forged1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.getProcessor().isEmpty(result));
    }

    /**
     * Test mandatory mandatory fail.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMandatoryMandatoryFail() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test mandatory mandatory select match.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMandatoryMandatorySelectMatch() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("UsernameToken#request");
        expected.add("UsernameToken#response");
        this.checkResult(result, expected);
    }

    /**
     * Test mandatory optional select match.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMandatoryOptionalSelectMatch() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("UsernameToken#response");
        this.checkResult(result, expected);
    }

    /**
     * sample case for run-time validation of agreed policy.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatched1() throws UnexpectedPolicyProcessingException {
        this.readData("matched1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("UsernameToken#request");
        this.checkResult(result, expected);
    }

    /**
     * Test optional mandatory both match.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalMandatoryBothMatch() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().matchAllResults(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("UsernameToken#request");
        expected.add("SAMLToken#request");
        this.checkResult(result, expected);
        result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        expected = new ArrayList(1);
        expected.add("SAMLToken#request");
        this.checkResult(result, expected);
    }

    /**
     * Test optional mandatory match.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalMandatoryMatch() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("UsernameToken#request");
        this.checkResult(result, expected);
    }

    /**
     * Test Q c3330.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testQC3330() throws UnexpectedPolicyProcessingException {
        this.readData("qc3330");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        // expect one assertion of defined type for each transport in match
        expected.add("UsernameToken#request");
        expected.add("UsernameToken#request");
        this.checkResult(result, expected);
    }

    /**
     * Test select by mandatory.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSelectByMandatory() throws UnexpectedPolicyProcessingException {
        // select one of multiple optionals from one mandatory
        this.readData("10");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("UsernameToken#response");
        this.checkResult(result, expected);
    }

    /**
     * Test select by optional.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSelectByOptional() throws UnexpectedPolicyProcessingException {
        // select one of multiple mandatory from one optionals
        this.readData("9");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("SAMLToken#response");
        this.checkResult(result, expected);
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
        TermIterator iterPrimitives =
                new TermIterator(eo, new PrimitiveAssertionSelector(AuthenticationProcessor.AUTHENTICATION_QNAME), true);
        while (iterPrimitives.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
            String type = assertion.getAttribute(AuthenticationProcessor.TYPE_ATTRIBUTE);
            String message = assertion.getAttribute(AuthenticationProcessor.MESSAGE_ATTRIBUTE);
            String encountered = type + "#" + message;
            if (!expected.contains(encountered)) {
                fail("Encountered unexpected value " + encountered);
            } else {
                expected.remove(encountered);
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
