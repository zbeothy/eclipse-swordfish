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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * The Class ConfigurationRepositoryPlaceholderConfigurerTest.
 * 
 */
public class ConfigurationRepositoryPlaceholderConfigurerTest extends TestCase {

    /**
     * The main method.
     * 
     * @param args
     *        the args
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ConfigurationRepositoryPlaceholderConfigurerTest.class);
    }

    /**
     * Suite.
     * 
     * @return Tests suite
     */
    public static final Test suite() {
        TestSuite ts = new TestSuite();
        ts.addTest(new ConfigurationRepositoryPlaceholderConfigurerTest("testCreateConfigurationRepositoryManager"));
        ts.addTest(new ConfigurationRepositoryPlaceholderConfigurerTest("testFetchPlaceholderConfiguredBeanWithIdent"));
        ts.addTest(new ConfigurationRepositoryPlaceholderConfigurerTest("testFetchPlaceholderConfiguredBeanWithoutIdent"));
        ts.addTest(new ConfigurationRepositoryPlaceholderConfigurerTest("testFetchPlaceholderConfiguredBeanFromBootConfig"));
        ts.addTest(new ConfigurationRepositoryPlaceholderConfigurerTest("testFetchPlaceholderConfiguredBeanFromSystemProperties"));
        return ts;
    }

    /** Is the bean factory which will be used to create all Spring beans. */
    private BeanFactory factory = null;

    /** Is the default configuration repository manager. */
    private ConfigurationRepositoryManagerInternalImpl crm = null;

    /**
     * Constructor for this test case.
     * 
     * @param aName
     *        provides the name of the actual test case.
     */
    public ConfigurationRepositoryPlaceholderConfigurerTest(final String aName) {
        super(aName);
    }

    /**
     * Test whether creating a configuration repository manager is possible.
     */
    public void testCreateConfigurationRepositoryManager() {
        this.crm =
                (ConfigurationRepositoryManagerInternalImpl) this.factory.getBean("manager",
                        ConfigurationRepositoryManagerInternalImpl.class);
        assertNotNull(this.crm);
    }

    /**
     * Test whether it is possible to fetch a configuration entry for a provided identifier.
     */
    public void testFetchPlaceholderConfiguredBeanFromBootConfig() {
        String result = (String) this.factory.getBean("aStringFromBootConfig", java.lang.String.class);
        assertNotNull(result);
    }

    /**
     * Test whether it is possible to fetch a configuration entry for a provided identifier.
     */
    public void testFetchPlaceholderConfiguredBeanFromSystemProperties() {
        String result = (String) this.factory.getBean("aStringFromSystemProperties", java.lang.String.class);
        // FIXME This is nonsense. The System.properties mechanism is currently
        // not operational. Fix this bug
        assertNotNull(result);
    }

    /**
     * Test whether it is possible to fetch a configuration entry for a provided identifier.
     */
    public void testFetchPlaceholderConfiguredBeanWithIdent() {
        String result = (String) this.factory.getBean("aStringWithIdent", java.lang.String.class);
        assertNotNull(result);
    }

    /**
     * Test whether it is possible to fetch a configuration entry for a provided identifier.
     */
    public void testFetchPlaceholderConfiguredBeanWithoutIdent() {
        String result = (String) this.factory.getBean("aStringWithoutIdent", java.lang.String.class);
        assertNotNull(result);
    }

    /**
     * Set up this test case.
     * 
     * @throws Exception
     *         the exception
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        BeanFactoryLocator beanFactoryLocator = ContextSingletonBeanFactoryLocator.getInstance("classpath*:**/beanRefContext.xml");
        BeanFactoryReference bfr = beanFactoryLocator.useBeanFactory("org.eclipse.swordfish.configrepos.proxy.test");
        this.factory = bfr.getFactory();
    }

    /**
     * Tear down this test case.
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
        ((AbstractApplicationContext) this.factory).close();
    }
}
