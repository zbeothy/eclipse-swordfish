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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyReader;
import org.eclipse.swordfish.policy.selector.ClassSelector;

/**
 * Process transport assertions
 * 
 * The only action necessary is to include a default transport assertion during preprocessing if
 * none has been specified for the alternative.
 * 
 * The available transport assertions are defined in an xml resource
 * <code>TransportAssertions.xml</code> in the same package as the terms of a root Policy. The
 * first transport assertion defined there will be introduced by this processor as default if no
 * transport assertion is contained in the alternative.
 * 
 */
public class TransportProcessor extends AbstractAssertionProcessor implements AssertionProcessor {

    /** The name of the resource file that contains a policy with the valid transport assertions. */
    private static final String ASSERTIONS_RESOURCE = "TransportAssertions.xml";

    /** The Constant reader. */
    private static final PolicyReader READER = PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);

    /**
     * Strings derived from QNames of legal transports uri#localname Not using QName directly
     * because of possible problems with implementation of equals.
     */
    private static List transportQNames = null;

    /** The default transport. */
    private static PrimitiveAssertion defaultTransport;

    /**
     * Instantiates a new transport processor.
     */
    public TransportProcessor() {
        if (null == transportQNames) {
            synchronized (TransportProcessor.class) {
                InputStream is = this.getClass().getResourceAsStream(ASSERTIONS_RESOURCE);
                Policy transports = READER.readPolicy(is);
                TermIterator it = new TermIterator(transports, new ClassSelector(PrimitiveAssertion.class));
                transportQNames = new ArrayList(transports.getTerms().size());
                try {
                    defaultTransport = (PrimitiveAssertion) it.next();
                } catch (NoSuchElementException e) {
                    throw new RuntimeException("Could not instantiate - no default transport specified in " + ASSERTIONS_RESOURCE);
                }
                QName qname = defaultTransport.getName();
                transportQNames.add(qname.toString());
                while (it.hasNext()) {
                    qname = ((PrimitiveAssertion) it.next()).getName();
                    transportQNames.add(qname.toString());
                }
                transportQNames = Collections.unmodifiableList(transportQNames);
            }
        }
    }

    /**
     * Transports are handled differently - the cheapest alternative <i>per transport</i> is
     * included in the result.
     * 
     * @param alternative
     *        the alternative
     * @param assertion
     *        the assertion
     * 
     * @return the cost
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessor#getCost(org.apache.ws.policy.All,
     *      org.apache.ws.policy.PrimitiveAssertion)
     */
    public int getCost(final All alternative, final PrimitiveAssertion assertion) {
        return 0;
    }

    // no special handling of wsp:Optional since not allowed for transport

    /**
     * Gets the transport Q names.
     * 
     * @return the transport Q names
     */
    public List getTransportQNames() {
        return transportQNames;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#postProcessAlternative(org.apache.ws.policy.All,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean postProcessAlternative(final All alternative, final Object preProcessResultConsumer,
            final Object preProcessResultProvider) throws UnexpectedPolicyProcessingException {
        // for each alternative result, the matching result should contain
        // exactly two transport assertions
        // these should have the same type, so any extra is removed
        PrimitiveAssertion toRemove = null;
        int count = 0;
        TermIterator it = new TermIterator(alternative, new ClassSelector(PrimitiveAssertion.class));
        while (it.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
            QName qname = (assertion).getName();
            if (transportQNames.contains(qname.toString())) {
                if (count++ != 0) {
                    toRemove = assertion;
                }
            }
        }
        if (null != toRemove) {
            alternative.remove(toRemove);
        }
        // if we did not see exactly 2 transport terms, there is something wrong
        // with the matching algo
        // !TODO: add logging
        if (2 != count)
            throw new UnexpectedPolicyProcessingException("Unexpected number of transports in matching result: " + count);
        return (null != toRemove);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#preProcessAlternative(org.apache.ws.policy.All)
     */
    @Override
    public Object preProcessAlternative(final All alternative) {
        TermIterator it = new TermIterator(alternative, new ClassSelector(PrimitiveAssertion.class));
        boolean transportSeen = false;
        while (it.hasNext()) {
            QName qname = ((PrimitiveAssertion) it.next()).getName();
            if (transportQNames.contains(qname.toString())) {
                transportSeen = true;
                break;
            }
        }
        if (!transportSeen) {
            alternative.addTerm(defaultTransport);
        }
        return null;
    }

}
