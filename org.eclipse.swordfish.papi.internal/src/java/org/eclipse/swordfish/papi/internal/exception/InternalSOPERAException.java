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
package org.eclipse.swordfish.papi.internal.exception;

import java.util.Map;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;

/**
 * This is the root of all exceptions defined by InternalSBB. Must define the necessary class
 * members that are needed for classification reasons. This will be at least include
 * <ul>
 * <li>an error-number
 * <li>further contextual information
 * </ul>
 * <p>
 * Each Error instance must be assigned a unique identifier. Repeatedly occurring errors must be
 * distinguishable. When dealing with remote exceptions, InternalSBB should provide a correlation
 * mechanism to identify the remotely happening incident with the one appearing at the local call
 * stack. For identification of an error each thrown exception (InternalSBBException) hold provide
 * the following information:
 * <ul>
 * <li>paticipantID (application and instance) of the root cause (If error root is InternalSBB
 * there will be a specific identifier)
 * <li>hostname of the host running the participant / InternalSBB
 * <li>time of the root cause occurred.
 * <li>incident ID which is unique to this incident . The incident ID must be created with the very
 * first error causing some problem. If this error is wrapped in some higher level error or even
 * reported to some remote system for causing an error at this system , the inci-dentID will NOT
 * change o error ID identifying the exceptional condition causing this error
 * <li>contextual parameters. All information items which are used in the context of the error
 * shall be accessible from the error.
 * <li>textual description of error and context. These are the concrete values used in the context
 * but also the origin of these values (e.g. whether some configuration attribute is taken from a
 * local or a remote configuration).
 * </ul>
 * 
 */

public abstract class InternalSOPERAException extends Exception {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: InternalSOPERAException.java,v 1.1.2.3 2007/11/09 17:47:15 kkiehne Exp $";

    /** This classes version UID. */
    private static final long serialVersionUID = 8193131881933668000L;

    // ----------------------------------------------------- Instance Variables

    /**
     * The error code of a specific error that can be assigned to all SOPERA exceptions The range of
     * the error code is defined for each category of exception.
     */
    private int errorCode;

    /**
     * paticipantID (application and instance) of the root cause. If error root is InternalSBB there
     * will be a specific identifier.
     * 
     * FIXME This field references a class that may not be serializable
     */
    private InternalParticipantIdentity paticipantId;

    /** hostname of the host running the participant / InternalSBB. */
    private String hostName;

    /** time of the root cause occurred in millisec. */
    private long time;

    /**
     * ID which is unique to this incident . The incident ID must be created with the very first
     * error causing some problem. If this error is wrapped in some higher level error or even
     * reported to some remote system for causing an error at this system , the inci-dentID will NOT
     * change o error ID identifying the exceptional condition causing this error
     */
    private String incidentId;

    /**
     * All information items which are used in the context of the error.
     * 
     * FIXME This field references a class that may not be serializable
     */
    private Map contextualParameters;

    // ----------------------------------------------------------- Constructors

    /**
     * The Constructor.
     */
    protected InternalSOPERAException() {
        super();
    }

    /**
     * <p>
     * The Constructor.
     * </p>
     * 
     * @param message
     *        a {@link String} as a textual message
     */
    protected InternalSOPERAException(final String message) {
        super(message);
    }

    /**
     * <p>
     * The Constructor.
     * </p>
     * 
     * @param message
     *        a {@link String} as a textual message
     * @param cause
     *        a {@link Throwable} that caused this throwable to get thrown, or null if this
     *        throwable was not caused by another throwable, or if the causative throwable is
     *        unknown. If this field is equal to this throwable itself, it indicates that the cause
     *        of this throwable has not yet been initialized.
     */
    protected InternalSOPERAException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>
     * The Constructor.
     * </p>
     * 
     * @param hostName
     *        a {@link String} as hostname of the host running the participant / InternalSBB.
     * @param time
     *        a <code>long</code> as time of the root cause occurred in millisec.
     * @param message
     *        a {@link String} as a textual message
     * @param cause
     *        a {@link Throwable} that caused this throwable to get thrown, or null if this
     *        throwable was not caused by another throwable, or if the causative throwable is
     *        unknown. If this field is equal to this throwable itself, it indicates that the cause
     *        of this throwable has not yet been initialized.
     * @param paticipantId
     *        a {@link InternalParticipantIdentity} instance as paticipantID (application and
     *        instance) of the root cause. If error root is InternalSBB there will be a specific
     *        identifier.
     * @param contextualParameters
     *        a {@link Map} as all information items which are used in the context of the error.
     * @param incidentId
     *        a {@link String} as ID which is unique to this incident . The incident ID must be
     *        created with the very first error causing some problem. If this error is wrapped in
     *        some higher level error or even reported to some remote system for causing an error at
     *        this system , the inci-dentID will NOT change o error ID identifying the exceptional
     *        condition causing this error
     * @param errorCode
     *        an <code>int</code> as the error code of a specific error that can be assigned to
     *        all SOPERA exceptions The range of the error code is defined for each category of
     *        exception.
     */
    protected InternalSOPERAException(final String message, final Throwable cause, final int errorCode,
            final InternalParticipantIdentity paticipantId, final String hostName, final long time, final String incidentId,
            final Map contextualParameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.paticipantId = paticipantId;
        this.hostName = hostName;
        this.time = time;
        this.incidentId = incidentId;
        this.contextualParameters = contextualParameters;
    }

    /**
     * <p>
     * The Constructor.
     * </p>
     * 
     * @param cause
     *        a {@link Throwable} that caused this throwable to get thrown, or null if this
     *        throwable was not caused by another throwable, or if the causative throwable is
     *        unknown. If this field is equal to this throwable itself, it indicates that the cause
     *        of this throwable has not yet been initialized.
     */
    protected InternalSOPERAException(final Throwable cause) {
        super(cause);
    }

    // ------------------------------------------------------------- Properties

    /**
     * Gets the contextual parameters.
     * 
     * @return the contextual parameters
     */
    public Map getContextualParameters() {
        return this.contextualParameters;
    }

    /**
     * Gets the error code.
     * 
     * @return this Exceptions error code
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * Gets the host name.
     * 
     * @return the host name
     */
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Gets the incident id.
     * 
     * @return the incident id
     */
    public String getIncidentId() {
        return this.incidentId;
    }

    /**
     * Gets the paticipant id.
     * 
     * @return the paticipant id
     */
    public InternalParticipantIdentity getPaticipantId() {
        return this.paticipantId;
    }

    /**
     * Gets the time.
     * 
     * @return the time
     */
    public long getTime() {
        return this.time;
    }

    /**
     * Sets the contextual parameters.
     * 
     * @param contextualParameters
     *        the contextual parameters
     */
    public void setContextualParameters(final Map contextualParameters) {
        this.contextualParameters = contextualParameters;
    }

    /**
     * Sets the error code.
     * 
     * @param errorCode
     *        the error code that needs to be assigned to this object
     */
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Sets the host name.
     * 
     * @param hostName
     *        the host name
     */
    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    /**
     * Sets the incident id.
     * 
     * @param incidentId
     *        the incident id
     */
    public void setIncidentId(final String incidentId) {
        this.incidentId = incidentId;
    }

    /**
     * Sets the paticipant id.
     * 
     * @param paticipantId
     *        the paticipant id
     */
    public void setPaticipantId(final InternalParticipantIdentity paticipantId) {
        this.paticipantId = paticipantId;
    }

    /**
     * Sets the time.
     * 
     * @param time
     *        the time
     */
    public void setTime(final long time) {
        this.time = time;
    }
}
