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

import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import org.apache.xml.security.signature.XMLSignature;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class MessageVerifier.
 */
public class MessageVerifier {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(MessageVerifier.class);

    /** processor reference. */
    private SigningProcessorBean processor = null;

    /**
     * The Constructor.
     * 
     * @param processorBean
     *        processor
     */
    public MessageVerifier(final SigningProcessorBean processorBean) {
        this.processor = processorBean;
    }

    /**
     * Verify.
     * 
     * @param nm
     *        normalized message
     * 
     * @throws InternalSBBException
     *         exception
     * @throws PolicyViolatedException
     */
    public void verify(final NormalizedMessage nm) throws InternalSBBException, PolicyViolatedException {
        Source source = nm.getContent();

        if (TransformerUtil.isSourceEmpty(source)) {
            LOG.warn("No data to process for verifying of message signature.");
            return;
        }

        // TODO work around, should be removed
        // currently the message delivered from the BC contains two extra
        // namespaces
        // for xsi and soap, they should be removed prior to verification.
        // The message document also contains new line characters and white
        // spaces,
        // they should also be removed.
        /*
         * Node srcNode = ((DOMSource) source).getNode().getFirstChild(); NodeList srcList =
         * srcNode.getChildNodes();
         * 
         * for (int y = 0; y < srcList.getLength(); y++) { if (srcList.item(y).getNodeValue() !=
         * null && srcList.item(y).getNodeType() == 3) { srcList.item(y).setNodeValue(
         * srcList.item(y).getNodeValue().replaceAll("\n", "")); srcList.item(y)
         * .setNodeValue(srcList.item(y).getNodeValue().replaceAll(" ", "")); } } Document document =
         * TransformerUtil.docFromSource(source); Element docElement =
         * document.getDocumentElement(); docElement.removeAttribute("xmlns:xsi");
         * docElement.removeAttribute("xmlns:soap");
         */
        // TODO work around over....
        try {
            Document document = TransformerUtil.docFromSource(source);
            LOG.debug("Message we received:");
            LOG.debug(TransformerUtil.stringFromDomNode(document));

            Node signatureNode = this.processor.getExchangeHelper().getSignatureFromExchange(nm);
            if (signatureNode == null) throw new PolicyViolatedException("Signature information not found in the message header.");
            Element importedNode = (Element) document.importNode(signatureNode, true);
            XMLSignature xmlSignature = new XMLSignature(importedNode, null);
            document.getFirstChild().appendChild(xmlSignature.getElement());
            boolean verification = xmlSignature.checkSignatureValue(this.processor.getKeyForVerifying(xmlSignature));
            if (!verification) {
                LOG.info("Verification of the Signature failed.");
                throw new SecurityException("Verification of the Signature failed.");
            }
        } catch (InternalSBBException se) {
            throw se;
        } catch (Exception e) {
            throw new SecurityException(e);
        }

    }

}
