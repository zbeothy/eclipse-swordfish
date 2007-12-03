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
package org.eclipse.swordfish.core.management.operations.impl;

import java.util.HashSet;
import java.util.List;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.internalproxy.InternalProxy;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;
import org.eclipse.swordfish.core.utils.DOM2Writer;
import org.w3c.dom.Document;

/**
 * The Class SbbPublisherBackendBean.
 */
public class SbbPublisherBackendBean extends NrPublisherBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(SbbPublisherBackendBean.class);

    /** The Constant operationName. */
    private static final String OPERATION_NAME = "publish";

    /** The activate. */
    private boolean activate = false;

    /** The service name. */
    private QName serviceName;

    /** The internal proxies. */
    private HashSet internalProxies;

    /**
     * Instantiates a new sbb publisher backend bean.
     */
    public SbbPublisherBackendBean() {
        this.internalProxies = new HashSet();
    }

    /**
     * Adds the internal proxy.
     * 
     * @param proxy
     *        the proxy
     */
    public void addInternalProxy(final InternalProxy proxy) {
        this.internalProxies.add(proxy);
        if (LOG.isTraceEnabled()) {
            LOG.trace("added proxy " + proxy + " to SbbPublisherBackend");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.NrPublisherBackend#destroy()
     */
    @Override
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        if (null != this.internalProxies) {
            this.internalProxies.clear();
        }
        super.destroy();
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.PublisherBackend#getInstrumentationOn()
     */
    public ObjectName getInstrumentationOn() {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.PublisherBackend#getState()
     */
    public State getState() {
        // Auto-generated method stub
        return null;
    }

    /**
     * Checks if is activate.
     * 
     * @return true, if is activate
     */
    public boolean isActivate() {
        return this.activate;
    }

    /**
     * Removes the internal proxy.
     * 
     * @param proxy
     *        the proxy
     * 
     * @return true, if successful
     */
    public boolean removeInternalProxy(final InternalProxy proxy) {
        boolean ret = this.internalProxies.remove(proxy);
        if (LOG.isTraceEnabled()) {
            LOG.trace("removed proxy " + proxy + " from SbbPublisherBackend with result " + ret);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.PublisherBackend#sendNotifications(java.util.List)
     */
    public boolean sendNotifications(final List notifications) {
        if ((0 >= this.internalProxies.size()) || (!this.activate)) return false;
        boolean ret = false;
        InternalProxy proxy = (InternalProxy) this.internalProxies.toArray()[0];
        Document doc = this.createMessage(notifications);
        String message = DOM2Writer.nodeToPrettyString(doc.getDocumentElement());
        try {
            proxy.invokeService(this.serviceName, OPERATION_NAME, message);
            ret = true;
        } catch (Exception e) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Publishing of notifications failed. Reason:\n" + e.getMessage());
                if (LOG.isTraceEnabled()) {
                    Object[] stackTrace = e.getStackTrace();
                    StringBuffer msg = new StringBuffer("Stracktrace:");
                    for (int i = 0; i < stackTrace.length; i++) {
                        msg.append("\n").append(stackTrace[i].toString());
                    }
                    LOG.trace(new String(msg));
                }
            }
        }
        return ret;
    }

    /**
     * Sets the activate.
     * 
     * @param activate
     *        the new activate
     */
    public void setActivate(final boolean activate) {
        this.activate = activate;
    }

    /**
     * Sets the service.
     * 
     * @param sServiceName
     *        the new service
     */
    public void setService(final String sServiceName) {
        this.serviceName = QName.valueOf(sServiceName);
    }

}
