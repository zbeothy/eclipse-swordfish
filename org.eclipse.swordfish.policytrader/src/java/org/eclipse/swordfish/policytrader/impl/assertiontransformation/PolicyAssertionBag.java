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
 * The Class PolicyAssertionBag.
 * 
 */
public class PolicyAssertionBag extends AbstractAssertionBag {

    /** Name to be used as tag name;. */
    private final QKey assertionName;

    /**
     * Instantiates a new policy assertion bag.
     * 
     * @param assertionName
     *        the assertion name
     */
    public PolicyAssertionBag(final QKey assertionName) {
        super();
        this.assertionName = assertionName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.AbstractAssertionBag#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
        writeStartElement(this.assertionName, writer);
        this.writeContent(writer);
        writer.writeEndElement();
    }
}
