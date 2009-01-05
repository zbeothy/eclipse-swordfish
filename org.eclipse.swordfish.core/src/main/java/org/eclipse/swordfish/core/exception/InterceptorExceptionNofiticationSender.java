package org.eclipse.swordfish.core.exception;


import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.event.EventService;
import org.eclipse.swordfish.core.util.AopProxyUtil;
import org.osgi.framework.BundleContext;

import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.context.BundleContextAware;

public class InterceptorExceptionNofiticationSender implements BundleContextAware {
    
    private static final Logger LOG = LoggerFactory
            .getLogger(InterceptorExceptionNofiticationSender.class);

    public final static String EXCEPTION_TOPIC = "org/eclipse/swordfish/api/interceptor/EXCEPTION";

    public static final String EXEPTION_EVENT_PROPERTY = "exeption.event.property";
    public static final String EXCHANGE_EVENT_PROPERTY = "exchange.event.property";
    public static final String INTERCEPTOR_EVENT_PROPERTY = "interceptor.event.property";

    private BundleContext bundleContext;

    private EventService eventService;

    public <T extends Interceptor> void sendNotification(Exception exception, MessageExchange exchange,
            T interceptor) {
        LOG.debug(String.format("received exception [%s] thrown during [%s] interceptor work "
                        + "for message exchange [%s]", exception, interceptor.getClass().getName(), exchange));
        LOG.debug("Proceed to sending notification event");
        InterceptorExceptionEvent event = new InterceptorExceptionEvent(exception, exchange, 
        		                                    AopProxyUtil.getTargetService(interceptor, bundleContext));
        eventService.postEvent(event);

    }

    
    public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
}
