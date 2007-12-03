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
 * The Class ContextTest.
 */
public class ContextTest extends TestCase {

    // private final static String CONTEXTDEF =
    // "src/test/org/eclipse/swordfish/core/management/ContextTestConfig.xml";
    /** The Constant CONTEXTDEF. */
    private final static String CONTEXTDEF = "src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml";

    /**
     * Test context creation and destruction.
     */
    public void testContextCreationAndDestruction() {
        for (int i = 0; i < 100; i++) {
            FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] {CONTEXTDEF});
            ctx.destroy();
            // Thread.yield();
            System.out.println(i + "-------------------------------------------------------------");
        }
    }
}
