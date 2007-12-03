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
package org.eclipse.swordfish.core.management.mock;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;
import javax.activation.DataHandler;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.security.auth.Subject;
import javax.xml.transform.Source;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;

/**
 * Dummy implementation of NormalizedMessage that provides functionality necessary for testing.
 * 
 */
public class DummyNormalizedMessage implements NormalizedMessage {

    /** The content. */
    private Source content;

    /** The properties. */
    private HashMap properties;

    /**
     * Instantiates a new dummy normalized message.
     * 
     * @param stream
     *        the stream
     */
    public DummyNormalizedMessage(final InputStream stream) {
        this.content = TransformerUtil.domFromInputStream(stream);
        this.properties = new HashMap();
        this.properties.put(HeaderUtil.HEADER_PROPERTY, new HashMap());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#addAttachment(java.lang.String,
     *      javax.activation.DataHandler)
     */
    public void addAttachment(final String arg0, final DataHandler arg1) throws MessagingException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#getAttachment(java.lang.String)
     */
    public DataHandler getAttachment(final String arg0) {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#getAttachmentNames()
     */
    public Set getAttachmentNames() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#getContent()
     */
    public Source getContent() {
        return this.content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#getProperty(java.lang.String)
     */
    public Object getProperty(final String arg0) {
        return this.properties.get(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#getPropertyNames()
     */
    public Set getPropertyNames() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#getSecuritySubject()
     */
    public Subject getSecuritySubject() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#removeAttachment(java.lang.String)
     */
    public void removeAttachment(final String arg0) throws MessagingException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#setContent(javax.xml.transform.Source)
     */
    public void setContent(final Source arg0) throws MessagingException {
        this.content = arg0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(final String arg0, final Object arg1) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.NormalizedMessage#setSecuritySubject(javax.security.auth.Subject)
     */
    public void setSecuritySubject(final Subject arg0) {

    }

}
