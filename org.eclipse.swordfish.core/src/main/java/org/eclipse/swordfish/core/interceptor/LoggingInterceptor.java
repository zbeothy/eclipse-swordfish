package org.eclipse.swordfish.core.interceptor;



import java.util.HashMap;
import java.util.Map;

import javax.jbi.messaging.MessageExchange;
import javax.xml.transform.Source;

import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.Type;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.core.util.ServiceMixSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LoggingInterceptor implements Interceptor {
private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
    private Map<String, ?> properties = new HashMap<String, Object>();
    public void process(MessageExchange messageExchange) throws SwordfishException {
        try {
            Exchange exchange = ServiceMixSupport.toNMRExchange(messageExchange);
                SourceTransformer transformer = new SourceTransformer();
                String request = exchange.getMessage(Type.In) != null ? transformer.toString((Source)exchange.getMessage(Type.In).getBody()) :
                    null;
                String response = exchange.getMessage(Type.Out) != null ? transformer.toString((Source)exchange.getMessage(Type.Out).getBody()) :
                    null;
                LOG.info(String.format("Received messageExchange with request = [%s] and response = [%s]", request, response));
        } catch (Exception ex) {
		throw new RuntimeException(ex);
        }

    }

    public Map<String, ?> getProperties() {
        return properties;
    }
}
