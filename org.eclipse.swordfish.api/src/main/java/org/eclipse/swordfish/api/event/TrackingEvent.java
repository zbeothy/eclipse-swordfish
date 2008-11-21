package org.eclipse.swordfish.api.event;

/**
 * message tracking events are used to track the progress of message processing in the core, 
 * operational events are used to notify administrators or other software components of events 
 * like failure situations etc.
 * @author akopachevsky
 */
public interface TrackingEvent extends Event {
    int getMessageExchangeId();
}
