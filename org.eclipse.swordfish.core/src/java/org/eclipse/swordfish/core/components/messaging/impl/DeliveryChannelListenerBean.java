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
package org.eclipse.swordfish.core.components.messaging.impl;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import org.eclipse.swordfish.core.components.endpointmanager.EndpointManager;
import org.eclipse.swordfish.core.components.headerprocessing.HeaderProcessor;
import org.eclipse.swordfish.core.components.instancemanager.InstanceManager;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.messaging.DeliveryChannelListener;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;

/**
 * this is the main bean for delivery channel management.
 */
public class DeliveryChannelListenerBean implements DeliveryChannelListener {

    /** log to report events to. */
    private static final Log LOG = SBBLogFactory.getLog(DeliveryChannelListenerBean.class);

    /** the delivery channel this listener listens to. */
    private DeliveryChannel channel;

    /**
     * injection point for Spring. The instance manager is able to locate an InternalSBB instance
     * that for which an incoming MEP is targeted
     */
    private InstanceManager instanceManager;

    /**
     * injection point for Spring. The endpoint manager takes the responsibility of indicating which
     * participant is the target of an incoming request (currently on the provider side)
     */
    private EndpointManager endpointManager;

    /**
     * injection point for Spring. The header processor is responsible for transferring message
     * metadata between InternalCallContext and SOAP headers
     */
    private HeaderProcessor headerProcessor;

    /**
     * injection point for Spring. This value determines how often the delivery channel is requested
     * about arrivals of a message. It directly has influence on the System load.
     */
    private int threadSleepTime;

    /**
     * injection point for Spring. This varibale indicates the waiting time before the delivery
     * channel is again requested about the arrival of a new exchange. It directly influences the
     * system load.
     */
    private int acceptTimeout;

    /** indicates weather this thread is supposed to stop or continue to run. */
    private boolean canRun;

    /**
     * injection point for Spring. This value indicates the minimum number of threads availabel for
     * request execution. So this variable controls the InternalSBB performance.
     */
    private int minThreadCount;

    /**
     * injection point for Spring. This value indicates the maximum number of threads availabel for
     * request execution. So this variable controls the InternalSBB performance. any other incoming
     * request will queue up until a new thread becomes available.
     */
    private int maxThreadCount;

    /** thread pooling happens with this instance. */
    private ThreadPoolExecutor poolExecutor;

    /**
     * default public constructor.
     */
    public DeliveryChannelListenerBean() {
        final int zehn = 10;
        final int fufzig = 50;

        this.threadSleepTime = zehn;
        this.acceptTimeout = fufzig;
        this.minThreadCount = 1;
        this.maxThreadCount = 1;
        this.poolExecutor = null;
    }

    /**
     * Gets the accept timeout.
     * 
     * @return Returns the acceptTimeout.
     */
    public int getAcceptTimeout() {
        return this.acceptTimeout;
    }

    /**
     * Gets the endpoint manager.
     * 
     * @return Returns the endpointManager.
     */
    public EndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    /**
     * Gets the header processor.
     * 
     * @return the header processor
     */
    public HeaderProcessor getHeaderProcessor() {
        return this.headerProcessor;
    }

    /**
     * Gets the max thread count.
     * 
     * @return Returns the maxThreadCount.
     */
    public int getMaxThreadCount() {
        return this.maxThreadCount;
    }

    /**
     * Gets the min thread count.
     * 
     * @return Returns the minThreadCount.
     */
    public int getMinThreadCount() {
        return this.minThreadCount;
    }

    /**
     * Gets the thread sleep time.
     * 
     * @return Returns the threadSleepTime.
     */
    public int getThreadSleepTime() {
        return this.threadSleepTime;
    }

    /**
     * Init.
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelListener#init()
     */
    public void init() {
        final long thousand = 1000;
        this.poolExecutor =
                new ThreadPoolExecutor(this.minThreadCount, this.maxThreadCount, thousand, TimeUnit.MILLISECONDS,
                        new SynchronousQueue(), new NamedThreadFactory("InternalSBB-DC-Thread"));
        LOG
            .config("initalized with min thread count of " + this.minThreadCount + " and max thread count of "
                    + this.maxThreadCount);
    }

    /**
     * main method to accept work TODO do real reworking on the behaviour of this method.
     * 
     * @see java.lang.Runnable#run() TODO clean up this method and make it more modular and
     *      efficient
     */
    public void run() {
        MessageExchange exchange = null;
        while (this.canRun) {
            try {
                // get a new Unit of work
                try {
                    exchange = this.channel.accept(this.acceptTimeout);
                } catch (MessagingException e) {
                    // TODO exception handling
                    LOG.warn("accept exception", e);
                }
                if (exchange != null) {
                    if (ExchangeStatus.DONE.equals(exchange.getStatus())) {
                        // System.out.println("*** DONE on " +
                        // ((exchange.getRole() ==
                        // MessageExchange.Role.CONSUMER)? "CONSUMER" :
                        // "PROVIDER"));
                        LOG.debug("got receipt for successfull operation processing for " + exchange.getOperation().toString());
                        // TODO this might be the correct point to remove the
                        // call context
                        // in the case that it has been stored.
                        continue;
                    }

                    if (exchange.getOperation() == null) {
                        LOG.debug("operation is set to null for " + exchange.getExchangeId() + ", sending it back");
                        this.refuseExchangeWithError(exchange, new InternalConfigurationException(
                                "exchange operation must not be null"));
                        continue;
                    }

                    if (exchange.getService() == null) {
                        LOG.debug("service is set to null for " + exchange.getExchangeId() + ", sending it back");
                        this.refuseExchangeWithError(exchange, new InternalConfigurationException(
                                "exchange service must not be null"));
                        continue;
                    }

                    // this is the good case where the MEP dispatcher can start
                    // working!
                    this.poolExecutor.execute(new MEPDispatcherImpl(this.channel, this.instanceManager, this.getEndpointManager(),
                            this.headerProcessor, exchange));
                } else {
                    Thread.sleep(this.threadSleepTime);
                }
            } catch (RejectedExecutionException e) {
                LOG.error("No thread available to process incoming message, adjust thread pool size for DeliveryChannelListener.");
            } catch (Throwable t) {
                // make a severe notification to MSS
                LOG.error("aborted due to ", t);
            }
        }
    }

    /**
     * Sets the accept timeout.
     * 
     * @param acceptTimeout
     *        The acceptTimeout to set.
     */
    public void setAcceptTimeout(final int acceptTimeout) {
        this.acceptTimeout = acceptTimeout;
    }

    /**
     * this methods gets the component access injected and pulls out the delivery channel out of
     * that.
     * 
     * @param contextAccess
     *        acccess object to JBI component context
     */
    public void setContext(final ComponentContextAccess contextAccess) {

        try {
            this.channel = contextAccess.getDeliveryChannel();
        } catch (MessagingException e) {
            throw new RuntimeException("access to delivery channel denied! ", e);
        }

    }

    /**
     * Sets the endpoint manager.
     * 
     * @param endpointManager
     *        The endpointManager to set.
     */
    public void setEndpointManager(final EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    /**
     * Sets the header processor.
     * 
     * @param headerProcessor
     *        the new header processor
     */
    public void setHeaderProcessor(final HeaderProcessor headerProcessor) {
        this.headerProcessor = headerProcessor;
    }

    /**
     * Sets the instance manager.
     * 
     * @param instanceManager
     *        The instanceManager to set.
     */
    public void setInstanceManager(final InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    /**
     * Sets the max thread count.
     * 
     * @param maxThreadCount
     *        The maxThreadCount to set.
     */
    public void setMaxThreadCount(final int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    /**
     * Sets the min thread count.
     * 
     * @param minThreadCount
     *        The minThreadCount to set.
     */
    public void setMinThreadCount(final int minThreadCount) {
        this.minThreadCount = minThreadCount;
    }

    /**
     * Sets the thread sleep time.
     * 
     * @param threadSleepTime
     *        The threadSleepTime to set.
     */
    public void setThreadSleepTime(final int threadSleepTime) {
        this.threadSleepTime = threadSleepTime;
    }

    /**
     * Shutdown.
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelListener#shutdown()
     */
    public void shutdown() {
        // TODO what about threads waiting to be complished?
        this.poolExecutor.shutdown();
        LOG.info("delivery channel manager shutdown");
    }

    /**
     * Start.
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelListener#start()
     */
    public void start() {
        this.canRun = true;
        new Thread(this, "DC-Listener").start();
        LOG.info("delivery channel manager started");
    }

    /**
     * Stop.
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelListener#stop()
     */
    public void stop() {
        this.canRun = false;
        LOG.info("delivery channel manager stopped");
    }

    /**
     * Refuse exchange with error.
     * 
     * @param exchange
     *        the exchange
     * @param theError
     *        the the error
     */
    private void refuseExchangeWithError(final MessageExchange exchange, final Exception theError) {
        try {
            /**
             * This is a work around! If there is an out message existing in the exchange than
             * remove it. This prevents a BC to try to send the msg instead of the error
             */
            if (exchange instanceof InOut) {
                ((InOut) exchange).setOutMessage(null);
            }
            /*
             * Fix for defect #1605, if we set error slot, the setting of fault is ignored by the
             * binding. So we should check first if fault is null and only then setError slot. To
             * enable this fix, we need to get a fix for the binding if(exchange.getFault() ==
             * null){ exchange.setError(theError); }
             */
            exchange.setError(theError);
            exchange.setStatus(ExchangeStatus.ERROR);
            this.channel.send(exchange);
        } catch (MessagingException e) {
            LOG.info("refusing of exchanged failed " + exchange.getOperation().toString());
        }
    }
}
