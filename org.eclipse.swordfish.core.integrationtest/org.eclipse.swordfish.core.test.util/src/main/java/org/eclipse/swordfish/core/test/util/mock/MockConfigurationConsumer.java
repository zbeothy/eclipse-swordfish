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
package org.eclipse.swordfish.core.test.util.mock;

import java.util.Map;

import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;

public class MockConfigurationConsumer<T> implements ConfigurationConsumer<T> {
    private String id;
    private Map<String, ?> configuration;

    public void onReceiveConfiguration(Map<String, T> configuration) {
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, ?> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, ?> configuration) {
        this.configuration = configuration;
    }

}
