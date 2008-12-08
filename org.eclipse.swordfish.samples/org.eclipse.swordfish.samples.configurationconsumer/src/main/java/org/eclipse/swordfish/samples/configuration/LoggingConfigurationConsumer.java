package org.eclipse.swordfish.samples.configuration;

import java.util.Map;

import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingConfigurationConsumer implements ConfigurationConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingConfigurationConsumer.class);

    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void onReceiveConfiguration(Map configuration) {
        LOG.warn("Received updated configuration" + configuration);
    }
}
