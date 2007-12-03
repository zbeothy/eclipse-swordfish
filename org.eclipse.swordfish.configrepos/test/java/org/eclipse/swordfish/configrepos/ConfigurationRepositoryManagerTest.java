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
package org.eclipse.swordfish.configrepos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.PathPartImpl;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.ScopePathImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * The Class ConfigurationRepositoryManagerTest.
 * 
 */
public class ConfigurationRepositoryManagerTest extends TestCase {

    /**
     * The main method.
     * 
     * @param args
     *        the args
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ConfigurationRepositoryManagerTest.class);
    }

    /**
     * Suite.
     * 
     * @return Tests suite
     */
    public static final Test suite() {
        TestSuite ts = new TestSuite();
        ts.addTest(new ConfigurationRepositoryManagerTest("testLoadBeanFactoryHierarchy"));
        ts.addTest(new ConfigurationRepositoryManagerTest("testCreateConfigurationRepositoryManager"));
        ts.addTest(new ConfigurationRepositoryManagerTest("testGetConfiguration"));
        ts.addTest(new ConfigurationRepositoryManagerTest("testGetConfigFromLocalAndRemote"));
        ts.addTest(new ConfigurationRepositoryManagerTest("testGetResource"));
        ts.addTest(new ConfigurationRepositoryManagerTest("testGetResourceTwice"));
        // ts.addTest(new
        // ConfigurationRepositoryManagerTest("testFetchConfigurationMap"));
        return ts;
    }

    /** The logger. */
    private Logger logger = Logger.getLogger(ConfigurationRepositoryManagerTest.class.getName());

    /** Spring bean factory to use. */
    private BeanFactory factory = null;

    /** The crm. */
    private ConfigurationRepositoryManagerInternalImpl crm = null;

    /** The factory creator for the beansFactory of this test-suite. */
    private BeanFactoryLocator beanFactoryLocator = null;

    /**
     * The Constructor.
     * 
     * @param arg0
     *        provides the name of this test case
     */
    public ConfigurationRepositoryManagerTest(final String arg0) {
        super(arg0);
    }

    /**
     * Test create configuration repository manager.
     */
    public void testCreateConfigurationRepositoryManager() {
        this.testLoadBeanFactoryHierarchy();
        this.crm =
                (ConfigurationRepositoryManagerInternalImpl) this.factory.getBean("manager",
                        ConfigurationRepositoryManagerInternalImpl.class);
        this.crm.setLocalResourceBase("./target/test-classes/conf");
    }

    /**
     * Test fetch configuration map.
     */
    public void testFetchConfigurationMap() {
        this.testLoadBeanFactoryHierarchy();
        Map result = (Map) this.factory.getBean("org.eclipse.swordfish.configrepos.spring.ConfigurationMapFactoryBean2");
        assertNotNull(result.keySet());
        assertTrue(result.keySet().contains("compression"));
        assertTrue(((Map) result.get("compression")).containsKey("is-mandatory"));
        assertTrue(result.keySet().contains("Transformation"));
        assertEquals("org.eclipse.swordfish.core.encryptionplugin.msglevelsecurity.encryption.EncryptionProcessingComponent",
                ((Map) result.get("Transformation")).get("component"));
    }

    /**
     * Test get config from local and remote.
     */
    public void testGetConfigFromLocalAndRemote() {
        this.testCreateConfigurationRepositoryManager();
        try {
            ScopePath path = new ScopePathImpl();
            PathPart part = new PathPartImpl();
            part.setType("Location");
            part.setValue("Bonn");
            path.getPathPart().add(part);

            Configuration cfg = this.crm.getConfiguration("SBB", path);
            assertNotNull(cfg);
            assertEquals("20", cfg.getProperty("sbbap:AuthorizationProcessor.sbbap:ExpiryClearance"));
            assertEquals("auth-id-password", cfg.getProperty("sbbaep:AuthenticationProcessor.sbbaep:CredentialLoginType"));
        } catch (ConfigurationRepositoryConfigException ce) {
            ce.printStackTrace(System.err);
            fail(ce.getMessage());
        }
    }

    /**
     * Test get configuration.
     */
    public void testGetConfiguration() {
        this.testCreateConfigurationRepositoryManager();
        try {
            ScopePath path = new ScopePathImpl();
            PathPart part = new PathPartImpl();
            part.setType("Location");
            part.setValue("Bonn");
            path.getPathPart().add(part);

            Configuration cfg = this.crm.getConfiguration("SBB", path);
            assertNotNull(cfg);
        } catch (ConfigurationRepositoryConfigException ce) {
            ce.printStackTrace(System.err);
            fail(ce.getMessage());
        }
    }

    /**
     * Test get resource.
     */
    public void testGetResource() {
        this.testCreateConfigurationRepositoryManager();
        try {
            ScopePath path = new ScopePathImpl();
            PathPart part = new PathPartImpl();
            part.setType("Location");
            part.setValue("Bonn");
            path.getPathPart().add(part);

            InputStream stream = this.crm.getResource("SBB", path, "compost", "README.txt");
            byte[] buffer = new byte[2048];
            int read = 0;
            while ((read = stream.read(buffer)) != -1) {
                this.logger.info(new String(buffer, 0, read));
            }
        } catch (ConfigurationRepositoryResourceException cre) {
            cre.printStackTrace(System.err);
            fail(cre.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
            fail(ioe.getMessage());
        }
    }

    /**
     * Test get resource twice.
     */
    public void testGetResourceTwice() {
        this.testCreateConfigurationRepositoryManager();
        try {
            ScopePath path = new ScopePathImpl();
            PathPart part = new PathPartImpl();
            part.setType("Location");
            part.setValue("Bonn");
            path.getPathPart().add(part);

            InputStream stream = this.crm.getResource("SBB", path, "compost", "README.txt");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int read = 0;
            while ((read = stream.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
                this.logger.info(new String(buffer, 0, read));
            }
            String first = new String(baos.toByteArray());
            stream = this.crm.getResource("SBB", path, "compost", "README.txt");
            baos = new ByteArrayOutputStream();
            while ((read = stream.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
                this.logger.info(new String(buffer, 0, read));
            }
            String second = new String(baos.toByteArray());

            assertEquals(first, second);

        } catch (ConfigurationRepositoryResourceException cre) {
            cre.printStackTrace(System.err);
            fail(cre.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
            fail(ioe.getMessage());
        }
    }

    /**
     * Test loading the bean factory hierarchy.
     */
    public void testLoadBeanFactoryHierarchy() {
        this.beanFactoryLocator = ContextSingletonBeanFactoryLocator.getInstance("classpath*:**/beanRefContext.xml");
        BeanFactoryReference bfr = this.beanFactoryLocator.useBeanFactory("org.eclipse.swordfish.configrepos.proxy.test");
        this.factory = bfr.getFactory();
    }

    protected Logger getLogger() {
        return this.logger;
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     *         the exception
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
     *         the exception
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (null != this.crm) {
            this.crm.close();
        }

        if (null != this.factory) {
            ((AbstractApplicationContext) this.factory).close();
            this.factory = null;
        }
    }
}
