package org.eclipse.swordfish.api.configuration;

import java.util.Map;

public interface ConfigurationService {
    /**
     * Updates the configuration with the specified id
     * @see org.eclipse.swordfish.api.configuration.ConfigurationConsumer
     * @param <T> Type of configuration appropriate for the id passed.
     * @param id unique configuration identifier, must not be <code>null</code>
     * or an empty String
     * @param configurationData Map of configurationData
     */
    public <T> void updateConfiguration(String id, Map<String, T> configurationData);
}
