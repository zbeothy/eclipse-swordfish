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
 * The Class CompressionProcessorTest.
 */
public class CompressionProcessorTest extends AssertionProcessorTest {

    /** The COMPRESSIO n_ QNAME. */
    public static final QName COMPRESSION_QNAME = CompressionProcessor.COMPRESSION_QNAME;

    /** The MESSAG e_ ATTRIBUTE. */
    public static final QName MESSAGE_ATTRIBUTE = CompressionProcessor.MESSAGE_ATTRIBUTE;

    /** The SIZ e_ ATTRIBUTE. */
    public static final QName SIZE_ATTRIBUTE = CompressionProcessor.SIZE_ATTRIBUTE;

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/compression";

    /**
     * Instantiates a new compression processor test.
     * 
     * @param name
     *        the name
     */
    public CompressionProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test all_ empty.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testAllEmpty() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("request#null");
        expected.add("response#null");
        this.checkResult(result, expected);
    }

    /**
     * Test all_ respose with size.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testAllResposeWithSize() throws UnexpectedPolicyProcessingException {
        this.readData("7");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("request#null");
        expected.add("response#1500");
        this.checkResult(result, expected);
    }

    /**
     * Test request_ double response only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestDoubleResponseOnly() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test request_ forbidden.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestForbidden() throws UnexpectedPolicyProcessingException {
        this.readData("8");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test request_ optional with size.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestOptionalWithSize() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("request#1500");
        this.checkResult(result, expected);
    }

    /**
     * Test request_ response optional.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestResponseOptional() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test request_ respose with size.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestResposeWithSize() throws UnexpectedPolicyProcessingException {
        this.readData("6");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("request#null");
        expected.add("response#1500");
        this.checkResult(result, expected);
    }

    /**
     * Test request with size_ double with size.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testRequestWithSizeDoubleWithSize() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("request#1000");
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
        TermIterator iterPrimitives = new TermIterator(all, new PrimitiveAssertionSelector(COMPRESSION_QNAME));
        while (iterPrimitives.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
            String message = assertion.getAttribute(MESSAGE_ATTRIBUTE);
            String size = assertion.getAttribute(SIZE_ATTRIBUTE);
            String encountered = message + "#" + size;
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
