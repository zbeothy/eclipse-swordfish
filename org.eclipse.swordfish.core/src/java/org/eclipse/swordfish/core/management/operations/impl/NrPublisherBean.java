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

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.messages.ManagementMessage;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;
import org.eclipse.swordfish.core.utils.BeanInspector;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSecurityException;

/**
 * Publisher that sends out operational log messages to the NotificationReceiver TSP Messages are
 * batched on a time/volume basis The publisher uses configurable backends (currently InternalSBB
 * and HTTP POST) to actually transmit the messages.
 * 
 */
public class NrPublisherBean extends Handler {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(NrPublisherBean.class);

    /** The Constant INSTRUMENTATION_DESCRIPTION. */
    private final static String INSTRUMENTATION_DESCRIPTION = "NrPublisherDesc.xml";

    /** The Constant INSTRUMENTATION_ID. */
    private final static String INSTRUMENTATION_ID = "org.eclipse.swordfish.sbb.NrPublisher";

    /** The Constant DEFAULT_PROCESSING_INTERVAL. */
    private static final long DEFAULT_PROCESSING_INTERVAL = 600000;

    /** The Constant DEFAULT_MESSAGE_THRESHOLD. */
    private static final int DEFAULT_MESSAGE_THRESHOLD = 500;

    /** The Constant DEFAULT_MAX_MESSAGES. */
    private static final int DEFAULT_MAX_MESSAGES = 1000;

    /** The activate. */
    private boolean activate = true;

    /** The instrumentation manager. */
    private InstrumentationManagerBean instrumentationManager;

    /** The instrumentation. */
    private Instrumentation instrumentation;

    /** The operations. */
    private Operations operations;

    /** The state. */
    private State state = State.STOPPED;

    /** The backends. */
    private List backends;

    /** Number of consecutive failures to connect to server. */
    private int failCount = 0;

    /** buffer for records to send out. */
    private List records;

    /** Timer for sending out notifications in regular intervals. */
    private Timer theTimer;

    /** The timer task. */
    private PublisherTimerTask theTimerTask;

    /** Interval in milliseconds to collect notifications before they are send. */
    private long processingInterval = DEFAULT_PROCESSING_INTERVAL;

    /** Maximum number of log messages to collect before sending them out. */
    private int messageThreshold = DEFAULT_MESSAGE_THRESHOLD;

    /**
     * Maximum number of messages to queue If connection to server fails, superfluous messages will
     * be truncated.
     */
    private int maxMessages = DEFAULT_MAX_MESSAGES;

    /**
     * Constructor.
     */
    public NrPublisherBean() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting NrPublisher()");
        }
        this.state = State.STARTING;
        this.backends = new Vector();
        this.records = new Vector();
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed NrPublisher()");
        }
    }

    /**
     * This method is necessary to implement java.util.logging.Handler Internally the shutdown of
     * this method is handled by destroy to maintain consistency with other classes.
     * 
     * @see java.util.logging.Handler#close()
     */
    @Override
    public void close() {
        this.destroy();
    }

    /**
     * Destroy.
     * 
     * @throws InternalSecurityException
     */
    public void destroy() throws SecurityException {
        if (!this.activate) return;
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting close");
        }
        if (null != this.theTimerTask) {
            this.theTimerTask.cancel();
            this.theTimerTask = null;
        }
        this.changeState(State.STOPPING);
        this.flush();
        this.unregisterInstrumentation();
        // set state directly, don't want to set timers and stuff
        this.state = State.STOPPED;
        this.instrumentationManager = null;
        this.records = null;
        this.theTimer = null;
        this.operations = null;
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed close");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#flush()
     */
    @Override
    public synchronized void flush() {
        if (!this.activate) return;
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting flush");
        }
        try {
            if (this.records.size() > 0) {
                List oldRecords = this.records;
                int newSize = oldRecords.size() * 11 / 10;
                this.records = new Vector(newSize);
                this.sendNotification(oldRecords);
            }
        } catch (Throwable e) {
            LOG.warn(NrPublisherBean.class.getName(), "flush", e);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed flush");
        }
    }

    /**
     * Gets the activate.
     * 
     * @return the activate
     */
    public Boolean getActivate() {
        return new Boolean(this.activate);
    }

    /**
     * Gets the max messages.
     * 
     * @return the max messages
     */
    public int getMaxMessages() {
        return this.maxMessages;
    }

    /**
     * Gets the message threshold.
     * 
     * @return the message threshold
     */
    public int getMessageThreshold() {
        return this.messageThreshold;
    }

    /**
     * Gets the processing interval.
     * 
     * @return the processing interval
     */
    public long getProcessingInterval() {
        return this.processingInterval;
    }

    public List getRecords() {
        return this.records;
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public State getState() {
        return this.state;
    }

    /**
     * Gets the state string.
     * 
     * @return the state string
     */
    public String getStateString() {
        if (null != this.state) return this.state.toString();
        return null;
    }

    /**
     * Init.
     */
    public void init() {
        this.scheduleTimer(this.processingInterval);
        this.state = State.RUNNING;
        if (!this.activate) {
            LOG.info("Not activating NrPublisher as per configuration");
            return;
        } else {
            LOG.info("Activated NrPublisher - " + this.backends.size() + " backends configured.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    @Override
    public void publish(final LogRecord record) {
        if (!this.activate) return;
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting publish");
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Received log record\n" + BeanInspector.beanToString(record));
        }
        if (record instanceof OperationalMessageRecord) {
            OperationalMessageRecord opRecord = (OperationalMessageRecord) record;
            this.records.add(opRecord);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Queued record for msg " + opRecord.getMessage() + ":\n"
                        + BeanInspector.beanToString(opRecord.getOperationalMessage()));
            } else if (LOG.isDebugEnabled()) {
                LOG.debug("Queued record for message " + opRecord.getOperationalMessage().getQualifiedName()
                        + opRecord.getMessage());
            }
            if (this.records.size() > this.messageThreshold) {
                if (0 == this.failCount) {
                    // no failure encountered on last send
                    this.flush();
                } else {
                    // failure encountered in last send, wait for next
                    // processing interval
                    // and make sure that queue does not get too large
                    if (this.records.size() > this.maxMessages) {
                        this.records = this.records.subList(0, this.maxMessages / 2);
                    }
                }
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed publish");
        }
    }

    /**
     * Sets the activate.
     * 
     * @param activate
     *        the new activate
     */
    public void setActivate(final Boolean activate) {
        this.activate = activate.booleanValue();
    }

    /**
     * Sets the backends.
     * 
     * @param backends
     *        the new backends
     */
    public void setBackends(final List backends) {
        this.backends = backends;
    }

    /**
     * Sets the instrumentation manager.
     * 
     * @param mgr
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManagerBean mgr) {
        this.unregisterInstrumentation();
        this.instrumentationManager = mgr;
        this.instrumentation = new Instrumentation(this);
        InputStream is = this.getClass().getResourceAsStream(INSTRUMENTATION_DESCRIPTION);
        try {
            mgr.registerInstrumentation(this.instrumentation, is, INSTRUMENTATION_ID);
        } catch (Exception e) {
            LOG.error("Could not register NrPublisher - operational logging will not be controlable via Element Manager. Reason:\n"
                    + e);
        }
    }

    /**
     * Sets the management timer.
     * 
     * @param timer
     *        the new management timer
     */
    public void setManagementTimer(final Timer timer) {
        this.theTimer = timer;
    }

    /**
     * Sets the max messages.
     * 
     * @param maxMessages
     *        the new max messages
     */
    public void setMaxMessages(final int maxMessages) {
        this.maxMessages = maxMessages;
    }

    /**
     * Sets the message threshold.
     * 
     * @param messageThreshold
     *        the new message threshold
     */
    public void setMessageThreshold(final int messageThreshold) {
        this.messageThreshold = messageThreshold;
    }

    /**
     * Sets the operations.
     * 
     * @param operations
     *        the new operations
     */
    public void setOperations(final Operations operations) {
        this.operations = operations;
    }

    /**
     * Sets the processing interval.
     * 
     * @param processingInterval
     *        the new processing interval
     */
    public void setProcessingInterval(final long processingInterval) {
        this.processingInterval = processingInterval;
        if (State.RUNNING.equals(this.state)) {
            this.scheduleTimer(processingInterval);
        }
    }

    public void setRecords(final List records) {
        this.records = records;
    }

    /**
     * Handles changes from and to failed state effective processing interval is adjusted if failed
     * state persists.
     * 
     * @param newState
     *        the new state
     */
    private void changeState(final State newState) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting changeState from " + String.valueOf(this.state) + " to " + String.valueOf(newState));
        }
        if (State.FAILED.equals(this.state)) {
            long timeout = this.processingInterval;
            if (!(State.FAILED.equals(newState))) {
                // operation resumed -> return everything to normal
                this.failCount = 0;
                if (LOG.isTraceEnabled()) {
                    LOG.trace("changeState - resume normal operation");
                }
            } else {
                // still not operational -> increase timeout
                this.failCount++;
                timeout = Math.max(this.processingInterval * this.failCount, Integer.MAX_VALUE);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("changeState - continuing failed state, timeout now " + timeout);
                }
            }
            this.scheduleTimer(timeout);
        } else if (State.FAILED.equals(newState)) {
            this.failCount = 1;
        }
        if ((null != this.operations) && !(newState.equals(this.state))) {
            this.operations.notify(ManagementMessage.COMPONENT_STATE_CHANGED, "NrPublisher", this.state, newState.toString());
        }
        this.state = newState;
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed changeState, now " + String.valueOf(this.state));
        }
    }

    /**
     * Schedule timer.
     * 
     * @param iProcessingInterval
     *        the processing interval
     */
    private void scheduleTimer(final long iProcessingInterval) {
        if (null != this.theTimerTask) {
            this.theTimerTask.cancel();
        }
        this.theTimerTask = new PublisherTimerTask(this);
        this.theTimer.schedule(this.theTimerTask, iProcessingInterval, iProcessingInterval);
    }

    /**
     * Send out the list of old notifications.
     * 
     * @param oldRecords
     *        <code>java.util.List</code> containing the <code>OperationalMessageRecord</code>s
     *        to be send out
     */
    private void sendNotification(final List oldRecords) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting sendNotification");
        }
        boolean published = false;
        for (Iterator iter = this.backends.iterator(); iter.hasNext();) {
            NrPublisherBackend backend = (NrPublisherBackend) iter.next();
            if (backend.sendNotifications(oldRecords)) {
                published = true;
                break;
            }
        }
        if (!published) {
            this.changeState(State.FAILED);
            this.records.addAll(oldRecords);
            LOG.error("Could not publish operational log messages to any backend.");
            if (LOG.isTraceEnabled()) {
                LOG.trace("Internal state:\n" + BeanInspector.beanToString(this));
            }
            if (null != this.operations) {
                this.operations.notify(ManagementMessage.PUBLICATION_FAILURE, new Integer(this.records.size()));
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed sendNotification");
        }
    }

    /**
     * Unregister instrumentation.
     */
    private void unregisterInstrumentation() {
        if ((null != this.instrumentation)) {
            if (null != this.instrumentationManager) {
                try {
                    this.instrumentationManager.unregisterInstrumentation(this.instrumentation);
                } catch (InternalInfrastructureException e) {
                    LOG.error("Could not unregister NrPublisher - component might still be visible via Element Manager,"
                            + " but is in undefined state. Reason:\n" + e);
                }
            }
            this.instrumentation.destroy();
        }
    }

    /**
     * The Class Instrumentation.
     */
    public class Instrumentation {

        /** The owner. */
        private NrPublisherBean owner;

        /**
         * Instantiates a new instrumentation.
         * 
         * @param instance
         *        the instance
         */
        public Instrumentation(final NrPublisherBean instance) {
            this.owner = instance;
        }

        /**
         * Destroy.
         */
        public void destroy() {
        }

        /**
         * Flush messages.
         */
        public void flushMessages() {
            NrPublisherBean.this.flush();
        }

        /**
         * Gets the activated.
         * 
         * @return the activated
         */
        public Boolean getActivated() {
            return new Boolean(this.owner.activate);
        }

        /**
         * Gets the fail count.
         * 
         * @return the fail count
         */
        public String getFailCount() {
            return String.valueOf(this.owner.failCount);
        }

        /**
         * Gets the max messages.
         * 
         * @return the max messages
         */
        public String getMaxMessages() {
            return String.valueOf(this.owner.getMaxMessages());
        }

        /**
         * Gets the message threshold.
         * 
         * @return the message threshold
         */
        public String getMessageThreshold() {
            return String.valueOf(this.owner.getMessageThreshold());
        }

        /**
         * Gets the processing interval.
         * 
         * @return the processing interval
         */
        public String getProcessingInterval() {
            return String.valueOf(this.owner.getProcessingInterval());
        }

        /**
         * Gets the queuesize.
         * 
         * @return the queuesize
         */
        public String getQueuesize() {
            return String.valueOf(this.owner.records.size());
        }

        /**
         * Gets the state.
         * 
         * @return the state
         */
        public String getState() {
            return String.valueOf(NrPublisherBean.this.state);
        }

        /**
         * Sets the max messages.
         * 
         * @param val
         *        the new max messages
         */
        public void setMaxMessages(final String val) {
            int value = Integer.parseInt(val);
            this.owner.setMaxMessages(value);
        }

        /**
         * Sets the message threshold.
         * 
         * @param val
         *        the new message threshold
         */
        public void setMessageThreshold(final String val) {
            int value = Integer.parseInt(val);
            this.owner.setMessageThreshold(value);
        }

        /**
         * Sets the processing interval.
         * 
         * @param val
         *        the new processing interval
         */
        public void setProcessingInterval(final String val) {
            int value = Integer.parseInt(val);
            this.owner.setProcessingInterval(value);
        }

    }

    /**
     * TimerTask to trigger sending of notifications in regular intervals
     * 
     * TODO: create common TimerTask.
     */
    private class PublisherTimerTask extends TimerTask {

        /** The <code>NrPublisher</code> holding this task. */
        private NrPublisherBean owner;

        /**
         * Constructor.
         * 
         * @param owner
         *        The <code>MessagingMonitor</code> holding this task
         */
        public PublisherTimerTask(final NrPublisherBean owner) {
            if (null != owner) {
                this.owner = owner;
            } else
                throw new NullPointerException("Cannot instantiate PublisherTimerTask for null owner");
        }

        /**
         * (non-Javadoc).
         * 
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            this.owner.flush();
        }

    }

}
