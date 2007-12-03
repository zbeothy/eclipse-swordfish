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
package org.eclipse.swordfish.core.interceptor.compression.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.interceptor.compression.CompressionProcessor;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * Implementation of the compression interceptor.
 * 
 */
public class CompressionProcessorBean extends AbstractProcessingComponent implements CompressionProcessor {

    /** logger for this class. */
    private static Log log = SBBLogFactory.getLog(CompressionProcessorBean.class);

    /**
     * the DocumentFragment for the SOAP header. Needs to created only once, so it's kept as a
     * static member
     */
    private static DocumentFragment compressionHeader;

    /**
     * builds the compression SOAP header.
     * 
     * @return DocumentFragment for the header
     * 
     * @throws InternalSBBException
     *         If the document fragment cannot be created
     */
    private static DocumentFragment getCompressionHeader() throws InternalSBBException {
        if (compressionHeader == null) {
            try {
                String compressionHeaderStr =
                        "<" + Constants.HEADER_QNAME + " xmlns:sbb=\"" + HeaderUtil.SBB_NS + "\" xmlns:soap=\""
                                + HeaderUtil.SOAP_NS + "\" soap:mustUnderstand=\"1\"/>";
                Document header = TransformerUtil.docFromString(compressionHeaderStr);
                compressionHeader = header.createDocumentFragment();
                compressionHeader.appendChild(header.getDocumentElement());
            } catch (Exception e) {
                throw new InternalMessagingException("Cannot create header document fragment", e);
            }
        }
        return compressionHeader;
    }

    /** the object doing the real work. */
    private CompressionSupport compressionSupport;

    /**
     * (non-Javadoc).
     * 
     * @param assertions
     *        the assertions
     * 
     * @return true, if can handle
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#canHandle(org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public boolean canHandle(final Collection/* <Assertion> */assertions) throws InternalSBBException {
        return true;
    }

    /**
     * Gets the content action.
     * 
     * @return the content action
     * 
     * @see org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent#getContentAction()
     */
    @Override
    public ContentAction getContentAction() {
        return ContentAction.READWRITE;
    }

    /**
     * FIXME enhance contentHandling.
     * 
     * @return the supported sources
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#getSupportedSources()
     */
    @Override
    public Class[] getSupportedSources() {
        return new Class[] {DOMSource.class};
    }

    /**
     * (non-Javadoc).
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleFault(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        // nothing to do here
    }

    /**
     * depending on the role, decides whether to compress or uncompress the message.
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, PolicyViolatedException {

        PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.REQUEST);
        if (null != assertion) {
            try {
                NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
                if (role.equals(Role.SENDER)) {
                    log.info("compression: request/sender");
                    this.performCompression(nmMessage);

                } else if (role.equals(Role.RECEIVER)) {
                    log.info("decompression: request/receiver");
                    this.performDecompression(nmMessage);
                }
            } catch (PolicyViolatedException pve) {
                throw pve;
            } catch (InternalSBBException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalInfrastructureException(e);
            }
        }
    }

    /**
     * depending on the role, decides whether to compress or uncompress the message.
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, PolicyViolatedException {
        PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.RESPONSE);
        if (null != assertion) {
            try {
                NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
                if (role.equals(Role.SENDER)) {
                    log.info("decompression: response/sender");
                    this.performDecompression(nmMessage);

                } else if (role.equals(Role.RECEIVER)) {
                    log.info("compression: response/receiver");
                    this.performCompression(nmMessage);
                }
            } catch (PolicyViolatedException pve) {
                throw pve;
            } catch (InternalSBBException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalInfrastructureException(e);
            } finally {
                System.out.print("");
                // throw new InternalSBBException(e);
            }
        }
    }

    /**
     * init method called by the Spring wiring.
     */
    public void init() {
        this.compressionSupport = new CompressionSupport();
    }

    /**
     * Perform compression.
     * 
     * @param nmMessage
     *        the nm message
     * 
     * @throws InternalSBBException
     */
    private void performCompression(final NormalizedMessage nmMessage) throws InternalSBBException {
        long beforeTime = System.currentTimeMillis();
        Source src = nmMessage.getContent();
        if (TransformerUtil.isSourceEmpty(src)) {
            log.warn("No data to process for compression.");
            return;
        }
        try {
            nmMessage.setContent(this.compressionSupport.asCompressedSource(src));
        } catch (MessagingException e) {
            throw new InternalMessagingException("Cannot set message content", e);
        }
        Map headers = (Map) nmMessage.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (null == headers) {
            headers = new HashMap();
        }
        headers.put(Constants.QNAME_STRING, getCompressionHeader());
        nmMessage.setProperty(HeaderUtil.HEADER_PROPERTY, headers);
        log.info("Compression took " + (System.currentTimeMillis() - beforeTime) + " ms.");
    }

    /**
     * Perform decompression.
     * 
     * @param nmMessage
     *        the nm message
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     */
    private void performDecompression(final NormalizedMessage nmMessage) throws InternalSBBException, PolicyViolatedException {
        long beforeTime = System.currentTimeMillis();
        Source src = nmMessage.getContent();
        if (TransformerUtil.isSourceEmpty(src)) {
            log.warn("No data to process for decompression.");
            return;
        }
        try {
            nmMessage.setContent(this.compressionSupport.asUncompressedSource(src));
        } catch (PolicyViolatedException pve) {
            throw pve;
        } catch (MessagingException e) {
            throw new InternalMessagingException("Cannot set message content", e);
        }
        log.info("Decompression took " + (System.currentTimeMillis() - beforeTime) + " ms.");
    }

}
