package org.eclipse.swordfish.api.event;

import java.util.Map;

/**
 * Event signalling a modification of a configuration.
 *
 * @param <T> type of configuration.
 */
public interface ConfigurationEvent<T> extends Event {

	/**
	 * Value indicating what has happened to the configuration.
	 */
	enum Action {Added, Removed, Updated}

	/**
	 * Get the configuration now valid.
	 * @return Map of configurations involved, may be empty,
	 * but never <code>null</code>.
	 */
	Map<String, T> getConfiguration();

	/**
	 * Indicate the reason of the present event.
	 * @return Indicator if configuration has been added, modified, or removed.
	 */
	Action getAction();

	/**
	 * Access to the underlying platform object from which the
	 * configuration has been created.
	 * @return raw platform configuration object.
	 */
	Object getConfigurationSource();
}
