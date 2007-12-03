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

import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * common base class for exceptions used in org.eclipse.swordfish.papi.extension.instrumentation.
 * 
 */
public abstract class InstrumentationException extends InternalInfrastructureException {

    /**
     * Instantiates a new instrumentation exception.
     * 
     * @param msg
     *        the msg
     */
    public InstrumentationException(final String msg) {
        super(msg);
    }

}
