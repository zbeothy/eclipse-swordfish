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

import java.util.Map;

import org.eclipse.swordfish.api.event.ConfigurationEvent;
import org.eclipse.swordfish.api.event.EventConstants;
import org.springframework.util.Assert;

public class ConfigurationEventImpl<T> extends EventImpl implements ConfigurationEvent<T> {
    private Action action = Action.Updated;
    private Map<String, T> configuration;
    private Object configurationSource;
    private String topic = EventConstants.TOPIC_CONFIGURATION_EVENT;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        Assert.notNull(action, "The supplied action parameter can not be null");
        this.action = action;
    }
    public Map<String, T> getConfiguration() {
        return configuration;
    }
    public void setConfiguration(Map<String,T> configuration) {
        this.configuration = configuration;
    }
    public Object getConfigurationSource() {
        return configurationSource;
    }
    public void setConfigurationSource(Object configurationSource) {
        this.configurationSource = configurationSource;
    }
}
