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
package org.eclipse.swordfish.configrepos.scopepath.query.dom.impl;

import org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQuery;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.GetConfigurationType;

/**
 * The Class GetConfigurationTypeImpl.
 * 
 */
public class GetConfigurationTypeImpl implements GetConfigurationType {

    /** The _ configuration query. */
    private ConfigurationQuery configurationQuery;

    /**
     * (non-Javadoc).
     * 
     * @return the configuration query
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.GetConfigurationType#getConfigurationQuery()
     */
    public ConfigurationQuery getConfigurationQuery() {
        return this.configurationQuery;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aConfigQuery
     *        the a config query
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.GetConfigurationType#setConfigurationQuery(org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQuery)
     */
    public void setConfigurationQuery(final ConfigurationQuery aConfigQuery) {
        this.configurationQuery = aConfigQuery;
    }

}
