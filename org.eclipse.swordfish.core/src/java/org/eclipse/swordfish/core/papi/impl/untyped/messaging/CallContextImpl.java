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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.utils.InnerParticipantIdentity;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.misc.CharacterDecoder;
import sun.misc.CharacterEncoder;

/**
 * The Class CallContextImpl.
 */
public class CallContextImpl implements CallContextExtension {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9136629492709254983L;

    /** The scope. */
    private Scope scope;

    /** The message exchange id. */
    private String messageExchangeId;

    /** The partner operation name. */
    private String partnerOperationName;

    /** The policy. */
    private AgreedPolicy policy;

    /** The processing method. */
    private int processingMethod;

    /** The reference parameters. */
    private DocumentFragment referenceParameters;

    /** The relates to. */
    private DocumentFragment faultReferenceParameters;

    private String relatesTo;

    /** The reply to. */
    private WSAEndpointReference replyTo;

    /** The fault to. */
    private WSAEndpointReference faultTo;

    /** The unified participant identity. */
    private UnifiedParticipantIdentity unifiedParticipantIdentity;

    /** The caller subject. */
    private Subject callerSubject;

    /** The communication style. */
    private InternalCommunicationStyle communicationStyle;

    /** The consumer call identifier. */
    private String consumerCallIdentifier;

    /** The correlation ID. */
    private String faultConsumerCallIdentifier;

    private String correlationID;

    /** The message ID. */
    private String faultCorrelationID;

    private String messageID;

    /** The operation name. */
    private String operationName;

    /** The provider ID. */
    private QName providerID;

    /** The provider policy ID. */
    private String providerPolicyID;

    /** The service name. */
    private QName serviceName;

    /** The relations. */
    private InternalCallRelation[] relations;

    /** The user messaging headers. */
    private Map userMessagingHeaders;

    /** The created timestamp. */
    private long createdTimestamp;

    /** The related timestamp. */
    private long relatedTimestamp;

    // private SBBExtension sbb;

    /** The soap action. */
    private String soapAction;

    /**
     * Instantiates a new call context impl.
     */
    public CallContextImpl() {
        this.userMessagingHeaders = new HashMap();
        this.relations = new CallRelationImpl[0];
        this.processingMethod = METHOD_NONE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#addUserMessagingHeader(javax.xml.namespace.QName,
     *      org.w3c.dom.DocumentFragment)
     */
    public void addUserMessagingHeader(final QName headerName, final DocumentFragment content) {
        this.userMessagingHeaders.put(headerName, content);

    }

    /**
     * appends a given array of relations to this objects set of relations.
     * 
     * @param rels
     *        a call relation array to be appended to this objects array of relations.
     */
    public void appendRelations(final InternalCallRelation[] rels) {
        CallRelationImpl[] tmp = new CallRelationImpl[this.relations.length + rels.length];
        System.arraycopy(this.relations, 0, tmp, 0, this.relations.length);
        System.arraycopy(rels, 0, tmp, this.relations.length, rels.length);
        this.relations = tmp;
    }

    /**
     * Gets the caller subject.
     * 
     * @return Returns the callerSubject.
     */
    public Subject getCallerSubject() {
        return this.callerSubject;
    }

    /**
     * Gets the communication style.
     * 
     * @return Returns the internalCommunicationStyle.
     */
    public InternalCommunicationStyle getCommunicationStyle() {
        return this.communicationStyle;
    }

    /**
     * Gets the consumer call identifier.
     * 
     * @return Returns the consumerCallIdentifier.
     */
    public String getConsumerCallIdentifier() {
        return this.consumerCallIdentifier;
    }

    /**
     * Gets the correlation ID.
     * 
     * @return Returns the correlationID.
     */
    public String getCorrelationID() {
        return this.correlationID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getCreatedTimestamp()
     */
    public long getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public String getFaultConsumerCallIdentifier() {
        return this.faultConsumerCallIdentifier;
    }

    public String getFaultCorrelationID() {
        return this.faultCorrelationID;
    }

    public DocumentFragment getFaultReferenceParameters() {
        return this.faultReferenceParameters;
    }

    /**
     * Gets the fault to.
     * 
     * @return Returns the replyTo.
     */
    public WSAEndpointReference getFaultTo() {
        return this.faultTo;
    }

    /**
     * Gets the message exchange id.
     * 
     * @return Returns the messageExchangeId.
     */
    public String getMessageExchangeId() {
        return this.messageExchangeId;
    }

    /**
     * Gets the message ID.
     * 
     * @return Returns the messageID.
     */
    public String getMessageID() {
        return this.messageID;
    }

    /**
     * Gets the operation name.
     * 
     * @return Returns the operationName.
     */
    public String getOperationName() {
        return this.operationName;
    }

    /**
     * Gets the partner operation name.
     * 
     * @return Returns the partnerOperationName.
     */
    public String getPartnerOperationName() {
        return this.partnerOperationName;
    }

    /**
     * Gets the policy.
     * 
     * @return Returns the policy.
     */
    public AgreedPolicy getPolicy() {
        return this.policy;
    }

    /**
     * Gets the processing method.
     * 
     * @return Returns the processingMethod.
     */
    public int getProcessingMethod() {
        return this.processingMethod;
    }

    /**
     * Gets the provider ID.
     * 
     * @return Returns the providerID.
     */
    public QName getProviderID() {
        return this.providerID;
    }

    /**
     * Gets the provider policy ID.
     * 
     * @return Returns the providerPolicyID.
     */
    public String getProviderPolicyID() {
        return this.providerPolicyID;
    }

    /**
     * Gets the reference parameters.
     * 
     * @return Returns the referenceParameters.
     */
    public DocumentFragment getReferenceParameters() {
        return this.referenceParameters;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getRelatedTimestamp()
     */
    public long getRelatedTimestamp() {
        return this.relatedTimestamp;
    }

    /**
     * Gets the relates to.
     * 
     * @return Returns the relatesTo.
     */
    public String getRelatesTo() {
        return this.relatesTo;
    }

    /**
     * Gets the relations.
     * 
     * @return Returns the relations.
     */
    public InternalCallRelation[] getRelations() {
        return this.relations;
    }

    /**
     * Gets the reply to.
     * 
     * @return Returns the replyTo.
     */
    public WSAEndpointReference getReplyTo() {
        return this.replyTo;
    }

    /**
     * Gets the scope.
     * 
     * @return Returns the scope.
     */
    public Scope getScope() {
        return this.scope;
    }

    /**
     * Gets the service name.
     * 
     * @return Returns the serviceName.
     */
    public QName getServiceName() {
        return this.serviceName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getSOAPAction()
     */
    public String getSOAPAction() {
        return this.soapAction;
    }

    /**
     * Gets the unified participant identity.
     * 
     * @return Returns the unifiedParticipantIdentity.
     */
    public UnifiedParticipantIdentity getUnifiedParticipantIdentity() {
        return this.unifiedParticipantIdentity;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getUserMessagingHeader(javax.xml.namespace.QName)
     */
    public DocumentFragment getUserMessagingHeader(final QName headerName) {
        return (DocumentFragment) this.userMessagingHeaders.get(headerName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#getUserMessagingHeaderNames()
     */
    public Set getUserMessagingHeaderNames() {
        return this.userMessagingHeaders.keySet();
    }

    /**
     * serializes the content of this callContext.
     * 
     * @param stream
     *        the stream to serialize to
     * 
     * @throws IOException
     *         if an exception occures on the outputstream
     */
    // igiiiiiiiiiit
    public void marshall(final OutputStream stream) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));
        StringBuffer buf = new StringBuffer();

        // Elements that must be available
        buf.append("<CallContext xmlns=\"http://www.sop.org\" messageId=\"" + this.getMessageID() + "\" scope=\""
                + this.getScope().toString() + "\">");
        buf.append("<ExchangeId>" + this.getMessageExchangeId() + "</ExchangeId>");
        buf.append("<Service xmlns:lns=\"" + this.getServiceName().getNamespaceURI() + "\" >lns:"
                + this.getServiceName().getLocalPart() + "</Service>");
        buf.append("<OperationName>" + this.getOperationName() + "</OperationName>");
        buf.append("<ProviderId xmlns:lns=\"" + this.getProviderID().getNamespaceURI() + "\" >lns:"
                + this.getProviderID().getLocalPart() + "</ProviderId>");
        buf.append("<ProcessingMethod>" + this.getProcessingMethod() + "</ProcessingMethod>");
        buf.append("<CommunicationStyle>" + this.getCommunicationStyle().getName() + "</CommunicationStyle>");
        buf.append("<Application>" + this.getUnifiedParticipantIdentity().getParticipantIdentity().getApplicationID()
                + "</Application>");

        // elements that might not be available
        if (this.getUnifiedParticipantIdentity().getParticipantIdentity().getInstanceID() != null) {
            buf
                .append("<Instance>" + this.getUnifiedParticipantIdentity().getParticipantIdentity().getInstanceID()
                        + "</Instance>");
        }
        if (this.getPartnerOperationName() != null) {
            buf.append("<PartnerOperationName>" + this.getPartnerOperationName() + "</PartnerOperationName>");
        }
        if (this.getRelatesTo() != null) {
            buf.append("<RelatesTo>" + this.getRelatesTo() + "</RelatesTo>");
        }
        if (this.getCreatedTimestamp() != 0) {
            buf.append("<CreatedTimestamp>" + String.valueOf(this.getCreatedTimestamp()) + "</CreatedTimestamp>");
        }
        if (this.getRelatedTimestamp() != 0) {
            buf.append("<RelatedTimestamp>" + this.getRelatedTimestamp() + "</RelatedTimestamp>");
        }
        if (this.getCorrelationID() != null) {
            buf.append("<CorrelationId>" + this.getCorrelationID() + "</CorrelationId>");
        }
        if (this.getConsumerCallIdentifier() != null) {
            buf.append("<ConsumerCallIdentifier>" + this.getConsumerCallIdentifier() + "</ConsumerCallIdentifier>");
        }
        if (this.getSOAPAction() != null) {
            buf.append("<SOAPAction>" + this.getSOAPAction() + "</SOAPAction>");
        }
        if (this.getPolicy() != null) {
            ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                this.getPolicy().writeTo(bos);
                buf.append("<AgreedPolicy>" + bos.toString("UTF8") + "</AgreedPolicy>");
            } finally {
                Thread.currentThread().setContextClassLoader(currThreadLoader);
            }
        }
        if (this.getReferenceParameters() != null) {
            buf.append("<ReferenceParameters>" + TransformerUtil.stringFromDomNode(this.getReferenceParameters())
                    + "</ReferenceParameters>");
        }
        if (this.relations.length > 0) {
            buf.append("<Relations>");
            for (int i = 0; i < this.relations.length; i++) {
                buf.append("<Relation type=\"" + this.relations[i].getType() + "\">" + this.relations[i].getMessageID()
                        + "</Relation>");
            }
            buf.append("</Relations>");
        }
        if (!this.userMessagingHeaders.isEmpty()) {
            buf.append("<CustomMessagingHeaders>");
            Set names = this.getUserMessagingHeaderNames();
            Iterator iter = names.iterator();
            while (iter.hasNext()) {
                QName qna = (QName) iter.next();
                buf.append("<" + qna.getLocalPart() + " xmlns=\"" + qna.getNamespaceURI() + "\" >");
                buf.append(TransformerUtil.stringFromDomNode(this.getUserMessagingHeader(qna)));
                buf.append("</" + qna.getLocalPart() + ">");
            }
            buf.append("</CustomMessagingHeaders>");
        }

        if (this.getReplyTo() != null) {
            buf.append("<ReplyTo>");
            buf.append("<Name>" + this.getReplyTo().getName() + "</Name>");
            if (this.getReplyTo().getAddress() != null) {
                buf.append("<Address>" + URLEncoder.encode(this.getReplyTo().getAddress()) + "</Address>");
            }
            buf.append("</ReplyTo>");
        }
        // marshall callerSubject
        if (this.getCallerSubject() != null) {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this.getCallerSubject());
            oo.flush();
            CharacterEncoder encoder = new BASE64Encoder();
            String cs = encoder.encode(bo.toByteArray());
            buf.append("<CallerSubject encoding=\"base64\">" + cs + "</CallerSubject>");
        }
        // -- surrounding tag
        buf.append("</CallContext>");
        String str = buf.toString();

        // /*
        // * TODO remoce this debug part
        // */
        // Document doc = TransformerUtil.docFromString(str);
        // System.out.println(DOM2Writer.nodeToPrettyString(doc));
        // // END Debug

        // flush it into the stream
        writer.print(str);
        writer.flush();
    }

    /**
     * pushes a given InternalCallRelation into the array of already existing relations to the top
     * most position.
     * 
     * @param rels
     *        the rels
     */
    public void pushRelation(final InternalCallRelation rels) {
        CallRelationImpl[] tmp = new CallRelationImpl[this.relations.length + 1];
        tmp[0] = (CallRelationImpl) rels;
        System.arraycopy(this.relations, 0, tmp, 1, this.relations.length);
        this.relations = tmp;
    }

    /**
     * Sets the caller subject.
     * 
     * @param callerSubject
     *        The callerSubject to set.
     */
    public void setCallerSubject(final Subject callerSubject) {
        this.callerSubject = callerSubject;
    }

    /**
     * Sets the communication style.
     * 
     * @param internalCommunicationStyle
     *        The internalCommunicationStyle to set.
     */
    public void setCommunicationStyle(final InternalCommunicationStyle communicationStyle) {
        this.communicationStyle = communicationStyle;
    }

    /**
     * Sets the consumer call identifier.
     * 
     * @param consumerCallIdentifier
     *        The consumerCallIdentifier to set.
     */
    public void setConsumerCallIdentifier(final String consumerCallIdentifier) {
        this.consumerCallIdentifier = consumerCallIdentifier;
    }

    /**
     * Sets the correlation ID.
     * 
     * @param correlationID
     *        The correlationID to set.
     */
    public void setCorrelationID(final String correlationID) {
        this.correlationID = correlationID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setCreatedTimestamp(long)
     */
    public void setCreatedTimestamp(final long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setFaultConsumerCallIdentifier(final String faultConsumerCallIdentifier) {
        this.faultConsumerCallIdentifier = faultConsumerCallIdentifier;
    }

    public void setFaultCorrelationID(final String faultCorrelationID) {
        this.faultCorrelationID = faultCorrelationID;
    }

    public void setFaultReferenceParameters(final DocumentFragment refParams) {
        this.faultReferenceParameters = refParams;
    }

    /**
     * Sets the fault to.
     * 
     * @param faultTo
     *        the fault to
     */
    public void setFaultTo(final WSAEndpointReference faultTo) {
        this.faultTo = faultTo;
    }

    /**
     * Sets the message exchange id.
     * 
     * @param messageExchangeId
     *        The messageExchangeId to set.
     */
    public void setMessageExchangeId(final String messageExchangeId) {
        this.messageExchangeId = messageExchangeId;
    }

    /**
     * Sets the message ID.
     * 
     * @param messageID
     *        The messageID to set.
     */
    public void setMessageID(final String messageID) {
        this.messageID = messageID;
    }

    /**
     * Sets the operation name.
     * 
     * @param operationName
     *        The operationName to set.
     */
    public void setOperationName(final String operationName) {
        this.operationName = operationName;
    }

    // public void setSBBExtensionInstance(final SBBExtension sbb) {
    // this.sbb = sbb;
    // }
    //
    // public SBBExtension getSBBExtensionInstance() {
    // return this.sbb;
    // }

    /**
     * Sets the partner operation name.
     * 
     * @param partnerOperationName
     *        The partnerOperationName to set.
     */
    public void setPartnerOperationName(final String partnerOperationName) {
        this.partnerOperationName = partnerOperationName;
    }

    /**
     * Sets the policy.
     * 
     * @param policy
     *        The policy to set.
     */
    public void setPolicy(final AgreedPolicy policy) {
        this.policy = policy;
    }

    /**
     * Sets the processing method.
     * 
     * @param processingMethod
     *        The processingMethod to set.
     */
    public void setProcessingMethod(final int processingMethod) {
        this.processingMethod = processingMethod;
    }

    /**
     * Sets the provider ID.
     * 
     * @param providerID
     *        The providerID to set.
     */
    public void setProviderID(final QName providerID) {
        this.providerID = providerID;
    }

    /**
     * Sets the provider policy ID.
     * 
     * @param providerPolicyID
     *        The providerPolicyID to set.
     */
    public void setProviderPolicyID(final String providerPolicyID) {
        this.providerPolicyID = providerPolicyID;
    }

    /**
     * Sets the reference parameters.
     * 
     * @param referenceParameters
     *        The referenceParameters to set.
     */
    public void setReferenceParameters(final DocumentFragment referenceParameters) {
        this.referenceParameters = referenceParameters;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setRelatedTimestamp(long)
     */
    public void setRelatedTimestamp(final long relatedTimestamp) {
        this.relatedTimestamp = relatedTimestamp;
    }

    /**
     * Sets the relates to.
     * 
     * @param relatesTo
     *        The relatesTo to set.
     */
    public void setRelatesTo(final String relatesTo) {
        this.relatesTo = relatesTo;
    }

    /**
     * Sets the relations.
     * 
     * @param relations
     *        The relations to set.
     */
    public void setRelations(final InternalCallRelation[] relations) {
        this.relations = relations;
    }

    /**
     * Sets the reply to.
     * 
     * @param replyTo
     *        The replyTo to set.
     */
    public void setReplyTo(final WSAEndpointReference replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * Sets the scope.
     * 
     * @param scope
     *        The scope to set.
     */
    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    /**
     * Sets the service name.
     * 
     * @param serviceName
     *        The serviceName to set.
     */
    public void setServiceName(final QName serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension#setSOAPAction(java.lang.String)
     */
    public void setSOAPAction(final String action) {
        this.soapAction = action;
    }

    /**
     * Sets the unified participant identity.
     * 
     * @param unifiedParticipantIdentity
     *        The unifiedParticipantIdentity to set.
     */
    public void setUnifiedParticipantIdentity(final UnifiedParticipantIdentity unifiedParticipantIdentity) {
        this.unifiedParticipantIdentity = unifiedParticipantIdentity;
    }

    /**
     * deserializes the content of the inputstream into an instance of this class.
     * 
     * @param stream
     *        the stream to read from
     * 
     * @throws IOException
     *         if an exception occures during reading from the stream
     */
    // Pfuuuuuuuuuuuuuuuuuiii
    public void unmarshall(final InputStream stream) throws IOException {
        // create the document first
        Document doc = TransformerUtil.docFromInputStream(stream);

        // get values from the root node
        Element ctxElement = doc.getDocumentElement();

        // get Scope
        String ctxScope = ctxElement.getAttribute("scope");
        if (ctxScope != null) {
            this.setScope(Scope.fromString(ctxScope));
        }

        // get MessageId
        String ctxMessageId = ctxElement.getAttribute("messageId");
        if (ctxMessageId != null) {
            this.setMessageID(ctxMessageId);
        }

        String application = null;
        String instance = null;

        // go through all remaining nodes
        NodeList nl = ctxElement.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            // handle the elements that we have been marshalling before
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;

                if ("ExchangeId".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setMessageExchangeId(str);
                    continue;
                }

                if ("Service".equals(elem.getNodeName())) {
                    String ns = elem.getAttribute("xmlns:lns");
                    String locPart = this.textValueOf(elem);
                    locPart = locPart.substring(locPart.indexOf(':') + 1, locPart.length());
                    this.setServiceName(new QName(ns, locPart));
                    continue;
                }

                if ("providerId".equals(elem.getNodeName())) {
                    String ns = elem.getAttribute("xmlns:lns");
                    String locPart = this.textValueOf(elem);
                    locPart = locPart.substring(locPart.indexOf(':') + 1, locPart.length());
                    this.setProviderID(new QName(ns, locPart));
                    continue;
                }

                if ("AgreedPolicy".equals(elem.getNodeName())) {
                    Element apElem = (Element) elem.getFirstChild();
                    AgreedPolicy agreedPolicy = null;
                    if (apElem != null) {
                        ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
                        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                        try {
                            agreedPolicy = AgreedPolicyFactory.getInstance().createFrom(apElem);
                        } catch (ParserConfigurationException e) {
                            throw new IOException("cannot unmarshall call context " + e.toString());
                        } finally {
                            Thread.currentThread().setContextClassLoader(currThreadLoader);
                        }
                        this.setPolicy(agreedPolicy);
                    }
                    continue;
                }

                if ("PartnerOperationName".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setPartnerOperationName(str);
                    continue;
                }

                if ("ProcessingMethod".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setProcessingMethod(Integer.parseInt(str));
                    continue;
                }

                if ("RelatesTo".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setRelatesTo(str);
                    continue;
                }

                if ("CreatedTimestamp".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    long timestamp = Long.parseLong(str);
                    this.setCreatedTimestamp(timestamp);
                    continue;
                }

                if ("RelatedTimestamp".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    long timestamp = Long.parseLong(str);
                    this.setRelatedTimestamp(timestamp);
                    continue;
                }

                if ("ReferenceParameters".equals(elem.getNodeName())) {
                    DocumentFragment frag = elem.getOwnerDocument().createDocumentFragment();
                    NodeList list = elem.getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        if (list.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            frag.appendChild(list.item(j));
                        }
                    }
                    this.setReferenceParameters(frag);
                    continue;
                }

                if ("ReplyTo".equals(elem.getNodeName())) {
                    String name = null;
                    String address = null;
                    NodeList list = elem.getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        Element item = (Element) list.item(j);
                        if ("Name".equals(item.getNodeName())) {
                            name = this.textValueOf(item);
                            continue;
                        }
                        if ("Address".equals(item.getNodeName())) {
                            address = URLDecoder.decode(this.textValueOf(item));
                            continue;
                        }
                    }
                    this.setReplyTo(new WSAEndpointReference(name, address, null));
                    continue;
                }

                if ("CommunicationStyle".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setCommunicationStyle(InternalCommunicationStyle.getEnum(str));
                    continue;
                }

                if ("ConsumerCallIdentifier".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setConsumerCallIdentifier(str);
                    continue;
                }

                if ("CorrelationId".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setCorrelationID(str);
                    continue;
                }

                if ("OperationName".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setOperationName(str);
                    continue;
                }

                if ("SOAPAction".equals(elem.getNodeName())) {
                    String str = this.textValueOf(elem);
                    this.setSOAPAction(str);
                    continue;
                }

                if ("Application".equals(elem.getNodeName())) {
                    application = this.textValueOf(elem);
                    continue;
                }

                if ("Instance".equals(elem.getNodeName())) {
                    instance = this.textValueOf(elem);
                    continue;
                }

                if ("CallerSubject".equals(elem.getNodeName())) {
                    String encoded = this.textValueOf(elem);
                    CharacterDecoder decoder = new BASE64Decoder();
                    byte[] ba = decoder.decodeBuffer(encoded);
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(ba));
                    Subject sub = null;
                    try {
                        sub = (Subject) ois.readObject();
                    } catch (IOException e) {
                        throw new IOException("cannot unmarshall call context " + e.toString());
                    } catch (ClassNotFoundException e) {
                        throw new IOException("cannot unmarshall call context " + e.toString());
                    }
                    this.setCallerSubject(sub);
                    continue;
                }

                if ("Relations".equals(elem.getNodeName())) {
                    NodeList list = elem.getChildNodes();
                    InternalCallRelation[] cra = new CallRelationImpl[list.getLength()];
                    for (int j = 0; j < list.getLength(); j++) {
                        Element rel = (Element) list.item(j);
                        String type = rel.getAttribute("type");
                        String mid = this.textValueOf(rel);
                        cra[j] = new CallRelationImpl(type, mid);
                    }
                    this.relations = cra;
                    continue;
                }

                if ("CustomMessagingHeaders".equals(elem.getNodeName())) {
                    NodeList list = elem.getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        if (list.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element item = (Element) list.item(j);
                            String loc = item.getNodeName();
                            String ns = item.getAttribute("xmlns");
                            QName key = new QName(ns, loc);
                            DocumentFragment frag = elem.getOwnerDocument().createDocumentFragment();
                            NodeList childs = item.getChildNodes();
                            for (int k = 0; k < childs.getLength(); k++) {
                                frag.appendChild(childs.item(k));
                            }
                            this.addUserMessagingHeader(key, frag);
                        }
                    }
                    continue;
                }
            }
        }
        // now set the participant identity od this InternalCallContext
        this.setUnifiedParticipantIdentity(new UnifiedParticipantIdentity(new InnerParticipantIdentity(application, instance)));
    }

    /**
     * _text value of.
     * 
     * @param parent
     *        the node which might contain a text node as child
     * 
     * @return -- the value of the text node or null if there is no child node.
     */
    private String textValueOf(final Element parent) {
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) return node.getNodeValue().trim();
        }
        return null;
    }

}
