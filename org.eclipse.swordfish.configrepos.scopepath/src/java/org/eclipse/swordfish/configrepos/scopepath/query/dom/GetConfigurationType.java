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
package org.eclipse.swordfish.configrepos.scopepath.query.dom;

/**
 * The Interface GetConfigurationType.
 * 
 */
public interface GetConfigurationType {

    /**
     * Gets the configuration query.
     * 
     * @return the configuration query
     */
    ConfigurationQuery getConfigurationQuery();

    /**
     * Sets the configuration query.
     * 
     * @param aConfigQuery
     *        the a config query
     */
    void setConfigurationQuery(ConfigurationQuery aConfigQuery);
}
