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

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * The Class PlainFormatter.
 */
public class PlainFormatter extends Formatter {

    /** The date format. */
    private DateFormat dateFormat;

    /**
     * Instantiates a new plain formatter.
     */
    public PlainFormatter() {
        super();
        this.dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    }

    /**
     * uses OperationalMessageRecord to create the log string.
     * 
     * @param record
     *        the record
     * 
     * @return the string
     * 
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    @Override
    public String format(final LogRecord record) {
        StringBuffer msg =
                new StringBuffer(record.getLevel().toString()).append(" : ").append(
                        this.dateFormat.format(new Date(record.getMillis()))).append(" : ");
        if (record instanceof OperationalMessageRecord) {
            OperationalMessageRecord sbbRecord = (OperationalMessageRecord) record;
            if (null != sbbRecord.getParticipant()) {
                msg.append(String.valueOf(sbbRecord.getParticipant())).append(": ");
            }
            msg.append(sbbRecord.getFqMsgID()).append("\n     ");
            msg.append(sbbRecord.getMessage()).append("\n");
        } else {
            msg.append(record.getLoggerName()).append("\n");
            msg.append(record.getMessage()).append("\n");
        }
        return new String(msg);
    }

    /**
     * Gets the date format.
     * 
     * @return the date format
     */
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

}
