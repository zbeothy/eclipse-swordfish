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
 * The Interface ParameterDefinition.
 */
public interface ParameterDefinition {

    /**
     * Gets the description.
     * 
     * @return the description
     */
    String getDescription();

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Gets the type name.
     * 
     * @return the type name
     */
    String getTypeName();

    /**
     * Checks if is nullable.
     * 
     * @return true, if is nullable
     */
    boolean isNullable();

}
