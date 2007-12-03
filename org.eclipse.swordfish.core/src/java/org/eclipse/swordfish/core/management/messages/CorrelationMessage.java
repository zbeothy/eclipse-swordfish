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
package org.eclipse.swordfish.core.management.messages;

import java.util.ResourceBundle;
import org.eclipse.swordfish.core.management.operations.AbstractOperationalMessage;
import org.eclipse.swordfish.papi.internal.extension.operations.InternalAbstractOperationalMessage;

/**
 * The Class CorrelationMessage.
 */
public class CorrelationMessage extends AbstractOperationalMessage {

    /** The Constant CORRELATION. */
    public static final CorrelationMessage CORRELATION = new CorrelationMessage(1);

    /** The Constant XPATH_EXCEPTION. */
    public static final CorrelationMessage XPATH_EXCEPTION = new CorrelationMessage(2);

    /**
     * The Constructor.
     * 
     * @param val
     *        the val
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int)
     */
    protected CorrelationMessage(final int val) {
        super(val);
    }

    /**
     * The Constructor.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int, ResourceBundle)
     */
    protected CorrelationMessage(final int arg0, final ResourceBundle arg1) {
        super(arg0, arg1);
    }

    /**
     * The Constructor.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int, String)
     */
    protected CorrelationMessage(final int arg0, final String arg1) {
        super(arg0, arg1);
    }

}
