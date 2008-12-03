/**
 *
 */
package org.eclipse.swordfish.core.test.mock;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SwordfishException;

/**
 * @author dwolz
 *
 */
public class MockInterceptor implements Interceptor {
    private List<MessageExchange> exchanges = new CopyOnWriteArrayList<MessageExchange>();
	public Map<String, ?> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}


	public void process(MessageExchange exchange) throws SwordfishException {
	    exchanges.add(exchange);
	}

    public List<MessageExchange> getExchanges() {
        return exchanges;
    }
}
