package org.eclipse.swordfish.endpoints.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.servicemix.common.JbiConstants;
import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.servicemix.jbi.runtime.impl.EndpointImpl;
import org.apache.servicemix.jbi.runtime.impl.MessageExchangeImpl;
import org.apache.servicemix.jbi.runtime.impl.NormalizedMessageImpl;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.core.ExchangeImpl;
import org.apache.servicemix.nmr.core.MessageImpl;
import org.apache.servicemix.soap.Context;
import org.apache.servicemix.soap.SoapHelper;
import org.apache.servicemix.soap.marshalers.SoapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class SimpleClient extends EndpointImpl implements InitializingBean {
	private static Logger log = LoggerFactory.getLogger(SimpleClient.class);
	private String dataToSend;
	private String uriToSend;
	private String endpointNameToSend;
	private Integer delayBeforeSending = 5000;
	private NMR nmr;
	private Map<String, Object> endpointProps;
	private SoapHelper soapHelper;

	private void checkConstraints() {
		Assert.notNull(dataToSend, "dataToSend property is compulsory");
		Assert.notNull(nmr, "nmr property is compulsory");

		if (this.getServiceName() == null && this.getEndpointName() == null) {
			throw new IllegalStateException(
					"serviceName or endpoint name should be set for the endpoint");
		}

	}
	public void start() {
		checkConstraints();
		endpointProps = new HashMap<String, Object>();
		if (this.getServiceName() != null) {
			endpointProps.put(Endpoint.SERVICE_NAME, this.getServiceName());
		} else {
			endpointProps.put(Endpoint.SERVICE_NAME, new QName("mockServiceName", "mockServiceName"));
		}
		if (this.getEndpointName() != null) {
			endpointProps.put(Endpoint.ENDPOINT_NAME, this.getEndpointName());
		}
		nmr.getEndpointRegistry().register(this, endpointProps);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					sendRequestSynchronously();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}, delayBeforeSending);

	}

	public void sendRequestSynchronously() throws Exception {
	    getSoapHelper().getSoapMarshaler().setUseDom(true);
	    SoapMessage soapMessage = getSoapHelper().getSoapMarshaler().createReader().read(ServiceMixHelper.convertStringToIS(dataToSend, "UTF8"));
		NormalizedMessage normalizedMessage = new NormalizedMessageImpl(new MessageImpl());
		getSoapHelper().getJBIMarshaler().toNMS(normalizedMessage, soapMessage);
		Context ctx = getSoapHelper().createContext(soapMessage);
		MessageExchange messageExchange = getSoapHelper().onReceive(ctx);
		ExchangeImpl exchange = (ExchangeImpl) ((MessageExchangeImpl) messageExchange)
				.getInternalExchange();
		if (uriToSend != null) {
			exchange.getIn().setHeader(JbiConstants.HTTP_DESTINATION_URI, uriToSend);
		}
		exchange.setSource(ServiceMixHelper.getEndpoint(nmr, endpointProps));
		Map<String, Object> targetProps = new HashMap<String, Object>();
		//targetProps.put(ENDPOINT_NAME, endpointNameToSend);
		exchange.setTarget(nmr.getEndpointRegistry().lookup(targetProps));
		log.info("Sending synchronous request with in message " + dataToSend);
		getChannel().sendSync(exchange);
		if (exchange.getError() != null) {
			log.error("The invocation wasn't successful", exchange.getError());
		}
		if (exchange.getFault() != null && exchange.getFault().getBody() != null) {
			log.error("The invocation wasn't successful " + exchange.getFault().getBody().toString());
		}
		log.info("The response is " + new SourceTransformer().toString(exchange.getOut().getBody(Source.class)));
	}
	public String getDataToSend() {
		return dataToSend;
	}
	public void setDataToSend(String dataToSend) {
		this.dataToSend = dataToSend;
	}
	public String getUriToSend() {
		return uriToSend;
	}
	public void setUriToSend(String uriToSend) {
		this.uriToSend = uriToSend;
	}
	public String getEndpointNameToSend() {
		return endpointNameToSend;
	}
	public void setEndpointNameToSend(String endpointNameToSend) {
		this.endpointNameToSend = endpointNameToSend;
	}
	public Integer getDelayBeforeSending() {
		return delayBeforeSending;
	}
	public void setDelayBeforeSending(Integer delayBeforeSending) {
		this.delayBeforeSending = delayBeforeSending;
	}
	public NMR getNmr() {
		return nmr;
	}
	public void setNmr(NMR nmr) {
		this.nmr = nmr;
	}
	public void afterPropertiesSet() throws Exception {
		start();
	}
	public synchronized SoapHelper getSoapHelper() {
		if (soapHelper == null) {
			soapHelper = new SoapHelper(new MockSoapEndpoint(this,
					new ServiceMixComponentMock(new ComponentContextMock(nmr))));
		}
		return soapHelper;
	}
}
