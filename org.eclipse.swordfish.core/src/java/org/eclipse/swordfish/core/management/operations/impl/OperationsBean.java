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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.iapi.impl.KernelBean;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.operations.OperationalMessage;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.core.management.operations.Severity;
import org.eclipse.swordfish.core.utils.BeanInspector;
import org.eclipse.swordfish.papi.internal.exception.InternalSecurityException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * The actual implementation of <code>InternalOperations</code> Takes care of setting up the
 * logging infrastructure for operational logging and piping <code>InternalOperationalMessage</code>s
 * into the loggers.
 * 
 */
public class OperationsBean implements Operations, BeanFactoryAware, PropertyChangeListener {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(OperationsBean.class);

    /** Root logger for operational logging. */
    private static final Logger OPERATIONAL_LOGGER = Logger.getLogger("operations");

    /** The Constant DUMMY. */
    private static final Object[] DUMMY = {};

    /** Specification for current filter settings. */
    private Map currentFilters;

    /**
     * Those handlers that were registered by me with OPERATIONAL_LOGGER and that need to be removed
     * by me.
     */
    private Handler[] myHandlers = new Handler[0];

    /** The bean factory. */
    private BeanFactory beanFactory;

    /** The handler names. */
    private List handlerNames = new Vector();

    /** The participant. */
    private UnifiedParticipantIdentity participant;

    /**
     * Destroy.
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        this.removeMyHandlers();
        this.myHandlers = new Handler[0];
        if (null != this.handlerNames) {
            this.handlerNames.clear();
            this.handlerNames = null;
        }
        if (null != this.currentFilters) {
            this.clearCurrentFilters();
            this.currentFilters.clear();
            this.currentFilters = null;
        }
        this.beanFactory = null;
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * Gets the filters.
     * 
     * @return <code>Map</code> specifying the current filters for operational logging
     * 
     * @see setFilters(Map)
     */
    public Map getFilters() {
        return this.currentFilters;
    }

    public Handler[] getMyHandlers() {
        return this.myHandlers;
    }

    /**
     * Init.
     */
    public void init() {
        if (null == OPERATIONAL_LOGGER.getLevel()) {
            OPERATIONAL_LOGGER.setLevel(Level.ALL);
        }
        this.initHandlers();
    }

    /**
     * Logs one record to the appropriate logger.
     * 
     * @param msg
     *        the msg
     * @param params
     *        the params
     */
    public void logRecord(final OperationalMessage msg, final Object[] params) {
        OperationalMessageRecord record = new OperationalMessageRecord(this.participant, msg, params);
        String loggerName = "operations." + record.getOperationalMessage().getQualifiedName();
        Logger theLogger = Logger.getLogger(loggerName);
        theLogger.log(record);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Received operational log message " + record.getMessage() + "\n" + BeanInspector.beanToString(msg)
                    + "\nTo logger " + loggerName);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("Received operational log message " + msg.getClass().getName() + "." + msg.getMsgID() + ":\n"
                    + record.getMessage());
        }
    }

    /**
     * Notify.
     * 
     * @param args
     *        the args
     */
    public void notify(final Object[] args) {
        System.out.println("Received args:");
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param msg
     *        the msg
     * 
     * @see org.eclipse.swordfish.core.management.operations.Operations#notify(org.eclipse.swordfish.core.management.operations.OperationalMessage)
     */
    public void notify(final OperationalMessage msg) {
        Object[] params = {};
        this.logRecord(msg, params);
    }

    /**
     * (non-Javadoc).
     * 
     * @param msg
     *        the msg
     * @param param1
     *        the param1
     * 
     * @see org.eclipse.swordfish.core.management.operations.Operations#notify(org.eclipse.swordfish.core.management.operations.OperationalMessage,
     *      java.lang.Object)
     */
    public void notify(final OperationalMessage msg, final Object param1) {
        Object[] params = {param1};
        this.logRecord(msg, params);
    }

    /**
     * (non-Javadoc).
     * 
     * @param msg
     *        the msg
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * 
     * @see org.eclipse.swordfish.core.management.operations.Operations#notify(org.eclipse.swordfish.core.management.operations.OperationalMessage,
     *      java.lang.Object, java.lang.Object)
     */
    public void notify(final OperationalMessage msg, final Object param1, final Object param2) {
        Object[] params = {param1, param2};
        this.logRecord(msg, params);
    }

    /**
     * (non-Javadoc).
     * 
     * @param msg
     *        the msg
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * 
     * @see org.eclipse.swordfish.core.management.operations.Operations#notify(org.eclipse.swordfish.core.management.operations.OperationalMessage,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public void notify(final OperationalMessage msg, final Object param1, final Object param2, final Object param3) {
        Object[] params = {param1, param2, param3};
        this.logRecord(msg, params);
    }

    /**
     * (non-Javadoc).
     * 
     * @param msg
     *        the msg
     * @param params
     *        the params
     * 
     * @see org.eclipse.swordfish.core.management.operations.Operations#notify(org.eclipse.swordfish.core.management.operations.OperationalMessage,
     *      java.lang.Object[])
     */
    public void notify(final OperationalMessage msg, final Object[] params) {
        Object[] theParams = params;
        if (null == params) {
            theParams = DUMMY;
        }
        this.logRecord(msg, theParams);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(final PropertyChangeEvent evt) {
        if ("participant".equals(evt.getPropertyName())) {
            this.participant = (UnifiedParticipantIdentity) evt.getNewValue();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(final BeanFactory arg0) throws BeansException {
        this.beanFactory = arg0;
    }

    /**
     * Sets the filter levels for operational logging <br/>The filter levels are specified by a map
     * with the following characteristics <br/>Key: Beginning of qualified classname of
     * InternalOperationalMessage to be filtered + "." + MessageID <br/>Value: The minimum severity
     * a notification has to have to be accepted. Allowed values are specified in <br/>The mechanism
     * uses <code>java.util.logging.Logger</code>s, so more specific filter settings override
     * less specific ones.
     * 
     * @param filters
     *        see above
     */
    public void setFilters(final Map filters) {
        // clear previously set filter levels
        this.clearCurrentFilters();
        // set filter levels
        this.currentFilters = filters;
        for (Iterator iter = filters.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            String logname = "operations." + key;
            Logger aLogger = Logger.getLogger(logname);
            String severityName = (String) filters.get(key);
            Severity severity = Severity.getByName(severityName);
            if (null != severity) {
                Level level = org.eclipse.swordfish.core.management.operations.impl.Level.getLevel(severity);
                aLogger.setLevel(level);
            } else {
                LOG.warn("Ignoring unknown filter level " + severityName + " for " + logname);
            }
        }
    }

    /**
     * Sets the handlers.
     * 
     * @param handlers
     *        to be used by logger
     */
    public void setHandlers(final List handlers) {
        this.handlerNames = handlers;
        // check necessary since beanFactory might not be set by Spring yet
        if (null != this.beanFactory) {
            this.initHandlers();
        }
    }

    /**
     * Sets the kernel.
     * 
     * @param kernel
     *        the new kernel
     */
    public void setKernel(final KernelBean kernel) {
        this.participant = kernel.getParticipant();
        kernel.addPropertyChangeListener("participant", this);
    }

    public void setMyHandlers(final Handler[] myHandlers) {
        this.myHandlers = myHandlers;
    }

    /**
     * Clear current filters.
     * 
     * @throws InternalSecurityException
     */
    private void clearCurrentFilters() throws SecurityException {
        if (null != this.currentFilters) {
            for (Iterator iter = this.currentFilters.keySet().iterator(); iter.hasNext();) {
                String key = "operations." + iter.next();
                Logger aLogger = Logger.getLogger(key);
                aLogger.setLevel(null);
            }
        }
    }

    /**
     * Inits the handlers.
     */
    private synchronized void initHandlers() {
        this.removeMyHandlers();
        List presentHandlers = Arrays.asList(OPERATIONAL_LOGGER.getHandlers());
        ArrayList added = new ArrayList(this.handlerNames.size());
        for (Iterator iter = this.handlerNames.iterator(); iter.hasNext();) {
            String handlerName = (String) iter.next();
            Handler handler = (Handler) this.beanFactory.getBean(handlerName);
            if (!presentHandlers.contains(handler)) {
                OPERATIONAL_LOGGER.addHandler(handler);
                added.add(handler);
            }
        }
        this.myHandlers = new Handler[added.size()];
        int i = 0;
        for (Iterator iter = added.iterator(); iter.hasNext();) {
            Handler handler = (Handler) iter.next();
            this.myHandlers[i++] = handler;
        }
    }

    /**
     * remove all handlers that were previously registered by me.
     */
    private synchronized void removeMyHandlers() {
        for (int i = 0; i < this.myHandlers.length; i++) {
            Handler handler = this.myHandlers[i];
            OPERATIONAL_LOGGER.removeHandler(handler);
        }
    }

}
