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
package org.eclipse.swordfish.policytrader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.DOMPolicyReader;
import org.apache.ws.policy.util.PolicyFactory;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A factory for creating AgreedPolicy objects.
 */
public class AgreedPolicyFactory {

    /** The Constant log. */
    private static final Log LOG = LogFactory.getLog(AgreedPolicyFactory.class);

    /** The instance. */
    private static AgreedPolicyFactory instance;

    /** The Constant dbf. */
    private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();

    /** The Constant SHORT_DATE_PATTERN. */
    private static final String SHORT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /** The Constant LONG_DATE_PATTERN. */
    private static final String LONG_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'";

    /** The Constant reader. */
    private static final DOMPolicyReader READER = (DOMPolicyReader) PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);

    static {
        DBF.setNamespaceAware(true);
        DBF.setValidating(false);
    }

    /**
     * Gets the single instance of AgreedPolicyFactory.
     * 
     * @return single instance of AgreedPolicyFactory
     * 
     * @throws ParserConfigurationException
     */
    public synchronized static AgreedPolicyFactory getInstance() throws ParserConfigurationException {
        if (null == instance) {
            instance = new AgreedPolicyFactory();
        }
        return instance;
    }

    /** The db. */
    private DocumentBuilder db;

    /**
     * Instantiates a new agreed policy factory.
     * 
     * @throws ParserConfigurationException
     */
    private AgreedPolicyFactory() throws ParserConfigurationException {
        this.db = DBF.newDocumentBuilder();
    }

    /**
     * Creates a new AgreedPolicy object.
     * 
     * @param src
     *        the src
     * 
     * @return the agreed policy
     */
    public AgreedPolicy createFrom(final AbstractAgreedPolicy src) {
        return new InstantiatedAgreedPolicy(src);
    }

    /**
     * Creates a new AgreedPolicy object.
     * 
     * @param root
     *        the root
     * 
     * @return the agreed policy
     */
    public AgreedPolicy createFrom(final Element root) {
        AgreedPolicyAttribs attribs = new AgreedPolicyAttribs(root);
        attribs.getOperationPolicies().putAll(this.getOperationPolicies(root));
        AgreedPolicy ret = new InstantiatedAgreedPolicy(attribs);
        return ret;
    }

    /**
     * Creates a new AgreedPolicy object.
     * 
     * @param is
     *        the is
     * 
     * @return the agreed policy
     * 
     * @throws SAXException
     * @throws IOException
     */
    public AgreedPolicy createFrom(final InputStream is) throws SAXException, IOException {
        Document doc = this.db.parse(is);
        Element element = doc.getDocumentElement();
        return this.createFrom(element);
    }

    /**
     * Gets the long formatter.
     * 
     * @return the long formatter
     */
    private DateFormat getLongFormatter() {
        DateFormat formatter = new SimpleDateFormat(LONG_DATE_PATTERN);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter;
    }

    /**
     * Gets the operation policies.
     * 
     * @param root
     *        the root
     * 
     * @return the operation policies
     */
    private Map getOperationPolicies(final Element root) {
        NodeList operations = root.getElementsByTagName("Operation");
        Map ret = new HashMap(operations.getLength());
        for (int i = 0; i < operations.getLength(); i++) {
            Element operationElement = (Element) operations.item(i);
            Node child = operationElement.getFirstChild();
            while ((null != child) && !(child instanceof Element)) {
                child = child.getNextSibling();
            }
            if (null == child) {
                LOG.warn("Corrupted AgreedPolicy - no ws-policy as child for operation " + operationElement.getAttribute("name"));
                continue;
            }
            Element policyElement = (Element) child;
            if (!OperationPolicy.WSP_POLICY_TAG.equals(policyElement.getLocalName())
                    || !OperationPolicy.WSP_NAMESPACE_URI.equals(policyElement.getNamespaceURI())) {
                LOG.warn("Corrupted AgreedPolicy - child for operation " + operationElement.getAttribute("name")
                        + " is not <wsp:Policy>");
                continue;
            }
            Policy operationPolicy = READER.readPolicy(policyElement);
            String operationName = operationElement.getAttribute(AgreedPolicy.OPERATION_POLICY_NAME_ATT);
            ret.put(operationName, operationPolicy);
        }
        return ret;
    }

    /**
     * Gets the short formatter.
     * 
     * @return the short formatter
     */
    private DateFormat getShortFormatter() {
        DateFormat formatter = new SimpleDateFormat(SHORT_DATE_PATTERN);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter;
    }

    /**
     * The Class AgreedPolicyAttribs.
     */
    private class AgreedPolicyAttribs {

        /** Consumer-side participant policy identifier. */
        private StandardParticipantPolicyIdentity consumerPID;

        /** Provider-side participant policy identifier. */
        private StandardParticipantPolicyIdentity providerPID;

        /** String representation of service qualified name. */
        private String service;

        /** String representation of provider qualified name. */
        private String provider;

        /** The operation policies. */
        private Map operationPolicies;

        /** The valid since. */
        private String validSince;

        /** The valid through. */
        private String validThrough;

        /**
         * Instantiates a new agreed policy attribs.
         */
        public AgreedPolicyAttribs() {
            this.operationPolicies = new HashMap();
        }

        /**
         * Instantiates a new agreed policy attribs.
         * 
         * @param root
         *        the root
         */
        public AgreedPolicyAttribs(final Element root) {
            this.operationPolicies = new HashMap();
            String val = root.getAttribute(AgreedPolicy.AGREED_POLICY_CONSUMER_POLICY_ID_ATT);
            this.consumerPID = new StandardParticipantPolicyIdentity(val);
            val = root.getAttribute(AgreedPolicy.AGREED_POLICY_PROVIDER_POLICY_ID_ATT);
            this.providerPID = new StandardParticipantPolicyIdentity(val);
            this.provider = root.getAttribute(AgreedPolicy.AGREED_POLICY_SERVICE_PROVIDER_ATT);
            this.service = root.getAttribute(AgreedPolicy.AGREED_POLICY_SERVICE_ATT);
            this.validSince = root.getAttribute(AgreedPolicy.AGREED_POLICY_VALID_SINCE_ATT);
            this.validThrough = root.getAttribute(AgreedPolicy.AGREED_POLICY_VALID_THROUGH_ATT);
        }

        /**
         * Instantiates a new agreed policy attribs.
         * 
         * @param consumerPID
         *        the consumer PID
         * @param providerPID
         *        the provider PID
         * @param service
         *        the service
         * @param provider
         *        the provider
         */
        public AgreedPolicyAttribs(final StandardParticipantPolicyIdentity consumerPID,
                final StandardParticipantPolicyIdentity providerPID, final String service, final String provider) {
            this.consumerPID = consumerPID;
            this.providerPID = providerPID;
            this.service = service;
            this.provider = provider;
            this.operationPolicies = new HashMap();
        }

        /**
         * Adds the operation policy.
         * 
         * @param name
         *        the name
         * @param operationPolicy
         *        the operation policy
         */
        public void addOperationPolicy(final String name, final OperationPolicyImpl operationPolicy) {
            this.operationPolicies.put(name, operationPolicy);

        }

        /**
         * Gets the consumer PID.
         * 
         * @return the consumer PID
         */
        public StandardParticipantPolicyIdentity getConsumerPID() {
            return this.consumerPID;
        }

        /**
         * Gets the operation policies.
         * 
         * @return the operation policies
         */
        public Map getOperationPolicies() {
            return this.operationPolicies;
        }

        /**
         * Gets the provider.
         * 
         * @return the provider
         */
        public String getProvider() {
            return this.provider;
        }

        /**
         * Gets the provider PID.
         * 
         * @return the provider PID
         */
        public StandardParticipantPolicyIdentity getProviderPID() {
            return this.providerPID;
        }

        /**
         * Gets the service.
         * 
         * @return the service
         */
        public String getService() {
            return this.service;
        }

        /**
         * Gets the valid since.
         * 
         * @return the valid since
         */
        public String getValidSince() {
            return this.validSince;
        }

        /**
         * Gets the valid through.
         * 
         * @return the valid through
         */
        public String getValidThrough() {
            return this.validThrough;
        }

        /**
         * Sets the consumer PID.
         * 
         * @param consumerPID
         *        the new consumer PID
         */
        public void setConsumerPID(final StandardParticipantPolicyIdentity consumerPID) {
            this.consumerPID = consumerPID;
        }

        /**
         * Sets the provider.
         * 
         * @param provider
         *        the new provider
         */
        public void setProvider(final String provider) {
            this.provider = provider;
        }

        /**
         * Sets the provider PID.
         * 
         * @param providerPID
         *        the new provider PID
         */
        public void setProviderPID(final StandardParticipantPolicyIdentity providerPID) {
            this.providerPID = providerPID;
        }

        /**
         * Sets the service.
         * 
         * @param service
         *        the new service
         */
        public void setService(final String service) {
            this.service = service;
        }

        /**
         * Sets the valid since.
         * 
         * @param validFrom
         *        the new valid since
         */
        public void setValidSince(final String validFrom) {
            this.validSince = validFrom;
        }

        /**
         * Sets the valid through.
         * 
         * @param validThrough
         *        the new valid through
         */
        public void setValidThrough(final String validThrough) {
            this.validThrough = validThrough;
        }

    }

    /**
     * The Class InstantiatedAgreedPolicy.
     */
    private class InstantiatedAgreedPolicy extends AbstractAgreedPolicy {

        /**
         * Instantiates a new instantiated agreed policy.
         * 
         * @param src
         *        the src
         */
        public InstantiatedAgreedPolicy(final AbstractAgreedPolicy src) {
            super(src);
        }

        /**
         * Instantiates a new instantiated agreed policy.
         * 
         * @param attribs
         *        the attribs
         */
        public InstantiatedAgreedPolicy(final AgreedPolicyAttribs attribs) {
            super(attribs.getConsumerPID(), attribs.getProviderPID(), attribs.getService(), attribs.getProvider(), attribs
                .getOperationPolicies());
            long currentTime = System.currentTimeMillis();
            Date validSince = new Date(currentTime);
            Date validThrough = new Date(currentTime + getValidityDuration());
            String value = attribs.getValidSince();
            if (null != value) {
                try {
                    validSince = this.convertDate(value);
                } catch (ParseException e) {
                    LOG.warn("Could not parse date from attribute validSince=" + value + " in agreed policy.");
                }
            }
            value = attribs.getValidThrough();
            if (null != value) {
                try {
                    validThrough = this.convertDate(value);
                } catch (ParseException e) {
                    LOG.warn("Could not parse date from attribute validThrough=" + value + " in agreed policy.");
                }
            }
            this.setValid(validSince, validThrough);
        }

        /**
         * Convert date.
         * 
         * @param value
         *        the value
         * 
         * @return the date
         * 
         * @throws ParseException
         */
        private synchronized Date convertDate(final String value) throws ParseException {
            if (-1 == value.indexOf('.')) // date string without milliseconds
                return AgreedPolicyFactory.this.getShortFormatter().parse(value);
            else
                // date string with milliseconds
                return AgreedPolicyFactory.this.getLongFormatter().parse(value);
        }

    }

}
