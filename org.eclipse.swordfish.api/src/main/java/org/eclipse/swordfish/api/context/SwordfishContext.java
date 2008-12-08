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

import javax.jbi.component.ComponentContext;

import org.apache.servicemix.nmr.api.EndpointRegistry;
import org.eclipse.swordfish.api.configuration.ConfigurationService;

/**
 * Provides the access to the underlying Swordfish and SMX facilities.
 * Is published as the osgi service with the interface name org.eclipse.swordfish.api.context.SwordfishContext.
 * Can be also injected via {@link SwordfishContextAware} interface
 *
 */
public interface SwordfishContext {

	/**
     * Access the SMX endpoint registry.
     * @return the ServiceMix platform endpoing registry.
     */
    EndpointRegistry getEndpointRegistry();


    ConfigurationService getConfigurationService();

    /**
     * Access the JBI component context.
     * @return the platform JBI context.
     */
    ComponentContext getComponentContext();
}
