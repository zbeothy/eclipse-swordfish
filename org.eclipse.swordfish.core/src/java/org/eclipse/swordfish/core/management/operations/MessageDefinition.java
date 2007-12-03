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
package org.eclipse.swordfish.core.management.operations;

/**
 * The Interface MessageDefinition.
 */
public interface MessageDefinition {

    /**
     * Gets the category.
     * 
     * @return the category
     */
    String getCategory();

    /**
     * Gets the msg ID.
     * 
     * @return the msg ID
     */
    int getMsgID();

    /**
     * Gets the parameter definitions.
     * 
     * @return the parameter definitions
     */
    ParameterDefinition[] getParameterDefinitions();

    /**
     * Gets the raw message.
     * 
     * @return the raw message
     */
    String getRawMessage();

    /**
     * Gets the severity.
     * 
     * @return the severity
     */
    Severity getSeverity();

}
