package org.eclipse.swordfish.core.event;

import java.util.Map;

import org.eclipse.swordfish.api.event.ConfigurationEvent;
import org.eclipse.swordfish.api.event.EventConstants;
import org.springframework.util.Assert;

public class ConfigurationEventImpl extends EventImpl implements ConfigurationEvent {
    private Action action = Action.Updated;
    private Map<String, ?> configuration;
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
    public Map<String, ?> getConfiguration() {
        return configuration;
    }
    public void setConfiguration(Map<String, ?> configuration) {
        this.configuration = configuration;
    }
    public Object getConfigurationSource() {
        return configurationSource;
    }
    public void setConfigurationSource(Object configurationSource) {
        this.configurationSource = configurationSource;
    }
}
