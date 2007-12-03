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

import java.io.Serializable;

/**
 * The Class DeploymentUnitType.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public final class DeploymentUnitType implements Serializable {

    /** The Constant CONFIGURATION. */
    public static final DeploymentUnitType CONFIGURATION = new DeploymentUnitType(DeploymentUnitType.CONFIGURATION_STR);

    /** The Constant BOOTSTRAP_CONFIGURATION. */
    public static final DeploymentUnitType BOOTSTRAP_CONFIGURATION = new DeploymentUnitType(DeploymentUnitType.CONFIGURATION_STR);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4153207627926305622L;

    /** initiator string constant. */
    private static final String CONFIGURATION_STR = "configuration";

    /** The Constant BOOTSTRAP_CONFIGURATION_STR. */
    private static final String BOOTSTRAP_CONFIGURATION_STR = "bootstrap-configuration";

    /**
     * from String.
     * 
     * @param value
     *        the value
     * 
     * @return enum
     */
    public static DeploymentUnitType fromString(final String value) {
        if (CONFIGURATION_STR.equals(value)) return CONFIGURATION;
        if (BOOTSTRAP_CONFIGURATION_STR.equals(value)) return CONFIGURATION;
        return null;
    }

    /** value. */
    private String value;

    /**
     * private constructor.
     * 
     * @param value
     *        the value
     */
    private DeploymentUnitType(final String value) {
        this.value = value;
    }

    /**
     * (non-Javadoc).
     * 
     * @param obj
     *        the obj
     * 
     * @return true, if equals
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DeploymentUnitType) {
            if (this.value.equals(((DeploymentUnitType) obj).value)) return true;
        }
        return false;
    }

    /**
     * To string.
     * 
     * @return the string
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.value;
    }
}
