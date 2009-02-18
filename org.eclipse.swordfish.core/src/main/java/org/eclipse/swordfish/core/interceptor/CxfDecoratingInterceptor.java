package org.eclipse.swordfish.core.interceptor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.jbi.messaging.MessageExchange;
import javax.xml.transform.Source;

import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Role;
import org.apache.servicemix.nmr.api.Status;
import org.apache.servicemix.nmr.api.internal.InternalEndpoint;
import org.apache.servicemix.nmr.api.internal.InternalExchange;
import org.apache.servicemix.nmr.core.InternalEndpointWrapper;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.core.util.ServiceMixSupport;
import org.eclipse.swordfish.core.util.xml.StringSource;
import org.eclipse.swordfish.core.util.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CxfDecoratingInterceptor implements Interceptor {
	private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
	private final static String SOAP_MESSAGE_PREFIX = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
		"<soap:Body>";
	private final static String SOAP_MESSAGE_SUFFIX = "</soap:Body></soap:Envelope>";
	private Map<String, ?> properties = new HashMap<String, Object>();
	private NMR nmr;


	public synchronized NMR getNmr() {
		return nmr;
	}

	public void setNmr(NMR nmr) {
		this.nmr = nmr;
	}

	private boolean isCxfEndpoint(InternalEndpoint endpoint) {
		try {
			InternalEndpointWrapper endpointWrapper = (InternalEndpointWrapper) endpoint;

			Field endpointField = InternalEndpointWrapper.class.getDeclaredField("endpoint");
			endpointField.setAccessible(true);
			Endpoint innerEndpoint = (Endpoint)endpointField.get(endpointWrapper);
			if (innerEndpoint != null && innerEndpoint.getClass().getCanonicalName().contains("cxf")) {
				return true;
			}
		} catch (Exception ex) {
			LOG.warn(ex.getMessage(), ex);
			return false;
		}
		return false;
	}

	public void process(MessageExchange exchange) throws SwordfishException {
		InternalExchange messageExchange = (InternalExchange) ServiceMixSupport.toNMRExchange(exchange);
		if (messageExchange.getTarget() == null) {
			throw new UnsupportedOperationException();
		}
		InternalEndpoint endpoint = ServiceMixSupport.getEndpoint(messageExchange.getTarget());
		if (!isCxfEndpoint(endpoint)) {
			 return;
		}
		try {
		if (messageExchange.getRole() == Role.Consumer) {
				String message = XmlUtil.toString(messageExchange.getIn(false).getBody(Source.class));
				if (!message.contains(":Body")) {
				int index = message.indexOf(">") + 1;
				String xmlPrefix = message.substring(0, index);
				String cutMessage = message.substring(index);
				String soapMessage = xmlPrefix + SOAP_MESSAGE_PREFIX + cutMessage + SOAP_MESSAGE_SUFFIX;
				messageExchange.getIn(false).setBody(new StringSource(soapMessage));
				}
				} else 	if (messageExchange.getRole() == Role.Provider && messageExchange.getOut(false) != null) {
				String message = XmlUtil.toString(messageExchange.getOut(false).getBody(Source.class));
				if (message.contains(":Body")) {
					int index = message.indexOf(">") + 1;
					String xmlPrefix = message.substring(0, index);
					String cutMessage = message.substring(index);

					int startIndex = cutMessage.indexOf(":Body");
					startIndex = cutMessage.indexOf(">", startIndex) + 1;
					int endIndex = cutMessage.indexOf(":Body", startIndex);//end of body
					endIndex = cutMessage.substring(0, endIndex).lastIndexOf("</");
					String bodyMessage = cutMessage.substring(startIndex, endIndex);
					bodyMessage = xmlPrefix + bodyMessage;
					messageExchange.getOut(false).setBody(new StringSource(bodyMessage));
				}
			}
		if (messageExchange.getStatus() == Status.Active) {
			if ( messageExchange.getFault(false) != null && messageExchange.getFault(false).getBody() == null) {
				messageExchange.setFault(null);
			}
			if ( messageExchange.getOut(false) != null && messageExchange.getOut(false).getBody() == null) {
				messageExchange.setOut(null);
			}
		}} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public Map<String, ?> getProperties() {
		return properties;
	}
}
