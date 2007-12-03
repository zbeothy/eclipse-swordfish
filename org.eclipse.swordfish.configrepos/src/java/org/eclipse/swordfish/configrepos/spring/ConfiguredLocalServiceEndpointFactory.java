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
package org.eclipse.swordfish.configrepos.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.wsdl.Port;
import javax.wsdl.extensions.ExtensibilityElement;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.eclipse.swordfish.configrepos.dao.LocalEndpoint;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddress;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddressImpl;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.w3c.dom.Element;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;

/**
 * The Class ConfiguredLocalServiceEndpointFactory.
 * 
 */
public class ConfiguredLocalServiceEndpointFactory extends AbstractFactoryBean {

    /** The Constant urlPattern. */
    private static final String URL_PATTERN = "jms:/(.*)\\?(.*)";

    /** The Constant p. */
    private static final Pattern COMPILED_PATTERN = Pattern.compile(URL_PATTERN);

    /** The service base path. */
    private String serviceBasePath = null;

    /** The callback base path. */
    private String callbackBasePath = null;

    /** The configuration. */
    private Configuration configuration = null;

    /**
     * Instantiates a new configured local service endpoint factory.
     */
    public ConfiguredLocalServiceEndpointFactory() {
        super();
    }

    /**
     * Gets the callback base path.
     * 
     * @return the callback base path
     */
    public String getCallbackBasePath() {
        return this.callbackBasePath;
    }

    /**
     * Gets the configuration.
     * 
     * @return Returns the configuration.
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the object type
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        return java.util.List.class;
    }

    /**
     * Gets the service base path.
     * 
     * @return Returns the basePath.
     */
    public String getServiceBasePath() {
        return this.serviceBasePath;
    }

    /**
     * Sets the callback base path.
     * 
     * @param callbackBasePath
     *        the callback base path to set.
     */
    public void setCallbackBasePath(final String callbackBasePath) {
        this.callbackBasePath = callbackBasePath;
    }

    /**
     * Sets the configuration.
     * 
     * @param configuration
     *        The configuration to set.
     */
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Sets the service base path.
     * 
     * @param basePath
     *        The basePath to set.
     */
    public void setServiceBasePath(final String basePath) {
        this.serviceBasePath = basePath;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the object
     * 
     * @throws Exception
     *         the exception
     * 
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
     */
    @Override
    protected Object createInstance() throws Exception {
        ArrayList result = this.collectServiceInstances();
        result.addAll(this.collectCallbackInstances());
        return result;
    }

    /**
     * Collect callback instances.
     * 
     * @return the collection
     */
    private Collection collectCallbackInstances() {
        JXPathContext ctx = JXPathContext.newContext(((XMLConfiguration) this.configuration).getDocument());
        ArrayList result = new ArrayList();
        try {
            Iterator iter = ctx.selectNodes(this.callbackBasePath).iterator();

            while (iter.hasNext()) {
                Element node = (Element) iter.next();
                String portTypeName = node.getAttribute("portTypeName");
                String namespace = node.getAttribute("namespace");

                LocalEndpoint ep = new LocalEndpoint(namespace, portTypeName + "__service", true);

                String anchor =
                        this.callbackBasePath + "[./attribute::portTypeName='" + portTypeName + "' and attribute::namespace='"
                                + namespace + "']";
                Iterator piter = ctx.iterate(anchor + "/Port/attribute::name");
                while (piter.hasNext()) {
                    String portName = (String) piter.next();
                    Port prt = new PortImpl();
                    prt.setName(portName);

                    List lst = ctx.selectNodes(anchor + "/Port[./attribute::name='" + portName + "']/JmsAddress");
                    if (!lst.isEmpty()) {
                        prt.addExtensibilityElement(this.compileJMSAddress((Element) lst.get(0)));
                    } else {
                        lst = ctx.selectNodes(anchor + "/Port[./attribute::name='" + portName + "']/Address");
                        if (!lst.isEmpty()) {
                            prt.addExtensibilityElement(this.compileURIAddress((Element) lst.get(0)));
                        }
                    }
                    ep.getService().addPort(prt);
                }
                result.add(ep);
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Runtime exception parsing callback endpoint definition:" + e.getMessage());
        }
        return result;
    }

    /**
     * Collect service instances.
     * 
     * @return the array list
     */
    private ArrayList collectServiceInstances() {
        JXPathContext ctx = JXPathContext.newContext(((XMLConfiguration) this.configuration).getDocument());
        ArrayList result = new ArrayList();
        try {
            Iterator iter = ctx.selectNodes(this.serviceBasePath).iterator();

            while (iter.hasNext()) {
                Element node = (Element) iter.next();
                String serviceName = node.getAttribute("name");
                String namespace = node.getAttribute("namespace");

                LocalEndpoint ep = new LocalEndpoint(namespace, serviceName, false);

                String anchor =
                        this.serviceBasePath + "[./attribute::name='" + serviceName + "' and attribute::namespace='" + namespace
                                + "']";

                Iterator piter = ctx.iterate(anchor + "/Port/attribute::name");
                while (piter.hasNext()) {
                    String portName = (String) piter.next();
                    Port prt = new PortImpl();
                    prt.setName(portName);

                    List lst = ctx.selectNodes(anchor + "/Port[./attribute::name='" + portName + "']/JmsAddress");
                    if (!lst.isEmpty()) {
                        prt.addExtensibilityElement(this.compileJMSAddress((Element) lst.get(0)));
                    } else {
                        lst = ctx.selectNodes(anchor + "/Port[./attribute::name='" + portName + "']/Address");
                        if (!lst.isEmpty()) {
                            prt.addExtensibilityElement(this.compileURIAddress((Element) lst.get(0)));
                        }
                    }
                    ep.getService().addPort(prt);
                }
                result.add(ep);
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Runtime exception parsing service endpoint definition:" + e.getMessage());
        }
        return result;
    }

    /**
     * Compile JMS address.
     * 
     * @param address
     *        the address
     * 
     * @return the extensibility element
     */
    private ExtensibilityElement compileJMSAddress(final Element address) {
        JXPathContext ctx = JXPathContext.newContext(address);
        JMSAddress jmsaddress = new JMSAddressImpl();
        jmsaddress.setInitialContextFactory(this.getValueOf(ctx, "/initialContextFactory"));
        jmsaddress.setJndiProviderURL(this.getValueOf(ctx, "/jndiProviderURL"));
        jmsaddress.setJndiConnectionFactoryName(this.getValueOf(ctx, "/jndiConnectionFactoryName"));
        jmsaddress.setJndiDestinationName(this.getValueOf(ctx, "/jndiDestinationName"));
        jmsaddress.setDestinationStyle(this.getValueOf(ctx, "/destinationStyle"));
        jmsaddress.setIsSharedDestination(this.getValueOf(ctx, "/isSharedDestination") == null ? "false" : this.getValueOf(ctx,
                "/isSharedDestination"));
        jmsaddress.setDestinationUser(this.getValueOf(ctx, "/JMSUser/UserID"));
        jmsaddress.setDestinationPassword(this.getValueOf(ctx, "/JMSUser/Password"));

        // deal all known jndi properties
        Iterator iter = ctx.iterate("/jndiProperties/property/@name");
        while (iter.hasNext()) {
            String name = (String) iter.next();
            String value = (String) ctx.getValue("/jndiProperties/property[./attribute::name='" + name + "']");

            if ("java.naming.factory.initial".equalsIgnoreCase(name)) {
                jmsaddress.setInitialContextFactory(value);
            }
            if ("java.naming.provider.url".equalsIgnoreCase(name)) {
                jmsaddress.setJndiProviderURL(value);
            }
            if ("ConnectionFactoryJNDIName".equalsIgnoreCase(name)) {
                jmsaddress.setJndiConnectionFactoryName(value);
            }
            if ("java.naming.security.principal".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingSecurityPrincipal(value);
            }
            if ("java.naming.security.credentials".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingSecurityCredentials(value);
            }
            if ("java.naming.factory.object".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingFactoryObject(value);
            }
            if ("java.naming.security.authentication".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingSecurityAuthentication(value);
            }
            if ("java.naming.security.protocol".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingSecurityProtocol(value);
            }
            if ("java.naming.referral".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingReferral(value);
            }
            if ("java.naming.language".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingLanguage(value);
            }
            if ("java.naming.factory.state".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingFactoryState(value);
            }
            if ("java.naming.dns.url".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingDNSURL(value);
            }
            if ("java.naming.authoritative".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingAuthoritative(value);
            }
            if ("java.naming.batchsize".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingBatchsize(value);
            }
            if ("java.naming.applet".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingApplet(value);
            }
            if ("java.naming.factory.url.pkgs".equalsIgnoreCase(name)) {
                jmsaddress.setJavaNamingFactoryURLPKGS(value);
            }
        }

        return jmsaddress;
    }

    /**
     * Compile URI address.
     * 
     * @param address
     *        the address
     * 
     * @return the extensibility element
     */
    private ExtensibilityElement compileURIAddress(final Element address) {
        JXPathContext ctx = JXPathContext.newContext(address);
        String uriLocation = (String) ctx.getValue("/");
        if (uriLocation.startsWith("jms"))
            return this.createJmsAdressFromUri(uriLocation);
        else {
            SOAPAddressImpl soapAddress = new SOAPAddressImpl();
            soapAddress.setLocationURI(uriLocation);
            return soapAddress;
        }
    }

    /**
     * Creates a new ConfiguredLocalServiceEndpoint object.
     * 
     * @param uri
     *        the uri
     * 
     * @return the JMS address
     */
    private JMSAddress createJmsAdressFromUri(final String uri) {
        JMSAddress address = new JMSAddressImpl();
        Matcher m = COMPILED_PATTERN.matcher(uri);
        if (m.matches()) {
            address.setJndiDestinationName(m.group(1));
            String attributes[] = m.group(2).split("&");
            for (int i = 0; i < attributes.length; i++) {
                String name = attributes[i].substring(0, attributes[i].indexOf("="));
                String value = attributes[i].substring(attributes[i].indexOf("=") + 1);
                if ("java.naming.factory.initial".equalsIgnoreCase(name)) {
                    address.setInitialContextFactory(value);
                }
                if ("java.naming.provider.url".equalsIgnoreCase(name)) {
                    address.setJndiProviderURL(value);
                }
                if ("ConnectionFactoryJNDIName".equalsIgnoreCase(name)) {
                    address.setJndiConnectionFactoryName(value);
                }
                if ("java.naming.provider.user".equalsIgnoreCase(name)) {
                    address.setJavaNamingSecurityPrincipal(value);
                }
                if ("java.naming.provider.password".equalsIgnoreCase(name)) {
                    address.setJavaNamingSecurityCredentials(value);
                }
            }
        }
        return address;
    }

    /**
     * Gets the value of.
     * 
     * @param ctx
     *        the ctx
     * @param path
     *        the path
     * 
     * @return the value of
     */
    private String getValueOf(final JXPathContext ctx, final String path) {
        try {
            return (String) ctx.getValue(path);
        } catch (JXPathException e) {
            return null;
        }
    }

}
