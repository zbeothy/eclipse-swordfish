/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.event;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swordfish.api.event.Event;


public class EventImpl implements Event {

    private String topic;
    private Map<String, Object>  properties = new Hashtable<String, Object>();
    
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
