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

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;

/**
 * The Class MessageSigner.
 */
public class MessageSigner {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(MessageSigner.class);

    /** processor reference. */
    private SigningProcessorBean processor = null;

    /**
     * The Constructor.
     * 
     * @param processorBean
     *        processor
     */
    public MessageSigner(final SigningProcessorBean processorBean) {
        this.processor = processorBean;
    }

    /**
     * Sign.
     * 
     * @param nm
     *        normalized message
     * 
     * @throws InternalSBBException
     */
    public void sign(final NormalizedMessage nm) throws InternalSBBException {
        Source source = nm.getContent();

        try {
            if (TransformerUtil.isSourceEmpty(source)) {
                LOG.warn("No data to process for signing.");
                return;
            }

            Document document = TransformerUtil.docFromSource(source);

            LOG.debug("Message we are signing");
            LOG.debug(TransformerUtil.stringFromDomNode(document));

            XMLSignature xmlSignature =
                    new XMLSignature(document, null, this.processor.getSignatureMethod(),
                            Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

            // add transformation and canonicalization
            Transforms transforms = new Transforms(xmlSignature.getDocument());
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            xmlSignature.addDocument("", transforms);

            // set the certificate
            X509Data x509data = new X509Data(document);
            Certificate[] certArray = this.processor.getCertificateChain();
            if ((certArray == null) || (certArray.length == 0)) throw new Exception("Could not find certificate chain.");
            Collection collection = Arrays.asList(certArray);
            if (collection != null) {
                int i = 0;
                for (Iterator iterator = collection.iterator(); iterator.hasNext(); i++) {
                    Object obj = iterator.next();
                    if (!(obj instanceof X509Certificate)) {
                        continue;
                    }
                    if (!iterator.hasNext() && (i > 0)
                            && ((X509Certificate) obj).getSubjectDN().equals(((X509Certificate) obj).getIssuerDN())) {
                        break;
                    }
                    x509data.addCertificate((X509Certificate) obj);
                }
            }
            if (x509data.lengthCertificate() > 0) {
                KeyInfo keyinfo = new KeyInfo(document);
                keyinfo.add(x509data);
                xmlSignature.getElement().appendChild(keyinfo.getElement());
            }

            LOG.debug("Generated Signature is: " + TransformerUtil.stringFromDomNode(xmlSignature.getElement()));

            xmlSignature.sign(this.processor.getKeyForSigning());
            this.processor.getExchangeHelper().setSignatureInExchange(nm, xmlSignature.getElement());
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

}
