package org.eclipse.swordfish.core.event;


import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.OperationEvent;


public class OperationEventImpl extends EventImpl implements OperationEvent {

    public String getTopic() {
        return EventConstants.TOPIC_OPERATION_EVENT;
    }

}
