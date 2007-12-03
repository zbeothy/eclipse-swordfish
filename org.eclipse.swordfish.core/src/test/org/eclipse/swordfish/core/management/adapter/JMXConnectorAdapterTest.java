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
package org.eclipse.swordfish.core.management.adapter;

import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.adapter.impl.JMXConnectorAdapter;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class JMXConnectorAdapterTest.
 */
public class JMXConnectorAdapterTest extends TestCase {

    /** The adapter. */
    JMXConnectorAdapter adapter;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /**
     * Instantiates a new JMX connector adapter test.
     * 
     * @param name
     *        the name
     */
    public JMXConnectorAdapterTest(final String name) {
        super(name);
    }

    /**
     * Test RMI registry.
     */
    public void testRMIRegistry() {
        assertTrue(State.RUNNING.equals(this.adapter.getState()));
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
        this.ctx.getBean("mbeanServer");
        this.adapter = (JMXConnectorAdapter) this.ctx.getBean("org.eclipse.swordfish.core.management.adapter.JMXConnectorAdapter");
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
