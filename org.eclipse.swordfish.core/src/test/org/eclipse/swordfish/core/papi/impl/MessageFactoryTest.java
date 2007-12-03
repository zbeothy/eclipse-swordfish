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
package org.eclipse.swordfish.core.papi.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageFactoryImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalMessageFactory;
import org.w3c.dom.Document;

/**
 * The Class MessageFactoryTest.
 */
public class MessageFactoryTest extends TestCase {

    /** The a text message. */
    String aTextMessage;

    /** The a stream message. */
    InputStream aStreamMessage;

    /** The a dom message. */
    Document aDomMessage;

    /**
     * Test deprecated attachment API.
     */
    public void testDeprecatedAttachmentAPI() {
        try {
            InternalMessageFactory fact = new MessageFactoryImpl();
            OutgoingMessageBase src = (OutgoingMessageBase) fact.createMessage(this.aTextMessage);
            InputStream attachment = new ByteArrayInputStream("123".getBytes());
            src.attach(attachment);
            src.attach(attachment);
        } catch (InternalSBBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test new attachment API.
     */
    public void testNewAttachmentAPI() {

    }

    // /**
    // * Test message factory products.
    // */
    // public void testMessageFactoryProducts() {
    // InternalMessageFactory fact = new MessageFactoryImpl();
    // OutgoingMessageBase src = null;
    //
    // try {
    // NormalizedMessage msg = new NormalizedMessageImpl();
    // CallContextImpl ctx = new CallContextImpl();
    //
    // src = (OutgoingMessageBase) fact.createMessage(aTextMessage);
    // src.fillMessage(msg, ctx);
    //
    // src = (OutgoingMessageBase) fact.createMessage(aStreamMessage);
    // src.fillMessage(msg, ctx);
    //
    // src = (OutgoingMessageBase) fact.createMessage(aDomMessage);
    // src.fillMessage(msg, ctx);
    //
    // } catch (InternalSBBException e) {
    // e.printStackTrace();
    // }
    // }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.aTextMessage =
                "<?xml version='1.0' encoding='ISO-8859-1'?>"
                        + "<ns:createLending xmlns:ns='http://www.sopware.org/demos/Library/1.0/common'>"
                        + "<ns:ISBNNumber>123456789012345</ns:ISBNNumber>" + "<ns:DateOfBirth>1963-10-09</ns:DateOfBirth>"
                        + "<ns:ZIP>10781</ns:ZIP>" + "<ns:Borrowed>2002-10-09</ns:Borrowed>" + "</ns:createLending>";

        this.aStreamMessage = new ByteArrayInputStream(this.aTextMessage.getBytes());
        this.aDomMessage = XMLUtil.docFromString(this.aTextMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
