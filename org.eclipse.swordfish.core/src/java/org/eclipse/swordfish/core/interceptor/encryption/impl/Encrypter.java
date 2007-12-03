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
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.EncryptionConstants;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class Encrypter.
 */
public class Encrypter {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(Encrypter.class);

    /** encryption processir bean. */
    private EncryptionProcessorBean processorBean = null;

    /**
     * The Constructor.
     * 
     * @param processorBean
     *        processor bean
     */
    public Encrypter(final EncryptionProcessorBean processorBean) {
        this.processorBean = processorBean;
    }

    /**
     * Encrypt.
     * 
     * @param nm
     *        normalized message
     * @param role
     *        Role
     * @param exchange
     *        message exchange
     * 
     * @throws InternalSBBException
     */
    public void encrypt(final NormalizedMessage nm, final Role role, final MessageExchange exchange) throws InternalSBBException {

        try {
            Source source = nm.getContent();

            if (TransformerUtil.isSourceEmpty(source)) {
                LOG.warn("No data to process for encryption.");
                return;
            }

            Document document = TransformerUtil.docFromSource(source);

            // get public key of the receiver for encryption
            Key publicKey = this.processorBean.getKeyForEncrypting(exchange, role, nm);

            // generate a symmetric key
            Key symmetricKey = this.generateSymmetricKey();

            // encrypt the symmetric key
            EncryptedKey encryptedKey = this.encryptSymmetricKey(document, symmetricKey, publicKey);

            // intialize the xmlCipher
            XMLCipher xmlCipher = XMLCipher.getInstance(this.processorBean.getDataEncryptionAlgorithm());
            xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);

            // prepare the keyinfo and add the encrypted key
            KeyInfo keyInfo = new KeyInfo(document);
            keyInfo.add(encryptedKey);

            // encrypt
            xmlCipher.doFinal(document, document);

            LOG.debug("The encrypted message is ");
            LOG.debug(TransformerUtil.stringFromDomNode(document));

            // get the encrypted key element from dskeyinfo element
            Element keyinfoElement = keyInfo.getElement();
            NodeList encKeyList =
                    keyinfoElement.getElementsByTagNameNS(EncryptionConstants.EncryptionSpecNS,
                            EncryptionConstants._TAG_ENCRYPTEDKEY);

            if ((encKeyList == null) || (encKeyList.getLength() == 0))
                throw new Exception("Error in getting Encrypted Key from DsKeyInfo.");
            Element encKeyElement = (Element) encKeyList.item(0);

            // set the encryption info into the header
            this.processorBean.getExchangeHelper().setEncryptionInExchange(nm, encKeyElement);

            // set the encrypted content to the normalized message.
            nm.setContent(new DOMSource(document));
        } catch (InternalSBBException e) {
            throw e;
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    /**
     * Encrypt symmetric key.
     * 
     * @param documentIn
     *        document
     * @param symmetricKey
     *        symm key
     * @param publicKey
     *        publci key
     * 
     * @return EncryptedKey encrypted key
     * 
     * @throws Exception
     *         exception
     */
    private EncryptedKey encryptSymmetricKey(final Document documentIn, final Key symmetricKey, final Key publicKey)
            throws Exception {

        EncryptedKey encryptedKey = null;
        XMLCipher keyCipher = null;
        keyCipher = XMLCipher.getInstance(this.processorBean.getAlgorithmToEncryptSymmetricKey());
        keyCipher.init(XMLCipher.WRAP_MODE, publicKey);
        encryptedKey = keyCipher.encryptKey(documentIn, symmetricKey);
        return encryptedKey;
    }

    /**
     * generates the symmetric key.
     * 
     * @return the key
     * 
     * @throws Exception
     *         in case of an error in called functions
     */
    private SecretKey generateSymmetricKey() throws Exception {
        KeyGenerator keyGenerator = null;
        keyGenerator = KeyGenerator.getInstance(this.processorBean.getSymmetricKeyAlgorithm());
        keyGenerator.init(this.processorBean.getSymmetricKeySize());
        return keyGenerator.generateKey();
    }

}
