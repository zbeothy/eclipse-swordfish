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
package org.eclipse.swordfish.configrepos.shared;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The Class XMLConfigurationTest.
 */
public class XMLConfigurationTest extends TestCase {

    /** The builder. */
    private DocumentBuilder builder = null;

    /**
     * Instantiates a new XML configuration test.
     * 
     * @param name
     *        the name
     */
    public XMLConfigurationTest(final String name) {
        super(name);
        try {
            this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            fail(pce.getMessage());
        }
    }

    /**
     * Test method for 'org.eclipse.swordfish.configrepos.shared.
     * XMLConfiguration.initProperties(Document, boolean)'
     */
    /**
     * Test init properties.
     */
    public final void testInitProperties() {
        try {
            Document person1 = this.builder.parse(new File("." + "/target/test-classes/resources/xml/person1.xml"));
            System.out.println(person1.toString());
        } catch (IOException ioe) {
            fail(ioe.getMessage());
        } catch (SAXException se) {
            fail(se.getMessage());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
