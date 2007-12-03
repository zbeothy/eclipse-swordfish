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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.Definition;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.internalproxy.InternalProxy;
import org.eclipse.swordfish.core.components.internalproxy.ProviderException;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl;
import org.eclipse.swordfish.core.components.srproxy.ServiceInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceNotFoundException;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderNotFoundException;
import org.eclipse.swordfish.core.components.srproxy.SrProxy;
import org.eclipse.swordfish.core.components.srproxy.SrProxyCommunicationException;
import org.eclipse.swordfish.core.components.srproxy.SrProxyException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.DOM2Writer;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.eclipse.swordfish.policytrader.impl.PolicyFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class is a proxy to the service registry. If it can't find descriptions in its local cache,
 * it queries the remote service registry. Doing so, it acts as a InternalSBB API consumer.
 * 
 */
public class SrProxyBean extends AbstractSrProxy {

    /** XML tag in service registry response. */
    private static final String XML_TAG_ANNOTATIONSUMMARY = "annotationSummary";

    /** XML tag in service registry response. */
    private static final String XML_TAG_SERVICEPROVIDERDETAIL = "serviceProviderDetail";

    /** XML tag in service registry response. */
    private static final String XML_TAG_ANNOTATIONCONTENT = "annotationContent";

    /** XML tag in service registry response. */
    private static final String XML_TAG_PARTICIPANTPOLICYCONTENT = "participantPolicyContent";

    /** Logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(SrProxyBean.class);

    /** The Constant proxyLog. */
    private static final Log PROXY_LOG = SBBLogFactory.getLog(SrProxy.class);

    /** XML template handler. */
    private TemplateHandler xfindServiceProvidersTemplateHandler;

    /** XML template handler. */
    private TemplateHandler xfindServiceProviderTemplateHandler;

    /** XML template handler. */
    private TemplateHandler xfindAndGetAnnotationTemplateHandler;

    /** XML template handler. */
    private TemplateHandler xgetParticipantPolicyTemplateHandler;

    /** XML template handler. */
    private TemplateHandler xgetWsPolicyTemplateHandler;

    /** XML template handler. */
    private TemplateHandler xfindParticipantPolicyTemplateHandler;

    /** XML template handler. */
    private TemplateHandler xgetSDXTemplateHandler;

    /** XML template handler. */
    private TemplateHandler xfindSDXTemplateHandler;

    /** XML template handler. */
    private TemplateHandler xfindSPDXandPoliciesTemplateHandler;

    /** sr provider agg desc. */
    private CompoundServiceDescription srProviderCompDesc = null;

    /** sr provider service qname. */
    private QName srProviderServiceQName = null;

    /** service qname as string*. */
    private String srProviderServiceName = null;

    /** port type qname. */
    private QName srProviderPortTypeQName = null;

    /** port type qname as string*. */
    private String srProviderPortType = null;

    /** internal proxy. */
    private InternalProxy consumerProxy = null;

    /**
     * Creates a new SrProxy.
     * 
     * @throws Exception
     *         exception
     */
    public SrProxyBean() throws Exception {
        super();
    }

    /**
     * Gets the consumer proxy.
     * 
     * @return InternalProxy proxy
     */
    public InternalProxy getConsumerProxy() {
        return this.consumerProxy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getProviderPoliciesRemote(javax.xml.namespace.QName)
     */
    public List getProviderPoliciesRemote(final QName serviceQName) throws SrProxyException {
        // dirty workaround: skip retrieving provider policies from the SR in
        // case we are looking for its own policies
        if (serviceQName.toString().equals(this.srProviderServiceQName.toString())) {
            LOG.debug("Skipping getProviderPoliciesRemote() for " + serviceQName.toString());
            return new ArrayList();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("SrProxy calling remote service registry (xfind_and_get_annotation)...");
        }
        List result = new ArrayList();

        String uddiKey = this.getCache().getUddiKey(serviceQName.toString());
        if (null == uddiKey) {
            LOG
                .debug("No UDDI key for service " + serviceQName.toString()
                        + ". Provider policies cannot be fetched from registry.");
            return result;
        }
        // //////////////////////////////////////////////////////////////
        // prepare xfind_and_get_annotation request XML
        // //////////////////////////////////////////////////////////////
        String templateFilename = "xfind_and_get_annotationTemplate.xml";

        String xmlRequest = null;
        try {
            xmlRequest =
                    this.xfindAndGetAnnotationTemplateHandler.fillInTemplate(templateFilename, new String[] {"UDDI_KEY",
                            "ANNOTATION_TYPE"}, new String[] {uddiKey, "Policy"});
        } catch (Exception e) {
            // Template cannot be found
            throw new SrProxyCommunicationException(e, "Template file cannot be found.", templateFilename);
        }

        String outMessage = null;
        try {
            outMessage = this.consumerProxy.invokeService(this.srProviderCompDesc, "xfind_and_get_annotation", xmlRequest);
        } catch (Exception e) {
            throw new SrProxyCommunicationException(e);
        }
        Document docResponse = TransformerUtil.docFromString(outMessage);

        // Build ParticipantPolicy from response
        Element annotationListElem = docResponse.getDocumentElement();
        if (annotationListElem == null)
            throw new SrProxyCommunicationException("Error while communicating to the SR Provider. The response returned is null.");

        List annotationContentList = this.getAllChildElementsByLocalName(annotationListElem, XML_TAG_ANNOTATIONCONTENT);

        if (0 == annotationContentList.size())
            throw new SrProxyException("No provider policies found for provider " + serviceQName.toString());
        for (Iterator iter = annotationContentList.iterator(); iter.hasNext();) {
            iter.next();
            Element policyElem = null; // extractProviderPolicy(annotationContentElem);
            if (null == policyElem)
                throw new SrProxyException(
                        "Invalid response structure: policy element not found in annotation content. Requested provider was "
                                + serviceQName.toString());
        }
        return result;
    }

    /**
     * Gets the SR provider desc from cache.
     * 
     * @throws Exception
     *         exception
     */
    public void getSRProviderDescFromCache() throws Exception {

        Definition sdx = this.getCache().getSDX(this.srProviderPortTypeQName.toString());
        if (null == sdx)
            throw new ServiceProviderNotFoundException(
                    "Service description for service registry not found in local cache. Check your configuration.");
        ServiceInfo serviceDescription = new ServiceInfoImpl(sdx);
        Definition spdx = this.getCache().getSPDX(this.srProviderServiceQName.toString());
        if (null == spdx)
            throw new ServiceProviderNotFoundException(
                    "Service provider description for service registry not found in local cache. Check your configuration.");
        AgreedPolicy agreedPolicy =
                this.getCache().getAgreedPolicy(this.srProviderPortTypeQName.toString(), this.srProviderServiceQName.toString());
        if (null == agreedPolicy)
            throw new ServiceProviderNotFoundException(
                    "AgreedPolicy for service registry not found in local cache. Check your configuration.");
        ServiceProviderInfo providerInfo = new ServiceProviderInfoImpl(spdx, agreedPolicy);
        this.srProviderCompDesc =
                new CompoundServiceDescriptionImpl(serviceDescription.getServiceDescription(), providerInfo
                    .getServiceProviderDescription(), providerInfo.getAgreedPolicy(), null);
    }

    /**
     * Gets the sr provider port type.
     * 
     * @return String service name
     */
    public String getSrProviderPortType() {
        return this.srProviderPortType;
    }

    /**
     * Gets the sr provider service name.
     * 
     * @return String service name
     */
    public String getSrProviderServiceName() {
        return this.srProviderServiceName;
    }

    /**
     * Gets the sr provider service Q name.
     * 
     * @return Returns the srProviderServiceQName.
     */
    public synchronized QName getSrProviderServiceQName() {
        return this.srProviderServiceQName;
    }

    public TemplateHandler getXfindAndGetAnnotationTemplateHandler() {
        return this.xfindAndGetAnnotationTemplateHandler;
    }

    public TemplateHandler getXfindParticipantPolicyTemplateHandler() {
        return this.xfindParticipantPolicyTemplateHandler;
    }

    public TemplateHandler getXfindSDXTemplateHandler() {
        return this.xfindSDXTemplateHandler;
    }

    public TemplateHandler getXfindServiceProvidersTemplateHandler() {
        return this.xfindServiceProvidersTemplateHandler;
    }

    public TemplateHandler getXfindServiceProviderTemplateHandler() {
        return this.xfindServiceProviderTemplateHandler;
    }

    public TemplateHandler getXfindSPDXandPoliciesTemplateHandler() {
        return this.xfindSPDXandPoliciesTemplateHandler;
    }

    public TemplateHandler getXgetParticipantPolicyTemplateHandler() {
        return this.xgetParticipantPolicyTemplateHandler;
    }

    public TemplateHandler getXgetSDXTemplateHandler() {
        return this.xgetSDXTemplateHandler;
    }

    public TemplateHandler getXgetWsPolicyTemplateHandler() {
        return this.xgetWsPolicyTemplateHandler;
    }

    /**
     * Init.
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.framework.activity.Initializable#initialize()
     */
    public void init() throws Exception {
        this.xfindSDXTemplateHandler = new TemplateHandler();
        this.xfindServiceProvidersTemplateHandler = new TemplateHandler();
        this.xfindServiceProviderTemplateHandler = new TemplateHandler();
        this.xfindSPDXandPoliciesTemplateHandler = new TemplateHandler();
        this.xgetSDXTemplateHandler = new TemplateHandler();
        this.xfindAndGetAnnotationTemplateHandler = new TemplateHandler();
        this.xgetParticipantPolicyTemplateHandler = new TemplateHandler();
        this.xgetWsPolicyTemplateHandler = new TemplateHandler();
        this.xfindParticipantPolicyTemplateHandler = new TemplateHandler();
        this.getSRProviderDescFromCache();
    }

    /**
     * Sets the consumer proxy.
     * 
     * @param consumerProxy
     *        proxy
     */
    public void setConsumerProxy(final InternalProxy consumerProxy) {
        this.consumerProxy = consumerProxy;
    }

    /**
     * Sets the sr provider port type.
     * 
     * @param srProviderPortType
     *        service name
     */
    public void setSrProviderPortType(final String srProviderPortType) {
        this.srProviderPortType = srProviderPortType;
        this.srProviderPortTypeQName = new QName(srProviderPortType);
    }

    /**
     * Sets the sr provider service name.
     * 
     * @param srProviderServiceName
     *        service name
     */
    public void setSrProviderServiceName(final String srProviderServiceName) {
        this.srProviderServiceName = srProviderServiceName;
        this.srProviderServiceQName = new QName(srProviderServiceName);
    }

    /**
     * Sets the sr provider service Q name.
     * 
     * @param srProviderServiceQName
     *        The srProviderServiceQName to set.
     */
    public synchronized void setSrProviderServiceQName(final QName srProviderServiceQName) {
        this.srProviderServiceQName = srProviderServiceQName;
    }

    public void setXfindAndGetAnnotationTemplateHandler(final TemplateHandler xfindAndGetAnnotationTemplateHandler) {
        this.xfindAndGetAnnotationTemplateHandler = xfindAndGetAnnotationTemplateHandler;
    }

    public void setXfindParticipantPolicyTemplateHandler(final TemplateHandler xfindParticipantPolicyTemplateHandler) {
        this.xfindParticipantPolicyTemplateHandler = xfindParticipantPolicyTemplateHandler;
    }

    public void setXfindSDXTemplateHandler(final TemplateHandler xfindSDXTemplateHandler) {
        this.xfindSDXTemplateHandler = xfindSDXTemplateHandler;
    }

    public void setXfindServiceProvidersTemplateHandler(final TemplateHandler xfindServiceProvidersTemplateHandler) {
        this.xfindServiceProvidersTemplateHandler = xfindServiceProvidersTemplateHandler;
    }

    public void setXfindServiceProviderTemplateHandler(final TemplateHandler xfindServiceProviderTemplateHandler) {
        this.xfindServiceProviderTemplateHandler = xfindServiceProviderTemplateHandler;
    }

    public void setXfindSPDXandPoliciesTemplateHandler(final TemplateHandler xfindSPDXandPoliciesTemplateHandler) {
        this.xfindSPDXandPoliciesTemplateHandler = xfindSPDXandPoliciesTemplateHandler;
    }

    public void setXgetParticipantPolicyTemplateHandler(final TemplateHandler xgetParticipantPolicyTemplateHandler) {
        this.xgetParticipantPolicyTemplateHandler = xgetParticipantPolicyTemplateHandler;
    }

    public void setXgetSDXTemplateHandler(final TemplateHandler xgetSDXTemplateHandler) {
        this.xgetSDXTemplateHandler = xgetSDXTemplateHandler;
    }

    public void setXgetWsPolicyTemplateHandler(final TemplateHandler xgetWsPolicyTemplateHandler) {
        this.xgetWsPolicyTemplateHandler = xgetWsPolicyTemplateHandler;
    }

    /**
     * Search a wsdlContentList or serviceProviderDetail for a definitions element. In all cases
     * (wsdlContentList and serviceProviderDetail), a definitions element is two levels below the
     * root node of the service registry response. Note: When a wsdlContentList element is
     * processed, a partnerDescriptions element may the first node that is two levels below the root
     * node, in which case the siblings of that partnerDescriptions element must be searched.
     * 
     * @param responseNode
     *        a WsdlContentList node or a serviceProvidersDetail node
     * 
     * @return Element the definitions element, or null if no definitions was found
     */
    Element extractDefinitions(final Element responseNode) {

        Element wsdlContentNode = this.getFirstChildElementByLocalName(responseNode, "wsdlContent");

        // Check if wsdlContent Element exists
        if (null == wsdlContentNode) {
            LOG.info("No description found. No " + "wsdlContent" + " element found.");
            return null;
        }

        Element definitionsNode = this.getFirstChildElementByLocalName(wsdlContentNode, "definitions");

        // Check if definitions Element exists
        if (null == definitionsNode) {
            LOG.info("No description found.");
            return null;
        }

        return definitionsNode;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#findServiceProviderRemote(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    ServiceProviderInfo findServiceProviderRemote(final QName porttypeQname, final String policyId)
            throws SrProxyCommunicationException, SrProxyException {
        LOG.debug("SrProxy calling remote service registry (xlookUp_serviceProviderRemote)...");
        // Prepare request XML
        final String policyID = policyId;
        final String templateFilename = "xlookUp_serviceProviderTemplate.xml";
        String xmlRequest = null;
        try {
            xmlRequest =
                    this.xfindServiceProviderTemplateHandler.fillInTemplate(templateFilename, new String[] {"SDX_LOCAL",
                            "SDX_NAMESPACE", "PARTICIPANT_POLICY_ID"}, new String[] {porttypeQname.getLocalPart().trim(),
                            porttypeQname.getNamespaceURI().trim(), policyID.toString()});
        } catch (Exception e) {
            // Template cannot be found
            throw new SrProxyCommunicationException(e, "Template file cannot be found.", templateFilename);
        }

        String outMessage = null;
        try {
            outMessage = this.consumerProxy.invokeService(this.srProviderCompDesc, "xlookUp_serviceProvider", xmlRequest);
        } catch (ProviderException e) {
            LOG.error("SrProxyCommunication Exception", e);
            throw new SrProxyCommunicationException("Service Registry sent a Fault message.");
        } catch (Exception e) {
            throw new SrProxyCommunicationException(e);
        }
        Document docResponse = TransformerUtil.docFromString(outMessage);
        // Build ServiceProviderInfo from response
        Element serviceProviderDetail = docResponse.getDocumentElement();
        if (serviceProviderDetail == null)
            throw new SrProxyCommunicationException("Error while communicating to the SR Provider. The response returned is null.");
        Element descriptionElem = this.extractDefinitions(serviceProviderDetail);
        if (null == descriptionElem)
            throw new ServiceProviderNotFoundException(
                    "Invalid response structure: definitions element not found in serviceProviderDetail. Requested porttype was "
                            + porttypeQname.toString() + " not found.");
        Element agreedPolicyElem = this.extractAgreedPolicyFromResponse(serviceProviderDetail);
        NamedNodeMap attrs = serviceProviderDetail.getAttributes();
        try {
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                if (name.startsWith("xmlns:")) {
                    agreedPolicyElem.setAttribute(name, attr.getNodeValue());
                } else if (name.startsWith("xmlns")) {
                    agreedPolicyElem.setAttribute(name, attr.getNodeValue());
                }
            }
        } catch (Exception e) {
            LOG.error("SrProxyCommunicationException", e);
            // Service Registry response is incomplete
            throw new SrProxyCommunicationException("Service Registry response is incomplete.");
        }

        if ((null == descriptionElem) || (null == agreedPolicyElem)) // Service Registry response
            // is incomplete
            throw new SrProxyCommunicationException("Service Registry response is incomplete.");

        AgreedPolicy agreedPolicy = null;
        try {
            agreedPolicy = AgreedPolicyFactory.getInstance().createFrom(agreedPolicyElem);
            this.getCache().putAgreedPolicy(agreedPolicy);
        } catch (Exception e) {
            LOG.error("Exception in Agreed Policy", e);
        }

        Definition spDescDefinition = null;

        try {
            spDescDefinition = this.getDefinitionHelper().elementToDefinition(descriptionElem);
            this.getCache().putSPDX(spDescDefinition);
        } catch (Exception e) {
            throw new SrProxyException(e);
        }

        ServiceProviderInfo info = new ServiceProviderInfoImpl(spDescDefinition, agreedPolicy);

        LOG.debug("SrProxy calling remote service registry (xfind_serviceProvidersRemote)...done.");

        return info;
    }

    /**
     * Find service providers remote.
     * 
     * @param porttypeQname
     *        the porttype qname
     * @param policyId
     *        the policy id
     * 
     * @return the collection
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     * @throws ServiceProviderNotFoundException
     * 
     * @see org.eclipse.swordfish.sregproxy.AbstractSrProxy#xfind_serviceProviderRemote(
     *      javax.xml.namespace.QName, org.eclipse.swordfish.policy.participant.ParticipantPolicy)
     */
    Collection findServiceProvidersRemote(final QName porttypeQname, final String policyId) throws SrProxyCommunicationException,
            SrProxyException, ServiceProviderNotFoundException {
        LOG.debug("SrProxy calling remote service registry (xlookUp_serviceProvidersRemote)...");
        ArrayList providerList = new ArrayList();
        // Prepare request XML
        final String policyID = policyId;
        final String templateFilename = "xlookUp_serviceProvidersTemplate.xml";

        String xmlRequest = null;
        try {
            xmlRequest =
                    this.xfindServiceProvidersTemplateHandler.fillInTemplate(templateFilename, new String[] {"SDX_LOCAL",
                            "SDX_NAMESPACE", "PARTICIPANT_POLICY_ID"}, new String[] {porttypeQname.getLocalPart().trim(),
                            porttypeQname.getNamespaceURI().trim(), policyID});
        } catch (Exception e) {
            // Template cannot be found
            throw new SrProxyCommunicationException(e, "Template file cannot be found.", templateFilename);
        }

        String outMessage = null;
        try {
            outMessage = this.consumerProxy.invokeService(this.srProviderCompDesc, "xlookUp_serviceProviders", xmlRequest);
        } catch (ProviderException e) {
            LOG.error("ProviderException", e);
            throw new SrProxyCommunicationException("Service Registry sent a Fault message.");
        } catch (Exception e) {
            throw new SrProxyCommunicationException(e);
        }
        Document docResponse = TransformerUtil.docFromString(outMessage);
        // Build ServiceProviderInfo from response
        Element serviceProvidersDetail = docResponse.getDocumentElement();

        if (serviceProvidersDetail == null)
            throw new SrProxyCommunicationException("Error while communicating to the SR Provider. The response returned is null.");

        List serviceProviderDetailList = this.getAllChildElementsByLocalName(serviceProvidersDetail, XML_TAG_SERVICEPROVIDERDETAIL);

        if (0 == serviceProviderDetailList.size())
            throw new ServiceProviderNotFoundException("No matching service providers found for portType "
                    + porttypeQname.toString() + " and consumer policy " + policyID);

        for (int x = 0; x < serviceProviderDetailList.size(); x++) {

            Element serviceProviderDetail = (Element) serviceProviderDetailList.get(x);
            Element descriptionElem = this.extractDefinitions(serviceProviderDetail);
            if (null == descriptionElem)
                throw new ServiceProviderNotFoundException(
                        "Invalid response structure: definitions element not found in serviceProviderDetail. Requested porttype was "
                                + porttypeQname.toString() + " not found.");
            Element agreedPolicyElem = this.extractAgreedPolicyFromResponse(serviceProviderDetail);
            NamedNodeMap attrs = serviceProviderDetail.getAttributes();
            try {
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    String name = attr.getNodeName();
                    if (name.startsWith("xmlns:")) {
                        agreedPolicyElem.setAttribute(name, attr.getNodeValue());
                    } else if (name.startsWith("xmlns")) {
                        agreedPolicyElem.setAttribute(name, attr.getNodeValue());
                    }
                }
            } catch (Exception e) {
                LOG.error("SrProxyCommunication Exception", e);
                // Service Registry response is incomplete
                throw new SrProxyCommunicationException("Service Registry response is incomplete.");
            }

            if ((null == descriptionElem) || (null == agreedPolicyElem)) // Service Registry
                // response is
                // incomplete
                throw new SrProxyCommunicationException("Service Registry response is incomplete.");

            AgreedPolicy agreedPolicy = null;
            try {
                agreedPolicy = AgreedPolicyFactory.getInstance().createFrom(agreedPolicyElem);
                this.getCache().putAgreedPolicy(agreedPolicy);
            } catch (Exception e) {
                LOG.error("Exception in AgreedPolicy", e);
            }

            Definition spDescDefinition = null;

            try {
                spDescDefinition = this.getDefinitionHelper().elementToDefinition(descriptionElem);
                this.getCache().putSPDX(spDescDefinition);
            } catch (Exception e) {
                throw new SrProxyException(e);
            }

            ServiceProviderInfo info = new ServiceProviderInfoImpl(spDescDefinition, agreedPolicy);

            providerList.add(info);
        }

        LOG.debug("SrProxy calling remote service registry (xfind_serviceProvidersRemote)...done.");

        return providerList;
    }

    /**
     * Gets a List of child Element nodes with the given local name.
     * 
     * @param node
     *        the node from which to get the child Elements
     * @param childElementLocalName
     *        the local name
     * 
     * @return the List (empty if no child Element with the given name was found)
     */
    List getAllChildElementsByLocalName(final Node node, final String childElementLocalName) {
        List list = new ArrayList();

        NodeList nodeList = node.getChildNodes();
        String nodeName = "";
        for (int x = 0; x < nodeList.getLength(); x++) {
            Node candidateNode = nodeList.item(x);
            nodeName = candidateNode.getLocalName();

            if ((nodeName == null) || (!nodeName.equalsIgnoreCase(childElementLocalName))
                    || (Node.ELEMENT_NODE != candidateNode.getNodeType())) {
                continue;
            } else {
                list.add(candidateNode);
            }
        }
        return list;
    }

    /**
     * Gets a the first child Element node with the given local name.
     * 
     * @param node
     *        the node from which to get the first child Element
     * @param childElementLocalName
     *        the local name
     * 
     * @return the first child Element (or null if no child Element with the given name was found)
     */
    Element getFirstChildElementByLocalName(final Node node, final String childElementLocalName) {
        NodeList nodeList = node.getChildNodes();
        String nodeName = "";
        for (int x = 0; x < nodeList.getLength(); x++) {
            Node candidateNode = nodeList.item(x);
            nodeName = candidateNode.getLocalName();

            if ((nodeName == null) || (!nodeName.equalsIgnoreCase(childElementLocalName))
                    || (Node.ELEMENT_NODE != candidateNode.getNodeType())) {
                continue;
            } else
                return (Element) candidateNode;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getOperationPolicyRemote(java.lang.String)
     */
    OperationPolicy getOperationPolicyRemote(final String policyId) throws SrProxyCommunicationException, SrProxyException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("SrProxy calling remote service registry (xget_participantPolicy)...");
        }
        // //////////////////////////////////////////////////////////////
        // prepare xget_participantPolicy request XML
        // //////////////////////////////////////////////////////////////
        String templateFilename = "xget_wsPolicyTemplate.xml";

        String xmlRequest = null;
        try {
            xmlRequest =
                    this.xgetWsPolicyTemplateHandler.fillInTemplate(templateFilename, new String[] {"POLICY_ID"},
                            new String[] {policyId});
        } catch (Exception e) {
            // Template cannot be found
            throw new SrProxyCommunicationException(e, "Template file cannot be found.", templateFilename);
        }
        String outMessage = null;
        try {
            outMessage = this.consumerProxy.invokeService(this.srProviderCompDesc, "xget_wsPolicy", xmlRequest);
        } catch (Exception e) {
            throw new SrProxyCommunicationException(e);
        }
        Document docResponse = TransformerUtil.docFromString(outMessage);
        Element wsPolicyContentListElem = docResponse.getDocumentElement();
        if (wsPolicyContentListElem == null)
            throw new SrProxyCommunicationException("Error while communicating to the SR Provider. The response returned is null.");
        List wsPolicyContentList = this.getAllChildElementsByLocalName(wsPolicyContentListElem, "wsPolicyContent");

        if (0 == wsPolicyContentList.size()) throw new SrProxyException("No operation policy found for policy ID " + policyId);
        Element wsPolicyContentElem = (Element) wsPolicyContentList.iterator().next();
        Element policyElem = this.getFirstChildElementByLocalName(wsPolicyContentElem, "Policy");
        if (null == policyElem)
            throw new SrProxyException(
                    "Invalid response structure: Policy element not found in wsPolicyContent. Requested policy ID was " + policyId);
        OperationPolicy result;
        try {
            result = new PolicyFactoryImpl().createOperationPolicy(policyElem);
        } catch (Exception e) {
            throw new SrProxyException("Invalid operation Policy returned from registry for policy ID " + policyId);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getParticipantPolicyIdsRemote(javax.xml.namespace.QName)
     */
    List getParticipantPolicyIdsRemote(final QName providerId) throws SrProxyCommunicationException, SrProxyException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SrProxy calling remote service registry (xfind_participantPolicy)...");
        }
        // //////////////////////////////////////////////////////////////
        // prepare xfind_participantPolicy request XML
        // //////////////////////////////////////////////////////////////
        String templateFilename = "xfind_participantPolicyTemplate.xml";

        String xmlRequest = null;
        try {
            xmlRequest =
                    this.xfindParticipantPolicyTemplateHandler.fillInTemplate(templateFilename, new String[] {"PROVIDER_NAME",
                            "PROVIDER_NAMESPACE"}, new String[] {providerId.getLocalPart(), providerId.getNamespaceURI()});
        } catch (Exception e) {
            // Template cannot be found
            throw new SrProxyCommunicationException(e, "Template file cannot be found.", templateFilename);
        }
        String outMessage = null;
        try {
            outMessage = this.consumerProxy.invokeService(this.srProviderCompDesc, "xfind_participantPolicy", xmlRequest);
        } catch (Exception e) {
            throw new SrProxyCommunicationException(e);
        }
        Document docResponse = TransformerUtil.docFromString(outMessage);
        Element participantPolicySummaryListElem = docResponse.getDocumentElement();
        if (participantPolicySummaryListElem == null)
            throw new SrProxyCommunicationException("Error while communicating to the SR Provider. The response returned is null.");
        List participantPolicySummaryList =
                this.getAllChildElementsByLocalName(participantPolicySummaryListElem, "participantPolicySummary");

        if (0 == participantPolicySummaryList.size()) return new ArrayList(0);
        List result = new ArrayList(participantPolicySummaryList.size());
        for (Iterator iter = participantPolicySummaryList.iterator(); iter.hasNext();) {
            Element participantPolicySummaryElem = (Element) iter.next();
            Element idElem = this.getFirstChildElementByLocalName(participantPolicySummaryElem, "ID");
            Node n = idElem.getFirstChild();
            if ((null != n) && (Node.TEXT_NODE == n.getNodeType())) {
                result.add(n.getNodeValue());
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getParticipantPolicyRemote(java.lang.String)
     */
    ParticipantPolicy getParticipantPolicyRemote(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SrProxy calling remote service registry (xget_participantPolicy)...");
        }
        // //////////////////////////////////////////////////////////////
        // prepare xget_participantPolicy request XML
        // //////////////////////////////////////////////////////////////
        String templateFilename = "xget_participantPolicyTemplate.xml";

        String xmlRequest = null;
        try {
            xmlRequest =
                    this.xgetParticipantPolicyTemplateHandler.fillInTemplate(templateFilename, new String[] {"POLICY_ID"},
                            new String[] {policyId});
        } catch (Exception e) {
            // Template cannot be found
            throw new SrProxyCommunicationException(e, "Template file cannot be found.", templateFilename);
        }
        String outMessage = null;
        try {
            outMessage = this.consumerProxy.invokeService(this.srProviderCompDesc, "xget_participantPolicy", xmlRequest);
        } catch (Exception e) {
            throw new SrProxyCommunicationException(e);
        }
        Document docResponse = TransformerUtil.docFromString(outMessage);
        Element participantPolicyListElem = docResponse.getDocumentElement();
        if (participantPolicyListElem == null)
            throw new SrProxyCommunicationException("Error while communicating to the SR Provider. The response returned is null.");
        List participantPolicyContentList =
                this.getAllChildElementsByLocalName(participantPolicyListElem, XML_TAG_PARTICIPANTPOLICYCONTENT);

        if (0 == participantPolicyContentList.size())
            throw new SrProxyException("No participant policy found for policy ID " + policyId);
        Element participantPolicyContentElem = (Element) participantPolicyContentList.iterator().next();
        Element policyElem = this.getFirstChildElementByLocalName(participantPolicyContentElem, "ParticipantPolicy");
        if (null == policyElem)
            throw new SrProxyException(
                    "Invalid response structure: ParticipantPolicy element not found in <participantPolicyContent. Requested policy ID was "
                            + policyId);
        ParticipantPolicy result;
        try {
            result = new PolicyFactoryImpl().createParticipantPolicy(policyElem);
        } catch (Exception e) {
            throw new SrProxyException("Invalid ParticipantPolicy returned from registry for policy ID " + policyId);
        }
        return result;
    }

    /**
     * Gets the service description remote.
     * 
     * @param qualifiedServiceName
     *        the qualified service name
     * 
     * @return the service description remote
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     * @throws ServiceNotFoundException
     * 
     * @see org.eclipse.swordfish.sregproxy.AbstractSrProxy#xfind_and_get_serviceDescriptionRemote(QName)
     */
    ServiceInfo getServiceDescriptionRemote(final QName qualifiedServiceName) throws SrProxyCommunicationException,
            SrProxyException, ServiceNotFoundException {
        LOG.debug("SrProxy calling remote service registry (xfind_and_get_serviceDescriptionRemote)...");
        // Prepare request XML
        final String templateFilename = "xfind_and_get_serviceDescriptionTemplate.xml";

        String xmlRequest = null;
        try {
            xmlRequest =
                    this.xfindSDXTemplateHandler
                        .fillInTemplate(templateFilename, new String[] {"SDX_LOCAL", "SDX_NAMESPACE"}, new String[] {
                                qualifiedServiceName.getLocalPart().trim(), qualifiedServiceName.getNamespaceURI().trim()});
        } catch (Exception e) {
            // Template cannot be found
            throw new SrProxyCommunicationException(e, "Template file cannot be found.", templateFilename);
        }

        String outMessage = null;

        try {
            outMessage = this.consumerProxy.invokeService(this.srProviderCompDesc, "xfind_and_get_serviceDescription", xmlRequest);
        } catch (Exception e) {
            throw new SrProxyCommunicationException(e);
        }
        Document docResponse = TransformerUtil.docFromString(outMessage);
        // Build ServiceDescription from response
        Element wsdlContentListElem = docResponse.getDocumentElement();

        if (null == wsdlContentListElem)
            throw new SrProxyCommunicationException("Error while communicating to the SR Provider. The response returned is null.");

        ServiceInfo description = null;
        Definition providerDefinition = null;

        try {
            Element definitionsElem = this.extractDefinitions(wsdlContentListElem);
            if (null == definitionsElem)
                throw new ServiceNotFoundException("Requested Service Description " + qualifiedServiceName.toString()
                        + " not found.");

            providerDefinition = this.getDefinitionHelper().elementToDefinition(definitionsElem);
            this.getCache().putSDX(providerDefinition);
        } catch (Exception e) {
            throw new SrProxyException(e, "Cannot parse definitions element.");
        }

        description = new ServiceInfoImpl(providerDefinition);

        LOG.info("SrProxy calling remote service registry (xfind_and_get_serviceDescriptionRemote)...done.");

        return description;
    }

    /**
     * Gets the service provider remote.
     * 
     * @param qualifiedServiceName
     *        the qualified service name
     * 
     * @return the service provider remote
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     * @throws ServiceProviderNotFoundException
     * 
     * @see org.eclipse.swordfish.sregproxy.AbstractSrProxy#xfind_serviceProviderRemote(
     *      javax.xml.namespace.QName, org.eclipse.swordfish.policy.participant.ParticipantPolicy)
     */
    ServiceProviderInfo getServiceProviderRemote(final QName qualifiedServiceName) throws SrProxyCommunicationException,
            SrProxyException, ServiceProviderNotFoundException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("SrProxy calling remote service registry (xfind_serviceProviderAndPoliciesRemote)...");
        }

        // Two calls are necessary to realize this

        // //////////////////////////////////////////////////////////////
        // prepare xfind_and_get_serviceProviderDescription request XML
        // //////////////////////////////////////////////////////////////
        String templateFilename = "xfind_and_get_serviceProviderDescriptionTemplate.xml";

        String xmlRequest = null;
        try {
            xmlRequest =
                    this.xfindSPDXandPoliciesTemplateHandler.fillInTemplate(templateFilename, new String[] {"SPDX_LOCAL",
                            "SPDX_NAMESPACE"}, new String[] {qualifiedServiceName.getLocalPart().trim(),
                            qualifiedServiceName.getNamespaceURI().trim()});
        } catch (Exception e) {
            // Template cannot be found
            throw new SrProxyCommunicationException(e, "Template file cannot be found.", templateFilename);
        }

        String outMessage = null;
        try {
            outMessage =
                    this.consumerProxy.invokeService(this.srProviderCompDesc, "xfind_and_get_serviceProviderDescription",
                            xmlRequest);
        } catch (Exception e) {
            throw new SrProxyCommunicationException(e);
        }
        Document docResponse = TransformerUtil.docFromString(outMessage);

        // Build ServiceDescription from response
        Element wsdlContentListElem = docResponse.getDocumentElement();

        if (wsdlContentListElem == null)
            throw new SrProxyCommunicationException("Error while communicating to the SR Provider. The response returned is null.");

        Element definitionsElem = this.extractDefinitions(wsdlContentListElem);

        if (null == definitionsElem)
            throw new ServiceProviderNotFoundException("Requested Service Provider Description " + qualifiedServiceName.toString()
                    + " not found.");

        Definition providerDefinition = null;

        try {
            providerDefinition = this.getDefinitionHelper().elementToDefinition(definitionsElem);
            this.getCache().putSPDX(providerDefinition);
        } catch (Exception e) {
            throw new SrProxyException(e, "Cannot parse definitions element.");
        }

        ServiceProviderInfo info = new ServiceProviderInfoImpl(providerDefinition, null);
        LOG.debug("SrProxy calling remote service registry (xfind_serviceProviderAndPoliciesRemote)...done.");

        return info;
    }

    /**
     * Extracts the AgreedPolicy from Service Registry response XML.
     * 
     * @param responseNode
     *        either serviceProviderDetail, may be null
     * 
     * @return the AgreedPolicy as Element or null if there is no AgreedPolicy in the responseNode
     *         or if responseNode is null
     */
    private Element extractAgreedPolicyFromResponse(final Node responseNode) {
        Element result = null;

        if (null == responseNode) return null;

        Node wsdlContentNode = responseNode.getFirstChild();
        while ((null != wsdlContentNode) && (wsdlContentNode instanceof Text)) {
            wsdlContentNode = wsdlContentNode.getNextSibling();
        }
        if (null == wsdlContentNode) {
            // Cannot extract AgreedPolicy from Service Registry response
            // Not even WSDL was found in response
            // This should not happen: invalid response was received
            LOG.debug("Service Registry Proxy cannot extract AgreedPolicy from response.");
            return null;
        }

        Node policyContentNode = wsdlContentNode.getNextSibling();
        while ((null != policyContentNode) && (policyContentNode instanceof Text)) {
            policyContentNode = policyContentNode.getNextSibling();
        }
        if (null == policyContentNode) {
            // Cannot extract AgreedPolicy from Service Registry response
            // No node for AgreedPolicy found next to WSDL in response
            // This should not happen: invalid response was received
            LOG.debug("Service Registry Proxy cannot extract AgreedPolicy from response.");
            return null;
        }

        Node agreedPolicy = policyContentNode.getFirstChild();
        while ((null != agreedPolicy) && (agreedPolicy instanceof Text)) {
            agreedPolicy = agreedPolicy.getNextSibling();
        }
        if (agreedPolicy.getLocalName().equals(XML_TAG_ANNOTATIONSUMMARY)) {
            agreedPolicy = agreedPolicy.getNextSibling();
            while ((null != agreedPolicy) && (agreedPolicy instanceof Text)) {
                agreedPolicy = agreedPolicy.getNextSibling();
            }
        }
        if (null == agreedPolicy) {
            // No agreed tag in policyContent node, this should not happen:
            // invalid response was received
            LOG.debug("Service Registry Proxy cannot extract AgreedPolicy from response.");
            return null;
        }

        result = (Element) agreedPolicy;

        if (PROXY_LOG.isInfoEnabled()) {
            String msg = DOM2Writer.nodeToPrettyString(result);
            PROXY_LOG.info(msg);
        }

        return result;
    }

}
