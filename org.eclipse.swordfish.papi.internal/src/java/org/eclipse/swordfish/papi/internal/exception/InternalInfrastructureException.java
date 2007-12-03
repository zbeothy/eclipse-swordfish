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
 * This Exception is thrown if an infrastructure problem occurs which cannot be resolved by the
 * application but by administration. However, an application may re-try the message send at a later
 * time.
 * <ul>
 * <li>a TSP required by an internal proxy is unavailable or broken</li>
 * <li>persistent storage (LDAP, data bases, disk storage) needed by InternalSBB is unavailable or
 * broken</li>
 * <li>network is out of order</li>
 * <li>outage of a specific transport</li>
 * <li>provider is down or unreachable (may not lead to immediate failure if a messaging system is
 * used as transport)</li>
 * </ul>
 * not thrown
 * <ul>
 * <li>configuration issues -&gt; throw {@link InternalConfigurationException} </li>
 * </ul>
 */
public class InternalInfrastructureException extends InternalSBBException {

    // -------------------------------------------------------------- Constants

    /**
     * (R)evision (C)ontrol (S)ystem (Id)entifier.
     */
    public static final String RCS_ID =
            "@(#) $Id: InternalInfrastructureException.java,v 1.1.2.3 2007/11/09 17:47:15 kkiehne Exp $";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2361884365194427234L;

    // ----------------------------------------------------------- Constructors

    /**
     * The Constructor.
     */
    public InternalInfrastructureException() {
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
    public InternalInfrastructureException(final String message) {
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
    public InternalInfrastructureException(final String message, final Throwable cause) {
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
    public InternalInfrastructureException(final String message, final Throwable cause, final int errorCode,
            final InternalParticipantIdentity paticipantId, final String hostName, final long time, final String incidentId,
            final Map contextualParameters) {
        super(message, cause, errorCode, paticipantId, hostName, time, incidentId, contextualParameters);
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
    public InternalInfrastructureException(final Throwable cause) {
        super(cause);
    }

}
