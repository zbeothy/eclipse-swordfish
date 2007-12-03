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
package org.eclipse.swordfish.core.components.jbi.impl;

import java.util.MissingResourceException;
import java.util.logging.Logger;
import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.management.MBeanNames;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.management.MBeanServer;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * This bean acts as a proxy the access to the JBI componentContext functionality. following System
 * properties are used here:
 * <ul>
 * <li> <code>SOPware.ServiceBackbone.WorkspaceRoot</code> if set, than it determines the engines
 * private workspace. If not the default JBI value is taken </li>
 * <li> <code>SOPware.ServiceBackbone.InstallRoot</code> if set, than it determines the engines
 * installtion directory containing the necessary configuration items. If not the default JBI value
 * is taken </li>
 * </ul>
 */
public class ComponentContextAccessBean implements ComponentContextAccess {

    /** the holder varibale for the JBI component context. */
    private ComponentContext ctx;

    /**
     * Activate endpoint.
     * 
     * @param servName
     *        the serv name
     * @param epName
     *        the ep name
     * 
     * @return the service endpoint
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentContext#activateEndpoint(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public ServiceEndpoint activateEndpoint(final QName servName, final String epName) throws JBIException {
        return this.getComponentContext().activateEndpoint(servName, epName);
    }

    /**
     * Deactivate endpoint.
     * 
     * @param sep
     *        the sep
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentContext#deactivateEndpoint(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public void deactivateEndpoint(final ServiceEndpoint sep) throws JBIException {
        this.getComponentContext().deactivateEndpoint(sep);
    }

    /**
     * Deregister external endpoint.
     * 
     * @param sep
     *        the sep
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentContext#deregisterExternalEndpoint(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public void deregisterExternalEndpoint(final ServiceEndpoint sep) throws JBIException {
        this.getComponentContext().deregisterExternalEndpoint(sep);
    }

    /**
     * Gets the component name.
     * 
     * @return the component name
     * 
     * @see javax.jbi.component.ComponentContext#getComponentName()
     */
    public String getComponentName() {
        return this.getComponentContext().getComponentName();
    }

    /**
     * Gets the delivery channel.
     * 
     * @return the delivery channel
     * 
     * @throws InternalMessagingException
     * 
     * @see javax.jbi.component.ComponentContext#getDeliveryChannel()
     */
    public DeliveryChannel getDeliveryChannel() throws MessagingException {
        return this.getComponentContext().getDeliveryChannel();
    }

    /**
     * Gets the endpoint.
     * 
     * @param servName
     *        the serv name
     * @param epName
     *        the ep name
     * 
     * @return the endpoint
     * 
     * @see javax.jbi.component.ComponentContext#getEndpoint(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public ServiceEndpoint getEndpoint(final QName servName, final String epName) {
        return this.getComponentContext().getEndpoint(servName, epName);
    }

    /**
     * Gets the endpoint descriptor.
     * 
     * @param sep
     *        the sep
     * 
     * @return the endpoint descriptor
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentContext#getEndpointDescriptor(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public Document getEndpointDescriptor(final ServiceEndpoint sep) throws JBIException {
        return this.getComponentContext().getEndpointDescriptor(sep);
    }

    /**
     * Gets the endpoints.
     * 
     * @param servName
     *        the serv name
     * 
     * @return the endpoints
     * 
     * @see javax.jbi.component.ComponentContext#getEndpoints(javax.xml.namespace.QName)
     */
    public ServiceEndpoint[] getEndpoints(final QName servName) {
        return this.getComponentContext().getEndpoints(servName);
    }

    /**
     * Gets the endpoints for service.
     * 
     * @param servName
     *        the serv name
     * 
     * @return the endpoints for service
     * 
     * @see javax.jbi.component.ComponentContext#getEndpointsForService(javax.xml.namespace.QName)
     */
    public ServiceEndpoint[] getEndpointsForService(final QName servName) {
        return this.getComponentContext().getEndpointsForService(servName);
    }

    /**
     * Gets the external endpoints.
     * 
     * @param servName
     *        the serv name
     * 
     * @return the external endpoints
     * 
     * @see javax.jbi.component.ComponentContext#getExternalEndpoints(javax.xml.namespace.QName)
     */
    public ServiceEndpoint[] getExternalEndpoints(final QName servName) {
        return this.getComponentContext().getExternalEndpoints(servName);
    }

    /**
     * Gets the external endpoints for service.
     * 
     * @param servName
     *        the serv name
     * 
     * @return the external endpoints for service
     * 
     * @see javax.jbi.component.ComponentContext#getExternalEndpointsForService(javax.xml.namespace.QName)
     */
    public ServiceEndpoint[] getExternalEndpointsForService(final QName servName) {
        return this.getComponentContext().getExternalEndpointsForService(servName);
    }

    /**
     * Gets the install root.
     * 
     * @return the install root
     * 
     * @see javax.jbi.component.ComponentContext#getInstallRoot()
     */
    public String getInstallRoot() {
        return System.getProperty("SOPware.ServiceBackbone.InstallRoot", this.getComponentContext().getInstallRoot());
    }

    /**
     * Gets the logger.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @return the logger
     * 
     * @throws MissingResourceException
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentContext#getLogger(java.lang.String, java.lang.String)
     */
    public Logger getLogger(final String arg0, final String arg1) throws MissingResourceException, JBIException {
        return this.getComponentContext().getLogger(arg0, arg1);
    }

    /**
     * Gets the M bean names.
     * 
     * @return the m bean names
     * 
     * @see javax.jbi.component.ComponentContext#getMBeanNames()
     */
    public MBeanNames getMBeanNames() {
        return this.getComponentContext().getMBeanNames();
    }

    /**
     * Gets the M bean server.
     * 
     * @return the m bean server
     * 
     * @see javax.jbi.component.ComponentContext#getMBeanServer()
     */
    public MBeanServer getMBeanServer() {
        return this.getComponentContext().getMBeanServer();
    }

    /**
     * Gets the naming context.
     * 
     * @return the naming context
     * 
     * @see javax.jbi.component.ComponentContext#getNamingContext()
     */
    public InitialContext getNamingContext() {
        return this.getComponentContext().getNamingContext();
    }

    /**
     * Gets the transaction manager.
     * 
     * @return the transaction manager
     * 
     * @see javax.jbi.component.ComponentContext#getTransactionManager()
     */
    public Object getTransactionManager() {
        return this.getComponentContext().getTransactionManager();
    }

    /**
     * Gets the workspace root.
     * 
     * @return the workspace root
     * 
     * @see javax.jbi.component.ComponentContext#getWorkspaceRoot()
     */
    public String getWorkspaceRoot() {
        return System.getProperty("SOPware.ServiceBackbone.WorkspaceRoot", this.getComponentContext().getWorkspaceRoot());
    }

    /**
     * Register external endpoint.
     * 
     * @param sep
     *        the sep
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentContext#registerExternalEndpoint(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public void registerExternalEndpoint(final ServiceEndpoint sep) throws JBIException {
        this.getComponentContext().registerExternalEndpoint(sep);
    }

    /**
     * Resolve endpoint reference.
     * 
     * @param frag
     *        the frag
     * 
     * @return the service endpoint
     * 
     * @see javax.jbi.component.ComponentContext#resolveEndpointReference(org.w3c.dom.DocumentFragment)
     */
    public ServiceEndpoint resolveEndpointReference(final DocumentFragment frag) {
        return this.getComponentContext().resolveEndpointReference(frag);
    }

    /**
     * Sets the component context.
     * 
     * @param context
     *        the context to set, this is a manula injection point
     */
    public void setComponentContext(final ComponentContext context) {
        this.ctx = context;
    }

    /**
     * Gets the component context.
     * 
     * @return the component context
     * 
     * @see org.eclipse.swordfish.core.components.jbi.ComponentContextAccess#getComponentContext()
     */
    private ComponentContext getComponentContext() {
        return this.ctx;
    }
}
