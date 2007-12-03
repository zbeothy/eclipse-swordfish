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
package org.eclipse.swordfish.core.management.objectname;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.monitor.Monitor;

// import org.eclipse.swordfish.core.management.logging.jmx.JMXLogFileReader;

/**
 * This class is the central manager to create and manage <code>ObjectNames</code> for all
 * relevant jmx components.
 * 
 */
public final class ManagementObjectNameUtil {

    /** Map for all <code>ObjectNames</code>. */
    private static Map objectNames = Collections.synchronizedMap(new WeakHashMap());

    /**
     * This method return a specific <code>ObjectName</code> for the passed
     * <code>Manageable</code> object.
     * 
     * @param manageable
     *        to create an ObjectName
     * 
     * @return the <code>ObjectName</code> for the passed object
     * 
     * @throws MalformedObjectNameException
     *         if it is not possible to create an ObjectName
     */
    public static ObjectName getObjectNameForComponent(final Object manageable) throws MalformedObjectNameException {
        return createObjectName("sbb/component:type=manageable,class=" + manageable.getClass().getName() + ",identifier="
                + manageable.hashCode());
    }

    /**
     * The method return a specific <code>ObjectName</code> for the passed <code>Monitor</code>.
     * 
     * @param observedObjectName
     *        the <code>ObjectName</code> of the observed component
     * @param monitor
     *        the <code>Monitor</code>
     * @return the <code>ObjectName</code> for the passed <code>Monitor</code>
     * @throws MalformedObjectNameException
     *         if it is not possible to create an ObjectName
     */
    public static ObjectName getObjectNameForJMXMonitor(final String observedObjectName, final Monitor monitor)
            throws MalformedObjectNameException {
        return createObjectName(observedObjectName + ",Monitor=" + monitor.hashCode());
    }

    /**
     * The method return a specific <code>ObjectName</code> for the passed <code>Stats</code>.
     * 
     * @param observedObjectName
     *        the observed object name
     * @param monitor
     *        the monitor
     * 
     * @return the <code>ObjectName</code> for the passed <code>Stats</code>
     * 
     * @throws MalformedObjectNameException
     *         if it is not possible to create an ObjectName
     */
    /*
     * - removed after moving in from sbb-artix public static ObjectName
     * getObjectNameForJSR77Stats(final Stats stats) throws MalformedObjectNameException { return
     * createObjectName("sbb/statistics:type=statistics,class=" + stats.getClass().getName() +
     * ",identifier=" + stats.hashCode()); }
     */

    /**
     * The method return a specific <code>ObjectName</code> for the passed <code>Statistic</code>
     * 
     * @param statistic
     *        to create an <code>ObjectName</code>
     * @param type
     *        the type of the stats
     * @param stats
     *        the Stats
     * @return the <code>ObjectName</code> for the passed <code>Statistic</code>
     * @throws MalformedObjectNameException
     *         if it is not possible to create an ObjectName
     */
    /*
     * - removed after moving in from sbb-artix public static ObjectName
     * getObjectNameForJSR77Statistics(final Statistic statistic, final String type, final Stats
     * stats) throws MalformedObjectNameException { String obString = "sbb/jsr77:type=" + type +
     * ",name=" + statistic.getName() + ",componentclass=" + stats.getClass().getName() + ",hash=" +
     * statistic.hashCode(); return createObjectName(obString); }
     */

    /**
     * This method is the creater of <code>ObjectName</code>
     * 
     * @param objectName
     *        the string representation of the <code>ObjectName</code>
     * @return the created <code>ObjectName</code>
     * @throws MalformedObjectNameException
     *         if it is not possible to create an ObjectName
     */
    private static ObjectName createObjectName(final String objectName) throws MalformedObjectNameException {
        ObjectName obName = (ObjectName) objectNames.get(objectName);
        if (obName == null) {
            obName = new ObjectName(objectName);
        }
        return obName;
    }

    /**
     * This method create a specific <code>ObjectName</code> for every instance of a
     * <code>JMXLogFileReader</code>.
     * 
     * @param objectName
     *        the object name
     * 
     * @return the ObjectName for the passed <code>JMXLogFileReader</code>
     * 
     * @throws MalformedObjectNameException
     *         if it is not possible to create an ObjectName
     */
    /*
     * - removed after moving in from sbb-artix public static ObjectName
     * getObjectNameForJMXLogFileReader(final JMXLogFileReader reader) throws
     * MalformedObjectNameException { return
     * createObjectName("sbb/logging/logfile:type=logfilereader,appender=" +
     * reader.getAppenderName() + ",id=" + reader.hashCode()); }
     */

    /**
     * The private Constructor for the ManagementObjectNameUtil.
     */
    private ManagementObjectNameUtil() {
        // do nothing

    }
}
