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

import java.util.ArrayList;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.w3c.dom.Document;

/**
 * The Class XMLCompositeConfigurationTest.
 */
public class XMLCompositeConfigurationTest extends TestCase {

    /**
     * The main method.
     * 
     * @param args
     *        the arguments
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(XMLCompositeConfigurationTest.class);
    }

    /** The config a. */
    private XMLConfiguration configA = null;

    /** The config b. */
    private XMLConfiguration configB = null;

    /** The config c1. */
    private XMLConfiguration configC1 = null;

    /** The config c2. */
    private XMLConfiguration configC2 = null;

    /** The config c3. */
    private XMLConfiguration configC3 = null;

    /** The config c4. */
    private XMLConfiguration configC4 = null;

    /** The config c5. */
    private XMLConfiguration configC5 = null;

    /** The config c6. */
    private XMLConfiguration configC6 = null;

    /** The config c7. */
    private XMLConfiguration configC7 = null;

    /** The log. */
    private Logger log;

    /**
     * Instantiates a new XML composite configuration test.
     * 
     * @param arg0
     *        the arg0
     */
    public XMLCompositeConfigurationTest(final String arg0) {
        super(arg0);
    }

    /**
     * Test merge configurations multiple XML.
     */
    public void testMergeConfigurationsMultipleXML() {
        try {
            ArrayList list = new ArrayList();
            list.add(this.configC1);
            list.add(this.configC2);
            XMLConfiguration result = XMLCompositeConfiguration.mergeConfigurations(list);
            System.out.println(result);
            Document doc = result.getDocument();
            assertNotNull(doc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test merge configurations person.
     */
    public void testMergeConfigurationsPerson() {
        try {
            XMLConfiguration result = XMLCompositeConfiguration.mergeConfigurations(this.configA, this.configB);
            System.out.println(result);
            Document doc = result.getDocument();
            assertNotNull(doc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test merge configurations two XML.
     */
    public void testMergeConfigurationsTwoXML() {
        try {
            XMLConfiguration result = XMLCompositeConfiguration.mergeConfigurations(this.configC1, this.configC2);
            System.out.println(result);
            Document doc = result.getDocument();
            assertNotNull(doc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test merge configurations two XML with clash.
     */
    public void testMergeConfigurationsTwoXMLWithClash() {
        try {
            XMLConfiguration result = XMLCompositeConfiguration.mergeConfigurations(this.configC4, this.configC5);
            System.out.println(result);
            Document doc = result.getDocument();
            assertNotNull(doc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test merge configurations two XML with clash2.
     */
    public void testMergeConfigurationsTwoXMLWithClash2() {
        try {
            XMLConfiguration result = XMLCompositeConfiguration.mergeConfigurations(this.configC6, this.configC7);
            System.out.println(result);
            Document doc = result.getDocument();
            assertNotNull(doc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test merge configuration with clash.
     */
    public void testMergeConfigurationWithClash() {
        try {
            ArrayList list = new ArrayList();
            list.add(this.configC1);
            list.add(this.configC3);
            XMLCompositeConfiguration.mergeConfigurations(list);
            fail("failed to detect configuration clash");
        } catch (ConfigurationException e) {
            this.log.info("Clash detected.");
        }
    }

    /**
     * Sets the up.
     * 
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.configA = new XMLConfiguration("test/resources/xml/person1.xml");
        this.configB = new XMLConfiguration("test/resources/xml/person2.xml");
        this.configC1 = new XMLConfiguration("test/resources/xml/remote.xml");
        this.configC2 = new XMLConfiguration("test/resources/xml/local.xml");
        this.configC3 = new XMLConfiguration("test/resources/xml/remoteclash.xml");
        this.configC4 = new XMLConfiguration("test/resources/xml/sp_fallbackcfg.xml");
        this.configC5 = new XMLConfiguration("test/resources/xml/sp_localcfg.xml");
        this.configC6 = new XMLConfiguration("test/resources/xml/remotefallbackcfg.xml");
        this.configC7 = new XMLConfiguration("test/resources/xml/sbblocalcfg.xml");

        this.log = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Tear down.
     * 
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
