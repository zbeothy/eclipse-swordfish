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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.transform.stream.StreamSource;
import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.xerces.dom.DOMInputImpl;
import org.eclipse.swordfish.configrepos.shared.validation.ExternalResolver;
import org.eclipse.swordfish.configrepos.shared.validation.ResourceException;
import org.eclipse.swordfish.configrepos.shared.validation.ValidationException;
import org.eclipse.swordfish.configrepos.shared.validation.XMLConfigurationValidator;
import org.w3c.dom.ls.LSInput;

/**
 * The Class SchemaValidationTest.
 */
public class SchemaValidationTest extends TestCase {

    /** The Constant TSP_COMPONENT_SCHEMA_DIR. */
    private static final String TSP_COMPONENT_SCHEMA_DIR = "resources/xml/schemas/tsp/components/";

    /** The Constant TSP_COMPONENT_INSTANCES_DIR. */
    private static final String TSP_COMPONENT_INSTANCES_DIR = "resources/xml/instances/tsp/";

    /** The Constant SBB_COMPONENT_SCHEMA_DIR. */
    private static final String SBB_COMPONENT_SCHEMA_DIR = "resources/xml/schemas/sbb/";

    /** The Constant SBB_COMPONENT_INSTANCES_DIR. */
    private static final String SBB_COMPONENT_INSTANCES_DIR = "resources/xml/instances/sbb/";

    /**
     * The main method.
     * 
     * @param args
     *        the arguments
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(SchemaValidationTest.class);
    }

    /** The log. */
    private Logger log;

    /** The validator. */
    private XMLConfigurationValidator validator = null;

    /**
     * Instantiates a new schema validation test.
     * 
     * @param arg0
     *        the arg0
     */
    public SchemaValidationTest(final String arg0) {
        super(arg0);
    }

    /**
     * Test SBB bootstrap cfg.
     */
    public void testSBBBootstrapCfg() {
        try {
            StreamSource schema = this.createSchema(SBB_COMPONENT_SCHEMA_DIR + "bootstrap_cfg.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(SBB_COMPONENT_INSTANCES_DIR + "bootstrap_cfg.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test SBB local cfg.
     */
    public void testSBBLocalCfg() {
        try {
            StreamSource schema = this.createSchema(SBB_COMPONENT_SCHEMA_DIR + "sbblocalcfg.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(SBB_COMPONENT_INSTANCES_DIR + "sbblocalcfg.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema authentication component.
     */
    public void testSchemaAuthenticationComponent() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_authentication.component.xsd");
            XMLConfiguration sampleInstance =
                    this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_authentication.component.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema authorization component.
     */
    public void testSchemaAuthorizationComponent() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_authorization.component.xsd");
            XMLConfiguration sampleInstance =
                    this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_authorization.component.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema certificate local.
     */
    public void testSchemaCertificateLocal() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_certificate.local.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_certificate.local.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema config admin.
     */
    public void testSchemaConfigAdmin() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_config_admin.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_config_admin.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema config runtime.
     */
    public void testSchemaConfigRuntime() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_config_runtime.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_config_runtime.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema EM local.
     */
    public void testSchemaEMLocal() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_em.local.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_em.local.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema jaas.
     */
    public void testSchemaJaas() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_jaas.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_jaas.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema keystore local.
     */
    public void testSchemaKeystoreLocal() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_keystore.local.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_keystore.local.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema keystore remote.
     */
    public void testSchemaKeystoreRemote() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_keystore.remote.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_keystore.remote.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema notification local.
     */
    public void testSchemaNotificationLocal() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_notification.local.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_notification.local.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema SR remore.
     */
    public void testSchemaSRRemore() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_sr.remote.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_sr.remote.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema UA local.
     */
    public void testSchemaUALocal() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_ua.local.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_ua.local.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test TSP configuration schema instance against TSP configuration schema.
     * 
     */

    public void testSchemaUARemote() {
        try {
            StreamSource schema = this.createSchema(TSP_COMPONENT_SCHEMA_DIR + "tsp_conf_ua.remote.xsd");
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_ua.remote.xml");
            try {
                this.validator.validateConfiguration(sampleInstance, schema);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test schema UA remote retrieve root schems.
     */
    public void testSchemaUARemoteRetrieveRootSchems() {
        try {
            XMLConfiguration sampleInstance = this.createSampleInstance(TSP_COMPONENT_INSTANCES_DIR + "tsp_ua.remote.xml");
            try {
                this.validator.validateConfiguration(sampleInstance);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // public void testSBBRemoteFallbackCfg() {
    // try {
    // StreamSource schema = createSchema(SBB_COMPONENT_SCHEMA_DIR +
    // "remotefallbackcfg.xsd");
    // XMLConfiguration sampleInstance =
    // createSampleInstance(SBB_COMPONENT_INSTANCES_DIR +
    // "remotefallbackcfg.xml");
    // try {
    // validator.validateConfiguration(sampleInstance, schema);
    // } catch (ValidationException e) {
    // fail(e.getMessage());
    // }
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * Sets the up.
     * 
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.log = Logger.getLogger(this.getClass().getName());
        this.validator = this.createValidator();
        this.validator.addExternalResolver(new TestResolver());
    }

    /**
     * Tear down.
     * 
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.validator = null;
    }

    /**
     * Factory method.
     * 
     * @param testInstanceFilename
     *        name of test instance file
     * 
     * @return a test instance
     * 
     * @throws ConfigurationException
     *         on error
     */
    private XMLConfiguration createSampleInstance(final String testInstanceFilename) throws ConfigurationException {
        URL url = this.getClass().getClassLoader().getResource(testInstanceFilename);
        return new XMLConfiguration(url);
    }

    /**
     * Factory method.
     * 
     * @param testSchemaFilename
     *        name of test schema
     * 
     * @return List containing the test schema
     * 
     * @throws FileNotFoundException
     *         on error
     */
    private StreamSource createSchema(final String testSchemaFilename) throws FileNotFoundException {
        InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(testSchemaFilename);
        StreamSource schema = new StreamSource(inStream);
        return schema;
    }

    /**
     * Factory method.
     * 
     * @return a generic XMLConfigurationValidator used for schema validation
     */
    private XMLConfigurationValidator createValidator() {
        try {
            return new XMLConfigurationValidator(this.log, "classpath:resources/xml/catalog.xml");
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        }
        return null;
    }

    /**
     * The Class TestResolver.
     */
    class TestResolver implements ExternalResolver {

        /**
         * Checks if is external resource.
         * 
         * @param uri
         *        the uri
         * 
         * @return true, if is external resourcee
         * 
         * @throws ResourceException
         */
        public boolean isExternalResource(final String uri) throws ResourceException {
            return uri.startsWith("classpath");
        }

        /**
         * Resolve external resource.
         * 
         * @param uri
         *        the uri
         * 
         * @return the LS input
         * 
         * @throws ResourceException
         */
        public LSInput resolveExternalResource(final String uri) throws ResourceException {
            String[] parts = uri.split(":");
            if (parts.length != 2) throw new ResourceException();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(parts[1]);
            LSInput di = new DOMInputImpl();
            di.setBaseURI(null);
            di.setByteStream(is);
            di.setEncoding("UTF-8");
            di.setPublicId(null);
            di.setSystemId(uri);
            return di;

        }

    }
}
