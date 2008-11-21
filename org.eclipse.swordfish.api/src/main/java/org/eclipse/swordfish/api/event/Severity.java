package org.eclipse.swordfish.api.event;

/**
 * specified event severity level, used in org.eclipse.swordfish.api.event.Event.getSeverity() and 
 * org.eclipse.swordfish.api.event.EventHandler.getSeverity();
 * 
 * NONE - returns from org.eclipse.swordfish.api.Event.getSeverity() implementation by default, if no
 *        severity specified
 * ALL  - returns from org.eclipse.swordfish.api.event.EventHandler.getSeverity() implementation by default
 *        to specify that events with all severity level acceptable
 * @author akopachevsky
 */

public interface Severity {
    /**
     * returns from org.eclipse.swordfish.api.SFEvent.getSeverity() implementation by default, if 
     * no severity specified
     */
    int NONE = 0;
    int DEBUG = 1;
    int INFO = 2;
    int ERROR = 3;
    /**
     * returns from org.eclipse.swordfish.api.event.SFEventHandler.getSeverity() implementation by default
     * to specify that events with all severity level acceptable
     */
    int ALL = 4;
}