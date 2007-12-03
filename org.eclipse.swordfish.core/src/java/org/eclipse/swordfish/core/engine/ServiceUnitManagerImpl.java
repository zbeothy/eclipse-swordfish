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
package org.eclipse.swordfish.core.engine;

import javax.jbi.component.ServiceUnitManager;
import javax.jbi.management.DeploymentException;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentRepository;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitState;
import org.eclipse.swordfish.core.components.deploymentmanagement.impl.DeploymentUnitImpl;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * This is the JBI declared service unit manager that is used to deploy InternalSBB artifacts.
 */
public class ServiceUnitManagerImpl implements ServiceUnitManager {

    /** This components log. */
    private static final Log LOG = SBBLogFactory.getLog(ServiceUnitManagerImpl.class);

    /** the internal deployment repository that keeps track of all deployed atrifacts. */
    private DeploymentRepository repository;

    /**
     * public constructor to instantiate a SU manager. According to the JBI standard the component
     * implementation is used to retrieve instances of this object.
     * 
     * @param repository
     *        the repository to be used by this SU manager.
     */
    public ServiceUnitManagerImpl(final DeploymentRepository repository) {
        this.repository = repository;
    }

    /**
     * Deploy.
     * 
     * @param name
     *        the name
     * @param path
     *        the path
     * 
     * @return the string
     * 
     * @throws DeploymentException
     * 
     * @see javax.jbi.component.ServiceUnitManager#deploy(java.lang.String, java.lang.String)
     */
    public String deploy(final String name, final String path) throws DeploymentException {
        DeploymentUnit du = new DeploymentUnitImpl(name, path, this.repository.getDeploymentRepositoryLocation());
        du.setState(DeploymentUnitState.SHUTDOWN);
        this.repository.addDeploymentUnit(du);
        LOG.debug("Deployment " + du.getName() + " deployed.");
        return "SUCCESS";
    }

    /**
     * Init.
     * 
     * @param name
     *        the name
     * @param path
     *        the path
     * 
     * @throws DeploymentException
     * 
     * @see javax.jbi.component.ServiceUnitManager#init(java.lang.String, java.lang.String)
     */
    public void init(final String name, final String path) throws DeploymentException {
        DeploymentUnit du = this.repository.getDeploymentUnit(name);
        if (du == null) {
            LOG.info("Adding the detected out of sync deployment: " + name);
            du = new DeploymentUnitImpl(name, path, this.repository.getDeploymentRepositoryLocation());
            du.setState(DeploymentUnitState.STOPPED);
            this.repository.addDeploymentUnit(du);
            LOG.info("Deployment " + du.getName() + " initialized.");
        }
    }

    /**
     * Shut down.
     * 
     * @param name
     *        the name
     * 
     * @throws DeploymentException
     * 
     * @see javax.jbi.component.ServiceUnitManager#shutDown(java.lang.String)
     */
    public void shutDown(final String name) throws DeploymentException {
        DeploymentUnit du = this.repository.getDeploymentUnit(name);
        du.setState(DeploymentUnitState.SHUTDOWN);
        LOG.debug("Deployment " + du.getName() + " shutdown.");
        // DIAG System.out.println( "Deployment " + du.getName() + "
        // shutdown.");
    }

    /**
     * Start.
     * 
     * @param name
     *        the name
     * 
     * @throws DeploymentException
     * 
     * @see javax.jbi.component.ServiceUnitManager#start(java.lang.String)
     */
    public void start(final String name) throws DeploymentException {
        DeploymentUnit du = this.repository.getDeploymentUnit(name);
        if ((du != null) && !DeploymentUnitState.STARTED.equals(du.getState())) {
            try {
                du.getHandler().deploy();
                du.setState(DeploymentUnitState.STARTED);
                // DIAG System.out.println( "Deployment " + du.getName() + "
                // started.");
            } catch (InternalInfrastructureException e) {
                LOG.error("failed to restart the deployment " + du.getName() + " because of " + e.getMessage());
            }
        }
    }

    /**
     * Stop.
     * 
     * @param name
     *        the name
     * 
     * @throws DeploymentException
     * 
     * @see javax.jbi.component.ServiceUnitManager#stop(java.lang.String)
     */
    public void stop(final String name) throws DeploymentException {
        DeploymentUnit du = this.repository.getDeploymentUnit(name);
        du.setState(DeploymentUnitState.STOPPED);
        LOG.debug("Deployment " + du.getName() + " stopped.");
        // DIAG System.out.println( "Deployment " + du.getName() + " stopped.");
    }

    /**
     * Undeploy.
     * 
     * @param name
     *        the name
     * @param path
     *        the path
     * 
     * @return the string
     * 
     * @throws DeploymentException
     * 
     * @see javax.jbi.component.ServiceUnitManager#undeploy(java.lang.String, java.lang.String)
     */
    public String undeploy(final String name, final String path) throws DeploymentException {
        DeploymentUnit du = this.repository.getDeploymentUnit(name);
        try {
            du.getHandler().undeploy();
            LOG.debug("Deployment " + du.getName() + " undeployed.");
            // DIAG System.out.println( "Deployment " + du.getName() + "
            // undeployed.");
        } catch (InternalInfrastructureException e) {
            LOG.error("failed to undeploy the deployment " + du.getName() + " because of " + e.getMessage());
            return "FAILED";
        }
        this.repository.removeDeploymentUnit(name);
        return "SUCCESS";
    }

}
