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
package org.eclipse.swordfish.core.management.operations.impl;

import org.eclipse.swordfish.core.management.operations.OperationalMessage;
import org.eclipse.swordfish.core.management.operations.Severity;
import org.eclipse.swordfish.papi.internal.extension.operations.InternalOperationalMessage;

/**
 * Since org.eclipse.swordfish.papi.adapter.extensions.operations is not always available, we need
 * this wrapper to map <code>InternalOperationalMessage</code>s from there to
 * org.eclipse.swordfish.core.management.operations.
 * 
 */
public class OperationalMessageWrapper implements OperationalMessage {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: OperationalMessageWrapper.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    // ----------------------------------------------------- Instance Variables

    /** The original <code>InternalOperationalMessage</code>. */
    private InternalOperationalMessage wrapped;

    // ----------------------------------------------------------- Constructors

    /**
     * The Constructor.
     * 
     * @param source
     *        <code>InternalOperationalMessage</code> to be integrated
     */
    public OperationalMessageWrapper(final InternalOperationalMessage source) {
        this.wrapped = source;
    }

    // ------------------------------------------------------------- Properties

    /**
     * (non-Javadoc).
     * 
     * @return the category
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getCategory()
     */
    public String getCategory() {
        return this.wrapped.getCategory();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the msg ID
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getMsgID()
     */
    public int getMsgID() {
        return this.wrapped.getMsgID();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the param count
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getParamCount()
     */
    public int getParamCount() {
        return this.wrapped.getParamCount();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getQualifiedName()
     */
    public String getQualifiedName() {
        return this.wrapped.getClass().getName() + "." + this.wrapped.getMsgID();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the raw message
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getRawMessage()
     */
    public String getRawMessage() {
        return this.wrapped.getRawMessage();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the severity
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getSeverity()
     */
    public Severity getSeverity() {
        int code = this.wrapped.getSeverity().getValue();
        Severity ret = Severity.getByValue(code);
        return ret;
    }
}
