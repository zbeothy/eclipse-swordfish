package org.eclipse.swordfish.core.configuration;

import java.util.Map;

import org.eclipse.swordfish.api.configuration.PollableConfigurationSource;
import org.eclipse.swordfish.core.RegistryImpl;
import org.springframework.util.Assert;

public class PollableConfigurationSourceRegistry extends RegistryImpl<PollableConfigurationSource> {
    private ConfigurationAgent configurationAgent;


    /* TODO in some time we would to create something like configuration managers to handle different types of PollableConfigurationSource instances
     *
     */
    @Override
    protected void doRegister(PollableConfigurationSource pollableConfigurationSource,
            Map<String, ?> properties) throws Exception {
        Assert.notNull(configurationAgent);
        Assert.notNull(pollableConfigurationSource);
        configurationAgent.handleConfiguration(pollableConfigurationSource.getConfigurations());
        super.doRegister(pollableConfigurationSource, properties);
    }

    public ConfigurationAgent getConfigurationAgent() {
        return configurationAgent;
    }

    public void setConfigurationAgent(ConfigurationAgent configurationAgent) {
        this.configurationAgent = configurationAgent;
    }
}