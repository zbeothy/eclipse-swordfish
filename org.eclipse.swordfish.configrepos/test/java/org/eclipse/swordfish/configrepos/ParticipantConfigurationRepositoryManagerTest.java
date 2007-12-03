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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
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
 * The Class ParticipantConfigurationRepositoryManagerTest.
 * 
 */
public class ParticipantConfigurationRepositoryManagerTest extends TestCase {

    /**
     * The main method.
     * 
     * @param args
     *        the args
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParticipantConfigurationRepositoryManagerTest.class);
    }

    /**
     * Suite.
     * 
     * @return Tests suite
     */
    public static final Test suite() {
        TestSuite ts = new TestSuite();
        // ts.addTest(new ParticipantConfigurationRepositoryManagerTest(
        // "testLoadBeanFactoryHierarchy"));
        ts.addTest(new ParticipantConfigurationRepositoryManagerTest("testCreateConfigurationRepositoryManager"));
        ts.addTest(new ParticipantConfigurationRepositoryManagerTest("testGetConfiguration"));
        ts.addTest(new ParticipantConfigurationRepositoryManagerTest("testGetLocalConfigurationParameter"));
        ts.addTest(new ParticipantConfigurationRepositoryManagerTest("testGetLocalConfigurationParameterFromOtherBase"));
        // ts.addTest(new ParticipantConfigurationRepositoryManagerTest(
        // "testGetLocalConfigurationParameterFromOtherBaseFullpath"));
        return ts;
    }

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
    public ParticipantConfigurationRepositoryManagerTest(final String arg0) {
        super(arg0);
    }

    /**
     * Test create configuration repository manager.
     */
    public void testCreateConfigurationRepositoryManager() {
        this.testLoadBeanFactoryHierarchy();
        this.crm =
                (ConfigurationRepositoryManagerInternalImpl) this.factory.getBean("manager_participant",
                        ConfigurationRepositoryManagerInternalImpl.class);
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
            part.setValue("Mars");
            path.getPathPart().add(part);

            Configuration cfg = this.crm.getConfiguration("OtherTree", path);
            assertNotNull(cfg);
        } catch (ConfigurationRepositoryConfigException ce) {
            ce.printStackTrace(System.err);
            fail(ce.getMessage());
        }
    }

    /**
     * Test get local configuration parameter.
     */
    public void testGetLocalConfigurationParameter() {
        this.testCreateConfigurationRepositoryManager();
        try {
            ScopePath path = new ScopePathImpl();
            PathPart part = new PathPartImpl();
            part.setType("Location");
            part.setValue("Mars");
            path.getPathPart().add(part);

            Configuration cfg = this.crm.getConfiguration("OtherTree", path);
            assertEquals("0815", cfg.getProperty("testcomponent2.testvalue"));
        } catch (ConfigurationRepositoryConfigException ce) {
            ce.printStackTrace(System.err);
            fail(ce.getMessage());
        }
    }

    /**
     * Test get local configuration parameter from other base.
     */
    public void testGetLocalConfigurationParameterFromOtherBase() {
        this.testCreateConfigurationRepositoryManager();
        try {
            ScopePath path = new ScopePathImpl();
            PathPart part = new PathPartImpl();
            part.setType("Location");
            part.setValue("Mars.Phobos");
            path.getPathPart().add(part);

            // change the base resource path
            this.crm.setLocalResourceBase("./conf/sub-conf");

            Configuration cfg = this.crm.getConfiguration("Other Sub Tree", path);
            assertEquals("0815", cfg.getProperty("testcomponent2.testvalue"));
        } catch (ConfigurationRepositoryConfigException ce) {
            ce.printStackTrace(System.err);
            fail(ce.getMessage());
        }
    }

    /**
     * Test get local configuration parameter from other base fullpath.
     */
    public void testGetLocalConfigurationParameterFromOtherBaseFullpath() {
        this.testCreateConfigurationRepositoryManager();
        try {
            ScopePath path = new ScopePathImpl();
            PathPart part = new PathPartImpl();
            part.setType("Location");
            part.setValue("Mars");
            path.getPathPart().add(part);

            // change the base resource path
            this.crm.setLocalResourceBase("D:/Daten/JBI/sbb-configrepos-proxy/target/test-classes/conf/sub-conf");

            Configuration cfg = this.crm.getConfiguration("Other%20Sub%20Tree", path);
            assertEquals("0815", cfg.getProperty("testcomponent2.testvalue"));
        } catch (ConfigurationRepositoryConfigException ce) {
            ce.printStackTrace(System.err);
            fail(ce.getMessage());
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
            this.crm = null;
        }

        if (null != this.factory) {
            ((AbstractApplicationContext) this.factory).close();
            this.factory = null;
        }
    }
}
