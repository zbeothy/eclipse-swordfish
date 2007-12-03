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
package org.eclipse.swordfish.core.interceptor.validation.impl;

import java.util.List;
import org.eclipse.swordfish.core.interceptor.validation.SchemaValidator;
import org.xml.sax.InputSource;

/**
 * XML validator against schemas based on Xerces This is a replacement for the former validator
 * implementaion which was based on a commercial product.
 * 
 * TODO needs to be implemented
 */
public class XercesSchemaValidator implements SchemaValidator {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.interceptor.validation.SchemaValidator#createSchema(java.util.List)
     */
    public Object createSchema(final List schemaList) throws Exception {
        // TODO TO BE IMPLEMENTED
        return "DUMMY";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.interceptor.validation.SchemaValidator#validate(java.lang.Object,
     *      org.xml.sax.InputSource)
     */
    public void validate(final Object schemas, final InputSource message) throws Exception {
        // TODO TO BE IMPLEMENTED

    }

}
