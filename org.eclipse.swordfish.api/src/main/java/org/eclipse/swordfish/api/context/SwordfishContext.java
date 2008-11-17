/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Oliver Wolf - initial API and implementation
 *******************************************************************************/

package org.eclipse.swordfish.api.context;

import java.util.Map;

import javax.jbi.component.ComponentContext;

import org.apache.servicemix.nmr.api.EndpointRegistry;

/**
 * Provides the access to the underlying Swordfish and SMX facilities.
 * Is published as the osgi service with the interface name org.eclipse.swordfish.api.context.SwordfishContext.
 * Can be also injected via @see org.eclipse.swordfish.api.context.SwordfishContextAware interface
 *
 */
public interface SwordfishContext {
    /**
     * Returns the SMX endpoint registry
     */
    public EndpointRegistry getEndpointRegistry();

    /**
     * Updates the configuration with the specified id
     * @see org.eclipse.swordfish.api.configuration.ConfigurationConsumer
     */
    public void updateConfiguration(String id, Map<String, ?> configurationData);

    /**
     * Returns the JBI component context
     */
    public ComponentContext getComponentContext();
}
