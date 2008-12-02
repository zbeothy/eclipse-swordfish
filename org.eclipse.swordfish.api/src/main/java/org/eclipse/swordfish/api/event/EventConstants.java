package org.eclipse.swordfish.api.event;
/**
 * specified base event topic prefix with sould be base prefix for all Swordfish events,
 * specified other standard swordfish events topics, like message tracking events and operational events
 * @author akopachevsky
 */
public interface EventConstants {

	/**
	 * Base path for all swordfish event identifiers.
	 */
	String TOPIC_BASE = "org/eclipse/runtime/swordfish/";

    /**
     * Full topic name of Swordfish events provided for operational logging.
     */
    String TOPIC_OPERATION_EVENT = TOPIC_BASE + OperationEvent.class.getSimpleName();

    /**
     *  Full topic name of Swordfish events provided for message tracking.
     */
    String TOPIC_TRACKING_EVENT = TOPIC_BASE + TrackingEvent.class.getSimpleName();

    /**
     * Full topic name of Swordfish events indicating configuration changes.
     */
    String TOPIC_CONFIGURATION_EVENT = TOPIC_BASE + ConfigurationEvent.class.getSimpleName();

    /**
     * property name for event severity, see org.eclipse.swordfish.api.event.Severity
     */
    String EVENT_SEVERITY = "event.severity";
}
