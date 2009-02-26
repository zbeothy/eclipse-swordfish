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
package org.eclipse.swordfish.core.test.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.servicemix.jbi.runtime.impl.EndpointImpl;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Reference;
import org.apache.servicemix.nmr.api.Status;
import org.apache.servicemix.nmr.api.internal.InternalEndpoint;
import org.apache.servicemix.nmr.core.DynamicReferenceImpl;
import org.apache.servicemix.nmr.core.util.Filter;
import org.eclipse.swordfish.core.util.xml.StringSource;

public class ServiceMixSupport {

    public static EndpointImpl createAndRegisterEndpoint(NMR nmr, QName serviceName, final ExchangeProcessor delegate) {
        Map<String, String> props = new HashMap<String, String>();
        props.put(EndpointImpl.ENDPOINT_NAME, serviceName.getLocalPart() + "Endpoint");
        props.put(EndpointImpl.SERVICE_NAME, serviceName.toString());
        props.put(EndpointImpl.NAME, serviceName.getLocalPart() + "Endpoint");

        EndpointImpl endpoint = new EndpointImpl(props){
                @Override
                public void process(Exchange exchange) {
                    super.process(exchange);
                    if (delegate != null) {
                        delegate.process(exchange);
                        getChannel().send(exchange);
                    }
                    exchange.setStatus(Status.Done);
                }
            };
        endpoint.setQueue(new LinkedList<Exchange>());
       nmr.getEndpointRegistry().register(endpoint, props);
        return endpoint;
    }


    public static Reference lookup(final NMR nmr, final Map<String, ?> properties) {
        DynamicReferenceImpl ref = new DynamicReferenceImpl(nmr.getEndpointRegistry(), new Filter<InternalEndpoint>() {
            public boolean match(InternalEndpoint endpoint) {
                Map<String, ?> epProps = nmr.getEndpointRegistry().getProperties(endpoint);
                for (Map.Entry<String, ?> name : properties.entrySet()) {//epProps.put(name.getKey(), name.getValue())
                    if (!name.getValue().equals(epProps.get(name.getKey()))) {
                        return false;
                    }
                }
                return true;
            }
        });
        return ref;
    }
    public static interface ExchangeProcessor {
        public void process(Exchange exchange);
    }
    public static class ExchangeProcessorImpl implements ExchangeProcessor {
        private String endpointUri;
        public ExchangeProcessorImpl(String endpointUri) {
            this.endpointUri = endpointUri;
        }
        public void process(Exchange exchange) {
            exchange.getOut(true).setBody(new StringSource("<Aloha from=\""+ endpointUri + "\" />"));
            System.out.println(String.format("Endpoint[%s] received exchange with id = [%s]", endpointUri, exchange.getId()));
        }

    }
}
