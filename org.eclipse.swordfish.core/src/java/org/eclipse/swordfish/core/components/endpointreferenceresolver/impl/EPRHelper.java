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
package org.eclipse.swordfish.core.components.endpointreferenceresolver.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddress;
import org.eclipse.swordfish.core.components.headerprocessing.impl.Constants;
import org.eclipse.swordfish.core.components.headerprocessing.impl.JDOMUtil;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.SAXException;

/**
 * The Class EPRHelper.
 */
public class EPRHelper {

    /** The util. */
    static private JDOMUtil util = new JDOMUtil();

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(EPRHelper.class);

    /**
     * Creates the B c_ EPR.
     * 
     * @param address
     *        the address
     * 
     * @return the document fragment
     */
    static public DocumentFragment createBcEPR(final String address) {

        try {
            Namespace.getNamespace("soap", HeaderUtil.SOAP_NS);
            Namespace wsa = Namespace.getNamespace("wsa", Constants.WSA_NS);
            Document doc = new Document();
            Element endp = new Element("EndpointReference", wsa);
            endp.addNamespaceDeclaration(wsa);
            Element addr = new Element("Address", wsa);
            addr.setText(address);
            endp.addContent(addr);
            doc.addContent(endp);
            return util.buildDocumentFragment(doc);
        } catch (SAXException e) {
            LOG.error("Problems creating endpoint references", e);
        } catch (IOException e) {
            LOG.error("Problems creating endpoint references", e);
        }
        return null;
    }

    /*
     * create a JMS adress URI understandable by the BC
     */
    /**
     * Creates the JMS address URI.
     * 
     * @param address
     *        the address
     * 
     * @return the string
     */
    public static String createJMSAddressURI(final JMSAddress address) {
        if (null == address) return null;
        String addressURI;
        if ("org.eclipse.swordfish.naming.SOPInitialContextFactory".equals(address.getInitialContextFactory())) {
            String dest = "";
            String cftype = "";
            String cf = "";
            if ("queue".equalsIgnoreCase(address.getDestinationStyle())) {
                dest = "QueueDestination/";
                cftype = "QueueConnectionFactory/";
            } else {
                dest = "TopicDestination/";
                cftype = "TopicConnectionFactory/";
            }
            if ((address.getJndiConnectionFactoryName() == null) || "".equals(address.getJndiConnectionFactoryName())) {
                cf = "arbitrary";
            } else {
                cf = address.getJndiConnectionFactoryName();
            }

            addressURI =
                    "jms:/" + dest + address.getJndiDestinationName() + "?" + "vendor=" + encode("JNDI")
                            + "&java.naming.factory.initial=" + encode(address.getInitialContextFactory())
                            + "&java.naming.provider.url=" + encode(address.getJndiProviderURL()) + "&ConnectionFactoryJNDIName="
                            + encode(cftype + cf);
        } else {
            addressURI =
                    "jms:/" + address.getJndiDestinationName() + "?" + "vendor=" + encode("JNDI") + "&java.naming.factory.initial="
                            + encode(address.getInitialContextFactory()) + "&java.naming.provider.url="
                            + encode(address.getJndiProviderURL()) + "&ConnectionFactoryJNDIName="
                            + encode(address.getJndiConnectionFactoryName());
        }

        if (address.getJavaNamingSecurityPrincipal() != null) {
            addressURI = addressURI + "&java.naming.provider.user=" + address.getJavaNamingSecurityPrincipal();
        }
        if (address.getJavaNamingSecurityPrincipal() != null) {
            addressURI = addressURI + "&java.naming.provider.password=" + address.getJavaNamingSecurityCredentials();
        }
        if (address.getDestinationUser() != null) {
            addressURI = addressURI + "&;jms-user=" + address.getDestinationUser();
        }
        if (address.getDestinationPassword() != null) {
            addressURI = addressURI + "&;jms-password=" + address.getDestinationPassword();
        }
        return addressURI;
    }

    /*
     * TODO Clean up this code once it works properly with the locator
     */
    /**
     * Creates the locator_ EPR.
     * 
     * @param asd
     *        the asd
     * @param port
     *        the port
     * 
     * @return the document fragment
     */
    public static DocumentFragment createLocatorEPR(final CompoundServiceDescription asd, final SPDXPort port) {
        String str =
                "<m1:proxy xmlns:m1=\"http://jbi.iona.com/locator\">" + "<wsaw:ServiceName EndpointName=\"" + port.getName()
                        + "\" xmlns:wsaw=\"http://www.w3.org/2005/03/addressing/wsdl\" " + "xmlns:srv=\""
                        + asd.getServiceQName().getNamespaceURI() + "\">" + "srv:" + asd.getServiceQName().getLocalPart()
                        + "</wsaw:ServiceName>" + "<m1:ClusterList>";
        List ls = port.getLocatorClusterLocations();
        for (int i = 0; i < ls.size(); i++) {
            str = str + "<m1:Cluster name=\"" + (String) ls.get(i) + "\"/>";
        }
        str = str + "</m1:ClusterList> </m1:proxy>";
        org.w3c.dom.Document doc = TransformerUtil.docFromString(str);
        org.w3c.dom.DocumentFragment frag = doc.createDocumentFragment();
        frag.appendChild(doc.getDocumentElement());
        return frag;
    }

    /**
     * Gets the port.
     * 
     * @param eprdf
     *        the eprdf
     * 
     * @return the port
     */
    public static String getPort(final DocumentFragment eprdf) {
        Element epr = util.fragmentToElement(eprdf);
        Namespace.getNamespace("wsa", Constants.WSA_NS);
        Namespace wsaw = Namespace.getNamespace("wsaw", Constants.WSAW_NS);
        String endpointName = null;
        Element metadata = epr.getChild("Metadata", Namespace.getNamespace(Constants.WSA_NS));
        if (metadata != null) {
            Element itf = metadata.getChild("ServiceName", wsaw);
            if (itf != null) {
                String name = itf.getText();
                int index = name.indexOf(":");
                if (index > 0) {
                    name.substring(0, index);
                    name = name.substring(index + 1, name.length());
                }
                endpointName = itf.getAttributeValue("EndpointName");
            }
        }
        return endpointName;
    }

    /**
     * Gets the service.
     * 
     * @param eprdf
     *        the eprdf
     * 
     * @return the service
     */
    public static QName getService(final DocumentFragment eprdf) {
        Element epr = util.fragmentToElement(eprdf);

        Namespace.getNamespace("wsa", Constants.WSA_NS);
        Namespace wsaw = Namespace.getNamespace("wsaw", Constants.WSAW_NS);
        QName serviceQname = null;
        Element metadata = epr.getChild("Metadata", Namespace.getNamespace(Constants.WSA_NS));
        if (metadata != null) {
            Element itf = metadata.getChild("ServiceName", wsaw);
            if (itf != null) {
                String name = itf.getText();
                String prefix = null;
                int index = name.indexOf(":");
                if (index > 0) {
                    prefix = name.substring(0, index);
                    name = name.substring(index + 1, name.length());
                }
                serviceQname = new QName(itf.getNamespace(prefix).getURI(), name);
            }
        }
        return serviceQname;
    }

    /**
     * Encode.
     * 
     * @param str
     *        the str
     * 
     * @return the string
     */
    private static String encode(final String str) {
        String encodedURL = null;
        try {
            encodedURL = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("while encoding a JMS address:", e);
        }
        return encodedURL;
    }
}
