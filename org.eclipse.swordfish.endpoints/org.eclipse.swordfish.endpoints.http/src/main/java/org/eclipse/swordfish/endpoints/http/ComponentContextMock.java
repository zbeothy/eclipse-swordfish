package org.eclipse.swordfish.endpoints.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.jbi.JBIException;
import javax.jbi.component.Component;
import javax.jbi.component.ComponentContext;
import javax.jbi.management.MBeanNames;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.servicemix.jbi.runtime.impl.EndpointImpl;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.NMR;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

public class ComponentContextMock implements  ComponentContext {
	private NMR nmr;
	public ComponentContextMock(NMR nmr) {
		this.nmr = nmr;
	}
	public synchronized ServiceEndpoint activateEndpoint(QName serviceName, String endpointName) throws JBIException {

            EndpointImpl endpoint = new EndpointImpl();
            endpoint.setQueue(new LinkedBlockingQueue<Exchange>());
            endpoint.setServiceName(serviceName);
            endpoint.setEndpointName(endpointName);
            Map<String, Object> props = new HashMap<String, Object>();
            props.put(Endpoint.NAME, serviceName.toString() + ":" + endpointName);
            props.put(Endpoint.SERVICE_NAME, serviceName);
            props.put(Endpoint.ENDPOINT_NAME, endpointName);
            nmr.getEndpointRegistry().register(endpoint,  props);
            return new SimpleServiceEndpoint(props, endpoint);

    }

    public synchronized void deactivateEndpoint(ServiceEndpoint endpoint) throws JBIException {
        EndpointImpl ep;
        if (endpoint instanceof EndpointImpl) {
            ep = (EndpointImpl) endpoint;
        } else if (endpoint instanceof SimpleServiceEndpoint) {
            ep = ((SimpleServiceEndpoint) endpoint).getEndpoint();
        } else {
            throw new IllegalArgumentException("Unrecognized endpoint");
        }
        nmr.getEndpointRegistry().unregister(ep, null);
    }

    public void registerExternalEndpoint(ServiceEndpoint externalEndpoint) throws JBIException {
        // TODO
    }

    public void deregisterExternalEndpoint(ServiceEndpoint externalEndpoint) throws JBIException {
        // TODO
    }

    public ServiceEndpoint resolveEndpointReference(DocumentFragment epr) {
    	throw new UnsupportedOperationException();
    }

    public String getComponentName() {
    	throw new UnsupportedOperationException();
    }

    public DeliveryChannel getDeliveryChannel() throws MessagingException {
    	return new DeliveryChannelMock(nmr, nmr.createChannel(), new LinkedBlockingQueue<Exchange>());
    }

    public ServiceEndpoint getEndpoint(QName serviceName, String endpointName) {
        Map<String, Object> props = new HashMap<String, Object>();
        if (serviceName != null) {
        	props.put(Endpoint.SERVICE_NAME, serviceName);
        }
        if (endpointName != null) {
        	props.put(Endpoint.ENDPOINT_NAME, endpointName);
        }
        List<Endpoint> endpoints = nmr.getEndpointRegistry().query(props);
        if (endpoints.isEmpty()) {
            return null;
        }
        Map<String, ?> p = nmr.getEndpointRegistry().getProperties(endpoints.get(0));
        return new SimpleServiceEndpoint(p);
    }

    public Document getEndpointDescriptor(ServiceEndpoint endpoint) throws JBIException {
    	return null;
    }

    public ServiceEndpoint[] getEndpoints(QName interfaceName) {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(Endpoint.INTERFACE_NAME, interfaceName);
        return internalQueryEndpoints(props);
    }

    protected SimpleServiceEndpoint[] internalQueryEndpoints(Map<String, Object> props) {
        List<Endpoint> endpoints = nmr.getEndpointRegistry().query(props);
        List<ServiceEndpoint> ses = new ArrayList<ServiceEndpoint>();
        for (Endpoint endpoint : endpoints) {
            Map<String, ?> epProps = nmr.getEndpointRegistry().getProperties(endpoint);
            QName serviceName = (QName) epProps.get(Endpoint.SERVICE_NAME);
            String endpointName = (String) epProps.get(Endpoint.ENDPOINT_NAME);
            if (serviceName != null && endpointName != null) {
                ses.add(new SimpleServiceEndpoint(epProps));
            }
        }
        return ses.toArray(new SimpleServiceEndpoint[ses.size()]);
    }

    public ServiceEndpoint[] getEndpointsForService(QName serviceName) {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(Endpoint.SERVICE_NAME, serviceName);
        return internalQueryEndpoints(props);
    }

    public ServiceEndpoint[] getExternalEndpoints(QName interfaceName) {
        return new ServiceEndpoint[0];  // TODO
    }

    public ServiceEndpoint[] getExternalEndpointsForService(QName serviceName) {
        return new ServiceEndpoint[0];  // TODO
    }

    public String getInstallRoot() {
    	throw new UnsupportedOperationException();
    }

    public Logger getLogger(String suffix, String resourceBundleName) throws  JBIException {
        throw new UnsupportedOperationException();
    }

    public MBeanNames getMBeanNames() {
    	throw new UnsupportedOperationException();
    }

    public MBeanServer getMBeanServer() {
    	throw new UnsupportedOperationException();
    }

    public InitialContext getNamingContext() {
    	throw new UnsupportedOperationException();
    }

    public Object getTransactionManager() {
    	throw new UnsupportedOperationException();
    }

    public String getWorkspaceRoot() {
    	throw new UnsupportedOperationException();
    }

    public ObjectName createCustomComponentMBeanName(String customName) {
    	throw new UnsupportedOperationException();
    }

    public String getJmxDomainName() {
    	throw new UnsupportedOperationException();
    }


    public ServiceEndpoint resolveInternalEPR(DocumentFragment epr) {
    	throw new UnsupportedOperationException();
    }


    public ServiceEndpoint resolveStandardEPR(DocumentFragment epr) {
       throw new UnsupportedOperationException();
    }

    public Component getComponent() {
       throw new UnsupportedOperationException();
    }


    protected static class SimpleServiceEndpoint implements ServiceEndpoint {

        private Map<String, ?> properties;
        private EndpointImpl endpoint;

        public SimpleServiceEndpoint(Map<String, ?> properties) {
            this.properties = properties;
        }

        public SimpleServiceEndpoint(Map<String, ?> properties, EndpointImpl endpoint) {
            this.properties = properties;
            this.endpoint = endpoint;
        }

        public Map<String, ?> getProperties() {
            return properties;
        }

        public EndpointImpl getEndpoint() {
            return endpoint;
        }

        public DocumentFragment getAsReference(QName operationName) {
            throw new UnsupportedOperationException();
        }

        public String getEndpointName() {
            return (String) properties.get(EndpointImpl.ENDPOINT_NAME);
        }

        public QName[] getInterfaces() {
            QName itf = (QName) properties.get(Endpoint.INTERFACE_NAME);
            return itf != null ? new QName[] { itf } : new QName[0];
        }

        public QName getServiceName() {
            return (QName) properties.get(Endpoint.SERVICE_NAME);
        }
}}
