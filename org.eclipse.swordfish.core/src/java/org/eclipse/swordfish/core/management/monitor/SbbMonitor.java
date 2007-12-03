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
package org.eclipse.swordfish.core.management.monitor;

import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.swordfish.core.management.monitor.impl.JvmMonitor;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;

/**
 * Statistics object for overall messaging monitoring Note: this has to be an abstract class so the
 * MBeanServer accepts it as an MBean.
 * 
 */

public class SbbMonitor extends BaseMonitor {

    /** The jvm monitor. */
    private JvmMonitor jvmMonitor;

    /** List of ids for currently registered participants Value: ParticipantMonitor. */
    private HashSet participantMonitors;

    /**
     * Instantiates a new sbb monitor.
     */
    public SbbMonitor() {
        this.jvmMonitor = new JvmMonitor();
        this.participantMonitors = new HashSet();
    }

    /**
     * Adds the participant monitor.
     * 
     * @param pm
     *        the pm
     */
    public void addParticipantMonitor(final ParticipantMonitor pm) {
        this.participantMonitors.add(pm);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.monitor.BaseMonitor#destroy()
     */
    @Override
    public void destroy() {
        this.jvmMonitor = null;
        if (null != this.participantMonitors) {
            this.participantMonitors.clear();
            this.participantMonitors = new HashSet();
        }
        super.destroy();
    }

    /**
     * Gets the java vendor.
     * 
     * @return the java vendor
     */
    public String getJavaVendor() {
        return this.jvmMonitor.getJavaVendor();
    }

    /**
     * Gets the java version.
     * 
     * @return the java version
     */
    public String getJavaVersion() {
        return this.jvmMonitor.getJavaVersion();
    }

    /**
     * Gets the JVM cpus.
     * 
     * @return the JVM cpus
     * 
     * @see org.eclipse.swordfish.core.management.SbbMonitorMBean#getJVMCpus()
     */
    public Integer getJVMCpus() {
        Integer ret = new Integer(this.jvmMonitor.getAvailableProcessors());
        return ret;
    }

    /**
     * Gets the JVM max memory.
     * 
     * @return the JVM max memory
     * 
     * @see org.eclipse.swordfish.core.management.SbbMonitorMBean#getJVMTotalMemory()
     */
    public Long getJVMMaxMemory() {
        Long ret = new Long(this.jvmMonitor.getMaxMemory());
        return ret;
    }

    /**
     * Gets the JVM total memory.
     * 
     * @return the JVM total memory
     * 
     * @see org.eclipse.swordfish.core.management.SbbMonitorMBean#getJVMTotalMemory()
     */
    public Long getJVMTotalMemory() {
        Long ret = new Long(this.jvmMonitor.getTotalMemory());
        return ret;
    }

    /**
     * Gets the JVM util pct.
     * 
     * @return the JVM util pct
     * 
     * @see org.eclipse.swordfish.core.management.SbbMonitorMBean#getJVMUtilPct()
     */
    public Integer getJVMUtilPct() {
        Integer ret = null;
        Long totalMem = new Long(this.jvmMonitor.getTotalMemory());
        Long freeMem = new Long(this.jvmMonitor.getFreeMemory());
        Long maxMem = new Long(this.jvmMonitor.getMaxMemory());
        if ((totalMem != null) && (freeMem != null) && (maxMem != null)) {
            float used = totalMem.floatValue() - freeMem.floatValue();
            float max = maxMem.floatValue();
            if (max != 0) {
                float util = (used / max) * 100;
                ret = new Integer(Math.round(util));
            } else {
                ret = new Integer(0);
            }
        }
        return ret;
    }

    /**
     * Gets the node.
     * 
     * @return the node
     */
    public String getNode() {
        return this.jvmMonitor.getNode();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the num participants
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getNumBusses()
     */
    public Integer getNumParticipants() {
        return new Integer(this.participantMonitors.size());
    }

    /**
     * (non-Javadoc).
     * 
     * @return the state
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getState()
     */
    public String getState() {
        State ret = State.STOPPED; // default if no participants are registered
        if (this.participantMonitors.size() > 0) {
            ret = State.RUNNING; // default if all participants are running
            for (Iterator iter = this.participantMonitors.iterator(); iter.hasNext();) {
                ParticipantMonitor pm = (ParticipantMonitor) iter.next();
                // state of the library is determined by worst state of
                // registered participant
                // running < stopped < failed
                if (ret.toInt() < pm.getStateInternal().toInt()) {
                    ret = pm.getStateInternal();
                }
            }
        }
        return ret.toString();
    }

    /**
     * Removes the participant monitor.
     * 
     * @param pm
     *        the pm
     */
    public void removeParticipantMonitor(final ParticipantMonitor pm) {
        this.participantMonitors.remove(pm);
    }

}
