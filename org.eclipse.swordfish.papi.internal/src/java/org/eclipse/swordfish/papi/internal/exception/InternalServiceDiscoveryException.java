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
 * This Exception is thrown if no suitable service provider can be resolved from the input given by
 * the participant. At provider registration, it is thrown on mismatch between requested identifiers
 * and service registry data or failure of default policy trading.
 * <ul>
 * <li>no service of the name given has been registered in the service registry</li>
 * <li>no service provider for the requested service has been registered in the service registry</li>
 * <li>policy trading does not find any provider with matching policy</li>
 * <li>on provider registration no provider policy matches with the default consumer policy</li>
 * <li>on provider registration no SPDX matching the provider name is available</li>
 * <li>on provider registration an SPDX matching the provider name is available but it refers to
 * another service (port type)</li>
 * </ul>
 * not thrown
 * <ul>
 * <li>syntactic errors in service or policy names -&gt; throw
 * {@link InternalIllegalInputException}</li>
 * <li>no mapping of policy name provided by participant -&gt; throw
 * {@link InternalConfigurationException}</li>
 * <li>service registry not available -%gt; throw {@link InternalInfrastructureException}</li>
 * <li>service discovery problems of TSPs invoked by internal proxies -&gt; throw
 * {@link InternalInfrastructureException}</li>
 * <li>the policy short name given by the participant maps to a policy identifier for which no
 * policy has been registered in the service registry -&gt; throw
 * {@link InternalConfigurationException}</li>
 * <li>service discovery succeeds but provider is unavailable (down or not reachable) -&gt; throw
 * {@link InternalInfrastructureException}</li>
 * <li>failure of service discovery from service descriptions stored locally is logged but does not
 * produce an Exception at the PAPI. Instead, the service registry is invoked transparently. If
 * service registry invocation fails, or service discovery fails with the service descriptions
 * stored in the service registry, the appropriated Exception ({@link InternalInfrastructureException}
 * or {@link InternalServiceDiscoveryException}) is thrown</li>
 * </ul>
 */
public class InternalServiceDiscoveryException extends InternalInfrastructureException {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID =
            "@(#) $Id: InternalServiceDiscoveryException.java,v 1.1.2.3 2007/11/09 17:47:15 kkiehne Exp $";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3934606908857869738L;

    // ----------------------------------------------------------- Constructors

    /**
     * The Constructor.
     */
    public InternalServiceDiscoveryException() {
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
    public InternalServiceDiscoveryException(final String message) {
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
    public InternalServiceDiscoveryException(final String message, final Throwable cause) {
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
    public InternalServiceDiscoveryException(final String message, final Throwable cause, final int errorCode,
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
    public InternalServiceDiscoveryException(final Throwable cause) {
        super(cause);
    }
}
