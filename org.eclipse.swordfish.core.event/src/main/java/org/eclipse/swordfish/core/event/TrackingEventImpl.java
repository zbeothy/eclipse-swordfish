package org.eclipse.swordfish.core.event;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.TrackingEvent;

public class TrackingEventImpl extends EventImpl implements TrackingEvent {

	private MessageExchange exchange;
	
    public TrackingEventImpl(MessageExchange exchange) {
		super();
		this.exchange = exchange;
	}

	public String getTopic() {
       return EventConstants.TOPIC_TRACKING_EVENT;
    }

    public int getMessageExchangeId() {
        throw new UnsupportedOperationException("method not implemented yet");
    }

	public int getSeverity() {
		return (Integer)getProperty(EventConstants.EVENT_SEVERITY);
	}

	public void setSeverity(int severity) {
		setProperty(EventConstants.EVENT_SEVERITY, severity);
	}

	public MessageExchange getExchange() {
		return exchange;
	}
}
