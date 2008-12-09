package org.eclipse.swordfish.core.event;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swordfish.api.event.Event;
import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.core.util.RegistryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventConstants;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.util.Assert;

public class EventHandlerRegistry<T extends Event> extends RegistryImpl<EventHandler<T>> implements BundleContextAware {
    
    private BundleContext bundleContext;
    
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    protected ConcurrentHashMap<EventHandler<T>, ServiceRegistration> registrations = new ConcurrentHashMap<EventHandler<T>, ServiceRegistration>();
   

    protected void doRegister(EventHandler<T> handler, Map<String, ?> properties) throws Exception {
        Assert.notNull(handler);
        
        LOG.info("Registering event listener for [" + handler.getSubscribedTopic() + "] topic");
        
        Assert.notNull(handler.getSubscribedTopic());
        
        Dictionary<String, Object> props = getEventHanlderProperties(handler);

        EventHandlerAdapter<T> eventHanlderAdapder = new EventHandlerAdapter<T>(handler);
        
        registrations.put(handler, bundleContext.registerService(
                org.osgi.service.event.EventHandler.class.getName(), 
                eventHanlderAdapder, props));
        super.doRegister(handler, properties);
    }
    
    protected void doUnregister(EventHandler<T> key, Map<String, ?> properties) throws Exception {
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
    
    protected Dictionary<String, Object> getEventHanlderProperties(EventHandler<T> handler){
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(EventConstants.EVENT_TOPIC, handler.getSubscribedTopic());
        if(handler.getEventFilter() != null){
        	props.put(EventConstants.EVENT_FILTER, handler.getEventFilter().getExpression());
        }
        return props;
    }
}
