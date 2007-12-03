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

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.StAXPolicyWriter;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.w3c.dom.Document;

/**
 * Base class for agreed policies.
 */
public abstract class AbstractAgreedPolicy implements AgreedPolicy {

    /** The Constant DEFAULT_VALIDITY_DURATION. */
    public static final long DEFAULT_VALIDITY_DURATION = 3600000;

    /** Duration in ms that an agreed policy should remain valid after creation. */
    private static long validityDuration = DEFAULT_VALIDITY_DURATION;

    /**
     * Gets the validity duration.
     * 
     * @return Returns the validityDuration.
     */
    public static long getValidityDuration() {
        return validityDuration;
    }

    /**
     * Sets the validity duration.
     * 
     * @param validityDuration
     *        The validityDuration to set.
     */
    public static void setValidityDuration(final long validityDuration) {
        AbstractAgreedPolicy.validityDuration = validityDuration;
    }

    /** Consumer-side participant policy identifier. */
    private final StandardParticipantPolicyIdentity consumerPID;

    /** Provider-side participant policy identifier. */
    private final StandardParticipantPolicyIdentity providerPID;

    /** String representation of service qualified name. */
    private final String service;

    /** String representation of provider qualified name. */
    private String provider;

    /** Validity start date. */
    private Date validSince;

    /** Validity end date. */
    private Date validThrough;

    /** Traded policies mapped to operation names. */
    private Map operationPolicies;

    /**
     * Copy constructor creating a read-only copy.
     * 
     * @param src
     *        from where to copy
     */
    protected AbstractAgreedPolicy(final AbstractAgreedPolicy src) {
        super();
        this.consumerPID = src.consumerPID;
        this.providerPID = src.providerPID;
        this.service = src.getService();
        this.provider = src.getProvider();
        this.validSince = src.validSince();
        this.validThrough = src.validThrough();
        this.operationPolicies = Collections.unmodifiableMap(src.operationPolicies);
    }

    /**
     * Internally used constructor for result of policy trading.
     * 
     * @param consumerPID
     *        consumer-side participant policy identifier
     * @param providerPID
     *        provider-side participant policy identifier
     * @param operationPolicies
     *        map of traded operation policies which has been set up completely
     * @param service
     *        the service
     * @param provider
     *        the provider
     */
    protected AbstractAgreedPolicy(final StandardParticipantPolicyIdentity consumerPID,
            final StandardParticipantPolicyIdentity providerPID, final String service, final String provider,
            final Map operationPolicies) {
        super();
        this.consumerPID = consumerPID;
        this.providerPID = providerPID;
        this.service = service;
        this.provider = provider;
        this.operationPolicies = operationPolicies;
        long currentTime = System.currentTimeMillis();
        this.validSince = new Date(currentTime);
        this.validThrough = new Date(currentTime + validityDuration);
    }

    /**
     * Get the consumer-side participant policy identifier.
     * 
     * @return participant policy identifier
     */
    public StandardParticipantPolicyIdentity getConsumerPID() {
        return this.consumerPID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getConsumerPolicyIdentity()
     */
    public ParticipantPolicyIdentity getConsumerPolicyIdentity() {
        return this.consumerPID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getDefaultOperationPolicy()
     */
    public Policy getDefaultOperationPolicy() {
        return (Policy) this.operationPolicies.get(ParticipantPolicy.ANY_OPERATION);
    }

    /**
     * Get the map of traded policies.
     * 
     * @return traded policies mapped to operations
     */
    public Map getOperationPolicies() {
        return this.operationPolicies;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getOperationPolicy(java.lang.String)
     */
    public Policy getOperationPolicy(final String operation) {
        return (Policy) this.operationPolicies.get(operation);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getProvider()
     */
    public String getProvider() {
        return this.provider;
    }

    /**
     * Get the provider-side participant policy identifier.
     * 
     * @return participant policy identifier
     */
    public StandardParticipantPolicyIdentity getProviderPID() {
        return this.providerPID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getProviderPolicyIdentity()
     */
    public ParticipantPolicyIdentity getProviderPolicyIdentity() {
        return this.providerPID;
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeClassicTo(javax.xml.stream.XMLStreamWriter)
     */
    /*
     * public void writeClassicTo(final XMLStreamWriter staxWriter) throws XMLStreamException { //
     * staxWriter.writeStartDocument(); staxWriter.writeStartElement("", AGREED_POLICY_TAG,
     * AGREED_POLICY_CLASSIC_NAMESPACE);
     * staxWriter.writeDefaultNamespace(AGREED_POLICY_CLASSIC_NAMESPACE);
     * staxWriter.setDefaultNamespace(AGREED_POLICY_CLASSIC_NAMESPACE);
     * staxWriter.writeNamespace("sopa", CLASSIC_ASSERTION_NAMESPACE); staxWriter.setPrefix("sopa",
     * CLASSIC_ASSERTION_NAMESPACE); staxWriter.writeAttribute(AGREED_POLICY_CONSUMER_POLICY_ID_ATT,
     * consumerPID.getKeyName()); staxWriter.writeAttribute(AGREED_POLICY_PROVIDER_POLICY_ID_ATT,
     * providerPID.getKeyName()); if (null != service) {
     * staxWriter.writeAttribute(AGREED_POLICY_SERVICE_ATT, service); } if (null != provider) {
     * staxWriter.writeAttribute(AGREED_POLICY_SERVICE_PROVIDER_ATT, provider); } if (null !=
     * validSince) { staxWriter.writeAttribute(AGREED_POLICY_VALID_SINCE_ATT, convert(validSince)); }
     * if (null != validThrough) { staxWriter.writeAttribute(AGREED_POLICY_VALID_THROUGH_ATT,
     * convert(validThrough)); } staxWriter.writeAttribute(AGREED_POLICY_ID_ATT, consumerPID
     * .getKeyName() + "^" + providerPID.getKeyName()); for (Iterator i =
     * operationPolicies.entrySet().iterator(); i.hasNext();) { final Map.Entry entry = (Map.Entry)
     * i.next(); final String opName = (String) entry.getKey(); final Policy opPolicy = (Policy)
     * entry.getValue(); if (ParticipantPolicy.ANY_OPERATION.equals(opName)) {
     * staxWriter.writeStartElement(AGREED_POLICY_CLASSIC_NAMESPACE, DEFAULT_OPERATION_POLICY_TAG); }
     * else { staxWriter.writeStartElement(AGREED_POLICY_CLASSIC_NAMESPACE, OPERATION_POLICY_TAG);
     * staxWriter.writeAttribute(OPERATION_POLICY_NAME_ATT, opName); } if
     * (ParticipantPolicy.EMPTY_POLICY == opPolicy) {
     * staxWriter.writeEmptyElement(AGREED_POLICY_CLASSIC_NAMESPACE, UNUSED_OPERATION_TAG); } else {
     * getClassicOperationPolicy(opName, opPolicy).writeTo(staxWriter); }
     * staxWriter.writeEndElement(); } staxWriter.writeEndElement(); // </Agreed> //
     * staxWriter.writeEndDocument(); }
     */

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeClassicTo(java.io.OutputStream)
     */
    /*
     * public void writeClassicTo(final OutputStream output) { XMLStreamWriter writer = null; try {
     * writer = XMLOutputFactory.newInstance() .createXMLStreamWriter(output);
     * writeClassicTo(writer); writer.flush(); } catch (XMLStreamException ex) { throw new
     * RuntimeException(ex); } }
     */

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeClassicTo(org.w3c.dom.Document)
     */
    /*
     * public void writeClassicTo(final Document document) { final XMLStreamToDOMWriter writer = new
     * XMLStreamToDOMWriter(document); try { writeClassicTo(writer); writer.flush(); } catch
     * (XMLStreamException ex) { throw new RuntimeException(ex); } }
     */

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getReducedAgreedPolicy(java.lang.String)
     */
    public AgreedPolicy getReducedAgreedPolicy(final String operation) {
        AgreedPolicyFactory factory = null;
        AgreedPolicy ret = null;
        try {
            factory = AgreedPolicyFactory.getInstance();
            ret = factory.createFrom(this);
            HashMap newPolicies = new HashMap(1);
            Object obj = this.operationPolicies.get(operation);
            Policy singlePolicy = (Policy) obj;
            if (null != singlePolicy) {
                newPolicies.put(operation, singlePolicy);
            }
            if (ret instanceof AbstractAgreedPolicy) {
                AbstractAgreedPolicy realRet = (AbstractAgreedPolicy) ret;
                realRet.operationPolicies = newPolicies;
            } else {
                // TODO add error handling
                ret = null;
            }
        } catch (ParserConfigurationException e) {
            // TODO add error handling
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getService()
     */
    public String getService() {
        return this.service;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#setProvider(java.lang.String)
     */
    public void setProvider(final String serviceProviderId) {
        this.provider = serviceProviderId;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#setValid(java.util.Date, java.util.Date)
     */
    public void setValid(final Date since, final Date through) {
        this.validSince = since;
        this.validThrough = through;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#validSince()
     */
    public Date validSince() {
        return this.validSince;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#validThrough()
     */
    public Date validThrough() {
        return this.validThrough;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeTo(org.w3c.dom.Document)
     */
    public void writeTo(final Document document) {
        final XMLStreamToDOMWriter writer = new XMLStreamToDOMWriter(document);
        try {
            this.writeTo(writer);
            writer.flush();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeTo(java.io.OutputStream)
     */
    public void writeTo(final OutputStream output) {
        XMLStreamWriter writer = null;
        try {
            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
            this.writeTo(writer);
            writer.flush();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    public void writeTo(final XMLStreamWriter staxWriter) throws XMLStreamException {
        // staxWriter.writeStartDocument();
        staxWriter.writeStartElement("", AGREED_POLICY_TAG, AGREED_POLICY_NAMESPACE);
        staxWriter.writeDefaultNamespace(AGREED_POLICY_NAMESPACE);
        staxWriter.writeNamespace("soptemp", SOP_ASSERTION_TEMPORARY_URI);
        // staxWriter.setPrefix("sopa", ASSERTION_NAMESPACE);
        // staxWriter.setPrefix("wsp", OperationPolicy.WSP_NAMESPACE_URI);
        staxWriter.setDefaultNamespace(AGREED_POLICY_NAMESPACE);
        staxWriter.writeAttribute(AGREED_POLICY_CONSUMER_POLICY_ID_ATT, this.consumerPID.getKeyName());
        staxWriter.writeAttribute(AGREED_POLICY_PROVIDER_POLICY_ID_ATT, this.providerPID.getKeyName());
        if (null != this.service) {
            staxWriter.writeAttribute(AGREED_POLICY_SERVICE_ATT, this.service);
        }
        if (null != this.provider) {
            staxWriter.writeAttribute(AGREED_POLICY_SERVICE_PROVIDER_ATT, this.provider);
        }
        if (null != this.validSince) {
            staxWriter.writeAttribute(AGREED_POLICY_VALID_SINCE_ATT, this.convert(this.validSince));
        }
        if (null != this.validThrough) {
            staxWriter.writeAttribute(AGREED_POLICY_VALID_THROUGH_ATT, this.convert(this.validThrough));
        }
        final StAXPolicyWriter polWriter = new StAXPolicyWriter();
        for (Iterator i = this.operationPolicies.entrySet().iterator(); i.hasNext();) {
            final Map.Entry entry = (Map.Entry) i.next();
            final String opName = (String) entry.getKey();
            final Policy opPolicy = (Policy) entry.getValue();
            if (ParticipantPolicy.ANY_OPERATION.equals(opName)) {
                staxWriter.writeStartElement(AGREED_POLICY_NAMESPACE, DEFAULT_OPERATION_POLICY_TAG);
            } else {
                staxWriter.writeStartElement(AGREED_POLICY_NAMESPACE, OPERATION_POLICY_TAG);
                staxWriter.writeAttribute(OPERATION_POLICY_NAME_ATT, opName);
            }
            if (ParticipantPolicy.EMPTY_POLICY != opPolicy) {
                // don't write unused operations to agreed policy - as discussed
                // with OWO
                polWriter.writePolicy(opPolicy, staxWriter);
            }
            staxWriter.writeEndElement();
        }
        staxWriter.writeEndElement();
        // staxWriter.writeEndDocument();
    }

    /**
     * Helper method.
     * 
     * @param date
     *        Date to be converted to standard date String
     * 
     * @return standard date String
     */
    private String convert(final Date date) {
        // TODO smarter formatting
        final Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.US);
        cal.setTime(date);
        final StringBuffer res = new StringBuffer();
        res.append(cal.get(Calendar.YEAR));
        res.append('-');
        int tmp = cal.get(Calendar.MONTH) + 1;
        if (tmp < 10) {
            res.append('0');
        }
        res.append(tmp);
        res.append('-');
        tmp = cal.get(Calendar.DAY_OF_MONTH);
        if (tmp < 10) {
            res.append('0');
        }
        res.append(tmp);
        res.append('T');
        tmp = cal.get(Calendar.HOUR_OF_DAY);
        if (tmp < 10) {
            res.append('0');
        }
        res.append(tmp);
        res.append(':');
        tmp = cal.get(Calendar.MINUTE);
        if (tmp < 10) {
            res.append('0');
        }
        res.append(tmp);
        res.append(':');
        tmp = cal.get(Calendar.SECOND);
        if (tmp < 10) {
            res.append('0');
        }
        res.append(tmp);
        res.append('Z');
        return res.toString();
    }
}
