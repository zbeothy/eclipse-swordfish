/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.configrepos.wsdl.extensions.jms;

import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class JMSAddressImpl implements JMSAddress {

    public static final long serialVersionUID = 1L;

    protected QName elementType;

    protected Boolean required;

    protected String destinationStyle;

    protected String jndiProviderURL;

    protected String initialContextFactory;

    protected String jndiConnectionFactoryName;

    protected String jndiDestinationName;

    protected String messageType;

    protected String durableSubscriberName;

    protected String messageSelector;

    protected String useMessageIDAsCorrelationID;

    protected String javaNamingFactoryInitial;

    protected String javaNamingProviderURL;

    protected String javaNamingFactoryObject;

    protected String javaNamingFactoryState;

    protected String javaNamingFactoryURLPKGS;

    protected String javaNamingDNSURL;

    protected String javaNamingAuthoritative;

    protected String javaNamingBatchsize;

    protected String javaNamingReferral;

    protected String javaNamingSecurityProtocol;

    protected String javaNamingSecurityAuthentication;

    protected String javaNamingSecurityPrincipal;

    protected String javaNamingSecurityCredentials;

    protected String javaNamingLanguage;

    protected String javaNamingApplet;

    protected Element element;

    // extension
    protected String isSharedDestination;

    protected String userId;

    protected String password;

    public JMSAddressImpl() {
        this.elementType = JMSConstants.Q_ELEM_JMS_ADDRESS;
        this.required = null;
        this.destinationStyle = null;
        this.jndiProviderURL = null;
        this.initialContextFactory = null;
        this.jndiConnectionFactoryName = null;
        this.jndiDestinationName = null;
        this.messageType = null;
        this.durableSubscriberName = null;
        this.messageSelector = null;
        this.useMessageIDAsCorrelationID = null;
        this.javaNamingFactoryInitial = null;
        this.javaNamingProviderURL = null;
        this.javaNamingFactoryObject = null;
        this.javaNamingFactoryState = null;
        this.javaNamingFactoryURLPKGS = null;
        this.javaNamingDNSURL = null;
        this.javaNamingAuthoritative = null;
        this.javaNamingBatchsize = null;
        this.javaNamingReferral = null;
        this.javaNamingSecurityProtocol = null;
        this.javaNamingSecurityAuthentication = null;
        this.javaNamingSecurityPrincipal = null;
        this.javaNamingSecurityCredentials = null;
        this.javaNamingLanguage = null;
        this.javaNamingApplet = null;

        // extension
        this.isSharedDestination = null;
        this.userId = null;
        this.password = null;

    }

    public String getDestinationPassword() {
        return this.password;
    }

    public String getDestinationStyle() {
        return this.destinationStyle;
    }

    public String getDestinationUser() {
        return this.userId;
    }

    public String getDurableSubscriberName() {
        return this.durableSubscriberName;
    }

    public Element getElement() {
        return this.element;
    }

    public QName getElementType() {
        return this.elementType;
    }

    public String getInitialContextFactory() {
        return this.initialContextFactory;
    }

    public String getIsSharedDestination() {
        return this.isSharedDestination;
    }

    public String getJavaNamingApplet() {
        return this.javaNamingApplet;
    }

    public String getJavaNamingAuthoritative() {
        return this.javaNamingAuthoritative;
    }

    public String getJavaNamingBatchsize() {
        return this.javaNamingBatchsize;
    }

    public String getJavaNamingDNSURL() {
        return this.javaNamingDNSURL;
    }

    public String getJavaNamingFactoryInitial() {
        return this.javaNamingFactoryInitial;
    }

    public String getJavaNamingFactoryObject() {
        return this.javaNamingFactoryObject;
    }

    public String getJavaNamingFactoryState() {
        return this.javaNamingFactoryState;
    }

    public String getJavaNamingFactoryURLPKGS() {
        return this.javaNamingFactoryURLPKGS;
    }

    public String getJavaNamingLanguage() {
        return this.javaNamingLanguage;
    }

    public String getJavaNamingProviderURL() {
        return this.javaNamingProviderURL;
    }

    public String getJavaNamingReferral() {
        return this.javaNamingReferral;
    }

    public String getJavaNamingSecurityAuthentication() {
        return this.javaNamingSecurityAuthentication;
    }

    public String getJavaNamingSecurityCredentials() {
        return this.javaNamingSecurityCredentials;
    }

    public String getJavaNamingSecurityPrincipal() {
        return this.javaNamingSecurityPrincipal;
    }

    public String getJavaNamingSecurityProtocol() {
        return this.javaNamingSecurityProtocol;
    }

    public String getJndiConnectionFactoryName() {
        return this.jndiConnectionFactoryName;
    }

    public String getJndiDestinationName() {
        return this.jndiDestinationName;
    }

    public String getJndiProviderURL() {
        return this.jndiProviderURL;
    }

    public String getMessageSelector() {
        return this.messageSelector;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public Boolean getRequired() {
        return this.required;
    }

    public String getUseMessageIDAsCorrelationID() {
        return this.useMessageIDAsCorrelationID;
    }

    public void setDestinationPassword(final String password) {
        this.password = password;

    }

    public void setDestinationStyle(final String destinationStyle) {
        this.destinationStyle = destinationStyle;
    }

    public void setDestinationUser(final String userId) {
        this.userId = userId;
    }

    public void setDurableSubscriberName(final String durableSubscriberName) {
        this.durableSubscriberName = durableSubscriberName;
    }

    public void setElement(final Element node) {
        this.element = node;
    }

    public void setElementType(final QName elementType) {
        this.elementType = elementType;
    }

    public void setInitialContextFactory(final String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    public void setIsSharedDestination(final String isSharedDestination) {
        this.isSharedDestination = isSharedDestination;
    }

    public void setJavaNamingApplet(final String javaNamingApplet) {
        this.javaNamingApplet = javaNamingApplet;
    }

    public void setJavaNamingAuthoritative(final String javaNamingAuthoritative) {
        this.javaNamingAuthoritative = javaNamingAuthoritative;
    }

    public void setJavaNamingBatchsize(final String javaNamingBatchsize) {
        this.javaNamingBatchsize = javaNamingBatchsize;
    }

    public void setJavaNamingDNSURL(final String javaNamingDNSURL) {
        this.javaNamingDNSURL = javaNamingDNSURL;
    }

    public void setJavaNamingFactoryInitial(final String javaNamingFactoryInitial) {
        this.javaNamingFactoryInitial = javaNamingFactoryInitial;
    }

    public void setJavaNamingFactoryObject(final String javaNamingFactoryObject) {
        this.javaNamingFactoryObject = javaNamingFactoryObject;
    }

    public void setJavaNamingFactoryState(final String javaNamingFactoryState) {
        this.javaNamingFactoryState = javaNamingFactoryState;
    }

    public void setJavaNamingFactoryURLPKGS(final String javaNamingFactoryURLPKGS) {
        this.javaNamingFactoryURLPKGS = javaNamingFactoryURLPKGS;
    }

    public void setJavaNamingLanguage(final String javaNamingLanguage) {
        this.javaNamingLanguage = javaNamingLanguage;
    }

    public void setJavaNamingProviderURL(final String javaNamingProviderURL) {
        this.javaNamingProviderURL = javaNamingProviderURL;
    }

    public void setJavaNamingReferral(final String javaNamingReferral) {
        this.javaNamingReferral = javaNamingReferral;
    }

    public void setJavaNamingSecurityAuthentication(final String javaNamingSecurityAuthentication) {
        this.javaNamingSecurityAuthentication = javaNamingSecurityAuthentication;
    }

    public void setJavaNamingSecurityCredentials(final String javaNamingSecurityCredentials) {
        this.javaNamingSecurityCredentials = javaNamingSecurityCredentials;
    }

    public void setJavaNamingSecurityPrincipal(final String javaNamingSecurityPrincipal) {
        this.javaNamingSecurityPrincipal = javaNamingSecurityPrincipal;
    }

    public void setJavaNamingSecurityProtocol(final String javaNamingSecurityProtocol) {
        this.javaNamingSecurityProtocol = javaNamingSecurityProtocol;
    }

    public void setJndiConnectionFactoryName(final String jndiConnectionFactoryName) {
        this.jndiConnectionFactoryName = jndiConnectionFactoryName;
    }

    public void setJndiDestinationName(final String jndiDestinationName) {
        this.jndiDestinationName = jndiDestinationName;
    }

    public void setJndiProviderURL(final String jndiProviderURL) {
        this.jndiProviderURL = jndiProviderURL;
    }

    public void setMessageSelector(final String messageSelector) {
        this.messageSelector = messageSelector;
    }

    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }

    public void setRequired(final Boolean required) {
        this.required = required;
    }

    public void setUseMessageIDAsCorrelationID(final String useMessageIDAsCorrelationID) {
        this.useMessageIDAsCorrelationID = useMessageIDAsCorrelationID;
    }

    @Override
    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("JMSAddress (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.destinationStyle != null) {
            strBuf.append("\ndestinationStyle=" + this.jndiProviderURL);
        }
        if (this.jndiProviderURL != null) {
            strBuf.append("\njndiProviderURL=" + this.jndiProviderURL);
        }
        if (this.initialContextFactory != null) {
            strBuf.append("\ninitialContextFactory=" + this.initialContextFactory);
        }
        if (this.jndiConnectionFactoryName != null) {
            strBuf.append("\njndiConnectionFactoryName=" + this.jndiConnectionFactoryName);
        }
        if (this.jndiDestinationName != null) {
            strBuf.append("\njndiDestinationName=" + this.jndiDestinationName);
        }
        if (this.messageType != null) {
            strBuf.append("\nmessageType=" + this.messageType);
        }
        if (this.durableSubscriberName != null) {
            strBuf.append("\ndurableSubscriberName=" + this.durableSubscriberName);
        }
        if (this.useMessageIDAsCorrelationID != null) {
            strBuf.append("\nuseMessageIDAsCorrelationID=" + this.useMessageIDAsCorrelationID);
        }
        if (this.javaNamingFactoryInitial != null) {
            strBuf.append("\njavaNamingFactoryInitial=" + this.javaNamingFactoryInitial);
        }
        if (this.javaNamingProviderURL != null) {
            strBuf.append("\njavaNamingProviderURL=" + this.javaNamingProviderURL);
        }
        if (this.javaNamingFactoryObject != null) {
            strBuf.append("\njavaNamingFactoryObject=" + this.javaNamingFactoryObject);
        }
        if (this.javaNamingFactoryState != null) {
            strBuf.append("\njavaNamingFactoryState=" + this.javaNamingFactoryState);
        }
        if (this.javaNamingFactoryURLPKGS != null) {
            strBuf.append("\njavaNamingFactoryURLPKGS=" + this.javaNamingFactoryURLPKGS);
        }
        if (this.javaNamingFactoryState != null) {
            strBuf.append("\njavaNamingFactoryState=" + this.javaNamingFactoryState);
        }
        if (this.javaNamingDNSURL != null) {
            strBuf.append("\njavaNamingDNSURL=" + this.javaNamingDNSURL);
        }
        if (this.javaNamingAuthoritative != null) {
            strBuf.append("\njavaNamingAuthoritative=" + this.javaNamingAuthoritative);
        }
        if (this.javaNamingBatchsize != null) {
            strBuf.append("\njavaNamingBatchsize=" + this.javaNamingBatchsize);
        }
        if (this.javaNamingReferral != null) {
            strBuf.append("\njavaNamingReferral=" + this.javaNamingReferral);
        }
        if (this.javaNamingSecurityProtocol != null) {
            strBuf.append("\njavaNamingSecurityProtocol=" + this.javaNamingSecurityProtocol);
        }
        if (this.javaNamingSecurityAuthentication != null) {
            strBuf.append("\njavaNamingSecurityAuthentication=" + this.javaNamingSecurityAuthentication);
        }
        if (this.javaNamingSecurityPrincipal != null) {
            strBuf.append("\njavaNamingSecurityPrincipal=" + this.javaNamingSecurityPrincipal);
        }
        if (this.javaNamingSecurityCredentials != null) {
            strBuf.append("\njavaNamingSecurityCredentials=" + this.javaNamingSecurityCredentials);
        }
        if (this.javaNamingLanguage != null) {
            strBuf.append("\njavaNamingLanguage=" + this.javaNamingLanguage);
        }
        if (this.javaNamingApplet != null) {
            strBuf.append("\njavaNamingApplet=" + this.javaNamingApplet);
        }
        // extension
        if (this.isSharedDestination != null) {
            strBuf.append("\nisSharedDestination=" + this.isSharedDestination);
        }
        if (this.userId != null) {
            strBuf.append("\nUserID=" + this.userId);
        }
        if (this.password != null) {
            strBuf.append("\nPassword=" + this.password);
        }

        return strBuf.toString();
    }
}
