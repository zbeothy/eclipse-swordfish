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

import org.eclipse.swordfish.papi.internal.extension.operations.InternalAbstractOperationalMessage;

/**
 * The Class ExternalOperationalMessage.
 */
public class ExternalOperationalMessage extends InternalAbstractOperationalMessage {

    /** The Constant NO_PASS. */
    public static final ExternalOperationalMessage NO_PASS = new ExternalOperationalMessage(1);

    /** The Constant PASS. */
    public static final ExternalOperationalMessage PASS = new ExternalOperationalMessage(2);

    /**
     * Instantiates a new external operational message.
     * 
     * @param val
     *        the val
     */
    private ExternalOperationalMessage(final int val) {
        super(val);
    }

    /**
     * Gets the parameters.
     * 
     * @return the parameters
     */
    public Object[] getParameters() {
        return null;
    }

}
