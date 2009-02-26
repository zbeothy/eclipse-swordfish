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

/**
 * Indicates that the implementor will be aware of the configuration updates
 * Can be plugged into the Swordfish framework as an osgi service with the interface name
 * org.eclipse.swordfish.api.configuration.ConfigurationConsumer
 * to receive notifications about the change in the configuration.
 * @param T The type of the configuration consumed by the receiver.
 *
 */
public interface ConfigurationConsumer<T> {
    /**
     *  Uniquely identifies the configuration
     *  @return the identifier String, expected to be unique among all registered
     *  ConfigurationConsumer instances registered to the system. Must not be
     *  <code>null</code> or an empty String.
     */
    String getId();

    /**
     * The callback method invoked by the Swordfish environment when the
     * configuration is changed
     * @param configuration the new/updated configuration.
     */
    void onReceiveConfiguration(Map<String, T> configuration);
}
