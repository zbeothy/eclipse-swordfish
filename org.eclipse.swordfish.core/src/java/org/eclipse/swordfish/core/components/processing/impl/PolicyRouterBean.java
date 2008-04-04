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
package org.eclipse.swordfish.core.components.processing.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.policy.All;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.planner.Planner;
import org.eclipse.swordfish.core.components.processing.PolicyRouter;
import org.eclipse.swordfish.core.components.processing.PolicyValidator;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.processing.ProcessingComponent;
import org.eclipse.swordfish.core.components.srproxy.SrProxyCache;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.exception.InfrastructureRuntimeException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalFatalException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.policy.selector.ClassSelector;
import org.eclipse.swordfish.policy.selector.NameSelector;
import org.eclipse.swordfish.policy.util.TermCollector;
import org.eclipse.swordfish.policy.util.TermIterator;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is instatiated to root the processing of a given message with respect to a given
 * policy TODO comment me even more.
 */
public class PolicyRouterBean implements PolicyRouter, BeanFactoryAware {

    /** logger. */
    private static final Log LOG = SBBLogFactory.getLog(PolicyRouterBean.class);

    /** reference to the planner bean. */
    private Planner planner = null;

    /** reference to the validator bean. */
    private PolicyValidator validator = null;

    /** bean factory reference. */
    private BeanFactory beanFactory = null;

    /** Configuration properties. */
    private boolean policyExpiryCheck = true;

    /** The policy expiry grace period. */
    private int policyExpiryGracePeriod = 0;

    /** The sr cache. */
    private SrProxyCache srCache = null;

    /**
     * Gets the planner.
     * 
     * @return Returns the planner.
     */
    public Planner getPlanner() {
        return this.planner;
    }

    /**
     * Gets the policy expiry grace period.
     * 
     * @return the policy expiry grace period
     */
    public int getPolicyExpiryGracePeriod() {
        return this.policyExpiryGracePeriod;
    }

    /**
     * Gets the validator.
     * 
     * @return the validator
     */
    public PolicyValidator getValidator() {
        return this.validator;
    }

    // --------------- private methods

    /**
     * This method processes faults on the base of the context information.
     * 
     * @param message
     *        the message exchange.
     * @param role
     *        the caller role - initiator/servicer
     * @param agreedPolicy
     *        the agreed policy to process.
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    public void handleFault(final MessageExchange message, final Role role, final AgreedPolicy agreedPolicy)
            throws InternalSBBException, PolicyViolatedException {
        this.handle(message, role, agreedPolicy, Scope.RESPONSE, true);
    }

    /**
     * This method processes requests on the base of the context information.
     * 
     * @param message
     *        the message exchange.
     * @param role
     *        the caller role - initiator/servicer
     * @param agreedPolicy
     *        the agreed policy to process.
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    public void handleRequest(final MessageExchange message, final Role role, final AgreedPolicy agreedPolicy)
            throws InternalSBBException, PolicyViolatedException {
        this.handle(message, role, agreedPolicy, Scope.REQUEST, false);
    }

    /**
     * This method processes responses on the base of the context information.
     * 
     * @param message
     *        the message exchange.
     * @param role
     *        the caller role - initiator/servicer
     * @param agreedPolicy
     *        the agreed policy to process.
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    public void handleResponse(final MessageExchange message, final Role role, final AgreedPolicy agreedPolicy)
            throws InternalSBBException, PolicyViolatedException {
        this.handle(message, role, agreedPolicy, Scope.RESPONSE, false);
    }

    /**
     * Checks if is policy expiry check.
     * 
     * @return true, if is policy expiry check
     */
    public boolean isPolicyExpiryCheck() {
        return this.policyExpiryCheck;
    }

    /**
     * Sets the bean factory.
     * 
     * @param factory
     *        the factory
     * 
     * @throws BeansException
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory
     *      (org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(final BeanFactory factory) throws BeansException {
        this.beanFactory = factory;

    }

    /**
     * Sets the planner.
     * 
     * @param planner
     *        The planner to set.
     */
    public void setPlanner(final Planner planner) {
        this.planner = planner;
    }

    /**
     * Sets the policy expiry check.
     * 
     * @param policyExpiryCheck
     *        the new policy expiry check
     */
    public void setPolicyExpiryCheck(final boolean policyExpiryCheck) {
        this.policyExpiryCheck = policyExpiryCheck;
    }

    /**
     * Sets the policy expiry grace period.
     * 
     * @param policyExpiryGracePeriod
     *        the new policy expiry grace period
     */
    public void setPolicyExpiryGracePeriod(final int policyExpiryGracePeriod) {
        this.policyExpiryGracePeriod = policyExpiryGracePeriod;
    }

    /**
     * Sets the sr cache.
     * 
     * @param srCache
     *        the new sr cache
     */
    public void setSrCache(final SrProxyCache srCache) {
        this.srCache = srCache;
    }

    /**
     * Sets the validator.
     * 
     * @param validator
     *        the new validator
     */
    public void setValidator(final PolicyValidator validator) {
        this.validator = validator;
    }

    /**
     * internal shortcut for debugging.
     * 
     * @param str
     *        what to report
     */
    private void debug(final String str) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(str);
        }
    }

    /**
     * internal shortcut for debugging.
     * 
     * @param str
     *        what to report
     * @param exception
     *        the exception
     */
    private void debug(final String str, final Throwable exception) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(str, exception);
        }
    }

    /**
     * return the current exchange regarding which execution path we are taking, and takes faults
     * into account too.
     * 
     * @param exchange
     *        the exchange containing the message
     * @param scope
     *        current scope
     * 
     * @return a normalized message
     */
    private NormalizedMessage getCurrentNormalizedMessage(final MessageExchange exchange, final Scope scope) {
        if (exchange != null) {
            if (exchange.getFault() != null) return exchange.getFault();
            if (exchange instanceof InOut) {
                if (Scope.REQUEST.equals(scope))
                    return exchange.getMessage("in");
                else
                    return exchange.getMessage("out");
            }
            if (exchange instanceof InOnly) return exchange.getMessage("in");
            return null;
        } else
            return null;

    }

    /**
     * This method processes the messages regardless from which chain side they are comming.
     * 
     * @param message
     *        the message exchange.
     * @param role
     *        the caller role - initiator/servicer
     * @param agreedPolicy
     *        the agreed policy to process.
     * @param scope
     *        the agreement scope (i.e. request or response)
     * @param isFault
     *        indicates if the invocation in this scope is done is behalf of a fault
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     */
    private void handle(final MessageExchange message, final Role role, final AgreedPolicy agreedPolicy, final Scope scope,
            final boolean isFault) throws InternalSBBException, PolicyViolatedException, InternalAuthenticationException,
            InternalAuthorizationException {
        this.debug("handle call in scope " + scope.toString());
        if (message == null)
            throw new InternalInfrastructureException("could not access message exchange in the processing context");

        QName operationQName = message.getOperation();
        if (operationQName == null) throw new InternalInfrastructureException("operation to be called should not be null");

        CallContextExtension ctxe = HeaderUtil.getCallContextExtension(message);
        String partnerOperationName = ctxe.getPartnerOperationName();
        String operation = partnerOperationName != null ? partnerOperationName : operationQName.getLocalPart();
        this.debug("handling operation " + operation.toString());
        // 2. determin the role we are in
        if (role == null) throw new InternalInfrastructureException("invalid role. Should be INITIATOR or SERVICER and not null");
        Role agreementRole = null;

        // switch to the messaging level interpretation of agreements
        if (scope.equals(Scope.RESPONSE)) {
            if (role.equals(Role.RECEIVER)) {
                agreementRole = Role.SENDER;
            } else {
                agreementRole = Role.RECEIVER;
            }
        } else {
            agreementRole = role;
        }
        this.debug("processing role is " + agreementRole.toString());

        // 3. check for agreed policy expiry on request messages
        if (this.isPolicyExpiryCheck() && scope.equals(Scope.REQUEST) && !this.isWithinValidityPeriod(agreedPolicy)) {
            // remove the agreed policy from srcache as it is expired.
            this.srCache.removeAgreedPolicy(agreedPolicy);
            throw new InternalMessagingException("Agreed policy expired, is valid since " + agreedPolicy.validSince() + " till "
                    + agreedPolicy.validThrough());
        }
        Policy operationPolicy = agreedPolicy.getOperationPolicy(operation);
        this.debug("found processing agreement for " + operation);

        // 3a. check validity
        if (Scope.REQUEST.equals(scope) && Role.RECEIVER.equals(role)) {
            this.validator.validate(operationPolicy, operation, agreedPolicy.getProviderPolicyIdentity(), message.getService(),
                    ctxe.getServiceName());
        }

        // 4. get the plan for execution
        List plan = null;
        plan = this.planner.plan(operationPolicy, agreementRole, scope);

        // 5. throw exception in the unlikely event that the planner is unable to create a plan
        if (plan == null) throw new InternalInfrastructureException("no plan how to process this message");
        this.debug("created processing plan " + plan.toString());
        // 6. run through the list of the processing units, lookup and invoke
        // the
        // components THEN release them
        boolean handleException = false;
        Exception exception = null;
        ProcessingComponent cmp = null;
        int cnt = 0;
        while ((cnt < plan.size()) && !handleException) {
            try {
                ProcessingUnit unit = (ProcessingUnit) plan.get(cnt);
                Collection assertions = null;
                ExactlyOne eo = (ExactlyOne) operationPolicy.getTerms().get(0);
                TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
                All all = (All) iterAll.next();
                assertions = new TermCollector(all, new NameSelector(unit.getAssertionName())).collect();
                Object obj = null;
                try {
                    obj = this.beanFactory.getBean(unit.getComponentName());
                } catch (NoSuchBeanDefinitionException nsbde) {
                    throw new InternalInfrastructureException("lookup of " + unit.getComponentName()
                            + " failed. This indicates a missconfiguration", nsbde);
                } catch (BeansException be) {
                    throw new InternalInfrastructureException("lookup of " + unit.getComponentName()
                            + " failed. Not able to get bean from the bean factory.", be);
                }

                if (null == obj)
                    throw new InternalInfrastructureException("lookup of " + unit.getComponentName()
                            + "returned a null component. This indicates a missconfiguration");
                if (!(obj instanceof ProcessingComponent))
                    throw new InternalInfrastructureException("component " + unit.getComponentName()
                            + " does not implement the right interface contract");

                cmp = (ProcessingComponent) obj;

                /*
                 * first check whether the processing component can really handle the assertion
                 */
                if (!cmp.canHandle(assertions)) {
                    cnt++;
                    continue;
                }

                /*
                 * TODO This is a simplistic implementation of preseting the content of a message in
                 * a desired Format to the component. We will have situations where we might keep an
                 * older message representation that is still valid.
                 */
                NormalizedMessage nm = this.getCurrentNormalizedMessage(message, scope);

                if (!cmp.supportSource(nm.getContent().getClass())) {
                    Source newSource = this.optimizeRepresentation(nm.getContent(), cmp);
                    try {
                        nm.setContent(newSource);
                    } catch (MessagingException e) {
                        throw new InfrastructureRuntimeException("cannot change message representation while "
                                + "preparing message for processing component " + unit.getComponentName());
                    }
                }

                // finally invoke the component
                if (Scope.REQUEST.equals(scope)) {
                    // call handle request
                    if (isFault) {
                        this.debug("about to invoke handleFault for " + cmp.getClass().getName() + " in request scope");
                        cmp.handleFault(message, role, assertions);
                    } else {
                        this.debug("about to invoke handleRequest for " + cmp.getClass().getName());
                        cmp.handleRequest(message, role, assertions);
                    }
                } else {
                    if (isFault) {
                        this.debug("about to invoke handleFault for " + cmp.getClass().getName() + " in response scope");
                        cmp.handleFault(message, role, assertions);
                    } else {
                        // call handle response
                        this.debug("about to invoke handleResponse for " + cmp.getClass().getName());
                        cmp.handleResponse(message, role, assertions);
                    }
                }
                cnt++;
            } catch (InternalAuthenticationException e) {
                this.debug("caught authentication exception while dispatching ", e);
                exception = e;
                handleException = true;
            } catch (InternalAuthorizationException e) {
                this.debug("caught authorization exception while dispatching ", e);
                exception = e;
                handleException = true;
            } catch (InternalSBBException e) {
                this.debug("caught InternalSBB exception while dispatching ", e);
                exception = e;
                handleException = true;
            } catch (PolicyViolatedException e) {
                this.debug("caught policy violation exception while dispatching ", e);
                exception = e;
                handleException = true;
            }
        }
        // 7. we are done with the processing so check exception conditions
        if (handleException) {
            this.debug("enetering exception handling case");
            // go through the list of successfully invoked components and make
            // them to handle fault
            // the component causing the exception is not invoked in this
            // process
            for (int i = cnt - 1; i >= 0; i--) {
                // 6.a- run back the chain and invoke the fault method
                try {
                    ProcessingUnit unit = (ProcessingUnit) plan.get(cnt);
                    Collection assertions = null;
                    ExactlyOne eo = (ExactlyOne) operationPolicy.getTerms().get(0);
                    TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
                    All all = (All) iterAll.next();
                    assertions = new TermCollector(all, new NameSelector(unit.getAssertionName())).collect();
                    this.debug("about to invoke handleFault for " + cmp.getClass().getName());
                    cmp = (ProcessingComponent) this.beanFactory.getBean(unit.getComponentName());
                    cmp.handleFault(message, role, assertions);
                } catch (Throwable e) {
                    this.debug("handleFault for " + cmp.getClass().getName() + " blasted with " + e + " continuing faultHandling",
                            e);
                }
            }
            // 7.b - we ran through the fault chain, now we must throw the
            // previously catched exception
            this.debug("rethrowing prevously caught exception", exception);
            if (exception instanceof InternalAuthenticationException)
                throw (InternalAuthenticationException) exception;
            else if (exception instanceof InternalAuthorizationException)
                throw (InternalAuthorizationException) exception;
            else if (exception instanceof InternalSBBException)
                throw (InternalSBBException) exception;
            else if (exception instanceof PolicyViolatedException)
                throw (PolicyViolatedException) exception;
            else {
                this.debug("received an unexpected exception");
                throw new InternalFatalException(exception);
            }
        }
    }

    /**
     * Checks if is within validity period.
     * 
     * @param agreedPolicy
     *        the agreed policy
     * 
     * @return true, if is within validity period
     */
    private boolean isWithinValidityPeriod(final AgreedPolicy agreedPolicy) {
        Date validSince = agreedPolicy.validSince();
        Date validThrough = agreedPolicy.validThrough();
        Date current = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
        if (LOG.isDebugEnabled()) {
            DateFormat datePrintFormatter;
            datePrintFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            LOG.debug("Checking Expiry with following dates");
            LOG.debug("Using grace period " + this.getPolicyExpiryGracePeriod() + " seconds");
            LOG.debug("-----------------------------------------");
            LOG.debug("validSince is " + datePrintFormatter.format(validSince));
            LOG.debug("current is " + datePrintFormatter.format(current));
            LOG.debug("validThrough is " + datePrintFormatter.format(validThrough));
            LOG.debug("-----------------------------------------");
        }
        boolean validSinceOK = validSince.before(current);
        boolean validThroughOK = validThrough.after(current);

        // if the current date is earlier than the validSince date.
        if (!validSinceOK) {
            if (((validSince.getTime() - current.getTime()) / 1000) <= this.getPolicyExpiryGracePeriod()) {
                LOG.info("Using clearance and resulting true");
                validSinceOK = true;
            }
        }
        // if the current is later than the validThrough date
        if (!validThroughOK) {
            if (((current.getTime() - validThrough.getTime()) / 1000) <= this.getPolicyExpiryGracePeriod()) {
                LOG.info("Using clearance and resulting true");
                validThroughOK = true;
            }
        }
        return (validSinceOK && validThroughOK);
    }

    /**
     * Optimize representation.
     * 
     * @param src
     *        the src
     * @param cmp
     *        the cmp
     * 
     * @return the source
     */
    private Source optimizeRepresentation(final Source src, final ProcessingComponent cmp) {
        if (cmp.supportSource(src.getClass()))
            // sollte eigentlich nie auftretten, aber wer weiss das schon :-))
            return src;
        else {
            // hoffentlich optimale Reihenfolge bei eingehender DOMSource
            if (src instanceof DOMSource) {
                if (cmp.supportSource(SAXSource.class)) {
                    String str = XMLUtil.stringFromDom(((DOMSource) src).getNode().getOwnerDocument());
                    InputSource is = new InputSource(new StringReader(str));
                    return new SAXSource(is);
                }

                if (cmp.supportSource(StreamSource.class)) {
                    Node node = ((DOMSource) src).getNode();
                    if (node instanceof Document) {
                        InputStream is = XMLUtil.inputStreamFromDom((Document) node);
                        // String str = XMLUtil.stringFromDom((Document)node);
                        return new StreamSource(is);
                        // return new StreamSource(new StringReader(str));
                    } else
                        throw new InfrastructureRuntimeException("Cannot transform DOMsource to StreamSource");
                }
            }
            // hoffentlich optimale Reihenfolge bei eingehender StreamSource
            if (src instanceof StreamSource) {
                if (cmp.supportSource(SAXSource.class)) {
                    InputSource is = new InputSource(((StreamSource) src).getInputStream());
                    return new SAXSource(is);
                }

                if (cmp.supportSource(DOMSource.class)) {
                    Document doc;
                    try {
                        doc = XMLUtil.docFromInputStream(((StreamSource) src).getInputStream());
                    } catch (SAXException e) {
                        throw new InfrastructureRuntimeException("cannot transform stream source to DOMsource ", e);
                    } catch (IOException e) {
                        throw new InfrastructureRuntimeException("cannot transform stream source to DOMsource ", e);
                    }
                    return new DOMSource(doc);
                }
            }
            // hoffentlich optimale Reihenfolge bei eingehender SAXSource
            if (src instanceof SAXSource) {
                if (cmp.supportSource(StreamSource.class))
                    return new StreamSource(((SAXSource) src).getInputSource().getByteStream());

                if (cmp.supportSource(DOMSource.class)) return XMLUtil.domSourceFromSAXSource((SAXSource) src);
            }
        }
        throw new InfrastructureRuntimeException("Unknown type of message source " + src.getClass().getName());
    }
}
