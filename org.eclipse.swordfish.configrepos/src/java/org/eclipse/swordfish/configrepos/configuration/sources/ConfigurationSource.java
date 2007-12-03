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
import org.eclipse.swordfish.configrepos.RepositorySource;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * Configuration sources shall implement this interface, in order to be able to make a separation
 * between configuration source and configuration target in the commons-configuration
 * implementation.
 * 
 */
public interface ConfigurationSource extends RepositorySource {

    /**
     * Should retrieve the configuration for the specific identifier provided in the call.
     * 
     * @param aTreeQualifier
     *        for the tree which shall be used as data source
     * @param aScopePath
     *        which should be used as a qualifier
     * 
     * @return Configuration which was found for the specified qualifier.
     * 
     * @throws ConfigurationRepositoryConfigException
     *         shall be thrown by any configuration source in case the requested configuration could
     *         not be fetched
     */
    Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException;
}
