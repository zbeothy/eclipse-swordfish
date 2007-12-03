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
 * The Class CorrelationProcessorTest.
 */
public class CorrelationProcessorTest extends AssertionProcessorTest {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/correlation";

    /** The Constant CORRELATION_QNAME. */
    private final static QName CORRELATION_QNAME = CorrelationProcessor.CORRELATION_QNAME;

    /** The Constant NAME_ATTRIBUTE. */
    private final static QName NAME_ATTRIBUTE = new QName("name");

    /** The Constant LOCATION_ATTRIBUTE. */
    private final static QName LOCATION_ATTRIBUTE = new QName("location");

    /**
     * Instantiates a new correlation processor test.
     * 
     * @param name
     *        the name
     */
    public CorrelationProcessorTest(final String name) {
        super(name, DATA_LOCATION);
    }

    /**
     * Test double correlation.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testDoubleCorrelation() throws UnexpectedPolicyProcessingException {
        this.readData("2");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(2);
        expected.add("OrderID#receiver");
        expected.add("RefTag#null");
        this.checkResult(result, expected);
    }

    /**
     * Test single correlation.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSingleCorrelation() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.getProcessor().match(this.getConsumerPolicy(), this.getProviderPolicy());
        ArrayList expected = new ArrayList(1);
        expected.add("OrderID#null");
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
        TermIterator iterPrimitives = new TermIterator(all, new PrimitiveAssertionSelector(CORRELATION_QNAME));
        while (iterPrimitives.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
            String name = assertion.getAttribute(NAME_ATTRIBUTE);
            String location = assertion.getAttribute(LOCATION_ATTRIBUTE);
            String encountered = name + "#" + location;
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
