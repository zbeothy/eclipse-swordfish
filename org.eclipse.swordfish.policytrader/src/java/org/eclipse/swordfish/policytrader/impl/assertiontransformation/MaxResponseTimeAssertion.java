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
 * The Class MaxResponseTimeAssertion.
 */
public class MaxResponseTimeAssertion extends Writeable implements ClassicAssertion {

    /** The value. */
    private final String value;

    /** The location. */
    private final String location;

    /** The grace. */
    private final String grace;

    /**
     * Instantiates a new max response time assertion.
     * 
     * @param value
     *        the value
     * @param location
     *        the location
     * @param grace
     *        the grace
     */
    public MaxResponseTimeAssertion(final String value, final String location, final String grace) {
        super();
        this.value = value;
        this.location = location;
        this.grace = grace;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicAssertion#getAssertionName()
     */
    public QKey getAssertionName() {
        return AssertionTransformer.CLASSIC_MAX_RESPONSE_TIME;
    }

    /**
     * Gets the grace.
     * 
     * @return the grace
     */
    public String getGrace() {
        return this.grace;
    }

    /**
     * Gets the location.
     * 
     * @return the location
     */
    public String getLocation() {
        return this.location;
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
        writeStartElement(AssertionTransformer.CLASSIC_MAX_RESPONSE_TIME, writer);
        writer.writeAttribute(MaxResponseTimeAssertionTransformer.VALUE_ATTRIB, this.value);
        writer.writeAttribute(MaxResponseTimeAssertionTransformer.LOCATION_ATTRIB, this.location);
        if (nonzero(this.grace)) {
            writer.writeAttribute(MaxResponseTimeAssertionTransformer.GRACE_ATTRIB, this.grace);
        }
        writer.writeEndElement();
    }
}
