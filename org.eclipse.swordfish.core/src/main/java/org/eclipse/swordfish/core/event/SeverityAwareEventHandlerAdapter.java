package org.eclipse.swordfish.core.event;

import org.eclipse.swordfish.api.event.Event;
import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.api.event.SeverityAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class SeverityAwareEventHandlerAdapter extends EventHandlerAdapter {
    private final Logger LOG = LoggerFactory.getLogger(SeverityAwareEventHandlerAdapter.class);

    public SeverityAwareEventHandlerAdapter() {
        
    }
    
    public SeverityAwareEventHandlerAdapter(EventHandler eventHandler) {
        super(eventHandler);
        Assert.isTrue(eventHandler instanceof SeverityAware, 
                "event handler should implement org.eclipse.swordfish.api.event.SeverityAware interface");
    }
    
    public void handleEvent(org.osgi.service.event.Event event) {
        Assert.notNull(delegate, "The EventListener delegate must be supplied");
        Event swordfishEvent = 
            (Event)event.getProperty(org.osgi.service.event.EventConstants.EVENT);
        Assert.notNull(swordfishEvent, "The swordfish event must be supplied");
        Assert.isTrue(swordfishEvent instanceof SeverityAware, 
                "Can't handle event because it isn't implements org.eclipse.swordfish.api.event.SeverityAware interface");
        SeverityAware severityAwareEvent = (SeverityAware) swordfishEvent;
        SeverityAware severityAwareHandler = (SeverityAware)delegate;
        if(severityAwareEvent.getSeverity() >= severityAwareHandler.getSeverity()){
            delegate.handleEvent(swordfishEvent);
        }
    }
/*    public EventListener getDelegate() {
        return delegate;
    }
    public void setDelegate(EventListener delegate) {
        this.delegate = delegate;
    }*/

}
