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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swordfish.api.configuration.PollableConfigurationSource;

public class PollableConfigurationSourceMock implements
        PollableConfigurationSource<Map<String, Object>> {

    private Map<String, Map<String, Object>> configurations;

    public PollableConfigurationSourceMock(Map<String, Map<String, Object>> configurations) {
        this.configurations = configurations;
    }

    public PollableConfigurationSourceMock() {
        configurations = new HashMap<String, Map<String, Object>>();
    }

    public PollableConfigurationSourceMock addConfiguration(String id, Map configData) {
        configurations.put(id, configData);
        return this;
    }


    public Map<String, Map<String, Object>> getConfigurations() {
        return configurations;
    }

    public Map<String, ?> getProperties() {
        // TODO Auto-generated method stub
        return null;
    }
}
