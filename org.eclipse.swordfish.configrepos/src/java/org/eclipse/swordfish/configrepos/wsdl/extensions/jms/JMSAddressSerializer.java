/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.configrepos.wsdl.extensions.jms;

import java.io.PrintWriter;
import java.io.Serializable;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import com.ibm.wsdl.Constants;
import com.ibm.wsdl.util.xml.DOMUtils;

public class JMSAddressSerializer implements ExtensionSerializer, ExtensionDeserializer, Serializable {

    public static final long serialVersionUID = 1L;

    public JMSAddressSerializer() {
    }

    public void marshall(final Class parentType, final QName elementType, final ExtensibilityElement extension,
            final PrintWriter pw, final Definition def, final ExtensionRegistry extReg) throws WSDLException {
        JMSAddress jmsAddress = (JMSAddress) extension;
        if (jmsAddress != null) {
            String tagName =
                    DOMUtils.getQualifiedValue("http://schemas.iona.com/transports/jms", JMSConstants.Q_ELEM_JMS_ADDRESS
                        .getLocalPart(), def);
            pw.print("      <" + tagName);
            DOMUtils.printAttribute("destinationStyle", jmsAddress.getDestinationStyle(), pw);
            DOMUtils.printAttribute("durableSubscriberName", jmsAddress.getDurableSubscriberName(), pw);
            DOMUtils.printAttribute("initialContextFactory", jmsAddress.getInitialContextFactory(), pw);
            DOMUtils.printAttribute("java.naming.applet", jmsAddress.getJavaNamingApplet(), pw);
            DOMUtils.printAttribute("java.naming.authoritative", jmsAddress.getJavaNamingAuthoritative(), pw);
            DOMUtils.printAttribute("java.naming.batchsize", jmsAddress.getJavaNamingBatchsize(), pw);
            DOMUtils.printAttribute("java.naming.dns.url", jmsAddress.getJavaNamingDNSURL(), pw);
            DOMUtils.printAttribute("java.naming.factory.initial", jmsAddress.getJavaNamingFactoryInitial(), pw);
            DOMUtils.printAttribute("java.naming.factory.object", jmsAddress.getJavaNamingFactoryObject(), pw);
            DOMUtils.printAttribute("java.naming.factory.state", jmsAddress.getJavaNamingFactoryState(), pw);
            DOMUtils.printAttribute("java.naming.factory.url.pkgs", jmsAddress.getJavaNamingFactoryURLPKGS(), pw);
            DOMUtils.printAttribute("java.naming.language", jmsAddress.getJavaNamingLanguage(), pw);
            DOMUtils.printAttribute("java.naming.provider.url", jmsAddress.getJavaNamingProviderURL(), pw);
            DOMUtils.printAttribute("java.naming.referral", jmsAddress.getJavaNamingReferral(), pw);
            DOMUtils.printAttribute("java.naming.security.authentication", jmsAddress.getJavaNamingSecurityAuthentication(), pw);
            DOMUtils.printAttribute("java.naming.security.credentials", jmsAddress.getJavaNamingSecurityCredentials(), pw);
            DOMUtils.printAttribute("java.naming.security.principal", jmsAddress.getJavaNamingSecurityPrincipal(), pw);
            DOMUtils.printAttribute("java.naming.security.protocol", jmsAddress.getJavaNamingSecurityProtocol(), pw);
            DOMUtils.printAttribute("jndiConnectionFactoryName", jmsAddress.getJndiConnectionFactoryName(), pw);
            DOMUtils.printAttribute("jndiDestinationName", jmsAddress.getJndiDestinationName(), pw);
            DOMUtils.printAttribute("jndiProviderURL", jmsAddress.getJndiProviderURL(), pw);
            DOMUtils.printAttribute("messageSelector", jmsAddress.getMessageSelector(), pw);
            DOMUtils.printAttribute("messageType", jmsAddress.getMessageType(), pw);
            DOMUtils.printAttribute("useMessageIDAsCorrelationID", jmsAddress.getUseMessageIDAsCorrelationID(), pw);
            // extensions
            DOMUtils.printAttribute("isSharedDestination", jmsAddress.getIsSharedDestination(), pw);
            DOMUtils.printAttribute("UserID", jmsAddress.getDestinationUser(), pw);
            DOMUtils.printAttribute("Password", jmsAddress.getDestinationPassword(), pw);

            Boolean required = jmsAddress.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(final Class parentType, final QName elementType, final Element el, final Definition def,
            final ExtensionRegistry extReg) throws WSDLException {
        JMSAddress jmsAddress = (JMSAddress) extReg.createExtension(parentType, elementType);
        jmsAddress.setElement(el);
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (requiredStr != null) {
            jmsAddress.setRequired(new Boolean(requiredStr));
        }
        jmsAddress.setDestinationStyle(DOMUtils.getAttribute(el, "destinationStyle"));
        jmsAddress.setDurableSubscriberName(DOMUtils.getAttribute(el, "durableSubscriberName"));
        jmsAddress.setInitialContextFactory(DOMUtils.getAttribute(el, "initialContextFactory"));
        jmsAddress.setJavaNamingApplet(DOMUtils.getAttribute(el, "java.naming.applet"));
        jmsAddress.setJavaNamingAuthoritative(DOMUtils.getAttribute(el, "java.naming.authoritative"));
        jmsAddress.setJavaNamingBatchsize(DOMUtils.getAttribute(el, "java.naming.batchsize"));
        jmsAddress.setJavaNamingDNSURL(DOMUtils.getAttribute(el, "java.naming.dns.url"));
        jmsAddress.setJavaNamingFactoryInitial(DOMUtils.getAttribute(el, "java.naming.factory.initial"));
        jmsAddress.setJavaNamingFactoryObject(DOMUtils.getAttribute(el, "java.naming.factory.object"));
        jmsAddress.setJavaNamingFactoryState(DOMUtils.getAttribute(el, "java.naming.factory.state"));
        jmsAddress.setJavaNamingFactoryURLPKGS(DOMUtils.getAttribute(el, "java.naming.factory.url.pkgs"));
        jmsAddress.setJavaNamingLanguage(DOMUtils.getAttribute(el, "java.naming.language"));
        jmsAddress.setJavaNamingProviderURL(DOMUtils.getAttribute(el, "java.naming.provider.url"));
        jmsAddress.setJavaNamingReferral(DOMUtils.getAttribute(el, "java.naming.referral"));
        jmsAddress.setJavaNamingSecurityAuthentication(DOMUtils.getAttribute(el, "java.naming.security.authentication"));
        jmsAddress.setJavaNamingSecurityCredentials(DOMUtils.getAttribute(el, "java.naming.security.credentials"));
        jmsAddress.setJavaNamingSecurityPrincipal(DOMUtils.getAttribute(el, "java.naming.security.principal"));
        jmsAddress.setJavaNamingSecurityProtocol(DOMUtils.getAttribute(el, "java.naming.security.protocol"));
        jmsAddress.setJndiConnectionFactoryName(DOMUtils.getAttribute(el, "jndiConnectionFactoryName"));
        jmsAddress.setJndiDestinationName(DOMUtils.getAttribute(el, "jndiDestinationName"));
        jmsAddress.setJndiProviderURL(DOMUtils.getAttribute(el, "jndiProviderURL"));
        jmsAddress.setMessageSelector(DOMUtils.getAttribute(el, "messageSelector"));
        jmsAddress.setMessageType(DOMUtils.getAttribute(el, "messageType"));
        jmsAddress.setUseMessageIDAsCorrelationID(DOMUtils.getAttribute(el, "useMessageIDAsCorrelationID"));

        // extensions
        jmsAddress.setIsSharedDestination(DOMUtils.getAttribute(el, "isSharedDestination"));
        jmsAddress.setDestinationUser(DOMUtils.getAttribute(el, "UserID"));
        jmsAddress.setDestinationPassword(DOMUtils.getAttribute(el, "Password"));

        return jmsAddress;
    }
}
