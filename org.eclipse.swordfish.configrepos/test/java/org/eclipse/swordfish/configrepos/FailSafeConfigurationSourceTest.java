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
import org.eclipse.swordfish.configrepos.configuration.sources.FailsafeConfigurationSource;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.PathPartImpl;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.ScopePathImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

/**
 * The Class FailSafeConfigurationSourceTest.
 * 
 */
public class FailSafeConfigurationSourceTest extends TestCase {

    /**
     * The main method.
     * 
     * @param args
     *        the args
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(FailSafeConfigurationSourceTest.class);
    }

    /**
     * Suite.
     * 
     * @return Tests suite
     */
    public static final Test suite() {
        TestSuite ts = new TestSuite();
        ts.addTest(new FailSafeConfigurationSourceTest("testSetFixedScopePath"));
        ts.addTest(new FailSafeConfigurationSourceTest("testGetFixedScopePath"));
        ts.addTest(new FailSafeConfigurationSourceTest("testSetFixedScopePathToNull"));
        ts.addTest(new FailSafeConfigurationSourceTest("testCreateFailsafeConfigSourceWithId"));
        return ts;
    }

    /** Is the bean factory which will be used to create all Spring beans. */
    private BeanFactory factory = null;

    /** The source. */
    private FailsafeConfigurationSource source = null;

    /**
     * Constructor for this test case.
     * 
     * @param aName
     *        provides the name of the actual test case.
     */
    public FailSafeConfigurationSourceTest(final String aName) {
        super(aName);
    }

    /**
     * Test.
     */
    public void testCreateFailsafeConfigSourceWithId() {
        // FIXME test currently does not work!
        // assertNotNull((FailsafeConfigurationSource)
        // factory.getBean("failsafe_withid"));
    }

    /**
     * Test.
     */
    public void testGetFixedScopePath() {
        this.testSetFixedScopePath();
        // FIXME tests currently does not work!
        // assertEquals("Location", ((PathPart)
        // source.geFixedScopePath().getPathPart().get(0)).getType());
        // assertEquals("Bonn", ((PathPart)
        // source.geFixedScopePath().getPathPart().get(0)).getValue());
    }

    /**
     * Test.
     */
    public void testSetFixedScopePath() {
        this.initSourceWithoutId();

        ScopePath path = new ScopePathImpl();
        PathPart part = new PathPartImpl();
        part.setType("Location");
        part.setValue("Bonn");

        this.source.setFixedScopePath(path);
    }

    /**
     * Test whether.
     */
    public void testSetFixedScopePathToNull() {
        this.initSourceWithoutId();
        try {
            this.source.setFixedScopePath(null);
        } catch (Throwable e) {
            assertTrue((e instanceof IllegalArgumentException));
        }
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
    }

    /**
     * Init source without specifying an id in the constructor.
     */
    private void initSourceWithoutId() {
        this.source = (FailsafeConfigurationSource) this.factory.getBean("failsafe");
    }
}
