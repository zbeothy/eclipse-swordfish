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
package org.eclipse.swordfish.core.configuration.xml;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swordfish.api.configuration.PollableConfigurationSource;
import org.springframework.util.Assert;

public class XmlConfigurationSource implements PollableConfigurationSource<Map<String, String>> {
    private Map<String, Object> props = new HashMap<String, Object>();
    private XmlToPropertiesTransformer propertiesTransformer = new XmlToPropertiesTransformerImpl();
    public void setConfigurationPath(String path) {
        propertiesTransformer.loadConfiguration(path);
    }

    public void setConfigurationPath(URL path) {
        propertiesTransformer.loadConfiguration(path);
    }

    public Map<String, Map<String, String>> getConfigurations() {
       Assert.state(propertiesTransformer.isConfigurationLoaded(), "Configuration is not loaded");
       return propertiesTransformer.getPropertiesForPids();
    }

    public Map<String, ?> getProperties() {
        return props;
    }
    public XmlToPropertiesTransformer getPropertiesTransformer() {
        return propertiesTransformer;
    }

    public void setPropertiesTransformer(
            XmlToPropertiesTransformer propertiesTransformer) {
        this.propertiesTransformer = propertiesTransformer;
    }
}
