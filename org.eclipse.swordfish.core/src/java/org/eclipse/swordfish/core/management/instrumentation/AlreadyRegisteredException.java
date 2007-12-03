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
package org.eclipse.swordfish.core.management.instrumentation;

/**
 * Exception indicating that a component is already registered with the management system.
 * 
 */
public class AlreadyRegisteredException extends InstrumentationException {

    /**
     * 
     */
    private static final long serialVersionUID = -7173739708872718219L;

    /** The component that is already registered. */
    private Object component;

    /**
     * Instantiates a new already registered exception.
     * 
     * @param msg
     *        the msg
     * @param component
     *        the component
     */
    public AlreadyRegisteredException(final String msg, final Object component) {
        super(msg);
        this.component = component;
    }

    /**
     * Gets the component.
     * 
     * @return the component
     */
    public Object getComponent() {
        return this.component;
    }

}
