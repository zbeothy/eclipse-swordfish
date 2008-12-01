package org.eclipse.swordfish.core.configuration.test;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.EventFilter;
import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.api.event.EventService;
import org.eclipse.swordfish.api.event.Severity;
import org.eclipse.swordfish.api.event.TrackingEvent;
import org.eclipse.swordfish.core.event.EventHandlerRegistry;
import org.eclipse.swordfish.core.event.EventServiceImpl;
import org.eclipse.swordfish.core.event.SeverityEventFilter;
import org.eclipse.swordfish.core.event.TrackingEventImpl;
import org.eclipse.swordfish.core.test.planner.TargetPlatformOsgiTestCase;
import org.eclipse.swordfish.core.test.util.OsgiSupport;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;


public class ConfigurationTest extends TargetPlatformOsgiTestCase {

    public void testConfiguration() throws Exception {
    }

 }