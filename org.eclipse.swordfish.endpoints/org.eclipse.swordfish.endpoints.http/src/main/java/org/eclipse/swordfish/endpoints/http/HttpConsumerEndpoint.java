/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.swordfish.endpoints.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.servicemix.JbiConstants;
import org.apache.servicemix.jbi.runtime.impl.EndpointImpl;
import org.apache.servicemix.jbi.runtime.impl.MessageExchangeImpl;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.internal.InternalEndpoint;
import org.apache.servicemix.nmr.core.ExchangeImpl;
import org.apache.servicemix.soap.Context;
import org.apache.servicemix.soap.SoapFault;
import org.apache.servicemix.soap.SoapHelper;
import org.apache.servicemix.soap.marshalers.JBIMarshaler;
import org.apache.servicemix.soap.marshalers.SoapMessage;
import org.apache.servicemix.soap.marshalers.SoapWriter;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.RetryRequest;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class HttpConsumerEndpoint extends EndpointImpl implements
		InitializingBean {
	public static final Integer DEFAULT_LISTENING_PORT = 8193;
	private static Logger log = LoggerFactory.getLogger(HttpConsumerEndpoint.class);

	protected DeliveryChannelMock channel;

	protected SoapHelper soapHelper;
	protected Map<String, Continuation> locks;
	protected Map<String, MessageExchange> exchanges;
	protected Map<String, MessageExchange> weakExchangeStorage = Collections
			.synchronizedMap(new WeakHashMap<String, MessageExchange>());
	protected int suspentionTime = 60000;
	protected NMR nmr;
	protected InternalEndpoint internalEndpoint;
	protected Integer port;
	protected String locationPath;
	protected Server server;

	public void start() throws Exception {
		Assert.notNull(nmr);
		Map<String, Object> props = new HashMap<String, Object>();
		if (this.getServiceName() == null && this.getEndpointName() == null) {
			throw new IllegalStateException(
					"serviceName or endpoint name should be set for the endpoint");
		}
		if (this.getServiceName() != null) {
			props.put(Endpoint.SERVICE_NAME, this.getServiceName());
		} else {
			props.put(Endpoint.SERVICE_NAME, new QName("mockServiceName",
					"mockServiceName"));
		}
		if (this.getEndpointName() != null) {
			props.put(Endpoint.ENDPOINT_NAME, this.getEndpointName());
		}
		nmr.getEndpointRegistry().register(this, props);
		internalEndpoint = ServiceMixHelper.getEndpoint(nmr, props);
		Assert.notNull(internalEndpoint);
		if (port == null) {
			port = DEFAULT_LISTENING_PORT;
		}
		if (locationPath == null) {
			locationPath = "/";
		}
		server = ServerStorage.getServerStorage().getServer(port);
		server.addHandler(new AbstractHandler() {
			public void handle(String string, HttpServletRequest request,
					HttpServletResponse response, int i) throws IOException,
					ServletException {
				String pathInfo = request.getPathInfo();
				if (pathInfo == null) {
					pathInfo = "/";
				}
				if (pathInfo.contains(locationPath)) {
					try {
						HttpConsumerEndpoint.this.process(request, response);
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
					((Request) request).setHandled(true);
				}

			}
		});
		if (!server.isRunning()) {
			server.start();
		}
	}

	public HttpConsumerEndpoint() {
		this.locks = new ConcurrentHashMap<String, Continuation>();
		this.exchanges = new ConcurrentHashMap<String, MessageExchange>();

	}

	public synchronized SoapHelper getSoapHelper() {
		if (soapHelper == null) {
			soapHelper = new SoapHelper(new MockSoapEndpoint(this,
					new ServiceMixComponentMock(new ComponentContextMock(nmr))));
		}
		return soapHelper;
	}

	@Override
	public void process(Exchange exchange) {
		Continuation cont = locks.remove(exchange.getId());
		if (cont != null) {
			synchronized (cont) {
				if (log.isDebugEnabled()) {
					log.debug("Resuming continuation for exchange: "
							+ exchange.getId());
				}
				MessageExchange messageExchange = weakExchangeStorage
						.get(exchange.getId());
				Assert.notNull(messageExchange);
				exchanges.put(exchange.getId(), messageExchange);
				cont.resume();
			}
		}
	}

	public void stop() throws Exception {
		// getServerManager().remove(httpContext);
	}

	public void process(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Receiving HTTP request: " + request);
		}
		if ("GET".equals(request.getMethod())) {
			throw new UnsupportedOperationException();
		}
		if (!"POST".equals(request.getMethod())) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
					request.getMethod() + " not supported");
			return;
		}
		// Not giving a specific mutex will synchronize on the contination
		// itself
		Continuation cont = ContinuationSupport.getContinuation(request, null);
		MessageExchange exchange;
		// If the continuation is not a retry
		if (!cont.isPending()) {
			try {
				InputStream inputStream = request.getInputStream();
				//if (log.isDebugEnabled()) {
					String soapRequest = convertISToString(request.getInputStream());
					log.info("Received soapRequest = " + soapRequest);
					inputStream = ServiceMixHelper.convertStringToIS(soapRequest, "UTF8");
				//}
				SoapMessage message = getSoapHelper().getSoapMarshaler()
						.createReader().read(inputStream,
								request.getHeader("Content-Type"));
				Context ctx = getSoapHelper().createContext(message);
				/*
				 * if (request.getUserPrincipal() != null) { if
				 * (request.getUserPrincipal() instanceof JaasJettyPrincipal) {
				 * Subject subject = ((JaasJettyPrincipal) request
				 * .getUserPrincipal()).getSubject();
				 * ctx.getInMessage().setSubject(subject); } else {
				 * ctx.getInMessage().addPrincipal( request.getUserPrincipal());
				 * } }
				 */
				request.setAttribute(Context.class.getName(), ctx);
				exchange = getSoapHelper().onReceive(ctx);
				weakExchangeStorage.put(exchange.getExchangeId(), exchange);
				NormalizedMessage inMessage = exchange.getMessage("in");
				inMessage.setProperty(JbiConstants.PROTOCOL_HEADERS,
						getHeaders(request));
				locks.put(exchange.getExchangeId(), cont);
				request.setAttribute(MessageExchange.class.getName(), exchange
						.getExchangeId());
				synchronized (cont) {
					((ExchangeImpl) ((MessageExchangeImpl) exchange)
							.getInternalExchange()).setSource(internalEndpoint);
					getDeliveryChannel().send(exchange);
					if (log.isDebugEnabled()) {
						log.debug("Suspending continuation for exchange: "
								+ exchange.getExchangeId());
					}
					boolean result = cont.suspend(suspentionTime);
					exchange = exchanges.remove(exchange.getExchangeId());
					if (!result) {
						locks.remove(exchange.getExchangeId());
						throw new Exception("Error sending exchange: aborted");
					}
					request.removeAttribute(MessageExchange.class.getName());
				}
			} catch (RetryRequest retry) {
				throw retry;
			} catch (SoapFault fault) {
				sendFault(fault, request, response);
				return;
			} catch (Exception ex) {
				log
						.error(
								"An error happened while trying to process http request",
								ex);
				SoapFault fault = new SoapFault(ex);
				sendFault(fault, request, response);
				return;
			}
		} else {
			String id = (String) request.getAttribute(MessageExchange.class
					.getName());
			exchange = exchanges.remove(id);
			request.removeAttribute(MessageExchange.class.getName());
			boolean result = cont.suspend(0);
			// Check if this is a timeout
			if (exchange == null) {
				throw new IllegalStateException("Exchange not found");
			}
			if (!result) {
				throw new Exception("Timeout");
			}
		}
		if (exchange.getStatus() == ExchangeStatus.ERROR) {
			if (exchange.getError() != null) {
				throw new Exception(exchange.getError());
			} else {
				throw new Exception("Unknown Error");
			}
		} else if (exchange.getStatus() == ExchangeStatus.ACTIVE) {
			try {
				if (exchange.getFault() != null) {
					processFault(exchange, request, response);
				} else {
					processResponse(exchange, request, response);
				}
			} finally {
				exchange.setStatus(ExchangeStatus.DONE);
				// channel.send(exchange);
			}
		} else if (exchange.getStatus() == ExchangeStatus.DONE) {
			// This happens when there is no response to send back
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		}
	}

	private void processResponse(MessageExchange exchange,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		NormalizedMessage outMsg = exchange.getMessage("out");
		if (outMsg != null) {
			Context ctx = (Context) request.getAttribute(Context.class
					.getName());
			SoapMessage out = getSoapHelper().onReply(ctx, outMsg);
			SoapWriter writer = getSoapHelper().getSoapMarshaler()
					.createWriter(out);
			response.setContentType(writer.getContentType());
			writer.write(response.getOutputStream());
		}
	}

	private void processFault(MessageExchange exchange,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SoapFault fault = new SoapFault((QName) exchange.getFault()
				.getProperty(JBIMarshaler.SOAP_FAULT_CODE), (QName) exchange
				.getFault().getProperty(JBIMarshaler.SOAP_FAULT_SUBCODE),
				(String) exchange.getFault().getProperty(
						JBIMarshaler.SOAP_FAULT_REASON), (URI) exchange
						.getFault().getProperty(JBIMarshaler.SOAP_FAULT_NODE),
				(URI) exchange.getFault().getProperty(
						JBIMarshaler.SOAP_FAULT_ROLE), exchange.getFault()
						.getContent());
		sendFault(fault, request, response);
	}

	protected void sendFault(SoapFault fault, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (SoapFault.SENDER.equals(fault.getCode())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		Context ctx = (Context) request.getAttribute(Context.class.getName());
		SoapMessage soapFault = getSoapHelper().onFault(ctx, fault);
		SoapWriter writer = getSoapHelper().getSoapMarshaler().createWriter(
				soapFault);
		response.setContentType(writer.getContentType());
		writer.write(response.getOutputStream());
	}

	protected Map<String, String> getHeaders(HttpServletRequest request) {
		Map<String, String> headers = new HashMap<String, String>();
		Enumeration<?> enumeration = request.getHeaderNames();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			String value = request.getHeader(name);
			headers.put(name, value);
		}
		return headers;
	}

	public String convertISToString(java.io.InputStream is) {
		 BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception ex) {
			ex.getMessage();
		} finally {
			try {
				is.close();
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}

	public NMR getNmr() {
		return nmr;
	}

	public DeliveryChannelMock getDeliveryChannel() {
		if (channel == null) {
			Assert.notNull(nmr);
			channel = new DeliveryChannelMock(nmr, getChannel(),
					new LinkedBlockingQueue());
		}
		return channel;
	}

	public void setNmr(NMR nmr) {
		this.nmr = nmr;
	}

	public int getSuspentionTime() {
		return suspentionTime;
	}

	public void setSuspentionTime(int suspentionTime) {
		this.suspentionTime = suspentionTime;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getLocationPath() {
		return locationPath;
	}

	public void setLocationPath(String locationPath) {
		this.locationPath = locationPath;
	}

	public void afterPropertiesSet() throws Exception {
		start();

	}

}
