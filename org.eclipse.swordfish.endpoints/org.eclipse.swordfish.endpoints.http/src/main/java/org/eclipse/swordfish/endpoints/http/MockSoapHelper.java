package org.eclipse.swordfish.endpoints.http;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.wsdl.Definition;

import org.apache.servicemix.jbi.runtime.impl.MessageExchangeFactoryImpl;
import org.apache.servicemix.soap.SoapEndpoint;
import org.apache.servicemix.soap.SoapExchangeProcessor;
import org.apache.servicemix.soap.SoapHelper;

public class MockSoapHelper extends SoapHelper {

    public MockSoapHelper(SoapEndpoint arg0) {
        super(arg0);
    }

    public MockSoapHelper() {
        super(new SoapEndpoint() {
            @Override
            protected SoapExchangeProcessor createConsumerProcessor() {
                return null;
            }

            @Override
            protected ServiceEndpoint createExternalEndpoint() {
                return null;
            }

            @Override
            protected SoapExchangeProcessor createProviderProcessor() {
                return null;
            }

            @Override
            protected void overrideDefinition(Definition arg0) throws Exception {
            }

        });
    }

    @Override
    protected javax.wsdl.Operation findOperation(org.apache.servicemix.soap.Context arg0) throws Exception {
        return null;
    }
    @Override
    protected MessageExchange createExchange(URI mep) throws MessagingException {
        MessageExchangeFactoryImpl exchangeFactoryImpl = new MessageExchangeFactoryImpl(new AtomicBoolean(false));
        return exchangeFactoryImpl.createExchange(mep);
    }
}
