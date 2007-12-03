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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.Role;
import org.eclipse.swordfish.policy.selector.ClassSelector;
import org.eclipse.swordfish.policy.selector.Selector;

/**
 * The Class PolicyProcessor.
 */
public class PolicyProcessor {

    /** The Constant NETWORK_OVERHEAD. */
    public final static String NETWORK_OVERHEAD = "org.eclipse.swordfish.policytrader.NetworkOverhead";

    /** The Constant SOP_ROLE_ATTRIBUTE. */
    public final static QName SOP_ROLE_ATTRIBUTE = PolicyConstants.SOP_ROLE_ATTRIBUTE;

    /** The Constant SOP_ID_ASSERTION. */
    public final static QName SOP_ID_ASSERTION = PolicyConstants.SOP_ID_ASSERTION;

    /** The props. */
    private static Properties props;

    /** The processor list. */
    private ArrayList processorList = new ArrayList();

    /** The transport list. */
    private List transportList;

    /**
     * Maps assertion QName to processor for all assertions that need pre-normalization handling
     * TODO: handling of assertion* -> processor and assertion -> processor*.
     */
    private HashMap assertionToProcessorMap = new HashMap();

    /** The selector. */
    private Selector selector = new Selector() {

        public boolean isValid(Object obj) {
            boolean ret = false;
            if (obj instanceof PrimitiveAssertion) {
                PrimitiveAssertion assertion = (PrimitiveAssertion) obj;
                ret = PolicyProcessor.this.assertionToProcessorMap.keySet().contains(assertion.getName());
            }
            return ret;
        }
    };

    /** The primitive selector. */
    private Selector primitiveSelector = new ClassSelector(PrimitiveAssertion.class);

    /** seed for unique identifier assertions. */
    private long sequence = 0;

    /**
     * Instantiates a new policy processor.
     */
    public PolicyProcessor() {
        TransportProcessor transportProcessor = new TransportProcessor();
        this.addAssertionProcessor(transportProcessor);
        this.transportList = transportProcessor.getTransportQNames();
        this.addAssertionProcessor(new SdxValidationProcessor());
        this.addAssertionProcessor(new TransformationProcessor());
        this.addAssertionProcessor(new ConversationalBindingProcessor());
        this.addAssertionProcessor(new PriorityProcessor());
        this.addAssertionProcessor(new EncryptionProcessor());
        this.addAssertionProcessor(new SignatureProcessor());
        this.addAssertionProcessor(new SubscriptionProcessor());
        this.addAssertionProcessor(new CorrelationProcessor());
        this.addAssertionProcessor(new CompressionProcessor());
        this.addAssertionProcessor(new CustomValidationProcessor());
        this.addAssertionProcessor(new AuthenticationProcessor());
        this.addAssertionProcessor(new AuthorizationProcessor());
        this.addAssertionProcessor(new ExtensionProcessor());
        this.addAssertionProcessor(new TrackingLevelProcessor());
        this.addAssertionProcessor(new MaxResponseTimeProcessor());
    }

    /**
     * Adds the assertion processor.
     * 
     * @param processor
     *        the processor
     * 
     * @return true, if successful
     */
    public synchronized boolean addAssertionProcessor(final AssertionProcessor processor) {
        QName assertionName = processor.getSupportedAssertion();
        if (null != assertionName) {
            this.assertionToProcessorMap.put(assertionName, processor);
        }
        return this.processorList.add(processor);
    }

    /**
     * Clone assertion.
     * 
     * @param source
     *        the source
     * 
     * @return the assertion
     */
    public Assertion cloneAssertion(final Assertion source) {
        Assertion ret = null;

        if (source instanceof Policy) {
            ret = new Policy(((Policy) source).getBase(), ((Policy) source).getId());
            Hashtable attribs = new Hashtable(((Policy) source).getAttributes());
            ((Policy) ret).setAttributes(attribs);
        } else if (source instanceof All) {
            ret = new All();
        } else if (source instanceof ExactlyOne) {
            ret = new ExactlyOne();
        } else if (source instanceof PrimitiveAssertion) {
            ret = new PrimitiveAssertion(((PrimitiveAssertion) source).getName(), ((PrimitiveAssertion) source).getValue());
            Hashtable attribs = new Hashtable(((PrimitiveAssertion) source).getAttributes());
            ((PrimitiveAssertion) ret).setAttributes(attribs);
            if (((PrimitiveAssertion) source).isOptional()) {
                ((PrimitiveAssertion) ret).setOptional(true);
            }
        } else
            throw new IllegalArgumentException("Unknown assertion type " + source.getClass().getName() + "encountered.");
        Iterator it = source.getTerms().iterator();
        while (it.hasNext()) {
            Assertion child = (Assertion) it.next();
            Assertion clone = this.cloneAssertion(child);
            ret.addTerm(clone);
        }
        return ret;
    }

    public HashMap getAssertionToProcessorMap() {
        return this.assertionToProcessorMap;
    }

    public Selector getPrimitiveSelector() {
        return this.primitiveSelector;
    }

    public ArrayList getProcessorList() {
        return this.processorList;
    }

    public Selector getSelector() {
        return this.selector;
    }

    public List getTransportList() {
        return this.transportList;
    }

    /**
     * Check for emptyness of a traded policy.
     * 
     * @param traded
     *        traded operation policy
     * 
     * @return <code>true</code> if empty i.e. trading gave no result
     */
    public boolean isEmpty(final Policy traded) {
        if (traded.isEmpty()) return true;
        final List terms = traded.getTerms();
        if (terms.size() == 1) {
            final Object o = terms.get(0);
            if ((o != null) && (o instanceof ExactlyOne)) {
                final ExactlyOne xo = (ExactlyOne) o;
                return (xo.isEmpty());
            }
            if ((o != null) && (o instanceof All)) {
                final All a = (All) o;
                return (a.isEmpty());
            }
            throw new IllegalArgumentException("unexpected traded policy received");
        }
        return false;
    }

    /**
     * Match.
     * 
     * @param consumer
     *        the consumer
     * @param provider
     *        the provider
     * 
     * @return the policy
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public Policy match(final Policy consumer, final Policy provider) throws UnexpectedPolicyProcessingException {
        Policy result = this.matchAllResults(consumer, provider);
        try {
            this.compressAssertion(result);
        } catch (RuntimeException e) {
            throw new UnexpectedPolicyProcessingException("During compression of matching result.", e);
        }
        return result;
    }

    /**
     * Match all results.
     * 
     * @param consumer
     *        the consumer
     * @param provider
     *        the provider
     * 
     * @return the policy
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    public Policy matchAllResults(final Policy consumer, final Policy provider) throws UnexpectedPolicyProcessingException {
        Policy cons = (Policy) this.cloneAssertion(consumer);
        Policy prov = (Policy) this.cloneAssertion(provider);
        this.preNormalize(cons);
        this.preNormalize(prov);
        cons = (Policy) cons.normalize();
        prov = (Policy) prov.normalize();
        HashMap consumerResults = this.preProcess(cons);
        HashMap providerResults = this.preProcess(prov);
        this.addRole(cons, Role.CONSUMER);
        this.addRole(prov, Role.PROVIDER);
        Policy result = (Policy) cons.intersect(prov);
        this.postProcess(result, consumerResults, providerResults);
        this.removeRoles(result);
        return result;
    }

    /**
     * Removes the all assertion processors.
     */
    public synchronized void removeAllAssertionProcessors() {
        for (Iterator iter = this.processorList.iterator(); iter.hasNext();) {
            AssertionProcessor processor = (AssertionProcessor) iter.next();
            this.clearSupportedAssertions(processor);
        }
        this.processorList.clear();
    }

    /**
     * Removes the assertion processor.
     * 
     * @param processor
     *        the processor
     * 
     * @return true, if successful
     */
    public synchronized boolean removeAssertionProcessor(final AssertionProcessor processor) {
        this.clearSupportedAssertions(processor);
        return this.processorList.remove(processor);
    }

    /**
     * Sets the properties.
     * 
     * @param properties
     *        the new properties
     */
    public void setProperties(final Properties properties) {
        props = properties;
        String networkOverheadSpec = props.getProperty(NETWORK_OVERHEAD);
        if (null != networkOverheadSpec) {
            Long networkOverhead = null;
            try {
                networkOverhead = Long.decode(networkOverheadSpec);
                MaxResponseTimeProcessor.setTransportOverhead(networkOverhead.longValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Could not convert value for org.eclipse.swordfish.policytrader.NetworkOverhead (" + networkOverheadSpec
                                + " to Long");
            }
        }
    }

    /**
     * Adds a role attribute to each.
     * 
     * @param assertion
     *        the assertion
     * @param role
     *        the role
     */
    private void addRole(final Assertion assertion, final Role role) {
        String roleName = role.toString();
        TermIterator it = new TermIterator(assertion, new ClassSelector(PrimitiveAssertion.class), true);
        while (it.hasNext()) {
            PrimitiveAssertion primitive = (PrimitiveAssertion) it.next();
            primitive.addAttribute(SOP_ROLE_ATTRIBUTE, roleName);
        }
    }

    /**
     * Clear supported assertions.
     * 
     * @param processor
     *        the processor
     */
    private void clearSupportedAssertions(final AssertionProcessor processor) {
        QName assertionName = processor.getSupportedAssertion();
        if (null != assertionName) {
            this.assertionToProcessorMap.remove(assertionName);
        }
    }

    /**
     * Compress assertion.
     * 
     * @param result
     *        the result
     */
    private void compressAssertion(final Policy result) {
        List terms = result.getTerms();
        if (terms.size() != 1) // log unexpected corrupt result
            throw new IllegalStateException("Encountered unexpected corrupted result policy - consider restarting the application");
        ExactlyOne eo = null;
        try {
            eo = (ExactlyOne) terms.get(0);
        } catch (ClassCastException e) {
            // log unexpected corrupt result
            throw new IllegalStateException("Encountered unexpected corrupted result policy - consider restarting the application");
        }
        List alternatives = eo.getTerms();
        if (1 >= alternatives.size()) // nothing to compress
            return;
        // transportAssertion -> CostResult for current preferred alternative
        HashMap preferredAlternatives = new HashMap(4);
        for (Iterator iter = alternatives.iterator(); iter.hasNext();) {
            try {
                All all = (All) iter.next();
                CostResult newCost = this.computeCost(all);
                CostResult oldCost = (CostResult) preferredAlternatives.get(newCost.transportQName);
                if (null == oldCost) {
                    preferredAlternatives.put(newCost.transportQName, newCost);
                } else {
                    if (newCost.cost < oldCost.cost) {
                        preferredAlternatives.put(newCost.transportQName, newCost);
                    }
                }
            } catch (ClassCastException e) {
                // log unexpected corrupt result
                throw new IllegalStateException(
                        "Encountered unexpected corrupted result policy - consider restarting the application");
            }
        }
        eo.getTerms().clear();
        for (Iterator iter = this.transportList.iterator(); iter.hasNext();) {
            String transportQName = (String) iter.next();
            CostResult val = (CostResult) preferredAlternatives.get(transportQName);
            if (null != val) {
                All alternative = val.alternative;
                eo.addTerm(alternative);
            }
        }
    }

    /**
     * Compute cost.
     * 
     * @param alternative
     *        the alternative
     * 
     * @return the cost result
     */
    private CostResult computeCost(final All alternative) {
        int cost = 0;
        CostResult result = new CostResult();
        TermIterator iter = new TermIterator(alternative, this.primitiveSelector);
        while (iter.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iter.next();
            QName name = assertion.getName();
            if (this.transportList.contains(name.toString())) {
                result.transportQName = assertion.getName().toString();
            } else {
                AssertionProcessor processor = (AssertionProcessor) this.assertionToProcessorMap.get(name);
                if (null != processor) {
                    cost += processor.getCost(alternative, assertion);
                }
            }
        }
        result.cost = cost;
        result.alternative = alternative;
        return result;
    }

    /**
     * Post process.
     * 
     * @param result
     *        the result
     * @param consumerResults
     *        the consumer results
     * @param providerResults
     *        the provider results
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private void postProcess(final Policy result, final HashMap consumerResults, final HashMap providerResults)
            throws UnexpectedPolicyProcessingException {
        ExactlyOne eo = (ExactlyOne) result.getTerms().get(0);
        // list of alternatives that are invalid according to at least one
        // AssertionProcessor
        ArrayList invalidTerms = new ArrayList(eo.getTerms().size());
        Iterator terms = new TermIterator(eo, new ClassSelector(All.class));
        while (terms.hasNext()) {
            All term = (All) terms.next();
            HashMap termConsumerResult = null;
            HashMap termProviderResult = null;
            Iterator ids = new TermIterator(term, new PrimitiveAssertionSelector(SOP_ID_ASSERTION));
            // the id assertions that should be removed after processing
            PrimitiveAssertion[] toRemove = new PrimitiveAssertion[2];
            int index = 0;
            // get id assertions from term and look up preprocessing results for
            // each role
            while (ids.hasNext()) {
                PrimitiveAssertion id = (PrimitiveAssertion) ids.next();
                String role = id.getAttribute(SOP_ROLE_ATTRIBUTE);
                if (Role.CONSUMER.toString().equals(role)) {
                    termConsumerResult = (HashMap) consumerResults.get(id.getValue());
                } else if (Role.PROVIDER.toString().equals(role)) {
                    termProviderResult = (HashMap) providerResults.get(id.getValue());
                } else
                    throw new UnexpectedPolicyProcessingException("Encountered illegal role " + id.getValue()
                            + " - aborting matching");
                toRemove[index++] = id;
            }
            // postprocess term with each AssertionProcessor
            Iterator processors = this.processorList.iterator();
            while (processors.hasNext()) {
                AssertionProcessor processor = (AssertionProcessor) processors.next();
                Object processorConsumerResult = termConsumerResult.get(processor);
                Object processorProviderResult = termProviderResult.get(processor);
                if (!processor.postProcessAlternative(term, processorConsumerResult, processorProviderResult)) {
                    invalidTerms.add(term);
                    break;
                }
            }
            // remove id assertions
            for (int i = 0; i < toRemove.length; i++) {
                term.remove(toRemove[i]);
            }
        }
        // remove invalid terms
        Iterator it = invalidTerms.iterator();
        while (it.hasNext()) {
            All term = (All) it.next();
            eo.remove(term);
        }
    }

    /**
     * Pre normalize.
     * 
     * @param policy
     *        the policy
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private void preNormalize(final Policy policy) throws UnexpectedPolicyProcessingException {
        TermIterator it = new TermIterator(policy, this.selector, true);
        while (it.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
            AssertionProcessor processor = (AssertionProcessor) this.assertionToProcessorMap.get(assertion.getName());
            if (null != processor) {
                processor.preNormalization(assertion);
            }
        }
    }

    /**
     * Calls <code>AssertionProcessor.preProcess</code> on each alternative in the pre
     * 
     * @param policy
     *        the policy
     * 
     * @return the hash map
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private HashMap preProcess(final Policy policy) throws UnexpectedPolicyProcessingException {
        ExactlyOne eo = (ExactlyOne) policy.getTerms().get(0);
        HashMap result = new HashMap(eo.getTerms().size());
        Iterator terms = new TermIterator(eo, new ClassSelector(All.class));
        while (terms.hasNext()) {
            All term = (All) terms.next();
            PrimitiveAssertion id = new PrimitiveAssertion(SOP_ID_ASSERTION, new Long(this.sequence++));
            term.addTerm(id);
            HashMap termResults = new HashMap(term.getTerms().size());
            Iterator it = this.processorList.iterator();
            while (it.hasNext()) {
                AssertionProcessor processor = (AssertionProcessor) it.next();
                Object res = processor.preProcessAlternative(term);
                if (null != res) {
                    termResults.put(processor, res);
                }
            }
            result.put(id.getValue(), termResults);
        }
        return result;
    }

    /**
     * Removes the roles.
     * 
     * @param assertion
     *        the assertion
     */
    private void removeRoles(final Assertion assertion) {
        TermIterator it = new TermIterator(assertion, new ClassSelector(PrimitiveAssertion.class), true);
        while (it.hasNext()) {
            PrimitiveAssertion primitive = (PrimitiveAssertion) it.next();
            primitive.removeAttribute(SOP_ROLE_ATTRIBUTE);
        }
    }

    /**
     * Just a struct to hold costing results.
     */
    private class CostResult {

        /** The transport Q name. */
        private String transportQName;

        /** The cost. */
        private int cost;

        /** The alternative. */
        private All alternative;

        public All getAlternative() {
            return this.alternative;
        }

        public int getCost() {
            return this.cost;
        }

        public String getTransportQName() {
            return this.transportQName;
        }

    }

}
