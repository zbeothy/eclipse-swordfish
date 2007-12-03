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
package org.eclipse.swordfish.core.management.adapter;

import java.io.InputStream;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.instrumentation.AlreadyRegisteredException;
import org.eclipse.swordfish.core.management.instrumentation.Referer;
import org.eclipse.swordfish.core.management.instrumentation.Resource;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.messaging.impl.ParticipantMonitorBackend;
import org.eclipse.swordfish.core.management.mock.DummyParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalInstrumentationManager;
import org.springframework.beans.BeansException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class HttpAdapterDummy.
 */
public class HttpAdapterDummy extends TestCase {

    /** The Constant rmiPort. */
    /*
     * private static final int rmiPort = 1099;
     */

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The jmx connection server. */
    private JMXConnectorServer jmxConnectionServer;

    /**
     * Instantiates a new http adapter dummy.
     * 
     * @param name
     *        the name
     */
    public HttpAdapterDummy(final String name) {
        super(name);
    }

    /**
     * Easy way to get a running MBeanServer with the required MBeans.
     */
    public void testRun() {

        try {
            System.out.println("I'm sooo tired...");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            // nothing to do
        }
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
        this.ctx.getBean("mbeanServer");
        this.ctx.getBean("org.eclipse.swordfish.core.management.components.ManagementController");
        InternalInstrumentationManager manager = this.getExternalManager();
        Resource res = this.registerExternal(manager);
        InstrumentationManagerBean managerBean = this.getInternalManager();
        this.registerInternal(res, managerBean);
        this.registerParticipantMonitor(managerBean);
        // make sure EmAdapter is instantiated
        this.ctx.getBean("org.eclipse.swordfish.core.management.adapter.EmAdapter");
        // Object jmxConnector =
        // ctx.getBean("org.eclipse.swordfish.core.management.adapter.JMXConnectorAdapter");
        // this.jmxConnectionServer = createJMXConnectionServer();
        // if (null != this.jmxConnectionServer) {
        // this.jmxConnectionServer.start();
        // }
    }

    /*	*//**
     * Creates the JMX connection server.
     * 
     * @return the JMX connector server
     */
    /*
     * private JMXConnectorServer createJMXConnectionServer() { JMXConnectorServer server =
     * null; try { // workaround due to a bug in JDK 1.4.2 //
     * (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4267864) // If an RMI registry
     * already exists, trying to create a second // one on a different port // results in an
     * exception // To work around this bug, we don't use the NamingService that // comes
     * with MX4J but call // createRegistry instead and ignore the exception.
     * 
     * try { Registry registry = LocateRegistry.createRegistry(rmiPort); } catch (Exception
     * e) { // can be ignored as described above }
     * 
     * if (registry == null) { this.namingServiceName =
     * ObjectName.getInstance("naming:type=rmiregistry"); if
     * (!this.mbeanServer.isRegistered(this.namingServiceName)) {
     * this.mbeanServer.createMBean("mx4j.tools.naming.NamingService", namingServiceName,
     * null); Attribute attr = new Attribute("Port", new Integer(rmiPort));
     * this.mbeanServer.setAttribute(this.namingServiceName, attr);
     * this.mbeanServer.invoke(this.namingServiceName, "start", null, null); } }
     * 
     * String jndiPath = "/jmxconnector"; JMXServiceURL url = new JMXServiceURL(
     * "service:jmx:rmi:///jndi/rmi://localhost:" + rmiPort + jndiPath); // Create and start
     * the RMIConnectorServer server = JMXConnectorServerFactory.newJMXConnectorServer(url,
     * null, this.mbs); } catch (Exception e) { e.printStackTrace(); } return server; }
     */

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        if (null != this.jmxConnectionServer) {
            this.jmxConnectionServer.stop();
        }
        this.ctx.destroy();
        super.tearDown();
    }

    /**
     * Gets the external manager.
     * 
     * @return the external manager
     * 
     * @throws BeansException
     */
    private InternalInstrumentationManager getExternalManager() throws BeansException {
        Object obj = this.ctx.getBean("org.eclipse.swordfish.papi.extension.instrumentation.InstrumentationManager");
        if (obj instanceof ExtensionFactory) {
            ExtensionFactory fac = (ExtensionFactory) obj;
            obj = fac.getInstance(null);
        }
        InternalInstrumentationManager manager = (InternalInstrumentationManager) obj;
        return manager;
    }

    /**
     * Gets the internal manager.
     * 
     * @return the internal manager
     * 
     * @throws BeansException
     */
    private InstrumentationManagerBean getInternalManager() throws BeansException {
        InstrumentationManagerBean managerBean =
                (InstrumentationManagerBean) this.ctx
                    .getBean("org.eclipse.swordfish.core.management.instrumentation.InstrumentationManagerExternal");
        return managerBean;
    }

    /**
     * Register external.
     * 
     * @param manager
     *        the manager
     * 
     * @return the resource
     * 
     * @throws ParticipantHandlingException *
     * @throws InternalSBBException
     */
    private Resource registerExternal(final InternalInstrumentationManager manager) throws InternalSBBException {
        Resource res = new Resource();
        res.setFoo("snafu");
        InputStream is =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/management/instrumentation/ResourceDesc.xml");
        manager.registerInstrumentation(res, is);
        return res;
    }

    /**
     * Register internal.
     * 
     * @param res
     *        the res
     * @param managerBean
     *        the manager bean
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     */
    private void registerInternal(final Resource res, final InstrumentationManagerBean managerBean)
            throws InternalInfrastructureException, AlreadyRegisteredException {
        InputStream is;
        Referer ref = new Referer();
        ObjectName on = managerBean.getObjectName(res);
        ref.setChild(on);
        ref.setSingleChild(on);
        is =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/management/instrumentation/RefererDesc.xml");
        managerBean.registerInstrumentation(ref, is);
    }

    /**
     * Register participant monitor.
     * 
     * @param managerBean
     *        the manager bean
     */
    private void registerParticipantMonitor(final InstrumentationManagerBean managerBean) {
        UnifiedParticipantIdentity uip = new UnifiedParticipantIdentity(new DummyParticipantIdentity());
        new ParticipantMonitorBackend(uip, managerBean);
    }

}
