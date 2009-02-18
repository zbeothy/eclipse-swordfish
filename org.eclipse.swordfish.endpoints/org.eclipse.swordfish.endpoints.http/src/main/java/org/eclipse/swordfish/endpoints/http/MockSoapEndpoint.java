package org.eclipse.swordfish.endpoints.http;

import java.util.Arrays;

import javax.jbi.component.Component;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.wsdl.Definition;

import org.apache.servicemix.common.DefaultServiceUnit;
import org.apache.servicemix.common.ServiceMixComponent;
import org.apache.servicemix.soap.SoapEndpoint;
import org.apache.servicemix.soap.SoapExchangeProcessor;
import org.apache.servicemix.soap.handlers.addressing.AddressingHandler;

public class MockSoapEndpoint extends SoapEndpoint {
	public MockSoapEndpoint(ServiceEndpoint endpoint,
			Component smx3CompatibilityComponent) {
		this.setEndpoint(endpoint.getEndpointName());
		this.setService(endpoint.getServiceName());
		if (endpoint.getInterfaces() != null && endpoint.getInterfaces().length > 0) {
			this.setInterfaceName(endpoint.getInterfaces()[0]);
		}
		this.setServiceUnit(new DefaultServiceUnit((ServiceMixComponent) smx3CompatibilityComponent));
		this.soap = true;

	}

	@Override
	public java.util.List getPolicies() {
		return Arrays.asList(new AddressingHandler());
	}



	@Override
	protected ServiceEndpoint createExternalEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    protected SoapExchangeProcessor createConsumerProcessor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected SoapExchangeProcessor createProviderProcessor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void overrideDefinition(Definition def) throws Exception {
        // TODO Auto-generated method stub

    }





}
