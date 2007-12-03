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

import junit.framework.TestCase;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.mock.DummyParticipantIdentity;
import org.eclipse.swordfish.core.management.operations.impl.OperationalMessageRecord;
import org.eclipse.swordfish.core.management.operations.impl.OperationalMessageWrapper;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class OperationalMessageTest.
 */
public class OperationalMessageTest extends TestCase {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: OperationalMessageTest.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    // ----------------------------------------------------- Instance Variables

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    // ----------------------------------------------------------- Constructors

    /**
     * Instantiates a new operational message test.
     * 
     * @param name
     *        the name
     */
    public OperationalMessageTest(final String name) {
        super(name);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Test operational message record.
     */
    public void testOperationalMessageRecord() {
        InternalOperationalMessage msg = InternalOperationalMessage.PASS_BY_MESSAGE;
        Object[] params1 = {};
        OperationalMessageRecord record = new OperationalMessageRecord(null, msg, params1);
        String processed = record.getMessage();
        assertEquals("Done", processed);
        Object[] params2 = {"foobar"};
        record = new OperationalMessageRecord(null, InternalOperationalMessage.PASS_BY_PACKAGE, params2);
        processed = record.getMessage();
        assertEquals("Starting component foobar", processed);
        UnifiedParticipantIdentity participantId = new UnifiedParticipantIdentity(new DummyParticipantIdentity());
        record = new OperationalMessageRecord(participantId, InternalOperationalMessage.PASS_BY_PACKAGE, params2);
        processed = record.getMessage();
        assertEquals("Starting component foobar", processed);
        record = new OperationalMessageRecord(null, InternalOperationalMessage.PASS_BY_PACKAGE, params1);
        processed = record.getMessage();
        assertTrue(processed.endsWith("Warning: unexpected number of parameters. Expected: 1 Was: 0"));
        assertTrue(processed.startsWith("Starting component {unknown}"));
        Object[] params3 = {"foo", "bar"};
        record = new OperationalMessageRecord(null, InternalOperationalMessage.PASS_BY_PACKAGE, params3);
        processed = record.getMessage();
        assertTrue(processed.indexOf("Warning: unexpected number of parameters.") != -1);
        assertTrue(processed.startsWith("Starting component foo"));
        assertTrue(processed.endsWith("bar\n"));
        Object[] params4 = {null};
        record = new OperationalMessageRecord(null, InternalOperationalMessage.PASS_BY_PACKAGE, params4);
        processed = record.getMessage();
        assertEquals("Starting component {null}", processed);
    }

    /**
     * Test wrapper.
     */
    public void testWrapper() {
        ExternalOperationalMessage source = ExternalOperationalMessage.NO_PASS;
        OperationalMessageWrapper wrapper = new OperationalMessageWrapper(source);
        assertEquals(Severity.WARN, wrapper.getSeverity());
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.ctx =
                new FileSystemXmlApplicationContext(
                        new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
        this.ctx.getBean("mbeanServer");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        this.ctx.destroy();
        super.tearDown();
    }

}
