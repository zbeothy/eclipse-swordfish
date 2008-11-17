package org.eclipse.swordfish.api.event;

/**
 *  Basic interface for sending Swordfish events inside Swordfish runtime environment
 *  Should be implemented via delegation to 
 *  org.osgi.service.event.EventAdmin service and implementation must be registered as a OSGI service itself
 *  under org.eclipse.swordfish.api.event.SFEventService interface name.
 */

public interface SFEventService {
    /*
     * Initiate asynchronous delivery of an event by invoking org.osgi.service.event.EventAdmin.postEvent() method.
     * Method returns to the caller before delivery of the event is completed.
     * Method supports events with topic name started with Swordfish prefix (e.g. “org/eclipse/runtime/swordfish”)
     * 
     * osgi Event Admin methanizm using for broadcasting Swordfish events. Swordfish broadcasts as 
     * org.osgi.service.event.Event property with name specified 
     * in org.osgi.service.event.EventConstants.EVENT constant.
     */
    void postEvent(SFEvent event);
}
