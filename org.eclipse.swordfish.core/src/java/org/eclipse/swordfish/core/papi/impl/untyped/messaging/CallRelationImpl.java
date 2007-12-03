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
package org.eclipse.swordfish.core.papi.impl.untyped.messaging;

import java.io.IOException;
import java.io.Serializable;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallRelation;

/**
 * The implementation of the call Relation interface. TODO test if Call Relations are persited
 * correctly test if Call Relations are set into the MEP correctly test if Call Relations are
 * serialized into the SOAP header correctly test if Call Relations are restored from the MEP
 * correctly
 */
public class CallRelationImpl implements InternalCallRelation, Serializable {

    /** this serialzeable classes version UID. */
    private static final long serialVersionUID = 2469053778455459105L;

    /** the type of the call relation held by this DAO. */
    private String type;

    /** the messageID of the call relation held by this DAO. */
    private String messageId;

    /**
     * Instantiates a new call relation impl.
     * 
     * @param type
     *        the type
     * @param messageId
     *        the message id
     */
    public CallRelationImpl(final String type, final String messageId) {
        this.messageId = messageId;
        this.type = type;
    }

    /**
     * Gets the message ID.
     * 
     * @return the message ID
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalCallRelation#getMessageID()
     */
    public String getMessageID() {
        return this.messageId;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalCallRelation#getType()
     */
    public String getType() {
        return this.type;
    }

    /**
     * To string.
     * 
     * @return the string
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(Type: " + this.getType() + "," + "messageID: " + this.getMessageID() + ")";
    }

    /**
     * deserializes the content of the inputstream into an instance of this class.
     * 
     * @param stream
     *        the stream to read from
     * 
     * @throws IOException
     *         if an exception occures during reading from the stream
     * @throws ClassNotFoundException
     *         if the class indicated on the stream is not found.
     */
    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        this.type = (String) stream.readObject();
        this.messageId = (String) stream.readObject();
    }

    /**
     * serializes the content of this InternalCallRelation.
     * 
     * @param stream
     *        the stream to serialize to
     * 
     * @throws IOException
     *         if an exception occures on the outputstream
     */
    private void writeObject(final java.io.ObjectOutputStream stream) throws IOException {
        stream.writeObject(this.getType());
        stream.writeObject(this.getMessageID());
    }

    // TODO we need this object to be able to represent itself as a DOM element
}
