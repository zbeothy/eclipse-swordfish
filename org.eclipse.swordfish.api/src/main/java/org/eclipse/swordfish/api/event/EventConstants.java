package org.eclipse.swordfish.api.event;
/**
 * specified base event topic prefix with sould be base prefix for all Swordfish events,
 * specified other standard swordfish events topics, like message tracking events and operational events
 * @author akopachevsky
 */
public interface EventConstants {
    String TOPIC_BASE = "org/eclipse/runtime/swordfish/";
    String TOPIC_OPERATION_EVENT = TOPIC_BASE + OperationEvent.class.getSimpleName();
    String TOPIC_TRACKING_EVENT = TOPIC_BASE + TrackingEvent.class.getSimpleName();
    String TOPIC_CONFIGURATION_EVENT = TOPIC_BASE + ConfigurationEvent.class.getSimpleName();
}
