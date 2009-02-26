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
package org.eclipse.swordfish.core.context;

import javax.jbi.component.ComponentContext;

import org.apache.servicemix.nmr.api.EndpointRegistry;
import org.apache.servicemix.nmr.api.NMR;
import org.eclipse.swordfish.api.configuration.ConfigurationService;
import org.eclipse.swordfish.api.context.SwordfishContext;
import org.eclipse.swordfish.api.event.EventService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class SwordfishContextImpl implements SwordfishContext, InitializingBean {
    private NMR nmr;
    private ConfigurationService configurationService;
    private EventService eventService;

    public ComponentContext getComponentContext() {
       throw new UnsupportedOperationException("This method is not implemented yet");
    }

    public EndpointRegistry getEndpointRegistry() {
        return nmr.getEndpointRegistry();
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(nmr, "nmr property is required");
        //Assert.notNull(configurationAdmin, "configurationAdmin property is required");
    }

    public NMR getNmr() {
        return nmr;
    }

    public void setNmr(NMR nmr) {
        this.nmr = nmr;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
	


}
