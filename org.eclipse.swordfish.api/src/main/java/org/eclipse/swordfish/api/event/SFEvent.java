package org.eclipse.swordfish.api.event;

import java.util.Map;

/**
 * Generic Swordfish event
 */
public interface SFEvent {
    public String getTopic();
    public int getSeverity();
    public Map<String, Object> getProperties();
    public Object getProperty(String key);
}
