package org.eclipse.swordfish.core.event;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.api.event.SeverityAware;
import org.eclipse.swordfish.core.RegistryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventConstants;

import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.service.importer.ImportedOsgiServiceProxy;
import org.springframework.osgi.service.importer.ServiceReferenceProxy;
import org.springframework.util.Assert;

public class EventHandlerRegistry extends RegistryImpl<EventHandler> implements BundleContextAware {
    
    private BundleContext bundleContext;
    
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    protected ConcurrentHashMap<EventHandler, ServiceRegistration> registrations = new ConcurrentHashMap<EventHandler, ServiceRegistration>();
   

    protected void doRegister(EventHandler eventListener, Map<String, ?> properties) throws Exception {
        Assert.notNull(eventListener);
        
        LOG.info("Registering event listener for ["+eventListener.getSubscribedTopic()+"] topic");
        
        Assert.notNull(eventListener.getSubscribedTopic());
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(EventConstants.EVENT_TOPIC, eventListener.getSubscribedTopic());
        EventHandlerAdapter eventHanlderAdapder = null;
        EventHandler actualEventHandler = getActualEventHandler(eventListener);
        if(actualEventHandler instanceof SeverityAware) {
            eventHanlderAdapder = new SeverityAwareEventHandlerAdapter(actualEventHandler);
        } else {
            eventHanlderAdapder = new EventHandlerAdapter(actualEventHandler);
        }
        registrations.put(eventListener, bundleContext.registerService(org.osgi.service.event.EventHandler.class.getName(), eventHanlderAdapder, props));
        super.doRegister(eventListener, properties);
    }
    
    protected void doUnregister(EventHandler key, Map<String, ?> properties) throws Exception {
        ServiceRegistration serviceRegistration = registrations.get(key);
        Assert.notNull(serviceRegistration, "serviceRegistration for the event listener with topic = ["+ key.getSubscribedTopic() + "] can not be found");
        serviceRegistration.unregister();
        super.doUnregister(key, properties);
    }

    protected void doDestroy() throws Exception {
        for (ServiceRegistration registration : registrations.values()) {
            registration.unregister();
        }
        super.doDestroy();
    }
    
    protected EventHandler getActualEventHandler(EventHandler eventHandler){
        if (eventHandler instanceof ImportedOsgiServiceProxy) {
            ServiceReference serviceReference = ((ImportedOsgiServiceProxy) eventHandler).getServiceReference();
            if (serviceReference instanceof ServiceReferenceProxy) {
                serviceReference = ((ServiceReferenceProxy) serviceReference).getTargetServiceReference();
            }
            return (EventHandler) bundleContext.getService(serviceReference);
        }
        return eventHandler;
    }
}
