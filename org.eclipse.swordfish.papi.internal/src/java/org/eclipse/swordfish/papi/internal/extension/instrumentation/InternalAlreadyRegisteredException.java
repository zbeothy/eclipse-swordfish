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
package org.eclipse.swordfish.papi.internal.extension.instrumentation;

/**
 * Exception indicating that a component is already registered with the management system.
 * 
 */
public class InternalAlreadyRegisteredException extends InternalInstrumentationException {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID =
            "@(#) $Id: InternalAlreadyRegisteredException.java,v 1.1.2.3 2007/11/09 17:47:16 kkiehne Exp $";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4484943751709482068L;

    // ----------------------------------------------------- Instance Variables

    /** The component that is already registered. */
    private Object component;

    // ----------------------------------------------------------- Constructors

    /**
     * <p>
     * The Constructor.
     * </p>
     * 
     * @param component
     *        an {@link Object} as component that is already registered.
     * @param msg
     *        a {@link String} as a textual message
     */
    public InternalAlreadyRegisteredException(final String msg, final Object component) {
        super(msg);
        this.component = component;
    }

    // ------------------------------------------------------------- Properties

    /**
     * Gets the component.
     * 
     * @return an {@link Object} as already registered component.
     */
    public Object getComponent() {
        return this.component;
    }
}
