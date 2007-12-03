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
 * The Class CustomValidationAssertion.
 */
public class CustomValidationAssertion extends Writeable implements ClassicAssertion {

    /** The schema source path. */
    private final String schemaSourcePath;

    /** The schema id. */
    private final String schemaId;

    /**
     * Instantiates a new custom validation assertion.
     * 
     * @param ruleSourcePath
     *        the rule source path
     * @param ruleId
     *        the rule id
     */
    public CustomValidationAssertion(final String ruleSourcePath, final String ruleId) {
        super();
        this.schemaSourcePath = ruleSourcePath;
        this.schemaId = ruleId;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicAssertion#getAssertionName()
     */
    public QKey getAssertionName() {
        return AssertionTransformer.CLASSIC_CUSTOM_VALIDATION;
    }

    /**
     * Gets the schema id.
     * 
     * @return the schema id
     */
    public String getSchemaId() {
        return this.schemaId;
    }

    /**
     * Gets the schema source path.
     * 
     * @return the schema source path
     */
    public String getSchemaSourcePath() {
        return this.schemaSourcePath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.Writeable#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
        writeStartElement(AssertionTransformer.CLASSIC_CUSTOM_VALIDATION, writer);
        writer.writeAttribute(CustomValidationAssertionTransformer.SCHEMA_SOURCE_PATH_ATTRIB, this.schemaSourcePath);
        writer.writeAttribute(CustomValidationAssertionTransformer.SCHEMA_ID_ATTRIB, this.schemaId);
        writer.writeEndElement();
    }
}
