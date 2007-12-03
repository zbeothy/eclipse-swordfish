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
 * The Class ExtensionAssertion.
 */
public class ExtensionAssertion extends Writeable implements ClassicAssertion {

    /** The name. */
    private final String name;

    /** The value. */
    private final String value;

    /**
     * Instantiates a new extension assertion.
     * 
     * @param name
     *        the name
     * @param value
     *        the value
     */
    public ExtensionAssertion(final String name, final String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicAssertion#getAssertionName()
     */
    public QKey getAssertionName() {
        return AssertionTransformer.CLASSIC_EXTENSION;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
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
        writeStartElement(AssertionTransformer.CLASSIC_EXTENSION, writer);
        writer.writeAttribute(ExtensionAssertionTransformer.NAME_ATTRIB, this.name);
        writer.writeAttribute(ExtensionAssertionTransformer.VALUE_ATTRIB, this.value);
        writer.writeEndElement();
    }
}
