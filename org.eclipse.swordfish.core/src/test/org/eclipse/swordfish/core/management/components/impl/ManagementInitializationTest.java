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
package org.eclipse.swordfish.core.management.components.impl;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBean;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class ManagementInitializationTest.
 */
public class ManagementInitializationTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The controller. */
    private ManagementControllerBean controller;;

    /**
     * Test instantiation.
     */
    public void testInstantiation() {
        assertNotNull(this.controller.getMbeanServer());
        MessagingMonitorBean messagingMonitor =
                (MessagingMonitorBean) this.ctx.getBean("org.eclipse.swordfish.core.management.messaging.MessagingMonitor");
        assertNotNull(messagingMonitor);
        LogManager.getLogManager().reset();
        Logger newLogger = Logger.getLogger("dummy");
        newLogger.setLevel(Level.SEVERE);
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
        this.controller =
                (ManagementControllerBean) this.ctx
                    .getBean("org.eclipse.swordfish.core.management.components.ManagementController");
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
