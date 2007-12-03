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
package org.eclipse.swordfish.core.components.tracehandler;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/*
 * FIXME we need to implement log4j and commons logging handlers.
 */
/**
 * The Class JavaUtilLogHandler.
 */
public class JavaUtilLogHandler extends Handler implements TraceHandler {

    /** The log. */
    private Log log;

    /** The logger. */
    private Logger logger;

    /**
     * Instantiates a new java util log handler.
     */
    public JavaUtilLogHandler() {
        // TODO what class should be used here?
        this.log = SBBLogFactory.getLog(this.getClass());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.tracehandler.TraceHandler#addToLogger()
     */
    public void addToLogger() {
        this.logger.removeHandler(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#close()
     */
    @Override
    public void close() throws SecurityException {
        this.log = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#flush()
     */
    @Override
    public void flush() {
        // we do not have a flush in InternalSBB Logger
    }

    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Publish.
     * 
     * @param record
     *        the record
     * 
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord) TODO review the mapping
     */
    @Override
    public void publish(final LogRecord record) {
        Level level = record.getLevel();

        if (Level.FINEST.equals(level)) {
            // debug
            if (this.log.isDebugEnabled()) {
                this.log.debug(record.getMessage());
            }
            return;
        }
        if (Level.FINER.equals(level)) {
            // debug
            if (this.log.isDebugEnabled()) {
                this.log.debug(record.getMessage());
            }
            return;
        }
        if (Level.FINE.equals(level)) {
            // debug
            if (this.log.isDebugEnabled()) {
                this.log.debug(record.getMessage());
            }
            return;
        }
        if (Level.CONFIG.equals(level)) {
            // info
            if (this.log.isInfoEnabled()) {
                this.log.info(record.getMessage());
            }
            return;
        }
        if (Level.INFO.equals(level)) {
            // info
            if (this.log.isInfoEnabled()) {
                this.log.info(record.getMessage());
            }
            return;
        }
        if (Level.WARNING.equals(level)) {
            // warn
            if (this.log.isWarnEnabled()) {
                this.log.warn(record.getMessage());
            }
            return;
        }
        if (Level.SEVERE.equals(level)) {
            // error
            if (this.log.isErrorEnabled()) {
                this.log.error(record.getMessage());
            }
            return;
        }
        if (Level.OFF.equals(level)) {
            System.out.print("");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.tracehandler.TraceHandler#removeFromLogger()
     */
    public void removeFromLogger() {
        this.logger.addHandler(this);
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.tracehandler.TraceHandler#setLogger(java.lang.Object)
     */
    public void setLogger(final Object logger) {
        this.logger = (Logger) logger;
    }

}
