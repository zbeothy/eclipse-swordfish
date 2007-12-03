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
 * Exception indicating that a component has been passed as a parameter to a method that was not
 * registered beforehand.
 * 
 */
public class InternalUnknownComponentException extends InternalInstrumentationException {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID =
            "@(#) $Id: InternalUnknownComponentException.java,v 1.1.2.3 2007/11/09 17:47:16 kkiehne Exp $";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5689898978617516474L;

    // ----------------------------------------------------- Instance Variables

    /** The unknown component. */
    private Object component;

    // ----------------------------------------------------------- Constructors

    /**
     * The Constructor.
     * 
     * @param component
     *        an {@link Object} as the unknown component.
     * 
     * @param msg
     *        a {@link String} as a textual message
     */
    public InternalUnknownComponentException(final String msg, final Object component) {
        super(msg);
        this.component = component;
    }

    // ------------------------------------------------------------- Properties

    /**
     * Gets the component.
     * 
     * @return the component
     */
    public Object getComponent() {
        return this.component;
    }

}
