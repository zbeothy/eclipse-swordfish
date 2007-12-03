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

import java.util.List;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.messaging.TestConsumerInOut;
import org.eclipse.swordfish.core.management.messaging.TestNotificationProcessor;
import org.eclipse.swordfish.core.management.mock.DummyProcessingNotification;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.impl.ManagementNotificationListenerBean;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class ProcessingNotificationTest.
 */
public class ProcessingNotificationTest extends TestCase {

    /** Delay in milliseconds for processing notifications to be processed. */
    private static final int PROCESSING_DELAY = 250;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The listener. */
    private ManagementNotificationListenerBean listener;

    /** The processor. */
    private TestNotificationProcessor processor;

    /**
     * Instantiates a new processing notification test.
     * 
     * @param name
     *        the name
     */
    public ProcessingNotificationTest(final String name) {
        super(name);
    }

    /**
     * Test dummy consumer blocking in out.
     */
    public void testDummyConsumerBlockingInOut() {
        List processors = this.listener.getNotificationProcessors();
        processors.add(this.processor);
        TestConsumerInOut exchange = new TestConsumerInOut(this.listener);
        String id = exchange.executeBlockingOut();
        exchange.executeBlockingIn(id);
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }
        assertEquals(7, this.processor.getCount());
    }

    /**
     * Test instantiation.
     */
    public void testInstantiation() {
        assertEquals(150, this.listener.getProcessingInterval());
    }

    /**
     * Test processing.
     */
    public void testProcessing() {
        List processors = this.listener.getNotificationProcessors();
        processors.clear();
        processors.add(this.processor);
        for (int i = 0; i < 3; i++) {
            MessageProcessingNotification notification = new DummyProcessingNotification();
            this.listener.sendNotification(notification);
        }
        this.listener.processNotifications();
        assertEquals(3, this.processor.getCount());
    }

    /**
     * Test timed processing.
     */
    public void testTimedProcessing() {
        List processors = this.listener.getNotificationProcessors();
        processors.clear();
        processors.add(this.processor);
        for (int i = 0; i < 3; i++) {
            MessageProcessingNotification notification = new DummyProcessingNotification();
            this.listener.sendNotification(notification);
        }
        try {
            Thread.sleep(PROCESSING_DELAY);
        } catch (InterruptedException e) {
            // no action necessary
        }
        assertEquals(3, this.processor.getCount());
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
        this.listener.setProcessingInterval(PROCESSING_DELAY - 100);
        this.listener.setDirectProcessing(false);
        this.processor = new TestNotificationProcessor();
        this.ctx.getBean("mbeanServer");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.listener = null;
        this.processor = null;
        this.ctx.destroy();
        super.tearDown();
    }
}
