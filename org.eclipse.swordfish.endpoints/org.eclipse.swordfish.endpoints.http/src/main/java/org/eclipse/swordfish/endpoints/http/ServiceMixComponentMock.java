package org.eclipse.swordfish.endpoints.http;

import javax.jbi.component.ComponentContext;
import javax.jbi.component.ComponentLifeCycle;
import javax.jbi.component.ServiceUnitManager;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.servicemix.common.Container;
import org.apache.servicemix.common.Endpoint;
import org.apache.servicemix.common.Registry;
import org.apache.servicemix.common.ServiceMixComponent;
import org.apache.servicemix.executors.Executor;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

public class ServiceMixComponentMock implements ServiceMixComponent {
	private ComponentContext context;
	public ServiceMixComponentMock(ComponentContext context) {
		this.context = context;
	}

	public ComponentContext getComponentContext() {
		// TODO Auto-generated method stub
		return context;
	}

	public String getComponentName() {
		// TODO Auto-generated method stub
		return null;
	}

	public QName getEPRElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Executor getExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

	public Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	public Registry getRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	public void prepareConsumerExchange(MessageExchange exchange,
			Endpoint endpoint) throws MessagingException {
		// TODO Auto-generated method stub

	}

	public void sendConsumerExchange(MessageExchange exchange, Endpoint endpoint)
			throws MessagingException {
		// TODO Auto-generated method stub

	}

	public ComponentLifeCycle getLifeCycle() {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getServiceDescription(ServiceEndpoint endpoint) {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceUnitManager getServiceUnitManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isExchangeWithConsumerOkay(ServiceEndpoint endpoint,
			MessageExchange exchange) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isExchangeWithProviderOkay(ServiceEndpoint endpoint,
			MessageExchange exchange) {
		// TODO Auto-generated method stub
		return false;
	}

	public ServiceEndpoint resolveEndpointReference(DocumentFragment epr) {
		// TODO Auto-generated method stub
		return null;
	}

    public Container getContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getSmx3Container() {
        // TODO Auto-generated method stub
        return null;
    }

    public void handleExchange(Endpoint endpoint, MessageExchange exchange,
            boolean add) {
        // TODO Auto-generated method stub

    }

    public void prepareExchange(MessageExchange exchange, Endpoint endpoint)
            throws MessagingException {
        // TODO Auto-generated method stub

    }

    public void prepareShutdown(Endpoint endpoint) throws InterruptedException {
        // TODO Auto-generated method stub

    }

}
