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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.swordfish.core.components.srproxy.ServiceInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceNotFoundException;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class contains JUnit tests for the class ServiceProviderDescriptionImpl.
 * 
 */
public class TestSrProxyBean extends TestCase {

    /** The Constant SDX_QNAME. */
    private static final QName SDX_QNAME = new QName("irrelevantTNS", "irrelevant");

    /** The Constant WSDL_IN_CONTENT_LIST. */
    private static final String WSDL_IN_CONTENT_LIST =
            "<wsdlContentList><wsdlContent><annotationSummary/><definitions xmlns=\"http://schemas.xmlsoap.org/wsdl/\">"
                    + "<types/></definitions></wsdlContent></wsdlContentList>";

    /** The Constant OTHER_TAG_IN_CONTENT_LIST. */
    private static final String OTHER_TAG_IN_CONTENT_LIST =
            "<wsdlContentList><wsdlContent><hereShouldBeAWSDL/></wsdlContent></wsdlContentList>";

    /** The Constant NO_TAG_IN_CONTENT_LIST. */
    private static final String NO_TAG_IN_CONTENT_LIST = "<wsdlContentList><wsdlContent></wsdlContent></wsdlContentList>";

    /** The Constant WSDL_IN_CONTENT_LIST_W_NS. */
    private static final String WSDL_IN_CONTENT_LIST_W_NS =
            "<wsdlContentList xmlns=\"http://types.sopware.org/registry/ServiceRegistry/1.0\" xmlns:uddi=\"urn:uddi-org:api_v3\">\r\n"
                    + "    <wsdlContent>\r\n"
                    + "        <annotationSummary xmlns=\"http://types.sopware.org/registry/ServiceRegistry/1.0\"/>\r\n"
                    + "        <definitions name=\"HelloLibrary3.spdx\" targetNamespace=\"www-deutschepost-de/TestDomain/HelloLibrary/1.0_binding\"\r\n"
                    + "            xmlns=\"http://schemas.xmlsoap.org/wsdl/\"><types/></definitions>\r\n"
                    + "    </wsdlContent>\r\n" + "</wsdlContentList>";

    /** The Constant WSDL_IN_SERVICEPROVIDERDETAIL_W_NS. */
    private static final String WSDL_IN_SERVICEPROVIDERDETAIL_W_NS =
            "    <sr:serviceProviderDetail xmlns:sr=\"http://types.sopware.org/registry/ServiceRegistry/1.0\">\r\n"
                    + "        <sr:wsdlContent>\r\n"
                    + "            <annotationSummary xmlns=\"http://types.sopware.org/registry/ServiceRegistry/1.0\"/>\r\n"
                    + "            <definitions name=\"HelloLibrary3.spdx\"\r\n"
                    + "                targetNamespace=\"www-deutschepost-de/TestDomain/HelloLibrary/1.0_binding\"\r\n"
                    + "                xmlns=\"http://schemas.xmlsoap.org/wsdl/\">\r\n"
                    + "                <service name=\"HelloLibrary_httpSoap\"/>\r\n" + "            </definitions>\r\n"
                    + "        </sr:wsdlContent>\r\n" + "        <sr:policyContent>\r\n"
                    + "            <annotationSummary xmlns=\"http://types.sopware.org/registry/ServiceRegistry/1.0\"/>\r\n"
                    + "            <Agreed xmlns=\"http://types.sopware.org/qos/AgreedPolicy/1.0\"/>\r\n"
                    + "        </sr:policyContent>\r\n" + "    </sr:serviceProviderDetail>\r\n";

    /** The Constant WSDL_IN_SERVICEPROVIDERSDETAIL_W_NS. */
    private static final String WSDL_IN_SERVICEPROVIDERSDETAIL_W_NS =
            "<sr:serviceProvidersDetail xmlns:sr=\"http://types.sopware.org/registry/ServiceRegistry/1.0\" xmlns:uddi=\"urn:uddi-org:api_v3\">\r\n"
                    + "    <sr:serviceProviderDetail>\r\n"
                    + "        <sr:wsdlContent>\r\n"
                    + "            <annotationSummary xmlns=\"http://types.sopware.org/registry/ServiceRegistry/1.0\"/>\r\n"
                    + "            <definitions name=\"HelloLibrary3.spdx\"\r\n"
                    + "                targetNamespace=\"www-deutschepost-de/TestDomain/HelloLibrary/1.0_binding\"\r\n"
                    + "                xmlns=\"http://schemas.xmlsoap.org/wsdl/\">\r\n"
                    + "                <service name=\"HelloLibrary_httpSoap\"/>\r\n"
                    + "            </definitions>\r\n"
                    + "        </sr:wsdlContent>\r\n"
                    + "        <sr:policyContent>\r\n"
                    + "            <annotationSummary xmlns=\"http://types.sopware.org/registry/ServiceRegistry/1.0\"/>\r\n"
                    + "            <Agreed xmlns=\"http://types.sopware.org/qos/AgreedPolicy/1.0\"/>\r\n"
                    + "        </sr:policyContent>\r\n"
                    + "    </sr:serviceProviderDetail>\r\n"
                    + "    <sr:serviceProviderDetail>\r\n"
                    + "        <sr:wsdlContent>\r\n"
                    + "            <annotationSummary xmlns=\"http://types.sopware.org/registry/ServiceRegistry/1.0\"/>\r\n"
                    + "            <definitions name=\"HelloLibrary3.spdx\"\r\n"
                    + "                targetNamespace=\"www-deutschepost-de/TestDomain/HelloLibrary/1.0_binding\"\r\n"
                    + "                xmlns=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:jms=\"http://schemas.xmlsoap.org/wsdl/jms/\">\r\n"
                    + "                <service name=\"HelloLibrary_httpSoap2\"/>\r\n"
                    + "            </definitions>\r\n"
                    + "        </sr:wsdlContent>\r\n"
                    + "        <sr:policyContent>\r\n"
                    + "            <annotationSummary xmlns=\"http://types.sopware.org/registry/ServiceRegistry/1.0\"/>\r\n"
                    + "            <Agreed xmlns=\"http://types.sopware.org/qos/AgreedPolicy/1.0\"/>\r\n"
                    + "        </sr:policyContent>\r\n"
                    + "    </sr:serviceProviderDetail>\r\n"
                    + "</sr:serviceProvidersDetail>\r\n" + "";

    /** The Constant EMPTY_SERVICEPROVIDERSDETAIL_W_NS. */
    private static final String EMPTY_SERVICEPROVIDERSDETAIL_W_NS =
            "<sr:serviceProvidersDetail xmlns:sr=\"http://types.sopware.org/registry/ServiceRegistry/1.0\" xmlns:uddi=\"urn:uddi-org:api_v3\"/>";

    /** The Constant CONSUMER_PARTICIPANT_POLICY. */
    private static final String CONSUMER_PARTICIPANT_POLICY =
            "http://types.sopware.org/qos/ParticipantPolicy/1.1/DefaultConsumerPolicy";

    /**
     * Start method for test case.
     * 
     * @param args
     *        arguments
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(TestSrProxyBean.class);
    }

    /**
     * Constructor for TestSrProxyBean.
     * 
     * @param name
     *        the name
     */
    public TestSrProxyBean(final String name) {
        super();
        this.setName(name);
    }

    /**
     * Utility method.
     * 
     * @param xml
     *        XML data as String
     * 
     * @return XML data as Element
     * 
     * @throws Exception
     *         on error
     */
    public Element stringToElement(final String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document testDoc = null;
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = null;
        builder = dbf.newDocumentBuilder();
        testDoc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        return testDoc.getDocumentElement();
    }

    /**
     * Test extract definitions fail.
     * 
     * @throws Exception
     */
    public void testExtractDefinitionsFail() throws Exception {
        SrProxyBean srProxy = new SrProxyBean();

        assertNull(srProxy.extractDefinitions(this.stringToElement(OTHER_TAG_IN_CONTENT_LIST)));
        assertNull(srProxy.extractDefinitions(this.stringToElement(NO_TAG_IN_CONTENT_LIST)));
    }

    /**
     * Test extract definitions success.
     * 
     * @throws Exception
     */
    public void testExtractDefinitionsSuccess() throws Exception {
        SrProxyBean srProxy = new SrProxyBean();

        this.assertIsWSDL(srProxy.extractDefinitions(this.stringToElement(WSDL_IN_CONTENT_LIST)));

        String wsdlContentHasPreviousSiblingPartnerDescriptions =
                "<wsdlContentList>  <partnerDescriptions/>  <wsdlContent>  <annotationSummary/>   <definitions xmlns=\"http://schemas.xmlsoap.org/wsdl/\">"
                        + "<types/></definitions></wsdlContent></wsdlContentList>";
        this.assertIsWSDL(srProxy.extractDefinitions(this.stringToElement(wsdlContentHasPreviousSiblingPartnerDescriptions)));

        this.assertIsWSDL(srProxy.extractDefinitions(this.stringToElement(WSDL_IN_CONTENT_LIST_W_NS)));

        this.assertIsWSDL(srProxy.extractDefinitions(this.stringToElement(WSDL_IN_SERVICEPROVIDERDETAIL_W_NS)));
    }

    /**
     * Test find service providers remote fail.
     * 
     * @throws Exception
     */
    public void testFindServiceProvidersRemoteFail() throws Exception {

        ArrayList echoList = new ArrayList();
        echoList.add(EMPTY_SERVICEPROVIDERSDETAIL_W_NS);

        SrProxyBean srProxy = this.getSrProxyBean(echoList);

        try {
            srProxy.findServiceProvidersRemote(SDX_QNAME, CONSUMER_PARTICIPANT_POLICY);
            fail("Expected ServiceProviderNotFoundException");
        } catch (ServiceProviderNotFoundException e) {
            // expected
        }
    }

    /**
     * Test find service providers remote success.
     * 
     * @throws Exception
     */
    public void testFindServiceProvidersRemoteSuccess() throws Exception {

        ArrayList echoList = new ArrayList();
        echoList.add(WSDL_IN_SERVICEPROVIDERSDETAIL_W_NS);

        SrProxyBean srProxy = this.getSrProxyBean(echoList);

        Collection collection = null;
        collection = srProxy.findServiceProvidersRemote(SDX_QNAME, CONSUMER_PARTICIPANT_POLICY);
        Iterator it = collection.iterator();
        int spdxCount = 0;
        while (it.hasNext()) {
            ServiceProviderInfo providerInfo = (ServiceProviderInfo) it.next();
            assertEquals("www-deutschepost-de/TestDomain/HelloLibrary/1.0_binding", providerInfo.getServiceProviderDescription()
                .getQName().getNamespaceURI());
            spdxCount++;
        }
        assertTrue(spdxCount == 2);
    }

    /**
     * Test get service description remote fail.
     * 
     * @throws Exception
     */
    public void testGetServiceDescriptionRemoteFail() throws Exception {

        ArrayList echoList = new ArrayList();
        echoList.add(OTHER_TAG_IN_CONTENT_LIST);
        echoList.add(NO_TAG_IN_CONTENT_LIST);

        SrProxyBean srProxy = this.getSrProxyBean(echoList);

        try {
            srProxy.getServiceDescriptionRemote(SDX_QNAME);
            fail("Expected ServiceNotFoundException");
        } catch (ServiceNotFoundException e) {
            // expected
        }

        try {
            srProxy.getServiceDescriptionRemote(SDX_QNAME);
            fail("Expected ServiceNotFoundException");
        } catch (ServiceNotFoundException e) {
            // expected
        }
    }

    /**
     * Test get service description remote success.
     * 
     * @throws Exception
     */
    public void testGetServiceDescriptionRemoteSuccess() throws Exception {

        ArrayList echoList = new ArrayList();
        echoList.add(WSDL_IN_CONTENT_LIST_W_NS);
        echoList.add(WSDL_IN_CONTENT_LIST);

        SrProxyBean srProxy = this.getSrProxyBean(echoList);

        ServiceInfo info = null;
        info = srProxy.getServiceDescriptionRemote(SDX_QNAME);
        this.assertIsWSDL(info.getServiceDescriptionAsElement());

        info = srProxy.getServiceDescriptionRemote(SDX_QNAME);
        this.assertIsWSDL(info.getServiceDescriptionAsElement());
    }

    /**
     * Sets the up.
     * 
     * @throws Exception
     * 
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
    }

    /**
     * Tear down.
     * 
     * @throws Exception
     * 
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
    }

    /**
     * Utility method.
     * 
     * @param wsdlCandidate
     *        the wsdl candidate
     * 
     * @throws IOException
     */
    private void assertIsWSDL(final Element wsdlCandidate) throws IOException {
        assertTrue(this.serialized(wsdlCandidate).endsWith("definitions>"));
    }

    /**
     * Utility method.
     * 
     * @param echoList
     *        the echo list
     * 
     * @return an SrProxyBean with a list of service registry responses that the internal
     *         InternalSBB proxy within the SrProxyBean will receive in that order.
     * 
     * @throws Exception
     */
    private SrProxyBean getSrProxyBean(final ArrayList echoList) throws Exception {
        SrProxyBean srProxy = new SrProxyBean() {

            /** overridden to omit prefilling of cache */
            @Override
            public void init() throws Exception {
                this.setXfindSDXTemplateHandler(new TemplateHandler());
                this.setXfindServiceProvidersTemplateHandler(new TemplateHandler());
                this.setXfindSPDXandPoliciesTemplateHandler(new TemplateHandler());
                this.setXgetSDXTemplateHandler(new TemplateHandler());
            }
        };
        srProxy.setCache(new SrProxyCacheBean());
        srProxy.init();
        MockInternalProxy fakeSBBcalls = new MockInternalProxy(echoList);
        srProxy.setConsumerProxy(fakeSBBcalls);
        return srProxy;
    }

    /**
     * Utility method.
     * 
     * @param result
     *        the result
     * 
     * @return the string
     * 
     * @throws IOException
     */
    private String serialized(final Element result) throws IOException {
        XMLSerializer serializer = new XMLSerializer();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        serializer.setOutputByteStream(outStream);

        serializer.serialize(result);
        return new String(outStream.toByteArray());
    }
}
