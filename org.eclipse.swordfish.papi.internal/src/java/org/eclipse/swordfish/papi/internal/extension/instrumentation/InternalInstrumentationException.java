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

import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * common base class for exceptions used in org.eclipse.swordfish.papi.extension.instrumentation.
 * 
 */
public abstract class InternalInstrumentationException extends InternalSBBException {

    // -------------------------------------------------------------- Constants

    /**
     * (R)evision (C)ontrol (S)ystem (Id)entifier.
     */
    public static final String RCS_ID =
            "@(#) $Id: InternalInstrumentationException.java,v 1.1.2.3 2007/11/09 17:47:16 kkiehne Exp $";

    // ----------------------------------------------------------- Constructors

    /**
     * <p>
     * The Constructor.
     * </p>
     * 
     * @param message
     *        a {@link String} as a textual message
     */
    public InternalInstrumentationException(final String message) {
        super(message);
    }

}
