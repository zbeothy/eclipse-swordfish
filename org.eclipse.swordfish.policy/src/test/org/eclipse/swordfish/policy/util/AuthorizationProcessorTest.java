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
 * The Class AuthorizationProcessorTest.
 */
public class AuthorizationProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/authorization";

    /** The Constant AUTHORIZATION_QNAME. */
    public static final QName AUTHORIZATION_QNAME = AuthorizationProcessor.AUTHORIZATION_QNAME;

    /** The Constant LOCATION_ATTRIBUTE. */
    public static final QName LOCATION_ATTRIBUTE = AuthorizationProcessor.LOCATION_ATTRIBUTE;

    /**
     * Instantiates a new authorization processor test.
     * 
     * @param name
     *        the name
     */
    public AuthorizationProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test match both.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchBoth() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("both");
        this.checkResult(result, expected);
    }

    /**
     * Test match consumer only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchConsumerOnly() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iterPrimitives = new TermIterator(result, new PrimitiveAssertionSelector(AUTHORIZATION_QNAME));
        assertFalse(iterPrimitives.hasNext());
    }

    /**
     * Test match provider only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchProviderOnly() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("provider");
        this.checkResult(result, expected);
    }

    /**
     * Test none.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNone() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iterPrimitives = new TermIterator(result, new PrimitiveAssertionSelector(AUTHORIZATION_QNAME));
        assertFalse(iterPrimitives.hasNext());
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
        TermIterator iterPrimitives = new TermIterator(all, new PrimitiveAssertionSelector(AUTHORIZATION_QNAME));
        while (iterPrimitives.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
            String location = assertion.getAttribute(LOCATION_ATTRIBUTE);
            if (!expected.contains(location)) {
                fail("Encountered unexpected value " + location);
            } else {
                expected.remove(location);
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
