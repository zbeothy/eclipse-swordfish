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
package org.eclipse.swordfish.core.management.notification.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.components.ManagementController;
import org.eclipse.swordfish.core.management.notification.ManagementNotification;
import org.eclipse.swordfish.core.management.notification.ManagementNotificationListener;
import org.eclipse.swordfish.core.management.notification.NotificationProcessor;
import org.eclipse.swordfish.core.utils.BeanInspector;

/**
 * Notification listener implementation for notifications that inform management about relevant
 * events. <br/> Responsible for de-coupling the reporting of notification from the actual
 * processing and for dispatching them to any notification processor registered for the type of
 * event <br/> The actual processing is done by the processors in
 * <code>notificationProcessors</code> Currently used only for message processing notifications,
 * might be expanded later
 * 
 */
public class ManagementNotificationListenerBean implements ManagementNotificationListener {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ManagementNotificationListenerBean.class);

    /** The Constant DEFAULT_PROCESSING_INTERVAL. */
    private static final long DEFAULT_PROCESSING_INTERVAL = 10000;

    /** The management controller. */
    private ManagementController managementController;

    /** The actual processors for notifications. */
    private List notificationProcessors;

    /**
     * switch to controll the processing of incoming notifications <code>true</code> directly
     * process incoming messages (for debugging purposes) <code>false</code> process messages
     * every <code>processingInterval</code> milliseconds.
     */
    private boolean directProcessing = false;

    /** Interval in milliseconds to collect notifications before they are processed. */
    private long processingInterval = DEFAULT_PROCESSING_INTERVAL;

    /** Collection of unprocessed notifications Values: <code>MessageProcessingNotification</code>s. */
    private Collection incoming;

    /** Timer used to control automatic processing of notifications. */
    private Timer managementTimer;

    /**
     * Task that initiates automatic processing of notifications every
     * <code>processingInterval</code> milliseconds.
     */
    private TimerTask theTimerTask;

    /** Flag indicating if this notification listener is still active. */
    private boolean active;

    /**
     * Constructor to initialize members not set by dependency injection.
     */
    public ManagementNotificationListenerBean() {
        this.incoming = new ArrayList();
        LOG.debug("instantiated");
    }

    /**
     * End notification listener activities so that it can be safely destroyed.
     */
    public void deactivate() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deactivate");
        }
        if (null != this.theTimerTask) {
            this.theTimerTask.cancel();
        }
        this.processNotifications();
        synchronized (this) {
            this.theTimerTask = null;
            this.active = false;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("deactivated");
        }
    }

    /**
     * Release all resources held by this object. After calling this method, subsequent calls to
     * other methods will result in arbitrary errors!
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        if (this.isActive()) {
            this.deactivate();
        }
        this.processNotifications();
        synchronized (this) {
            this.incoming = null;
            this.managementTimer = null;
            this.managementController = null;
            if (null != this.notificationProcessors) {
                this.notificationProcessors.clear();
                this.notificationProcessors = null;
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * Gets the management controller.
     * 
     * @return the management controller
     */
    public ManagementController getManagementController() {
        return this.managementController;
    }

    // Business interface

    /**
     * Gets the management timer.
     * 
     * @return the management timer
     */
    public Timer getManagementTimer() {
        return this.managementTimer;
    }

    /**
     * Gets the notification processors.
     * 
     * @return the notification processors
     */
    public List getNotificationProcessors() {
        return this.notificationProcessors;
    }

    // Dependency injection interface

    /**
     * Gets the processing interval.
     * 
     * @return the processing interval
     */
    public long getProcessingInterval() {
        return this.processingInterval;
    }

    /**
     * Init.
     */
    public void init() {
        // init timer structures
        this.active = true;
        this.setProcessingInterval(this.processingInterval);
        LOG.debug("initialized");
    }

    /**
     * (non-Javadoc).
     * 
     * @return true, if is active
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotificationListener#isActive()
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * triggers the processing of pending notifications <br/> Normally called by internal
     * <code>Timer</code> instance, but my be called manually.
     */
    public synchronized void processNotifications() {
        if (!this.active) {
            LOG.warn("Asked to process notifications after destroy - ignoring");
            return;
        }
        Collection notifications = this.getCurrentNotifications();
        for (Iterator iter = notifications.iterator(); iter.hasNext();) {
            ManagementNotification notification = (ManagementNotification) iter.next();
            for (Iterator processors = this.notificationProcessors.iterator(); processors.hasNext();) {
                NotificationProcessor processor = (NotificationProcessor) processors.next();
                processor.process(notification);
            }
            if (LOG.isDebugEnabled()) {
                StringBuffer msg = new StringBuffer("Notification " + notification + " processed.\n");
                LOG.debug(msg.toString());
            }
        }
        if (0 != notifications.size()) {
            if (LOG.isDebugEnabled()) {
                LOG.info("" + notifications.size() + " notifications processed");
            }
        }
    }

    /**
     * Send notification.
     * 
     * @param notification
     *        the notification
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotificationListener#sendNotification(org.eclipse.swordfish.core.management.notification.MessageProcessingNotification)
     */
    public void sendNotification(final ManagementNotification notification) {
        if (!this.active) {
            LOG.warn("Received notification after destroy - ignoring: " + notification.toString());
            return;
        }
        this.incoming.add(notification);
        if (this.directProcessing) {
            this.processNotifications();
        }
        if (LOG.isDebugEnabled()) {
            String msg = "Notification " + notification + " received";
            LOG.debug(msg);
            if (LOG.isTraceEnabled()) {
                LOG.trace(BeanInspector.beanToString(notification));
            }
        }
    }

    /**
     * Sets the direct processing.
     * 
     * @param directProcessing
     *        the new direct processing
     */
    public void setDirectProcessing(final boolean directProcessing) {
        this.directProcessing = directProcessing;
    }

    /**
     * Sets the management controller.
     * 
     * @param managementController
     *        the new management controller
     */
    public void setManagementController(final ManagementController managementController) {
        this.managementController = managementController;
    }

    // Utility methods

    /**
     * Sets the management timer.
     * 
     * @param managementTimer
     *        the new management timer
     */
    public void setManagementTimer(final Timer managementTimer) {
        this.managementTimer = managementTimer;
    }

    /**
     * Sets up the <code>NotificationProcessor</code>s to be used. Normally called by container
     * 
     * @param spec
     *        <code>java.util.List</code> containing the <code>NotificationProcessor</code>s
     */
    public void setNotificationProcessors(final List spec) {
        this.notificationProcessors = new Vector(spec.size());
        for (Iterator it = spec.iterator(); it.hasNext();) {
            NotificationProcessor processor = (NotificationProcessor) it.next();
            this.notificationProcessors.add(processor);
        }
    }

    /**
     * Sets the processing interval.
     * 
     * @param processingInterval
     *        the new processing interval
     */
    public void setProcessingInterval(final long processingInterval) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setProcessingInterval start");
        }
        this.processingInterval = processingInterval;
        if (null != this.theTimerTask) {
            this.theTimerTask.cancel();
            if (LOG.isDebugEnabled()) {
                LOG.debug("TimerTask canceled");
            }
        }
        if (this.active) {
            if (null != this.managementTimer) {
                this.theTimerTask = new NotificationTimerTask(this);
                this.managementTimer.schedule(this.theTimerTask, processingInterval, processingInterval);
            } else {
                LOG.warn("No management timer available - switching to direct processing");
                this.directProcessing = true;
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("setProcessingInterval finished");
        }

    }

    /**
     * creates a new collection for incoming messages and returns the current one.
     * 
     * @return the <code>Collection</code> with unprocessed notifications
     */
    private Collection getCurrentNotifications() {
        // try to guess a good size for the new collection
        int newsize = Math.max(10, (this.incoming.size() * 11 / 10));
        Collection newIncoming = new ArrayList(newsize);
        Collection ret = this.incoming;
        this.incoming = newIncoming;
        return ret;
    }

    /**
     * TimerTask to trigger processing of notifications in regular intervals
     * 
     * TODO: create common TimerTask.
     */
    private class NotificationTimerTask extends TimerTask {

        /** The <code>MessagingMonitor</code> holding this task. */
        private ManagementNotificationListenerBean owner;

        /**
         * Constructor.
         * 
         * @param owner
         *        The <code>MessagingMonitor</code> holding this task
         */
        public NotificationTimerTask(final ManagementNotificationListenerBean owner) {
            if (null != owner) {
                this.owner = owner;
            } else
                throw new NullPointerException("Cannot instantiate MessagingTimerTask for null owner");
        }

        /**
         * (non-Javadoc).
         * 
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            this.owner.processNotifications();
        }

    }

}
