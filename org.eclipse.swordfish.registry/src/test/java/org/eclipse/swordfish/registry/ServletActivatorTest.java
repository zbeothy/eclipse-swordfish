package org.eclipse.swordfish.registry;

import static org.easymock.EasyMock.*;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;


public class ServletActivatorTest {
	
	public static final String ALIAS = "/registry/wsdl";

	private LookupServlet servlet = new LookupServlet();

	private HttpService httpService = createMock(HttpService.class);

	private ServletActivator activator = new ServletActivator();

	@Before
	public void setup() {
		unsetLocationProperty();
		
		activator.setHttpService(httpService);
		activator.setServlet(servlet);		
	}

	@After
	public void tearDown() {
		unsetLocationProperty();
	}

	@Test
	public void givenStartCalledAndFileLocationPropertySetShouldRegisterServlet() throws Exception {
		Dictionary<String, String> params = servletParams("directory");
		setLocationProperty("directory");

		httpService.registerServlet(ALIAS, servlet, params, null);
		replay(httpService);

		activator.start();
		
		verify(httpService);
	}

	@Test
	public void givenStopCalledShouldUnregisterServlet() throws Exception {

		httpService.unregister(ALIAS);
		replay(httpService);

		activator.stop();
		
		verify(httpService);
	}

	@Test
	public void givenStartCalledAndFileLocationPropertyNotSetShouldRegisterServlet() throws Exception {		
		try {
			activator.start();
			fail("A RegistryException should have been thrown.");
		} catch (RegistryException e) {
		}
	}

	@Test
	public void givenStartCalledAndNameSpaceExceptionThrownWhenRegisteringShouldForwardException() throws Exception {
		NamespaceException throwIt = new NamespaceException("");
		Dictionary<String, String> params = servletParams("directory");
		setLocationProperty("directory");

		httpService.registerServlet(ALIAS, servlet, params, null);
		expectLastCall().andThrow(throwIt);
		replay(httpService);

		try {
			activator.start();
			fail("A RegistryException should have been thrown.");
		} catch (RegistryException e) {
			Throwable expected = throwIt;
			assertThat(e.getCause(), sameInstance(expected));
		}
		
		verify(httpService);
	}

	@Test
	public void givenStartCalledAndServletExceptionThrownWhenRegisteringShouldForwardException() throws Exception {
		Throwable throwIt = new ServletException("");
		Dictionary<String, String> params = servletParams("directory");
		setLocationProperty("directory");

		httpService.registerServlet(ALIAS, servlet, params, null);
		expectLastCall().andThrow(throwIt);
		replay(httpService);

		try {
			activator.start();
			fail("A RegistryException should have been thrown.");
		} catch (RegistryException e) {
			assertThat(e.getCause(), sameInstance(throwIt));
		}
		
		verify(httpService);
	}

	private static Dictionary<String, String> servletParams(String locationValue) {
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("wsdlLocation", locationValue);

		return params;
	}
	
	private static void setLocationProperty(String location) {
		System.getProperties().put("org.eclipse.swordfish.registry.fileLocation", location);		
	}

	private static void unsetLocationProperty() {
		System.getProperties().remove("org.eclipse.swordfish.registry.fileLocation");		
	}
}
