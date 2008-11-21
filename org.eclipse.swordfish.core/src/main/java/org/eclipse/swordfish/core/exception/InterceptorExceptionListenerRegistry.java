package org.eclipse.swordfish.core.exception;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.InterceptorExceptionListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterceptorExceptionListenerRegistry {
	
	private static final Logger LOG = LoggerFactory.getLogger(InterceptorExceptionListenerRegistry.class);
	
	
	private Dictionary<String,Object> execptionProperties = new Hashtable<String, Object>();
	
	private Map<InterceptorExceptionListener, ServiceReference> eventListenersMap = 
		new HashMap<InterceptorExceptionListener, ServiceReference>();	
	BundleContext bundleContext;
	
	public InterceptorExceptionListenerRegistry() {
		execptionProperties.put(EventConstants.EVENT_TOPIC, InterceptorExceptionNofiticationSender.EXCEPTION_TOPIC);
	}
	
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void onBindExceptionListener(final InterceptorExceptionListener exceptionListener, 
							final Map properties) throws Exception {
		
		
		EventHandler exceptionHandler = new EventHandler(){
			public void handleEvent(Event event) {
				MessageExchange exchange = 
					(MessageExchange)event.getProperty(InterceptorExceptionNofiticationSender.EXCHANGE_EVENT_PROPERTY);
				Exception exception = 
					(Exception)event.getProperty(InterceptorExceptionNofiticationSender.EXEPTION_EVENT_PROPERTY);
				Interceptor interceptor = 
					(Interceptor)event.getProperty(InterceptorExceptionNofiticationSender.INTERCEPTOR_EVENT_PROPERTY);
				exceptionListener.handle(exception, exchange, interceptor);
			}
		};
	
		//registering service
		ServiceRegistration serviceRegistration = 
			bundleContext.registerService(EventHandler.class.getName(),exceptionHandler, execptionProperties);
		eventListenersMap.put(exceptionListener, serviceRegistration.getReference());
		
		LOG.debug("Interceptor Exception Listener Service [%s] binded", exceptionListener);
	}

	public void onUnbindExceptionListener(InterceptorExceptionListener exceptionListener, Map properties) throws Exception {
		ServiceReference handlerServiceReference = eventListenersMap.remove(exceptionListener);
		bundleContext.ungetService(handlerServiceReference);
		LOG.debug("Interceptor Exception Listener Service [%s] unbinded", exceptionListener);
	}
	
	public void destroy() throws Exception {
		for(InterceptorExceptionListener listener : eventListenersMap.keySet()){
			try {
				onUnbindExceptionListener(listener, null);
			} catch (Exception ex){
				LOG.error("Error during unregistering interceptor exception listener service");
			}
		}
	}
}
