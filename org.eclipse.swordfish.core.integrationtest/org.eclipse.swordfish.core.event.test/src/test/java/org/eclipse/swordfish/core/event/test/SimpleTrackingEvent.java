package org.eclipse.swordfish.core.event.test;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.core.event.TrackingEventImpl;

public class SimpleTrackingEvent extends TrackingEventImpl {

	public static final String TOPIC = EventConstants.TOPIC_TRACKING_EVENT + "TEST";
	
	public SimpleTrackingEvent(MessageExchange exchange) {
		super(exchange);
	}

	public String getTopic() {
		return TOPIC;
	}
	
}