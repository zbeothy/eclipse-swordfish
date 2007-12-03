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
package org.eclipse.swordfish.core.logging;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * The Class CompactFormatter.
 */
public class CompactFormatter extends Formatter {

    /** message format. */
    private static final MessageFormat MESSAGE_FORMAT = new MessageFormat("[{1}|{2}|{3,date,hh:mm:ss}] {0}: {4} \n");

    /**
     * constructor.
     */
    public CompactFormatter() {
        super();
    }

    /**
     * Format.
     * 
     * @param record
     *        record
     * 
     * @return String string
     */
    @Override
    public String format(final LogRecord record) {
        Object[] arguments = new Object[6];
        arguments[0] = record.getLoggerName();
        arguments[1] = record.getLevel();
        arguments[2] = Thread.currentThread().getName();
        arguments[3] = new Date(record.getMillis());
        arguments[4] = record.getMessage();
        return MESSAGE_FORMAT.format(arguments);
    }

}
