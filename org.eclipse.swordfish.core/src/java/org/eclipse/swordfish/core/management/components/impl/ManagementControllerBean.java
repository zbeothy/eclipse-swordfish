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
package org.eclipse.swordfish.core.management.components.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.MBeanServer;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.components.LifecycleComponent;
import org.eclipse.swordfish.core.management.components.ManagementController;

/**
 * The Class ManagementControllerBean.
 * 
 */
public class ManagementControllerBean implements ManagementController {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(ManagementControllerBean.class);

    /** MBean server to use. */
    private MBeanServer mbeanServer;

    /** The adapters. */
    private List adapters;

    /** The components. */
    private ArrayList components;

    /**
     * Instantiates a new management controller bean.
     */
    public ManagementControllerBean() {
        this.components = new ArrayList();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.components.ManagementController#addLifecycleComponent(org.eclipse.swordfish.core.management.components.LifecycleComponent)
     */
    public synchronized void addLifecycleComponent(final LifecycleComponent component) {
        if (this.components.contains(component)) return; // already added by dependency
        ArrayList depends;
        if (null != component.getDependents()) {
            depends = new ArrayList(component.getDependents());
        } else {
            depends = new ArrayList();
        }
        int i = 0;
        if (null != depends) {
            while ((depends.size() > 0) && (i < this.components.size())) {
                if (depends.contains(this.components.get(i))) {
                    depends.remove(this.components.get(i));
                }
                i++;
            }
        }
        if ((null == depends) || (0 == depends.size())) {
            this.components.add(i, component);
        } else {
            StringBuffer msg = new StringBuffer("Unready dependencies when adding LifecycleComponent " + component + " : ");
            for (Iterator iter = depends.iterator(); iter.hasNext();) {
                LifecycleComponent dependency = (LifecycleComponent) iter.next();
                msg.append(dependency).append(" ");
            }
            throw new IllegalArgumentException(msg.toString());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.components.ManagementController#destroy()
     */
    public synchronized void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        if (null != this.components) {
            for (Iterator iter = this.components.iterator(); iter.hasNext();) {
                LifecycleComponent component = (LifecycleComponent) iter.next();
                component.destroy();
            }
            this.components.clear();
            this.components = null;
        }
        this.mbeanServer = null;
        if (null != this.adapters) {
            this.adapters.clear();
            this.adapters = null;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * Gets the mbean server.
     * 
     * @return the <code>MBeanServer</code>
     */
    public MBeanServer getMbeanServer() {
        return this.mbeanServer;
    }

    /**
     * initialization - called from container.
     */
    public void init() {
        // adapters are initialized via container
    }

    /**
     * Sets the adapters.
     * 
     * @param adapters
     *        the new adapters
     */
    public void setAdapters(final List adapters) {
        this.adapters = adapters;
    }

    /**
     * Sets the mbean server.
     * 
     * @param mbeanServer
     *        set by container
     */
    public void setMbeanServer(final MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }

}
