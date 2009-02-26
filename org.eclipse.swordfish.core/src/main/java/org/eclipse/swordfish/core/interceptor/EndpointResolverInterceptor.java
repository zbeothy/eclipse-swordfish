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
package org.eclipse.swordfish.core.interceptor;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.jbi.messaging.MessageExchange;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.apache.servicemix.jbi.runtime.impl.MessageExchangeImpl;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Reference;
import org.apache.servicemix.nmr.api.Role;
import org.apache.servicemix.nmr.api.internal.InternalEndpoint;
import org.apache.servicemix.nmr.core.DynamicReferenceImpl;
import org.apache.servicemix.nmr.core.StaticReferenceImpl;
import org.apache.servicemix.nmr.core.util.Filter;
import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;
import org.eclipse.swordfish.core.util.JbiConstants;
import org.eclipse.swordfish.core.util.ServiceMixSupport;
import org.eclipse.swordfish.core.wsdl.ServiceDescription;
import org.eclipse.swordfish.core.wsdl.WSDLInterceptor;
import org.eclipse.swordfish.core.wsdl.WSDLManager;
import org.eclipse.swordfish.core.wsdl.WSDLManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class EndpointResolverInterceptor<T> implements ConfigurationConsumer<T>, WSDLInterceptor, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(EndpointResolverInterceptor.class);
    private NMR nmr;
    private WSDLManager wsdlManager;
    private String wsdlStorage;
    private Map<String, ?> properties = new HashMap<String, Object>();

	public void process(MessageExchange messageExchange) throws SwordfishException {
	    Assert.notNull(wsdlStorage, "wsdlStorage is not loaded");
		Exchange exchange = ServiceMixSupport.toNMRExchange(messageExchange);
		try {
		if (exchange.getRole() != Role.Consumer) {
			return;
		}
			QName interfaceName = (QName) exchange.getProperty(Endpoint.INTERFACE_NAME);
		if (interfaceName == null) {
			interfaceName = (QName) exchange.getProperty(MessageExchangeImpl.INTERFACE_NAME_PROP);
		}
		if (interfaceName == null) {
			return;
		}
		QName operation = exchange.getOperation();
		if (operation == null)
			return;
		ServiceDescription serviceDescription;
		try {
			serviceDescription = wsdlManager.getServiceDescription(interfaceName);
		} catch (WSDLException e) {
			throw new SwordfishException("Error resolving endpoint - cannot retrieve service description for port type " + interfaceName
					+ " message: " + e.getMessage());
		}
		if (serviceDescription != null) {
//			SwordfishPort port = serviceDescription.choosePort(operation.getLocalPart(), Transport.HTTP_STR);
//			Binding binding = port.getBinding();
//			Map<String,String> props = new HashMap<String, String>();
//			props.put(Endpoint.SERVICE_NAME, serviceDescription.getServiceQName().toString());
//			Reference reference = lookup(props);
			QName service = serviceDescription.getServiceQName();
			if (service == null)
				throw new SwordfishException("Error resolving endpoint - cannot resolve service for port type " + interfaceName
						+ " operation " + operation);
			Map<String,Object> props = new HashMap<String, Object>();
			props.put(Endpoint.SERVICE_NAME, service.toString());
			InternalEndpoint serviceEndpoint = ServiceMixSupport.getEndpoint(nmr, props);
			if (serviceEndpoint != null) {
				logger.info("The service endpoint for the servicename + [" + service + "} has been found");
				exchange.setTarget(new StaticReferenceImpl(Arrays.asList(serviceEndpoint)));
			} else {
				logger.info("The service endpoint for the servicename + [" + service + "} not found");
				logger.info("Trying to find the transport endpoint");
				serviceDescription.getAvailableLocations();
				for (Map.Entry<SOAPAddress, SOAPBinding> entry : serviceDescription.getAvailableLocations().entrySet()) {
					props.clear();
					props.put(JbiConstants.PROTOCOL_TYPE, entry.getValue().getTransportURI());
					serviceEndpoint = ServiceMixSupport.getEndpoint(nmr, props);
					if (serviceEndpoint != null) {
						logger.info("Have found the suitable endpoint with transport = " + entry.getValue().getTransportURI());
						exchange.setTarget(new StaticReferenceImpl(Arrays.asList(serviceEndpoint)));
						exchange.getIn().setHeader(JbiConstants.HTTP_DESTINATION_URI, entry.getKey().getLocationURI());
					}
				}
			}
		} else {
			logger.info("Error resolving endpoint - wsdl cannot be found for port type " + interfaceName);
		}
		} catch (Exception ex) {
			logger.warn("The exception happened while trying to resolve service name via supplied wsdls ", ex);
		}
	}

	public Reference lookup(final Map<String, ?> properties) {
        DynamicReferenceImpl ref = new DynamicReferenceImpl(nmr.getEndpointRegistry(), new Filter<InternalEndpoint>() {
            public boolean match(InternalEndpoint endpoint) {
                Map<String, ?> epProps = nmr.getEndpointRegistry().getProperties(endpoint);
                for (Map.Entry<String, ?> name : properties.entrySet()) {//epProps.put(name.getKey(), name.getValue())
                    if (!name.getValue().equals(epProps.get(name.getKey()))) {
                        return false;
                    }
                }
                return true;
            }
        });
        return ref;
    }

	public NMR getNmr() {
		return nmr;
	}

	public void setNmr(NMR nmr) {
		this.nmr = nmr;
	}

	public void setWSDLManager(WSDLManager wsdlManager) {
		this.wsdlManager = wsdlManager;
	}

	public Map<String, ?> getProperties() {
	    return properties;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(nmr);
		wsdlManager = new WSDLManagerImpl();

	}


    public String getId() {
        return getClass().getName();
    }

    public void onReceiveConfiguration(Map<String, T> configuration) {
        if (configuration != null) {
            wsdlStorage = (String) configuration.get("wsdlStorage");
            try {
                wsdlManager.setupWSDLs(new URL(wsdlStorage));
            } catch (Exception ex) {
               throw new SwordfishException(ex);
            }
        }


    }
}
