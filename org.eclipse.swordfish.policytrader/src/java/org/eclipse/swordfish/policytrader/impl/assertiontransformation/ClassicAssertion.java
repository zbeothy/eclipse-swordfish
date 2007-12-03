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
 * The Interface ClassicAssertion.
 */
public interface ClassicAssertion {

    /**
     * Gets the assertion name.
     * 
     * @return the assertion name
     */
    QKey getAssertionName();

    /**
     * Write to.
     * 
     * @param writer
     *        the writer
     * 
     * @throws XMLStreamException
     */
    void writeTo(XMLStreamWriter writer) throws XMLStreamException;
}
