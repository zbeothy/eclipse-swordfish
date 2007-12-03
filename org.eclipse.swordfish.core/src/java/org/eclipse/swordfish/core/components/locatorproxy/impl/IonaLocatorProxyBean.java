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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.w3c.dom.DocumentFragment;

/**
 * The Class IonaLocatorProxyBean.
 */
public class IonaLocatorProxyBean extends AbstractLocatorProxy {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(IonaLocatorProxyBean.class);

    /** The locator M bean name. */
    private String locatorMBeanName;

    /** The mbean server. */
    private MBeanServer mbeanServer;

    /** The iona ON. */
    private ObjectName ionaON;

    /** The deployments. */
    private Map deployments;

    /** The inited. */
    private boolean inited;

    /**
     * Instantiates a new iona locator proxy bean.
     */
    public IonaLocatorProxyBean() {
        this.inited = false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.impl.AbstractLocatorProxy#deploy(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void deploy(final String locatorId, final String configuration) throws InternalInfrastructureException {
        if (this.isActive()) {
            if (!this.inited) {
                this.initDeployment();
            }
            if (!this.deployments.containsKey(locatorId)) {
                this.deployRecource(locatorId, configuration);
                this.deployments.put(locatorId, new Integer(1));
            } else {
                int count = ((Integer) this.deployments.get(locatorId)).intValue();
                this.deployments.put(locatorId, new Integer(count + 1));
            }
        }
    }

    /**
     * destroy method.
     */
    public void destroy() {
        this.mbeanServer = null;
        this.ionaON = null;
        if (this.deployments != null) {
            this.deployments.clear();
            this.deployments = null;
        }
    }

    /**
     * Gets the locator M bean name.
     * 
     * @return the locator M bean name
     */
    public String getLocatorMBeanName() {
        return this.locatorMBeanName;
    }

    /**
     * Init.
     */
    public void init() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.impl.AbstractLocatorProxy#register(java.lang.String,
     *      javax.xml.namespace.QName, java.lang.String, org.w3c.dom.DocumentFragment)
     */
    @Override
    public void register(final String locatorId, final QName service, final String endpoint, final DocumentFragment address)
            throws InternalInfrastructureException {

        LOG.info("Registring " + service.toString() + " with endpoint " + endpoint + " at locator " + locatorId);
        if (LOG.isDebugEnabled()) {
            String str = TransformerUtil.stringFromDomNode(address);
            LOG.debug("location Address Fragment is: " + str);
        }

        try {
            this.invokeLocatorProxy("registerEndpoint", this.createRegisterEndpointMessage(locatorId, service, endpoint, address));
            LOG.info("Registration " + service.toString() + " with endpoint " + endpoint + " at locator " + locatorId
                    + " was successfull");
        } catch (InternalInfrastructureException e) {
            throw new InternalInfrastructureException("registration of " + service.toString() + " with endpoint " + endpoint
                    + " failed on locator for " + locatorId, e);
        }
    }

    /**
     * Sets the locator M bean name.
     * 
     * @param beanName
     *        the new locator M bean name
     */
    public void setLocatorMBeanName(final String beanName) {
        this.locatorMBeanName = beanName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.impl.AbstractLocatorProxy#undeploy(java.lang.String)
     */
    @Override
    public void undeploy(final String locatorId) throws InternalInfrastructureException {
        if (this.isActive()) {
            if (!this.inited)
                throw new InternalInfrastructureException("You cannot undeploy a cluster configuration with locator bla bla");

            if (this.deployments.containsKey(locatorId)) {
                int count = ((Integer) this.deployments.get(locatorId)).intValue();
                count = count - 1;
                if (count == 0) {
                    this.undeployRecource(locatorId);
                    this.deployments.remove(locatorId);
                } else {
                    this.deployments.put(locatorId, new Integer(count));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.impl.AbstractLocatorProxy#unregister(java.lang.String,
     *      javax.xml.namespace.QName, java.lang.String)
     */
    @Override
    public void unregister(final String locatorId, final QName service, final String endpoint)
            throws InternalInfrastructureException {
        LOG.info("Unregistring " + service.toString() + " with endpoint " + endpoint + " from locator " + locatorId);

        try {
            this.invokeLocatorProxy("deregisterEndpoint", this.createUnregisterEndpointMessage(locatorId, service, endpoint));
            LOG.info("Unregistration " + service.toString() + " with endpoint " + endpoint + " from locator " + locatorId
                    + " was successfull");
        } catch (InternalInfrastructureException e) {
            throw new InternalInfrastructureException("Unregistration of " + service.toString() + " with endpoint " + endpoint
                    + " failed on locator for " + locatorId, e);
        }
    }

    /**
     * Creates the register endpoint message.
     * 
     * @param locatorId
     *        the locator id
     * @param service
     *        the service
     * @param endpoint
     *        the endpoint
     * @param address
     *        the address
     * 
     * @return the string
     */
    private String createRegisterEndpointMessage(final String locatorId, final QName service, final String endpoint,
            final DocumentFragment address) {
        String str =
                "<m1:registerEndpoint xmlns:m1=\"http://jbi.iona.com/locator\">"
                        + "<wsaw:ServiceName xmlns:wsaw=\"http://www.w3.org/2005/03/addressing/wsdl\" " + "xmlns:sns=\""
                        + service.getNamespaceURI() + "\">sns:" + service.getLocalPart() + "</wsaw:ServiceName>" + "<m1:Endpoints>"
                        + "<m1:Endpoint EndpointName=\"" + endpoint + "\">" + TransformerUtil.stringFromDomNode(address)
                        + "</m1:Endpoint>" + "</m1:Endpoints>" + "<m1:Cluster name=\"" + locatorId + "\" />"
                        + "</m1:registerEndpoint>";
        return str;
    }

    /**
     * Creates the unregister endpoint message.
     * 
     * @param locatorId
     *        the locator id
     * @param service
     *        the service
     * @param endpoint
     *        the endpoint
     * 
     * @return the string
     */
    private String createUnregisterEndpointMessage(final String locatorId, final QName service, final String endpoint) {
        String str =
                "<m1:deregisterEndpoint xmlns:m1=\"http://jbi.iona.com/locator\">" + "<wsaw:ServiceName EndpointName=\"" + endpoint
                        + "\" xmlns:wsaw=\"http://www.w3.org/2005/03/addressing/wsdl\" " + "xmlns:sns=\""
                        + service.getNamespaceURI() + "\">sns:" + service.getLocalPart() + "</wsaw:ServiceName>"
                        + "<m1:Cluster name=\"" + locatorId + "\" />" + "</m1:deregisterEndpoint>";
        return str;
    }

    /**
     * _deploy.
     * 
     * @param locatorId
     *        the locator id
     * @param configuration
     *        the configuration
     * 
     * @throws InternalInfrastructureException
     */
    private void deployRecource(final String locatorId, final String configuration) throws InternalInfrastructureException {
        try {
            LOG.debug("deploying to cluster " + locatorId + ":\n" + configuration);
            this.lookupMBeanServer().invoke(this.ionaON, "addCluster", new Object[] {configuration},
                    new String[] {String.class.getName()});
        } catch (InstanceNotFoundException e) {
            throw new InternalInfrastructureException("locator-proxy service engine not accessible using "
                    + this.getLocatorMBeanName(), e);
        } catch (MBeanException e) {
            throw new InternalInfrastructureException("could not invoke 'addCluster' on locator-proxy service engine", e);
        } catch (ReflectionException e) {
            throw new InternalInfrastructureException("could not invoke 'addCluster' on locator-proxy service engine", e);
        }
    }

    /**
     * ugly ugly :-).
     * 
     * @return the xml infoset that the locator proxy expects as its initial deployment
     */
    private String getInitialDeploymentString() {
        String str =
                "<?xml version='1.0' encoding='utf-8'?>" + "<LocatorSeConfig>" + "<LocatorEndpoint EndpointName=\""
                        + this.getEndpointName() + "\"" + " xmlns:ns1=\"" + this.getServiceNS() + "\">" + "ns1:"
                        + this.getServiceName() + "</LocatorEndpoint>" + "<Base_Id>" + Runtime.getRuntime().hashCode()
                        + "</Base_Id>" + "<Element_Id>SOP</Element_Id>" + "<ArtixClusterList>" + "</ArtixClusterList>"
                        + "</LocatorSeConfig>";
        return str;
    }

    /**
     * _init deployment.
     * 
     * @throws InternalInfrastructureException
     */
    private void initDeployment() throws InternalInfrastructureException {
        try {
            if (this.isActive()) {
                this.deployments = new HashMap();
                try {
                    this.ionaON = new ObjectName(this.getLocatorMBeanName());
                    LOG.debug("initializing locator with:\n" + this.getInitialDeploymentString());
                    this.mbeanServer = this.lookupMBeanServer();
                    this.mbeanServer.invoke(this.ionaON, "deploy", new Object[] {this.getInitialDeploymentString()},
                            new String[] {String.class.getName()});
                    this.inited = true;
                } catch (MalformedObjectNameException e) {
                    throw new RuntimeException("bad locator proxy configuration. MBean name " + this.getLocatorMBeanName()
                            + " is malformed.", e);
                }
            }
        } catch (InstanceNotFoundException e) {
            throw new InternalInfrastructureException("locator-proxy service engine not accessible using "
                    + this.getLocatorMBeanName(), e);
        } catch (MBeanException e) {
            throw new InternalInfrastructureException("could not invoke 'deploy' on locator-proxy service engine", e);
        } catch (ReflectionException e) {
            throw new InternalInfrastructureException("could not invoke 'deploy' on locator-proxy service engine", e);
        } catch (Exception e) {
            throw new RuntimeException("invoking initial deployment of " + this.getInitialDeploymentString() + " failed with.", e);
        }

    }

    /**
     * Lookup M bean server.
     * 
     * @return the MBeanServer that has the iona MBean registered
     */
    private MBeanServer lookupMBeanServer() {
        if (this.mbeanServer != null)
            return this.mbeanServer;
        else {
            ArrayList servers = MBeanServerFactory.findMBeanServer(null);
            boolean registeredBeanFound = false;
            MBeanServer server = null;
            if (servers.size() > 0) {
                for (int i = 0; i < servers.size(); i++) {
                    server = (MBeanServer) servers.get(i);
                    if (server.isRegistered(this.ionaON)) {
                        registeredBeanFound = true;
                        break;
                    }
                }
                if (!registeredBeanFound)
                    throw new RuntimeException(
                            "No locator-proxy service engine is installed or the MBean name to access it is wrong.");
                this.mbeanServer = server;
                return server;
            } else
                throw new RuntimeException("No MBeanServer foung to access the locator-proxy service engine");
        }
    }

    /**
     * _undeploy.
     * 
     * @param locatorId
     *        the locator id
     * 
     * @throws InternalInfrastructureException
     */
    private void undeployRecource(final String locatorId) throws InternalInfrastructureException {
        try {
            LOG.debug("undeploying cluster " + locatorId + ".");
            this.lookupMBeanServer().invoke(this.ionaON, "removeCluster", new Object[] {locatorId},
                    new String[] {String.class.getName()});
        } catch (InstanceNotFoundException e) {
            throw new InternalInfrastructureException("locator-proxy service engine not accessible using "
                    + this.getLocatorMBeanName(), e);
        } catch (MBeanException e) {
            throw new InternalInfrastructureException("could not invoke 'removeCluster' on locator-proxy service engine", e);
        } catch (ReflectionException e) {
            throw new InternalInfrastructureException("could not invoke 'removeCluster' on locator-proxy service engine", e);
        }
    }
}
