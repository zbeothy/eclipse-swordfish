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
public class ServiceLifecycleMessage extends AbstractOperationalMessage {

    /** Registration/Deregistration of participants. */
    public static final ServiceLifecycleMessage PARTICIPANT_REGISTRATION = new ServiceLifecycleMessage(1);

    /** Registration/Deregistration of service providers. */
    public static final ServiceLifecycleMessage SERVICE_PROVIDER_REGISTRATION = new ServiceLifecycleMessage(2);

    /** Registration/Deregistration of service providers. */
    public static final ServiceLifecycleMessage SERVICE_CONSUMER_REGISTRATION = new ServiceLifecycleMessage(3);

    /** Registration/Deregistration of operation providers. */
    public static final ServiceLifecycleMessage OPERATION_PROVIDER_REGISTRATION = new ServiceLifecycleMessage(4);

    /** Registration/Deregistration of operation providers. */
    public static final ServiceLifecycleMessage OPERATION_CONSUMER_REGISTRATION = new ServiceLifecycleMessage(5);

    /**
     * The Constructor.
     * 
     * @param val
     *        the val
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int)
     */
    protected ServiceLifecycleMessage(final int val) {
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
    protected ServiceLifecycleMessage(final int arg0, final ResourceBundle arg1) {
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
    protected ServiceLifecycleMessage(final int arg0, final String arg1) {
        super(arg0, arg1);
    }

}
