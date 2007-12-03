/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.core.components.headerprocessing.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.headerprocessing.HeaderProcessor;
import org.eclipse.swordfish.core.components.helpers.UUIDGenerator;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallRelationImpl;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.InnerParticipantIdentity;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Processing component to turn message and exchange properties into SOAP headers and vice versa.
 */
public class HeaderProcessorBean implements HeaderProcessor {

    /** The InternalSBB namespace in JDOM format. */

    private static final Namespace SBB_NS = Namespace.getNamespace("sbb", HeaderUtil.SBB_NS);

    /**
     * The WS-Addressing namespace in JDOM format
     */

    private static final Namespace WSA_NS = Namespace.getNamespace("wsa", Constants.WSA_NS);

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(HeaderProcessorBean.class);

    /** <code>util</code> is a handle to a utility helper object. */
    private JDOMUtil util;

    /** a formatter for creating XSD timestamps. */
    private SimpleDateFormat tsFormatter;

    /** The uuid generator. */
    private UUIDGenerator uuidGenerator;

    /**
     * Gets the uuid generator.
     * 
     * @return the uuid generator
     */
    public UUIDGenerator getUuidGenerator() {
        return this.uuidGenerator;
    }

    /**
     * init method called by the Spring wiring.
     */
    public void init() {
        this.util = new JDOMUtil();
        this.tsFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSS'Z'");
        this.tsFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Map incoming request.
     * 
     * @param msg
     *        the msg
     * @param ctx
     *        the ctx
     * 
     * @throws SAXException
     * @throws IOException
     * @throws ParseException
     * @throws InternalSBBException
     */
    public void mapIncomingRequest(final NormalizedMessage msg, final CallContextExtension ctx) throws InternalSBBException {
        Map headers = (Map) msg.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (null == headers) {
            headers = new HashMap();
        }

        // get MESSAGE ID property
        try {
            DocumentFragment idFrag = (DocumentFragment) headers.get(Constants.MESSAGE_ID);
            if (null == idFrag) {
                idFrag = (DocumentFragment) headers.get(Constants.MESSAGE_ID_DOTNET);
            }
            if (null != idFrag) {
                ctx.setMessageID(this.util.getStringValue(idFrag, "MessageID", "wsa", Constants.WSA_NS));
            } else {
                ctx.setMessageID(this.getUuidGenerator().getUUID("uuid:"));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract or create Message ID.", e);
        }

        // get Agreed Policy
        try {
            DocumentFragment policyFrag = (DocumentFragment) headers.get(Constants.AGREED_POLICY);
            if (null != policyFrag) {
                AgreedPolicy policy = this.getAgreedPolicyFromIncomingRequest(ctx, policyFrag);
                ctx.setPolicy(policy);
                ctx.setProviderPolicyID(policy.getProviderPolicyIdentity().getKeyName());
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract Agreed Policy.", e);
        }

        // get CorrelationId property
        try {
            DocumentFragment idFrag = (DocumentFragment) headers.get(Constants.CORRELATION_ID);
            if (null != idFrag) {
                ctx.setCorrelationID(this.util.getStringValue(idFrag, "CorrelationId", "sbb", Constants.CORRELATION_ID));
            } else {
                ctx.setMessageID(this.getUuidGenerator().getUUID("uuid:"));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract or create CorrelationId.", e);
        }

        // get ConsumerCalldId property
        try {
            DocumentFragment idFrag = (DocumentFragment) headers.get(Constants.CONSCALL_ID);
            if (null != idFrag) {
                ctx.setConsumerCallIdentifier(this.util.getStringValue(idFrag, "ConsumerCallId", "sbb", Constants.CONSCALL_ID));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract or create ConsumerCallId.", e);
        }

        // get REPLY TO property
        try {
            DocumentFragment eprFrag = (DocumentFragment) headers.get(Constants.REPLY_TO);
            if (null == eprFrag) {
                eprFrag = (DocumentFragment) headers.get(Constants.REPLY_TO_DOTNET);
            }
            WSAEndpointReference replyTo = null;
            if (null != eprFrag) {
                Element epr = this.util.fragmentToElement(eprFrag);
                replyTo = this.buildEndpoint(epr, "ReplyTo");
                ctx.setReplyTo(replyTo);
                Element refParams = epr.getChild("ReferenceParameters", WSA_NS);
                if (refParams != null) {
                    this.addIsReferenceParamsAttribute(refParams);
                    ctx.setReferenceParameters(this.util.buildDocumentFragment(refParams));
                    ctx.setCorrelationID(this.getRefParam(refParams, HeaderUtil.SBB_NS, "CorrelationId"));
                    ctx.setConsumerCallIdentifier(this.getRefParam(refParams, HeaderUtil.SBB_NS, "ConsumerCallId"));
                }
            }
            // generate CorrelationID if none is provided in the reference
            // parameters
            if (null == ctx.getCorrelationID()) {
                ctx.setCorrelationID(this.getUuidGenerator().getUUID("uuid:"));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract wsa:ReplyTo.", e);
        }

        // get FAULT TO property
        try {
            DocumentFragment eprFrag = (DocumentFragment) headers.get(Constants.FAULT_TO);
            if (null == eprFrag) {
                eprFrag = (DocumentFragment) headers.get(Constants.FAULT_TO_DOTNET);
            }
            WSAEndpointReference faultTo = null;
            if (null != eprFrag) {
                Element epr = this.util.fragmentToElement(eprFrag);
                faultTo = this.buildEndpoint(epr, "FaultTo");
                ctx.setFaultTo(faultTo);
                Element refParams = epr.getChild("ReferenceParameters", WSA_NS);
                if (refParams != null) {
                    this.addIsReferenceParamsAttribute(refParams);
                    ctx.setFaultReferenceParameters(this.util.buildDocumentFragment(refParams));
                    ctx.setFaultCorrelationID(this.getRefParam(refParams, HeaderUtil.SBB_NS, "CorrelationId"));
                    ctx.setFaultConsumerCallIdentifier(this.getRefParam(refParams, HeaderUtil.SBB_NS, "ConsumerCallId"));
                }
            }
            // generate CorrelationID if none is provided in the reference
            // parameters
            if (null == ctx.getFaultCorrelationID()) {
                ctx.setFaultCorrelationID(ctx.getCorrelationID());
                if (null == ctx.getFaultCorrelationID()) {
                    ctx.setFaultCorrelationID(this.getUuidGenerator().getUUID("uuid:"));
                }
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract wsa:ReplyTo.", e);
        }

        // TODO: do we really need FaultTo endpoints?
        /*
         * DocumentFragment faultEprFrag = (DocumentFragment) headers .get(Constants.FAULT_TO);
         * WSAEndpointReference faultTo = null; if (null != faultEprFrag) { Element epr =
         * util.fragmentToElement(faultEprFrag); faultTo = buildEndpoint(epr, "FaultTo");
         * ctx.setFaultTo(faultTo); Element refParams = epr.getChild("ReferenceParameters", WSA_NS); //
         * what should be done about the reference parameters }
         */

        // extract message tracking data
        try {
            this.extractTimestamps(ctx, headers);
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract message tracking data.", e);
        }

        // extract call relations
        try {
            DocumentFragment relationsFrag = (DocumentFragment) headers.get(Constants.CALL_RELATIONS);
            ctx.setRelations(this.extractCallRelations(relationsFrag));
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract sbb:CallRelations.", e);
        }

        // extract custom headers
        // we do not know which headers are custom and which one are not, so we
        // filter for the namespaces.

        try {

            Iterator iter = headers.keySet().iterator();
            while (iter.hasNext()) {
                Object next = iter.next();
                QName name = QName.valueOf((String) next);
                if (!this.hasSOPNameSpace(name)) {
                    ctx.addUserMessagingHeader(name, (DocumentFragment) headers.get(name));
                }
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract custom headers.", e);
        }
        ctx.setProcessingMethod(CallContextExtension.METHOD_INCOMING_REQUEST);
    }

    /**
     * Map incoming response.
     * 
     * @param msg
     *        the msg
     * @param ctx
     *        the ctx
     * 
     * @throws SAXException
     * @throws IOException
     * @throws ParseException
     * @throws InternalSBBException
     */
    public void mapIncomingResponse(final NormalizedMessage msg, final CallContextExtension ctx) throws InternalSBBException {
        Map headers = (Map) msg.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (null == headers) {
            headers = new HashMap();
        }
        // get MESSAGE ID property
        try {
            DocumentFragment idFrag = (DocumentFragment) headers.get(Constants.MESSAGE_ID);
            if (null == idFrag) {
                idFrag = (DocumentFragment) headers.get(Constants.MESSAGE_ID_DOTNET);
            }
            if (null != idFrag) {
                ctx.setMessageID(this.util.getStringValue(idFrag, "MessageID", "wsa", Constants.WSA_NS));
            } else {
                ctx.setMessageID(this.getUuidGenerator().getUUID("uuid:"));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract or create Message ID.", e);
        }

        // get addressing parameters
        try {
            String appId =
                    this.getStringValueFromHeaderMap(headers, Constants.APPLICATION_ID, "ApplicationId", "sbb", HeaderUtil.SBB_NS);
            String instId =
                    this.getStringValueFromHeaderMap(headers, Constants.INSTANCE_ID, "InstanceId", "sbb", HeaderUtil.SBB_NS);
            if (null != appId) {
                ctx.setUnifiedParticipantIdentity(new UnifiedParticipantIdentity(new InnerParticipantIdentity(appId, instId)));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract participant identity.", e);
        }
        try {
            String correlationID =
                    this.getStringValueFromHeaderMap(headers, Constants.CORRELATION_ID, "CorrelationId", "sbb", HeaderUtil.SBB_NS);
            if (null != correlationID) {
                ctx.setCorrelationID(correlationID);
            }
            String consId =
                    this.getStringValueFromHeaderMap(headers, Constants.CONSCALL_ID, "ConsumerCallId", "sbb", HeaderUtil.SBB_NS);
            if (null != consId) {
                ctx.setConsumerCallIdentifier(consId);
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract correlation information.", e);
        }

        // get Agreed Policy
        // TODO if the resulting context does not contain an agreed policy
        // than
        // we may reuse the
        // callContext that is still inside the MEP
        try {
            DocumentFragment policyFrag = (DocumentFragment) headers.get(Constants.AGREED_POLICY);
            if (null != policyFrag) {
                AgreedPolicy policy = this.getAgreedPolicyFromIncomingRequest(ctx, policyFrag);
                ctx.setPolicy(policy);
                ctx.setProviderPolicyID(policy.getProviderPolicyIdentity().getKeyName());
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract Agreed Policy.", e);
        }

        // extract message tracking data
        try {
            this.extractTimestamps(ctx, headers);
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract message tracking data.", e);
        }

        // extract call relations
        try {
            DocumentFragment relationsFrag = (DocumentFragment) headers.get(Constants.CALL_RELATIONS);
            ctx.setRelations(this.extractCallRelations(relationsFrag));
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract sbb:CallRelations.", e);
        }

        // extract custom headers
        // we do not know which headers are custom and which one are not, so we
        // filter for the namespaces.
        try {
            Iterator iter = headers.keySet().iterator();
            while (iter.hasNext()) {
                Object next = iter.next();
                QName name = QName.valueOf((String) next);
                if (!this.hasSOPNameSpace(name)) {
                    ctx.addUserMessagingHeader(name, (DocumentFragment) headers.get(name));
                }
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not extract custom headers.", e);
        }
        ctx.setProcessingMethod(CallContextExtension.METHOD_INCOMING_RESPONSE);
    }

    /**
     * Map outgoing request.
     * 
     * @param ctx
     *        the ctx
     * @param msg
     *        the msg
     * 
     * @throws InternalSBBException
     */
    public void mapOutgoingRequest(final CallContextExtension ctx, final NormalizedMessage msg) throws InternalSBBException {
        Map headers = (Map) msg.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (headers == null) {
            headers = new HashMap();
        }

        // preparation
        String appId = ctx.getUnifiedParticipantIdentity().getParticipantIdentity().getApplicationID();
        String instId = ctx.getUnifiedParticipantIdentity().getParticipantIdentity().getInstanceID();
        Element policy = null;
        try {
            policy = this.buildElement(ctx.getPolicy());
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create Element from AgreedPolicy.", e);
        }

        // add ACTION property
        try {
            headers.put(Constants.ACTION, this.util.buildDocumentFragment(this.buildActionHeader(ctx)));
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create wsa:Action header.", e);
        }

        // add MESSAGE ID property
        try {
            headers.put(Constants.MESSAGE_ID, this.util.buildStringFragment("MessageID", "wsa", Constants.WSA_NS, ctx
                .getMessageID()));
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create wsa:MessageID header.", e);
        }

        // add REPLYTO property
        try {
            WSAEndpointReference epr = ctx.getReplyTo();
            if (null != epr) {
                DocumentFragment endpointFragment =
                        this.buildEndpointFragment(epr, policy, appId, instId, ctx.getCorrelationID(), ctx
                            .getConsumerCallIdentifier());
                headers.put(Constants.REPLY_TO, endpointFragment);
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create wsa:ReplyTo header.", e);
        }

        // add reference parameters
        try {
            policy.setAttribute("IsReferenceParameter", "true", Namespace.getNamespace("wsa", Constants.WSA_NS));
            headers.put(Constants.AGREED_POLICY, this.util.buildDocumentFragment(policy));
            Element correlationId = this.buildSbbHeader(ctx.getCorrelationID(), "CorrelationId");
            correlationId.setAttribute("IsReferenceParameter", "true", Namespace.getNamespace("wsa", Constants.WSA_NS));
            headers.put(Constants.CORRELATION_ID, this.util.buildDocumentFragment(correlationId));
            if (null != ctx.getConsumerCallIdentifier()) {
                Element consumerCallId = this.buildSbbHeader(ctx.getConsumerCallIdentifier(), "ConsumerCallId");
                consumerCallId.setAttribute("IsReferenceParameter", "true", Namespace.getNamespace("wsa", Constants.WSA_NS));
                headers.put(Constants.CONSCALL_ID, this.util.buildDocumentFragment(consumerCallId));
            }

        } catch (Exception e) {
            throw new InternalMessagingException("Could not add reference parameters.", e);
        }

        // add message tracking information
        try {
            Element mte = this.buildMessageTrackingElement(ctx);
            if (null != mte) {
                headers.put(Constants.MSG_TRACKING, this.util.buildDocumentFragment(mte));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create sbb:MessageTracking header.", e);
        }

        // add call relations
        try {
            Element relationElement = this.buildCallRelationsElement(ctx);
            if (null != relationElement) {
                headers.put(Constants.CALL_RELATIONS, this.util.buildDocumentFragment(relationElement));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create sbb:CallRelations header.", e);
        }
        // TODO: do we really need FaultTo endpoints?
        /*
         * responsePolicy = getResponsePolicyElementFromPolicy(ctx.getPolicy()); DocumentFragment
         * faultEndpointFragment = buildEndpointFragment( new WSAEndpointReference("FaultTo",
         * epr.getAddress(), epr .getServiceQName(), epr.getPortName()), responsePolicy, appId,
         * instId, ctx.getCorrelationID(), ctx .getConsumerCallIdentifier());
         * headers.put(Constants.FAULT_TO, faultEndpointFragment);
         */

        // add user defined headers
        try {
            Iterator iter = ctx.getUserMessagingHeaderNames().iterator();
            while (iter.hasNext()) {
                QName name = (QName) iter.next();
                headers.put(name.toString(), ctx.getUserMessagingHeader(name));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not add user defined headers.", e);
        }

        msg.setProperty(HeaderUtil.HEADER_PROPERTY, headers);
        ctx.setProcessingMethod(CallContextExtension.METHOD_OUTGOING_REQUEST);
    }

    /**
     * Map outgoing response.
     * 
     * @param ctx
     *        the ctx
     * @param msg
     *        the msg
     * 
     * @throws SAXException
     * @throws IOException
     * @throws InternalSBBException
     */
    public void mapOutgoingResponse(final CallContextExtension ctx, final NormalizedMessage msg) throws InternalSBBException {
        Map headers = (Map) msg.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (headers == null) {
            headers = new HashMap();
        }
        // add ACTION property
        try {
            headers.put(Constants.ACTION, this.util.buildDocumentFragment(this.buildActionHeader(ctx)));
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create wsa:Action header.", e);
        }

        // add MESSAGE ID property
        try {
            headers.put(Constants.MESSAGE_ID, this.util.buildStringFragment("MessageID", "wsa", Constants.WSA_NS, ctx
                .getMessageID()));
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create wsa:MessageID header.", e);
        }

        // add RELATES TO property
        try {
            String relates = ctx.getRelatesTo();
            if (relates != null) {
                headers.put(Constants.RELATES_TO, this.util.buildDocumentFragment(this.buildReplyRelation(relates)));
            } else {
                LOG.warn("Message ID of request message this response is related to could not be found");
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create wsa:RelatesTo header.", e);
        }

        // add reference parameters
        try {
            DocumentFragment frag = ctx.getReferenceParameters();
            if (frag != null) {
                Element refParams = this.util.fragmentToElement(frag);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Copying reference parameters: " + refParams);
                    if (refParams != null) {
                        LOG.debug(this.util.prettyPrinted(refParams));
                    }
                }
                Iterator iter = refParams.getChildren().iterator();
                while (iter.hasNext()) {
                    Element elem = (Element) iter.next();
                    headers.put(this.util.getNameNS(elem), this.util.buildDocumentFragment(elem));
                }
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not add reference parameters.", e);
        }

        // add message tracking information
        try {
            Element mte = this.buildMessageTrackingElement(ctx);
            if (null != mte) {
                headers.put(Constants.MSG_TRACKING, this.util.buildDocumentFragment(mte));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not create sbb:MessageTracking header.", e);
        }

        // get the call relations
        try {
            InternalCallRelation[] relations = ctx.getRelations();
            if (relations.length > 0) {
                Element relationElement = new Element("CallRelations", SBB_NS);
                for (int i = 0; i < relations.length; i++) {
                    Element anElem = new Element("Relation", SBB_NS);
                    anElem.setAttribute("type", relations[i].getType());
                    anElem.setAttribute("messageId", relations[i].getMessageID());
                    relationElement.addContent(anElem);
                }
                headers.put(Constants.CALL_RELATIONS, this.util.buildDocumentFragment(relationElement));

            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not add call relations.", e);
        }

        // add user defined headers
        try {
            Iterator iter = ctx.getUserMessagingHeaderNames().iterator();
            while (iter.hasNext()) {
                QName name = (QName) iter.next();
                headers.put(name.toString(), ctx.getUserMessagingHeader(name));
            }
        } catch (Exception e) {
            throw new InternalMessagingException("Could not add user defined headers.", e);
        }
        msg.setProperty(HeaderUtil.HEADER_PROPERTY, headers);
        ctx.setProcessingMethod(CallContextExtension.METHOD_OUTGOING_RESPONSE);
    }

    /**
     * Print SOAP headers to debug log.
     * 
     * @param context
     *        the message exchange
     * @param message
     *        the normalized message
     * @param map
     *        the map
     */
    public void printSOAPHeaders(final MessageExchange context, final NormalizedMessage message, final Map map) {
        if (map != null) {
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                DocumentFragment frag = (DocumentFragment) entry.getValue();
                LOG.debug("[" + name + " ==> " + this.util.prettyPrinted(frag) + " ]");
            }
        } else {
            LOG.warn("No SOAP Headers");
        }
    }

    /**
     * Sets the uuid generator.
     * 
     * @param uuidGenerator
     *        the new uuid generator
     */
    public void setUuidGenerator(final UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    /**
     * Adds the is reference params attribute.
     * 
     * @param refParams
     *        the ref params
     */
    private void addIsReferenceParamsAttribute(final Element refParams) {
        Iterator it = refParams.getChildren().iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            e.setAttribute("IsReferenceParameter", "true", Namespace.getNamespace("wsa", Constants.WSA_NS));
        }

    }

    /**
     * builds a WS-Addression wsa:Action header.
     * 
     * @param ctx
     *        the ctx
     * 
     * @return the element
     */
    private Element buildActionHeader(final CallContextExtension ctx) {
        Element action = new Element("Action", WSA_NS);
        action.setText(ctx.getServiceName().getNamespaceURI() + "/" + ctx.getOperationName());
        return action;
    }

    /**
     * Builds the call relations element.
     * 
     * @param ctx
     *        the ctx
     * 
     * @return the element
     */
    private Element buildCallRelationsElement(final CallContextExtension ctx) {
        InternalCallRelation[] relations = ctx.getRelations();
        if ((null != relations) && (relations.length > 0)) {
            Element relationElement = new Element("CallRelations", SBB_NS);
            for (int i = 0; i < relations.length; i++) {
                Element anElem = new Element("Relation", SBB_NS);
                anElem.setAttribute("type", relations[i].getType());
                anElem.setAttribute("messageId", relations[i].getMessageID());
                relationElement.addContent(anElem);
            }
            return relationElement;
        } else
            return null;

    }

    /**
     * Builds the element.
     * 
     * @param policy
     *        the policy
     * 
     * @return the element
     */
    private Element buildElement(final AgreedPolicy policy) {
        org.w3c.dom.Document doc = XMLUtil.newDocument();
        new Document();
        policy.writeTo(doc);
        return this.util.domDocumentToDocument(doc).detachRootElement();
    }

    /**
     * Builds the endpoint.
     * 
     * @param epr
     *        the epr
     * @param eprName
     *        the epr name
     * 
     * @return the WSA endpoint reference
     */
    private WSAEndpointReference buildEndpoint(final Element epr, final String eprName) {
        Namespace wsa = Namespace.getNamespace("wsa", Constants.WSA_NS);
        Namespace.getNamespace("wsaw", Constants.WSAW_NS);
        Namespace sbbiop = Namespace.getNamespace("sbbiop", "http://www.sopware.org/interop/addressing");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating endpoint from document:\n" + this.util.prettyPrinted(epr));
        }
        String address = epr.getChildText("Address", wsa);
        if (null == address) {
            address = epr.getChildText("Address", sbbiop);
        }
        Element refParams = epr.getChild("ReferenceParameters", WSA_NS);
        final String applicationId = this.getRefParam(refParams, HeaderUtil.SBB_NS, "ApplicationId");
        final String instanceId = this.getRefParam(refParams, HeaderUtil.SBB_NS, "InstanceId");
        InternalParticipantIdentity participant = new InternalParticipantIdentity() {

            public String getApplicationID() {
                return applicationId;
            }

            public String getInstanceID() {
                return instanceId;
            }

        };
        return new WSAEndpointReference(eprName, address, participant);
    }

    /**
     * Builds the endpoint fragment.
     * 
     * @param epr
     *        the epr
     * @param responsePolicy
     *        the response policy
     * @param appId
     *        the app id
     * @param instId
     *        the inst id
     * @param correlationId
     *        the correlation id
     * @param consumerCallId
     *        the consumer call id
     * 
     * @return the document fragment
     */
    private DocumentFragment buildEndpointFragment(final WSAEndpointReference epr, final Element responsePolicy,
            final String appId, final String instId, final String correlationId, final String consumerCallId) {
        try {
            Namespace soap = Namespace.getNamespace("soap", HeaderUtil.SOAP_NS);
            Namespace wsa = Namespace.getNamespace("wsa", Constants.WSA_NS);
            Document doc = new Document();
            Element endp = new Element(epr.getName(), wsa);
            endp.addNamespaceDeclaration(wsa);
            endp.addNamespaceDeclaration(SBB_NS);
            Element addr = new Element("Address", wsa);
            String addressString = epr.getAddress();
            if (addressString != null) {
                // A 'real' reply endpoint has been set; in this case, the
                // receiver must understand us
                endp.setAttribute("mustUnderstand", "1", soap);
            } else {
                addressString = Constants.WSA_ANONYMOUS;
            }
            addr.setText(addressString);
            endp.addContent(addr);
            Element refParams = new Element("ReferenceParameters", wsa);
            this.util.addElementIfNotNull(refParams, "ApplicationId", SBB_NS, appId);
            this.util.addElementIfNotNull(refParams, "InstanceId", SBB_NS, instId);
            this.util.addElementIfNotNull(refParams, "CorrelationId", SBB_NS, correlationId);
            this.util.addElementIfNotNull(refParams, "ConsumerCallId", SBB_NS, consumerCallId);

            refParams.addContent(responsePolicy);
            endp.addContent(refParams);
            doc.addContent(endp);
            return this.util.buildDocumentFragment(doc);
        } catch (Exception e) {
            // TODO: Throw a decent exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds the message tracking element.
     * 
     * @param ctx
     *        the ctx
     * 
     * @return the element
     * 
     * @throws IOException
     * @throws SAXException
     */
    private Element buildMessageTrackingElement(final CallContextExtension ctx) throws SAXException, IOException {
        Element element = new Element("MessageTracking", SBB_NS);
        long createdTimestamp = ctx.getCreatedTimestamp();
        if (createdTimestamp != 0) {
            Element created = new Element("Created", SBB_NS);
            created.setText(String.valueOf(createdTimestamp));
            element.addContent(created);
        }
        long relatedTimestamp = ctx.getRelatedTimestamp();
        if (relatedTimestamp != 0) {
            Element related = new Element("Related", SBB_NS);
            related.setText(String.valueOf(relatedTimestamp));
            element.addContent(related);
        }
        if ((createdTimestamp != 0) || (relatedTimestamp != 0))
            return element;
        else
            return null;
    }

    /**
     * Builds the reply relation.
     * 
     * @param relates
     *        the relates
     * 
     * @return the element
     */
    private Element buildReplyRelation(final String relates) {
        Namespace wsa = Namespace.getNamespace("wsa", Constants.WSA_NS);
        Element relatesTo = new Element("RelatesTo", wsa);
        // This would be correct, but it's the default -- so let's save some
        // bandwidth for once
        // relatesTo.setAttribute("RelationshipType",
        // Constants.RESPONSE_RELATION);
        relatesTo.setText(relates);
        return relatesTo;
    }

    /**
     * Builds the sbb header.
     * 
     * @param value
     *        the value
     * @param name
     *        the name
     * 
     * @return the element
     * 
     * @throws SAXException
     * @throws IOException
     */
    private Element buildSbbHeader(final String value, final String name) throws SAXException, IOException {
        Element elem = new Element(name, SBB_NS);
        elem.setText(value);
        return elem;
    }

    /**
     * Extract call relations.
     * 
     * @param fragment
     *        the fragment
     * 
     * @return the call relation[]
     */
    private InternalCallRelation[] extractCallRelations(final DocumentFragment fragment) {
        InternalCallRelation[] relations = new CallRelationImpl[0];
        if (fragment != null) {
            org.w3c.dom.Element firstChild = (org.w3c.dom.Element) fragment.getFirstChild(); // <-
            NodeList children = firstChild.getChildNodes();
            List tmp = new ArrayList();
            for (int i = 0; i < children.getLength(); i++) {
                org.w3c.dom.Element elem = (org.w3c.dom.Element) children.item(i);
                String type = elem.getAttribute("type");
                String messageId = elem.getAttribute("messageId");
                tmp.add(new CallRelationImpl(type, messageId));
            }
            relations = new CallRelationImpl[tmp.size()];
            for (int i = 0; i < tmp.size(); i++) {
                relations[i] = (CallRelationImpl) tmp.get(i);
            }
        }
        return relations;
    }

    /**
     * Extract timestamps.
     * 
     * @param ctx
     *        the ctx
     * @param headers
     *        the headers
     * 
     * @throws ParseException
     */
    private void extractTimestamps(final CallContextExtension ctx, final Map headers) throws ParseException {
        DocumentFragment msgTrackingFrag = (DocumentFragment) headers.get(Constants.MSG_TRACKING);
        if (msgTrackingFrag != null) {
            Element elem = this.util.fragmentToElement(msgTrackingFrag);
            long createdTimestamp = this.getTimestamp(elem.getChildText("Created", SBB_NS));
            if (createdTimestamp != 0) {
                ctx.setCreatedTimestamp(createdTimestamp);
            }
            long relatedTimestamp = this.getTimestamp(elem.getChildText("Related", SBB_NS));
            if (relatedTimestamp != 0) {
                ctx.setRelatedTimestamp(relatedTimestamp);
            }
        }
    }

    /**
     * Gets the agreed policy from incoming request.
     * 
     * @param ctx
     *        the ctx
     * @param fragment
     *        the fragment
     * 
     * @return the agreed policy from incoming request
     * 
     * @throws ParserConfigurationException
     */
    private AgreedPolicy getAgreedPolicyFromIncomingRequest(final CallContextExtension ctx, final DocumentFragment fragment)
            throws ParserConfigurationException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving policy from document:\n" + this.util.prettyPrinted(this.util.fragmentToElement(fragment)));
        }

        org.w3c.dom.Element policy = (org.w3c.dom.Element) fragment.getFirstChild();
        if (null != policy)
            return AgreedPolicyFactory.getInstance().createFrom(policy);
        else
            return null;
    }

    /**
     * Gets the ref param.
     * 
     * @param namespace
     *        the namespace
     * @param localName
     *        the local name
     * @param refParam
     *        the ref param
     * 
     * @return the string value of the wsa:ReferenceParameter element with localname and namespace
     */
    private String getRefParam(final Element refParam, final String namespace, final String localName) {
        if (refParam != null) return refParam.getChildText(localName, Namespace.getNamespace(namespace));
        return null;
    }

    /**
     * Gets the string value from header map.
     * 
     * @param headers
     *        the headers
     * @param key
     *        the key
     * @param localName
     *        the local name
     * @param prefix
     *        the prefix
     * @param namespace
     *        the namespace
     * 
     * @return the string value from header map
     */
    private String getStringValueFromHeaderMap(final Map headers, final String key, final String localName, final String prefix,
            final String namespace) {
        String ret = null;
        DocumentFragment frag = (DocumentFragment) headers.get(key);
        if (frag != null) {
            ret = this.util.getStringValue(frag, localName, prefix, namespace);
        }
        return ret;
    }

    /**
     * Gets the timestamp.
     * 
     * @param value
     *        the value
     * 
     * @return the timestamp
     * 
     * @throws ParseException
     */
    private long getTimestamp(final String value) throws ParseException {
        return value != null ? Long.parseLong(value) : 0;
    }

    /**
     * Checks for SOP name space.
     * 
     * @param name
     *        a QName to examine weather its namespace is contained in a resered SOP namespace.
     * 
     * @return true if the namespace is one of the name spaces used by SOP false otherwise.
     */
    private boolean hasSOPNameSpace(final QName name) {
        String ns = name.getNamespaceURI();
        if (HeaderUtil.SBB_NS.equals(ns)) return true;
        if (HeaderUtil.SOAP_NS.equals(ns)) return true;
        if (HeaderUtil.WSSECURITY_NS.equals(ns)) return true;
        if (Constants.WSA_NS.equals(ns)) return true;
        if (Constants.AGREED_POLICY.equals(ns)) return true;
        return false;
    }

}