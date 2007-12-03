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
package org.eclipse.swordfish.core.utils.jmx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 * WebSphere naming strategy. Shall be used on WebSphere Application Server.
 * 
 */
public class WebSphereNamingStrategy implements NamingStrategy {

    /** Singleton instance. */
    private static final WebSphereNamingStrategy SINGLETON = new WebSphereNamingStrategy();

    /** "cell" property name. */
    private static final String PROPN_CELL = "cell";

    /** "node" property name. */
    private static final String PROPN_NODE = "node";

    /** "process" property name. */
    private static final String PROPN_PROCESS = "process";

    /**
     * Returns the instance of this class.
     * 
     * @return singleton instance
     */
    public static WebSphereNamingStrategy getInstance() {
        return SINGLETON;
    }

    /**
     * Finds the method in a class.
     * 
     * @param targetClass
     *        class to search method in
     * @param methodName
     *        name of the method to search
     * @param parameters
     *        expected parameters
     * @return reflection method or null if there is no such method
     */
    private static Method findMethod(final Class targetClass, final String methodName, final Class[] parameters) {
        if (targetClass == null)
            return null;
        else {
            try {
                return targetClass.getMethod(methodName, new Class[] {});
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
    }

    /**
     * Determines if the system is running on WebSphere.
     */
    private final boolean runningOnWebSphere;

    /** Cached reflection method. */
    private final Method methodGetAdminService;

    /** Cached reflection method. */
    private final Method methodGetCellName;

    /** Cached reflection method. */
    private final Method methodGetNodeName;

    /** Cached reflection method. */
    private final Method methodGetProcessName;

    private final Method methodQueryNames;

    private final String websphereVersion;

    /**
     * Hidden constructor.
     */
    private WebSphereNamingStrategy() {
        Class classAdminServiceFactory = null;
        Class classAdminService = null;
        boolean classesFound = true;
        try {
            classAdminServiceFactory = Class.forName("com.ibm.websphere.management.AdminServiceFactory");
            classAdminService = Class.forName("com.ibm.websphere.management.AdminService");
        } catch (ClassNotFoundException e) {
            classesFound = false;
        }
        this.methodGetAdminService = findMethod(classAdminServiceFactory, "getAdminService", new Class[] {});
        this.methodGetCellName = findMethod(classAdminService, "getCellName", new Class[] {});
        this.methodGetNodeName = findMethod(classAdminService, "getNodeName", new Class[] {});
        this.methodGetProcessName = findMethod(classAdminService, "getProcessName", new Class[] {});
        this.methodQueryNames = findMethod(classAdminService, "queryNames", new Class[] {ObjectName.class, QueryExp.class});
        this.runningOnWebSphere =
                classesFound && (this.methodGetAdminService != null) && (this.methodGetCellName != null)
                        && (this.methodGetNodeName != null) && (this.methodGetProcessName != null);
        String serverVersion = null;
        if (this.methodQueryNames != null) {
            try {
                final Set names =
                        (Set) this.methodQueryNames.invoke(this.getAdminService(), new Object[] {
                                new ObjectName("WebSphere:type=Server,*"), null});
                if (!names.isEmpty()) {
                    final ObjectName objectNameServer = (ObjectName) names.iterator().next();
                    serverVersion = objectNameServer.getKeyProperty("version");
                }
            } catch (Throwable t) {
                System.out.println("Failed to get WAS Version");
                t.printStackTrace(System.out);
            }
        }
        this.websphereVersion = serverVersion;
        System.out.println("Detected WebSphere Application Server version: " + this.websphereVersion);
    }

    /**
     * {@inheritDoc} Delegates to {@link #getObjectName(ObjectName)} with parameter
     * <code>new ObjectName(objectName)</code>.
     * 
     * @see org.eclipse.swordfish.core.utils.jmx.NamingStrategy#createObjectName(java.lang.String)
     */
    public ObjectName createObjectName(final String objectName) throws MalformedObjectNameException {
        return this.getObjectName(new ObjectName(objectName));
    }

    /**
     * Executes method getCellName on WAS AdminService.
     * 
     * @return cell name
     */
    public String getCellName() {
        return this.invokeAdminServiceStringMethod(this.methodGetCellName);
    }

    /**
     * Executes method getNodeName on WAS AdminService.
     * 
     * @return node name
     */
    public String getNodeName() {
        return this.invokeAdminServiceStringMethod(this.methodGetNodeName);
    }

    /**
     * {@inheritDoc}
     * 
     * @return the object name extended with a WebSphere specific properties: <code>cell</code>,
     *         <code>node</code> and <code>process</code>.
     * 
     * @see org.eclipse.swordfish.core.utils.jmx.NamingStrategy#getObjectName(javax.management.ObjectName)
     */
    public ObjectName getObjectName(final ObjectName objectName) {
        if (this.isRunningOnWebSphere()) {
            if (objectName == null) return null;
            final Map extendedProperties = this.extendProperties(objectName.getKeyPropertyList());
            final String extendedName =
                    this.buildObjectNameString(objectName.getDomain(), extendedProperties, objectName.isPropertyPattern());
            try {
                return ObjectName.getInstance(extendedName);
            } catch (MalformedObjectNameException e) {
                System.out.println("Failed to create an extended ObjectName for string " + extendedName);
                e.printStackTrace(System.out);
            }
        }
        return StandardNamingStrategy.getInstance().getObjectName(objectName);
    }

    /**
     * Executes method getProcessName on WAS AdminService.
     * 
     * @return process name
     */
    public String getProcessName() {
        return this.invokeAdminServiceStringMethod(this.methodGetProcessName);
    }

    /**
     * {@inheritDoc}
     * 
     * @return the object name without a WebSphere specific properties: <code>cell</code>,
     *         <code>node</code> and <code>process</code>.
     * 
     * @see org.eclipse.swordfish.core.utils.jmx.NamingStrategy#getSimpleObjectName(javax.management.ObjectName)
     */
    public ObjectName getSimpleObjectName(final ObjectName objectName) {
        if (this.isRunningOnWebSphere()) {
            final Map suppressedProperties = this.suppressProperties(objectName.getKeyPropertyList());
            final String suppressedName =
                    this.buildObjectNameString(objectName.getDomain(), suppressedProperties, objectName.isPropertyPattern());
            try {
                return ObjectName.getInstance(suppressedName);
            } catch (MalformedObjectNameException e) {
                System.out.println("Failed to create a simple ObjectName for string " + suppressedName);
                e.printStackTrace(System.out);
            }
        }
        return StandardNamingStrategy.getInstance().getSimpleObjectName(objectName);
    }

    /**
     * Returns the version of the application server.
     * 
     * @return version as string, like "6.1.0.3"
     */
    public String getWebsphereVersion() {
        return this.websphereVersion;
    }

    /**
     * Determines whether the system runs on WebSphere.
     * 
     * @return true if the IBM Classes are available, false - otherwise
     */
    public boolean isRunningOnWebSphere() {
        return this.runningOnWebSphere;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.utils.jmx.NamingStrategy#lookupObjectName(javax.management.ObjectName)
     */
    public ObjectName lookupObjectName(final ObjectName objectName) {
        if (this.isRunningOnWebSphere()) {
            try {
                final Set extendedObjectNames = this.queryNames(this.getObjectName(objectName));
                if (extendedObjectNames.isEmpty()) {
                    final Set objectNames = this.queryNames(objectName);
                    if (!objectNames.isEmpty())
                        return (ObjectName) objectNames.iterator().next();
                    else {
                        System.out.println("Empty set returned when querying " + objectName);
                    }
                } else
                    return (ObjectName) extendedObjectNames.iterator().next();
            } catch (Throwable t) {
                System.out.println("Failed to query names for " + objectName);
                t.printStackTrace();
            }
        }
        return StandardNamingStrategy.getInstance().lookupObjectName(objectName);
    }

    /**
     * Queries the object names by a template.
     * 
     * @param objectName
     *        template
     * @return set of found object names
     */
    public Set queryNames(final ObjectName objectName) {
        try {
            return (Set) this.methodQueryNames.invoke(this.getAdminService(), new Object[] {objectName, null});
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Builds an object name string which can be used to obtain ObjectName instance.
     * 
     * @param domain
     *        domain
     * @param properties
     *        map of properties
     * @param pattern
     *        if true then name shall be patterned
     * @return the string in form "domain:prop1=val1,prop2=val2,..." if pattern is true then "*"
     *         will be added to the end
     */
    private String buildObjectNameString(final String domain, final Map properties, final boolean pattern) {
        final StringBuffer name = new StringBuffer();
        name.append(domain);
        name.append(":");
        for (Iterator keys = properties.keySet().iterator(); keys.hasNext();) {
            final String propertyKey = (String) keys.next();
            name.append(propertyKey);
            name.append("=");
            name.append(properties.get(propertyKey));
            if (keys.hasNext()) {
                name.append(",");
            }
        }
        if (pattern) {
            if (!properties.isEmpty()) {
                name.append(",");
            }
            name.append("*");
        }
        return name.toString();
    }

    /**
     * Extends properties map with WebSphere specific properties.
     * 
     * @param properties
     *        the properties to extend
     * @return the extended properties
     */
    private Map extendProperties(final Hashtable properties) {
        final Map map = new HashMap(properties);
        if (!map.containsKey(PROPN_CELL)) {
            map.put(PROPN_CELL, this.getCellName());
        }
        if (!map.containsKey(PROPN_NODE)) {
            map.put(PROPN_NODE, this.getNodeName());
        }
        if (!map.containsKey(PROPN_PROCESS)) {
            map.put(PROPN_PROCESS, this.getProcessName());
        }
        return map;
    }

    /**
     * Executes static method getAdminService of WAS AdminServiceFactory.
     * 
     * @return WAS AdminService instance
     * @throws InvocationTargetException
     *         if cannot invoke
     * @throws IllegalAccessException
     *         if cannot invoke
     */
    private Object getAdminService() throws InvocationTargetException, IllegalAccessException {
        return this.methodGetAdminService.invoke(null, new Object[] {});
    }

    /**
     * Invokes the specified method on WAS AdminService.
     * 
     * @param method
     *        the method to invoke
     * @return the string result
     */
    private String invokeAdminServiceStringMethod(final Method method) {
        try {
            return (String) method.invoke(this.getAdminService(), new Object[] {});
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Removes WebSphere specific properties from properties map.
     * 
     * @param properties
     *        the properties to suppress
     * @return the suppressed properties
     */
    private Map suppressProperties(final Hashtable properties) {
        final Map map = new HashMap(properties);
        if (map.containsKey(PROPN_CELL)) {
            map.remove(PROPN_CELL);
        }
        if (map.containsKey(PROPN_NODE)) {
            map.remove(PROPN_NODE);
        }
        if (map.containsKey(PROPN_PROCESS)) {
            map.remove(PROPN_PROCESS);
        }
        return map;
    }

}
