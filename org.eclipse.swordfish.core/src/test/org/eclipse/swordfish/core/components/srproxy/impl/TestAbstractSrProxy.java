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
package org.eclipse.swordfish.core.components.srproxy.impl;

import junit.framework.TestCase;

/**
 * This class contains JUnit tests for the class AbstractSrProxy.
 * 
 */
public class TestAbstractSrProxy extends TestCase {

    /**
     * Start method for test case.
     * 
     * @param args
     *        arguments
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(TestAbstractSrProxy.class);
    }

    /**
     * Constructor for TestAbstractSrProxy.
     * 
     * @param name
     *        the name
     */
    public TestAbstractSrProxy(final String name) {
        super();
        this.setName(name);
    }

    /**
     * Checks if bugfix 1741 works, that is, fillCache should not throw a
     * StringIndexOutOfBoundsException when reading files from a directory that have no ending or
     * are smaller than "sdx".length().
     * 
     * @throws Exception
     *         on error
     */
    public void testFillCacheSuccess() throws Exception {
        AbstractSrProxy proxy = new SrProxyBean() {};
        proxy.setCache(new SrProxyCacheBean());
    }

    /**
     * Sets the up.
     * 
     * @throws Exception
     * 
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
    }

    /**
     * Tear down.
     * 
     * @throws Exception
     * 
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
    }
}
