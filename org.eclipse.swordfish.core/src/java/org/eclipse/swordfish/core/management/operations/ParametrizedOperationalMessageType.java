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

/**
 * The Class ParametrizedOperationalMessageType.
 */
public class ParametrizedOperationalMessageType implements OperationalMessage {

    /** The category. */
    private String category;

    /** The msg ID. */
    private int msgID;

    /** The param count. */
    private int paramCount;

    /** The raw message. */
    private String rawMessage;

    /** The severity. */
    private Severity severity;

    /** The qualified name. */
    private String qualifiedName;

    /**
     * Instantiates a new parametrized operational message type.
     * 
     * @param params
     *        the params
     */
    public ParametrizedOperationalMessageType(final String[] params) {
        this.category = params[0];
        this.msgID = new Integer(params[1]).intValue();
        this.paramCount = new Integer(params[2]).intValue();
        this.rawMessage = params[3];
        this.severity = Severity.getByName(params[4]);
        this.qualifiedName = params[5];
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getCategory()
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getMsgID()
     */
    public int getMsgID() {
        return this.msgID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getParamCount()
     */
    public int getParamCount() {
        return this.paramCount;
    }

    /**
     * default implementation intended to be overwritten by implementing sub-classes.
     * 
     * @return the qualified name
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getQualifiedName()
     */
    public String getQualifiedName() {
        return this.qualifiedName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getRawMessage()
     */
    public String getRawMessage() {
        return this.rawMessage;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getSeverity()
     */
    public Severity getSeverity() {
        return this.severity;
    }

}
