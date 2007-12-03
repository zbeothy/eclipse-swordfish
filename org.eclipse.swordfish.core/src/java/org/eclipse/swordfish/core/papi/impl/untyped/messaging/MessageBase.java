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
package org.eclipse.swordfish.core.papi.impl.untyped.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.swordfish.core.components.headerprocessing.impl.Constants;
import org.eclipse.swordfish.core.papi.impl.untyped.SOPObjectBase;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.w3c.dom.Document;

/**
 * Base and utility class for message payload handling.
 */
public abstract class MessageBase extends SOPObjectBase {

    /** Size of temporary buffer used when reading from or writing to Streams. */
    protected static final int COPY_BUFFER_SIZE = 2048;

    /**
     * To DOM.
     * 
     * @param aStream
     *        the stream
     * 
     * @return the content as document
     * 
     * @throws InternalMessagingException
     */
    protected static Document toDOM(final InputStream aStream) throws InternalMessagingException {
        return toDOM(new StreamSource(aStream));
    }

    /**
     * To DOM.
     * 
     * @param aReader
     *        the reader to convert
     * 
     * @return an Document of the reader's content
     * 
     * @throws InternalMessagingException
     */
    protected static Document toDOM(final Reader aReader) throws InternalMessagingException {
        return toDOM(new StreamSource(aReader));
    }

    /**
     * To DOM.
     * 
     * @param aXMLString
     *        a string
     * 
     * @return the document representation of this stream
     * 
     * @throws InternalMessagingException
     */
    protected static Document toDOM(final String aXMLString) throws InternalMessagingException {
        return toDOM(new StreamSource(new StringReader(aXMLString)));
    }

    /**
     * To input stream.
     * 
     * @param aDoc
     *        the a doc
     * 
     * @return an inputstream representing the source
     * 
     * @throws InternalMessagingException
     */
    protected static InputStream toInputStream(final Document aDoc) throws InternalMessagingException {
        return XMLUtil.inputStreamFromDom(aDoc);
        /*
         * ByteArrayOutputStream theStream = new ByteArrayOutputStream(); try { OutputStreamWriter
         * writer = new OutputStreamWriter(theStream, "UTF-8"); DOM2Writer.serializeAsXML(aNode,
         * writer, true); } catch (Exception e) { throw new PayloadAccessException("Conversion from
         * DOM to InputStream failed", e); } return new
         * ByteArrayInputStream(theStream.toByteArray());
         */
    }

    /**
     * To input stream.
     * 
     * @param aReader
     *        the reader to convert
     * 
     * @return an inputstream of the reader
     * 
     * @throws InternalMessagingException
     */
    protected static InputStream toInputStream(final Reader aReader) throws InternalMessagingException {
        // TODO optimize for real streaming
        ByteArrayOutputStream theStream = new ByteArrayOutputStream();
        // TODO Fix used encoding, encoding may be named in XML
        // so we must use this encoding
        try {
            final OutputStreamWriter theWriter = new OutputStreamWriter(theStream, "UTF-8");
            transferData(aReader, theWriter);
            theWriter.close();
        } catch (UnsupportedEncodingException e) {
            throw new InternalMessagingException("Conversion failed due to encoding problems", e);
        } catch (IOException e) {
            throw new InternalMessagingException("Conversion failed due to encoding problems", e);
        }
        return new ByteArrayInputStream(theStream.toByteArray());
    }

    /**
     * To input stream.
     * 
     * @param aXMLString
     *        a string
     * 
     * @return the inpitsream representation of this stream
     * 
     * @throws InternalMessagingException
     */
    protected static InputStream toInputStream(final String aXMLString) throws InternalMessagingException {
        return new ByteArrayInputStream(aXMLString.getBytes());
    }

    /**
     * To string.
     * 
     * @param aDocument
     *        the a document
     * 
     * @return a string representation of the dom
     * 
     * @throws InternalMessagingException
     */
    protected static String toString(final Document aDocument) throws InternalMessagingException {
        try {
            if (XMLUtil.isEmpty(aDocument))
                return "";
            else
                return XMLUtil.stringFromDom(aDocument);
        } catch (Exception e) {
            throw new InternalMessagingException(e);
        }
    }

    /**
     * To string.
     * 
     * @param aStream
     *        the stream
     * 
     * @return the content as string
     * 
     * @throws InternalMessagingException
     */
    protected static String toString(final InputStream aStream) throws InternalMessagingException {
        StringWriter theWriter = new StringWriter();
        final InputStreamReader theReader;
        // TODO Fix used encoding, encoding may be named in XML
        // so we must use this encoding
        try {
            theReader = new InputStreamReader(aStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalMessagingException("Conversion failed due to encoding problems", e);
        }
        transferData(theReader, theWriter);
        return theWriter.toString();
    }

    /**
     * To string.
     * 
     * @param aReader
     *        areader
     * 
     * @return the string representation of the reader's content
     * 
     * @throws InternalMessagingException
     */
    protected static String toString(final Reader aReader) throws InternalMessagingException {
        final StringWriter theWriter = new StringWriter();
        transferData(aReader, theWriter);
        return theWriter.toString();
    }

    /**
     * To DOM.
     * 
     * @param source
     *        a StreamSource as Transformer input
     * 
     * @return the content as document
     * 
     * @throws PayloadAccessException
     *         PayloadAccessException
     */
    private static Document toDOM(final StreamSource source) throws InternalMessagingException {
        return TransformerUtil.docFromSource(source);
    }

    /**
     * Transfer data.
     * 
     * @param reader
     *        the input stream
     * @param writer
     *        the output stream
     * 
     * @throws PayloadAccessException
     *         on data transfer problems
     */
    private static void transferData(final Reader reader, final Writer writer) throws InternalMessagingException {
        try {
            final char[] theBuffer = new char[COPY_BUFFER_SIZE];
            for (int len = reader.read(theBuffer); len > 0; len = reader.read(theBuffer)) {
                writer.write(theBuffer, 0, len);
            }
        } catch (IOException e) {
            throw new InternalMessagingException("Data transfer from Reader to Writer failed", e);
        }
    }

    /** Map that contains the message headers which should be made available to the participant. */
    private Map headers = null;

    /**
     * protected standard constructor.
     */
    protected MessageBase() {
        super();
    }

    /**
     * Gets the header map.
     * 
     * @return Returns a map of headers.
     */
    public Map getHeaderMap() {
        if (this.headers == null) {
            this.headers = new HashMap();
        }
        return this.headers;
    }

    public Map getHeaders() {
        return this.headers;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.SOPObjectBase#getParticipantIdentityAsString()
     */
    @Override
    public String getParticipantIdentityAsString() {
        return null;
    }

    public void setHeaders(final Map headers) {
        this.headers = headers;
    }

    /**
     * Checks if is in protected namespace.
     * 
     * @param name
     *        the name
     * 
     * @return true, if is in protected namespace
     */
    protected boolean isInProtectedNamespace(final String name) {
        return (name.startsWith(AgreedPolicy.AGREED_POLICY_NAMESPACE, 1) || name.startsWith(HeaderUtil.SBB_NS, 1)
                || name.startsWith(Constants.WSA_NS, 1) || name.startsWith(HeaderUtil.WSSECURITY_NS, 1));
    }
}
