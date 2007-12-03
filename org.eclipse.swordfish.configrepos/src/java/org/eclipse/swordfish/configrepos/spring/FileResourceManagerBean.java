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
package org.eclipse.swordfish.configrepos.spring;

import java.io.File;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.FileSequence;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * The Class FileResourceManagerBean.
 * 
 */
public class FileResourceManagerBean extends FileResourceManager implements InitializingBean, DisposableBean {

    /** Default sequence name for this transaction manager. */
    public static final String TRANSACTION_CONFIGSEQUENCE_NAME = "org.eclipse.swordfish.configrepos.proxysequence";

    /** Default initialization vale for this transaction manager. */
    public static final long TRANSACTION_CONFIGSEQUENCE_INITVAL = 1;

    /**
     * Correct path.
     * 
     * @param aPath
     *        to be modified
     * 
     * @return the correted path
     */
    private static String correctPath(final String aPath) {
        String path = aPath;
        if (path.startsWith("file:")) {
            path = StringUtils.substring(path, "file:".length());
        }
        if (path.matches("[\\/]*\\p{Upper}:.*")) {
            path = StringUtils.stripStart(path, "\\/");
        }
        return path;
    }

    /** Grace period the manager will wait before shuting down automatically. */
    private int shutdownTimeout = 0;

    /** The sequence. */
    private FileSequence sequence = null;

    /** The sequencename. */
    private String sequencename = TRANSACTION_CONFIGSEQUENCE_NAME;

    /** The sequenceinitval. */
    private long sequenceinitval = TRANSACTION_CONFIGSEQUENCE_INITVAL;

    /** The initialized. */
    private boolean initialized = false;

    /**
     * Bean wrapper to integrate the file resource manager into spring framework.
     * 
     * @param aStorePath
     *        storage which is being used for in-flight transactional data
     * @param aWorkPath
     *        use to finally write the resulting files
     * @param aDebug
     *        flag to enable special logging
     */
    public FileResourceManagerBean(final String aStorePath, final String aWorkPath, final boolean aDebug) {
        super(correctPath(aStorePath), correctPath(aStorePath) + File.separator + correctPath(aWorkPath), false, new Jdk14Logger(
                Logger.getLogger(FileResourceManagerBean.class.getName())));
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     *         the exception
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        super.start();
        this.sequence = new FileSequence(this.getStoreDir() + File.separator + "seq", this.getLogger());
    }

    /**
     * Clean shutdown hook.
     * 
     * @throws Exception
     *         which has been encountered during shutdown of the file resource manager instance
     */
    public void destroy() throws Exception {
        super.stop(this.shutdownTimeout);
    }

    /**
     * Create transaction identifier.
     * 
     * @return long integer containing the next sequence number
     * 
     * @throws ResourceManagerException
     *         in case the next TXID could not be fetched.
     */
    public long fetchNextTXID() throws ResourceManagerException {
        if (!this.initialized) {
            this.sequence.create(TRANSACTION_CONFIGSEQUENCE_NAME, this.sequenceinitval);
        }

        return this.sequence.nextSequenceValueBottom(TRANSACTION_CONFIGSEQUENCE_NAME, 1L);
    }

    /**
     * Gets the sequenceinitval.
     * 
     * @return the sequenceinitval
     */
    public long getSequenceinitval() {
        return this.sequenceinitval;
    }

    /**
     * Gets the sequencename.
     * 
     * @return the sequencename
     */
    public String getSequencename() {
        return this.sequencename;
    }

    /**
     * Gets the shutdown timeout.
     * 
     * @return Returns the shutdownTimeout.
     */
    public int getShutdownTimeout() {
        return this.shutdownTimeout;
    }

    /**
     * Sets the sequenceinitval.
     * 
     * @param sequenceinitval
     *        the sequenceinitval to set
     */
    public void setSequenceinitval(final long sequenceinitval) {
        this.sequenceinitval = sequenceinitval;
    }

    /**
     * Sets the sequencename.
     * 
     * @param sequencename
     *        the sequencename to set
     */
    public void setSequencename(final String sequencename) {
        this.sequencename = sequencename;
    }

    /**
     * Sets the shutdown timeout.
     * 
     * @param shutdownTimeout
     *        The shutdownTimeout to set.
     */
    public void setShutdownTimeout(final int shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }
}
