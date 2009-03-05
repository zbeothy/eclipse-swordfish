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

import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;
import org.eclipse.swordfish.api.configuration.PollableConfigurationSource;
import org.eclipse.swordfish.api.context.SwordfishContext;
import org.eclipse.swordfish.api.event.EventService;
import org.eclipse.swordfish.core.event.ConfigurationEventImpl;
import org.eclipse.swordfish.core.event.EventServiceImpl;
import org.eclipse.swordfish.core.test.util.OsgiSupport;
import org.eclipse.swordfish.core.test.util.base.TargetPlatformOsgiTestCase;
import org.eclipse.swordfish.core.test.util.mock.MapEntryMatcher;
import org.osgi.framework.ServiceReference;

public class ConfigurationTest extends TargetPlatformOsgiTestCase {

	/**
	 * Verify that the update of a configuration is published to a configuration
	 * consumer
	 */
	@SuppressWarnings("unchecked")
	public void testPublishUpdateConfiguration() throws Exception {
		String servicePid = "testPublishUpdateConfiguration";

		// This is the update we push in ...
		final Map<String, String> configuration = createConfiguration("TestTime");

		ConfigurationConsumer configurationConsumer = createStrictMock(ConfigurationConsumer.class);
		makeThreadSafe(configurationConsumer, true);

		// Stub the method getId to always return the service.pid
		expect(configurationConsumer.getId()).andStubReturn(servicePid);

		// On update we first receive an empty (null) configuration ...
		configurationConsumer.onReceiveConfiguration(null);
		configurationConsumer.onReceiveConfiguration(mapContainsEntry(configuration, "TestTime"));

		// Go, Willi!
		replay(configurationConsumer);

		String serviceClassName = ConfigurationConsumer.class.getCanonicalName();
		addRegistrationToCancel(bundleContext.registerService(serviceClassName,	configurationConsumer, null));

		ServiceReference contextService = bundleContext.getServiceReference(SwordfishContext.class.getCanonicalName());
		SwordfishContext swordfishContext = (SwordfishContext) bundleContext.getService(contextService);
		swordfishContext.getConfigurationService().updateConfiguration(servicePid, configuration);
		Thread.sleep(500);

		verify(configurationConsumer);
	}

	
	/**
	 * Verify that a configuration stored in a PollableConfigurationSource will update a configuration consumer
	 * upon availability of the pollable source.
	 */
	@SuppressWarnings("unchecked")
	public void testPollableConfigurationSource() throws Exception {
		String servicePid = "testUpdatePollableConfigurationSource";

		// This is the update we expect in our consumer
		final Map<String, String> configuration = createConfiguration("TestTime");
		ConfigurationConsumer configurationConsumer = createStrictMock(ConfigurationConsumer.class);
		makeThreadSafe(configurationConsumer, true);

		// Stub the method getId to always return the service.pid
		expect(configurationConsumer.getId()).andStubReturn(servicePid);

		// On update we first receive an empty (null) configuration ...
		configurationConsumer.onReceiveConfiguration(null);
		configurationConsumer.onReceiveConfiguration(mapContainsEntry(configuration, "TestTime"));
		
		PollableConfigurationSource pollableConfig = createStrictMock(PollableConfigurationSource.class);
		makeThreadSafe(pollableConfig, true);
		
		// The PollableConfigurationSource delivers a map of configurations with String keys ...
		final Map<String, Map<String, String>> pollConfig = new HashMap<String, Map<String, String>>();
		pollConfig.put(servicePid, configuration);		
		expect(pollableConfig.getConfigurations()).andReturn(pollConfig);
		
		// Go, Willi!
		replay(configurationConsumer, pollableConfig);
		
		addRegistrationToCancel(bundleContext.registerService(
				ConfigurationConsumer.class.getCanonicalName(), configurationConsumer, null));
		
		addRegistrationToCancel(bundleContext.registerService(
				PollableConfigurationSource.class.getCanonicalName(), pollableConfig, null));
		Thread.sleep(500);

		verify(configurationConsumer, pollableConfig);
	}

	
	/**
	 * Check if an explicitly created configuration event will reach a configuration consumer.
	 */
	@SuppressWarnings("unchecked")
	public void testAsynchronousConfigurationUpdate() throws Exception {
		String servicePid = "testAsynchronousConfigurationUpdate";

		// This is the update we expect in our consumer
		final Map<String, String> configuration = createConfiguration("TestTime");
		ConfigurationConsumer configurationConsumer = createStrictMock(ConfigurationConsumer.class);
		makeThreadSafe(configurationConsumer, true);

		// Stub the method getId to always return the service.pid
		expect(configurationConsumer.getId()).andStubReturn(servicePid);

		// On update we first receive an empty (null) configuration ...
		configurationConsumer.onReceiveConfiguration(null);
		configurationConsumer.onReceiveConfiguration(mapContainsEntry(configuration, "TestTime"));

		// Go, Willi!
		replay(configurationConsumer);
		
		addRegistrationToCancel(bundleContext.registerService(
				ConfigurationConsumer.class.getCanonicalName(),	configurationConsumer, null));

		// Get the event service ...
		EventServiceImpl eventSender = (EventServiceImpl) OsgiSupport.getReference(bundleContext, EventService.class);
		assertNotNull(eventSender);
		assertNotNull(eventSender.getEventAdmin());

		// ... create a configuration event, add a "new" configuration for the consumer ...
		ConfigurationEventImpl<Object> configurationEvent = new ConfigurationEventImpl<Object>();
		Map<String, Object> configurations = new HashMap<String, Object>();
		configurations.put(servicePid, configuration);
		configurationEvent.setConfiguration(configurations);
		
		// ... and publish it!
		eventSender.postEvent(configurationEvent);
		Thread.sleep(500);

		verify(configurationConsumer);
	}

	// -------------------------------- HELPERS --------------------------------
	
	/**
	 * Create a default configuration
	 * @param key - the key that should be added with the current nano time as value.
	 * @return
	 */
	private static Map<String, String> createConfiguration(String key) {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(key, String.valueOf(System.nanoTime()));
		return configuration;
	}

	
	/**
	 * Verify that a Map contains an expected key with expected value
	 * @param mapIn - the expected map
	 * @param key - the key to be found
	 * @return true if the actual map contains the key and the value
	 */
	public static Map<?,?> mapContainsEntry(Map<?,?> mapIn, Object key) {
		reportMatcher(new MapEntryMatcher(mapIn, key));
		return null;
	}

	
	@Override
	protected String getManifestLocation() {
		return "classpath:org/eclipse/swordfish/core/configuration/test/MANIFEST.MF";
	}
}