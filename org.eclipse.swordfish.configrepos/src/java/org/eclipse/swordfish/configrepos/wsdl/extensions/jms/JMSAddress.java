/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.configrepos.wsdl.extensions.jms;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;
import org.w3c.dom.Element;

public interface JMSAddress extends ExtensibilityElement, Serializable {

    public abstract String getDestinationPassword();

    public abstract String getDestinationStyle();

    public abstract String getDestinationUser();

    public abstract String getDurableSubscriberName();

    public abstract Element getElement();

    public abstract String getInitialContextFactory();

    public abstract String getIsSharedDestination();

    public abstract String getJavaNamingApplet();

    public abstract String getJavaNamingAuthoritative();

    public abstract String getJavaNamingBatchsize();

    public abstract String getJavaNamingDNSURL();

    public abstract String getJavaNamingFactoryInitial();

    public abstract String getJavaNamingFactoryObject();

    public abstract String getJavaNamingFactoryState();

    public abstract String getJavaNamingFactoryURLPKGS();

    public abstract String getJavaNamingLanguage();

    public abstract String getJavaNamingProviderURL();

    public abstract String getJavaNamingReferral();

    public abstract String getJavaNamingSecurityAuthentication();

    public abstract String getJavaNamingSecurityCredentials();

    public abstract String getJavaNamingSecurityPrincipal();

    public abstract String getJavaNamingSecurityProtocol();

    public abstract String getJndiConnectionFactoryName();

    public abstract String getJndiDestinationName();

    public abstract String getJndiProviderURL();

    public abstract String getMessageSelector();

    public abstract String getMessageType();

    public abstract String getUseMessageIDAsCorrelationID();

    public abstract void setDestinationPassword(String isSharedDestination);

    public abstract void setDestinationStyle(String s);

    public abstract void setDestinationUser(String userId);

    public abstract void setDurableSubscriberName(String s);

    public abstract void setElement(Element element);

    public abstract void setInitialContextFactory(String s);

    // extended elements
    public abstract void setIsSharedDestination(String isSharedDestination);

    public abstract void setJavaNamingApplet(String s);

    public abstract void setJavaNamingAuthoritative(String s);

    public abstract void setJavaNamingBatchsize(String s);

    public abstract void setJavaNamingDNSURL(String s);

    public abstract void setJavaNamingFactoryInitial(String s);

    public abstract void setJavaNamingFactoryObject(String s);

    public abstract void setJavaNamingFactoryState(String s);

    public abstract void setJavaNamingFactoryURLPKGS(String s);

    public abstract void setJavaNamingLanguage(String s);

    public abstract void setJavaNamingProviderURL(String s);

    public abstract void setJavaNamingReferral(String s);

    public abstract void setJavaNamingSecurityAuthentication(String s);

    public abstract void setJavaNamingSecurityCredentials(String s);

    public abstract void setJavaNamingSecurityPrincipal(String s);

    public abstract void setJavaNamingSecurityProtocol(String s);

    public abstract void setJndiConnectionFactoryName(String s);

    public abstract void setJndiDestinationName(String s);

    public abstract void setJndiProviderURL(String s);

    public abstract void setMessageSelector(String s);

    public abstract void setMessageType(String s);

    public abstract void setUseMessageIDAsCorrelationID(String s);
}
