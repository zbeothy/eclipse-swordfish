/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
/**
 *
 */
package org.eclipse.swordfish.core.wsdl;

/**
 * @author dwolz
 *
 */
	import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





	/**
	 * The Class CompoundServiceDescriptionImpl.
	 */
	public class ServiceDescriptionImpl implements ServiceDescription  {
		private static final Logger logger = LoggerFactory.getLogger(ServiceDescriptionImpl.class);
		/** The wsdl. */
		private Definition wsdl;

		/** The port type. */
		private PortType portType;

		/** The partner port type. */
		private PortType partnerPortType = null;

		/** The service. */
		private Service service;

		/** The operations. */
		private Map<String,Operation> operations = null;

		/** The populated. */
		private boolean populated = false;

		private Map<SOAPAddress, SOAPBinding> availableLocations = null;
		/**
		 * Instantiates a new compound service description impl.
		 *
		 * @param wsdl the wsdl
		 *
		 * @throws Exception TODO.
		 */
		public ServiceDescriptionImpl(Definition wsdl) throws Exception {
			if (wsdl == null) {
				throw new Exception("Service description is null.");
			}
			this.wsdl = wsdl;
			try {
				populate();
			} catch (WSDLException e) {
				throw e;
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getPortTypeQName()
		 */
		public QName getPortTypeQName() {
			return portType.getQName();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getPartnerPortTypeQName()
		 */
		public QName getPartnerPortTypeQName() {
			if (null != this.partnerPortType) {
				return this.partnerPortType.getQName();
			} else {
				return null;
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getServiceQName()
		 */
		public QName getServiceQName() {
			return service.getQName();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getOperations()
		 */
		public Collection<Operation> getOperations() {
			return this.operations.values();
		}

		/**
		 * Populate.
		 *
		 * @throws WSDLException.
		 */
		private void populate() throws WSDLException {
			if (this.populated) {
				return;
			}

			this.portType = getServicePortType();
			// use the first (and only) Service in the SPDX
			this.service = (Service) wsdl.getServices().values().iterator().next();

			// To make sure that we don't end up constructing proxies or skeletons
			// for unsupported operations, we collect the operations declared in all
			// all the provider's ports

			this.operations = new HashMap<String,Operation>();
			Set<String> declaredOperationNames = new HashSet<String>();
			for(Port port: (Iterable<Port>)service.getPorts().values()) {
				Binding portBinding = port.getBinding();
				for(BindingOperation bop: (Iterable<BindingOperation>)portBinding.getBindingOperations()) {
					declaredOperationNames.add(bop.getName());
				}
			}
			for (String opName: declaredOperationNames) {
				Operation op = portType.getOperation(opName, null, null);
				if (null != op) {
					this.operations.put(op.getName(), op);
				} else {
//					LOG
//							.warn("InternalOperation "
//									+ opName
//									+ " will be ignored because it is bound to a port but not defined in the corresponding PortType "
//									+ portType.getQName().toString());
				}
			}
			this.populated = true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getOperation(java.lang.String)
		 */
		public Operation getOperation(String operationName) {
			return this.operations.get(operationName);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getPort(java.lang.String)
		 */
		public SwordfishPort getPort(String wsdlPortName) {
			SwordfishPort SwordfishPort = null;
			Port port = service.getPort(wsdlPortName);
			if (port != null) {
				// TODO this causes to many runs on SwordfishPort.interpret .. change it
				SwordfishPort = new SwordfishPortImpl(port);
			}
			return SwordfishPort;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getSupportedPorts(java.lang.String)
		 */
		public SwordfishPortImpl[] getSupportedPorts(String operationName) {
			List<SwordfishPortImpl> list = new ArrayList<SwordfishPortImpl>();
			for(Port port: (Iterable<Port>)service.getPorts().values()) {
				Binding binding = port.getBinding();
				for(BindingOperation bop:(Iterable<BindingOperation>)binding.getBindingOperations()) {
					if (bop.getName().equals(operationName)) {
						list.add(new SwordfishPortImpl(port));
					}
				}
			}
			return list.toArray(new SwordfishPortImpl[list.size()]);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getPorts()
		 */
		public SwordfishPort[] getPorts() {
			List<SwordfishPortImpl> list = new ArrayList<SwordfishPortImpl>();
			for(Port port: (Iterable<Port>)service.getPorts().values()) {
				list.add(new SwordfishPortImpl(port));
			}
			return list.toArray(new SwordfishPortImpl[list.size()]);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#choosePort(java.lang.String, java.lang.String)
		 */

		public SwordfishPort choosePort(String operationName, String defaultTransport) {

			// get all ports that provide the operation
			SwordfishPortImpl[] ports = getSupportedPorts(operationName);
			Map<TransportImpl,SwordfishPortImpl> usablePorts = new HashMap<TransportImpl,SwordfishPortImpl>();
			for (int i = 0; i < ports.length; i++) {
				usablePorts.put(ports[i].getTransport(), ports[i]);
			}
			if (usablePorts.size() == 1) {
				// if the intersection contains exactly one element, this is the
				// port to be used
				return usablePorts.values().iterator().next();
			} else if (usablePorts.size() > 1) {
				// if the intersection contains more than one element, we check
				// whether one of them matches the default transport specified in
				// the configuration
				SwordfishPort port = usablePorts.get(TransportImpl
						.fromString(defaultTransport));
				if (null != port) {
					// if this is the case, we use that one
					return port;
				} else {
					// otherwise we bail out and tell the boss that we don't know
					// what to do now
					throw new RuntimeException(
							"Cannot decide which transport to use: The service description and the agreed policy allow for more than one transport to be used "
									+ "but none of them matches the default transport defined in the configuration.");
				}
			} else {
				// if no usable port is available, we can't do anything about it
				throw new RuntimeException(
						"No ports are defined in the service provider description for "
								+ getServiceQName().toString());
			}
		}
		public synchronized Map<SOAPAddress, SOAPBinding> getAvailableLocations() {
			if (!populated) {
				throw new IllegalStateException("Has not been populated yet");
			}
			if (availableLocations != null) {
				return availableLocations;
			}
			availableLocations = new HashMap<SOAPAddress, SOAPBinding>();
			Map ports = service.getPorts();
			try {
			for (Object portObj : service.getPorts().values()) {
				Port port = (Port) portObj;
				SOAPAddress address = null;
				for (Object soapAddr : port.getExtensibilityElements()) {
					if (soapAddr instanceof SOAPAddress) {
						address = (SOAPAddress) soapAddr;
					}
				}
				SOAPBinding soapBinding = null;
				if (address != null) {
					for (Object  soapBindObj : port.getBinding().getExtensibilityElements()) {
						if (soapBindObj instanceof SOAPBinding) {
							soapBinding = (SOAPBinding) soapBindObj;
						}
					}
				}
				if (address != null && soapBinding != null && soapBinding.getTransportURI() != null) {
					availableLocations.put(address, soapBinding);
				}

			}
			} catch (Exception ex) {
				logger.warn("Ignoring the exception and returning null", ex);
				availableLocations = new HashMap<SOAPAddress, SOAPBinding>();
			}
			return availableLocations;
		}
		// if there is no partnerlink defines this returns the first porttype from
		// wsdl it can find
		/**
		 * Gets the service port type.
		 *
		 * @return the service port type
		 */
		private PortType getServicePortType() {
			return (PortType) wsdl.getPortTypes().values().iterator().next();
		}


		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getOperationInputMessagePart(java.lang.String)
		 */
		public Part getOperationInputMessagePart(String operationName) {
			Operation op = portType.getOperation(operationName, null, null);
			return (Part) op.getInput().getMessage().getParts().values().iterator()
					.next();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swordfish.nmr.wsdl.ServiceDescription#getOperationOutputMessagePart(java.lang.String)
		 */
		public Part getOperationOutputMessagePart(String operationName) {
			Operation op = portType.getOperation(operationName, null, null);
			return (Part) op.getOutput().getMessage().getParts().values()
					.iterator().next();
		}

	}
