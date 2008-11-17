package org.eclipse.swordfish.api.event;

/**
 * specified event severity level, used in org.eclipse.swordfish.api.event.SFEvent.getSeverity() and 
 * org.eclipse.swordfish.api.event.SFEventHandler.getSeverity();
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
