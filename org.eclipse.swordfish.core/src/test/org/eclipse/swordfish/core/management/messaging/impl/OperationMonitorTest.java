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

import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.messaging.TestConsumerInOut;
import org.eclipse.swordfish.core.management.messaging.TestProviderInOut;
import org.eclipse.swordfish.core.management.notification.impl.ManagementNotificationListenerBean;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class OperationMonitorTest.
 */
public class OperationMonitorTest extends TestCase {

    /** Delay in milliseconds for processing notifications to be processed. */
    private static final int PROCESSING_DELAY = 250;

    /** The listener. */
    private ManagementNotificationListenerBean listener;

    /** The mbs. */
    private MBeanServer mbs;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /**
     * Instantiates a new operation monitor test.
     * 
     * @param name
     *        the name
     */
    public OperationMonitorTest(final String name) {
        super(name);
    }

    /**
     * Test consumer blocking in out.
     * 
     * @throws Exception
     */
    public void testConsumerBlockingInOut() throws Exception {
        TestConsumerInOut exchange = new TestConsumerInOut(this.listener);

        // check that no monitor for operation is registered yet
        ObjectName onPattern = new ObjectName("sbb:type=monitor,name=operationmonitor,role=consumer,operation=foo#bar,*");
        Set monitors = this.mbs.queryNames(onPattern, null);
        int startSize = monitors.size();
        assertEquals(0, startSize);

        // simulate sending of service request
        String id = exchange.executeBlockingOut();

        // wait until events are processed
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }

        // check that OperationMonitor for new operation is added
        monitors = this.mbs.queryNames(onPattern, null);
        int newSize = monitors.size();
        assertEquals(1, newSize);
        ObjectName myMonitor = (ObjectName) monitors.toArray()[0];

        // check that request was added
        int currentCount = ((Integer) this.mbs.getAttribute(myMonitor, "TotalRequests")).intValue();
        assertEquals(1, currentCount);
        // simulate receiving of service response
        exchange.executeBlockingIn(id);
        // wait until events are processed
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }
        // check that the count of requests was not increased
        currentCount = ((Integer) this.mbs.getAttribute(myMonitor, "TotalRequests")).intValue();
        assertEquals(1, currentCount);
    }

    /**
     * Test provider blocking in out.
     * 
     * @throws Exception
     */
    public void testProviderBlockingInOut() throws Exception {
        TestProviderInOut exchange = new TestProviderInOut(this.listener);

        // check that no monitor for operation is registered yet
        ObjectName onPattern = new ObjectName("sbb:type=monitor,name=operationmonitor,role=provider,operation=foo#bar,*");
        Set monitors = this.mbs.queryNames(onPattern, null);
        int startSize = monitors.size();
        assertEquals(0, startSize);

        // simulate sending of service request
        String id = exchange.executeIn();

        // wait until events are processed
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }

        // check that OperationMonitor for new operation is added
        monitors = this.mbs.queryNames(onPattern, null);
        int newSize = monitors.size();
        assertEquals(1, newSize);
        ObjectName myMonitor = (ObjectName) monitors.toArray()[0];

        // check that request was added
        int currentCount = ((Integer) this.mbs.getAttribute(myMonitor, "TotalRequests")).intValue();
        assertEquals(1, currentCount);
        // simulate receiving of service response
        exchange.executeOut(id);
        // wait until events are processed
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }
        // check that the count of requests was not increased
        currentCount = ((Integer) this.mbs.getAttribute(myMonitor, "TotalRequests")).intValue();
        assertEquals(1, currentCount);
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
