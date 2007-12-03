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
package org.eclipse.swordfish.core.components.handlerregistry.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.namespace.QName;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.eclipse.swordfish.core.components.handlerregistry.HandlerRegistry;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;

/**
 * Responsible for registering and deregistering incoming message handler mappings.
 * 
 */
public class HandlerRegistryBean implements HandlerRegistry {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(HandlerRegistryBean.class);

    /** The handler map. */
    private ConcurrentMap handlerMap = new ConcurrentHashMap();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.handlerregistry.HandlerRegistry#associate(org.eclipse.swordfish.core.components.iapi.Role,
     *      javax.xml.namespace.QName, java.lang.String,
     *      org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy)
     */
    public void associate(final Role role, final QName service, final String operationName,
            final IncomingMessageHandlerProxy handler) {
        MultiKey key = this.buildKey(role, service, operationName);
        LOG.debug("associating handler with key " + key.toString());
        this.handlerMap.put(key, handler);
    }

    /**
     * destroy method.
     */
    public void destroy() {
        if (this.handlerMap != null) {
            this.handlerMap.clear();
            this.handlerMap = null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.handlerregistry.HandlerRegistry#getHandler(org.eclipse.swordfish.core.components.iapi.Role,
     *      javax.xml.namespace.QName, java.lang.String)
     */
    public IncomingMessageHandlerProxy getHandler(final Role role, final QName service, final String operationName) {
        MultiKey key = this.buildKey(role, service, operationName);
        return (IncomingMessageHandlerProxy) this.handlerMap.get(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.handlerregistry.HandlerRegistry#has(org.eclipse.swordfish.core.components.iapi.Role,
     *      javax.xml.namespace.QName, java.lang.String)
     */
    public boolean has(final Role role, final QName service, final String operationName) {
        MultiKey key = this.buildKey(role, service, operationName);
        return this.handlerMap.containsKey(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.handlerregistry.HandlerRegistry#remove(org.eclipse.swordfish.core.components.iapi.Role,
     *      javax.xml.namespace.QName, java.lang.String, boolean)
     */
    public void remove(final Role role, final QName service, final String operationName, final boolean sbbInitiated) {
        MultiKey key = this.buildKey(role, service, operationName);
        InternalIncomingMessageHandler handler = (InternalIncomingMessageHandler) this.handlerMap.remove(key);
        if (null != handler) {
            handler.onRelease(sbbInitiated);
        }
    }

    /**
     * Builds the key.
     * 
     * @param role
     *        the role
     * @param service
     *        the service
     * @param operationName
     *        the operation name
     * 
     * @return the multi key
     */
    private MultiKey buildKey(final Role role, final QName service, final String operationName) {
        return new MultiKey(role, service, operationName);
    }

}
