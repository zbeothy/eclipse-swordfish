package org.eclipse.swordfish.core.configuration.xml;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swordfish.api.configuration.PollableConfigurationSource;
import org.springframework.util.Assert;

public class XmlConfigurationSource implements PollableConfigurationSource<Map<String, String>> {
    private Map<String, Object> props = new HashMap<String, Object>();
    private XmlToPropertiesTransformer propertiesTransformer = new XmlToPropertiesTransformerImpl();
    public void setConfigurationPath(String path) {
        propertiesTransformer.loadConfiguration(path);
    }

    public void setConfigurationPath(URL path) {
        propertiesTransformer.loadConfiguration(path);
    }

    public Map<String, Map<String, String>> getConfigurations() {
       Assert.state(propertiesTransformer.isConfigurationLoaded(), "Configuration is not loaded");
       return propertiesTransformer.getPropertiesForPids();
    }

    public Map<String, ?> getProperties() {
        return props;
    }
    public XmlToPropertiesTransformer getPropertiesTransformer() {
        return propertiesTransformer;
    }

    public void setPropertiesTransformer(
            XmlToPropertiesTransformer propertiesTransformer) {
        this.propertiesTransformer = propertiesTransformer;
    }
}
