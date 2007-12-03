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
 * The Class ExtensionProcessorTest.
 */
public class ExtensionProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/extension";

    /** The Constant EXTENSION_QNAME. */
    public static final QName EXTENSION_QNAME = ExtensionProcessor.EXTENSION_QNAME;

    /** The Constant NAME_ATTRIBUTE. */
    public static final QName NAME_ATTRIBUTE = ExtensionProcessor.NAME_ATTRIBUTE;

    /** The Constant VALUE_ATTRIBUTE. */
    public static final QName VALUE_ATTRIBUTE = ExtensionProcessor.VALUE_ATTRIBUTE;

    /**
     * Instantiates a new extension processor test.
     * 
     * @param name
     *        the name
     */
    public ExtensionProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test match cap only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchCapOnly() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertEquals(0, this.numAssertionsOfType(result, EXTENSION_QNAME));
    }

    /**
     * Test match multiple names.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchMultipleNames() throws UnexpectedPolicyProcessingException {
        this.readData("8");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("foo#bar");
        expected.add("sna#baz");
        this.checkResult(result, expected);
    }

    /**
     * Test match select multiple.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchSelectMultiple() throws UnexpectedPolicyProcessingException {
        this.readData("6");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("foo#bar");
        expected.add("foo#baz");
        this.checkResult(result, expected);
    }

    /**
     * Test match select multiple with null.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchSelectMultipleWithNull() throws UnexpectedPolicyProcessingException {
        this.readData("7");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("foo#bar");
        expected.add("foo#null");
        this.checkResult(result, expected);
    }

    /**
     * Test match select value.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchSelectValue() throws UnexpectedPolicyProcessingException {
        this.readData("5");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("foo#bar");
        this.checkResult(result, expected);
    }

    /**
     * Test match simple.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchSimple() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("PayloadHandling#attached");
        this.checkResult(result, expected);
    }

    /**
     * Test match simple null value.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testMatchSimpleNullValue() throws UnexpectedPolicyProcessingException {
        this.readData("4");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("foo#null");
        this.checkResult(result, expected);
    }

    /**
     * Test no match simple.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testNoMatchSimple() throws UnexpectedPolicyProcessingException {
        this.readData("3");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertTrue(this.isNullPolicy(result));
    }

    /**
     * Test optional only.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testOptionalOnly() throws UnexpectedPolicyProcessingException {
        this.readData("11");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        assertEquals(1, this.getAlternatives(result).size());
        TermIterator iterPrimitives = new TermIterator(result, new PrimitiveAssertionSelector(EXTENSION_QNAME));
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
        TermIterator iterPrimitives = new TermIterator(all, new PrimitiveAssertionSelector(EXTENSION_QNAME));
        while (iterPrimitives.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
            String name = assertion.getAttribute(NAME_ATTRIBUTE);
            String value = assertion.getAttribute(VALUE_ATTRIBUTE);
            String encountered = name + "#" + value;
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
