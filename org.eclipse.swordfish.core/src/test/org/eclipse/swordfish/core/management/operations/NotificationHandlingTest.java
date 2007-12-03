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

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class NotificationHandlingTest.
 */
public class NotificationHandlingTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The internal operations. */
    private org.eclipse.swordfish.core.management.operations.Operations internalOperations;

    /** The external operations. */
    private org.eclipse.swordfish.papi.internal.extension.operations.InternalOperations externalOperations;

    /** The handler. */
    private TestLogHandler handler;

    /** The old filters. */
    private Map oldFilters;

    /**
     * Instantiates a new notification handling test.
     * 
     * @param name
     *        the name
     */
    public NotificationHandlingTest(final String name) {
        super(name);
    }

    /**
     * Test notification filtering.
     */
    public void testNotificationFiltering() {
        /*
         * FIXME m&m Do it later, but don't forgot !!!!!! List logRecords = handler.getRecords();
         * int initCount = logRecords.size(); InternalOperationalMessage msg =
         * InternalOperationalMessage.PASS_BY_MESSAGE; internalOperations.notify(msg, "foobar");
         * assertEquals(initCount + 1, logRecords.size()); msg =
         * InternalOperationalMessage.BLOCK_BY_MESSAGE; internalOperations.notify(msg);
         * assertEquals(initCount + 1, logRecords.size()); msg =
         * InternalOperationalMessage.PASS_BY_MESSAGE; internalOperations.notify(msg);
         * assertEquals(initCount + 2, logRecords.size()); msg =
         * InternalOperationalMessage.PASS_BY_CLASS; internalOperations.notify(msg);
         * assertEquals(initCount + 3, logRecords.size()); ExternalOperationalMessage exMsg =
         * ExternalOperationalMessage.PASS; externalOperations.notify(exMsg, "1138");
         * assertEquals(initCount + 4, logRecords.size()); exMsg =
         * ExternalOperationalMessage.NO_PASS; externalOperations.notify(exMsg);
         * assertEquals(initCount + 4, logRecords.size());
         */
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        InputStream is =
                this.getClass().getClassLoader().getResourceAsStream("org/eclipse/swordfish/core/management/logging.properties");
        Properties filters = new Properties();
        filters.load(is);
        this.ctx =
                new FileSystemXmlApplicationContext(
                        new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
        this.internalOperations =
                (org.eclipse.swordfish.core.management.operations.Operations) this.ctx
                    .getBean("org.eclipse.swordfish.core.management.operations.Operations");
        this.oldFilters =
                ((org.eclipse.swordfish.core.management.operations.impl.OperationsBean) this.internalOperations).getFilters();
        ((org.eclipse.swordfish.core.management.operations.impl.OperationsBean) this.internalOperations).setFilters(filters);
        OperationsProxyFactory factory =
                (OperationsProxyFactory) this.ctx.getBean("org.eclipse.swordfish.papi.adapter.extensions.operations.Operations");
        this.externalOperations =
                (org.eclipse.swordfish.papi.internal.extension.operations.InternalOperations) factory.getInstance(null);
        this.handler = (TestLogHandler) this.ctx.getBean("test.operations.TestHandler");
        Logger logger = Logger.getLogger("operations");
        logger.addHandler(this.handler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        ((org.eclipse.swordfish.core.management.operations.impl.OperationsBean) this.internalOperations)
            .setFilters(this.oldFilters);
        this.ctx.destroy();
        super.tearDown();
    }

}
