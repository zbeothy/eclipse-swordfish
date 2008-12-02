package org.eclipse.swordfish.api.configuration;

import java.util.Map;

/**
 * Can be queried for the configuration. Is plugged into the Swordfish as an
 * osgi service with name
 * org.eclipse.swordfish.api.configuration.PollableConfigurationSource.
 *
 *
 * @param <T> Type of the configuration object expected.
 */
public interface PollableConfigurationSource<T> {

	/**
     * Returns the mapping between PID(configuration ids as described in
     * {@link ConfigurationConsumer#getId()}) and the configuration data.
     * @return a configuration Map, the result may be <code>null</code>
     * if no configuration is provided.
     */
    Map<String,T> getConfigurations();

    /**
     * Default properties. May be enhanced or overridden by the properties provided
     * at OSGI service registration. Merged properties are stored in and provided
     * by an internal registry.
     * @return Map of properties.
     */
    Map<String, ?> getProperties();
}
