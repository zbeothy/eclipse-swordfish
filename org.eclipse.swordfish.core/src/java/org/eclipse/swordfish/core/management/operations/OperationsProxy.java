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

import org.eclipse.swordfish.core.management.operations.impl.OperationalMessageWrapper;
import org.eclipse.swordfish.core.management.operations.impl.OperationsBean;
import org.eclipse.swordfish.papi.internal.extension.operations.InternalOperationalMessage;
import org.eclipse.swordfish.papi.internal.extension.operations.InternalOperations;

/**
 * Wrapper for org.eclipse.swordfish.core.management.operations.Operations that implements
 * org.eclipse.swordfish.papi.adapter.extensions.operations.Operations. This allows
 * org.eclipse.swordfish.papi.extensions.operations to use the implemenetation in
 * org.eclipse.swordfish.core.management.operations.
 * 
 */
public class OperationsProxy implements InternalOperations {

    /** The actual <code>InternalOperations</code> implementation to be used. */
    private OperationsBean wrapped;

    /**
     * Destroy.
     */
    public void destroy() {
        this.wrapped = null;
    }

    /**
     * (non-Javadoc).
     * 
     * @param arg0
     *        the arg0
     * 
     * @see org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperations#notify(org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperationalMessage)
     */
    public void notify(final InternalOperationalMessage arg0) {
        OperationalMessageWrapper msgWrapper = new OperationalMessageWrapper(arg0);
        this.wrapped.notify(msgWrapper);
    }

    /**
     * (non-Javadoc).
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @see org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperations#notify(org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperationalMessage,
     *      java.lang.Object)
     */
    public void notify(final InternalOperationalMessage arg0, final Object arg1) {
        OperationalMessageWrapper msgWrapper = new OperationalMessageWrapper(arg0);
        this.wrapped.notify(msgWrapper, arg1);
    }

    /**
     * (non-Javadoc).
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * @param arg2
     *        the arg2
     * 
     * @see org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperations#notify(org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperationalMessage,
     *      java.lang.Object, java.lang.Object)
     */
    public void notify(final InternalOperationalMessage arg0, final Object arg1, final Object arg2) {
        OperationalMessageWrapper msgWrapper = new OperationalMessageWrapper(arg0);
        this.wrapped.notify(msgWrapper, arg1, arg2);
    }

    /**
     * (non-Javadoc).
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * @param arg2
     *        the arg2
     * @param arg3
     *        the arg3
     * 
     * @see org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperations#notify(org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperationalMessage,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public void notify(final InternalOperationalMessage arg0, final Object arg1, final Object arg2, final Object arg3) {
        OperationalMessageWrapper msgWrapper = new OperationalMessageWrapper(arg0);
        this.wrapped.notify(msgWrapper, arg1, arg2, arg3);
    }

    /**
     * (non-Javadoc).
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @see org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperations#notify(org.eclipse.swordfish.papi.adapter.extensions.operations.InternalOperationalMessage,
     *      java.lang.Object[])
     */
    public void notify(final InternalOperationalMessage arg0, final Object[] arg1) {
        OperationalMessageWrapper msgWrapper = new OperationalMessageWrapper(arg0);
        this.wrapped.notify(msgWrapper, arg1);
    }

    /**
     * Setter called by container.
     * 
     * @param val
     *        <code>InternalOperations</code> implementation to be used
     */
    public void setWrapped(final OperationsBean val) {
        this.wrapped = val;
    }

}
