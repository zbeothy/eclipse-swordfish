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
import java.net.URI;
import java.util.HashMap;
import java.util.Set;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.utils.ExchangeProperties;

/**
 * Dummy implementation of MesssageExchange that provides the methods necessary for testing
 * Correlation processing.
 * 
 */
public class DummyMessageExchange implements InOut {

    /** key: indicator for getMessage value: NormalizedMessage. */
    private final HashMap messages = new HashMap();

    /** The properties. */
    private final HashMap properties = new HashMap();

    /** The operation. */
    private QName operation = new QName("urn://sopgroup.org", "fooOp");

    /**
     * Instantiates a new dummy message exchange.
     */
    public DummyMessageExchange() {
        this.properties.put(ExchangeProperties.CALL_CONTEXT, new DummyCallContext());
    }

    /**
     * Adds the message.
     * 
     * @param key
     *        the key
     * @param stream
     *        the stream
     */
    public void addMessage(final String key, final InputStream stream) {
        NormalizedMessage message = new DummyNormalizedMessage(stream);
        this.messages.put(key, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#createFault()
     */
    public Fault createFault() throws MessagingException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#createMessage()
     */
    public NormalizedMessage createMessage() throws MessagingException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getEndpoint()
     */
    public ServiceEndpoint getEndpoint() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getError()
     */
    public Exception getError() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getExchangeId()
     */
    public String getExchangeId() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getFault()
     */
    public Fault getFault() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.InOut#getInMessage()
     */
    public NormalizedMessage getInMessage() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getInterfaceName()
     */
    public QName getInterfaceName() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getMessage(java.lang.String)
     */
    public NormalizedMessage getMessage(final String arg0) {
        return (NormalizedMessage) this.messages.get(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getOperation()
     */
    public QName getOperation() {
        return this.operation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.InOut#getOutMessage()
     */
    public NormalizedMessage getOutMessage() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getPattern()
     */
    public URI getPattern() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getProperty(java.lang.String)
     */
    public Object getProperty(final String arg0) {
        return this.properties.get(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getPropertyNames()
     */
    public Set getPropertyNames() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getRole()
     */
    public Role getRole() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getService()
     */
    public QName getService() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#getStatus()
     */
    public ExchangeStatus getStatus() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#isTransacted()
     */
    public boolean isTransacted() {

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setEndpoint(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public void setEndpoint(final ServiceEndpoint arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setError(java.lang.Exception)
     */
    public void setError(final Exception arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setFault(javax.jbi.messaging.Fault)
     */
    public void setFault(final Fault arg0) throws MessagingException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.InOut#setInMessage(javax.jbi.messaging.NormalizedMessage)
     */
    public void setInMessage(final NormalizedMessage arg0) throws MessagingException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setInterfaceName(javax.xml.namespace.QName)
     */
    public void setInterfaceName(final QName arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setMessage(javax.jbi.messaging.NormalizedMessage,
     *      java.lang.String)
     */
    public void setMessage(final NormalizedMessage arg0, final String arg1) throws MessagingException {
        this.messages.put(arg1, arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setOperation(javax.xml.namespace.QName)
     */
    public void setOperation(final QName arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.InOut#setOutMessage(javax.jbi.messaging.NormalizedMessage)
     */
    public void setOutMessage(final NormalizedMessage arg0) throws MessagingException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(final String arg0, final Object arg1) {
        this.properties.put(arg0, arg1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setService(javax.xml.namespace.QName)
     */
    public void setService(final QName arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.messaging.MessageExchange#setStatus(javax.jbi.messaging.ExchangeStatus)
     */
    public void setStatus(final ExchangeStatus arg0) throws MessagingException {

    }

}
