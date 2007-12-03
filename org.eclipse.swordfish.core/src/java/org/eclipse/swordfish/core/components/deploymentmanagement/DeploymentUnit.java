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

/**
 * The Interface DeploymentUnit.
 */
public interface DeploymentUnit {

    /**
     * Gets the deployment path.
     * 
     * @return the deployment path
     */
    String getDeploymentPath();

    /**
     * Gets the handler.
     * 
     * @return the handler
     */
    DeploymentUnitHandler getHandler();

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Gets the state.
     * 
     * @return the state
     */
    DeploymentUnitState getState();

    /**
     * Gets the type.
     * 
     * @return the type
     */
    DeploymentUnitType getType();

    /**
     * Sets the state.
     * 
     * @param state
     *        the new state
     */
    void setState(DeploymentUnitState state);

}
