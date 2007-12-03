/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.core.components.contextstore.impl;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * The Class InMemoryContextStoreBean.
 */
public class InMemoryContextStoreBean extends AbstractContextStore {

    /** The transient cache. */
    private Map transientCache;

    /**
     * Instantiates a new in memory context store bean.
     */
    public InMemoryContextStoreBean() {
        this.transientCache = new HashMap();
    }

    /**
     * Destroy.
     */
    public void destroy() {
    }

    /**
     * Init.
     */
    public void init() {
    }

    /**
     * Removes the call context.
     * 
     * @param key
     *        the key
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.impl.AbstractContextStore#removeCallContext(java.lang.String)
     */
    @Override
    public void removeCallContext(final String key) {
        if (this.transientCache.containsKey(key)) {
            this.transientCache.remove(key);
        }
    }

    /**
     * Restore call context.
     * 
     * @param key
     *        the key
     * 
     * @return the call context extension
     * 
     * @throws ContextNotRestoreableException
     * @throws ContextNotFoundException
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.impl.AbstractContextStore#restoreCallContext(java.lang.String)
     */
    @Override
    public CallContextExtension restoreCallContext(final String key) throws InternalIllegalInputException,
            InternalInfrastructureException {
        CallContextExtension ctx = null;
        ctx = (CallContextExtension) this.transientCache.get(key);
        if (null != ctx) {
            this.removeCallContext(key);
        } else
            throw new InternalConfigurationException("failed to look-up key " + key + " in the memory store.");
        return ctx;
    }

    /**
     * Store call context.
     * 
     * @param ctx
     *        the ctx
     * 
     * @return the string
     * 
     * @throws ContextNotStoreableException
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.impl.AbstractContextStore#storeCallContext(org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension)
     */
    @Override
    public String storeCallContext(final CallContextExtension ctx) throws InternalIllegalInputException,
            InternalInfrastructureException {
        final String key = this.buildKey(ctx);
        this.transientCache.put(key, ctx);
        return key;
    }

}
