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
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.w3c.dom.Document;

/**
 * The Class ScopePathUtilTest.
 * 
 */
public class ScopePathUtilTest extends TestCase {

    /** The Constant TESTCASE_A_SINGLESCOPE. */
    public static final String TESTCASE_A_SINGLESCOPE = ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_LOCATION + "=Mars";

    /** The Constant TESTCASE_A_DOUBLESCOPE. */
    public static final String TESTCASE_A_DOUBLESCOPE =
            ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_LOCATION + "=Saturn/"
                    + ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_APPLICATION + "=Rocket";

    /** The Constant TESTCASE_A_SINGLESCOPE_HIERACHICAL. */
    public static final String TESTCASE_A_SINGLESCOPE_HIERACHICAL =
            ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_LOCATION + "=Jupiter.Callisto";

    /** The Constant TESTCASE_A_DOUBLESCOPE_HIERACHICAL. */
    public static final String TESTCASE_A_DOUBLESCOPE_HIERACHICAL =
            ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_LOCATION + "=Uranus.Oberon/"
                    + ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_APPLICATION + "=Spacemission.Lander";

    /** The Constant TESTCASE_B_ERROR1. */
    public static final String TESTCASE_B_ERROR1 = "Location/loc1=Application/app1/Instance=inst1";

    /** The tfactory. */
    private TransformerFactory tfactory = null;

    /** The util. */
    private ScopePathUtil util = null;

    /**
     * The Constructor.
     * 
     * @param arg0
     *        name of the test
     */
    public ScopePathUtilTest(final String arg0) {
        super(arg0);
    }

    /**
     * Test method for
     * 'org.eclipse.swordfish.configrepos.scopepath.basic.ScopePathUtil.composeScopePath(String)'
     */
    public final void testComposeScopePathStringDoubleScope() {
        ScopePath path = this.util.composeScopePath(TESTCASE_A_DOUBLESCOPE);
        this.printScopePath(path);
        assertEquals(TESTCASE_A_DOUBLESCOPE, path.toString());
    }

    /**
     * Test method for
     * 'org.eclipse.swordfish.configrepos.scopepath.basic.ScopePathUtil.composeScopePath(String)'
     */
    public final void testComposeScopePathStringDoubleScopeHierachical() {
        ScopePath path = this.util.composeScopePath(TESTCASE_A_DOUBLESCOPE_HIERACHICAL);
        this.printScopePath(path);
        assertEquals(TESTCASE_A_DOUBLESCOPE_HIERACHICAL, path.toString());
    }

    /**
     * Test method for
     * 'org.eclipse.swordfish.configrepos.scopepath.basic.ScopePathUtil.composeScopePath(String)'
     */
    public final void testComposeScopePathStringError1() {
        try {
            this.printScopePath(this.util.composeScopePath(TESTCASE_B_ERROR1));
        } catch (IllegalArgumentException iae) {
            // We've found an error
        }
    }

    /**
     * Test method for
     * 'org.eclipse.swordfish.configrepos.scopepath.basic.ScopePathUtil.composeScopePath(String)'
     */
    public final void testComposeScopePathStringSinlgeScope() {
        ScopePath path = this.util.composeScopePath(TESTCASE_A_SINGLESCOPE);
        this.printScopePath(path);
        assertEquals(TESTCASE_A_SINGLESCOPE, path.toString());
    }

    /**
     * Test method for
     * 'org.eclipse.swordfish.configrepos.scopepath.basic.ScopePathUtil.composeScopePath(String)'
     */
    public final void testComposeScopePathStringSinlgeScopeHierarchical() {
        ScopePath path = this.util.composeScopePath(TESTCASE_A_SINGLESCOPE_HIERACHICAL);
        this.printScopePath(path);
        assertEquals(TESTCASE_A_SINGLESCOPE_HIERACHICAL, path.toString());
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
        this.tfactory = TransformerFactory.newInstance();
        this.util = new ScopePathUtil(ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR);
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
        this.util = null;
        this.tfactory = null;
        super.tearDown();
    }

    /**
     * Pretty print.
     * 
     * @param scopePath
     *        the path to print
     */
    private void printScopePath(final ScopePath scopePath) {
        try {
            // just output something
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            scopePath.marshal(document);

            ByteArrayOutputStream baos;
            Transformer trans = this.tfactory.newTransformer();
            Properties props = new Properties();
            props.setProperty(OutputKeys.INDENT, "yes");
            props.setProperty(OutputKeys.ENCODING, "UTF-8");
            props.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
            props.setProperty(OutputKeys.METHOD, "xml");
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "false");
            trans.setOutputProperties(props);

            baos = new ByteArrayOutputStream();

            trans.transform(new DOMSource(document), new StreamResult(baos));

            System.out.println(baos.toString());
        } catch (Exception jaxbe) {
            fail(jaxbe.getMessage());
        }
    }
}
