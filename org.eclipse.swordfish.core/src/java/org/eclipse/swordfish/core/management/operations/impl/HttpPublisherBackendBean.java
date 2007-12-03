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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import javax.management.ObjectName;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.messages.ManagementMessage;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;
import org.eclipse.swordfish.core.utils.BeanInspector;
import org.eclipse.swordfish.core.utils.DOM2Writer;
import org.w3c.dom.Document;

/**
 * The Class HttpPublisherBackendBean.
 */
public class HttpPublisherBackendBean extends NrPublisherBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(HttpPublisherBackendBean.class);

    /** The activate. */
    private boolean activate = false;

    /** The url. */
    private URL url;

    /** The state. */
    private State state;

    /** The operations. */
    private Operations operations;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.NrPublisherBackend#destroy()
     */
    @Override
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        this.operations = null;
        super.destroy();
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.PublisherBackend#getInstrumentationOn()
     */
    public ObjectName getInstrumentationOn() {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.PublisherBackend#getState()
     */
    public State getState() {
        return this.state;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl() {
        if (null != this.url) return this.url.toString();
        return null;
    }

    /**
     * Checks if is activate.
     * 
     * @return true, if is activate
     */
    public boolean isActivate() {
        return this.activate;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.PublisherBackend#sendNotifications(java.util.List)
     */
    public boolean sendNotifications(final List notifications) {
        if (!this.activate) return false;
        Document doc = this.createMessage(notifications);
        boolean ret = false;
        try {
            this.sendHttpNotification(doc);
            ret = true;
            if (!State.STOPPING.equals(this.getState())) {
                // leave state unchanged if we are shutting down
                this.changeState(State.RUNNING);
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("Logged " + notifications.size() + " messages to operational log");
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Generated log message:\n" + DOM2Writer.nodeToString(doc.getDocumentElement(), true));
                    LOG.trace("Internal state:\n" + BeanInspector.beanToString(this));
                }
            }
        } catch (IOException e) {
            this.changeState(State.FAILED);
            LOG.error("Could not publish operational log messages - reason " + e.getMessage());
            if (LOG.isTraceEnabled()) {
                LOG.trace("Internal state:\n" + BeanInspector.beanToString(this));
            }
            if (null != this.operations) {
                this.operations.notify(ManagementMessage.SERVER_CONNECTION_FAILED, "NrPublisher", this.getUrl(), e.toString());
            }
        }
        return ret;
    }

    /**
     * Sets the activate.
     * 
     * @param activate
     *        the new activate
     */
    public void setActivate(final boolean activate) {
        this.activate = activate;
    }

    /**
     * Sets the operations.
     * 
     * @param newOps
     *        the new operations
     */
    public void setOperations(final Operations newOps) {
        this.operations = newOps;
    }

    /**
     * Sets the url.
     * 
     * @param urlName
     *        the new url
     */
    public void setUrl(final String urlName) {
        this.setUrlInternal(urlName);
    }

    /**
     * Sets the url internal.
     * 
     * @param urlName
     *        the url name
     * 
     * @return the string
     */
    public String setUrlInternal(final String urlName) {
        String msg = "ok";
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting setUrl to " + urlName);
        }
        this.url = null;
        if ((null != urlName) && (!"".equals(urlName))) {
            try {
                this.url = new URL(urlName);
                String host = this.url.getHost();
                int port = this.url.getPort();
                if (this.activate) {
                    try {
                        Socket socket = new Socket(host, port);
                        socket.close();
                        this.changeState(State.RUNNING);
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("setUrl succeeded");
                        }
                    } catch (IOException e) {
                        msg = "Could not connect to server at " + urlName + " - Reason " + e.toString();
                        LOG.error(msg);
                        this.changeState(State.FAILED);
                        // operations.notify(ManagementMessage.SERVER_CONNECTION_FAILED,
                        // "NrPublisher", urlName, e.toString());
                    }
                }
            } catch (MalformedURLException e) {
                msg = "Could not connect to server at " + urlName + " - Reason " + e.toString();
                LOG.error(msg);
                this.changeState(State.FAILED);
                if (null != this.operations) {
                    this.operations.notify(ManagementMessage.SERVER_CONNECTION_FAILED, "HttpPublisherBackend", urlName, e
                        .toString());
                }
            }
        } else {
            this.changeState(State.STOPPED);
            if (LOG.isTraceEnabled()) {
                LOG.trace("setUrl - stopped");
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed setUrl");
        }
        return msg;
    }

    /**
     * Change state.
     * 
     * @param newState
     *        the new state
     */
    private void changeState(final State newState) {
        this.state = newState;
    }

    /**
     * Check http post result.
     * 
     * @param connection
     *        the connection
     * 
     * @throws IOException
     */
    private void checkHttpPostResult(final HttpURLConnection connection) throws IOException {
        final int[] resultHolder = new int[2];
        resultHolder[0] = -1;
        resultHolder[1] = -1;
        Thread checker = new Thread() {

            @Override
            public void run() {
                try {
                    resultHolder[0] = connection.getResponseCode();
                } catch (Exception ex) {
                    LOG.debug(

                    "Error getting Response while posting operational notifications", ex);
                }
                connection.disconnect();
                resultHolder[1] = 1;
            };
        };
        // ensure check thread will not hinder end of JVM
        checker.setDaemon(true);
        // run check Thread
        checker.start();
        try {
            checker.join(5000); // TODO configure wait interval
        } catch (InterruptedException iex) {
            iex.printStackTrace();
            // ignored
        }
        if (resultHolder[1] != 1) {
            // checker didn't finish, ensure clean up
            connection.disconnect();
            checker.interrupt();
        }
        if ((resultHolder[0] < 200) || (resultHolder[0] > 299))
            throw new IOException("sendHttpNotification failed with return code " + resultHolder[0]);
    }

    /**
     * Send http notification.
     * 
     * @param doc
     *        the doc
     * 
     * @throws IOException
     */
    private void sendHttpNotification(final Document doc) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting sendHttpNotification");
        }
        HttpURLConnection connection;
        connection = (HttpURLConnection) this.url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "text/xml");
        PrintWriter out = new PrintWriter(connection.getOutputStream());
        DOM2Writer.serializeAsXML(doc.getDocumentElement(), out, false, false);
        out.println("");
        out.flush();
        out.close();
        this.checkHttpPostResult(connection);
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed sendHttpNotification");
        }
    }

}
