package org.eclipse.swordfish.core.event;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.swordfish.api.event.Event;
import org.eclipse.swordfish.api.event.EventService;
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
        LOG.debug("Sening event to topic ["+swordfishEvent.getTopic()+"]");
        Assert.notNull(swordfishEvent.getTopic(), "The destination topic must be supplied");
        Assert.notNull(eventAdmin, "The EventAdmin service must be supplied");
        
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(org.osgi.service.event.EventConstants.EVENT, swordfishEvent);
        org.osgi.service.event.Event event = 
            new org.osgi.service.event.Event(swordfishEvent.getTopic(), properties);
        eventAdmin.postEvent(event);
    }

}
