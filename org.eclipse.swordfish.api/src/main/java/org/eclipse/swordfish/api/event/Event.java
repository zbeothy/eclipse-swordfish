package org.eclipse.swordfish.api.event;

import java.util.Map;

/**
 * Generic Swordfish event
 */
public interface Event {
    public String getTopic();
    public Map<String, ?> getProperties();
    public Object getProperty(String key);
}
