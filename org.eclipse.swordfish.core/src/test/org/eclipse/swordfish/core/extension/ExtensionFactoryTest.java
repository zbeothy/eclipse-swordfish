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
package org.eclipse.swordfish.core.extension;

import junit.framework.TestCase;
import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.core.management.instrumentation.InstrumentationManagerProxy;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalInstrumentationManager;
import org.eclipse.swordfish.papi.internal.extension.operations.InternalOperations;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class ExtensionFactoryTest.
 */
public class ExtensionFactoryTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /**
     * Instantiates a new extension factory test.
     * 
     * @param name
     *        the name
     */
    public ExtensionFactoryTest(final String name) {
        super(name);
    }

    /**
     * Test the default extension factory to ensure that an instance of the configured papi
     * extension is returned when using the methods documented for use by participant developers.
     */
    public void testExternalFactory() {
        ExtensionFactory factory = (ExtensionFactory) this.ctx.getBean(InternalInstrumentationManager.class.getName());
        Object instance = factory.getInstance(null);
        assertTrue(instance instanceof InstrumentationManagerProxy);
        assertTrue(instance instanceof InternalInstrumentationManager);
        factory = (ExtensionFactory) this.ctx.getBean(InternalOperations.class.getName());
        instance = factory.getInstance(null);
        assertTrue(instance instanceof InternalOperations);
    }

    /**
     * Test internal access.
     */
    public void testInternalAccess() {
        Object myManager = this.ctx.getBean("org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager");
        assertTrue(myManager instanceof org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager);
        assertTrue(myManager instanceof InstrumentationManagerBean);
        Object myOperations = this.ctx.getBean("org.eclipse.swordfish.core.management.operations.Operations");
        assertTrue(myOperations instanceof org.eclipse.swordfish.core.management.operations.Operations);
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
                        new String[] {"src/test/org/eclipse/swordfish/core/extension/PapiExtensionConfig.xml"});
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
