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
 * This Exception is thrown if participant code provides syntactically incorrect input
 * <ul>
 * <li>payload is no XML</li>
 * <li>payload is not well-formed XML</li>
 * <li>InternalSBB identifier, service name, or policy name contains illegal characters</li>
 * </ul>
 * not thrown
 * <ul>
 * <li><code>null</code> as argument where not permitted -&gt; throw
 * {@link IllegalArgumentException}</li>
 * <li>empty String as argument where not permitted -&gt; throw {@link IllegalArgumentException}</li>
 * <li>service name not known -&gt; throw {@link InternalServiceDiscoveryException} </li>
 * <li>policy name not in policy map -&gt; throw {@link InternalConfigurationException}</li>
 * <li>payload is validated and fails -&gt; throw {@link InternalMessagingException} </li>
 * </ul>
 */
public class InternalIllegalInputException extends InternalSBBException {

    // -------------------------------------------------------------- Constants

    /**
     * (R)evision (C)ontrol (S)ystem (Id)entifier.
     */
    public static final String RCS_ID = "@(#) $Id: InternalIllegalInputException.java,v 1.1.2.3 2007/11/09 17:47:15 kkiehne Exp $";

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 3726878468170010998L;

    // ----------------------------------------------------------- Constructors

    /**
     * The Constructor.
     */
    public InternalIllegalInputException() {
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
    public InternalIllegalInputException(final String message) {
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
    public InternalIllegalInputException(final String message, final Throwable cause) {
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
    public InternalIllegalInputException(final String message, final Throwable cause, final int errorCode,
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
    public InternalIllegalInputException(final Throwable cause) {
        super(cause);
    }

}
