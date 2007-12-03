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
package org.eclipse.swordfish.core.components.resolver;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The Class AllTests.
 */
public class AllTests {

    /**
     * Suite.
     * 
     * @return the test
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.eclipse.swordfish.core.components.resolver");
        // $JUnit-BEGIN$
        suite.addTestSuite(PolicyResolverTest.class);
        suite.addTestSuite(ResolverTest.class);
        suite.addTestSuite(CompoundServiceDescriptionTest.class);
        // $JUnit-END$
        return suite;
    }

}
