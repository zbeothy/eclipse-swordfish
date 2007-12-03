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
package org.eclipse.swordfish.core.components.locatorproxy.impl;

import java.io.InputStream;
import java.util.List;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorProxyInstanceConfigurer;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * The Class IonaLocatorProxyInstanceConfigurerBean.
 */
public class IonaLocatorProxyInstanceConfigurerBean implements LocatorProxyInstanceConfigurer {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(IonaLocatorProxyInstanceConfigurerBean.class);

    /** The locator proxy. */
    private LocatorProxy locatorProxy;

    /** The location list. */
    private List locationList;

    /** The config manager. */
    private ConfigurationRepositoryManagerInternal configManager;

    /**
     * Instantiates a new iona locator proxy instance configurer bean.
     */
    public IonaLocatorProxyInstanceConfigurerBean() {
    }

    /**
     * deploys an atrifact into the locator-proxy service engine if not already deployed. If there
     * is an artifact already deployed than a reference counter is incremented.
     * 
     * @param locatorId
     *        the locator id
     * @param deploymentArtifact
     *        the deployment artifact
     * 
     * @throws InternalInfrastructureException
     */
    public void deploy(final String locatorId, final String deploymentArtifact) throws InternalInfrastructureException {
        this.locatorProxy.deploy(locatorId, deploymentArtifact);
    }

    /**
     * when this bean is destroyed than it undeploys all locator definitions for a participant into
     * the locator proxy for the case that the proxy usage has been activated.
     */
    public void destroy() {
        if (this.locatorProxy.isActive()) {
            for (int i = 0; i < this.locationList.size(); i++) {
                String locationId = (String) this.locationList.get(i);
                try {
                    this.getLocatorProxy().undeploy(locationId);
                    LOG.info("undeployed access to locator services at locator (cluster) " + locationId);
                } catch (InternalInfrastructureException e) {
                    LOG.warn("could not undeploy locator deployment information for " + locationId
                            + " because of an infrastructure exception.");
                }
            }
        }
    }

    /**
     * Gets the config manager.
     * 
     * @return Returns the configManager.
     */
    public ConfigurationRepositoryManagerInternal getConfigManager() {
        return this.configManager;
    }

    /**
     * Gets the location list.
     * 
     * @return Returns the locationList.
     */
    public List getLocationList() {
        return this.locationList;
    }

    /**
     * Gets the location names.
     * 
     * @return the location names
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.LocatorProxyInstanceConfigurer#getLocationNames()
     */
    public List getLocationNames() {
        return this.locationList;
    }

    /**
     * Gets the locator proxy.
     * 
     * @return Returns the locatorProxy.
     */
    public LocatorProxy getLocatorProxy() {
        return this.locatorProxy;
    }

    /**
     * Note: This method returns null if the list is empty!!!.
     * 
     * @return the prefered location
     */
    public String getPreferedLocation() {
        if (this.locationList.size() > 0)
            return (String) this.locationList.get(0);
        else
            return null;
    }

    /**
     * when this bean is initialized than it deploys all locator definitions for a participant into
     * the locator proxy for the case that the proxy usage has been activated.
     */
    public void init() {
        if (this.locatorProxy.isActive()) {
            for (int i = 0; i < this.locationList.size(); i++) {
                String locationId = (String) this.locationList.get(i);
                try {
                    InputStream stream = this.configManager.getResource(null, null, "Locator", locationId + ".xml");
                    String locationConfig = TransformerUtil.stringFromInputStream(stream);
                    String proxyRepresentationOfConfig = IonaDeploymentFormatter.formatDeployment(locationConfig);
                    this.getLocatorProxy().deploy(locationId, proxyRepresentationOfConfig);
                    LOG.info("deployed access to locator services at locator (cluster) " + locationId);
                } catch (ConfigurationRepositoryResourceException e) {
                    LOG
                        .warn("could not retrieve locator deployment information for "
                                + locationId
                                + " because of an configuration repository exception, services from this location will not be accessible.");
                } catch (InternalInfrastructureException e) {
                    LOG.warn("could not retrieve locator deployment information for " + locationId
                            + " because of an infrastructure exception, services from this location will not be accessible.");
                }
            }
        }
    }

    /**
     * Sets the config manager.
     * 
     * @param configManager
     *        The configManager to set.
     */
    public void setConfigManager(final ConfigurationRepositoryManagerInternal configManager) {
        this.configManager = configManager;
    }

    /**
     * Sets the location list.
     * 
     * @param locationList
     *        The locationList to set.
     */
    public void setLocationList(final List locationList) {
        this.locationList = locationList;
    }

    /**
     * Sets the locator proxy.
     * 
     * @param deploymentHelper
     *        the deployment helper
     */
    public void setLocatorProxy(final LocatorProxy deploymentHelper) {
        this.locatorProxy = deploymentHelper;
    }

    /**
     * undeploys a locatorId configuration if all references to the configuration has been
     * undeployed.
     * 
     * @param locatorId
     *        the locator id
     * 
     * @throws InternalInfrastructureException
     */
    public void undeploy(final String locatorId) throws InternalInfrastructureException {
        this.locatorProxy.undeploy(locatorId);
    }
}
