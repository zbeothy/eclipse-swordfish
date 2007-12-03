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

import java.util.ResourceBundle;

/**
 * The Class InternalOperationalMessage.
 */
public class InternalOperationalMessage extends AbstractOperationalMessage {

    /** The Constant PASS_BY_PACKAGE. */
    public static final InternalOperationalMessage PASS_BY_PACKAGE = new InternalOperationalMessage(1);

    /** The Constant PASS_BY_CLASS. */
    public static final InternalOperationalMessage PASS_BY_CLASS = new InternalOperationalMessage(2);

    /** The Constant PASS_BY_MESSAGE. */
    public static final InternalOperationalMessage PASS_BY_MESSAGE = new InternalOperationalMessage(3);

    /** The Constant BLOCK_BY_MESSAGE. */
    public static final InternalOperationalMessage BLOCK_BY_MESSAGE = new InternalOperationalMessage(4);

    /**
     * Instantiates a new internal operational message.
     * 
     * @param val
     *        the val
     */
    protected InternalOperationalMessage(final int val) {
        super(val);
    }

    /**
     * Instantiates a new internal operational message.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     */
    protected InternalOperationalMessage(final int arg0, final ResourceBundle arg1) {
        super(arg0, arg1);
    }

    /**
     * Instantiates a new internal operational message.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     */
    protected InternalOperationalMessage(final int arg0, final String arg1) {
        super(arg0, arg1);
    }

}
