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
 * Operational log messages concerning operations of MSS.
 * 
 */
public class ManagementMessage extends AbstractOperationalMessage {

    /** Error message for failed connection to server. */
    public static final ManagementMessage SERVER_CONNECTION_FAILED = new ManagementMessage(1);

    /**
     * Information that component is operational again Note that previous change to "FAILED" should
     * be indicated by detailed error message.
     */
    public static final ManagementMessage COMPONENT_STATE_CHANGED = new ManagementMessage(2);

    /** Information that shutdown of a component failed. */
    public static final ManagementMessage SERVER_SHUTDOWN_FAILED = new ManagementMessage(3);

    /** Could not publish operational log messages to any backend. */
    public static final ManagementMessage PUBLICATION_FAILURE = new ManagementMessage(5);

    /**
     * The Constructor.
     * 
     * @param val
     *        the val
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int)
     */
    protected ManagementMessage(final int val) {
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
    protected ManagementMessage(final int arg0, final ResourceBundle arg1) {
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
    protected ManagementMessage(final int arg0, final String arg1) {
        super(arg0, arg1);
    }

}
