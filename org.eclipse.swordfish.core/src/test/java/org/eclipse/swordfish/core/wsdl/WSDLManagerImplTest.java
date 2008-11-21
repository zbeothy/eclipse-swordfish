package org.eclipse.swordfish.core.wsdl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.Map;

import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
@Ignore
public class WSDLManagerImplTest {
	private WSDLManagerImpl wsdlManager;
	public static final QName BOOKINGSERVICE_PORTTYPE_NAME = new QName("http://cxf.samples.swordfish.eclipse.org/", "BookingService");
	@Before
	public void setUp() throws Exception {
		wsdlManager = new WSDLManagerImpl();
		//wsdlManager.setupWSDLs("BookingServiceImpl.zip");
	}
	@Test
	public void test1AvailableLocations() throws Exception {
		ServiceDescription description = wsdlManager.getServiceDescription(BOOKINGSERVICE_PORTTYPE_NAME);
		 Map<SOAPAddress, SOAPBinding> availableLocations =  description.getAvailableLocations();
		 assertEquals(availableLocations.size(), 1);
		 Map.Entry<SOAPAddress, SOAPBinding> entry = availableLocations.entrySet().iterator().next();
		 assertNotNull(entry);
		 assertEquals(entry.getKey().getLocationURI().toString(),"http://localhost:9090/BookingServiceImplPort");
		 assertEquals(entry.getValue().getTransportURI().toString(), "http://schemas.xmlsoap.org/soap/http");

	}
}
