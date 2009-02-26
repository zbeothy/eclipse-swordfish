/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.api.Registry;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.util.Assert;

public class RegistryImpl<T> implements Registry<T>, BundleContextAware, DisposableBean {

    protected Logger LOG = LoggerFactory.getLogger(getClass());

    private ConcurrentMap<T, Map<String, ?>> registry = new ConcurrentHashMap<T, Map<String, ?>>();

    protected BundleContext bundleContext;

    public void register(T key, Map<String, ?> properties) throws SwordfishException {
        Assert.notNull(key, "key should not be null");
        LOG.warn("!!!!" + key);
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        if (registry.putIfAbsent(key, properties) == null) {
            try {
                doRegister(key, properties);
            } catch (Exception e) {
                LOG.info("Unable to register key " +
                        key + " with properties " + properties + ". Reason: " + e);
                registry.remove(key);
                throw new SwordfishException("Unable to register key " +
                        key + " with properties " + properties + ". Reason: " + e, e);
            }
        }
    }
    public void unregister(T key, Map<String, ?> properties) throws SwordfishException {
        Assert.notNull(key, "key should not be null");
        if (key != null && registry.remove(key) != null) {
            try {
                doUnregister(key, properties);
            } catch (Exception e) {
                LOG.info("Unable to unregister key " +
                        key + ". Reason: " + e);
                throw new SwordfishException("Unable to unregister key " +
                        key + ". Reason: " + e);
            }
        }
    }
    

    protected void doRegister(T key, Map<String, ?> properties) throws Exception {
    }

    protected void doUnregister(T key, Map<String, ?> properties) throws Exception {
    }

    public Set<T> getKeySet() {
        //TODO: please refactor this. The next line may throw concurrent mofification exception. Made just for prototyping
    	Set<T> defensiveSet = new HashSet<T>(registry.keySet());
    	return defensiveSet;
    }

    public Map<String, ?> getProperties(T key) {
        return registry.get(key);
    }

    public void setBundleContext(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void destroy() throws Exception {
        doDestroy();
    }

    protected void doDestroy() throws Exception {

    }
}


