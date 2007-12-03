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
package org.eclipse.swordfish.papi.internal.untyped;

/**
 * Describes the relation from a call to a previous message. For more information see
 * {@link org.eclipse.swordfish.papi.untyped.InternalCallContext#getRelations()}
 * 
 * 
 */
public interface InternalCallRelation {

    /**
     * Known type indicating a relation to a triggeringCall.
     */
    String TYPE_TRIGGERING_CALL = "TriggeringCall";

    /**
     * Known type indicating a relation to a oneway call.
     */
    String TYPE_ONEWAY = "Oneway";

    /**
     * Known type indicating a relation to a request.
     */
    String TYPE_REQUEST = "Request";

    /**
     * This method provides the messageId of the related message.
     * 
     * @return the messageId of the related message.
     */
    String getMessageID();

    /**
     * This method provides the type of a relation.
     * 
     * @return a String describing the type of relation.
     */
    String getType();

}
