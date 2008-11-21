package org.eclipse.swordfish.core.context;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.jbi.component.ComponentContext;

import org.apache.servicemix.nmr.api.EndpointRegistry;
import org.apache.servicemix.nmr.api.NMR;
import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.api.context.SwordfishContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class SwordfishContextImpl implements SwordfishContext, InitializingBean {
    private NMR nmr;
    private ConfigurationAdmin configurationAdmin;

    public ComponentContext getComponentContext() {
       throw new UnsupportedOperationException("This method is not implemented yet");
    }

    public EndpointRegistry getEndpointRegistry() {
        return nmr.getEndpointRegistry();
    }

    public void updateConfiguration(String id, Map<String, ?> configurationData) {
        try {
            Configuration configuration = configurationAdmin.getConfiguration(id);
            Assert.notNull(configuration, "Could npot find configuration by id = " + id);
            if (configurationData == null) {
                configuration.update(null);
                return;
            }
            Dictionary properties = new Hashtable();
            for (Object key : configurationData.keySet()) {
                properties.put(key, configurationData.get(key));
            }
            configuration.update(properties);
        } catch (Exception ex) {
            throw new SwordfishException(ex);
        }


    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(nmr, "nmr property is required");
        Assert.notNull(configurationAdmin, "configurationAdmin property is required");
    }

    public NMR getNmr() {
        return nmr;
    }

    public void setNmr(NMR nmr) {
        this.nmr = nmr;
    }

    public ConfigurationAdmin getConfigurationAdmin() {
        return configurationAdmin;
    }

    public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }
}
