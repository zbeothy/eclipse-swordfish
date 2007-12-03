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
package org.eclipse.swordfish.core.components.resolver.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddress;
import org.eclipse.swordfish.core.components.endpointreferenceresolver.impl.EPRHelper;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.iapi.Transport;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.iapi.impl.WSDLHelper;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkType;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.components.srproxy.impl.DefinitionHelper;
import org.eclipse.swordfish.core.components.srproxy.impl.SPDXPortImpl;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.DOM2Writer;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.exception.InfrastructureRuntimeException;
import org.eclipse.swordfish.policy.selector.ClassSelector;
import org.eclipse.swordfish.policy.selector.RegexNameSelector;
import org.eclipse.swordfish.policy.util.TermIterator;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The Class CompoundServiceDescriptionImpl.
 */
public class CompoundServiceDescriptionImpl implements CompoundServiceDescription {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(CompoundServiceDescriptionImpl.class);

    /** The sdx. */
    private Definition sdx;

    /** The spdx. */
    private Definition spdx;

    /** The partner description. */
    private CompoundServiceDescription partnerDescription = null;

    /** The agreed policy. */
    private AgreedPolicy agreedPolicy;

    /** The port type. */
    private PortType portType;

    /** The partner port type. */
    private PortType partnerPortType = null;

    /** The service. */
    private Service service;

    /** The callback service. */
    private Service callbackService;

    /** The identity. */
    private UnifiedParticipantIdentity identity;

    /** The operations. */
    private Map operations = null;

    /** The partner operations. */
    private Map partnerOperations = null;

    /** The populated. */
    private boolean populated = false;

    /** The is partner. */
    private boolean isPartner = false;

    /** The sdx schema list. */
    private List sdxSchemaList = null;

    /** The provider policies. */
    private Map providerPolicies = new HashMap();

    /**
     * Instantiates a new compound service description impl.
     * 
     * @param sdx
     *        the sdx
     * @param spdx
     *        the spdx
     * @param agreedPolicy
     *        the agreed policy
     * @param providerPolicies
     *        the provider policies
     * @param participant
     *        the participant
     * 
     * @throws Exception
     */
    public CompoundServiceDescriptionImpl(final Definition sdx, final Definition spdx, final AgreedPolicy agreedPolicy,
            final List providerPolicies, final UnifiedParticipantIdentity participant) throws Exception {
        this(sdx, spdx, agreedPolicy, participant, false);
        if (null != providerPolicies) {
            for (Iterator iter = providerPolicies.iterator(); iter.hasNext();) {
                ParticipantPolicy policy = (ParticipantPolicy) iter.next();
                this.providerPolicies.put(policy.getId(), policy);
            }
        }
    }

    /**
     * Instantiates a new compound service description impl.
     * 
     * @param sdx
     *        the sdx
     * @param spdx
     *        the spdx
     * @param agreedPolicy
     *        the agreed policy
     * @param identity
     *        the identity
     * 
     * @throws Exception
     */
    public CompoundServiceDescriptionImpl(final Definition sdx, final Definition spdx, final AgreedPolicy agreedPolicy,
            final UnifiedParticipantIdentity identity) throws Exception {
        this(sdx, spdx, agreedPolicy, identity, false);
    }

    /**
     * Instantiates a new compound service description impl.
     * 
     * @param sdx
     *        the sdx
     * @param spdx
     *        the spdx
     * @param agreedPolicy
     *        the agreed policy
     * @param identity
     *        the identity
     * @param isPartner
     *        the is partner
     * 
     * @throws Exception
     */
    public CompoundServiceDescriptionImpl(final Definition sdx, final Definition spdx, final AgreedPolicy agreedPolicy,
            final UnifiedParticipantIdentity identity, final boolean isPartner) throws Exception {
        if (sdx == null) throw new Exception("Service description is null.");
        if (spdx == null) throw new Exception("Service provider description is null.");
        this.sdx = sdx;
        this.spdx = spdx;
        this.agreedPolicy = agreedPolicy;
        this.identity = identity;
        this.isPartner = isPartner;
        try {
            this.populate();
        } catch (WSDLException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Chooses the WSDL port to use as the endpoint for the operation referred to by the supplied
     * operation name The choice depends on 1. which ports are available in the service description
     * 2. what types of transport they are bound to 3. what types of transport are defined by the
     * agreed policy for the operation
     * 
     * @param operationName
     *        the operation name (local part of WSDL operation name)
     * @param defaultTransport
     *        the default transport
     * 
     * @return the selected SPDXPort
     */

    public SPDXPort choosePort(final String operationName, final String defaultTransport) {

        // get all ports that provide the operation
        SPDXPort[] ports = this.getSupportedPorts(operationName);
        Map usablePorts = new HashMap();
        for (int i = 0; i < ports.length; i++) {
            usablePorts.put(ports[i].getTransport(), ports[i]);
        }
        List agreedTransports = this.getAgreedTransports(operationName, Scope.REQUEST);
        // if the agreed policy decribes one or more transports to be used,
        // determine the intersection between the described transports and
        // the ones available in the provider descriptions
        if (!agreedTransports.isEmpty()) {
            usablePorts = this.getIntersection(usablePorts, agreedTransports);
            // if the intersection is empty, the policy cannot be fulfilled
            if (usablePorts.isEmpty())
                throw new InfrastructureRuntimeException("The agreed policy prescribes to use (a) specific transport(s), "
                        + "but no corresponding port is defined in the service description.");
        }
        if (usablePorts.size() == 1)
            // if the intersection contains exactly one element, this is the
            // port to be used
            return (SPDXPort) usablePorts.values().iterator().next();
        else if (usablePorts.size() > 1) {
            // if the intersection contains more than one element, we check
            // whether one of them matches the default transport specified in
            // the configuration
            SPDXPort port = (SPDXPort) usablePorts.get(Transport.fromString(defaultTransport));
            if (null != port) // if this is the case, we use that one
                return port;
            else
                // otherwise we bail out and tell the boss that we don't know
                // what to do now
                throw new InfrastructureRuntimeException(
                        "Cannot decide which transport to use: The service description and the agreed policy allow for more than one transport to be used "
                                + "but none of them matches the default transport defined in the configuration.");
        } else
            // if no usable port is available, we can't do anything about it
            throw new InfrastructureRuntimeException("No ports are defined in the service provider description for "
                    + this.getServiceQName().toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#createWSDL()
     */
    public Document createWSDL() {
        return WSDLHelper.createWSDL(this.portType, this.service.getQName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getAgreedPolicy()
     */
    public AgreedPolicy getAgreedPolicy() {
        return this.agreedPolicy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getAgreedTransports(java.lang.String,
     *      org.eclipse.swordfish.core.components.iapi.Scope)
     */
    public List /* <Transport> */getAgreedTransports(final String operationName, final Scope scope) {
        List agreedTransports = new ArrayList();

        Policy operationPolicy = this.agreedPolicy.getOperationPolicy(operationName);
        ExactlyOne eo = (ExactlyOne) operationPolicy.getTerms().get(0);
        TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
        while (iterAll.hasNext()) {
            All all = (All) iterAll.next();
            TermIterator iterPrimitives = new TermIterator(all, new RegexNameSelector(".*Transport"));
            while (iterPrimitives.hasNext()) {
                PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitives.next();
                String name = assertion.getName().getLocalPart();
                Transport transport = Transport.fromString(name);
                if (null != transport) {
                    agreedTransports.add(transport);
                } else {
                    LOG.warn("Unrecognized transport type \"" + transport + "\" specified in AgreedPolicy " + "for operation "
                            + operationName);
                }
            }
        }
        return agreedTransports;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getDefaultFaultOperation()
     */
    public OperationDescription getDefaultFaultOperation() {
        OperationDescription faultDesc = null;
        if (this.hasPartnerDescription())
            return this.getPartnerDescription().getDefaultFaultOperation();
        else if (this.isPartnerDescription()) {
            for (Iterator iter = this.portType.getOperations().iterator(); iter.hasNext();) {
                Operation op = (Operation) iter.next();
                if (op != null) {
                    QName isFaultOperation = (QName) op.getExtensionAttribute(OperationDescription.FAULTOPERATION_ATTRIBUTE_QNAME);
                    if (isFaultOperation != null) {
                        String result = isFaultOperation.getLocalPart();
                        if ((result != null) && result.equalsIgnoreCase("true")) {
                            faultDesc = (OperationDescription) this.operations.get(op.getName());
                            break;
                        }
                    }
                }
            }
        }
        return faultDesc;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getOperation(java.lang.String)
     */
    public OperationDescription getOperation(final String operationName) {
        return (OperationDescription) this.operations.get(operationName);
    }

    /**
     * returns the (we support only one)"part" of the input message of the indicated operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the operation input message part
     */
    public Part getOperationInputMessagePart(final String operationName) {
        Operation op = this.portType.getOperation(operationName, null, null);
        return (Part) op.getInput().getMessage().getParts().values().iterator().next();
    }

    /**
     * returns the (we support only one)"part" of the out message of the indicated operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the operation output message part
     */
    public Part getOperationOutputMessagePart(final String operationName) {
        Operation op = this.portType.getOperation(operationName, null, null);
        return (Part) op.getOutput().getMessage().getParts().values().iterator().next();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getOperations()
     */
    public Collection /* <OperationDescription> */getOperations() {
        return this.operations.values();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getPartnerDescription()
     */
    public CompoundServiceDescription getPartnerDescription() {
        if (this.partnerDescription == null) {
            if (this.getCallbackPortType() != null) {
                try {
                    this.partnerDescription =
                            new CompoundServiceDescriptionImpl(this.createVirtualSDX(), this.createVirtualCallbackSPDX(), null,
                                    this.identity, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error("Could not create partner service description for " + this.getPortTypeQName().toString());
                    return null;
                }
            }
        }
        return this.partnerDescription;
    }

    /**
     * Gets the partner port type Q name.
     * 
     * @return the partner port type Q name
     */
    public QName getPartnerPortTypeQName() {
        if (null != this.partnerPortType)
            return this.partnerPortType.getQName();
        else
            return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getPort(java.lang.String)
     */
    public SPDXPort getPort(final String wsdlPortName) {
        SPDXPort spdxPort = null;
        Port port = this.service.getPort(wsdlPortName);
        if (port != null) {
            // TODO this causes to many runs on SPDXPort.interpret .. change it
            spdxPort = new SPDXPortImpl(port);
        }
        return spdxPort;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getPorts()
     */
    public SPDXPort[] getPorts() {
        ArrayList list = new ArrayList();
        for (Iterator iter = this.service.getPorts().values().iterator(); iter.hasNext();) {
            // TODO this causes to many runs on SPDXPort.interpret .. change it
            list.add(new SPDXPortImpl((Port) iter.next()));
        }
        return (SPDXPort[]) list.toArray(new SPDXPort[list.size()]);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getPortTypeQName()
     */
    public QName getPortTypeQName() {
        return this.portType.getQName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getProviderPolicy(java.lang.String)
     */
    public ParticipantPolicy getProviderPolicy(final String providerPolicyId) {
        return (ParticipantPolicy) this.providerPolicies.get(providerPolicyId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getReplyEndpointForOperation(java.lang.String)
     */
    public String getReplyEndpointForOperation(final String operationName) {
        if (this.isPartnerOperation(operationName)) {
            SPDXPort chosenPort = null;
            CompoundServiceDescription csd = this.getPartnerDescription();
            SPDXPort ports[] = csd.getPorts();

            // Fixes for 2793 alias Tesa212
            if (ports.length == 0) throw new InfrastructureRuntimeException("No callback endpoint defined for " + operationName);
            // this is ugly but "DummyCallbackPort" ist ugly also
            if ((ports.length == 1) && "DummyCallbackPort".equals(ports[0].getName()))
                throw new InfrastructureRuntimeException("No callback endpoint defined for " + operationName);

            Map usablePorts = new HashMap();
            for (int i = 0; i < ports.length; i++) {
                usablePorts.put(ports[i].getTransport(), ports[i]);
            }
            List agreedTransports = this.getAgreedTransports(operationName, Scope.RESPONSE);
            if (!agreedTransports.isEmpty()) {
                usablePorts = this.getIntersection(usablePorts, agreedTransports);
                // if the intersection is empty, the policy cannot be fulfilled
                if (usablePorts.isEmpty())
                    throw new InfrastructureRuntimeException("The agreed policy prescribes to use (a) specific transport(s), "
                            + "but no corresponding port is defined in the callback definition.");
            }
            if (usablePorts.size() == 1) {
                // if the intersection contains exactly one element, this is the
                // port to be used
                chosenPort = (SPDXPort) usablePorts.values().iterator().next();
            } else if (usablePorts.size() > 1) {
                // if the intersection contains more than one element, we check
                // whether one of them matches the default transport specified
                // in
                // the configuration
                chosenPort = (SPDXPort) usablePorts.get(Transport.HTTP);
                if (null == chosenPort) {
                    chosenPort = (SPDXPort) usablePorts.values().iterator().next();
                }
            }
            if (null != chosenPort) {
                List ee = chosenPort.getExtensibilityElements();
                for (Iterator iter = ee.iterator(); iter.hasNext();) {
                    ExtensibilityElement element = (ExtensibilityElement) iter.next();
                    if (element instanceof SOAPAddress) return ((SOAPAddress) element).getLocationURI();
                    if (element instanceof JMSAddress) return EPRHelper.createJMSAddressURI(((JMSAddress) element));
                }
            }

        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getServiceQName()
     */
    public QName getServiceQName() {
        return this.service.getQName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getSoapAction(java.lang.String)
     */
    public String getSoapAction(final String operationName) {
        String soapAction = null;
        // search for a SOAP over HTTP port
        SPDXPort ports[] = this.getSupportedPorts(operationName);
        for (int i = 0; i < ports.length; i++) {
            if (!ports[i].getTransport().equals(Transport.JMS)) {
                // if one is found, get the corresponding binding information
                // and look for a <soap:operation ...> element
                BindingOperation bindingOperation = ports[i].getBinding().getBindingOperation(operationName, null, null);
                List elements = bindingOperation.getExtensibilityElements();
                for (Iterator iter = elements.iterator(); iter.hasNext();) {
                    ExtensibilityElement element = (ExtensibilityElement) iter.next();
                    if (element instanceof SOAPOperation) {
                        // if one is found, extract the SOAP action from there
                        soapAction = ((SOAPOperation) element).getSoapActionURI();
                    }
                }
                if (soapAction == null) {
                    // if no SOAP action is defined in the binding, use the
                    // operation name as a fallback
                    soapAction = operationName;
                }
            }
        }
        return soapAction; // null if no SOAP over HTTP port was found
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getSupportedPortLocalNames(java.lang.String)
     */
    public String[] getSupportedPortLocalNames(final String operationName) {
        SPDXPort ports[] = this.getSupportedPorts(operationName);
        String portNames[] = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            portNames[i] = ports[i].getName();
        }
        return portNames;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getSupportedPorts(java.lang.String)
     */
    public SPDXPort[] getSupportedPorts(final String operationName) {
        Map ports = this.service.getPorts();
        ArrayList list = new ArrayList();
        for (Iterator iter = ports.values().iterator(); iter.hasNext();) {
            Port port = (Port) iter.next();
            Binding binding = port.getBinding();
            List bindingOpList = binding.getBindingOperations();
            for (int j = 0; j < bindingOpList.size(); j++) {
                BindingOperation bindingOp = (BindingOperation) bindingOpList.get(j);
                if (bindingOp.getName().equals(operationName)) {
                    // TODO this causes to many runs on SPDXPort.interpret ..
                    // change it
                    list.add(new SPDXPortImpl(port));
                }
            }
        }
        return (SPDXPort[]) list.toArray(new SPDXPort[list.size()]);
    }

    /**
     * Gets the WSD ldefined schemas.
     * 
     * @return the WSD ldefined schemas
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#getTypesAsElements(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public List getWSDLdefinedSchemas() {
        if (this.sdxSchemaList == null) {

            List allTypes = null;
            allTypes = this.getAllTypesElements(this.sdx);
            this.sdxSchemaList = new ArrayList();
            int i = 0;
            for (Iterator iter = allTypes.iterator(); iter.hasNext();) {
                Schema element = (Schema) iter.next();
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                DOM2Writer.serializeAsXML(element.getElement(), new OutputStreamWriter(bao), true);
                Document doc = null;
                try {
                    doc = XMLUtil.docFromInputStream(new ByteArrayInputStream(bao.toByteArray()));
                } catch (SAXException e) {
                    return new ArrayList();
                } catch (IOException e) {
                    return new ArrayList();
                }
                this.sdxSchemaList.add(doc);
                i++;
            }
        }
        return this.sdxSchemaList;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#hasPartnerDescription()
     */
    public boolean hasPartnerDescription() {
        return (this.getCallbackPortType() != null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#hasProviderPolicies()
     */
    public boolean hasProviderPolicies() {
        return !this.providerPolicies.isEmpty();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#isNotificationOnlyPort(java.lang.String)
     */
    public boolean isNotificationOnlyPort(final String portName) {
        SPDXPort port = this.getPort(portName);
        if (port == null) return false;
        Binding binding = port.getBinding();
        List cOperations = binding.getBindingOperations();
        for (Iterator iter = cOperations.iterator(); iter.hasNext();) {
            BindingOperation bindingOperation = (BindingOperation) iter.next();
            Operation operation = this.portType.getOperation(bindingOperation.getName(), null, null); // what about overloading?
            if (operation.getStyle() != OperationType.NOTIFICATION) return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#isNotificationPort(java.lang.String)
     */
    public boolean isNotificationPort(final String portName) {
        SPDXPort port = this.getPort(portName);
        if (port == null) return false;
        Binding binding = port.getBinding();
        List cOperations = binding.getBindingOperations();
        for (Iterator iter = cOperations.iterator(); iter.hasNext();) {
            BindingOperation bindingOperation = (BindingOperation) iter.next();
            Operation operation = this.portType.getOperation(bindingOperation.getName(), null, null); // what about overloading?
            if (operation.getStyle() == OperationType.NOTIFICATION) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#isPartnerDescription()
     */
    public boolean isPartnerDescription() {
        return this.isPartner;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription#isPartnerOperation(java.lang.String)
     */
    public boolean isPartnerOperation(final String operationName) {
        if (this.hasPartnerDescription()) {
            PortType ptnPortType = this.getCallbackPortType();
            for (Iterator iter = ptnPortType.getOperations().iterator(); iter.hasNext();) {
                Operation op = (Operation) iter.next();
                if (op != null) {
                    QName partnerOperationQName =
                            (QName) op.getExtensionAttribute(OperationDescription.PARTNEROPERATION_ATTRIBUTE_QNAME);
                    if (partnerOperationQName != null) {
                        String opName = partnerOperationQName.getLocalPart();
                        if ((opName != null) && operationName.equalsIgnoreCase(opName)) return true;
                    }
                }
            }
            return false;
        } else
            return false;
    }

    /**
     * Sets the callback service.
     * 
     * @param serService
     *        the new callback service
     */
    public void setCallbackService(final Service serService) {
        this.callbackService = serService;
    }

    /**
     * Adds the all types elements.
     * 
     * @param def
     *        the def
     * @param toList
     *        the to list
     */
    private void addAllTypesElements(final Definition def, final List toList) {
        Types types = def.getTypes();
        if (types != null) {
            Iterator extEleIt = types.getExtensibilityElements().iterator();
            while (extEleIt.hasNext()) {
                Schema typesElement = (Schema) extEleIt.next();
                toList.add(typesElement);
            }
        }

        Map imports = def.getImports();

        if (imports != null) {
            Iterator valueIterator = imports.values().iterator();

            while (valueIterator.hasNext()) {
                List importList = (List) valueIterator.next();

                if (importList != null) {
                    Iterator importIterator = importList.iterator();

                    while (importIterator.hasNext()) {
                        Import tempImport = (Import) importIterator.next();

                        if (tempImport != null) {
                            Definition importedDef = tempImport.getDefinition();

                            if (importedDef != null) {
                                this.addAllTypesElements(importedDef, toList);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates the binding.
     * 
     * @param virtualSPDX
     *        the virtual SPDX
     * @param cdxPortType
     *        the cdx port type
     * @param extReg
     *        the ext reg
     * @param transportURI
     *        the transport URI
     * 
     * @return the binding
     * 
     * @throws WSDLException
     */
    private Binding createBinding(final Definition virtualSPDX, final PortType cdxPortType, final ExtensionRegistry extReg,
            final String transportURI) throws WSDLException {

        virtualSPDX.addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        Binding binding = virtualSPDX.createBinding();
        binding.setPortType(cdxPortType);
        String nameSuffix = transportURI.substring(transportURI.lastIndexOf('/') + 1);
        binding.setQName(new QName(this.spdx.getTargetNamespace(), cdxPortType.getQName().getLocalPart() + "_" + nameSuffix));
        SOAPBinding soapBinding =
                (SOAPBinding) extReg.createExtension(Binding.class, new QName("http://schemas.xmlsoap.org/wsdl/soap/", "binding"));
        soapBinding.setTransportURI(transportURI);
        soapBinding.setStyle("document");
        binding.addExtensibilityElement(soapBinding);
        for (Iterator iter = cdxPortType.getOperations().iterator(); iter.hasNext();) {

            Operation op = (Operation) iter.next();
            BindingOperation bindingOp = virtualSPDX.createBindingOperation();
            bindingOp.setOperation(op);
            bindingOp.setName(op.getName());
            BindingInput input = virtualSPDX.createBindingInput();
            SOAPBody soapBody =
                    (SOAPBody) extReg.createExtension(BindingInput.class,
                            new QName("http://schemas.xmlsoap.org/wsdl/soap/", "body"));
            soapBody.setUse("literal");
            input.addExtensibilityElement(soapBody);
            bindingOp.setBindingInput(input);
            binding.addBindingOperation(bindingOp);
        }
        binding.setUndefined(false);
        virtualSPDX.addBinding(binding);
        return binding;
    }

    // creates a virtual service description for the callback only
    /**
     * Creates the virtual callback SPDX.
     * 
     * @return the definition
     * 
     * @throws WSDLException
     */
    private Definition createVirtualCallbackSPDX() throws WSDLException {
        WSDLFactory factory = WSDLFactory.newInstance();
        ExtensionRegistry extReg = factory.newPopulatedExtensionRegistry();
        Definition virtualSPDX = factory.newDefinition();
        Binding binding = virtualSPDX.createBinding();
        PortType cdxPortType = this.getCallbackPortType();

        // was passiert wenn er nicht da war? NPE!

        binding.setPortType(cdxPortType);
        virtualSPDX.addNamespace("tns", this.spdx.getTargetNamespace());
        virtualSPDX.addNamespace("pt", cdxPortType.getQName().getNamespaceURI());
        Binding httpBinding = null; // create Binding on demand only
        Binding jmsBinding = null; // create Binding on demand only
        Binding sbbBinding = null; // create Binding on demand only

        if (null != this.callbackService) {
            Map ports = this.callbackService.getPorts();
            // preparation BUGFIX: 2096
            String uniqueness = (Math.random() + "").replace('.', '-');
            for (Iterator iter = ports.keySet().iterator(); iter.hasNext();) {
                Port port = (Port) ports.get(iter.next());
                // BUGFIX: 2096
                // This is an issue that happenes when we provide callback
                // endpoints on the
                // consumer side. Two endpoints for different policies/two
                // participants cannot
                // be registered for the same callback definition. It should be
                // sufficient to
                // have the partner ports renamed to something unique. This will
                // not affect the
                // provider side and should be enough for the consumer side.
                port.setName(port.getName() + "-" + uniqueness);
                String address = "";
                if (port.getExtensibilityElements().get(0) instanceof SOAPAddress) {
                    SOAPAddress soapAddress = (SOAPAddress) port.getExtensibilityElements().get(0);
                    address = soapAddress.getLocationURI();
                    if (address.startsWith("http")) {
                        if (null == httpBinding) {
                            httpBinding =
                                    this.createBinding(virtualSPDX, cdxPortType, extReg, "http://schemas.xmlsoap.org/soap/http");
                        }
                        port.setBinding(httpBinding);
                    } else {
                        LOG.warn("Port with unrecognized address URI encountered: " + address + " --- ignored");
                    }
                }
                if (port.getExtensibilityElements().get(0) instanceof JMSAddress) {
                    if (null == jmsBinding) {
                        jmsBinding = this.createBinding(virtualSPDX, cdxPortType, extReg, "http://schemas.xmlsoap.org/soap/jms");
                    }
                    port.setBinding(jmsBinding);
                }
            }
        } else {
            // fixed defect #2569:
            // on the provider side, no callback service mapping will normally
            // be defined in the configuration,
            // so we create a dummy entry here
            // THIS IS UGLY BEYOND WORDS but does the trick
            // TODO: needs major refactoring
            this.callbackService = virtualSPDX.createService();
            this.callbackService.setQName(new QName(cdxPortType.getQName().getNamespaceURI(), "DummyCallbackService"));
            httpBinding = this.createBinding(virtualSPDX, cdxPortType, extReg, "http://schemas.xmlsoap.org/soap/http");
            Port port = virtualSPDX.createPort();
            port.setName("DummyCallbackPort");
            port.setBinding(httpBinding);
            this.callbackService.addPort(port);
        }
        virtualSPDX.addService(this.callbackService);
        // WSDLWriter w = factory.newWSDLWriter();
        // w.writeWSDL(virtualSPDX, System.out);
        return virtualSPDX;
    }

    /**
     * Creates the virtual SDX.
     * 
     * @return the definition
     * 
     * @throws WSDLException
     */
    private Definition createVirtualSDX() throws WSDLException {
        /*
         * we create the callback service by coping the entire SDX and setting the correct service
         * name into partnerlink information
         */
        Definition virtualSDX = DefinitionHelper.getInstance().cloneDefinition(this.sdx);

        PartnerLinkType plnk = null;
        Iterator iter = virtualSDX.getExtensibilityElements().iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof PartnerLinkType) {
                plnk = (PartnerLinkType) o;
            }
        }
        if (plnk == null) throw new RuntimeException("forced to create a partner description with no definition");
        // remove the old roles
        PartnerLinkRole[] actualRoles = plnk.getPartnerLinkRoles();
        for (int i = 0; i < actualRoles.length; i++) {
            plnk.removePartnerLinkRole(actualRoles[i]);
        }

        // add this services callback port as the callbacks service port :-)
        PartnerLinkRole role = plnk.createPartnerLinkRole();
        role.setName("service");
        role.setPortTypeQName(this.getCallbackPortType().getQName());

        plnk.addPartnerLinkRole(role);

        return virtualSDX;
    }

    /**
     * Gets the all types elements.
     * 
     * @param def
     *        the def
     * 
     * @return the all types elements
     */
    private List getAllTypesElements(final Definition def) {
        List ret = new Vector();

        this.addAllTypesElements(def, ret);

        return ret;
    }

    // if there is no partnerlink defines this returns null
    // otherwise it will return the porttype that is defined in the callback
    // plink
    /**
     * Gets the callback port type.
     * 
     * @return the callback port type
     */
    private PortType getCallbackPortType() {
        PartnerLinkType pl = this.getPartnerLinkType();
        if (pl == null)
            return null;
        else {
            PartnerLinkRole role = pl.getPartnerLinkRole("callback");
            if (role == null)
                return null;
            else {
                PortType pt = this.sdx.getPortType(role.getPortTypeQName());
                return pt;
            }
        }
    }

    /**
     * Gets the intersection.
     * 
     * @param portMap
     *        the port map
     * @param agreedTransports
     *        the agreed transports
     * 
     * @return the intersection
     */
    private Map getIntersection(final Map portMap, final List agreedTransports) {
        Map intersection = new HashMap();
        for (Iterator iter = agreedTransports.iterator(); iter.hasNext();) {
            Transport transport = (Transport) iter.next();
            SPDXPort port = (SPDXPort) portMap.get(transport);
            if (null != port) {
                intersection.put(transport, port);
            }
        }
        return intersection;
    }

    // returns the partnerlink type defined in this sdx or null if none defined
    /**
     * Gets the partner link type.
     * 
     * @return the partner link type
     */
    private PartnerLinkType getPartnerLinkType() {
        Iterator iter = this.sdx.getExtensibilityElements().iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof PartnerLinkType) return (PartnerLinkType) o;
        }
        return null;
    }

    // if there is no partnerlink defines this returns the first porttype from
    // sdx it can find
    // otherwise it will return the porttype that is defined in the service
    // plink
    /**
     * Gets the service port type.
     * 
     * @return the service port type
     */
    private PortType getServicePortType() {
        PartnerLinkType pl = this.getPartnerLinkType();
        if (pl == null)
            return (PortType) this.sdx.getPortTypes().values().iterator().next();
        else {
            PartnerLinkRole role = pl.getPartnerLinkRole("service");
            PortType pt = this.sdx.getPortType(role.getPortTypeQName());
            return pt;
        }
    }

    /**
     * Populate.
     * 
     * @throws WSDLException
     */
    private void populate() throws WSDLException {
        if (this.populated) return;

        this.portType = this.getServicePortType();
        // use the first (and only) Service in the SPDX
        this.service = (Service) this.spdx.getServices().values().iterator().next();

        // The set of operations defined in the PortType might be a superset
        // of the operations available in a particular provider. This is not
        // at all compliant with WS-I but reality in InternalSBB.
        // To make sure that we don't end up constructing proxies or skeletons
        // for unsupported operations, we collect the operations declared in all
        // all the provider's ports
        // (fixes defect #1596)
        this.operations = new HashMap();
        Set declaredOperationNames = new HashSet();
        Iterator portIterator = this.service.getPorts().values().iterator();
        while (portIterator.hasNext()) {
            Binding portBinding = ((Port) portIterator.next()).getBinding();
            List bindingOperations = portBinding.getBindingOperations();
            for (Iterator bindingIter = bindingOperations.iterator(); bindingIter.hasNext();) {
                BindingOperation bop = (BindingOperation) bindingIter.next();
                declaredOperationNames.add(bop.getName());
            }
        }
        for (Iterator iter = declaredOperationNames.iterator(); iter.hasNext();) {
            String opName = (String) iter.next();
            Operation op = this.portType.getOperation(opName, null, null);
            if (null != op) {
                this.operations.put(op.getName(), new OperationDescription(this, op));
            } else {
                LOG.warn("InternalOperation " + opName
                        + " will be ignored because it is bound to a port but not defined in the corresponding PortType "
                        + this.portType.getQName().toString());
            }
        }

        if (this.getCallbackPortType() != null) {
            this.partnerPortType = this.getCallbackPortType();
            this.partnerOperations = new HashMap();
            for (Iterator iter = this.partnerPortType.getOperations().iterator(); iter.hasNext();) {
                Operation op = (Operation) iter.next();
                this.partnerOperations.put(op.getName(), op);
            }
        }
        this.populated = true;
    }
}
