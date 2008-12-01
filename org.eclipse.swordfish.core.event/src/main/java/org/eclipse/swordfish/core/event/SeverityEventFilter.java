package org.eclipse.swordfish.core.event;

import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.EventFilter;
import org.eclipse.swordfish.api.event.Severity;

public class SeverityEventFilter implements EventFilter {

	private int severity = Severity.ALL;
	
	public SeverityEventFilter(int severity) {
		super();
		this.severity = severity;
	}

	public String getExpression() {
		return String.format("(%s>=%s)", EventConstants.EVENT_SEVERITY, severity);
	}
}
