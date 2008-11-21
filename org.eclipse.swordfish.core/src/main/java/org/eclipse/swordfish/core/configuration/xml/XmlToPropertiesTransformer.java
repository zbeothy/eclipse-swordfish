package org.eclipse.swordfish.core.configuration.xml;

import java.net.URL;
import java.util.Map;

public interface XmlToPropertiesTransformer {
    public void loadConfiguration(String path);
    public void loadConfiguration(URL path);
    public Map<String, String> getProperties();
    public Map<String, Map<String,String>> getPropertiesForPids();
    public boolean isConfigurationLoaded();
}
