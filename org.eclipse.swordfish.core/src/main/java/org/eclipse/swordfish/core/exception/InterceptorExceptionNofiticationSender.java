package org.eclipse.swordfish.core.exception;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.core.util.AopProxyUtil;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.osgi.context.BundleContextAware;

import javax.jbi.messaging.MessageExchange;

public class InterceptorExceptionNofiticationSender implements BundleContextAware {
    
    private static final Logger LOG = LoggerFactory
            .getLogger(InterceptorExceptionNofiticationSender.class);

    public final static String EXCEPTION_TOPIC = "org/eclipse/swordfish/api/interceptor/EXCEPTION";

    public static final String EXEPTION_EVENT_PROPERTY = "exeption.event.property";
    public static final String EXCHANGE_EVENT_PROPERTY = "exchange.event.property";
    public static final String INTERCEPTOR_EVENT_PROPERTY = "interceptor.event.property";

    private BundleContext bundleContext;
    private EventAdmin eventAdmin;

    public <T extends Interceptor> void sendNotification(Exception exception, MessageExchange exchange,
            T interceptor) {
        LOG.debug(String.format("received exception [%s] thrown during [%s] interceptor work "
                        + "for message exchange [%s]", exception, interceptor.getClass().getName(), exchange));
        LOG.debug("Proceed to sending notification event");

        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(EXEPTION_EVENT_PROPERTY, exception);
        properties.put(EXCHANGE_EVENT_PROPERTY, exchange);
        properties.put(INTERCEPTOR_EVENT_PROPERTY, AopProxyUtil.getTargetService(interceptor, bundleContext));

        Event event = new Event(EXCEPTION_TOPIC, properties);
        eventAdmin.postEvent(event);
    }

    public EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    public void setEventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
