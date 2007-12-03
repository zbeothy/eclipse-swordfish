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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class LoggerControllerTest.
 */
public class LoggerControllerTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The logger controller. */
    private LoggerControllerBean loggerController;

    /**
     * Instantiates a new logger controller test.
     * 
     * @param name
     *        the name
     */
    public LoggerControllerTest(final String name) {
        super(name);
    }

    /**
     * Test logger registration.
     */
    public void testLoggerRegistration() {
        Map test = this.loggerController.getLoggers();
        assertFalse(test.containsKey("chuy"));
        Logger logger = Logger.getLogger("chuy");
        test = this.loggerController.getLoggers();
        assertFalse(test.containsKey("chuy")); // no level -> filtered
        logger = Logger.getLogger("mev");
        logger.setLevel(Level.INFO);
        test = this.loggerController.getLoggers();
        assertTrue(test.containsKey("mev")); // this one has a level
        this.loggerController.setFilterNullLoggers(new Boolean(false));
        test = this.loggerController.getLoggers();
        assertTrue(test.containsKey("chuy")); // and now we also see loggers
        // without level
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
        this.loggerController = (LoggerControllerBean) this.ctx.getBean("org.eclipse.swordfish.core.management.LoggerController");

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
