package org.eclipse.swordfish.core.test.event;


import org.eclipse.swordfish.api.event.Event;
import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.api.event.SeverityAware;

public abstract class SeverityAwareEventHandler<T extends Event> implements EventHandler<T>, SeverityAware {
}
