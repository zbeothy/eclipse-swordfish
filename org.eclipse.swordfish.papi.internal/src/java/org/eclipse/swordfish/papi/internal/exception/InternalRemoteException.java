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
 * This Exception is thrown at the message sender side due to a problem which occurs at the receiver
 * side and lies in the responsibility of the receiver side, if the problem hinders message delivery
 * to the receiver participant or processing by the participant. The problem must originate from the
 * remote participant or the local conditions of its infrastructure. It will probably affect
 * subsequent calls to the same participant but not to different participants (e.g. other providers
 * of the same service).
 * <p>
 * Problems discovered at the receiver side but originating at the sender side, i.e. rejection of
 * the message received, do not produce a InternalRemoteException at the sender side. Instead, the
 * same Exception has to be thrown which would have been produced if the same problem had been
 * discovered at the sender side.
 * </p>
 * <p>
 * A sender may choose to try a later re-send the message, or in case of a message consumer, to get
 * another provider.
 * </p>
 * <ul>
 * <li>receiver participant fails to process the message and throws an Exception</li>
 * <li>strictly local problem in receiver-side InternalSBB</li>
 * <li>configuration problem at receiver side</li>
 * </ul>
 * not thrown
 * <ul>
 * <li>general problem affecting receiver-side InternalSBB or infrastructure which will also occur
 * at other locations (e.g. TSP unreachable) -&gt; throw {@link InternalInfrastructureException}</li>
 * <li>receiver side policy enforcement component rejects message -&gt; throw
 * {@link InternalMessagingException}</li>
 * <li>payload is validated by receiver and fails -&gt throw {@link InternalMessagingException}</li>
 * <li>authorization is performed by receiver and fails -&gt; throw
 * {@link InternalAuthorizationException}</li>
 * </ul>
 */
public class InternalRemoteException extends InternalSBBException {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: InternalRemoteException.java,v 1.1.2.3 2007/11/09 17:47:15 kkiehne Exp $";

    /** This classes version UID. */
    private static final long serialVersionUID = 394281750182710041L;

    // ----------------------------------------------------------- Constructors

    /**
     * The Constructor.
     */
    public InternalRemoteException() {
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
    public InternalRemoteException(final String message) {
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
    public InternalRemoteException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>
     * The Constructor
     * </p>.
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
    public InternalRemoteException(final String message, final Throwable cause, final int errorCode,
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
    public InternalRemoteException(final Throwable cause) {
        super(cause);
    }
}
