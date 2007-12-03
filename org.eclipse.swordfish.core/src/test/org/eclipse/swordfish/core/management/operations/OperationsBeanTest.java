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
package org.eclipse.swordfish.core.management.operations;

import java.util.List;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.operations.impl.OperationsBean;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class OperationsBeanTest.
 */
public class OperationsBeanTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The operations bean. */
    private OperationsBean operationsBean;

    /** The handler. */
    private TestLogHandler handler;

    /**
     * Instantiates a new operations bean test.
     * 
     * @param name
     *        the name
     */
    public OperationsBeanTest(final String name) {
        super(name);
    }

    /**
     * tests that one call to InternalOperations.notify creates a log record
     */
    public void testLogging() {
        List logRecords = this.handler.getRecords();
        int initCount = logRecords.size();
        InternalOperationalMessage msg = new InternalOperationalMessage(1);
        this.operationsBean.notify(msg);
        assertEquals(initCount + 1, logRecords.size());
        this.operationsBean.notify(msg, new String("testing"));
        assertEquals(initCount + 2, logRecords.size());
        this.operationsBean.notify(msg, new String("testing"), new Integer(5));
        assertEquals(initCount + 3, logRecords.size());
        this.operationsBean.notify(msg, new String("testing"), new Integer(5), new Boolean(true));
        assertEquals(initCount + 4, logRecords.size());
        this.operationsBean.notify(msg, new String[2]);
        assertEquals(initCount + 5, logRecords.size());
    }

    /**
     * Test message instantiation.
     */
    public void testMessageInstantiation() {
        InternalOperationalMessage msg = new InternalOperationalMessage(1);
        assertEquals(Severity.FATAL, msg.getSeverity());
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
        this.operationsBean = (OperationsBean) this.ctx.getBean("org.eclipse.swordfish.core.management.operations.Operations");
        String loggerName = "operations";
        this.handler = (TestLogHandler) this.ctx.getBean("test.operations.TestHandler");
        Logger logger = Logger.getLogger(loggerName);
        logger.addHandler(this.handler);
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
