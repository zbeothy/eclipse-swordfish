package org.eclipse.swordfish.core.event.listener;

import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.api.event.Severity;
import org.eclipse.swordfish.api.event.TrackingEvent;

public class TrackingEventHandler implements EventHandler<TrackingEvent> {

    public String getSubscribedTopic() {
        return null;
    }

    public int getSeverity() {
        return Severity.ALL;
    }

    public void handleEvent(TrackingEvent event) {
        
    }
   
}
