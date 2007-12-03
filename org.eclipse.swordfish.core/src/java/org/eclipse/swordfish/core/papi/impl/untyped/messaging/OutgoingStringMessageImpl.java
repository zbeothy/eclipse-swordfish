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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.w3c.dom.Document;

/**
 * The Class OutgoingStringMessageImpl.
 * 
 */
public class OutgoingStringMessageImpl extends OutgoingMessageBase {

    /** The Constant directivePattern. */
    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile("<\\?[Xx][Mm][Ll](.*)\\?>");

    /** The Constant encPattern. */
    private static final Pattern ENC_PATTERN = Pattern.compile("[Ee][Nn][Cc][Oo][Dd][Ii][Nn][Gg]='(.*)'");

    /** the XMl content of this payload;. */
    private String content;

    /**
     * Constructor for this class. Instances may only be created from within this package.
     * 
     * @param content
     *        content
     */
    public OutgoingStringMessageImpl(final String content) {
        super();
        this.content = content;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the source
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.payload.AbstractOutgoingPayload#createContentSource()
     */
    @Override
    protected Source createContentSource() throws InternalMessagingException {
        Document dom = null;
        try {
            if (this.content == null) {
                dom = XMLUtil.newDocument();
                return new DOMSource(dom);
            } else {
                this.content.trim();
                ByteArrayInputStream bais = new ByteArrayInputStream(this.content.getBytes(this.getEncoding()));
                return new StreamSource(bais);
            }
        } catch (Exception e) {
            throw new InternalMessagingException(e);
        }
    }

    /**
     * Gets the encoding.
     * 
     * @return the encoding
     */
    private String getEncoding() {

        String encoding = "UTF-8";
        String group;
        if (this.content != null) {
            Matcher directiveMatcher = DIRECTIVE_PATTERN.matcher(this.content);
            if (directiveMatcher.find()) {
                group = directiveMatcher.group();
                Matcher encMatcher = ENC_PATTERN.matcher(group);
                String enc = "";
                if (encMatcher.find()) {
                    enc = encMatcher.group(1).trim();
                }
                if (enc.length() > 0) {
                    encoding = enc;
                }
            }
        }
        return encoding;
    }

}
