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
package org.eclipse.swordfish.configrepos.resource.sources;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.transaction.file.ResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;

/**
 * The Class TransactionalInputStream.
 * 
 */
public class TransactionalInputStream extends FilterInputStream {

    /** The logger. */
    private Logger logger = Logger.getLogger(TransactionalInputStream.class.getName());

    /** The tx id. */
    private String txId = null;

    /** The manager. */
    private ResourceManager manager = null;

    /**
     * The Constructor.
     * 
     * @param aManager
     *        which provided the stream
     * @param aTxId
     *        the transaction id
     * @param aStream
     *        the stream itself
     */
    public TransactionalInputStream(final ResourceManager aManager, final String aTxId, final InputStream aStream) {
        super(aStream);
        this.manager = aManager;
        this.txId = aTxId;
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.finest("Transactional input stream created with resource manager '" + aManager + "' and transaction '"
                    + aTxId + "'.");
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @throws IOException
     *         in case the close operation could not be executed
     * 
     * @see java.io.ByteArrayInputStream#close()
     */
    @Override
    public void close() throws IOException {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(TransactionalInputStream.class.getName(), "close");
        }
        boolean failed = true;
        try {
            super.close();
            this.manager.commitTransaction(this.txId);
            failed = false;
        } catch (ResourceManagerException e) {
            throw new IOException("Error while rolling back transactional input stream in resource manager '" + this.manager
                    + "' for transaction '" + this.txId + "'");
        } finally {
            if (this.logger.isLoggable(Level.FINEST)) {
                if (failed) {
                    this.logger.finest("Failed closing transactional input stream in resource manager '" + this.manager
                            + "' for transaction '" + this.txId + "'.");
                } else {
                    this.logger.finest("Closed transactional input stream in resource manager '" + this.manager
                            + "' for transaction '" + this.txId + "'.");
                }
                this.logger.exiting(TransactionalInputStream.class.getName(), "close");
            }
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @throws IOException
     *         in case the rollback operation could not be executed
     * 
     * @see java.io.ByteArrayInputStream#close()
     */
    public void rollback() throws IOException {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(TransactionalInputStream.class.getName(), "rollback");
        }
        boolean failed = true;
        try {
            super.close();
            this.manager.rollbackTransaction(this.txId);
            failed = false;
        } catch (ResourceManagerException e) {
            throw new IOException("Error while rolling back transactional input stream");
        } finally {
            if (this.logger.isLoggable(Level.FINEST)) {
                if (failed) {
                    this.logger.finest("Failed rolling back transactional input stream in resource manager '" + this.manager
                            + "' for transaction '" + this.txId + "'.");
                } else {
                    this.logger.finest("Rolled back transactional input stream in resource manager '" + this.manager
                            + "' for transaction '" + this.txId + "'.");
                }
                this.logger.exiting(TransactionalInputStream.class.getName(), "rollback");
            }
        }
    }

}
