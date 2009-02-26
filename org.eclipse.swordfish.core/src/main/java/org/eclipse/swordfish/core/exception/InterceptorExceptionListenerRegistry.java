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
package org.eclipse.swordfish.core.exception;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.InterceptorExceptionListener;
import org.eclipse.swordfish.api.event.EventFilter;
import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.core.util.RegistryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class InterceptorExceptionListenerRegistry extends RegistryImpl<InterceptorExceptionListener> {
	
	private static final Logger LOG = LoggerFactory.getLogger(InterceptorExceptionListenerRegistry.class);
	
	
	private Dictionary<String,Object> execptionProperties = new Hashtable<String, Object>();
	
    protected ConcurrentHashMap<InterceptorExceptionListener, ServiceRegistration> registrations = 
    	new ConcurrentHashMap<InterceptorExceptionListener, ServiceRegistration>();

	BundleContext bundleContext;
	
	public InterceptorExceptionListenerRegistry() {
		//execptionProperties.put(EventConstants.EVENT_TOPIC, InterceptorExceptionNofiticationSender.EXCEPTION_TOPIC);
	}
	
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	protected void doRegister(final InterceptorExceptionListener exceptionListener, Map<String, ?> properties) throws Exception {
		
		LOG.debug("Registeting Interceptor Exception Listener Service [%s] ", exceptionListener);
		
		Assert.notNull(exceptionListener);
		
		EventHandler<InterceptorExceptionEvent> exceptionHandler = new EventHandler<InterceptorExceptionEvent>(){
			public void handleEvent(InterceptorExceptionEvent event) {
				Exception exception = event.getException();
				MessageExchange exchange = event.getExchange();
				Interceptor interceptor = event.getInterceptor();
				exceptionListener.handle(exception, exchange, interceptor);
			}

			public EventFilter getEventFilter() {
				return null;
			}

			public String getSubscribedTopic() {
				return InterceptorExceptionEvent.TOPIC_INTECEPTOR_EXCEPTOIN_EVENT;
			}
		};
	
		//registering service
		ServiceRegistration serviceRegistration = 
			bundleContext.registerService(EventHandler.class.getName(),exceptionHandler, execptionProperties);
		
        registrations.put(exceptionListener, bundleContext.registerService(
        		EventHandler.class.getName(), 
        		exceptionHandler, null));
        super.doRegister(exceptionListener, properties);
		
	}

    protected void doUnregister(InterceptorExceptionListener key, Map<String, ?> properties) throws Exception {
        ServiceRegistration serviceRegistration = registrations.get(key);
        Assert.notNull(serviceRegistration, 
        		String.format(" service registration for interceptor exception listener [%s] can not be found", key));
        serviceRegistration.unregister();
        super.doUnregister(key, properties);
    }
    
    protected void doDestroy() throws Exception {
        for (ServiceRegistration registration : registrations.values()) {
            registration.unregister();
        }
        super.doDestroy();
    }
}
