package org.eclipse.swordfish.core.event;


import org.eclipse.swordfish.api.event.Severity;
import org.eclipse.swordfish.api.event.SeverityAware;

public class SeverityAwareEventImpl extends EventImpl implements SeverityAware {
    private int severity = Severity.NONE;
    
    public int getSeverity() {
        return severity;
    }
    
    public void setSeverity(int severity){
        this.severity = severity;
    }
}
