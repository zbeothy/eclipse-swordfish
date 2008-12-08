package org.eclipse.swordfish.core.event;

import java.util.Dictionary;

import org.eclipse.swordfish.api.event.Event;
import org.eclipse.swordfish.api.event.EventService;
import org.eclipse.swordfish.core.util.MapBasedDictionary;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class EventServiceImpl implements EventService {
    
    private static final Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);

    private EventAdmin eventAdmin;

    public EventAdmin getEventAdmin() {
        return eventAdmin;
    }
    
    public void setEventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    public void postEvent(Event swordfishEvent) {
        LOG.debug("Sending event to topic ["+swordfishEvent.getTopic()+"]");
        Assert.notNull(swordfishEvent.getTopic(), "The destination topic must be supplied");
        Assert.notNull(eventAdmin, "The EventAdmin service must be supplied");
        
        Dictionary properties = new MapBasedDictionary(swordfishEvent.getProperties());
        properties.put(org.osgi.service.event.EventConstants.EVENT, swordfishEvent);
        
        org.osgi.service.event.Event event = 
            new org.osgi.service.event.Event(swordfishEvent.getTopic(), properties);
        eventAdmin.postEvent(event);
    }

}