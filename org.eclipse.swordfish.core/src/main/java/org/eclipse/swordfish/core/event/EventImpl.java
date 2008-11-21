package org.eclipse.swordfish.core.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swordfish.api.event.Event;


public class EventImpl implements Event{

    private String topic;
    private Map<String, Object>  properties = new ConcurrentHashMap<String, Object>();
    
    public Object getProperty(String key){
        return properties.get(key);
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic){
        this.topic = topic;
    }
}
