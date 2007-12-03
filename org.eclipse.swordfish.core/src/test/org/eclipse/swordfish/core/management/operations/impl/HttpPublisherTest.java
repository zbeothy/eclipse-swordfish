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

import java.net.MalformedURLException;
import java.util.Vector;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.management.operations.InternalOperationalMessage;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class HttpPublisherTest.
 */
public class HttpPublisherTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The publisher. */
    private NrPublisherBean publisher;

    /** The backend. */
    private HttpPublisherBackendBean backend;

    /**
     * Instantiates a new http publisher test.
     * 
     * @param name
     *        the name
     */
    public HttpPublisherTest(final String name) {
        super(name);
    }

    /**
     * Test block.
     * 
     * @throws MalformedURLException
     */
    public void testBlock() throws MalformedURLException {
        this.publisher.setProcessingInterval(Integer.MAX_VALUE);
        this.backend.setUrl("http://localhost:9999");
        InternalOperationalMessage msg = InternalOperationalMessage.PASS_BY_PACKAGE;
        Object[] params1 = {"foo"};
        OperationalMessageRecord record = new OperationalMessageRecord(null, msg, params1);
        this.publisher.publish(record);
        assertEquals(1, this.publisher.getRecords().size());
        this.publisher.flush();
        msg = InternalOperationalMessage.PASS_BY_MESSAGE;
        Object[] params2 = {};
        OperationalMessageRecord record2 = new OperationalMessageRecord(null, msg, params2);
        this.publisher.publish(record2);
        assertTrue(this.publisher.getRecords().contains(record));
        assertTrue(this.publisher.getRecords().contains(record2));
    }

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

    /**
     * Test reconnect.
     */
    public void testReconnect() {
        this.publisher.setProcessingInterval(Integer.MAX_VALUE);
        String oldUrl = this.backend.getUrl();
        this.backend.setUrl("http://localhost:9999");
        InternalOperationalMessage msg = InternalOperationalMessage.PASS_BY_PACKAGE;
        Object[] params1 = {"foo"};
        OperationalMessageRecord record = new OperationalMessageRecord(null, msg, params1);
        this.publisher.publish(record);
        assertEquals(1, this.publisher.getRecords().size());
        this.publisher.flush();
        msg = InternalOperationalMessage.PASS_BY_MESSAGE;
        Object[] params2 = {};
        OperationalMessageRecord record2 = new OperationalMessageRecord(null, msg, params2);
        this.publisher.publish(record2);
        assertTrue(this.publisher.getRecords().contains(record));
        assertTrue(this.publisher.getRecords().contains(record2));
        this.backend.setUrl(oldUrl);
        this.publisher.flush();
        assertFalse(this.publisher.getRecords().contains(record));
        assertFalse(this.publisher.getRecords().contains(record2));
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
        this.publisher = (NrPublisherBean) this.ctx.getBean("org.eclipse.swordfish.core.mangement.HttpPublisher");
        this.backend = (HttpPublisherBackendBean) this.ctx.getBean("org.eclipse.swordfish.core.management.HttpPublisherBackend");
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
