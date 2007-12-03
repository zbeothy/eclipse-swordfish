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
package org.eclipse.swordfish.core.components.deploymentmanagement;

import java.util.Collection;

/**
 * The Interface DeploymentRepository.
 */
public interface DeploymentRepository {

    /**
     * adds a deployment unit to the list of the deployments and persists the state.
     * 
     * @param unit
     *        the deployment unit to add
     */
    void addDeploymentUnit(DeploymentUnit unit);

    /**
     * Gets the deployment repository location.
     * 
     * @return the deployment repository location
     */
    String getDeploymentRepositoryLocation();

    /**
     * Gets the deployment unit.
     * 
     * @param name
     *        the name of the deployment unit to retrieve
     * 
     * @return -- the deployment unit for the requested name, or null if such an object does not
     *         exist
     */
    DeploymentUnit getDeploymentUnit(String name);

    /**
     * Gets the deployment unit names.
     * 
     * @return -- a collection of Strings that are names of deployed deployment units
     */
    Collection getDeploymentUnitNames();

    /**
     * Gets the deployment units in state.
     * 
     * @param state
     *        the state used for selection
     * 
     * @return -- a non-null collection containing the Deployment units in the requested state, or
     *         an empty collection if the are no deployments in the requested state.
     */
    Collection getDeploymentUnitsInState(DeploymentUnitState state);

    /**
     * Gets the out of sync deplyoments.
     * 
     * @return -- a collection of deploy units that are out of sync with the containers deployment
     *         repository (in terms that they are indicated to be deployed into the engine, but the
     *         deployment path in the file system does not exist anymore)
     */
    Collection getOutOfSyncDeplyoments();

    /**
     * removes a deployment unit from the list of the deployments and persists the state.
     * 
     * @param name
     *        the deployment unit to add
     */

    void removeDeploymentUnit(String name);

    /**
     * changes the state of a given deployment Unit to the given state.
     * 
     * @param name
     *        the name of the deployment Unit to be changed
     * @param state
     *        the new state of the deployment unit
     */
    void setDeploymentUnitState(String name, DeploymentUnitState state);
}
