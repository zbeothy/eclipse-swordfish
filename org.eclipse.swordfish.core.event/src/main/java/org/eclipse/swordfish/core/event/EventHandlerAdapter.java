package org.eclipse.swordfish.core.event;

import org.eclipse.swordfish.api.event.Event;
import org.eclipse.swordfish.api.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class EventHandlerAdapter<T extends Event> implements org.osgi.service.event.EventHandler {
    private final Logger LOG = LoggerFactory.getLogger(EventHandlerAdapter.class);

    protected EventHandler<T> delegate;

    public EventHandlerAdapter() {
        
    }
    public EventHandlerAdapter(EventHandler<T> eventListener) {
        delegate = eventListener;
    }
    
    public void handleEvent(org.osgi.service.event.Event event) {
        Assert.notNull(delegate, "The EventListener delegate must be supplied");
        Event swordfishEvent = (Event)event.getProperty(org.osgi.service.event.EventConstants.EVENT);
        Assert.notNull(swordfishEvent, "The swordfish event must be supplied");
        delegate.handleEvent((T) swordfishEvent);
    }
}
