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
package org.eclipse.swordfish.core.management.instrumentation;

import java.io.InputStream;
import java.util.Set;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import junit.framework.TestCase;
import org.apache.commons.modeler.Registry;
import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalInstrumentationManager;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class InstrumentationManagerTest.
 */
public class InstrumentationManagerTest extends TestCase {

    /** The mbs. */
    private MBeanServer mbs;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The initial M bean count. */
    private int initialMBeanCount;

    /** The resource. */
    private Resource resource;

    /**
     * Instantiates a new instrumentation manager test.
     * 
     * @param name
     *        the name
     */
    public InstrumentationManagerTest(final String name) {
        super(name);
    }

    /**
     * Tests that an mbean declaration can refer to an interface type.
     * 
     * @throws ParticipantHandlingException
     * @throws MalformedObjectNameException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     * @throws AttributeNotFoundException
     * @throws InternalSBBException
     */
    public void testExternalInterface() throws InternalInfrastructureException, MalformedObjectNameException,
            InstanceNotFoundException, MBeanException, ReflectionException, AttributeNotFoundException, InternalSBBException {
        InputStream desc =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/management/instrumentation/ResourceInterfaceDesc.xml");
        Object obj = this.ctx.getBean(InternalInstrumentationManager.class.getName());
        String typeName = "org.eclipse.swordfish.core.management.instrumentation.ResourceInterface";
        if (obj instanceof ExtensionFactory) {
            ExtensionFactory factory = (ExtensionFactory) obj;
            obj = factory.getInstance(null);
        }
        InternalInstrumentationManager manager = (InternalInstrumentationManager) obj;
        manager.registerInstrumentation(this.resource, desc, typeName);
        this.verifyRegistration(this.resource, "participant");
        this.verifyInterface(this.resource, "participant");
    }

    /**
     * Test that instrumentation object is correctly registered when using PAPI extension.
     * 
     * @throws ParticipantHandlingException
     * @throws MalformedObjectNameException
     * @throws ReflectionException
     * @throws MBeanException
     * @throws InstanceNotFoundException
     * @throws AttributeNotFoundException
     * @throws InternalSBBException
     */
    public void testExternalRegistration() throws InternalInfrastructureException, MalformedObjectNameException,
            InstanceNotFoundException, MBeanException, ReflectionException, AttributeNotFoundException, InternalSBBException {
        InputStream desc =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/management/instrumentation/ResourceDesc.xml");
        Object obj = this.ctx.getBean(InternalInstrumentationManager.class.getName());
        if (obj instanceof ExtensionFactory) {
            ExtensionFactory factory = (ExtensionFactory) obj;
            obj = factory.getInstance(null);
        }
        InternalInstrumentationManager manager = (InternalInstrumentationManager) obj;
        manager.registerInstrumentation(this.resource, desc);
        this.verifyRegistration(this.resource, "participant");
        this.verifyObject(this.resource, "participant");
    }

    /**
     * Tests that an mbean declaration can refer to an interface type.
     * 
     * @throws ParticipantHandlingException
     * @throws MalformedObjectNameException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     * @throws AttributeNotFoundException
     */
    public void testInternalInterface() throws Exception {
        this.ctx.getBean("org.eclipse.swordfish.core.management.adapter.JMXConnectorAdapter");
        InputStream desc =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/management/instrumentation/ResourceInterfaceDesc.xml");
        Object obj = this.ctx.getBean(org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager.class.getName());
        String typeName = "org.eclipse.swordfish.core.management.instrumentation.ResourceInterface";
        if (obj instanceof ExtensionFactory) {
            ExtensionFactory factory = (ExtensionFactory) obj;
            obj = factory.getInstance(null);
        }
        org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager manager =
                (org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager) obj;
        manager.registerInstrumentation(this.resource, desc, typeName);
        this.verifyRegistration(this.resource, "component");
        this.verifyInterface(this.resource, "component");
    }

    /**
     * Test internal registration.
     * 
     * @throws ParticipantHandlingException
     * @throws MalformedObjectNameException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     * @throws AttributeNotFoundException
     */
    public void testInternalRegistration() throws Exception {
        // Object connector =
        // ctx.getBean("org.eclipse.swordfish.core.management.adapter.JMXConnectorAdapter");
        InputStream desc =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/management/instrumentation/ResourceDesc.xml");
        Object obj = this.ctx.getBean(org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager.class.getName());
        if (obj instanceof ExtensionFactory) {
            ExtensionFactory factory = (ExtensionFactory) obj;
            obj = factory.getInstance(null);
        }
        InstrumentationManager manager = (InstrumentationManager) obj;
        manager.registerInstrumentation(this.resource, desc);
        this.verifyRegistration(this.resource, "component");
        this.verifyObject(this.resource, "component");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // ctx = new FileSystemXmlApplicationContext(
        // new String[]
        // {"src/test/org/eclipse/swordfish/core/extension/PapiExtensionConfig.xml"});
        this.ctx =
                new FileSystemXmlApplicationContext(
                        new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
        this.mbs = (MBeanServer) this.ctx.getBean("mbeanServer");
        this.initialMBeanCount = this.mbs.getMBeanCount().intValue();
        this.resource = new Resource();
        Registry registry = Registry.getRegistry(null, null);
        registry.resetMetadata();
        // manager = (InternalInstrumentationManager)
        // ctx.getBean("org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager");
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

    /**
     * Verify interface.
     * 
     * @param resource
     *        the resource
     * @param specifier
     *        the specifier
     * 
     * @throws MalformedObjectNameException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     */
    private void verifyInterface(final Resource resource, final String specifier) throws MalformedObjectNameException,
            InstanceNotFoundException, MBeanException, ReflectionException {
        String id = "" + resource.hashCode();
        ObjectName on = new ObjectName("sbb/" + specifier + ":id=" + id + ",*");
        Set names = this.mbs.queryNames(on, null);
        assertEquals(1, names.size());
        ObjectName bean = (ObjectName) names.toArray()[0];
        try {
            this.mbs.getAttribute(bean, "Bar");
            fail("accessing attribute 'bar' should result in exception");
        } catch (AttributeNotFoundException e) {
            // expected
        }
    }

    /**
     * Verify object.
     * 
     * @param resource
     *        the resource
     * @param specifier
     *        the specifier
     * 
     * @throws MalformedObjectNameException
     * @throws AttributeNotFoundException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     */
    private void verifyObject(final Resource resource, final String specifier) throws MalformedObjectNameException,
            AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
        String id = "" + resource.hashCode();
        ObjectName on = new ObjectName("sbb/" + specifier + ":id=" + id + ",*");
        Set names = this.mbs.queryNames(on, null);
        assertEquals(1, names.size());
        ObjectName bean = (ObjectName) names.toArray()[0];
        Object result = this.mbs.getAttribute(bean, "Bar");
        assertTrue(result instanceof java.lang.String);
        assertEquals("bar", result);
    }

    /**
     * Verify registration.
     * 
     * @param resource
     *        the resource
     * @param specifier
     *        the specifier
     * 
     * @throws MalformedObjectNameException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     * @throws AttributeNotFoundException
     */
    private void verifyRegistration(final Resource resource, final String specifier) throws MalformedObjectNameException,
            InstanceNotFoundException, MBeanException, ReflectionException, AttributeNotFoundException {
        int newCount = this.mbs.getMBeanCount().intValue();
        assertEquals(1, newCount - this.initialMBeanCount);
        String id = "" + resource.hashCode();
        ObjectName on = new ObjectName("sbb/" + specifier + ":id=" + id + ",*");
        Set names = this.mbs.queryNames(on, null);
        assertEquals(1, names.size());
        ObjectName bean = (ObjectName) names.toArray()[0];
        this.mbs.invoke(bean, "trigger", new Object[0], new String[0]);
        newCount = resource.getCount();
        assertEquals(1, newCount);
        Object result = this.mbs.getAttribute(bean, "Foo");
        assertTrue(result instanceof java.lang.String);
        assertEquals("foo", result);
        try {
            this.mbs.invoke(bean, "unexposed", new Object[0], new String[0]);
            fail("Invoking method 'unexposed' should throw exception");
        } catch (MBeanException e) {
            // expected
        }
    }

}
