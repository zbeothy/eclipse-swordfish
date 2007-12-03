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
package org.eclipse.swordfish.configrepos.scopepath.query.dom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.PathPartImpl;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.ScopePathImpl;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.impl.ConfigurationQueryImpl;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.impl.GetConfigurationImpl;
import org.w3c.dom.Document;

/**
 * The Class GetConfigurationTest.
 */
public class GetConfigurationTest extends TestCase {

    /** The tfactory. */
    private TransformerFactory tfactory = null;

    /**
     * Instantiates a new get configuration test.
     * 
     * @param name
     *        the name
     */
    public GetConfigurationTest(final String name) {
        super(name);
    }

    /**
     * Test compile get configuration.
     */
    public final void testCompileGetConfiguration() {
        try {
            ScopePath scopePath = new ScopePathImpl();
            scopePath.setSeparator("/");
            PathPart pathPart = new PathPartImpl();
            pathPart.setValue("Bonn");
            pathPart.setType("Location");
            scopePath.getPathPart().add(pathPart);
            pathPart = new PathPartImpl();
            pathPart.setValue("Library");
            pathPart.setType("Application");
            scopePath.getPathPart().add(pathPart);
            ConfigurationQuery query = new ConfigurationQueryImpl();
            query.setScopePath(scopePath);
            query.setTree("SBB");
            GetConfiguration request = new GetConfigurationImpl();
            request.setConfigurationQuery(query);
            this.outputConfigurationQuery(request);
        } catch (IOException ioe) {
            fail(ioe.getMessage());
        } catch (ParserConfigurationException pce) {
            fail(pce.getMessage());
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
        this.tfactory = TransformerFactory.newInstance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.tfactory = null;
        super.tearDown();
    }

    /**
     * Output configuration query.
     * 
     * @param aRequest
     *        the a request
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private void outputConfigurationQuery(final GetConfiguration aRequest) throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().newDocument();
        aRequest.marshal(document);

        ByteArrayOutputStream baos;
        try {
            Transformer trans = this.tfactory.newTransformer();
            Properties props = new Properties();
            props.setProperty(OutputKeys.INDENT, "yes");
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "false");
            props.setProperty(OutputKeys.ENCODING, "UTF-8");
            props.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
            props.setProperty(OutputKeys.METHOD, "xml");
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "false");

            baos = new ByteArrayOutputStream();

            trans.transform(new DOMSource(document), new StreamResult(baos));

            System.out.println(baos.toString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
