package org.eclipse.swordfish.api.configuration;

import java.util.Map;

/**
 * Can be queried for the configuration. Is plugged into the Swordfish as an osgi service with name
 * org.eclipse.swordfish.api.configuration.PollableConfigurationSource.
 *
 *
 * @param <T>
 */
public interface PollableConfigurationSource<T> {
    /**
     * Returns the mapping between PID(configuration ids, @see {@link ConfigurationConsumer#getId()}) and the configuration data
     */
    public Map<String,T> getConfigurations();
    public Map<String, ?> getProperties();
}
