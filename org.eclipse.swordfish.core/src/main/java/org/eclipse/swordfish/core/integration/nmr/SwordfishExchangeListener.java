package org.eclipse.swordfish.core.integration.nmr;


import java.util.List;

import javax.jbi.messaging.MessageExchange.Role;

import org.apache.servicemix.jbi.runtime.impl.MessageExchangeImpl;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.event.ExchangeListener;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.core.exception.InterceptorExceptionNofiticationSender;
import org.eclipse.swordfish.core.planner.InterceptorRegistry;
import org.eclipse.swordfish.core.planner.api.Planner;
import org.eclipse.swordfish.core.planner.api.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class SwordfishExchangeListener implements ExchangeListener, InitializingBean {
	private transient static final Logger LOG = LoggerFactory.getLogger(SwordfishExchangeListener.class);
	private NMR nmr;
	private Planner planner;
	private Registry interceptorRegistry;
	private InterceptorExceptionNofiticationSender exceptionNotificationSender;
	
	public Registry getInterceptorRegistry() {
		return interceptorRegistry;
	}

	public void setInterceptorRegistry(Registry interceptorRegistry) {
		this.interceptorRegistry = interceptorRegistry;
	}

	public void exchangeDelivered(Exchange exchange) {
		LOG.debug("ExchangeDelivered exchangeId=" + exchange.getId());

	}

	public InterceptorExceptionNofiticationSender getExceptionNotificationSender() {
		return exceptionNotificationSender;
	}

	public void setExceptionNotificationSender(
			InterceptorExceptionNofiticationSender exceptionNotificationSender) {
		this.exceptionNotificationSender = exceptionNotificationSender;
	}

	public void exchangeSent(Exchange exchange) {
		MessageExchangeImpl exchangeImpl = new MessageExchangeImpl(exchange);
		try {
			List<Interceptor> interceptors = planner.getInterceptorChain(interceptorRegistry.getKeySet(), exchangeImpl);
			for (Interceptor interceptor : interceptors) {
				try {
					interceptor.process(exchangeImpl);
				} catch (SwordfishException ex) {
					LOG.warn("The interceptor has thrown exception", ex);
					exceptionNotificationSender.sendNotification(ex, exchangeImpl, interceptor);
	                if(exchangeImpl.getRole() == Role.CONSUMER) {
	                    throw ex;
	                } else {
	                    exchangeImpl.setError(ex);
	                }
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	public NMR getNmr() {
		return nmr;
	}

	public void setNmr(NMR nmr) {
		this.nmr = nmr;
	}
	public Planner getPlanner() {
		return planner;
	}

	public void setPlanner(Planner planner) {
		this.planner = planner;
	}

	protected void start() {
		nmr.getListenerRegistry().register(this, null);
	}
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(nmr);
		Assert.notNull(planner);
		Assert.notNull(interceptorRegistry);
		start();
	}
}
