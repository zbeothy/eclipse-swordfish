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

/**
 * An interface that describes an <code>InternalAuthenticationHandler</code>, which is accessed
 * by InternalSBB when it requires authentication information.
 * <p>
 * Authentication handler implementations obey the following rules:
 * <ul>
 * <li>Each handler implements exactly one concrete sub-interface of the present interface.</li>
 * <li>A handler has to be registered by the application for each authentication method it deals
 * with using the
 * {@link org.eclipse.swordfish.papi.InternalSBB#addAuthenticationHandler(InternalAuthenticationHandler)}
 * method.</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.swordfish.papi.internal.authentication.InternalX509CertificateAuthenticationHandler
 * @see org.eclipse.swordfish.papi.internal.authentication.InternalUserPasswordAuthenticationHandler
 * 
 */
public interface InternalAuthenticationHandler {

}
