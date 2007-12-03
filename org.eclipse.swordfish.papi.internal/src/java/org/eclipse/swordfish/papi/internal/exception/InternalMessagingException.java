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
 * This Exceptions is thrown on problems in the messaging path which the participant may be able to
 * correct or work around choosing a different policy setting. This Exception indicates a problem in
 * the context of a single message respectively call which will probably not affect subsequent calls
 * with different or corrected messages.
 * <ul>
 * <li>incompliance of the message with a policy assertion</li>
 * <li>payload is validated and fails</li>
 * </ul>
 * not thrown
 * <ul>
 * <li>unavailability of a specific transport -&gt; throw {@link InternalInfrastructureException}</li>
 * <li>general infrastructure problem which cannot be circumvented by the application but may have
 * been resolved when re-trying later -&gt; throw {@link InternalInfrastructureException}</li>
 * </ul>
 */
public class InternalMessagingException extends InternalSBBException {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: InternalMessagingException.java,v 1.1.2.3 2007/11/09 17:47:15 kkiehne Exp $";

    /** This classes version UID. */
    private static final long serialVersionUID = -2082165149139484374L;

    // ----------------------------------------------------------- Constructors

    /**
     * The Constructor.
     */
    public InternalMessagingException() {
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
    public InternalMessagingException(final String message) {
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
    public InternalMessagingException(final String message, final Throwable cause) {
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
    public InternalMessagingException(final String message, final Throwable cause, final int errorCode,
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
    public InternalMessagingException(final Throwable cause) {
        super(cause);
    }
}
