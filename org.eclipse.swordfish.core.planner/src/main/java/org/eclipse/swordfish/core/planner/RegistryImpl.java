/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Mattes - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.planner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.api.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryImpl<T> implements Registry<T> {

	private Logger logger = LoggerFactory.getLogger(RegistryImpl.class);

    private ConcurrentMap<T, Map<String, ?>> registry = new ConcurrentHashMap<T, Map<String, ?>>();

    /* (non-Javadoc)
	 * @see org.eclipse.swordfish.core.planner.InterceptorRegistry#register(T, java.util.Map)
	 */
    public void register(T key, Map<String, ?> properties) throws SwordfishException {
        assert key != null : "key should not be null";
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        if (registry.putIfAbsent(key, properties) == null) {
            try {
                doRegister(key, properties);
            } catch (Exception e) {
            	logger.info("Unable to register key " +
                        key + " with properties " + properties + ". Reason: " + e);
                registry.remove(key);
                throw new SwordfishException("Unable to register key " +
                        key + " with properties " + properties + ". Reason: " + e, e);
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.swordfish.core.planner.keyRegistry#unregister(T, java.util.Map)
	 */
    public void unregister(T key, Map<String, ?> properties) throws SwordfishException {
        assert key != null : "key should not be null";
        if (key != null && registry.remove(key) != null) {
            try {
                doUnregister(key, properties);
            } catch (Exception e) {
                logger.info("Unable to unregister key " +
                        key + " with properties " + properties + ". Reason: " + e);
                throw new SwordfishException("Unable to unregister key " +
                        key + " with properties " + properties + ". Reason: " + e);
            }
        }
    }

    /**
     * Placeholder to perform any registry specific operation
     * when a new key is registered.
     *
     * @param key
     * @param properties
     * @throws Exception
     */
    protected void doRegister(T key, Map<String, ?> properties) throws Exception {
    }

    /**
     * Placeholder to perform any registry specific operation
     * when a key is unregistered.
     *
     * @param key
     * @param properties
     * @throws Exception
     */
    protected void doUnregister(T key, Map<String, ?> properties) throws Exception {
    }

    /* (non-Javadoc)
	 * @see org.eclipse.swordfish.core.planner.keyRegistry#getkeys()
	 */
    public Set<T> getKeySet() {
        //TODO: please refactor this. The next line may throw concurrent mofification exception. Made just for prototyping
    	Set<T> defensiveSet = new HashSet<T>(registry.keySet());
    	return defensiveSet;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.swordfish.core.planner.keyRegistry#getProperties(T)
	 */
    public Map<String, ?> getProperties(T key) {
        return registry.get(key);
    }

	public void unregister(T item) throws SwordfishException {
		
	}
}


