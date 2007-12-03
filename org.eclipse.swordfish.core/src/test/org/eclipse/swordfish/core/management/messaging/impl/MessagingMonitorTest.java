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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.messaging.TestConsumerInOut;
import org.eclipse.swordfish.core.management.messaging.TestProviderInOut;
import org.eclipse.swordfish.core.management.notification.impl.ManagementNotificationListenerBean;
import org.eclipse.swordfish.core.management.objectname.ManagementObjectNameConstants;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class MessagingMonitorTest.
 */
public class MessagingMonitorTest extends TestCase {

    /** Delay in milliseconds for processing notifications to be processed. */
    private static final int PROCESSING_DELAY = 250;

    /** The listener. */
    private ManagementNotificationListenerBean listener;

    /** The mbs. */
    private MBeanServer mbs;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /**
     * Instantiates a new messaging monitor test.
     * 
     * @param name
     *        the name
     */
    public MessagingMonitorTest(final String name) {
        super(name);
    }

    /**
     * Test consumer blocking in out.
     * 
     * @throws Exception
     */
    public void testConsumerBlockingInOut() throws Exception {
        TestConsumerInOut exchange = new TestConsumerInOut(this.listener);
        MessagingMonitorBean monitor =
                (MessagingMonitorBean) this.ctx.getBean("org.eclipse.swordfish.core.management.messaging.MessagingMonitor");
        HashMap map = monitor.getPending();

        // is SbbMonitor present
        Set names = this.mbs.queryNames(ManagementObjectNameConstants.SBB_MONITOR, null);
        assertEquals(1, names.size());
        ObjectName sbbMonitor = (ObjectName) names.toArray()[0];
        int count = ((Integer) this.mbs.getAttribute(sbbMonitor, "TotalRequests")).intValue();
        // check that no messages are pending
        assertEquals(0, map.size());
        // simulate sending of service request
        String id = exchange.executeBlockingOut();
        // wait until events are processed
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }
        // check that exactly 1 message is pending
        assertEquals(1, map.size());
        // check that request was added
        int currentCount = ((Integer) this.mbs.getAttribute(sbbMonitor, "TotalRequests")).intValue();
        assertEquals(1, currentCount - count);
        // check that the protocol for new request is of correct type
        Iterator it = map.values().iterator();
        ExchangeJournal protocol = (ExchangeJournal) it.next();
        assertTrue(protocol instanceof ConsumerInOutJournal);
        // simulate receiving of service response
        exchange.executeBlockingIn(id);
        // wait until events are processed
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }
        // check that no messages are pending anymore
        assertEquals(0, map.size());
        // check that the count of requests was not increased
        currentCount = ((Integer) this.mbs.getAttribute(sbbMonitor, "TotalRequests")).intValue();
        assertEquals(1, currentCount - count);
    }

    /**
     * Test provider blocking in out.
     * 
     * @throws Exception
     */
    public void testProviderBlockingInOut() throws Exception {
        TestProviderInOut exchange = new TestProviderInOut(this.listener);
        MessagingMonitorBean monitor =
                (MessagingMonitorBean) this.ctx.getBean("org.eclipse.swordfish.core.management.messaging.MessagingMonitor");
        HashMap map = monitor.getPending();

        // is SbbMonitor present
        Set names = this.mbs.queryNames(ManagementObjectNameConstants.SBB_MONITOR, null);
        assertEquals(1, names.size());
        ObjectName sbbMonitor = (ObjectName) names.toArray()[0];
        int count = ((Integer) this.mbs.getAttribute(sbbMonitor, "TotalRequests")).intValue();
        assertEquals(0, map.size());
        String id = exchange.executeIn();
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }
        assertEquals(1, map.size());
        int currentCount = ((Integer) this.mbs.getAttribute(sbbMonitor, "TotalRequests")).intValue();
        assertEquals(1, currentCount - count);
        Iterator it = map.values().iterator();
        ExchangeJournal protocol = (ExchangeJournal) it.next();
        assertTrue(protocol instanceof ProviderInOutJournal);
        exchange.executeOut(id);
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }
        assertEquals(0, map.size());
        currentCount = ((Integer) this.mbs.getAttribute(sbbMonitor, "TotalRequests")).intValue();
        assertEquals(1, currentCount - count);
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
        this.mbs = (MBeanServer) this.ctx.getBean("mbeanServer");
        this.ctx.getBean("org.eclipse.swordfish.core.management.ObjectNameFactory");
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
