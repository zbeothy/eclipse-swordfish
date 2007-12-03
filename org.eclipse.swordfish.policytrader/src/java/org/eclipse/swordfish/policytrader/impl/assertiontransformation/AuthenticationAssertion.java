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
package org.eclipse.swordfish.policytrader.impl.assertiontransformation;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * The Class AuthenticationAssertion.
 */
public class AuthenticationAssertion extends Writeable implements ClassicAssertion {

    /** The type. */
    private String type;

    /**
     * Instantiates a new authentication assertion.
     */
    public AuthenticationAssertion() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicAssertion#getAssertionName()
     */
    public QKey getAssertionName() {
        return AssertionTransformer.CLASSIC_AUTHENTICATION;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the type.
     * 
     * @param type
     *        the new type
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.Writeable#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
        writeStartElement(AssertionTransformer.CLASSIC_AUTHENTICATION, writer);
        if (null != this.type) {
            writer.writeAttribute(AuthenticationAssertionTransformer.TYPE_ATTRIB, this.type);
        }
        writer.writeEndElement();
    }

}
