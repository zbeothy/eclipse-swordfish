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
package org.eclipse.swordfish.core.management.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.w3c.dom.DocumentFragment;

/**
 * The Class DummyCallContext.
 */
public class DummyCallContext implements CallContextExtension {

    /**
     * 
     */
    private static final long serialVersionUID = -2345012582888303120L;

    /** The correlation ID. */
    private String correlationID = "thx1138";

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#addUserMessagingHeader(javax.xml.namespace.QName,
     *      org.w3c.dom.DocumentFragment)
     */
    public void addUserMessagingHeader(final QName headerName, final DocumentFragment content) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#appendRelations(org.eclipse.swordfish.papi.untyped.CallRelation[])
     */
    public void appendRelations(final InternalCallRelation[] relations) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getCallerSubject()
     */
    public Subject getCallerSubject() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getCommunicationStyle()
     */
    public InternalCommunicationStyle getCommunicationStyle() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getConsumerCallIdentifier()
     */
    public String getConsumerCallIdentifier() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getCorrelationID()
     */
    public String getCorrelationID() {
        return this.correlationID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getCreatedTimestamp()
     */
    public long getCreatedTimestamp() {
        return 0;
    }

    public String getFaultConsumerCallIdentifier() {
        return null;
    }

    public String getFaultCorrelationID() {
        return null;
    }

    public DocumentFragment getFaultReferenceParameters() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getFaultTo()
     */
    public WSAEndpointReference getFaultTo() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getMessageExchangeId()
     */
    public String getMessageExchangeId() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getMessageID()
     */
    public String getMessageID() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getOperationName()
     */
    public String getOperationName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getPartnerOperationName()
     */
    public String getPartnerOperationName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getPolicy()
     */
    public AgreedPolicy getPolicy() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getProcessingMethod()
     */
    public int getProcessingMethod() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getProviderID()
     */
    public QName getProviderID() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getProviderPolicyID()
     */
    public String getProviderPolicyID() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getReferenceParameters()
     */
    public DocumentFragment getReferenceParameters() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getRelatedTimestamp()
     */
    public long getRelatedTimestamp() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getRelatesTo()
     */
    public String getRelatesTo() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getRelations()
     */
    public InternalCallRelation[] getRelations() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getReplyTo()
     */
    public WSAEndpointReference getReplyTo() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getScope()
     */
    public Scope getScope() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.CallContext#getServiceName()
     */
    public QName getServiceName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getSOAPAction()
     */
    public String getSOAPAction() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getUnifiedParticipantIdentity()
     */
    public UnifiedParticipantIdentity getUnifiedParticipantIdentity() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getUserMessagingHeader(javax.xml.namespace.QName)
     */
    public DocumentFragment getUserMessagingHeader(final QName headerName) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getUserMessagingHeaderNames()
     */
    public Set getUserMessagingHeaderNames() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#marshall(java.io.OutputStream)
     */
    public void marshall(final OutputStream stream) throws IOException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#pushRelation(org.eclipse.swordfish.papi.untyped.CallRelation)
     */
    public void pushRelation(final InternalCallRelation relation) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setCallerSubject(javax.security.auth.Subject)
     */
    public void setCallerSubject(final Subject subject) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setCommunicationStyle(org.eclipse.swordfish.papi.CommunicationStyle)
     */
    public void setCommunicationStyle(final InternalCommunicationStyle communicationStyle) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setConsumerCallIdentifier(java.lang.String)
     */
    public void setConsumerCallIdentifier(final String consumerCallIdentifier) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setCorrelationID(java.lang.String)
     */
    public void setCorrelationID(final String correlationId) {
        this.correlationID = correlationId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setCreatedTimestamp(long)
     */
    public void setCreatedTimestamp(final long createdTimestamp) {

    }

    public void setFaultConsumerCallIdentifier(final String consumerCallIdentifier) {

    }

    public void setFaultCorrelationID(final String correlationId) {

    }

    public void setFaultReferenceParameters(final DocumentFragment refParams) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setFaultTo(org.eclipse.swordfish.core.components.addressing.WSAEndpointReference)
     */
    public void setFaultTo(final WSAEndpointReference ref) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setMessageExchangeId(java.lang.String)
     */
    public void setMessageExchangeId(final String exchangeId) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setMessageID(java.lang.String)
     */
    public void setMessageID(final String messageId) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setOperationName(java.lang.String)
     */
    public void setOperationName(final String operationName) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setPartnerOperationName(java.lang.String)
     */
    public void setPartnerOperationName(final String partnerOperationName) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setPolicy(org.eclipse.swordfish.policytrader.AgreedPolicy)
     */
    public void setPolicy(final AgreedPolicy policy) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setProcessingMethod(int)
     */
    public void setProcessingMethod(final int method) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setProviderID(javax.xml.namespace.QName)
     */
    public void setProviderID(final QName providerId) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setProviderPolicyID(java.lang.String)
     */
    public void setProviderPolicyID(final String providerPolicyId) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setReferenceParameters(org.w3c.dom.DocumentFragment)
     */
    public void setReferenceParameters(final DocumentFragment refParams) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setRelatedTimestamp(long)
     */
    public void setRelatedTimestamp(final long relatedTimestamp) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setRelatesTo(java.lang.String)
     */
    public void setRelatesTo(final String messageId) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setRelations(org.eclipse.swordfish.papi.untyped.CallRelation[])
     */
    public void setRelations(final InternalCallRelation[] relations) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setReplyTo(org.eclipse.swordfish.core.components.addressing.WSAEndpointReference)
     */
    public void setReplyTo(final WSAEndpointReference ref) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setScope(org.eclipse.swordfish.core.components.iapi.Scope)
     */
    public void setScope(final Scope currentScope) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setServiceName(javax.xml.namespace.QName)
     */
    public void setServiceName(final QName serviceName) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setSOAPAction(java.lang.String)
     */
    public void setSOAPAction(final String action) {

    }

    /**
     * Sets the SOPA action.
     * 
     * @param action
     *        the new SOPA action
     */
    public void setSOPAAction(final String action) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setUnifiedParticipantIdentity(org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity)
     */
    public void setUnifiedParticipantIdentity(final UnifiedParticipantIdentity identity) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#unmarshall(java.io.InputStream)
     */
    public void unmarshall(final InputStream stream) throws IOException {

    }

}
