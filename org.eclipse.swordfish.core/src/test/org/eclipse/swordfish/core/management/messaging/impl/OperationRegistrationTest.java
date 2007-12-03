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
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.mock.DummyOperation;
import org.eclipse.swordfish.core.management.mock.DummyService;
import org.eclipse.swordfish.core.management.notification.EntityState;
import org.eclipse.swordfish.core.management.notification.OperationStateNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class OperationRegistrationTest.
 */
public class OperationRegistrationTest extends TestCase {

    /** The mbs. */
    private MBeanServer mbs;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /**
     * Instantiates a new operation registration test.
     * 
     * @param name
     *        the name
     */
    public OperationRegistrationTest(final String name) {
        super(name);
    }

    /**
     * Test operation add remove.
     * 
     * @throws MalformedObjectNameException
     */
    public void testOperationAddRemove() throws MalformedObjectNameException {
        OperationMonitorBackendFactoryBean factory =
                (OperationMonitorBackendFactoryBean) this.ctx
                    .getBean("org.eclipse.swordfish.core.management.messaging.OperationMonitorBackendFactory");
        InternalService dummyService = new DummyService("foo");
        InternalOperation dummyOp = new DummyOperation(dummyService, "bar");
        assertEquals(0, factory.getBackends().size());
        String onString = "sbb:type=monitor,name=operationmonitor,operation={http_//sopgroup.org}foo#bar,role=consumer,*";
        ObjectName on = new ObjectName(onString);
        Set beans = this.mbs.queryMBeans(on, null);
        assertEquals(0, beans.size());
        OperationStateNotification notification =
                new OperationStateNotification(EntityState.ADDED, dummyOp, null, ParticipantRole.CONSUMER);
        OperationRegistrationBean monitor =
                (OperationRegistrationBean) this.ctx
                    .getBean("org.eclipse.swordfish.core.management.messaging.OperationRegistration");
        monitor.process(notification);
        assertEquals(1, factory.getBackends().size());
        beans = this.mbs.queryMBeans(on, null);
        assertEquals(1, beans.size());
        notification = new OperationStateNotification(EntityState.REMOVED, dummyOp, null, ParticipantRole.CONSUMER);
        monitor.process(notification);
        assertEquals(0, factory.getBackends().size());
        beans = this.mbs.queryMBeans(on, null);
        assertEquals(0, beans.size());
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
        this.mbs = (MBeanServer) this.ctx.getBean("mbeanServer");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.ctx.destroy();
        super.tearDown();
    }

}
