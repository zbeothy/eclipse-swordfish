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
 * The Interface OperationMonitorMBean.
 * 
 */
public interface OperationMonitorMBean {

    /**
     * Gets the avg response time.
     * 
     * @return average response time in milliseconds for request/response type service operations 0
     *         for other operations
     */
    Integer getAvgResponseTime();

    /**
     * Gets the avg response time period.
     * 
     * @return the sample time for which to collect average response times in minutes note that the
     *         resolution is full minutes of sample data
     */
    Integer getAvgResponseTimePeriod();

    /**
     * Gets the max response time.
     * 
     * @return maximum response time in milliseconds during last maxResponseTimePeriod minutes
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
     * @return minimum response time in milliseconds during last minResponseTimePeriod minutes
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
     * Gets the operation name.
     * 
     * @return the name of the service operation for which monitoring data is collected
     */
    String getOperationName();

    /**
     * Gets the participant id.
     * 
     * @return the id of the service participant providing/using the operation. Form:
     *         [ApplicationId/InstanceId]
     */
    String getParticipantId();

    /**
     * Gets the participant role.
     * 
     * @return the role the participant has regarding the operation (consumer|provider)
     */
    String getParticipantRole();

    /**
     * Gets the pct app fail.
     * 
     * @return percentage of application failures for requests during last pctAppFailPeriod minutes
     */
    Integer getPctAppFail();

    /**
     * Gets the pct app fail period.
     * 
     * @return the sample time for which to collect the percentage of application failures in
     *         minutes note that the resolution is full minutes of sample data
     */
    Integer getPctAppFailPeriod();

    /**
     * Gets the request rate.
     * 
     * @return number of requests per minute during last requestRatePeriod minutes
     */
    Integer getRequestRate();

    /**
     * Gets the request rate period.
     * 
     * @return the sample time for which to collect the request rate in minutes note that the
     *         resolution is full minutes of sample data
     */
    Integer getRequestRatePeriod();

    /**
     * Gets the request utilization.
     * 
     * @return dummy - currently unused
     */
    Integer getRequestUtilization();

    /**
     * Gets the request utilization period.
     * 
     * @return dummy currently unused
     */
    Integer getRequestUtilizationPeriod();

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
     * @param minutes -
     *        sample time for which to collect average response times note that the resolution is
     *        full minutes of sample data
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
     * Sets the pct app fail period.
     * 
     * @param minutes -
     *        sample time for which to collect application failure rates note that the resolution is
     *        full minutes of sample data
     */
    void setPctAppFailPeriod(Integer minutes);

    /**
     * Sets the request rate period.
     * 
     * @param minutes -
     *        sample time for which to request rates note that the resolution is full minutes of
     *        sample data
     */
    void setRequestRatePeriod(Integer minutes);

    /**
     * Sets the request utilization period.
     * 
     * @param minutes -
     *        dummy currently unused
     */
    void setRequestUtilizationPeriod(Integer minutes);

    /**
     * Sets the total requests period.
     * 
     * @param minutes
     *        new sample period for total requests
     */
    void setTotalRequestsPeriod(Integer minutes);

}
