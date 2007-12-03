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
 * The Class TransformationAssertion.
 */
public class TransformationAssertion extends Writeable implements ClassicAssertion {

    /** The destination. */
    private final String destination;

    /** The rule source path. */
    private final String ruleSourcePath;

    /** The rule id. */
    private final String ruleId;

    /**
     * Instantiates a new transformation assertion.
     * 
     * @param destination
     *        the destination
     * @param ruleSourcePath
     *        the rule source path
     * @param ruleId
     *        the rule id
     */
    public TransformationAssertion(final String destination, final String ruleSourcePath, final String ruleId) {
        super();
        this.destination = destination;
        this.ruleSourcePath = ruleSourcePath;
        this.ruleId = ruleId;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicAssertion#getAssertionName()
     */
    public QKey getAssertionName() {
        return AssertionTransformer.CLASSIC_TRANSFORMATION;
    }

    /**
     * Gets the destination.
     * 
     * @return the destination
     */
    public String getDestination() {
        return this.destination;
    }

    /**
     * Gets the rule id.
     * 
     * @return the rule id
     */
    public String getRuleId() {
        return this.ruleId;
    }

    /**
     * Gets the rule source path.
     * 
     * @return the rule source path
     */
    public String getRuleSourcePath() {
        return this.ruleSourcePath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.Writeable#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
        writeStartElement(AssertionTransformer.CLASSIC_TRANSFORMATION, writer);
        writer.writeAttribute(TransformationAssertionTransformer.DESTINATION_ATTRIB, this.destination);
        writer.writeAttribute(TransformationAssertionTransformer.RULE_SOURCE_PATH_ATTRIB, this.ruleSourcePath);
        writer.writeAttribute(TransformationAssertionTransformer.RULE_ID_ATTRIB, this.ruleId);
        writer.writeEndElement();
    }
}
