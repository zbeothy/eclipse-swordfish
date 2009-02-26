/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.util;

public interface JbiConstants {
	 String SEND_SYNC = "javax.jbi.messaging.sendSync";

	    String PROTOCOL_TYPE = "javax.jbi.messaging.protocol.type";

	    String PROTOCOL_HEADERS = "javax.jbi.messaging.protocol.headers";

	    String SECURITY_SUBJECT = "javax.jbi.security.subject";

	    String SOAP_HEADERS = "org.apache.servicemix.soap.headers";

	    String PERSISTENT_PROPERTY_NAME = "org.apache.servicemix.persistent";

	    String DATESTAMP_PROPERTY_NAME = "org.apache.servicemix.datestamp";

	    String FLOW_PROPERTY_NAME = "org.apache.servicemix.flow";

	    String STATELESS_CONSUMER = "org.apache.servicemix.consumer.stateless";

	    String STATELESS_PROVIDER = "org.apache.servicemix.provider.stateless";

	    String SENDER_ENDPOINT = "org.apache.servicemix.senderEndpoint";

	    String HTTP_DESTINATION_URI = "org.apache.servicemix.http.destination.uri";

	    /**
	     * This property should be set when a consumer endpoint creates an exchange
	     * related to another provider exchange. The value of the property should be
	     * set to the value of this property in the provider exchange, or to the id
	     * of the provider exchange if the property does not exist.
	     */
	    String CORRELATION_ID = "org.apache.servicemix.correlationId";
}
