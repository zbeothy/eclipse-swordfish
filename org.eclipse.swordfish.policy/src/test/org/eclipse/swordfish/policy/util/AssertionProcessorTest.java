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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.CompositeAssertion;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyReader;
import org.eclipse.swordfish.policy.exploration.IdValidationAssertionProcessor;
import org.eclipse.swordfish.policy.selector.ClassSelector;

/**
 * The Class AssertionProcessorTest.
 */
public class AssertionProcessorTest extends TestCase {

    /** The data location. */
    private String dataLocation = "replaceme";

    /** The consumer policy. */
    private Policy consumerPolicy;

    /** The provider policy. */
    private Policy providerPolicy;

    /** The reader. */
    private PolicyReader reader = PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);

    /** The processor. */
    private PolicyProcessor processor;

    /** The id validator. */
    private IdValidationAssertionProcessor idValidator;

    /**
     * Instantiates a new assertion processor test.
     * 
     * @param name
     *        the name
     */
    public AssertionProcessorTest(final String name) {
        super(name);
    }

    /**
     * Instantiates a new assertion processor test.
     * 
     * @param name
     *        the name
     * @param dataLocation
     *        the data location
     */
    public AssertionProcessorTest(final String name, final String dataLocation) {
        super(name);
        this.dataLocation = dataLocation;
    }

    /**
     * Gets the data location.
     * 
     * @return Returns the dataLocation.
     */
    public String getDataLocation() {
        return this.dataLocation;
    }

    /**
     * Sets the data location.
     * 
     * @param dataLocation
     *        The dataLocation to set.
     */
    public void setDataLocation(final String dataLocation) {
        this.dataLocation = dataLocation;
    }

    /**
     * Test dummy.
     */
    public void testDummy() {
        // dummy test to prevent error during test execution
        // maven tries to run abstract test classes also :-(
    }

    /**
     * compares two assertions, ignoring sequence of terms - <b>destructive!</b>.
     * 
     * @param a
     *        the a
     * @param b
     *        the b
     * 
     * @return true, if are assertions equal
     */
    protected boolean areAssertionsEqual(final Assertion a, final Assertion b) {
        boolean ret = false;
        if (a.getType() == b.getType()) {
            if ((a instanceof CompositeAssertion) && (b instanceof CompositeAssertion)) {
                Iterator itA = a.getTerms().iterator();
                while (itA.hasNext()) {
                    Assertion childA = (Assertion) itA.next();
                    Iterator itB = b.getTerms().iterator();
                    Assertion childB = null;
                    boolean found = false;
                    while (itB.hasNext()) {
                        childB = (Assertion) itB.next();
                        if (this.areAssertionsEqual(childA, childB)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) return false;
                    b.remove(childB);
                }
                if (itA.hasNext()) return false;
                if (b.getTerms().size() == 0) {
                    ret = true;
                }
            } else if ((a instanceof PrimitiveAssertion) && (b instanceof PrimitiveAssertion)) {
                PrimitiveAssertion primA = (PrimitiveAssertion) a;
                PrimitiveAssertion primB = (PrimitiveAssertion) b;
                QName nameA = primA.getName();
                QName nameB = primB.getName();
                if (nameA.equals(nameB)) {
                    Hashtable attribsA = primA.getAttributes();
                    Hashtable attribsB = primB.getAttributes();
                    return attribsA.equals(attribsB);
                } else
                    return false;
            }
        }
        return ret;
    }

    /**
     * Gets the alternatives.
     * 
     * @param result
     *        the result
     * 
     * @return the alternatives
     */
    protected List getAlternatives(final Policy result) {
        ExactlyOne eo = (ExactlyOne) result.getTerms().get(0);
        return eo.getTerms();
    }

    /**
     * Gets the consumer policy.
     * 
     * @return Returns the consumerPolicy.
     */
    protected Policy getConsumerPolicy() {
        return this.consumerPolicy;
    }

    /**
     * Gets the id validator.
     * 
     * @return Returns the idValidator.
     */
    protected IdValidationAssertionProcessor getIdValidator() {
        return this.idValidator;
    }

    /**
     * Gets the processor.
     * 
     * @return Returns the processor.
     */
    protected PolicyProcessor getProcessor() {
        return this.processor;
    }

    /**
     * Gets the provider policy.
     * 
     * @return Returns the providerPolicy.
     */
    protected Policy getProviderPolicy() {
        return this.providerPolicy;
    }

    /**
     * Checks if is empty policy.
     * 
     * @param result
     *        the result
     * 
     * @return true, if is empty policy
     */
    protected boolean isEmptyPolicy(final Policy result) {
        boolean ret = true;
        ExactlyOne eo = null;
        try {
            eo = (ExactlyOne) result.getTerms().get(0);
        } catch (RuntimeException e) {
            ret = false;
        }
        if (1 != result.getTerms().size()) {
            ret = false;
        }
        All all = null;
        try {
            all = (All) eo.getTerms().get(0);
        } catch (RuntimeException e) {
            ret = false;
        }
        if (0 != all.getTerms().size()) return false;
        return ret;
    }

    /**
     * Checks if is null policy.
     * 
     * @param result
     *        the result
     * 
     * @return true, if is null policy
     */
    protected boolean isNullPolicy(final Policy result) {
        boolean ret = true;
        ExactlyOne eo = null;
        try {
            eo = (ExactlyOne) result.getTerms().get(0);
        } catch (RuntimeException e) {
            ret = false;
        }
        if ((1 != result.getTerms().size()) || (0 != eo.getTerms().size())) {
            ret = false;
        }
        return ret;
    }

    /**
     * checks that there is exactly one alternative.
     * 
     * @param result
     *        the result
     * @param type
     *        the type
     * 
     * @return number or assertions of given type in result
     */
    protected int numAssertionsOfType(final Policy result, final QName type) {
        int ret = 0;
        ExactlyOne eo = (ExactlyOne) result.getTerms().get(0);
        TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
        All all = (All) iterAll.next();
        assertFalse(iterAll.hasNext());
        TermIterator iterPrimitives = new TermIterator(all, new PrimitiveAssertionSelector(type));
        while (iterPrimitives.hasNext()) {
            ret++;
            iterPrimitives.next();
        }
        return ret;
    }

    /**
     * Read data.
     * 
     * @param id
     *        the id
     */
    protected void readData(final String id) {
        String consumerLocation = this.dataLocation + "/consumer_" + id + ".xml";
        String providerLocation = this.dataLocation + "/provider_" + id + ".xml";
        InputStream is = this.getClass().getResourceAsStream(consumerLocation);
        try {
            this.consumerPolicy = this.reader.readPolicy(is);
        } catch (IllegalArgumentException e) {
            String msg = "While trying to get data from " + consumerLocation;
            throw new RuntimeException(msg, e);
        }
        is = this.getClass().getResourceAsStream(providerLocation);
        try {
            this.providerPolicy = this.reader.readPolicy(is);
        } catch (IllegalArgumentException e) {
            String msg = "While trying to get data from " + providerLocation;
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Sets the consumer policy.
     * 
     * @param consumerPolicy
     *        The consumerPolicy to set.
     */
    protected void setConsumerPolicy(final Policy consumerPolicy) {
        this.consumerPolicy = consumerPolicy;
    }

    /**
     * Sets the id validator.
     * 
     * @param idValidator
     *        The idValidator to set.
     */
    protected void setIdValidator(final IdValidationAssertionProcessor idValidator) {
        this.idValidator = idValidator;
    }

    /**
     * Sets the processor.
     * 
     * @param processor
     *        The processor to set.
     */
    protected void setProcessor(final PolicyProcessor processor) {
        this.processor = processor;
    }

    /**
     * Sets the provider policy.
     * 
     * @param providerPolicy
     *        The providerPolicy to set.
     */
    protected void setProviderPolicy(final Policy providerPolicy) {
        this.providerPolicy = providerPolicy;
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
        this.idValidator = new IdValidationAssertionProcessor();
        this.processor.addAssertionProcessor(this.idValidator);
    }

    /**
     * Gets the reader.
     * 
     * @return Returns the reader.
     */
    PolicyReader getReader() {
        return this.reader;
    }

    /**
     * Sets the reader.
     * 
     * @param reader
     *        The reader to set.
     */
    void setReader(final PolicyReader reader) {
        this.reader = reader;
    }

}
