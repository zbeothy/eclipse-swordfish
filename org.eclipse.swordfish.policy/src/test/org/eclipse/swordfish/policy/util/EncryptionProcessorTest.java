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
 * The Class EncryptionProcessorTest.
 */
public class EncryptionProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/encryption";

    /** The Constant ENCRYPTION_QNAME. */
    private final static QName ENCRYPTION_QNAME = EncryptionProcessor.ENCRYPTION_QNAME;

    /** The Constant MESSAGE_ATTRIBUTE. */
    private final static QName MESSAGE_ATTRIBUTE = EncryptionProcessor.MESSAGE_ATTRIBUTE;

    /** The Constant selector. */
    private static final Selector SELECTOR = new PrimitiveAssertionSelector(ENCRYPTION_QNAME);

    /**
     * Instantiates a new encryption processor test.
     * 
     * @param name
     *        the name
     */
    public EncryptionProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test alloptional both.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testAlloptionalBoth() throws UnexpectedPolicyProcessingException {
        this.readData("12");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("request");
        this.checkResult(result, expected);
    }

    /**
     * Test alloptional response.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testAlloptionalResponse() throws UnexpectedPolicyProcessingException {
        this.readData("11");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("response");
        this.checkResult(result, expected);
    }

    /**
     * Test all response.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testAllResponse() throws UnexpectedPolicyProcessingException {
        this.readData("10");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test no no.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoNo() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iter = new TermIterator(result, SELECTOR, true);
        assertTrue(!iter.hasNext());
        assertTrue(!this.isNullPolicy(result));
    }

    /**
     * Test no optional.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoOptional() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iter = new TermIterator(result, SELECTOR, true);
        assertTrue(!iter.hasNext());
        assertTrue(!this.isNullPolicy(result));
    }

    /**
     * Test no yes.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoYes() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test optional no.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalNo() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iter = new TermIterator(result, SELECTOR, true);
        assertTrue(!iter.hasNext());
        assertTrue(!this.isNullPolicy(result));
    }

    /**
     * Test optional optional.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalOptional() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        TermIterator iter = new TermIterator(result, SELECTOR, true);
        assertTrue(!iter.hasNext());
        assertTrue(!this.isNullPolicy(result));
    }

    /**
     * Test optional yes.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalYes() throws UnexpectedPolicyProcessingException {
        this.readData("6");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("request");
        this.checkResult(result, expected);
    }

    /**
     * Test sample.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSample() throws UnexpectedPolicyProcessingException {
        this.readData("sample1");
        this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
    }

    /**
     * Test yes no.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testYesNo() throws UnexpectedPolicyProcessingException {
        this.readData("7");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test yes optional.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testYesOptional() throws UnexpectedPolicyProcessingException {
        this.readData("8");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("request");
        this.checkResult(result, expected);
    }

    /**
     * Test yes yes.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testYesYes() throws UnexpectedPolicyProcessingException {
        this.readData("9");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("request");
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
            String message = assertion.getAttribute(MESSAGE_ATTRIBUTE);
            if (!expected.contains(message)) {
                fail("Encountered unexpected value " + message);
            } else {
                expected.remove(message);
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
