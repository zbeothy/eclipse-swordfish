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
package org.eclipse.swordfish.configrepos.configuration.sources;

import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * Allows writing back the configuration into a location which is equilavent to a scopepath.
 * 
 */
public interface WritebackConfigurationSource extends ConfigurationSource {

    /**
     * Write back configuration data into the specified source.
     * 
     * @param aTreeQualifier
     *        which is the root of the configuration
     * @param aScopePath
     *        which should be used to write the configuration
     * @param aConfiguration
     *        which should be writen
     * 
     * @throws ConfigurationRepositoryConfigException
     *         wrapping exceptions created inside the mechanism
     */
    void updateConfiguration(final String aTreeQualifier, final ScopePath aScopePath, final Configuration aConfiguration)
            throws ConfigurationRepositoryConfigException;
}
