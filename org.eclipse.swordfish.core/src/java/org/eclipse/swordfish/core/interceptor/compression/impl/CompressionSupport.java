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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalFatalException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Performs compression and uncompression of XML Sources Currently, only DOMSources are supported.
 * 
 */
public class CompressionSupport {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(CompressionSupport.class);

    /**
     * creates a compressed Source.
     * 
     * @param src
     *        the Source to compress
     * 
     * @return the compressed Source
     * 
     * @throws InternalSBBException
     *         if the wrong type of source is used or the source cannot be compressed
     */
    public Source asCompressedSource(final Source src) throws InternalSBBException {
        if (src instanceof DOMSource)
            return this.createCompressedDOMSource((DOMSource) src);
        // TODO handle other types of Sources
        /*
         * } else if (src instanceof StreamSource) { StreamSource streamSrc = (StreamSource) src;
         * return new CompressedStreamSource(streamSrc);
         */
        else
            throw new InternalMessagingException("Cannot handle Sources of type " + src.getClass().getName());
    }

    /**
     * creates an uncompressed from a compressed source.
     * 
     * @param src
     *        the Source to uncompress. Must be a DOMSource
     * 
     * @return the uncompressed Source
     * 
     * @throws InternalSBBException
     *         if the wrong type of source is used or the source cannot be decompressed
     * @throws PolicyViolatedException
     */
    public Source asUncompressedSource(final Source src) throws InternalSBBException, PolicyViolatedException {
        if (src instanceof DOMSource)
            return this.createUncompressedDOMSource((DOMSource) src);
        else
            throw new InternalMessagingException("Cannot handle Sources of type " + src.getClass().getName());
    }

    /**
     * creates a compressed Source, specialized for DOMSource.
     * 
     * @param src
     *        the Source to compress
     * 
     * @return the compressed Source
     * 
     * @throws InternalSBBException
     *         if the wrong type of source is used or the source cannot be compressed
     */
    private DOMSource createCompressedDOMSource(final DOMSource src) throws InternalSBBException {
        try {
            DOMSource domSrc = src;
            Node root = domSrc.getNode();
            if (root instanceof Document) {
                root = ((Document) root).getFirstChild();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(new GZIPOutputStream(baos), "UTF-8");
            OutputFormat of = new OutputFormat();
            XMLSerializer serializer = new XMLSerializer(writer, of);
            of.setOmitXMLDeclaration(true);
            serializer.serialize((Element) root);
            writer.close();
            byte[] compressedBytes = baos.toByteArray();
            String encoded = new Base64Support().encode(compressedBytes);
            Document doc = TransformerUtil.getDocumentBuilder().newDocument();
            Element wrapper = doc.createElementNS(Constants.NS_URI, Constants.WRAPPER_QNAME);
            // wrapper.setAttribute("xmlns:" + Constants.PREFIX,
            // Constants.NS_URI);
            wrapper.appendChild(doc.createTextNode(encoded));
            doc.appendChild(wrapper);
            // return new DOMSource(doc.getDocumentElement());
            return new DOMSource(doc);
        } catch (Exception e) {
            throw new InternalInfrastructureException("Exception during compression", e);
        }
    }

    /**
     * creates an uncompressed DOMSource from a compressed DOMSource.
     * 
     * @param src
     *        the Source to uncompress
     * 
     * @return the uncompressed Source
     * 
     * @throws InternalSBBException
     *         if the wrapper element is not found or the body is empty source cannot be
     *         decompressed
     * @throws PolicyViolatedException
     */
    private DOMSource createUncompressedDOMSource(final DOMSource src) throws InternalSBBException, PolicyViolatedException {
        try {
            DOMSource domSrc = src;
            Node root = domSrc.getNode();
            if (root instanceof Document) {
                root = ((Document) root).getFirstChild();
            }
            Element rootElement = (Element) root;
            // DOM sucks
            String qName = rootElement.getNodeName();
            String localName = qName.substring(qName.indexOf(":") + 1, qName.length());
            LOG.info("QName: {" + rootElement.getNamespaceURI() + "}" + localName);
            if (!(Constants.WRAPPER_LOCAL_NAME.equals(localName) && Constants.NS_URI.equals(rootElement.getNamespaceURI())))
                throw new PolicyViolatedException("Required root element not found: local name is '" + localName
                        + "', namespace name is '" + rootElement.getNamespaceURI() + "'");
            Node node = rootElement.getFirstChild();
            if ((node != null) && (node.getNodeType() == Node.TEXT_NODE)) {
                Base64Support b64 = new Base64Support();
                InputStream is = new GZIPInputStream(new ByteArrayInputStream(b64.decode(node.getNodeValue())));
                DocumentBuilder builder = TransformerUtil.getDocumentBuilder();
                Document doc = builder.parse(is);
                return new DOMSource(doc);
                // return new DOMSource(doc.getDocumentElement());
            } else
                throw new PolicyViolatedException("No text to uncompress found.");
        } catch (Exception e) {
            LOG.error("Unexpected exception", e);
            throw new InternalFatalException("Exception during decompression", e);
        }
    }
}
