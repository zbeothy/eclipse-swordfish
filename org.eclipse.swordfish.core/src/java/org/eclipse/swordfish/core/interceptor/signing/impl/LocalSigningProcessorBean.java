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
package org.eclipse.swordfish.core.interceptor.signing.impl;

import java.security.Key;
import org.apache.xml.security.signature.XMLSignature;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InternalSecurityException;

/**
 * The Class LocalSigningProcessorBean.
 * 
 */
public class LocalSigningProcessorBean extends SigningProcessorBean {

    /**
     * Gets the key for verifying.
     * 
     * @param xmlSignature
     *        signature
     * 
     * @return Key public key for verifying
     * 
     * @throws InternalSBBException
     * @throws InternalSecurityException
     */
    @Override
    public Key getKeyForVerifying(final XMLSignature xmlSignature) throws InternalSBBException, SecurityException {
        try {
            Key key = this.getKeyStore().getCertificate(this.getCertificateAlias()).getPublicKey();
            return key;
        } catch (Exception e) {
            throw new InternalConfigurationException(e);
        }
    }

}
