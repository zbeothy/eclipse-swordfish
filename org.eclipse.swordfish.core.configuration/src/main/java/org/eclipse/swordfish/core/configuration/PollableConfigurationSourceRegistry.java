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
package org.eclipse.swordfish.core.configuration;

import java.util.Map;

import org.eclipse.swordfish.api.configuration.PollableConfigurationSource;
import org.eclipse.swordfish.core.util.RegistryImpl;
import org.springframework.util.Assert;

public class PollableConfigurationSourceRegistry extends RegistryImpl<PollableConfigurationSource> {
    private ConfigurationAgent configurationAgent;


    /* TODO in some time we would to create something like configuration managers to handle different types of PollableConfigurationSource instances
     *
     */
    @Override
    protected void doRegister(PollableConfigurationSource pollableConfigurationSource,
            Map<String, ?> properties) throws Exception {
        Assert.notNull(configurationAgent);
        Assert.notNull(pollableConfigurationSource);
        configurationAgent.handleConfiguration(pollableConfigurationSource.getConfigurations());
        super.doRegister(pollableConfigurationSource, properties);
    }

    public ConfigurationAgent getConfigurationAgent() {
        return configurationAgent;
    }

    public void setConfigurationAgent(ConfigurationAgent configurationAgent) {
        this.configurationAgent = configurationAgent;
    }
}