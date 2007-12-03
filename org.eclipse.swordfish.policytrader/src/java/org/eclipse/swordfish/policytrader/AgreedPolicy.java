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
package org.eclipse.swordfish.policytrader;

import java.io.OutputStream;
import java.util.Date;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.ws.policy.Policy;
import org.w3c.dom.Document;

/**
 * Wrapper object for agreed policies which allows to obtain the agreed policy in different formats.
 */
public interface AgreedPolicy {

    /** Empty WS Policy object for the trading result of "unused operation". */
    Policy EMPTY_POLICY = ParticipantPolicy.EMPTY_POLICY;

    /** Void agreed policy for failed agreements. */
    AgreedPolicy FAILED_AGREEMENT_POLICY = ParticipantPolicy.FAILED_AGREEMENT_POLICY;

    /** The AGREE d_ POLIC y_ TAG. */
    String AGREED_POLICY_TAG = "Agreed";

    /** The AGREE d_ POLIC y_ NAMESPACE. */
    String AGREED_POLICY_NAMESPACE = "http://types.sopware.org/qos/AgreedPolicy/2.0";

    /** The AGREE d_ POLIC y_ CLASSI c_ NAMESPACE. */
    String AGREED_POLICY_CLASSIC_NAMESPACE = "http://types.sopware.org/qos/AgreedPolicy/1.0";

    /** The ASSERTIO n_ NAMESPACE. */
    String ASSERTION_NAMESPACE = "http://types.sopware.org/qos/SOPAssertions/1.1";

    /**
     * URI for temporary sop attributes in ws policies This is a workaround for defect 3364 TODO:
     * remove once 3364 is fixed.
     */
    String SOP_ASSERTION_TEMPORARY_URI = org.eclipse.swordfish.policy.PolicyConstants.SOP_ASSERTION_TEMPORARY_URI;

    /** The CLASSI c_ ASSERTIO n_ NAMESPACE. */
    String CLASSIC_ASSERTION_NAMESPACE = "http://types.sopware.org/qos/SOPAssertions/1.0";

    /** The AGREE d_ POLIC y_ CONSUME r_ POLIC y_ I d_ ATT. */
    String AGREED_POLICY_CONSUMER_POLICY_ID_ATT = "consumerPolicy";

    /** The AGREE d_ POLIC y_ PROVIDE r_ POLIC y_ I d_ ATT. */
    String AGREED_POLICY_PROVIDER_POLICY_ID_ATT = "providerPolicy";

    /** The AGREE d_ POLIC y_ SERVIC e_ ATT. */
    String AGREED_POLICY_SERVICE_ATT = "service";

    /** The AGREE d_ POLIC y_ SERVIC e_ PROVIDE r_ ATT. */
    String AGREED_POLICY_SERVICE_PROVIDER_ATT = "serviceProvider";

    /** The AGREE d_ POLIC y_ VALI d_ SINC e_ ATT. */
    String AGREED_POLICY_VALID_SINCE_ATT = "validSince";

    /** The AGREE d_ POLIC y_ VALI d_ THROUG h_ ATT. */
    String AGREED_POLICY_VALID_THROUGH_ATT = "validThrough";

    /** The AGREE d_ POLIC y_ I d_ ATT. */
    String AGREED_POLICY_ID_ATT = "id";

    /** The OPERATIO n_ POLIC y_ TAG. */
    String OPERATION_POLICY_TAG = "Operation";

    /** The OPERATIO n_ POLIC y_ NAM e_ ATT. */
    String OPERATION_POLICY_NAME_ATT = "name";

    /** The DEFAUL t_ OPERATIO n_ POLIC y_ TAG. */
    String DEFAULT_OPERATION_POLICY_TAG = "DefaultOperation";

    /** The UNUSE d_ OPERATIO n_ TAG. */
    String UNUSED_OPERATION_TAG = "unused";

    /**
     * Gets the consumer policy identity.
     * 
     * @return the consumer policy identity
     */
    ParticipantPolicyIdentity getConsumerPolicyIdentity();

    /**
     * Gets the default operation policy.
     * 
     * @return the default operation policy
     */
    Policy getDefaultOperationPolicy();

    /**
     * Gets the operation policy.
     * 
     * @param operation
     *        the operation
     * 
     * @return the operation policy
     */
    Policy getOperationPolicy(String operation);

    /**
     * Gets the provider.
     * 
     * @return the provider
     */
    String getProvider();

    /**
     * Gets the provider policy identity.
     * 
     * @return the provider policy identity
     */
    ParticipantPolicyIdentity getProviderPolicyIdentity();

    /**
     * creates a clone of the agreed policy that only contains the operation policy for the
     * specified operation.
     * 
     * @param operation
     *        the operation
     * 
     * @return the reduced agreed policy
     */
    AgreedPolicy getReducedAgreedPolicy(String operation);

    /**
     * Gets the service.
     * 
     * @return the service
     */
    String getService();

    /**
     * Sets the provider.
     * 
     * @param providerId
     *        the new provider
     */
    void setProvider(String providerId);

    /**
     * Sets the valid.
     * 
     * @param since
     *        the since
     * @param through
     *        the through
     */
    void setValid(Date since, Date through);

    /**
     * Valid since.
     * 
     * @return the date
     */
    Date validSince();

    /**
     * Valid through.
     * 
     * @return the date
     */
    Date validThrough();

    /**
     * Write the receiver as XML to an empty DOM Document.
     * 
     * @param document
     *        empty DOM document with namespace support enabled
     */
    void writeTo(Document document);

    /**
     * Write the receiver as XML to a stream.
     * 
     * @param output
     *        stream to which the receiver is written
     */
    void writeTo(OutputStream output);

    /**
     * Write the receiver as XML to a StAX writer.
     * 
     * @param writer
     *        StAX writer to which the receiver is written
     * 
     * @throws XMLStreamException
     */
    void writeTo(XMLStreamWriter writer) throws XMLStreamException;

    /**
     * Write the receiver in classic format as XML to a stream.
     * 
     * @param output
     *        stream to which the receiver is written
     */
    // void writeClassicTo(OutputStream output);
    /**
     * Write the receiver in classic format as XML to a StAX writer.
     * 
     * @param writer
     *        StAX writer to which the receiver is written
     */
    // void writeClassicTo(XMLStreamWriter writer) throws XMLStreamException;
    /**
     * Write the receiver in classic format as XML to an empty DOM Document.
     * 
     * @param document
     *        empty DOM document with namespace support enabled
     */
    // void writeClassicTo(Document document);
}
