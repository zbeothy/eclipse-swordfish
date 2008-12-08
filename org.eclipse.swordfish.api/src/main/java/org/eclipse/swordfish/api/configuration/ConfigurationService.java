package org.eclipse.swordfish.api.configuration;

import java.util.Map;

public interface ConfigurationService {
    public <T> void updateConfiguration(String id, Map<String, T> configurationData);
}
