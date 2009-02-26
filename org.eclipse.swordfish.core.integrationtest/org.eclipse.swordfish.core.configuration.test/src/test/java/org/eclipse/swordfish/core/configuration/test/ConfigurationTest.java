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
package org.eclipse.swordfish.core.configuration.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;
import org.eclipse.swordfish.api.configuration.PollableConfigurationSource;
import org.eclipse.swordfish.api.context.SwordfishContext;
import org.eclipse.swordfish.api.event.EventService;
import org.eclipse.swordfish.core.event.ConfigurationEventImpl;
import org.eclipse.swordfish.core.event.EventServiceImpl;
import org.eclipse.swordfish.core.test.util.OsgiSupport;
import org.eclipse.swordfish.core.test.util.base.TargetPlatformOsgiTestCase;
import org.eclipse.swordfish.core.test.util.mock.MockConfigurationConsumer;
import org.eclipse.swordfish.core.test.util.mock.PollableConfigurationSourceMock;


public class ConfigurationTest extends TargetPlatformOsgiTestCase {

    public void test1SimpleConfiguration() throws Exception {
        final CountDownLatch contextInjectedLatch = new CountDownLatch(1);
        final CountDownLatch configurationUpdated = new CountDownLatch(1);
        final Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("TestTime", String.valueOf(System.nanoTime()));
        MockConfigurationConsumer configurationConsumer = new MockConfigurationConsumer() {
            @Override
            public void onReceiveConfiguration(Map configuration) {
                if (configuration != null && configuration.containsKey("TestTime")) {
                    super.onReceiveConfiguration(configuration);
                   configurationUpdated.countDown();
               }

            }
        };
        configurationConsumer.setId("MockConfigurationConsumerIdTest1");
        addRegistrationToCancel(bundleContext.registerService(ConfigurationConsumer.class.getCanonicalName(), configurationConsumer, null));

        SwordfishContext swordfishContext = (SwordfishContext) bundleContext.getService(bundleContext.getServiceReference(SwordfishContext.class.getCanonicalName()));
        swordfishContext.getConfigurationService().updateConfiguration("MockConfigurationConsumerIdTest1", configuration);

        assertTrue(configurationUpdated.await(9999, TimeUnit.SECONDS));
        assertEquals(configurationConsumer.getConfiguration().get("TestTime"),
                configuration.get("TestTime"));
    }
    public void test2ConfigurationPollableSourceTest() throws Exception {
        final CountDownLatch configurationUpdated = new CountDownLatch(1);
        final Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("TestTime", String.valueOf(System.currentTimeMillis()));
        MockConfigurationConsumer configurationConsumer = new MockConfigurationConsumer() {
            @Override
            public void onReceiveConfiguration(Map configuration) {
               if (configuration != null && configuration.containsKey("TestTime")) {
                   super.onReceiveConfiguration(configuration);
                   configurationUpdated.countDown();
               }

            }
        };
        configurationConsumer.setId("MockConfigurationConsumerIdTest2");
        addRegistrationToCancel(bundleContext.registerService(ConfigurationConsumer.class.getCanonicalName(), configurationConsumer, null));
        addRegistrationToCancel(bundleContext.registerService(PollableConfigurationSource.class.getCanonicalName(),
                new PollableConfigurationSourceMock().addConfiguration("MockConfigurationConsumerIdTest2", configuration), null));
        assertTrue(configurationUpdated.await(4, TimeUnit.SECONDS));
        assertEquals(configurationConsumer.getConfiguration().get("TestTime"), configuration.get("TestTime"));
    }
    public void test3AsynchronousConfigurationUpdateTest() throws Exception {
        final CountDownLatch configurationUpdated = new CountDownLatch(1);
        final Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("TestTime", String.valueOf(System.nanoTime()));
        EventServiceImpl eventSender = (EventServiceImpl)OsgiSupport.getReference(bundleContext, EventService.class);
        assertNotNull(eventSender);
        assertNotNull(eventSender.getEventAdmin());
        MockConfigurationConsumer configurationConsumer = new MockConfigurationConsumer() {
            @Override
            public void onReceiveConfiguration(Map configuration) {

               if (configuration != null && configuration.containsKey("TestTime")) {
                   super.onReceiveConfiguration(configuration);
                   configurationUpdated.countDown();
               }

            }
        };
        configurationConsumer.setId("MockConfigurationConsumerIdTest3");
        addRegistrationToCancel(bundleContext.registerService(ConfigurationConsumer.class.getCanonicalName(), configurationConsumer, null));
        ConfigurationEventImpl configurationEvent = new ConfigurationEventImpl();
        Map<String, Object> configurations = new HashMap<String, Object>();
        configurations.put("MockConfigurationConsumerIdTest3", configuration);
        configurationEvent.setConfiguration(configurations);
        eventSender.postEvent(configurationEvent);
        assertTrue(configurationUpdated.await(4, TimeUnit.SECONDS));
        assertEquals(configurationConsumer.getConfiguration().get("TestTime"),
                configuration.get("TestTime"));
    }

    @Override
    protected String getManifestLocation() {
        return "classpath:org/eclipse/swordfish/core/configuration/test/MANIFEST.MF";
    }
}