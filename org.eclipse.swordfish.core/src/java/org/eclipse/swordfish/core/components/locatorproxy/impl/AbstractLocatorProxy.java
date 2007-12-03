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
package org.eclipse.swordfish.core.components.locatorproxy.impl;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * TODO move generic parts of the locator proxy into this class.
 */
public abstract class AbstractLocatorProxy implements LocatorProxy {

    /** The locator present. */
    private boolean locatorPresent;

    /** The service NS. */
    private String serviceNS;

    /** The service name. */
    private String serviceName;

    /** The endpoint name. */
    private String endpointName;

    /** The component context. */
    private ComponentContextAccess componentContext;

    /** The exchange factory. */
    private MessageExchangeFactory exchangeFactory;

    /**
     * Instantiates a new abstract locator proxy.
     */
    public AbstractLocatorProxy() {
        this.locatorPresent = false;
        this.exchangeFactory = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy#deploy(java.lang.String,
     *      java.lang.String)
     */
    public abstract void deploy(String locatorId, String configuration) throws InternalInfrastructureException;

    // abstract methods to be implemented by the specific subclasses that
    // implement locator functionality

    /**
     * Gets the component context.
     * 
     * @return Returns the componentContext.
     */
    public ComponentContextAccess getComponentContext() {
        return this.componentContext;
    }

    /**
     * Gets the endpoint name.
     * 
     * @return Returns the endpointName.
     */
    public String getEndpointName() {
        return this.endpointName;
    }

    /**
     * Gets the service name.
     * 
     * @return Returns the serviceName.
     */
    public String getServiceName() {
        return this.serviceName;
    }

    /**
     * Gets the service NS.
     * 
     * @return Returns the serviceNS.
     */
    public String getServiceNS() {
        return this.serviceNS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy#isActive()
     */
    public boolean isActive() {
        return this.locatorPresent;
    }

    // Spring injection points
    /**
     * Checks if is locator present.
     * 
     * @return Returns the locatorPresent.
     */
    public boolean isLocatorPresent() {
        return this.locatorPresent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy#register(java.lang.String,
     *      javax.xml.namespace.QName, java.lang.String, org.w3c.dom.DocumentFragment)
     */
    public abstract void register(String locatorId, QName service, String endpoint, DocumentFragment address)
            throws InternalInfrastructureException;

    /**
     * Sets the component context.
     * 
     * @param componentContext
     *        The componentContext to set.
     */
    public void setComponentContext(final ComponentContextAccess componentContext) {
        this.componentContext = componentContext;
    }

    /**
     * Sets the endpoint name.
     * 
     * @param endpointName
     *        The endpointName to set.
     */
    public void setEndpointName(final String endpointName) {
        this.endpointName = endpointName;
    }

    /**
     * Sets the locator present.
     * 
     * @param locatorPresent
     *        The locatorPresent to set.
     */
    public void setLocatorPresent(final boolean locatorPresent) {
        this.locatorPresent = locatorPresent;
    }

    /**
     * Sets the service name.
     * 
     * @param serviceName
     *        The serviceName to set.
     */
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Sets the service NS.
     * 
     * @param serviceNS
     *        The serviceNS to set.
     */
    public void setServiceNS(final String serviceNS) {
        this.serviceNS = serviceNS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy#undeploy(java.lang.String)
     */
    public abstract void undeploy(String locatorId) throws InternalInfrastructureException;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy#unregister(java.lang.String,
     *      javax.xml.namespace.QName, java.lang.String)
     */
    public abstract void unregister(String locatorId, QName service, String endpoint) throws InternalInfrastructureException;

    /**
     * FIXME beautify the code and the resuting error messages out of the faults.
     * 
     * @param operation
     *        the operation
     * @param msg
     *        the msg
     * 
     * @return the string
     * 
     * @throws InternalInfrastructureException
     */
    protected String invokeLocatorProxy(final String operation, final String msg) throws InternalInfrastructureException {
        try {
            QName locatorSQN = new QName(this.getServiceNS(), this.getServiceName());
            if (this.exchangeFactory == null) {
                this.exchangeFactory = this.componentContext.getDeliveryChannel().createExchangeFactory(locatorSQN);
            }
            InOut inout = this.exchangeFactory.createInOutExchange();
            inout.setService(locatorSQN);
            inout.setOperation(new QName(this.serviceNS, operation));
            NormalizedMessage in = inout.createMessage();
            // FIXME we use a DOMSource explicitly here
            in.setContent(new DOMSource(TransformerUtil.docFromString(msg)));
            inout.setInMessage(in);

            if (this.componentContext.getDeliveryChannel().sendSync(inout)) {
                if (inout.getStatus() == ExchangeStatus.ACTIVE) {
                    NormalizedMessage out = inout.getOutMessage();
                    if ((out != null) && (out.getContent() != null)) {
                        Element elem = TransformerUtil.docFromSource(out.getContent()).getDocumentElement();
                        String answer = elem.getLocalName();
                        inout.setStatus(ExchangeStatus.DONE);
                        this.componentContext.getDeliveryChannel().send(inout);
                        return answer;
                    } else {
                        Element elem = TransformerUtil.docFromSource(inout.getFault().getContent()).getDocumentElement();
                        String answer = elem.getLocalName();
                        // String detail =
                        // TransformerUtil.stringFromSource(inout.getFault()
                        // .getContent());
                        inout.setStatus(ExchangeStatus.DONE);
                        this.componentContext.getDeliveryChannel().send(inout);
                        throw new InternalInfrastructureException(answer);
                    }
                } else if (inout.getStatus() == ExchangeStatus.ERROR)
                    throw new InternalInfrastructureException(operation + " failed because: ", inout.getError());
                else
                    throw new InternalInfrastructureException("exchange status DONE received! this should not happen at this point");
            } else
                throw new InternalInfrastructureException(operation
                        + " on locator proxy failed: maybe the infrastructure is not correctly configured.");

        } catch (MessagingException e) {
            throw new InternalInfrastructureException("the infrastructure is not correctly configured."
                    + " cannot address the locator proxy.", e);
        }
    }

}
