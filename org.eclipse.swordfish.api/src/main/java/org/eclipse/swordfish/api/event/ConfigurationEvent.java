package org.eclipse.swordfish.api.event;

import java.util.Map;

public interface ConfigurationEvent extends Event {
    public static enum Action {Added, Removed, Updated}
    public Map<String, ?> getConfiguration();
    public Action getAction();
    public Object getConfigurationSource();
}
