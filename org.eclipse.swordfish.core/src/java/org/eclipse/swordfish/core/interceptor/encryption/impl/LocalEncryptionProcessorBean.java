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
package org.eclipse.swordfish.core.interceptor.encryption.impl;

import java.security.Key;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.iapi.Role;

/**
 * The Class LocalEncryptionProcessorBean.
 */
public class LocalEncryptionProcessorBean extends EncryptionProcessorBean {

    /**
     * Gets the key for encrypting.
     * 
     * @param exchange
     *        exchange
     * @param role
     *        Role
     * @param nm
     *        normalized message
     * 
     * @return private key for signing
     * 
     * @throws Exception
     *         exception
     */
    @Override
    public Key getKeyForEncrypting(final MessageExchange exchange, final Role role, final NormalizedMessage nm) throws Exception {
        Key key = this.getKeyStore().getCertificate(this.getCertificateAlias()).getPublicKey();
        return key;
    }

}
