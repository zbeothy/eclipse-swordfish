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

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.eclipse.swordfish.policytrader.exceptions.UnreadableSourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Converter of a stream of raw XML into a {@link ParticipantPolicy}.
 */
public class ParticipantPolicyReader {

    /** Namespace URI of SOPware paricipant policies. */
    private static final String PARTICIPANT_POLICY_NS = "http://types.sopware.org/qos/ParticipantPolicy/1.1";

    /** Namespace URI of WS Policy. */
    private static final String WS_POLICY_NS = OperationPolicy.WSP_NAMESPACE_URI;

    /** Tag name of the participant policy root element. */
    private static final String PARTICIPANT_POLICY_TAG = "ParticipantPolicy";

    /** Participant policy attribute. */
    private static final String PP_ROLE_ATTRIB = "role";

    /** Participant policy attribute. */
    private static final String PP_SERVICE_ATTRIB = "service";

    /** Optional location attribute for service description. */
    private static final String PP_SERVICE_LOCATION_ATTRIB = "serviceLocation";

    /** Participant policy attribute. */
    private static final String PP_PROVIDER_ATTRIB = "provider";

    /** Participant policy attribute. */
    private static final String PP_VERSION_ATTRIB = "version";

    /** Participant policy attribute. */
    private static final String PP_ID_ATTRIB = "id";

    /** Participant policy attribute. */
    private static final String PP_NAME_ATTRIB = "name";

    /** Tag name of the default operation element. */
    private static final String DEFAULT_OPERATION_TAG = "DefaultOperation";

    /** Tag name of the operation elements. */
    private static final String OPERATION_TAG = "Operation";

    /** Operation name attribute. */
    private static final String OP_NAME_ATTRIB = "name";

    /** Tag name of policy reference elements (in the WS Policy namespace). */
    private static final String POLICY_REFERENCE_TAG = "PolicyReference";

    /** URI attribute of a policy reference. */
    private static final String PR_URI_ATTRIB = "URI";

    /** location attribute of a policy reference. */
    private static final String PR_LOCATION_ATTRIB = "location";

    /** Tag name of the "unused" element. */
    private static final String UNUSED_TAG = "unused";

    /** Input stream with the raw XML data. */
    private final InputStream source;

    /** DOM Element. */
    private final Element rootElement;

    /** Toolbox for XML processing. */
    private final Toolbox toolbox;

    /**
     * Constructor used within the {@link PolicyFactory}.
     * 
     * @param rootElement
     *        DOM Element
     * @param toolbox
     *        XML processing toolbox
     */
    public ParticipantPolicyReader(final Element rootElement, final Toolbox toolbox) {
        super();
        this.rootElement = rootElement;
        this.source = null;
        this.toolbox = toolbox;
    }

    /**
     * Constructor used within the {@link PolicyFactory}.
     * 
     * @param source
     *        raw XML data
     * @param toolbox
     *        XML processing toolbox
     */
    public ParticipantPolicyReader(final InputStream source, final Toolbox toolbox) {
        super();
        this.source = source;
        this.rootElement = null;
        this.toolbox = toolbox;
    }

    /**
     * Create a {@link ParticipantPolicy} from the raw XML data.
     * 
     * @return resulting paricipant policy
     * 
     * @throws UnreadableSourceException
     *         if the raw XML stream cannot be read
     * @throws CorruptedSourceException
     *         if the raw XML stream does not contain a valid participant policy.
     */
    public ParticipantPolicy readParticipantPolicy() throws UnreadableSourceException, CorruptedSourceException {
        Element ppElement = null;
        if (null != this.source) {
            // TODO SaX-based parsing
            final Document document = this.toolbox.streamToDocument(this.source);
            ppElement = document.getDocumentElement();
        } else {
            ppElement = this.rootElement;
        }
        final ParticipantPolicyImpl result = new ParticipantPolicyImpl();
        if (!PARTICIPANT_POLICY_TAG.equals(ppElement.getLocalName()))
            throw new CorruptedSourceException("root element is not ParticipantPolicy");
        if (!PARTICIPANT_POLICY_NS.equals(ppElement.getNamespaceURI()))
            throw new CorruptedSourceException("root element has not ParticipantPolicy namespace");
        result.role = ppElement.getAttribute(PP_ROLE_ATTRIB);
        result.service = ppElement.getAttribute(PP_SERVICE_ATTRIB);
        result.serviceLocation = ppElement.getAttribute(PP_SERVICE_LOCATION_ATTRIB);
        result.provider = ppElement.getAttribute(PP_PROVIDER_ATTRIB);
        result.version = ppElement.getAttribute(PP_VERSION_ATTRIB);
        result.id = ppElement.getAttribute(PP_ID_ATTRIB);
        result.name = ppElement.getAttribute(PP_NAME_ATTRIB);
        for (Node child = ppElement.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element opElement = (Element) child;
            if (OPERATION_TAG.equals(opElement.getLocalName()) && PARTICIPANT_POLICY_NS.equals(opElement.getNamespaceURI())) {
                this.processOperation(opElement, result);
            } else if (DEFAULT_OPERATION_TAG.equals(opElement.getLocalName())
                    && PARTICIPANT_POLICY_NS.equals(opElement.getNamespaceURI())) {
                this.processDefaultOperation(opElement, result);
            }
        }
        result.roPolicyIds = Collections.unmodifiableMap(result.policyIds);
        return result;
    }

    /**
     * Helper method.
     * 
     * @param opElement
     *        operation element
     * @param result
     *        participant policy being built
     * 
     * @throws CorruptedSourceException
     *         if anything in the XML data is invalid
     */
    private void processDefaultOperation(final Element opElement, final ParticipantPolicyImpl result)
            throws CorruptedSourceException {
        final OperationPolicyIdentity id = this.resolveOperationPolicyIdentity(opElement);
        result.defaultPolicyId = id;
        result.policyIds.put(ParticipantPolicy.ANY_OPERATION, id);
    }

    /**
     * Helper method.
     * 
     * @param opElement
     *        operation element
     * @param result
     *        participant policy being built
     * 
     * @throws CorruptedSourceException
     *         if anything in the XML data is invalid
     */
    private void processOperation(final Element opElement, final ParticipantPolicyImpl result) throws CorruptedSourceException {
        final String opName = opElement.getAttribute(OP_NAME_ATTRIB);
        if ((opName == null) || (opName.length() == 0)) throw new CorruptedSourceException("Operation defined without name");
        final OperationPolicyIdentity id = this.resolveOperationPolicyIdentity(opElement);
        result.policyIds.put(opName, id);
    }

    /**
     * Helper method.
     * 
     * @param opElement
     *        operation element
     * 
     * @return operation policy identifier or <null> for an unused operation
     * 
     * @throws CorruptedSourceException
     *         if anything in the XML data is invalid
     */
    private OperationPolicyIdentity resolveOperationPolicyIdentity(final Element opElement) throws CorruptedSourceException {
        for (Node child = opElement.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element prElement = (Element) child;
            if (POLICY_REFERENCE_TAG.equals(prElement.getLocalName()) && WS_POLICY_NS.equals(prElement.getNamespaceURI())) {
                final String res = prElement.getAttribute(PR_URI_ATTRIB);
                if ((res == null) || (res.length() == 0)) throw new CorruptedSourceException("PolicyReference has no URI");
                final String loc = prElement.getAttribute(PR_LOCATION_ATTRIB);
                return ((loc == null) || (loc.length() == 0)) ? new StandardOperationPolicyIdentity(res)
                        : new StandardOperationPolicyIdentity(res, loc);
            }
            if (UNUSED_TAG.equals(prElement.getLocalName()) && PARTICIPANT_POLICY_NS.equals(opElement.getNamespaceURI()))
                return ParticipantPolicy.VOID_POLICY_ID;
            throw new CorruptedSourceException("Unknown PolicyReference");
        }
        throw new CorruptedSourceException("No PolicyReference found for Operation");
    }

    /**
     * Implementation of the {@link ParticipantPolicy}:.
     */
    private static class ParticipantPolicyImpl implements ParticipantPolicy {

        /** Value of "role" attribute. */
        private String role;

        /** Value of "service" attribute. */
        private String service;

        /** Value of "serviceLocation" attribute. */
        private String serviceLocation;

        /** Value of "provider" attribute. */
        private String provider;

        /** Value of "version" attribute. */
        private String version;

        /** Value of "id" attribute. */
        private String id;

        /** Value of "name" attribute. */
        private String name;

        /** Default policy id. */
        private OperationPolicyIdentity defaultPolicyId;

        /** Policy id values mapped to operation names. */
        private Map policyIds = new HashMap();

        /** Read only view of {@link policyIds}. */
        private Map roPolicyIds;

        /**
         * Internal constructor for use by surrounding class only.
         */
        protected ParticipantPolicyImpl() {
            super();
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getDefaultPolicyIdentity()
         */
        public OperationPolicyIdentity getDefaultPolicyIdentity() {
            return this.defaultPolicyId;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getId()
         */
        public String getId() {
            return this.id;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getName()
         */
        public String getName() {
            return this.name;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getPolicyIdentityForOperation(java.lang.String)
         */
        public OperationPolicyIdentity getPolicyIdentityForOperation(final String operationName) {
            return (OperationPolicyIdentity) this.policyIds.get(operationName);
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getPolicyIdentityForOperationOrDefault(java.lang.String)
         */
        public OperationPolicyIdentity getPolicyIdentityForOperationOrDefault(final String operationName) {
            final OperationPolicyIdentity res = (OperationPolicyIdentity) this.policyIds.get(operationName);
            return (null == res) ? this.defaultPolicyId : res;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getPolicyIdentityMap()
         */
        public Map getPolicyIdentityMap() {
            return this.roPolicyIds;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getProvider()
         */
        public String getProvider() {
            return this.provider;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getRole()
         */
        public String getRole() {
            return this.role;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getService()
         */
        public String getService() {
            return this.service;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getServiceLocation()
         */
        public String getServiceLocation() {
            return this.serviceLocation;
        }

        /*
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ParticipantPolicy#getVersion()
         */
        public String getVersion() {
            return this.version;
        }
    }
}
