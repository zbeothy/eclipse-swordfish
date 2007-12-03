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
package org.eclipse.swordfish.core.management.adapter;

import java.util.ResourceBundle;
import org.eclipse.swordfish.core.management.operations.AbstractOperationalMessage;
import org.eclipse.swordfish.papi.internal.extension.operations.InternalAbstractOperationalMessage;

/**
 * The Class AdapterMessage.
 */
public class AdapterMessage extends AbstractOperationalMessage {

    /** The Constant EM_CONNECTION. */
    public static final AdapterMessage EM_CONNECTION = new AdapterMessage(1);

    /** The Constant EM_AUTH_FAIL. */
    public static final AdapterMessage EM_AUTH_FAIL = new AdapterMessage(2);

    /**
     * The Constructor.
     * 
     * @param val
     *        the val
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int)
     */
    protected AdapterMessage(final int val) {
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
    protected AdapterMessage(final int arg0, final ResourceBundle arg1) {
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
    protected AdapterMessage(final int arg0, final String arg1) {
        super(arg0, arg1);
    }

}
