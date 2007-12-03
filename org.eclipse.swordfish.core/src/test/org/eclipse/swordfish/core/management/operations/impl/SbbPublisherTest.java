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

import java.util.Vector;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.operations.InternalOperationalMessage;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class SbbPublisherTest.
 */
public class SbbPublisherTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The publisher. */
    private NrPublisherBean publisher;

    /** The backend. */
    private SbbPublisherBackendBean backend;

    /**
     * Instantiates a new sbb publisher test.
     * 
     * @param name
     *        the name
     */
    public SbbPublisherTest(final String name) {
        super(name);
    }

    /*
     * This test currently does not work without instantiated InternalSBB ToDo: provide dummy proxy
     * implementation ToDo: make test runnable in integration-superbc
     */
    /**
     * Test out.
     */
    public void testOut() {
        this.publisher.setProcessingInterval(Integer.MAX_VALUE);
        InternalOperationalMessage msg = InternalOperationalMessage.PASS_BY_PACKAGE;
        Object[] params1 = {"foo"};
        OperationalMessageRecord record = new OperationalMessageRecord(null, msg, params1);
        this.publisher.publish(record);
        msg = InternalOperationalMessage.PASS_BY_MESSAGE;
        Object[] params2 = {};
        record = new OperationalMessageRecord(null, msg, params2);
        this.publisher.publish(record);
        assertEquals(2, this.publisher.getRecords().size());
        this.publisher.flush();
        assertEquals(0, this.publisher.getRecords().size());
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
        this.publisher = (NrPublisherBean) this.ctx.getBean("org.eclipse.swordfish.mangement.HttpPublisher");
        this.backend = (SbbPublisherBackendBean) this.ctx.getBean("org.eclipse.swordfish.core.management.SbbPublisherBackend");
        Vector backends = new Vector(1);
        backends.add(this.backend);
        this.publisher.setBackends(backends);
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

    // TODO: timing tests

}
