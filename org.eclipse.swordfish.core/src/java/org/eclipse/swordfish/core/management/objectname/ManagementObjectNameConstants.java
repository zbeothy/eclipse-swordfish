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

import javax.management.ObjectName;

// import org.eclipse.swordfish.core.management.ManagementAdapterMaster;
// import org.eclipse.swordfish.core.management.logging.SBBManagementAppender;
// import org.eclipse.swordfish.core.management.logging.SBBNotificationBroadcasterManager;
// import org.eclipse.swordfish.core.management.logging.jmx.JMXLogFileManager;

/**
 * This class contains all used JMX ObjectNames inside the ManagementService.
 * 
 */
public final class ManagementObjectNameConstants {

    /** The <code>ObjectName</code> for the jmx Timer. */
    public static final ObjectName MANAGEMENT_TIMER_OBJECTNAME =
            createObjectName("sbb/monitoring:type=timer,class=javax.management.timer.Timer");

    /** The <code>ObjectName</code> for the jsr77 statistic listener. */
    public static final ObjectName MANAGEMENT_JSR77_LISTENER =
            createObjectName("sbb/monitoring:type=listener,"
                    + "class=org.eclipse.swordfish.core.management.jsr77.jmx.StatisticNotificationListener");

    /** The <code>ObjectName</code> of the <code>JMXLogFileManager</code>. */
    /*
     * - removed after moving in from sbb-artix public static final ObjectName
     * MANAGEMENT_LOGGING_MANAGER = createObjectName("sbb/logging/reader:type=logfilereadermanager" +
     * JMXLogFileManager.class.getName());
     */

    /**
     * The <code>ObjectName</code> of the <code>SBBManagementAppender</code>
     */
    /*
     * - removed after moving in from sbb-artix public static final ObjectName
     * MANAGEMENT_LOGGING_NOTIFICATIONAPPENDER =
     * createObjectName("sbb/logging:type=notificationappender,class=" +
     * SBBManagementAppender.class.getName());
     */

    /**
     * The <code>ObjectName</code> of the <code>SBBNotificationBroadcasterManager</code>
     */
    /*
     * - removed after moving in from sbb-artix public static final ObjectName
     * MANAGEMENT_LOGGING_NOTIFICATIONBROADCASTERMANAGER =
     * createObjectName("sbb/logging:type=notificationbroadcastermanager,class=" +
     * SBBNotificationBroadcasterManager.class.getName());
     */

    /**
     * Object name to use for ManagementServiceMaster
     */
    /*
     * - removed after moving in from sbb-artix public static final ObjectName
     * MANAGEMENT_ADAPTER_MASTER =
     * createObjectName("sbb/internal:type=ManagementAdapterMaster,class=" +
     * ManagementAdapterMaster.class.getName());
     */

    /**
     * Object name to use for MessagingMonitor implementation.
     */
    public static final ObjectName MANAGEMENT_MESSAGING_MONITOR = createObjectName("sbb/monitor:type=messagingmonitor");

    /** Object name to use for SbbMonitor. */
    public static final ObjectName SBB_MONITOR = createObjectName("sbb:name=sbbmonitor,type=adapter,*");

    /** Wildcard ObjectName for JVM MBeans. */
    public static final ObjectName JVM_WILDCARD =
            createObjectName("sbb/component:class=org.eclipse.swordfish.core.management.JVMImplInstrumentation,*");

    /**
     * This method create the <code>ObjectName</code> for the passed Strings.
     * 
     * @param obName
     *        String representation of the ObjectName
     * 
     * @return the <code>ObjectName</code> for the passed String
     */
    private static ObjectName createObjectName(final String obName) {
        ObjectName ob = null;
        try {
            ob = new ObjectName(obName);
        } catch (Exception e) {
            e.printStackTrace();
            // Ignore
        }
        return ob;
    }

    /**
     * The private Constructor.
     */
    private ManagementObjectNameConstants() {
    }

}
