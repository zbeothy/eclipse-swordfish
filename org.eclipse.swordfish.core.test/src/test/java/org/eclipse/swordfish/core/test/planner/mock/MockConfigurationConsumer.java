package org.eclipse.swordfish.core.test.planner.mock;

import java.util.Map;

import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;

public class MockConfigurationConsumer implements ConfigurationConsumer {
    private String id;
    private Map<String, ?> configuration;

    public void onReceiveConfiguration(Map<String, ?> configuration) {
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, ?> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, ?> configuration) {
        this.configuration = configuration;
    }

}
