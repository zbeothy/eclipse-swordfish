/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.core.components.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.components.srproxy.impl.DefinitionHelper;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The Class CompoundServiceDescriptionTest.
 */
public class CompoundServiceDescriptionTest extends TestCase {

    /** The sdx. */
    private Definition sdx;

    /** The spdx. */
    private Definition spdx;

    /** The agreed policy. */
    private AgreedPolicy agreedPolicy;

    /** The identity. */
    private UnifiedParticipantIdentity identity;

    /** The dbf. */
    private DocumentBuilderFactory dbf;

    /** The builder. */
    private DocumentBuilder builder;

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.CompoundServiceDescriptionImpl(Definition,
     * Definition, Definition, AgreedPolicy)'
     */
    /**
     * Test compound service description impl.
     */
    public void testCompoundServiceDescriptionImpl() {
        CompoundServiceDescription csd = null;
        try {
            csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            csd = null;
            csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            csd = null;
            csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
            csd = null;
            csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
        csd = null;
        try {
            csd = new CompoundServiceDescriptionImpl(this.sdx, null, null, null);
            fail("Exception expected but not thrown.");
        } catch (Exception e) {
            assertNull(csd);
        }
        try {
            csd = new CompoundServiceDescriptionImpl(null, this.spdx, null, null);
            fail("Exception expected but not thrown.");
        } catch (Exception e) {
            assertNull(csd);
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.createWSDL(QName,
     * String)'
     */
    /**
     * Test create WSDL.
     */
    public void testCreateWSDL() {
        try {
            CompoundServiceDescription csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
            Document doc = csd.createWSDL();
            assertNotNull(doc);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }

    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getAgreedPolicy()'
     */
    /**
     * Test get agreed policy.
     */
    public void testGetAgreedPolicy() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            AgreedPolicy ap = csd.getAgreedPolicy();
            assertNotNull(ap);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /**
     * Test get definition as element.
     */
    public void testGetDefinitionAsElement() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            List e = csd.getWSDLdefinedSchemas();
            for (Iterator iter = e.iterator(); iter.hasNext();) {
                Element element = (Element) iter.next();
                this.printElement(element);

            }
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /**
     * Test get operation.
     */
    public void testGetOperation() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            this.testGetOperationWith(csd, "seekBook", "http://www.w3.org/2004/08/wsdl/in-out", true);
            this.testGetOperationWith(csd, "createLending", "http://www.w3.org/2004/08/wsdl/in-only", true);
            this.testGetOperationWith(csd, "seekBookInBasement", "http://www.w3.org/2004/08/wsdl/in-only", true);
            this.testGetOperationWith(csd, "I_do_not_exist", "http://www.w3.org/2004/08/wsdl/in-only", false);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getOperations()'
     */
    /**
     * Test get operations.
     */
    public void testGetOperations() {
        try {
            CompoundServiceDescription csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
            Collection operations = csd.getOperations();
            assertNotNull(operations);
            assertEquals(4, operations.size());
            for (Iterator iter = operations.iterator(); iter.hasNext();) {
                iter.next();
            }
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getPort(String)'
     */
    /**
     * Test get port.
     */
    public void testGetPort() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            SPDXPort port = csd.getPort("Library_WS-I");
            assertNotNull(port);
            assertEquals("Library_WS-I", port.getName());
            port = null;
            port = csd.getPort("LibraryProvider_jmsPort");
            assertNotNull(port);
            assertEquals("LibraryProvider_jmsPort", port.getName());
            port = null;
            port = csd.getPort("I_do_not_exist");
            assertNull(port);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }

    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getPorts()'
     */
    /**
     * Test get ports.
     */
    public void testGetPorts() {
        try {
            CompoundServiceDescription csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
            SPDXPort[] ports = csd.getPorts();
            assertNotNull(ports);
            assertEquals(3, ports.length);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getPortTypeQName()'
     */
    /**
     * Test get port type Q name.
     */
    public void testGetPortTypeQName() {
        try {
            CompoundServiceDescription csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
            assertEquals("Library", csd.getPortTypeQName().getLocalPart());
            assertEquals("http://services.sopware.org/demos/Library/1.0", csd.getPortTypeQName().getNamespaceURI());
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.testGetReplyEndpointForOperation(String)'
     */
    /**
     * Test get reply endpoint for operation.
     */
    public void testGetReplyEndpointForOperation() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            String ep = csd.getReplyEndpointForOperation("seekBook");
            assertNull(ep);
            ep = csd.getReplyEndpointForOperation("seekBookInBasement");
            assertNotNull(ep);
            String expected = "http://localhost:8888/soap/LibraryConsumer/";
            assertEquals(expected, ep.substring(0, expected.length()));
            assertEquals(expected.length() + 32, ep.length());
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getServiceQName()'
     */
    /**
     * Test get service Q name.
     */
    public void testGetServiceQName() {
        try {
            CompoundServiceDescription csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
            assertEquals("LibraryProvider", csd.getServiceQName().getLocalPart());
            assertEquals("http://services.sopware.org/demos/LibraryProvider/1.0", csd.getServiceQName().getNamespaceURI());
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getSupportedPortLocalNames(String)'
     */
    /**
     * Test get supported port local names.
     */
    public void testGetSupportedPortLocalNames() {
        try {
            CompoundServiceDescription csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
            String[] ports = csd.getSupportedPortLocalNames("seekBook");
            assertNotNull(ports);
            assertEquals(2, ports.length);
            assertTrue((ports[0].equals("Library_WS-I") && ports[1].equals("LibraryProvider_jmsPort"))
                    || (ports[1].equals("Library_WS-I") && ports[0].equals("LibraryProvider_jmsPort")));
            ports = null;
            ports = csd.getSupportedPortLocalNames("seekBookInBasement");
            assertNotNull(ports);
            assertEquals(1, ports.length);
            assertEquals("LibraryProvider_jmsPort", ports[0]);
            ports = null;
            ports = csd.getSupportedPortLocalNames("I_do_not_exist");
            assertEquals(0, ports.length);

        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getSupportedPorts(String)'
     */
    /**
     * Test get supported ports.
     */
    public void testGetSupportedPorts() {
        try {
            CompoundServiceDescription csd = new CompoundServiceDescriptionImpl(this.sdx, this.spdx, null, this.identity);
            assertNotNull(csd);
            SPDXPort[] ports = csd.getSupportedPorts("seekBook");
            assertNotNull(ports);
            assertEquals(2, ports.length);
            assertTrue((ports[0].getName().equals("Library_WS-I") && ports[1].getName().equals("LibraryProvider_jmsPort"))
                    || (ports[1].getName().equals("Library_WS-I") && ports[0].getName().equals("LibraryProvider_jmsPort")));
            ports = null;
            ports = csd.getSupportedPorts("seekBookInBasement");
            assertNotNull(ports);
            assertEquals(1, ports.length);
            assertEquals("LibraryProvider_jmsPort", ports[0].getName());
            ports = null;
            ports = csd.getSupportedPorts("I_do_not_exist");
            assertEquals(0, ports.length);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.hasPartnerDescription()'
     */
    /**
     * Test has partner description.
     */
    public void testHasPartnerDescription() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            assertTrue(csd.hasPartnerDescription());
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /**
     * Test is notification port.
     */
    public void testIsNotificationPort() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            assertTrue(csd.isNotificationPort("LibraryNotificationProvider_jmsPort"));
            assertFalse(csd.isNotificationPort("LibraryProvider_jmsPort"));
            assertFalse(csd.isNotificationPort("Library_WS"));
            assertFalse(csd.isNotificationPort("I_do_not_exist"));
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.isPartnerDescription()'
     */
    /**
     * Test is partner description.
     */
    public void testIsPartnerDescription() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            assertFalse(csd.isPartnerDescription());
            csd = csd.getPartnerDescription();
            assertNotNull(csd);
            assertTrue(csd.isPartnerDescription());
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.isPartnerOperation(String)'
     */
    /**
     * Test is partner operation.
     */
    public void testIsPartnerOperation() {
        try {
            CompoundServiceDescription csd =
                    new CompoundServiceDescriptionImpl(this.sdx, this.spdx, this.agreedPolicy, this.identity);
            assertNotNull(csd);
            assertFalse(csd.isPartnerOperation("seekBook"));
            assertTrue(csd.isPartnerOperation("seekBookInBasement"));
            assertFalse(csd.isPartnerOperation("seekBookInBasementResponse"));
            assertFalse(csd.isPartnerOperation("I_do_not_exist"));
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.dbf = DocumentBuilderFactory.newInstance();
        this.dbf.setNamespaceAware(true);
        this.builder = this.dbf.newDocumentBuilder();
        WSDLFactory factory = WSDLFactory.newInstance();
        ExtensionRegistry reg = factory.newPopulatedExtensionRegistry();
        WSDLReader reader = factory.newWSDLReader();
        reader.setExtensionRegistry(reg);
        reader.setFeature("javax.wsdl.importDocuments", false);
        reader.setFeature("javax.wsdl.verbose", false);

        DefinitionHelper helper = DefinitionHelper.getInstance();

        this.sdx = helper.elementToDefinition(this.elementFromStream(this.getClass().getResourceAsStream("Library.sdx")));
        this.spdx = helper.elementToDefinition(this.elementFromStream(this.getClass().getResourceAsStream("LibraryProvider.spdx")));
        this.agreedPolicy =
                AgreedPolicyFactory.getInstance().createFrom(this.getClass().getResourceAsStream("LibraryProvider.agreedpolicy"));
        this.identity = new UnifiedParticipantIdentity(new InternalParticipantIdentity() {

            public String getApplicationID() {
                return "Library";
            }

            public String getInstanceID() {
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.sdx = null;
        this.spdx = null;
        this.agreedPolicy = null;
        super.tearDown();
    }

    /**
     * Element from stream.
     * 
     * @param in
     *        the in
     * 
     * @return the element
     * 
     * @throws SAXException
     * @throws IOException
     */
    private Element elementFromStream(final InputStream in) throws SAXException, IOException {
        Document doc = this.builder.parse(in);
        return doc.getDocumentElement();
    }

    /**
     * Prints the element.
     * 
     * @param el
     *        the el
     */
    private void printElement(final Element el) {
        OutputFormat format = new OutputFormat();
        format.setOmitXMLDeclaration(true);
        format.setIndenting(true);
        format.setStandalone(false);
        format.setIndent(3);
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        try {
            serial.serialize(el);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(stringOut.toString());
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl.getOperation(String)'
     */
    /**
     * Test get operation with.
     * 
     * @param csd
     *        the csd
     * @param operationName
     *        the operation name
     * @param exchangePattern
     *        the exchange pattern
     * @param exists
     *        the exists
     */
    private void testGetOperationWith(final CompoundServiceDescription csd, final String operationName,
            final String exchangePattern, boolean exists) {
        OperationDescription opdesc = csd.getOperation(operationName);
        if (!exists) {
            assertNull(opdesc);
            return;
        }
        assertNotNull(opdesc);
        assertEquals(operationName, opdesc.getName());
        assertEquals(exchangePattern, opdesc.getExchangePattern().toASCIIString());
        assertSame(csd, opdesc.getServiceDescription());
    }
}
