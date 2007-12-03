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
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.eclipse.swordfish.core.management.operations.impl.OperationalMessageRecord;

/**
 * Handler that allows access to logged LogRecords for testing purposes.
 * 
 */
public class TestLogHandler extends Handler {

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(TestLogHandler.class.getName());

    /** The records. */
    private Vector records = new Vector();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.logging.Handler#close()
     */
    @Override
    public void close() throws SecurityException {
        // no-op
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.logging.Handler#flush()
     */
    @Override
    public void flush() {
        // no-op
    }

    /**
     * Gets the records.
     * 
     * @return the records
     */
    public List getRecords() {
        return this.records;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    @Override
    public void publish(final LogRecord record) {
        if (record instanceof OperationalMessageRecord) {
            OperationalMessageRecord opRecord = (OperationalMessageRecord) record;
            log.info("Got record : " + opRecord.getFqMsgID() + " : " + record.getMessage());
            this.records.add(record);
        }
    }

}
