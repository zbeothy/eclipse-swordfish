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
package org.eclipse.swordfish.core.interceptor.validation;

import java.util.List;
import org.xml.sax.InputSource;

/**
 * The Interface SchemaValidator.
 */
public interface SchemaValidator {

    /**
     * Creates the schema.
     * 
     * @param schemaList
     *        schema list
     * 
     * @return Object schema object
     * 
     * @throws Exception
     *         exception
     */
    Object createSchema(List schemaList) throws Exception;

    /**
     * Validate.
     * 
     * @param schemas
     *        the list of schemas to be validated against
     * @param message
     *        the message to be validated
     * 
     * @throws Exception
     *         exception while validation
     */
    void validate(Object schemas, InputSource message) throws Exception;

}
