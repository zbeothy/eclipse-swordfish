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
package org.eclipse.swordfish.core.components.processing.impl;

/**
 * Holder Object.
 */
public interface ProcessingUnit {

    /**
     * Gets the assertion name.
     * 
     * @return the name of the assertion for this processing unit
     */
    String getAssertionName();

    /**
     * Gets the component name.
     * 
     * @return the name of the component doing the processing
     */

    String getComponentName();

}
