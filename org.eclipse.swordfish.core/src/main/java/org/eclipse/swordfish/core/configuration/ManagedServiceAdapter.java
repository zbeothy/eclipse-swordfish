package org.eclipse.swordfish.core.configuration;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class ManagedServiceAdapter implements ManagedService {
    private final Logger LOG = LoggerFactory.getLogger(ManagedServiceAdapter.class);

    private ConfigurationConsumer delegate;

    public ManagedServiceAdapter() {

    }
    public ManagedServiceAdapter(ConfigurationConsumer configurationConsumer) {
        delegate = configurationConsumer;
    }

    public void updated(Dictionary properties) throws ConfigurationException {
        Assert.notNull(delegate, "The ConfigurationConsumer delegate must be supplied");
        if (properties == null) {
            delegate.onReceiveConfiguration(null);
            return;
        }
        Map configuration =  new HashMap();
        Enumeration e =  properties.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            configuration.put(key, properties.get(key));
        }
        LOG.info(String.format("Received configuration [%s] for the configurationConsumer with id = [%s] ", configuration.toString(), delegate.getId()));

        delegate.onReceiveConfiguration(configuration);

    }
    public ConfigurationConsumer getDelegate() {
        return delegate;
    }
    public void setDelegate(ConfigurationConsumer delegate) {
        this.delegate = delegate;
    }
}
