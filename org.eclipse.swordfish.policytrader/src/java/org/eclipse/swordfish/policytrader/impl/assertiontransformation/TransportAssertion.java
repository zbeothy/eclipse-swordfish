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
 * The Class TransportAssertion.
 */
public class TransportAssertion extends Writeable implements ClassicAssertion {

    /** The type. */
    private final String type;

    /**
     * Instantiates a new transport assertion.
     * 
     * @param type
     *        the type
     */
    public TransportAssertion(final String type) {
        super();
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicAssertion#getAssertionName()
     */
    public QKey getAssertionName() {
        return AssertionTransformer.CLASSIC_TRANSPORT;
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
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.Writeable#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
        writeStartElement(AssertionTransformer.CLASSIC_TRANSPORT, writer);
        writer.writeAttribute(AssertionTransformer.CLASSIC_ASSERTION_TYPE_ATTRIB, this.type);
        writer.writeEndElement();
    }
}
