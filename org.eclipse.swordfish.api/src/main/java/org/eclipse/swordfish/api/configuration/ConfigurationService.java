/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.api.configuration;

import java.util.Map;

public interface ConfigurationService {
    /**
     * Updates the configuration with the specified id
     * @see org.eclipse.swordfish.api.configuration.ConfigurationConsumer
     * @param <T> Type of configuration appropriate for the id passed.
     * @param id unique configuration identifier, must not be <code>null</code>
     * or an empty String
     * @param configurationData Map of configurationData
     */
    public <T> void updateConfiguration(String id, Map<String, T> configurationData);
}
