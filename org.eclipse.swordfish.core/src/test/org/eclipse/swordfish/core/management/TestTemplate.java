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
package org.eclipse.swordfish.core.management;

import junit.framework.TestCase;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class TestTemplate.
 */
public class TestTemplate extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /**
     * Instantiates a new test template.
     * 
     * @param name
     *        the name
     */
    public TestTemplate(final String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.ctx =
                new FileSystemXmlApplicationContext(
                        new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.ctx.destroy();
        super.tearDown();
    }

}
