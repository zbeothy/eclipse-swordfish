package org.eclipse.swordfish.core.configuration;

import java.util.Map;

import org.eclipse.swordfish.api.context.SwordfishContext;
import org.eclipse.swordfish.api.context.SwordfishContextAware;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.util.Assert;

public class ConfigurationInjector implements InitializingBean, BundleContextAware, SwordfishContextAware {
    private final Logger LOG = LoggerFactory.getLogger(ConfigurationInjector.class);

    private String id;
    private Map configuration;
    private BundleContext bundleContext;
    private SwordfishContext swordfishContext;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Map getConfiguration() {
        return configuration;
    }
    public void setConfiguration(Map configuration) {
        this.configuration = configuration;
    }
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(id);
        Assert.notNull(configuration);
        bundleContext.registerService(SwordfishContextAware.class.getName(), this, null);
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;

    }
    public void setContext(SwordfishContext swordfishContext) {
        this.swordfishContext = swordfishContext;
        LOG.info(String.format("Injecting configuration [%s] for the configurationConsumer with id = [%s] ", configuration.toString(), id));
        swordfishContext.updateConfiguration(id, configuration);
    }
}
