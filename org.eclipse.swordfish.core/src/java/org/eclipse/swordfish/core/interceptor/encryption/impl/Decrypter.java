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

import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.EncryptionConstants;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Class Decrypter.
 */
public class Decrypter {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(Decrypter.class);

    /** processor bean. */
    private EncryptionProcessorBean processorBean = null;

    /**
     * The Constructor.
     * 
     * @param processorBean
     *        processor bean
     */
    public Decrypter(final EncryptionProcessorBean processorBean) {
        this.processorBean = processorBean;

    }

    /**
     * Decrypt.
     * 
     * @param nm
     *        normalized message
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     */
    public void decrypt(final NormalizedMessage nm) throws InternalSBBException, PolicyViolatedException {

        try {
            Source source = nm.getContent();

            if (TransformerUtil.isSourceEmpty(source)) {
                LOG.warn("No data to process for decryption.");
                return;
            }

            Document document = TransformerUtil.docFromSource(source);

            // create an xml cipher to decrypt
            XMLCipher xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.DECRYPT_MODE, null);

            // get the encrypted key element from header
            Node encKeyNode = this.processorBean.getExchangeHelper().getEncryptionFromExchange(nm);
            if (encKeyNode == null) throw new PolicyViolatedException("Encryption information not found in the message header.");
            KeyInfo keyInfo = new KeyInfo(document);
            Element keyInfoElement = keyInfo.getElement();
            Node importedencKeyNode = document.importNode(encKeyNode, true);
            keyInfoElement.appendChild(importedencKeyNode);

            LOG.debug("The message received is ");
            LOG.debug(TransformerUtil.stringFromDomNode(document));

            // look for the cipher data in the message
            NodeList cipherDataList =
                    document.getElementsByTagNameNS(EncryptionConstants.EncryptionSpecNS, EncryptionConstants._TAG_CIPHERDATA);

            if ((cipherDataList == null) || (cipherDataList.getLength() == 0))
                throw new PolicyViolatedException("Cipher Data not found in the message.");

            // insert the keyinfo before the cipherdata element
            document.getFirstChild().insertBefore(keyInfoElement, cipherDataList.item(0));

            // set the key for decrypting the symmetric key
            xmlCipher.setKEK(this.processorBean.getKeyForDecrypting());

            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                xmlCipher.doFinal(document, document);
            } catch (Exception e) {
                throw new SecurityException(e);
            } finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }

            // set the decrypted content to the normalized message.
            nm.setContent(new DOMSource(document));
        } catch (InternalSBBException se) {
            throw se;
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }
}
