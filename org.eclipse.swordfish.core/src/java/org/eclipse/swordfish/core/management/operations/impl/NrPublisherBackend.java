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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.adapter.ManagementAdapter;
import org.eclipse.swordfish.core.management.operations.OperationalMessage;
import org.eclipse.swordfish.core.utils.DOM2Writer;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * The Class NrPublisherBackend.
 */
public abstract class NrPublisherBackend implements PublisherBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(NrPublisherBackend.class);

    /** The Constant MESSAGE_SKELETON. */
    private final static String MESSAGE_SKELETON = "NotificationSkeleton.xml";

    /** The adapter. */
    private ManagementAdapter adapter = null;

    /**
     * Destroy.
     */
    public void destroy() {
        this.adapter = null;
    }

    /**
     * Sets the adapter.
     * 
     * @param anAdapter
     *        the new adapter
     */
    public void setAdapter(final ManagementAdapter anAdapter) {
        this.adapter = anAdapter;
    }

    /**
     * Creates the message.
     * 
     * @param oldRecords
     *        the old records
     * 
     * @return the document
     */
    protected Document createMessage(final List oldRecords) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("starting createMessage");
        }
        InputStream is = this.getClass().getResourceAsStream(MESSAGE_SKELETON);
        Document doc = TransformerUtil.docFromInputStream(is);
        Element root = doc.getDocumentElement();
        root.setAttribute("sender", this.getManagementHost());
        for (Iterator iter = oldRecords.iterator(); iter.hasNext();) {
            OperationalMessageRecord record = (OperationalMessageRecord) iter.next();
            Element msg = this.buildNode(record, doc);
            root.appendChild(msg);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("completed createMessage, result:\n" + DOM2Writer.nodeToString(doc.getDocumentElement(), false));
        }
        return doc;
    }

    /**
     * Builds the node.
     * 
     * @param record
     *        the record
     * @param doc
     *        the doc
     * 
     * @return the element
     */
    private Element buildNode(final OperationalMessageRecord record, final Document doc) {
        Element ret = doc.createElement("msg");
        ret.setAttribute("id", record.getFqMsgID());
        String level = "UNKNOWN";
        String cat = "UNKNOWN";
        OperationalMessage msg = record.getOperationalMessage();
        if (null != msg) {
            level = msg.getSeverity().getName();
            cat = msg.getCategory();
        }
        ret.setAttribute("lvl", level);
        ret.setAttribute("cat", cat);
        ret.setAttribute("text", record.getMessage());
        String participantId = "sbb";
        if (null != record.getParticipant()) {
            participantId = String.valueOf(record.getParticipant());
        }
        ret.setAttribute("app", participantId);
        Date date = new Date(record.getMillis());
        SimpleDateFormat iso8601UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        iso8601UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = iso8601UTC.format(date);
        ret.setAttribute("tsp", timestamp);
        Object[] params = record.getParameters();
        for (int i = 0; i < params.length; i++) {
            Element param = doc.createElement("para");
            Text text = doc.createTextNode(String.valueOf(params[i]));
            param.appendChild(text);
            ret.appendChild(param);
        }
        return ret;
    }

    /**
     * Gets the management host.
     * 
     * @return servername/port for EmAdapter
     */
    private String getManagementHost() {
        String ret = null;
        if ((null != this.adapter) && (null != this.adapter.getHost())) {
            ret = this.adapter.getHost();
        } else {
            try {
                InetAddress address = InetAddress.getLocalHost();
                ret = address.getCanonicalHostName();
            } catch (UnknownHostException e) {
                ret = "unknown";
            }
        }
        if (null != this.adapter) {
            ret = ret + ":" + this.adapter.getPort();
        }
        return ret;
    }
}
