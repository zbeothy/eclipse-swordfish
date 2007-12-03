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
package org.eclipse.swordfish.configrepos.scopepath.basic.dom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.PathPartImpl;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.ScopePathImpl;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.w3c.dom.Document;

/**
 * The Class ScopePathTest.
 */
public class ScopePathTest extends TestCase {

    /** The tfactory. */
    private TransformerFactory tfactory = null;

    /**
     * Instantiates a new scope path test.
     * 
     * @param name
     *        the name
     */
    public ScopePathTest(final String name) {
        super(name);
    }

    /**
     * Test whether it is possible to add a path part to the scope path.
     */
    public final void testAddInvalidPathPart() {
        try {
            ScopePath scopePath = new ScopePathImpl();
            scopePath.setSeparator(".");
            PathPart pathPart = new PathPartImpl();
            pathPart.setValue("Bonn");
            scopePath.getPathPart().add(pathPart);
            assertTrue(scopePath.getPathPart().contains(pathPart));

            // just output something
            this.outputScopePath(scopePath);

        } catch (Exception jaxbe) {
            // Great! We found an error
        }
    }

    /*
     * Test whether it is possible to add a path part to the scope path
     */
    /**
     * Test add longer valid path part.
     */
    public final void testAddLongerValidPathPart() {
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
            assertTrue(scopePath.getPathPart().contains(pathPart));

            // just output something
            this.outputScopePath(scopePath);
            assertEquals("Location=Bonn/Application=Library", scopePath.toString());
        } catch (Exception jaxbe) {
            fail(jaxbe.getMessage());
        }
    }

    /*
     * Test whether it is possible to add a path part to the scope path
     */
    /**
     * Test add valid path part.
     */
    public final void testAddValidPathPart() {
        try {
            ScopePath scopePath = new ScopePathImpl();
            scopePath.setSeparator(".");
            PathPart pathPart = new PathPartImpl();
            pathPart.setValue("Bonn");
            pathPart.setType("Location");
            scopePath.getPathPart().add(pathPart);
            assertTrue(scopePath.getPathPart().contains(pathPart));

            // just output something
            this.outputScopePath(scopePath);
            assertEquals("Location=Bonn", scopePath.toString());
        } catch (Exception jaxbe) {
            fail(jaxbe.getMessage());
        }
    }

    /**
     * Test equality.
     */
    public final void testEquality() {
        ScopePath scopePathA = new ScopePathImpl();
        scopePathA.setSeparator("/");
        PathPart pathPart = new PathPartImpl();
        pathPart.setValue("Bonn");
        pathPart.setType("Location");
        scopePathA.getPathPart().add(pathPart);
        pathPart = new PathPartImpl();
        pathPart.setValue("Library");
        pathPart.setType("Application");
        scopePathA.getPathPart().add(pathPart);

        ScopePath scopePathB = new ScopePathImpl();
        scopePathB.setSeparator("/");
        pathPart = new PathPartImpl();
        pathPart.setValue("Bonn");
        pathPart.setType("Location");
        scopePathB.getPathPart().add(pathPart);
        pathPart = new PathPartImpl();
        pathPart.setValue("Library");
        pathPart.setType("Application");
        scopePathB.getPathPart().add(pathPart);

        assertTrue(scopePathA.equals(scopePathB));
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.configrepos.scopepath.basic.impl.PathTypeImpl.getPathPart()'
     */
    /**
     * Test get path part.
     */
    public final void testGetPathPart() {
        try {
            ScopePath scopePath = new ScopePathImpl();
            assertNotNull(scopePath.getPathPart());
        } catch (Exception jaxbe) {
            fail(jaxbe.getMessage());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.configrepos.scopepath.basic.impl.PathTypeImpl.getSeparator()'
     */
    /**
     * Test get separator.
     */
    public final void testGetSeparator() {
        try {
            ScopePath scopePath = new ScopePathImpl();
            assertEquals(ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR, scopePath.getSeparator());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test non equality.
     */
    public final void testNonEquality() {
        ScopePath scopePathA = new ScopePathImpl();
        scopePathA.setSeparator("/");
        PathPart pathPart = new PathPartImpl();
        pathPart.setValue("Berlin");
        pathPart.setType("Location");
        scopePathA.getPathPart().add(pathPart);
        pathPart = new PathPartImpl();
        pathPart.setValue("Library");
        pathPart.setType("Application");
        scopePathA.getPathPart().add(pathPart);

        ScopePath scopePathB = new ScopePathImpl();
        scopePathB.setSeparator("/");
        pathPart = new PathPartImpl();
        pathPart.setValue("Bonn");
        pathPart.setType("Location");
        scopePathB.getPathPart().add(pathPart);
        pathPart = new PathPartImpl();
        pathPart.setValue("Library");
        pathPart.setType("Application");
        scopePathB.getPathPart().add(pathPart);

        assertFalse(scopePathA.equals(scopePathB));
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.configrepos.scopepath.basic.impl.PathTypeImpl.setSeparator(String)'
     */
    /**
     * Test set separator.
     */
    public final void testSetSeparator() {
        try {
            ScopePath scopePath = new ScopePathImpl();
            scopePath.setSeparator(".");
            assertEquals(".", scopePath.getSeparator());
        } catch (Exception jaxbe) {
            fail(jaxbe.getMessage());
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
     * Output scope path.
     * 
     * @param scopePath
     *        the scope path
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private void outputScopePath(final ScopePath scopePath) throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        scopePath.marshal(document);

        try {
            Transformer trans = this.tfactory.newTransformer();
            Properties props = new Properties();
            props.setProperty(OutputKeys.INDENT, "yes");
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "false");
            props.setProperty(OutputKeys.ENCODING, "UTF-8");
            props.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
            props.setProperty(OutputKeys.METHOD, "xml");
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "false");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            trans.transform(new DOMSource(document), new StreamResult(baos));

            System.out.println(baos.toString());
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintWriter(baos));
            e.printStackTrace();
            fail(baos.toString());
        }
    }
}
