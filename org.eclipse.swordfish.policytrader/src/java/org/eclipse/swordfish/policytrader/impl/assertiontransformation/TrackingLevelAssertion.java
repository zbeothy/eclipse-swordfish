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
 * The Class TrackingLevelAssertion.
 */
public class TrackingLevelAssertion extends Writeable implements ClassicAssertion {

    /** The value. */
    private final String value;

    /**
     * Instantiates a new tracking level assertion.
     * 
     * @param value
     *        the value
     */
    TrackingLevelAssertion(final String value) {
        super();
        this.value = value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicAssertion#getAssertionName()
     */
    public QKey getAssertionName() {
        return AssertionTransformer.CLASSIC_TRACKING_LEVEL;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.Writeable#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
        writeStartElement(AssertionTransformer.CLASSIC_TRACKING_LEVEL, writer);
        writer.writeAttribute(TrackingLevelAssertionTransformer.VALUE_ATTRIB, this.value);
        writer.writeEndElement();
    }
}
