package org.eclipse.swordfish.core.configuration;

import java.util.Map;

public interface ConfigurationAgent {
    public void handleConfiguration(Map<String, ?> configurations);
}
