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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.util.MissingResourceException;
import java.util.logging.Logger;
import javax.jbi.JBIException;
import javax.jbi.management.MBeanNames;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.management.MBeanServer;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * The Class ComponentContextAccessStub.
 */
public class ComponentContextAccessStub implements ComponentContextAccess {

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#activateEndpoint(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public ServiceEndpoint activateEndpoint(final QName qname, final String s) throws JBIException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#deactivateEndpoint(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public void deactivateEndpoint(final ServiceEndpoint serviceendpoint) throws JBIException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#deregisterExternalEndpoint(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public void deregisterExternalEndpoint(final ServiceEndpoint serviceendpoint) throws JBIException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getComponentName()
     */
    public String getComponentName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getDeliveryChannel()
     */
    public DeliveryChannel getDeliveryChannel() throws MessagingException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getEndpoint(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public ServiceEndpoint getEndpoint(final QName qname, final String s) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getEndpointDescriptor(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public Document getEndpointDescriptor(final ServiceEndpoint serviceendpoint) throws JBIException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getEndpoints(javax.xml.namespace.QName)
     */
    public ServiceEndpoint[] getEndpoints(final QName qname) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getEndpointsForService(javax.xml.namespace.QName)
     */
    public ServiceEndpoint[] getEndpointsForService(final QName qname) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getExternalEndpoints(javax.xml.namespace.QName)
     */
    public ServiceEndpoint[] getExternalEndpoints(final QName qname) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getExternalEndpointsForService(javax.xml.namespace.QName)
     */
    public ServiceEndpoint[] getExternalEndpointsForService(final QName qname) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getInstallRoot()
     */
    public String getInstallRoot() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getLogger(java.lang.String, java.lang.String)
     */
    public Logger getLogger(final String s, final String s1) throws MissingResourceException, JBIException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getMBeanNames()
     */
    public MBeanNames getMBeanNames() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getMBeanServer()
     */
    public MBeanServer getMBeanServer() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getNamingContext()
     */
    public InitialContext getNamingContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getTransactionManager()
     */
    public Object getTransactionManager() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#getWorkspaceRoot()
     */
    public String getWorkspaceRoot() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#registerExternalEndpoint(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public void registerExternalEndpoint(final ServiceEndpoint serviceendpoint) throws JBIException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jbi.component.ComponentContext#resolveEndpointReference(org.w3c.dom.DocumentFragment)
     */
    public ServiceEndpoint resolveEndpointReference(final DocumentFragment documentfragment) {
        return null;
    }

}
