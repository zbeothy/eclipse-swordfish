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

import java.io.UnsupportedEncodingException;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class LocalFilePublisher.
 */
public class LocalFilePublisher extends Handler {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(LocalFilePublisher.class);

    /** The handler. */
    private Handler handler;

    /**
     * Instantiates a new local file publisher.
     * 
     * @param pattern
     *        the pattern
     * @param limit
     *        the limit
     * @param count
     *        the count
     */
    public LocalFilePublisher(final String pattern, final int limit, final int count) {
        try {
            this.handler = new FileHandler(pattern, limit, count);
        } catch (Throwable t) {
            String msg =
                    ("Could not create local output for operational log messages.\n"
                            + "Operational log messages will NOT be available locally, but might still be send to remote receivers.\n"
                            + "Parameters:\npattern: " + pattern + "\nlimit: " + limit + "\ncount: " + count);
            if (LOG.isTraceEnabled()) {
                LOG.trace(msg, t);
            } else {
                LOG.warn(msg + "\nCause: " + t.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#close()
     */
    @Override
    public void close() throws SecurityException {
        if (null != this.handler) {
            this.handler.close();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#flush()
     */
    @Override
    public void flush() {
        if (null != this.handler) {
            this.handler.flush();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#getEncoding()
     */
    @Override
    public String getEncoding() {
        if (null != this.handler)
            return this.handler.getEncoding();
        else
            return super.getEncoding();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#getErrorManager()
     */
    @Override
    public ErrorManager getErrorManager() {
        if (null != this.handler)
            return this.handler.getErrorManager();
        else
            return super.getErrorManager();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#getFilter()
     */
    @Override
    public Filter getFilter() {
        if (null != this.handler)
            return this.handler.getFilter();
        else
            return super.getFilter();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#getFormatter()
     */
    @Override
    public Formatter getFormatter() {
        if (null != this.handler)
            return this.handler.getFormatter();
        else
            return super.getFormatter();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#getLevel()
     */
    @Override
    public synchronized Level getLevel() {
        if (null != this.handler)
            return this.handler.getLevel();
        else
            return super.getLevel();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#isLoggable(java.util.logging.LogRecord)
     */
    @Override
    public boolean isLoggable(final LogRecord record) {
        if (null != this.handler)
            return this.handler.isLoggable(record);
        else
            return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    @Override
    public void publish(final LogRecord record) {
        if (null != this.handler) {
            try {
                this.handler.publish(record);
            } catch (Throwable t) {
                String msg = "Could not write operational log message.\n" + "Message: " + record.getMessage();
                if (LOG.isTraceEnabled()) {
                    LOG.trace(msg, t);
                } else {
                    LOG.warn(msg + "\nCause: " + t.getMessage());
                }
            }
        } else if (LOG.isTraceEnabled()) {
            LOG.trace("Could not write operational log message since no local handler is available.\n" + "Message: "
                    + record.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#setEncoding(java.lang.String)
     */
    @Override
    public void setEncoding(final String encoding) throws SecurityException, UnsupportedEncodingException {
        if (null != this.handler) {
            this.handler.setEncoding(encoding);
        } else {
            super.setEncoding(encoding);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#setErrorManager(java.util.logging.ErrorManager)
     */
    @Override
    public void setErrorManager(final ErrorManager em) {
        if (null != this.handler) {
            this.handler.setErrorManager(em);
        } else {
            super.setErrorManager(em);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#setFilter(java.util.logging.Filter)
     */
    @Override
    public void setFilter(final Filter newFilter) throws SecurityException {
        if (null != this.handler) {
            this.handler.setFilter(newFilter);
        } else {
            super.setFilter(newFilter);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#setFormatter(java.util.logging.Formatter)
     */
    @Override
    public void setFormatter(final Formatter newFormatter) throws SecurityException {
        if (null != this.handler) {
            this.handler.setFormatter(newFormatter);
        } else {
            super.setFormatter(newFormatter);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#setLevel(java.util.logging.Level)
     */
    @Override
    public synchronized void setLevel(final Level newLevel) throws SecurityException {
        if (null != this.handler) {
            this.handler.setLevel(newLevel);
        } else {
            super.setLevel(newLevel);
        }
    }

}
