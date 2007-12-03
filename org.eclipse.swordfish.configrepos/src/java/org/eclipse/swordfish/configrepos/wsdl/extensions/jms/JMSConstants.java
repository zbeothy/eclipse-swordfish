/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.configrepos.wsdl.extensions.jms;

import javax.xml.namespace.QName;

public final class JMSConstants {

    public static final String NS_URI_JMS = "http://schemas.iona.com/transports/jms";

    public static final String ELEM_ADDRESS = "address";

    public static final QName Q_ELEM_JMS_ADDRESS = new QName("http://schemas.iona.com/transports/jms", "address");

    public static final String ATTR_DESTINATION_STYLE = "destinationStyle";

    public static final String ATTR_JNDI_PROVIDER_URL = "jndiProviderURL";

    public static final String ATTR_INITIAL_CONTEXT_FACTORY = "initialContextFactory";

    public static final String ATTR_JNDI_CONNECTION_FACTORY_NAME = "jndiConnectionFactoryName";

    public static final String ATTR_JNDI_DESTINATION_NAME = "jndiDestinationName";

    public static final String ATTR_MESSAGE_TYPE = "messageType";

    public static final String ATTR_DURABLE_SUBSCRIBER_NAME = "durableSubscriberName";

    public static final String ATTR_MESSAGE_SELECTOR = "messageSelector";

    public static final String ATTR_USE_MESSAGE_ID_AS_CORRELATION_ID = "useMessageIDAsCorrelationID";

    public static final String ATTR_SHARED_DESTINATION = "isSharedDestination";

    public static final String ATTR_DESTINATIN_USER_ID = "userID";

    public static final String ATTR_DESTINATIN_USER_PASSWORD = "password";

    public static final String ATTR_JAVA_NAMING_FACTORY_INITIAL = "java.naming.factory.initial";

    public static final String ATTR_JAVA_NAMING_PROVIDER_URL = "java.naming.provider.url";

    public static final String ATTR_JAVA_NAMING_FACTORY_OBJECT = "java.naming.factory.object";

    public static final String ATTR_JAVA_NAMING_FACTORY_STATE = "java.naming.factory.state";

    public static final String ATTR_JAVA_NAMING_FACTORY_URL_PKGS = "java.naming.factory.url.pkgs";

    public static final String ATTR_JAVA_NAMING_DNS_URL = "java.naming.dns.url";

    public static final String ATTR_JAVA_NAMING_AUTHORITATIVE = "java.naming.authoritative";

    public static final String ATTR_JAVA_NAMING_BATCHSIZE = "java.naming.batchsize";

    public static final String ATTR_JAVA_NAMING_REFERRAL = "java.naming.referral";

    public static final String ATTR_JAVA_NAMING_SECURITY_PROTOCOL = "java.naming.security.protocol";

    public static final String ATTR_JAVA_NAMING_SECURITY_AUTHENTICATION = "java.naming.security.authentication";

    public static final String ATTR_JAVA_NAMING_SECURITY_PRINCIPAL = "java.naming.security.principal";

    public static final String ATTR_JAVA_NAMING_SECURITY_CREDENTIALS = "java.naming.security.credentials";

    public static final String ATTR_JAVA_NAMING_LANGUAGE = "java.naming.language";

    public static final String ATTR_JAVA_NAMING_APPLET = "java.naming.applet";

    private JMSConstants() {
    }

}
