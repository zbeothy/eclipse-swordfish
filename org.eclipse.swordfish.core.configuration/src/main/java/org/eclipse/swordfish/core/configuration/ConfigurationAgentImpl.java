package org.eclipse.swordfish.core.configuration;

import java.util.Map;

import org.eclipse.swordfish.api.configuration.ConfigurationService;
import org.eclipse.swordfish.api.event.ConfigurationEvent;
import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.EventFilter;
import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.api.event.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public class ConfigurationAgentImpl implements EventHandler<ConfigurationEvent>, ConfigurationAgent {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationAgentImpl.class);
    private PollableConfigurationSourceRegistry configurationSourceRegistry;
    private ConfigurationService configurationService;


    public Severity getSeverity() {
        return null;
    }
    public String getSubscribedTopic() {
        return EventConstants.TOPIC_CONFIGURATION_EVENT;
    }

    public void handleEvent(ConfigurationEvent configurationEvent) {
        LOG.info("Received configuration event " + configurationEvent);
        if (configurationEvent != null && configurationEvent.getConfiguration() != null) {
            handleConfiguration(configurationEvent.getConfiguration());
        }
    }

    public void handleConfiguration(Map<String, ?> configurations) {
        Assert.notNull(configurations);
        Assert.notNull(configurationService);
        for (Object id : configurations.keySet()) {
            Object configuration = configurations.get(id);
            if (! (configuration instanceof Map)) {
                throw new UnsupportedOperationException("Only map based configuration is supported as for now");
            }
            configurationService.updateConfiguration((String) id, (Map)configuration);
        }
    }
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    public PollableConfigurationSourceRegistry getConfigurationSourceRegistry() {
        return configurationSourceRegistry;
    }
    public void setConfigurationSourceRegistry(
            PollableConfigurationSourceRegistry configurationSourceRegistry) {
        this.configurationSourceRegistry = configurationSourceRegistry;
    }

    public EventFilter getEventFilter() {
    	return null;
    }
}
