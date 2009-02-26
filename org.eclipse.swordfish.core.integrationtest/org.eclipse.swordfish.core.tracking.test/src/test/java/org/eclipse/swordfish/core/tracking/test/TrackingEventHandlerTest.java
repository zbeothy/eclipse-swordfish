package org.eclipse.swordfish.core.tracking.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.messaging.MessageExchange.Role;
import javax.xml.namespace.QName;

import org.apache.servicemix.jbi.runtime.impl.EndpointImpl;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Pattern;
import org.apache.servicemix.nmr.core.ChannelImpl;
import org.apache.servicemix.nmr.core.ExchangeImpl;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.EventFilter;
import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.api.event.TrackingEvent;
import org.eclipse.swordfish.core.test.util.OsgiSupport;
import org.eclipse.swordfish.core.test.util.ServiceMixSupport;
import org.eclipse.swordfish.core.test.util.ServiceMixSupport.ExchangeProcessorImpl;
import org.eclipse.swordfish.core.test.util.base.TargetPlatformOsgiTestCase;
import org.eclipse.swordfish.core.util.xml.StringSource;
import org.osgi.framework.ServiceRegistration;

public class TrackingEventHandlerTest extends TargetPlatformOsgiTestCase {

	public void test1BlockingInterceptingCall() throws Exception {
		EndpointImpl endpointService1 = null;
		EndpointImpl endpointService2 = null;
		final NMR nmr = OsgiSupport.getReference(bundleContext, NMR.class);
		assertNotNull(nmr);
		// prepeare objects for tes

		List<SimpleExchange> exchangeList = new ArrayList<SimpleExchange>();
		EventHandler genericListener = new SimpleHandler(exchangeList);
		// adding listener to registry
		ServiceRegistration eventListenerSegistration = bundleContext
				.registerService(EventHandler.class.getName(), genericListener,
						null);
		addRegistrationToCancel(eventListenerSegistration);

		Thread.sleep(500);
		try {
			endpointService1 = ServiceMixSupport.createAndRegisterEndpoint(nmr,
					new QName("namespace", "Service1"), null);
			endpointService2 = ServiceMixSupport.createAndRegisterEndpoint(nmr,
					new QName("namespace", "Service2"),
					new ExchangeProcessorImpl(
							new QName("namespace", "Service2").toString()));
			ExchangeImpl exchange = new ExchangeImpl(Pattern.InOut);
			exchange.setSource(((ChannelImpl) endpointService1.getChannel())
					.getEndpoint());
			exchange.getIn(true).setBody(new StringSource("<Hello/>"));
			Map<String, String> props = new HashMap<String, String>();
			props.put(Endpoint.SERVICE_NAME, endpointService2.getServiceName()
					.toString());
			exchange.setTarget(ServiceMixSupport.lookup(nmr, props));

			// send exchange
			assertTrue(nmr.createChannel().sendSync(exchange));

			Thread.sleep(500);

			assertEquals(2, exchangeList.size());

			assertEquals(Role.CONSUMER, exchangeList.get(0).role);
			assertEquals("StringSource[<Hello/>]", exchangeList.get(0).in
					.getContent().toString());
			assertEquals(null, exchangeList.get(0).out);

			assertEquals(exchangeList.get(0).id, exchangeList.get(1).id);
			assertEquals(Role.PROVIDER, exchangeList.get(1).role);
			assertEquals("StringSource[<Hello/>]", exchangeList.get(1).in
					.getContent().toString());
			assertEquals(
					"StringSource[<Aloha from=\"{namespace}Service2\" />]",
					exchangeList.get(1).out.getContent().toString());

		} finally {
		    unregisterEndpoints(nmr, endpointService1, endpointService2);
		}
	}
	public void unregisterEndpoints(NMR nmr, Endpoint... endpoints) {
	    for (Endpoint endpoint : endpoints) {
	        try {
	            nmr.getEndpointRegistry().unregister(endpoint, null);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	}
	public void test1BlockingInterceptingCallWithException() throws Exception {
        EndpointImpl endpointService1 = null;
        EndpointImpl endpointService2 = null;
        NMR nmr = OsgiSupport.getReference(bundleContext, NMR.class);
        assertNotNull(nmr);

        final List<SimpleExchange> exchangeList = new ArrayList<SimpleExchange>();

        addRegistrationToCancel(bundleContext.registerService(EventHandler.class.getName(),
        				                                      new SimpleHandler(exchangeList), null));

        final String EXCEPTION_TEST_MESSAGE = "messageForException";
        addRegistrationToCancel(bundleContext.registerService(Interceptor.class.getCanonicalName(),
                                                              new ExceptionThrowableInterceptor(EXCEPTION_TEST_MESSAGE), null));

        Thread.sleep(500);
        try {
            endpointService1 = ServiceMixSupport.createAndRegisterEndpoint(nmr, new QName("namespace", "Service1"), null);
            endpointService2 = ServiceMixSupport.createAndRegisterEndpoint(nmr, new QName("namespace", "Service2"),
                    new ExchangeProcessorImpl(new QName("namespace", "Service2").toString()));
            ExchangeImpl exchange = new ExchangeImpl(Pattern.InOut);
            exchange.setSource(((ChannelImpl) endpointService1.getChannel()).getEndpoint());
            exchange.getIn(true).setBody(new StringSource("<Hello/>"));
            Map<String, String> props = new HashMap<String, String>();
            props.put(Endpoint.SERVICE_NAME, endpointService2.getServiceName().toString());
            exchange.setTarget(ServiceMixSupport.lookup(nmr, props));

            try {
                assertTrue(nmr.createChannel().sendSync(exchange));
                fail();
            } catch(Exception ex){
            }
            Thread.sleep(500);

            assertEquals(2, exchangeList.size());

            assertEquals(Role.CONSUMER, exchangeList.get(0).role);
            assertEquals("StringSource[<Hello/>]", exchangeList.get(0).in.getContent().toString());
            assertEquals(null, exchangeList.get(0).out);

            assertEquals(exchangeList.get(0).id, exchangeList.get(1).id);
            assertEquals(Role.CONSUMER, exchangeList.get(1).role);
            assertEquals("StringSource[<Hello/>]", exchangeList.get(1).in.getContent().toString());
            assertEquals(null, exchangeList.get(1).out.getContent());
            assertEquals(EXCEPTION_TEST_MESSAGE, exchangeList.get(1).error.getMessage());


        } finally {
            unregisterEndpoints(nmr, endpointService1, endpointService2);
        }
    }

	@Override
    protected String getManifestLocation() {
		return "classpath:org/eclipse/swordfish/core/tracking/test/MANIFEST.MF";
	}

	private static class SimpleExchange {
		public String id;
		public Role role;
		public NormalizedMessage in;
		public NormalizedMessage out;
		public Exception error;
	}

	private static class SimpleHandler implements EventHandler<TrackingEvent> {

		List<SimpleExchange> exchangeList;

		public SimpleHandler(List<SimpleExchange> exchangeList) {
			super();
			this.exchangeList = exchangeList;
		}

		public void handleEvent(TrackingEvent event) {
			MessageExchange exchange = event.getExchange();
			SimpleExchange simpleExchange = new SimpleExchange();
			simpleExchange.id = exchange.getExchangeId();
			simpleExchange.role = exchange.getRole();
			simpleExchange.in = exchange.getMessage("in");
			simpleExchange.out = exchange.getMessage("out");
			simpleExchange.error = exchange.getError();
			exchangeList.add(simpleExchange);
		}

		public EventFilter getEventFilter() {
			return null;
		}

		public String getSubscribedTopic() {
			return EventConstants.TOPIC_TRACKING_EVENT;
		}
	}

	private static class ExceptionThrowableInterceptor implements Interceptor {

		private String message;

	    public ExceptionThrowableInterceptor(String message) {
			this.message = message;
		}

		public void process(MessageExchange exchange) throws SwordfishException {
			throw new SwordfishException(message);
		}

	    public Map<String, ?> getProperties() {
	        return null;
	    }

	}

}
