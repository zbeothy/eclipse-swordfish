/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.core.management.messaging.impl;

import junit.framework.TestCase;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.mock.DummyParticipantMonitor;
import org.eclipse.swordfish.core.management.monitor.SbbMonitor;
import org.eclipse.swordfish.core.management.notification.EntityState;
import org.eclipse.swordfish.core.management.notification.ManagementNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantStateNotification;
import org.eclipse.swordfish.core.management.notification.impl.ManagementNotificationListenerBean;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class SbbMonitorTest.
 */
public class SbbMonitorTest extends TestCase {

    /** The Constant identity. */
    private final static UnifiedParticipantIdentity identity = new UnifiedParticipantIdentity(new InternalParticipantIdentity() {

        public String getApplicationID() {
            return "Test";
        }

        public String getInstanceID() {
            return "foo";
        }

    });

    /** The dummy1. */
    private DummyParticipantMonitor dummy1;

    /** The dummy2. */
    private DummyParticipantMonitor dummy2;

    /** Delay in milliseconds for processing notifications to be processed. */
    private ManagementNotificationListenerBean listener;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The sbb monitor. */
    private SbbMonitor sbbMonitor;

    /**
     * Instantiates a new sbb monitor test.
     * 
     * @param name
     *        the name
     */
    public SbbMonitorTest(final String name) {
        super(name);
    }

    /**
     * Test num participants.
     * 
     * @throws Exception
     */
    public void testNumParticipants() throws Exception {
        assertEquals(0, this.sbbMonitor.getNumParticipants().intValue());
        this.sbbMonitor.addParticipantMonitor(this.dummy1);
        this.sbbMonitor.addParticipantMonitor(this.dummy2);
        assertEquals(2, this.sbbMonitor.getNumParticipants().intValue());
        this.sbbMonitor.addParticipantMonitor(this.dummy1);
        assertEquals(2, this.sbbMonitor.getNumParticipants().intValue());
        this.sbbMonitor.removeParticipantMonitor(this.dummy1);
        assertEquals(1, this.sbbMonitor.getNumParticipants().intValue());
        this.sbbMonitor.removeParticipantMonitor(this.dummy2);
        assertEquals(0, this.sbbMonitor.getNumParticipants().intValue());
    }

    /**
     * tests that a new ParticipantMonitors is correctly registered in the SbbMonitor.
     * 
     * @throws Exception
     */
    public void testParticipantRegistration() throws Exception {
        ManagementNotification created = new ParticipantStateNotification(identity, EntityState.ADDED);
        assertEquals(0, this.sbbMonitor.getNumParticipants().intValue());
        this.listener.sendNotification(created);
        assertEquals(1, this.sbbMonitor.getNumParticipants().intValue());
        ManagementNotification removed = new ParticipantStateNotification(identity, EntityState.REMOVED);
        this.listener.sendNotification(removed);
        assertEquals(0, this.sbbMonitor.getNumParticipants().intValue());
    }

    /**
     * Test sbb state.
     * 
     * @throws Exception
     */
    public void testSbbState() throws Exception {
        assertEquals("STOPPED", this.sbbMonitor.getState());
        this.sbbMonitor.addParticipantMonitor(this.dummy1);
        this.sbbMonitor.addParticipantMonitor(this.dummy2);
        assertEquals("STOPPED", this.sbbMonitor.getState());
        this.dummy1.setState(State.RUNNING);
        assertEquals("STOPPED", this.sbbMonitor.getState());
        this.dummy2.setState(State.RUNNING);
        assertEquals("RUNNING", this.sbbMonitor.getState());
        this.dummy2.setState(State.FAILED);
        assertEquals("FAILED", this.sbbMonitor.getState());
        this.dummy1.setState(State.STOPPED);
        assertEquals("FAILED", this.sbbMonitor.getState());
        this.dummy2.setState(State.STOPPED);
        assertEquals("STOPPED", this.sbbMonitor.getState());
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.ctx =
                new FileSystemXmlApplicationContext(
                        new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
        this.listener =
                (ManagementNotificationListenerBean) this.ctx
                    .getBean("org.eclipse.swordfish.core.management.notification.ManagementNotificationListener");
        this.listener.setDirectProcessing(true);
        SbbMonitorBackendFactoryBean factory =
                (SbbMonitorBackendFactoryBean) this.ctx
                    .getBean("org.eclipse.swordfish.core.management.messaging.SbbMonitorBackendFactory");
        this.sbbMonitor = factory.getBackend().getMonitor();
        this.ctx.getBean("mbeanServer");
        this.dummy1 = new DummyParticipantMonitor(identity);
        this.dummy1.setState(State.STOPPED);
        this.dummy2 = new DummyParticipantMonitor(identity);
        this.dummy2.setState(State.STOPPED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.listener = null;
        this.ctx.destroy();
        super.tearDown();
    }

}
