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
 * The Class ProcessingUnitImpl.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class ProcessingUnitImpl implements ProcessingUnit {

    /** the name of the assertion. */
    private String assertion;

    /** the name of the component to look up to handle the assertion. */
    private String component;

    /**
     * constructor.
     * 
     * @param asser
     *        the assertion name
     * @param comp
     *        the component name
     */
    public ProcessingUnitImpl(final String asser, final String comp) {
        this.assertion = asser;
        this.component = comp;
    }

    /**
     * Gets the assertion name.
     * 
     * @return the assertion name
     * 
     * @see org.eclipse.swordfish.processing.ProcessingUnit#getAssertionName()
     */
    public String getAssertionName() {
        return this.assertion;
    }

    /**
     * Gets the component name.
     * 
     * @return the component name
     * 
     * @see org.eclipse.swordfish.processing.ProcessingUnit#getComponentName()
     */
    public String getComponentName() {
        return this.component;
    }

    /**
     * make the debugger happy :-).
     * 
     * @return the String representation of this object
     */
    @Override
    public String toString() {
        return "{" + this.getAssertionName() + " , " + this.getComponentName() + "}";
    }
}
