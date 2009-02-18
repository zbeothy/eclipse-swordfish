package org.eclipse.swordfish.endpoints.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.servicemix.common.JbiConstants;
import org.apache.servicemix.jbi.runtime.impl.EndpointImpl;
import org.apache.servicemix.jbi.runtime.impl.MessageExchangeImpl;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Pattern;
import org.apache.servicemix.soap.Context;
import org.apache.servicemix.soap.SoapHelper;
import org.apache.servicemix.soap.marshalers.SoapMessage;
import org.apache.servicemix.soap.marshalers.SoapReader;
import org.apache.servicemix.soap.marshalers.SoapWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class HttpProviderEndpoint extends EndpointImpl implements InitializingBean {
	private static Logger log = LoggerFactory.getLogger(HttpProviderEndpoint.class);
	public static final String HEADER_SOAP_ACTION = "SOAPAction";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	protected SoapHelper soapHelper;
	protected DeliveryChannel channel;
	private static HttpClient httpClient;
	private static int timeOut = 30000;
	protected NMR nmr;
	protected String defaultLocationUri;
	private boolean isSoap = true;
	private Map<String, PostMethod> methods;
	private boolean synchronous;
	private boolean isStreamingEnabled;

	public static synchronized HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new HttpClient();
			httpClient.getParams().setSoTimeout(timeOut);
		}

		return httpClient;
	}
	 public void start() throws Exception {
	        Assert.notNull(nmr);
	        if (this.getServiceName() == null && this.getEndpointName() == null) {
				throw new IllegalStateException(
						"serviceName or endpoint name should be set for the endpoint");
			}
	        Map<String, Object> props = new HashMap<String, Object>();
	        props.put(JbiConstants.PROTOCOL_TYPE, "http://schemas.xmlsoap.org/soap/http");
	        if (this.getServiceName() != null) {
				props.put(Endpoint.SERVICE_NAME, this.getServiceName());
			} else {
				props.put(Endpoint.SERVICE_NAME, new QName("mockServiceName", "mockServiceName"));
			}
			if (this.getEndpointName() != null) {
				props.put(Endpoint.ENDPOINT_NAME, this.getEndpointName());
			}
			nmr.getEndpointRegistry().register(this, props);
			channel = getDeliveryChannel();
	 }

	public long getTimeOut() {
		return timeOut;
	}

	public static void setTimeOut(int timeOut) {
		HttpProviderEndpoint.timeOut = timeOut;
	}

	public boolean isStreamingEnabled() {
		return isStreamingEnabled;
	}

	public void setStreamingEnabled(boolean isStreamingEnabled) {
		this.isStreamingEnabled = isStreamingEnabled;
	}



	public boolean isSoap() {
		return isSoap;
	}

	public void setSoap(boolean isSoap) {
		this.isSoap = isSoap;
	}

	public String getDefaultLocationUri() {
		return defaultLocationUri;
	}

	public void setDefaultLocationUri(String defaultLocationUri) {
		this.defaultLocationUri = defaultLocationUri;
	}

	public DeliveryChannel getDeliveryChannel() {
		if (channel == null) {
			Assert.notNull(nmr);
			channel = new DeliveryChannelMock(nmr, getChannel(),
					new LinkedBlockingQueue<Exchange>());
		}
		return channel;
	}

	public NMR getNmr() {
		return nmr;
	}

	public void setNmr(NMR nmr) {
		this.nmr = nmr;
	}

	public boolean isSynchronous() {
		return synchronous;
	}

	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}

	public HttpProviderEndpoint() {
		soapHelper = new SoapHelper(new MockSoapEndpoint(this,
				new ServiceMixComponentMock(new ComponentContextMock(nmr))));

		this.methods = new ConcurrentHashMap<String, PostMethod>();
	}

	private String getRelUri(String locationUri) {
		java.net.URI uri = java.net.URI.create(locationUri);
		String relUri = uri.getPath();
		if (!relUri.startsWith("/")) {
			relUri = "/" + relUri;
		}
		if (uri.getQuery() != null) {
			relUri += "?" + uri.getQuery();
		}
		if (uri.getFragment() != null) {
			relUri += "#" + uri.getFragment();
		}
		return relUri;
	}

	@Override
	public void process(Exchange exchange) {
		MessageExchange messageExchange = new MessageExchangeImpl(exchange);
		exchange.setOut(null);
		exchange.setFault(null);
		PostMethod method = null;
		boolean close = true;
		try {
			if (messageExchange.getStatus() == ExchangeStatus.DONE
					|| messageExchange.getStatus() == ExchangeStatus.ERROR) {
				method = methods.remove(messageExchange.getExchangeId());
				if (method != null) {
					method.releaseConnection();
				}
				return;
			}
			boolean txSync = messageExchange.isTransacted()
					&& Boolean.TRUE.equals(messageExchange
							.getProperty(JbiConstants.SEND_SYNC));
			txSync |= isSynchronous();
			NormalizedMessage nm = messageExchange.getMessage("in");
			if (nm == null) {
				throw new IllegalStateException("Exchange has no input message");
			}
			String locationURI = getDefaultLocationUri();

			// Incorporated because of JIRA SM-695
			Object newDestinationURI = nm
					.getProperty(JbiConstants.HTTP_DESTINATION_URI);
			if (newDestinationURI == null) {
				newDestinationURI = messageExchange.getProperty(JbiConstants.HTTP_DESTINATION_URI);
			}
			if (newDestinationURI != null) {
				locationURI = (String) newDestinationURI;
				log.debug("Location URI overridden: " + locationURI);
			}

			method = new PostMethod(getRelUri(locationURI));
			SoapMessage soapMessage = new SoapMessage();
			soapHelper.getJBIMarshaler().fromNMS(soapMessage, nm);
			Context context = soapHelper.createContext(soapMessage);
			soapHelper.onSend(context);
			SoapWriter writer = soapHelper.getSoapMarshaler().createWriter(
					soapMessage);
			copyHeaderInformation(nm, method);
			RequestEntity entity = writeMessage(writer);

			if (entity.getContentLength() < 0) {
				method.removeRequestHeader(HEADER_CONTENT_LENGTH);
			} else {
				method.setRequestHeader(HEADER_CONTENT_LENGTH, Long
						.toString(entity.getContentLength()));
			}
			if (isSoap() && method.getRequestHeader(HEADER_SOAP_ACTION) == null) {

				method.setRequestHeader(HEADER_SOAP_ACTION, "\"\"");

			}
			method.setRequestEntity(entity);

			// Set the retry handler
			int retries = 3;
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(retries, true));
			// Set authentication
			/*
			 * if (endpoint.getBasicAuthentication() != null) {
			 * endpoint.getBasicAuthentication().applyCredentials(getClient(),
			 * exchange, nm); }
			 */
			// Execute the HTTP method
			int response = getHttpClient().executeMethod(
					getHostConfiguration(locationURI, messageExchange, nm),
					method);
			if (response != HttpStatus.SC_OK
					&& response != HttpStatus.SC_ACCEPTED) {
				if (!exchange.getPattern().equals(Pattern.InOnly)) {
					SoapReader reader = soapHelper.getSoapMarshaler()
							.createReader();
					Header contentType = method
							.getResponseHeader(HEADER_CONTENT_TYPE);
					soapMessage = reader
							.read(method.getResponseBodyAsStream(),
									contentType != null ? contentType
											.getValue() : null);
					context.setFaultMessage(soapMessage);
					soapHelper.onAnswer(context);
					Fault fault = messageExchange.createFault();
					fault.setProperty(JbiConstants.PROTOCOL_HEADERS,
							getHeaders(method));
					soapHelper.getJBIMarshaler().toNMS(fault, soapMessage);
					messageExchange.setFault(fault);
					if (txSync) {
						channel.sendSync(messageExchange);
					} else {
						methods.put(messageExchange.getExchangeId(), method);
						channel.send(messageExchange);
						close = false;
					}
					return;
				} else {
					throw new Exception("Invalid status response: " + response);
				}
			}
			if (exchange.getPattern() == Pattern.InOut) {
				close = processInOut(messageExchange, method, context, txSync,
						close);
			} else if (exchange.getPattern() == Pattern.InOptionalOut) {
				close = processInOptionalOut(method, messageExchange, context,
						txSync, close);
			} else {
				messageExchange.setStatus(ExchangeStatus.DONE);
				channel.send(messageExchange);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			messageExchange.setError(ex);
			try {
				messageExchange.setStatus(ExchangeStatus.ERROR);
			} catch (MessagingException mex) {
				log.warn(mex.getMessage(), mex);
			}
			throw new RuntimeException(ex);

		} finally {
			if (close) {
				if (method != null) {
					method.releaseConnection();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void copyHeaderInformation(NormalizedMessage nm, PostMethod method) {
		Map<String, String> headers = (Map<String, String>) nm
				.getProperty(JbiConstants.PROTOCOL_HEADERS);
		if (headers != null) {
			for (String name : headers.keySet()) {
				String value = headers.get(name);
				method.addRequestHeader(name, value);
			}
		}
	}

	private boolean processInOptionalOut(PostMethod method,
			MessageExchange exchange, Context context, boolean txSync,
			boolean close) throws Exception {
		if (method.getResponseContentLength() == 0) {
			exchange.setStatus(ExchangeStatus.DONE);
			channel.send(exchange);
		} else {
			NormalizedMessage msg = exchange.createMessage();
			SoapReader reader = soapHelper.getSoapMarshaler().createReader();
			SoapMessage soapMessage = reader.read(method
					.getResponseBodyAsStream(), method.getResponseHeader(
					HEADER_CONTENT_TYPE).getValue());
			context.setOutMessage(soapMessage);
			soapHelper.onAnswer(context);
			msg.setProperty(JbiConstants.PROTOCOL_HEADERS, getHeaders(method));

			soapHelper.getJBIMarshaler().toNMS(msg, soapMessage);
			exchange.setMessage(msg, "out");
			if (txSync) {
				channel.sendSync(exchange);
			} else {
				methods.put(exchange.getExchangeId(), method);
				channel.send(exchange);
				close = false;
			}
		}
		return close;
	}

	private boolean processInOut(MessageExchange exchange, PostMethod method,
			Context context, boolean txSync, boolean close) throws Exception {
		NormalizedMessage msg = exchange.createMessage();
		SoapReader reader = soapHelper.getSoapMarshaler().createReader();
		Header contentType = method.getResponseHeader(HEADER_CONTENT_TYPE);
		SoapMessage soapMessage = reader.read(method.getResponseBodyAsStream(),
				contentType != null ? contentType.getValue() : null);
		context.setOutMessage(soapMessage);
		soapHelper.onAnswer(context);

		msg.setProperty(JbiConstants.PROTOCOL_HEADERS, getHeaders(method));

		soapHelper.getJBIMarshaler().toNMS(msg, soapMessage);
		exchange.setMessage(msg, "out");
		if (txSync) {
			channel.sendSync(exchange);
		} else {
			methods.put(exchange.getExchangeId(), method);
			channel.send(exchange);
			close = false;
		}
		return close;
	}

	private HostConfiguration getHostConfiguration(String locationURI,
			MessageExchange exchange, NormalizedMessage message)
			throws Exception {
		HostConfiguration host;
		URI uri = new URI(locationURI, false);
		/*
		 * if (uri.getScheme().equals("https")) { synchronized (this) { if
		 * (protocol == null) { ProtocolSocketFactory sf = new
		 * CommonsHttpSSLSocketFactory( endpoint.getSsl(),
		 * endpoint.getKeystoreManager()); protocol = new Protocol("https", sf,
		 * 443); } } HttpHost httphost = new HttpHost(uri.getHost(),
		 * uri.getPort(), protocol); host = new HostConfiguration();
		 * host.setHost(httphost); } else {
		 */
		host = new HostConfiguration();
		host.setHost(uri.getHost(), uri.getPort());
		// }
		/*
		 * f (endpoint.getProxy() != null) { if
		 * (endpoint.getProxy().getProxyHost() != null &&
		 * endpoint.getProxy().getProxyPort() != 0) {
		 * host.setProxy(endpoint.getProxy().getProxyHost(),
		 * endpoint.getProxy().getProxyPort()); } if
		 * (endpoint.getProxy().getProxyCredentials() != null) {
		 * endpoint.getProxy
		 * ().getProxyCredentials().applyProxyCredentials(getClient(), exchange,
		 * message); } } else if (getConfiguration().getProxyHost() != null &&
		 * getConfiguration().getProxyPort() != 0) {
		 * host.setProxy(getConfiguration().getProxyHost(),
		 * getConfiguration().getProxyPort()); }
		 */
		return host;
	}

	public void stop() throws Exception {
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

	protected Map<String, String> getHeaders(HttpMethod method) {
		Map<String, String> headers = new HashMap<String, String>();
		Header[] h = method.getResponseHeaders();
		for (int i = 0; i < h.length; i++) {
			headers.put(h[i].getName(), h[i].getValue());
		}
		return headers;
	}

	protected RequestEntity writeMessage(SoapWriter writer) throws Exception {
		if (isStreamingEnabled()) {
			return new StreamingRequestEntity(writer);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.write(baos);
			return new ByteArrayRequestEntity(baos.toByteArray(), writer
					.getContentType());
		}
	}



	public static class StreamingRequestEntity implements RequestEntity {

		private SoapWriter writer;

		public StreamingRequestEntity(SoapWriter writer) {
			this.writer = writer;
		}

		public boolean isRepeatable() {
			return false;
		}

		public void writeRequest(OutputStream out) throws IOException {
			try {
				writer.write(out);
				out.flush();
			} catch (Exception e) {
				throw (IOException) new IOException("Could not write request")
						.initCause(e);
			}
		}

		public long getContentLength() {
			// not known so we send negative value
			return -1;
		}

		public String getContentType() {
			return writer.getContentType();
		}

	}



	public void afterPropertiesSet() throws Exception {
		Assert.notNull(nmr, "The nmr property has to be set");
		start();

	}
}
