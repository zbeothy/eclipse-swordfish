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
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyReader;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.exploration.IdValidationAssertionProcessor;
import org.eclipse.swordfish.policy.selector.ClassSelector;

/**
 * The Class PolicyProcessorTest.
 */
public class PolicyProcessorTest extends TestCase {

    /** The Constant DATA_LOCATION. */
    public static final String DATA_LOCATION = "/data/match";

    /** The name. */
    private static final QName HTTP_TRANSPORT_NAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "HttpTransport");

    /** The reader. */
    private static final PolicyReader READER = PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);

    /** The consumer policy. */
    private Policy consumerPolicy;

    /** The provider policy. */
    private Policy providerPolicy;

    /** The processor. */
    private PolicyProcessor processor;

    /** The id validator. */
    private IdValidationAssertionProcessor idValidator;

    /**
     * Instantiates a new policy processor test.
     * 
     * @param name
     *        the name
     */
    public PolicyProcessorTest(final String name) {
        super(name);
    }

    /**
     * Test clone.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testClone() throws UnexpectedPolicyProcessingException {
        InputStream is = this.getClass().getResourceAsStream("/data/mixed.xml");
        Policy mixedPolicy = READER.readPolicy(is);
        Policy test = (Policy) this.processor.cloneAssertion(mixedPolicy);
        TermIterator sourceIt = new TermIterator(mixedPolicy, new PrimitiveAssertionSelector(HTTP_TRANSPORT_NAME));
        TermIterator destIt = new TermIterator(test, new PrimitiveAssertionSelector(HTTP_TRANSPORT_NAME));
        PrimitiveAssertion source = (PrimitiveAssertion) sourceIt.next();
        PrimitiveAssertion dest = (PrimitiveAssertion) destIt.next();
        assertNotSame(source, dest);
        String sourceAt = source.getAttribute(new QName("at"));
        String destAt = dest.getAttribute(new QName("at"));
        // assertNotSame(sourceAt, destAt);
        assertEquals(sourceAt, destAt);
        dest.addAttribute(new QName("at"), "bar");
        sourceAt = source.getAttribute(new QName("at"));
        assertEquals("foo2", sourceAt);
        dest.addAttribute(new QName("baz"), "");
        sourceAt = source.getAttribute(new QName("baz"));
        assertNull(sourceAt);
    }

    /**
     * Test select one from many.
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public void testSelectOneFromMany() throws UnexpectedPolicyProcessingException {
        this.readData("1");
        Policy result = this.processor.match(this.consumerPolicy, this.providerPolicy);
        TermIterator dit = new TermIterator(result, new ClassSelector(All.class), true);
        All all = (All) dit.next();
        assertFalse(dit.hasNext());
        List terms = all.getTerms();
        assertEquals(2, terms.size());
        boolean consSeen = false;
        boolean provSeen = false;
        Iterator it = terms.iterator();
        QName name = new QName(PolicyConstants.SOP_ASSERTION_URI, "Foo");
        QName atName = new QName("at");
        while (it.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
            assertEquals(name, assertion.getName());
            String val = assertion.getAttribute(atName);
            if (val.equals("cons")) {
                consSeen = true;
            } else if (val.equals("prov")) {
                provSeen = true;
            }
        }
        assertTrue(consSeen);
        assertTrue(provSeen);
        // after normalization, consumer has 1 alternative, provider has 2
        assertEquals(3, this.idValidator.getCount());
        // and in the result, one from the consumer and one from the provider
        // was present
        assertEquals(1, this.idValidator.getOpenIds().size());
    }

    /**
     * Read data.
     * 
     * @param id
     *        the id
     */
    protected void readData(final String id) {
        String consumerLocation = DATA_LOCATION + "/consumer_" + id + ".xml";
        String providerLocation = DATA_LOCATION + "/provider_" + id + ".xml";
        InputStream is = this.getClass().getResourceAsStream(consumerLocation);
        this.consumerPolicy = READER.readPolicy(is);
        is = this.getClass().getResourceAsStream(providerLocation);
        this.providerPolicy = READER.readPolicy(is);
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.processor = new PolicyProcessor();
        this.processor.removeAllAssertionProcessors();
        this.idValidator = new IdValidationAssertionProcessor();
        this.processor.addAssertionProcessor(this.idValidator);
    }

}
