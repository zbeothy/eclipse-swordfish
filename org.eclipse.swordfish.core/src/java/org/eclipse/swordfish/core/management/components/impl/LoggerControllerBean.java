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
package org.eclipse.swordfish.core.management.components.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.components.LoggerController;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.operations.impl.OperationsBean;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSecurityException;

/**
 * Instrumentation component to control loggers in the sbb instance. Responsible for instantiating
 * MBeans for individual loggers on request and remotely instantiating/deleting loggers
 * 
 */
public class LoggerControllerBean implements LoggerController, PropertyChangeListener {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(LoggerControllerBean.class);

    /** The Constant INSTRUMENTATION_DESCRIPTION. */
    private final static String INSTRUMENTATION_DESCRIPTION = "LoggerControllerDesc.xml";

    /** The Constant READER_DESCRIPTION. */
    private final static String READER_DESCRIPTION = "LogGroupReaderDesc.xml";

    /** The Constant INSTRUMENTATION_ID. */
    private final static String INSTRUMENTATION_ID = "org.eclipse.swordfish.sbb.LoggerController";

    /** The Constant READER_ID. */
    private final static String READER_ID = "org.eclipse.swordfish.sbb.LogGroupReader";

    /** List of domain parts for this components <code>ObjectName</code>. */
    private ArrayList domainParts;

    /** Name/value pairs for ObjectName properties. */
    private Properties nameProperties;

    /** List of logger names encountered during last <code>rescan()</code>. */
    // private ArrayList loggers;
    private InstrumentationManagerBean instrumentationManager;

    /** The filter null loggers. */
    private Boolean filterNullLoggers = new Boolean(true);

    /**
     * FileHandlers whose parameters can be determined (root or accessible) key: loggerName of
     * logger the handler belongs to value: filename pattern of FileHandler.
     */
    private Map fileHandlers = null; // lazy init so InternalOperations logger

    // already has handlers

    /** Logfile readers currently registered. */
    private Collection fileReaders;

    /**
     * Instantiates a new logger controller bean.
     */
    public LoggerControllerBean() {
        this.domainParts = new ArrayList(1);
        this.domainParts.add("logging");
        this.nameProperties = new Properties();
        this.nameProperties.put("type", "LoggerController");
        this.nameProperties.put("id", String.valueOf(this.hashCode()));
    }

    // ToDo: unused, remove
    /*
     * private void initLogFileManagers(Collection patterns) { this.readers = new
     * HashMap(patterns.size()); for (Iterator iter = patterns.iterator(); iter.hasNext();) { String
     * pattern = (String) iter.next(); LogFileManager manager = new LogFileManager(pattern);
     * Properties props = new Properties(); props.put("type", "LogGroupReader"); pattern =
     * pattern.replaceAll("(:|,|\n)", "_"); props.put("pattern", pattern); props.put("id",
     * String.valueOf(manager.hashCode())); this.readers.put(props, manager.getGroupReader(0)); if
     * (log.isLoggable(Level.FINER)) { log.finer("Added LogFileManager for " + pattern); } } if
     * (null != this.instrumentationManager) { InputStream is =
     * this.getClass().getResourceAsStream(READER_DESCRIPTION); for (Iterator iter =
     * readers.keySet().iterator(); iter.hasNext();) { Properties props = (Properties) iter.next();
     * LogGroupReader reader = (LogGroupReader) readers.get(props); try {
     * this.instrumentationManager.registerInstrumentation(reader, is, domainParts, props,
     * READER_ID); } catch (Exception e) { log.log(Level.SEVERE, "Could not register
     * LogGroupReader.", e); } } } }
     */

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.components.LoggerController#addLogger(java.lang.String,
     *      java.lang.String)
     */
    public String addLogger(final String loggerName, final String levelName) {
        String msg = null;
        Logger logger = Logger.getLogger(loggerName);
        Level level = org.eclipse.swordfish.core.management.operations.impl.Level.getLevel(levelName);
        if (null != level) {
            msg = this.setLoggerLevel(logger, levelName);
        } else {
            msg = "Logger " + loggerName + " created, but unknown level " + levelName + " - set to null";
        }
        return msg;
    }

    /**
     * Destroy.
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        if (null != this.instrumentationManager) {
            synchronized (this.instrumentationManager) {
                try {
                    this.instrumentationManager.unregisterInstrumentation(this);
                } catch (InternalInfrastructureException e) {
                    LOG.error("Could not unregister LoggerController" + " - component might still be visible via Element Manager,"
                            + " but is in undefined state.", e);
                }
                this.unregisterFileReaders();
                this.instrumentationManager = null;
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * Gets accessible <code>FileHandler</code>s.
     * 
     * @return <code>FileHander</code>s where the filename pattern can be determined lazy init
     *         since InternalOperations logger may not have any handlers attached when setters or
     *         init is called
     * 
     * @see OperationsBean
     */
    public Map getFileHandlers() {
        if (null == this.fileHandlers) {
            this.refreshFileHandlers();
        }
        return this.fileHandlers;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.components.LoggerController#getFilterNullLoggers()
     */
    public Boolean getFilterNullLoggers() {
        return this.filterNullLoggers;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the loggers
     * 
     * @see org.eclipse.swordfish.core.management.components.LoggerController#rescan() private void
     *      rescan() { Enumeration loggerNames = LogManager.getLogManager().getLoggerNames();
     *      loggers = new ArrayList(); while (loggerNames.hasMoreElements()) { String loggerName =
     *      (String) loggerNames.nextElement(); loggers.add(loggerName); } }
     */

    public Map getLoggers() {
        Enumeration loggerNames = LogManager.getLogManager().getLoggerNames();
        TreeMap ret = new TreeMap();
        while (loggerNames.hasMoreElements()) {
            String loggerName = (String) loggerNames.nextElement();
            Logger logger = LogManager.getLogManager().getLogger(loggerName);
            Level level = logger.getLevel();
            if (!(this.filterNullLoggers.booleanValue()) || (null != level)) {
                ret.put(loggerName, String.valueOf(level));
            }
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(final PropertyChangeEvent evt) {
        // noop - seems the property change is never invoked
    }

    /**
     * searches for <code>FileHandlers</code> where the pattern attribute can be accessed.
     * 
     * @return <code>Map</code> of <code>FileHandler</code>s where the pattern can be accessed
     */
    public String refreshFileHandlers() {
        String ret = "ok";
        if (null != this.fileHandlers) {
            this.fileHandlers.clear();
        }
        this.fileHandlers = new TreeMap();
        if (null != this.fileReaders) {
            this.unregisterFileReaders();
            this.fileReaders.clear();
        }
        this.fileReaders = new ArrayList();
        Enumeration loggerNames = LogManager.getLogManager().getLoggerNames();
        InputStream is = this.getClass().getResourceAsStream(READER_DESCRIPTION);
        while (loggerNames.hasMoreElements()) {
            String loggerName = (String) loggerNames.nextElement();
            Logger logger = LogManager.getLogManager().getLogger(loggerName);
            Handler[] handlers = logger.getHandlers();
            for (int i = 0; i < handlers.length; i++) {
                if (handlers[i] instanceof FileHandler) {
                    LogGroupReader reader = new LogGroupReader((FileHandler) handlers[i]);
                    this.fileReaders.add(reader);
                    if (null != this.instrumentationManager) {
                        Properties props = new Properties();
                        props.put("type", "LogGroupReader");
                        String onPattern = reader.getPattern().replaceAll("(:|,|\n)", "_");
                        props.put("pattern", onPattern);
                        props.put("id", String.valueOf(reader.hashCode()));
                        try {
                            this.instrumentationManager.registerInstrumentation(reader, is, this.domainParts, props, READER_ID);
                            this.fileHandlers.put(loggerName, this.instrumentationManager.getObjectName(reader));
                        } catch (Exception e) {
                            LOG.error("Could not register LogGroupReader " + reader.getPattern(), e);
                            String pattern = reader.getPattern() + " - unable to register";
                            this.fileHandlers.put(loggerName, pattern);
                            ret = pattern;
                        }
                    } else {
                        String pattern = reader.getPattern() + " - unable to register";
                        this.fileHandlers.put(loggerName, pattern);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.components.LoggerController#setFilterNullLoggers(java.lang.Boolean)
     */
    public void setFilterNullLoggers(final Boolean val) {
        this.filterNullLoggers = val;
    }

    /**
     * Sets the instrumentation manager.
     * 
     * @param im
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManagerBean im) {
        if (null != this.instrumentationManager) {
            synchronized (this.instrumentationManager) {
                try {
                    im.unregisterInstrumentation(this);
                } catch (InternalInfrastructureException e) {
                    LOG.error("Could not unregister LoggerController from " + "pre-exisiting InternalInstrumentationManager.", e);
                }
                this.unregisterFileReaders();
            }
        }
        this.instrumentationManager = im;
        synchronized (this.instrumentationManager) {
            InputStream is = this.getClass().getResourceAsStream(INSTRUMENTATION_DESCRIPTION);
            try {
                im.registerInstrumentation(this, is, this.domainParts, this.nameProperties, INSTRUMENTATION_ID);
            } catch (Exception e) {
                LOG.error("Could not register LoggerController" + " - no online access to logging configuration will be possible.",
                        e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.components.LoggerController#setLoggerLevel(java.lang.String,
     *      java.lang.String)
     */
    public String setLoggerLevel(final String loggerName, final String levelName) {
        String msg = null;
        Logger logger = LogManager.getLogManager().getLogger(loggerName);
        if (null != logger) {
            msg = this.setLoggerLevel(logger, levelName);
        } else {
            msg = "Could not get logger " + loggerName + " - not set, please add logger first!";
        }
        return msg;
    }

    /**
     * Sets the logger level.
     * 
     * @param logger
     *        the logger
     * @param levelName
     *        the level name
     * 
     * @return the string
     * 
     * @throws InternalSecurityException
     */
    private String setLoggerLevel(final Logger logger, final String levelName) throws SecurityException {
        String msg = null;
        Level level = org.eclipse.swordfish.core.management.operations.impl.Level.getLevel(levelName);
        msg = logger.getName() + " set to " + String.valueOf(level);
        if (null != level) {
            logger.setLevel(level);
        } else if ("NULL".equals(levelName.toUpperCase())) {
            logger.setLevel(null);
        } else {
            msg = "Unknown level " + levelName + " - not set";
        }
        return msg;
    }

    /**
     * Unregister file readers.
     */
    private void unregisterFileReaders() {
        if ((null != this.fileReaders) && (null != this.instrumentationManager)) {
            for (Iterator iter = this.fileReaders.iterator(); iter.hasNext();) {
                LogGroupReader reader = (LogGroupReader) iter.next();
                try {
                    this.instrumentationManager.unregisterInstrumentation(reader);
                } catch (InternalInfrastructureException e) {
                    LOG.error("Could not unregister LogGroupReader " + reader.getPattern()
                            + " - component might still be visible via Element Manager," + " but is in undefined state.", e);
                }
            }
        }
    }
}
