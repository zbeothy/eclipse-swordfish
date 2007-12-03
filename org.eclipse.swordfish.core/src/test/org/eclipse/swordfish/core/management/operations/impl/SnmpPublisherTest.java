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
package org.eclipse.swordfish.core.management.operations.impl;

import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.operations.InternalOperationalMessage;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class SnmpPublisherTest.
 */
public class SnmpPublisherTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The publisher. */
    private SnmpPublisherWrapper publisher;

    /**
     * Instantiates a new snmp publisher test.
     * 
     * @param name
     *        the name
     */
    public SnmpPublisherTest(final String name) {
        super(name);
    }

    /**
     * Test out.
     */
    public void testOut() {
        InternalOperationalMessage msg = InternalOperationalMessage.PASS_BY_PACKAGE;
        Object[] params1 = {"foo"};
        OperationalMessageRecord record = new OperationalMessageRecord(null, msg, params1);
        this.publisher.publish(record);
        msg = InternalOperationalMessage.PASS_BY_MESSAGE;
        Object[] params2 = {};
        record = new OperationalMessageRecord(null, msg, params2);
        this.publisher.publish(record);
        this.publisher.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            this.ctx =
                    new FileSystemXmlApplicationContext(
                            new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.publisher = (SnmpPublisherWrapper) this.ctx.getBean("org.eclipse.swordfish.core.management.SnmpPublisher");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.publisher.close();
        this.ctx.destroy();
        super.tearDown();
    }

    // TODO test receiver
}
