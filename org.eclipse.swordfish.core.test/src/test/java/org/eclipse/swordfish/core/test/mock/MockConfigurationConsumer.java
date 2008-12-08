package org.eclipse.swordfish.core.test.mock;

import java.util.Map;

import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;

public class MockConfigurationConsumer<T> implements ConfigurationConsumer<T> {
    private String id;
    private Map<String, ?> configuration;

    public void onReceiveConfiguration(Map<String, T> configuration) {
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
