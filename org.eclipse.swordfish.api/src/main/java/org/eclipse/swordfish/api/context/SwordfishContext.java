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

public interface SwordfishContext {
    public EndpointRegistry getEndpointRegistry();
    public void updateConfiguration(String id, Map<String, ?> configurationData);
    public ComponentContext getComponentContext();
}
