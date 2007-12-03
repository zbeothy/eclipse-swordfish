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

import java.io.InputStream;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.ws.policy.All;
import org.apache.ws.policy.CompositeAssertion;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyReader;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.selector.ClassSelector;

/**
 * The Class DeepTermIteratorTest.
 */
public class DeepTermIteratorTest extends TestCase {

    /** The reader. */
    private PolicyReader reader = PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);

    /** The empty policy. */
    private Policy emptyPolicy;

    /** The mixed policy. */
    private Policy mixedPolicy;

    /** The normalized policy. */
    private Policy normalizedPolicy;

    /**
     * Instantiates a new deep term iterator test.
     * 
     * @param name
     *        the name
     */
    public DeepTermIteratorTest(final String name) {
        super(name);
    }

    /**
     * Test concurrent modification.
     */
    public void testConcurrentModification() {
        TermIterator it =
                new TermIterator(this.mixedPolicy, new PrimitiveAssertionSelector(new QName(PolicyConstants.SOP_ASSERTION_URI,
                        "HttpTransport")), true);
        it.next();
        this.mixedPolicy.addTerm(new PrimitiveAssertion(new QName("foo")));
        try {
            it.next();
            fail("ConcurrentModificationException expected");
        } catch (ConcurrentModificationException e) {
            assertTrue(true);
        }

    }

    /**
     * Test deep term iterator assertion class.
     */
    public void testDeepTermIteratorAssertionClass() {
        // check that the iterator works for a concrete class
        TermIterator it = new TermIterator(this.mixedPolicy, new ClassSelector(All.class), true);
        int count = 0;
        while (it.hasNext()) {
            Object val = it.next();
            assertNotNull(val);
            assertTrue(val instanceof All);
            count++;
        }
        assertEquals(3, count);
        // check that the iterator works for an interface
        it = new TermIterator(this.mixedPolicy, new ClassSelector(CompositeAssertion.class), true);
        count = 0;
        while (it.hasNext()) {
            Object val = it.next();
            assertNotNull(val);
            assertTrue((val instanceof All) || (val instanceof ExactlyOne) || (val instanceof Policy));
            count++;
        }
        assertEquals(5, count);
    }

    /**
     * Test deep term iterator Q name.
     */
    public void testDeepTermIteratorQName() {
        QName name = new QName(PolicyConstants.SOP_ASSERTION_URI, "HttpTransport");
        TermIterator it = new TermIterator(this.mixedPolicy, new PrimitiveAssertionSelector(name), true);
        int count = 0;
        while (it.hasNext()) {
            Object val = it.next();
            assertNotNull(val);
            assertEquals(name, ((PrimitiveAssertion) val).getName()); // also
            // checks
            // type
            count++;
        }
        assertEquals(4, count);
    }

    /**
     * Test end of iteration.
     */
    public void testEndOfIteration() {
        TermIterator it = new TermIterator(this.mixedPolicy, new ClassSelector(All.class), true);
        while (it.hasNext()) {
            it.next();
        }
        try {
            it.next();
            fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
    }

    /**
     * Gets the empty policy.
     * 
     * @return Returns the emptyPolicy.
     */
    protected Policy getEmptyPolicy() {
        return this.emptyPolicy;
    }

    /**
     * Gets the normalized policy.
     * 
     * @return Returns the normalizedPolicy.
     */
    protected Policy getNormalizedPolicy() {
        return this.normalizedPolicy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        InputStream is = this.getClass().getResourceAsStream("/data/empty.xml");
        this.emptyPolicy = this.reader.readPolicy(is);
        is = this.getClass().getResourceAsStream("/data/mixed.xml");
        this.mixedPolicy = this.reader.readPolicy(is);
        this.normalizedPolicy = (Policy) this.mixedPolicy.normalize();

    }

}
