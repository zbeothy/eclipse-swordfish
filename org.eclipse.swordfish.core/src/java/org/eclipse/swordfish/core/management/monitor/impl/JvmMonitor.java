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
package org.eclipse.swordfish.core.management.monitor.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Strongly reduced version of old JVM monitor by mmoehler.
 * 
 */
public class JvmMonitor {

    // -------------------------------------------------------------- Constants

    /** Constant that holds the java version of the of the VM in that this code runs. */
    public static final String JAVA_VERSION = System.getProperty("java.version");

    /** Constant that holds the java vendor of the of the VM in that this code runs. */
    public static final String JAVA_VENDOR = System.getProperty("java.vendor");

    /** Constant that holds the host name of the machine into this VM runs. */
    public static final String NODE;

    /** Constant that holds the current RUNTIME instance. */
    private static final Runtime RUNTIME = Runtime.getRuntime();

    // Initializer of the NODE constant
    static {
        String node;
        try {
            node = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            node = null;
        }
        NODE = node;
    }

    /**
     * Returns the count of available processors.
     * 
     * @return an int as count of the processors
     */
    public int getAvailableProcessors() {
        return RUNTIME.availableProcessors();
    }

    /**
     * Returns a <code>long</code> as free memory in bytes.
     * 
     * @return <code>long</code> as free memory in bytes
     */
    public long getFreeMemory() {
        return RUNTIME.freeMemory();
    }

    /**
     * The vendor of the JVMImpl we are running on. This is the value of java.vendor system property
     * 
     * @return the java vendor
     */
    public String getJavaVendor() {
        return JAVA_VENDOR;
    }

    /**
     * The version of the JVMImpl we are running on. This is the value of java.version system
     * property
     * 
     * @return the java version
     */
    public String getJavaVersion() {
        return JAVA_VERSION;
    }

    /**
     * Returns a <code>long</code> as maximal availlable memory in bytes.
     * 
     * @return <code>long</code> as maximal availlable memory in bytes
     */
    public long getMaxMemory() {
        return RUNTIME.maxMemory();
    }

    /**
     * The node we are running on. This is the fully qualified host name returned for
     * InetAddress.getLocalHost.toString(); we return null if there is no network
     * 
     * @return the node we are running on
     */
    public String getNode() {
        return NODE;
    }

    /**
     * Returns a <code>long</code> as total available memory in bytes.
     * 
     * @return <code>long</code> as total available memory in bytes
     */
    public long getTotalMemory() {
        return RUNTIME.totalMemory();
    }

}
