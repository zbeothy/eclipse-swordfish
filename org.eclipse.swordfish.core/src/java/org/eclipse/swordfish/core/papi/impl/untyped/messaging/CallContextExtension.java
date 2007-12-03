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
package org.eclipse.swordfish.core.papi.impl.untyped.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Set;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.w3c.dom.DocumentFragment;

/**
 * This interface declares extensions to the InternalCallContext. Methods defined here are not
 * visible to the PAPI user, but to the internal Programming of InternalSBB. A CallContextExtension
 * is used to contain metainformation about the call.
 */
/**
 * CallContextExtension.
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public interface CallContextExtension extends InternalCallContext, Serializable {

    /** The Constant METHOD_NONE. */
    int METHOD_NONE = -1;

    /** The Constant METHOD_OUTGOING_REQUEST. */
    int METHOD_OUTGOING_REQUEST = 0;

    /** The Constant METHOD_INCOMING_REQUEST. */
    int METHOD_INCOMING_REQUEST = 1;

    /** The Constant METHOD_OUTGOING_RESPONSE. */
    int METHOD_OUTGOING_RESPONSE = 2;

    /** The Constant METHOD_INCOMING_RESPONSE. */
    int METHOD_INCOMING_RESPONSE = 3;

    // ---- pair wise operations

    /**
     * adds messaging headers to the call context that are not known to InternalSBB as tyed objects.
     * 
     * @param headerName
     *        name of the header
     * @param content
     *        the document fragment that is the header content.
     */
    void addUserMessagingHeader(QName headerName, DocumentFragment content);

    /**
     * adds the relation array to the end of the current relation array.
     * 
     * @param relations
     *        the array of relations to append to this objects relation array.
     */
    void appendRelations(InternalCallRelation[] relations);

    /**
     * Gets the created timestamp.
     * 
     * @return a timestamp indicating the instant at which this message has been created
     */
    long getCreatedTimestamp();

    /**
     * Gets the fault consumer call identifier.
     * 
     * @return consumer call id which may have been internally modified for management of fault case
     */
    String getFaultConsumerCallIdentifier();

    /**
     * Gets the fault correlation ID.
     * 
     * @return correlation id which may have been internally modified for management of fault case
     */
    String getFaultCorrelationID();

    /**
     * Gets the fault reference parameters.
     * 
     * @return the reference parameters to build a response
     */
    DocumentFragment getFaultReferenceParameters();

    /**
     * Gets the fault to.
     * 
     * @return the replyTo endpoint reference
     */
    WSAEndpointReference getFaultTo();

    /**
     * Gets the message exchange id.
     * 
     * @return -- the exchange Id that is associted by this call context TODO: We may not keep the
     *         exchange in the InternalCallContext, but have a Map of exchangeIDs and keep the
     *         exchanges in a specific mep in the Kernel!!!
     */
    String getMessageExchangeId();

    /**
     * Gets the partner operation name.
     * 
     * @return the partner operation name that is used when this contaxt is going to be answered
     *         TODO: where is this needed? The property is set in the OneWayOperationProxy.
     */
    String getPartnerOperationName();

    /**
     * Gets the policy.
     * 
     * @return the agreed policy for this context
     */
    AgreedPolicy getPolicy();

    /**
     * Gets the processing method.
     * 
     * @return the method that was used to transform the SOAP headers into this object
     */
    int getProcessingMethod();

    /**
     * Gets the reference parameters.
     * 
     * @return the reference parameters to build a response
     */
    DocumentFragment getReferenceParameters();

    /**
     * Gets the related timestamp.
     * 
     * @return a timestamp indicating the instant at which this message has been created in relation
     *         to the original message
     */
    long getRelatedTimestamp();

    /**
     * Gets the relates to.
     * 
     * @return the messageId this message relates to
     */
    String getRelatesTo();

    /**
     * Gets the reply to.
     * 
     * @return the replyTo endpoint reference
     */
    WSAEndpointReference getReplyTo();

    /**
     * Gets the scope.
     * 
     * @return the current scope this object operates in
     */
    Scope getScope();

    // /**
    // * @param sbb the handling instance for kernel access
    // */
    // void setSBBExtensionInstance(SBBExtension sbb);
    //
    // /**
    // * @return the InternalSBB instance that was deduced to be responsible for this
    // * context or null if it cannot be deduced. The return value of null
    // * indicates an Error case.
    // */
    // SBBExtension getSBBExtensionInstance();

    /**
     * Gets the SOAP action.
     * 
     * @return the actual value for the SOAP action
     */
    String getSOAPAction();

    /**
     * Gets the unified participant identity.
     * 
     * @return the particioant Identity that this context was assigned to
     */
    UnifiedParticipantIdentity getUnifiedParticipantIdentity();

    // -- complimentary operations to the existing ones in the PAPI

    /**
     * Gets the user messaging header.
     * 
     * @param headerName
     *        the name of the header to get the value from
     * 
     * @return the document fragment that has been the SOAP header
     */
    DocumentFragment getUserMessagingHeader(QName headerName);

    /**
     * Gets the user messaging header names.
     * 
     * @return a set that contains the QNames of the user added messaging headers
     */
    Set getUserMessagingHeaderNames();

    /**
     * marshals this object into the given stream.
     * 
     * @param stream
     *        the stream to serialize this object to
     * 
     * @throws IOException
     */
    void marshall(OutputStream stream) throws IOException;

    /**
     * puts a relation into the current relation array at position 0 and shifts all other relations
     * by one position.
     * 
     * @param relation
     *        the relation to be opushed into current relation array.
     */
    void pushRelation(InternalCallRelation relation);

    /**
     * used to set the caller Subject out of a NM.
     * 
     * @param subject
     *        the security subject
     */
    void setCallerSubject(Subject subject);

    /**
     * used to set the communication style of this object.
     * 
     * @param communicationStyle
     *        the style
     */
    void setCommunicationStyle(InternalCommunicationStyle communicationStyle);

    /**
     * Sets the consumer call identifier.
     * 
     * @param consumerCallIdentifier
     *        consumer specific call identifier
     */
    void setConsumerCallIdentifier(String consumerCallIdentifier);

    /**
     * Sets the correlation ID.
     * 
     * @param correlationId
     *        the corelationId of this message associted with this context
     */
    void setCorrelationID(String correlationId);

    /**
     * Sets the created timestamp.
     * 
     * @param createdTimestamp
     *        a timestamp indicating the instant at which this message has been created
     */
    void setCreatedTimestamp(final long createdTimestamp);

    /**
     * Sets the fault consumer call identifier.
     * 
     * @param consumerCallIdentifier
     *        consumer specific call identifier
     */
    void setFaultConsumerCallIdentifier(String consumerCallIdentifier);

    /**
     * Sets the fault correlation ID.
     * 
     * @param correlationId
     *        the corelationId of this message associted with this context
     */
    void setFaultCorrelationID(String correlationId);

    /**
     * Sets the fault reference parameters.
     * 
     * @param refParams
     *        are the refernce parameters for a response
     */
    void setFaultReferenceParameters(DocumentFragment refParams);

    /**
     * Sets the fault to.
     * 
     * @param ref
     *        is the replyTo endpoint reference
     */
    void setFaultTo(WSAEndpointReference ref);

    /**
     * Sets the message exchange id.
     * 
     * @param exchangeId
     *        the exchange id
     */
    void setMessageExchangeId(String exchangeId);

    /**
     * Sets the message ID.
     * 
     * @param messageId
     *        the ID of the message related to this call context
     */
    void setMessageID(String messageId);

    /**
     * Sets the operation name.
     * 
     * @param operationName
     *        the operation of the exchange of this is targeted to or is going to be targeted to
     */
    void setOperationName(String operationName);

    // -- pure messaging header related stuff

    /**
     * Sets the partner operation name.
     * 
     * @param partnerOperationName
     *        the partner operation name that is used when this contaxt is going to be answered
     */
    void setPartnerOperationName(String partnerOperationName);

    /**
     * Sets the policy.
     * 
     * @param policy
     *        the agreed policy to set for this object
     */
    void setPolicy(AgreedPolicy policy);

    /**
     * Sets the processing method.
     * 
     * @param method
     *        the method that was used to transform the SOAP headers into this object - something
     *        between 0 and 3 I would say :-))
     */
    void setProcessingMethod(int method);

    /**
     * Sets the provider ID.
     * 
     * @param providerId
     *        actually the WSDL service name.
     */
    void setProviderID(QName providerId);

    /**
     * Sets the provider policy ID.
     * 
     * @param providerPolicyId
     *        the provider policy ID that was used to retrieve the agreed policy
     */
    void setProviderPolicyID(String providerPolicyId);

    /**
     * Sets the reference parameters.
     * 
     * @param refParams
     *        are the refernce parameters for a response
     */
    void setReferenceParameters(DocumentFragment refParams);

    /**
     * Sets the related timestamp.
     * 
     * @param relatedTimestamp
     *        a timestamp indicating the instant at which this message has been created in relation
     *        to the original message
     */
    void setRelatedTimestamp(final long relatedTimestamp);

    /**
     * Sets the relates to.
     * 
     * @param messageId
     *        the message Id this message has a relation to
     */
    void setRelatesTo(String messageId);

    /**
     * sets the array of call relations for this call context.
     * 
     * @param relations
     *        the relations
     */
    void setRelations(InternalCallRelation[] relations);

    /**
     * Sets the reply to.
     * 
     * @param ref
     *        is the replyTo endpoint reference
     */
    void setReplyTo(WSAEndpointReference ref);

    /**
     * Sets the scope.
     * 
     * @param currentScope
     *        a scope to be assigne to this context
     */
    void setScope(Scope currentScope);

    /**
     * Sets the service name.
     * 
     * @param serviceName
     *        the QName of the WSDL Interface this call contextes exchange is targeted to or is
     *        going to be targeted to.
     */
    void setServiceName(QName serviceName);

    /**
     * Sets the SOAP action.
     * 
     * @param action
     *        the actual value for the SOAP action to set
     */
    void setSOAPAction(String action);

    /**
     * Sets the unified participant identity.
     * 
     * @param identity
     *        the participant identity this context is assigned to
     */
    void setUnifiedParticipantIdentity(UnifiedParticipantIdentity identity);

    /**
     * unmarshals this object from the given stream.
     * 
     * @param stream
     *        the stream to deserialize this object from
     * 
     * @throws IOException
     */
    void unmarshall(InputStream stream) throws IOException;
}
