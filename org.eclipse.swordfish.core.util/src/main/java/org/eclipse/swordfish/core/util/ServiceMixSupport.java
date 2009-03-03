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

import java.util.Iterator;
import java.util.Map;

import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.servicemix.jbi.runtime.impl.MessageExchangeImpl;
import org.apache.servicemix.jbi.runtime.impl.NormalizedMessageImpl;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.Message;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Reference;
import org.apache.servicemix.nmr.api.internal.InternalEndpoint;
import org.apache.servicemix.nmr.api.internal.InternalReference;
import org.apache.servicemix.nmr.core.MessageImpl;
import org.eclipse.swordfish.core.util.xml.StringSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class ServiceMixSupport {
	private final Logger LOG = LoggerFactory.getLogger(ServiceMixSupport.class);
	public static Exchange toNMRExchange(MessageExchange messageExchange) {
		Assert.notNull(messageExchange);
		Assert.isTrue(messageExchange instanceof MessageExchangeImpl);
		return ((MessageExchangeImpl) messageExchange).getInternalExchange();
	}

	public static void setStringMessage(MessageExchange source, String messageType, String content) {

		try {
			if (source.getMessage(messageType) != null) {
			source.getMessage(messageType).setContent(new StringSource(content));
			} else {
				Message responseMessage = new MessageImpl();
				responseMessage.setBody(new StringSource(content), Source.class);
				NormalizedMessageImpl responseNormalizedMessage = new NormalizedMessageImpl(responseMessage);
				source.setMessage(responseNormalizedMessage, messageType);
			}
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
	}
	public static void setSourceService(ComponentContext componentContext, MessageExchange messageExchange, QName sourceService) {
		Assert.notNull(componentContext);
		Assert.notNull(messageExchange);
		Assert.notNull(sourceService);
		ServiceEndpoint[] serviceEndpoints = componentContext.getEndpointsForService(sourceService);
		Assert.notEmpty(serviceEndpoints);
		messageExchange.setProperty(JbiConstants.SENDER_ENDPOINT, serviceEndpoints[0].getServiceName() + ":" + serviceEndpoints[0].getEndpointName());
	}
	public static InternalEndpoint getEndpoint(Reference reference) {
	    Iterator<InternalEndpoint> endpointsIterator = ((InternalReference)reference).choose().iterator();
    	if (!endpointsIterator.hasNext()) {
    		return null;
        }
        return endpointsIterator.next();
    }
	public static InternalEndpoint getEndpoint(NMR nmr, Map<String, ?> props) {
    	InternalReference reference = (InternalReference)nmr.getEndpointRegistry().lookup(props);
    	Iterator<InternalEndpoint> endpointsIterator = reference.choose().iterator();
    	if (!endpointsIterator.hasNext()) {
        	return null;
        }
        return endpointsIterator.next();
    }

	public static java.io.InputStream convertStringToIS(String xml,
            String encoding) {
        if (xml == null)
            return null;
        xml = xml.trim();
        java.io.InputStream in = null;
        try {
            in = new java.io.ByteArrayInputStream(xml.getBytes(encoding));
        } catch (Exception ex) {
        }
        return in;
    }
}
