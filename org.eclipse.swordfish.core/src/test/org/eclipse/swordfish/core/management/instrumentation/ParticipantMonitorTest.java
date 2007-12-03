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
package org.eclipse.swordfish.core.management.instrumentation;

import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalMonitorable;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalParticipantMonitor;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class ParticipantMonitorTest.
 */
public class ParticipantMonitorTest extends TestCase {

    /** The mbs. */
    private MBeanServer mbs;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The monitor. */
    private InternalParticipantMonitor monitor;

    /**
     * Instantiates a new participant monitor test.
     * 
     * @param name
     *        the name
     */
    public ParticipantMonitorTest(final String name) {
        super(name);
    }

    /**
     * Test monitorable instantiation.
     * 
     * @throws Exception
     */
    public void testMonitorableInstantiation() throws Exception {
        InternalMonitorable test = new TestMonitorable();
        this.monitor.register(test);
        String onName = "sbb/participant:id=" + test.hashCode() + ",*";
        ObjectName on = new ObjectName(onName);
        Set names = this.mbs.queryNames(on, null);
        assertEquals(1, names.size());
        Object[] nameArray = names.toArray();
        ObjectName realName = (ObjectName) nameArray[0];
        Object val = this.mbs.getAttribute(realName, "InternalState");
        assertEquals("RUNNING", val);
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
        ExtensionFactory factory =
                (ExtensionFactory) this.ctx.getBean("org.eclipse.swordfish.papi.extension.instrumentation.ParticipantMonitor");
        this.monitor = (InternalParticipantMonitor) factory.getInstance(null);
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
