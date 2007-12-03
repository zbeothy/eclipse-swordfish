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

/**
 * Definition of attributes that are continually monitored by an attached management system.
 * 
 */

public interface SbbMonitorMBean {

    /**
     * Gets the avg response time.
     * 
     * @return average response time in milliseconds for request/response style service operations
     */
    Integer getAvgResponseTime();

    /**
     * Gets the avg response time period.
     * 
     * @return sample period for AvgResponseTime in minutes
     */
    Integer getAvgResponseTimePeriod();

    /**
     * Gets the java vendor.
     * 
     * @return vendor id of Java Virtual Machine
     */
    String getJavaVendor();

    /**
     * Gets the java version.
     * 
     * @return version identifier of Java VirtualMachine
     */
    String getJavaVersion();

    /**
     * Gets the JVM cpus.
     * 
     * @return number of CPUs available to the JVM
     */
    Integer getJVMCpus();

    /**
     * Gets the JVM total memory.
     * 
     * @return total memory available to JVM in bytes
     */
    Long getJVMTotalMemory();

    /**
     * Gets the JVM util pct.
     * 
     * @return percentage of heap space used in JVM - see WL metrics
     */
    Integer getJVMUtilPct();

    /**
     * Gets the max response time.
     * 
     * @return maximum response time in milliseconds for request/response style service operations
     */
    Integer getMaxResponseTime();

    /**
     * Gets the max response time period.
     * 
     * @return the sample time for which to collect maximum response time in minutes note that the
     *         resolution is full minutes of sample data
     */
    Integer getMaxResponseTimePeriod();

    /**
     * Gets the min response time.
     * 
     * @return minimum response time in milliseconds for request/response style service operations
     */
    Integer getMinResponseTime();

    /**
     * Gets the min response time period.
     * 
     * @return the sample time for which to collect minimum response time in minutes note that the
     *         resolution is full minutes of sample data
     */
    Integer getMinResponseTimePeriod();

    /**
     * Gets the node.
     * 
     * @return node (network address) this InternalSBB is operating on
     */
    String getNode();

    /**
     * Gets the num busses.
     * 
     * @return number of busses currently instantiated
     */
    Integer getNumBusses();

    /**
     * Gets the pct app fail1.
     * 
     * @return percentage of requests that failed due to application (i.e., non-InternalSBB) errors
     *         during the last 5 min
     */
    Integer getPctAppFail1();

    /**
     * Gets the pct app fail1 period.
     * 
     * @return sample period for PctAppFail1 in minutes
     */
    Integer getPctAppFail1Period();

    /**
     * Gets the pct app fail2.
     * 
     * @return percentage of requests that failed due to application (i.e., non-InternalSBB) errors
     *         during the last hour
     */
    Integer getPctAppFail2();

    /**
     * Gets the pct app fail2 period.
     * 
     * @return sample period for PctAppFail2 in minutes
     */
    Integer getPctAppFail2Period();

    /**
     * Gets the pct app fail3.
     * 
     * @return percentage of requests that failed due to application (i.e., non-InternalSBB) errors
     *         during the last 6 hours
     */
    Integer getPctAppFail3();

    /**
     * Gets the pct app fail3 period.
     * 
     * @return sample period for PctAppFail3 in minutes
     */
    Integer getPctAppFail3Period();

    /**
     * Gets the pct net fail.
     * 
     * @return percentage of failures due to network problems TODO add time intervals
     */
    Integer getPctNetFail();

    /**
     * Gets the pct net fail period.
     * 
     * @return sample period for PctNetFail in minutes
     */
    Integer getPctNetFailPeriod();

    /**
     * Gets the req rate1.
     * 
     * @return average number of requests per minute during the period set in ReqRate1Period
     */
    Integer getReqRate1();

    /**
     * Gets the req rate1 period.
     * 
     * @return sample period for ReqRate1 in minutes
     */
    Integer getReqRate1Period();

    /**
     * Gets the req rate2.
     * 
     * @return average number of requests per minute during the last hour
     */
    Integer getReqRate2();

    /**
     * Gets the req rate2 period.
     * 
     * @return sample period for ReqRate2 in minutes
     */
    Integer getReqRate2Period();

    /**
     * Gets the req rate3.
     * 
     * @return average number of requests per minute during the last 6 hours
     */
    Integer getReqRate3();

    /**
     * Gets the req rate3 period.
     * 
     * @return sample period for ReqRate2 in minutes
     */
    Integer getReqRate3Period();

    /**
     * Gets the state.
     * 
     * @return state of overall InternalSBB operation - see JSR 77
     */
    String getState();

    /**
     * Gets the total requests.
     * 
     * @return the total number of requests processed in the period
     */
    Integer getTotalRequests();

    /**
     * Gets the total requests period.
     * 
     * @return sample period in minutes for total requests
     */
    Integer getTotalRequestsPeriod();

    /**
     * Sets the avg response time period.
     * 
     * @param minutes
     *        period for AvgResponseTime in minutes
     */
    void setAvgResponseTimePeriod(Integer minutes);

    /**
     * Sets the max response time period.
     * 
     * @param minutes -
     *        sample time for which to collect maximum response times note that the resolution is
     *        full minutes of sample data
     */
    void setMaxResponseTimePeriod(Integer minutes);

    /**
     * Sets the min response time period.
     * 
     * @param minutes -
     *        sample time for which to collect minimum response times note that the resolution is
     *        full minutes of sample data
     */
    void setMinResponseTimePeriod(Integer minutes);

    /**
     * Sets the pct app fail1 period.
     * 
     * @param minutes
     *        period for PctAppFail1 in minutes
     */
    void setPctAppFail1Period(Integer minutes);

    /**
     * Sets the pct app fail2 period.
     * 
     * @param minutes
     *        period for PctAppFail2 in minutes
     */
    void setPctAppFail2Period(Integer minutes);

    /**
     * Sets the pct app fail3 period.
     * 
     * @param minutes
     *        period for PctAppFail3 in minutes
     */
    void setPctAppFail3Period(Integer minutes);

    /**
     * Sets the pct net fail period.
     * 
     * @param minutes
     *        period for PctNetFail in minutes
     */
    void setPctNetFailPeriod(Integer minutes);

    /**
     * Sets the req rate1 period.
     * 
     * @param minutes
     *        period for ReqRate1 in minutes
     */
    void setReqRate1Period(Integer minutes);

    /**
     * Sets the req rate2 period.
     * 
     * @param minutes
     *        period for ReqRate2 in minutes
     */
    void setReqRate2Period(Integer minutes);

    /**
     * Sets the req rate3 period.
     * 
     * @param minutes
     *        period for ReqRate3 in minutes
     */
    void setReqRate3Period(Integer minutes);

    /**
     * Sets the total requests period.
     * 
     * @param minutes
     *        new sample period for total requests
     */
    void setTotalRequestsPeriod(Integer minutes);

}
