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

import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.w3c.dom.Document;

/**
 * InputStream-based outgoing message.
 */
public class OutgoingStreamMessageImpl extends OutgoingMessageBase {

    /** the XMl content of this payload;. */
    private InputStream content;

    /**
     * Constructor for this class. Instances may only be created from within this package.
     * 
     * @param content
     *        content
     */
    public OutgoingStreamMessageImpl(final InputStream content) {
        super();
        this.content = content;
    }

    /**
     * Creates the content source.
     * 
     * @return the source
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.payload.AbstractOutgoingPayload#createContentSource()
     *      The current implementation is a workaround! Currently, the binding component does only
     *      support DOMSource.
     */
    @Override
    protected Source createContentSource() throws InternalMessagingException {
        Document dom = null;
        try {
            if (this.content == null) {
                dom = XMLUtil.newDocument();
                return new DOMSource(dom);
            } else
                return new StreamSource(this.content);
        } catch (Exception e) {
            throw new InternalMessagingException(e);
        }
    }
}
