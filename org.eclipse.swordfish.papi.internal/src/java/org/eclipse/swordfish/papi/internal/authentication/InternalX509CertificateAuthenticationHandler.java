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
package org.eclipse.swordfish.papi.internal.authentication;

import java.security.cert.X509Certificate;

/**
 * This interface prompts for a X509 certificate required for authentication of a user in a consumer
 * application.
 * 
 * Participant applications register implementations of this interface with the InternalSBB using
 * the
 * {@link org.eclipse.swordfish.papi.InternalSBB#addAuthenticationHandler(InternalAuthenticationHandler)}
 * method.
 * 
 */
public interface InternalX509CertificateAuthenticationHandler extends InternalAuthenticationHandler {

    /**
     * This method returns the <code>X509CertificateCredentials</code> that identifies the user.
     * 
     * @return The X509 certificate of the user.
     */
    X509Certificate getCredentials();

}
