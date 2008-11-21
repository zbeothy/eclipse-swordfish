package org.eclipse.swordfish.core.test.exception;

import java.util.Map;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SwordfishException;

public class ExceptionThrowableInterceptor implements Interceptor {

    protected SwordfishException exception;

	public ExceptionThrowableInterceptor(SwordfishException exception) {
        super();
        this.exception = exception;
    }



    public void process(MessageExchange exchange) throws SwordfishException {
		throw exception;
	}



    public Map<String, ?> getProperties() {
        return null;
    }

}